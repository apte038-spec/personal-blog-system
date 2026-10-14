import { defineStore } from 'pinia'
import { authApi } from '../api/auth'
import { clearAuthStorage, getStoredUser, getToken, setStoredUser, setToken } from '../utils/token'
import { profileApi } from '../api/profile'
import { setAuthFailureHandlers } from '../utils/request'

const useStore = defineStore('auth', {
  state: () => ({ token: getToken(), user: getStoredUser(), initialized: false }),
  getters: {
    isAuthenticated: (state) => Boolean(state.token),
    isAdmin: (state) => state.user?.role === 'ADMIN',
  },
  actions: {
    async register(payload) {
      return authApi.register(payload)
    },
    async login(payload) {
      const result = await authApi.login(payload)
      if (!result?.accessToken) throw new Error('登录响应中缺少 accessToken')
      this.token = result.accessToken
      this.user = result.user || null
      this.initialized = true
      setToken(this.token)
      setStoredUser(this.user)
      return result
    },
    async fetchCurrentUser(config = {}) {
      const token = this.token
      const user = await authApi.getCurrentUser(config)
      if (this.token !== token) throw new Error('登录状态已变更，请重试')
      this.user = user
      setStoredUser(user)
      return user
    },
    async updateProfile(payload) {
      const user = await profileApi.update(payload)
      this.user = user
      setStoredUser(user)
      return user
    },
    async uploadAvatar(file) {
      const result = await profileApi.uploadAvatar(file)
      this.user = { ...this.user, avatar: result.avatar }
      setStoredUser(this.user)
      return result
    },
    async restoreSession({ force = false } = {}) {
      if (this.initialized && !force) return
      this.token = getToken()
      this.user = getStoredUser()
      try {
        if (this.token && (force || !this.user)) {
          await this.fetchCurrentUser({ authFailure: 'silent', refreshPermissions: false })
        }
      } catch (error) {
        if (error.staleSession) return
        // Only an authentication failure clears identity (handled by request).
        // A network outage must not masquerade as an expired account.
        if (error.status === 403) { this.user = null; setStoredUser(null) }
        throw error
      } finally {
        this.initialized = true
      }
    },
    async refreshPermissions() {
      const token = this.token
      try {
        await this.fetchCurrentUser({ authFailure: 'silent', refreshPermissions: false })
      } catch {
        if (this.token !== token) return
        // Unknown role is fail-closed; do not keep a cached ADMIN menu.
        this.user = null
        setStoredUser(null)
      }
    },
    logout() {
      this.token = ''
      this.user = null
      this.initialized = true
      clearAuthStorage()
    },
  },
})

export function useAuthStore(...args) {
  const store = useStore(...args)
  setAuthFailureHandlers({ unauthorized: () => store.logout(), forbidden: () => store.refreshPermissions() })
  return store
}
