import { defineStore } from 'pinia'
import { authApi } from '../api/auth'
import { clearAuthStorage, getStoredUser, getToken, setStoredUser, setToken } from '../utils/token'
import { profileApi } from '../api/profile'

export const useAuthStore = defineStore('auth', {
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
    async fetchCurrentUser() {
      const user = await authApi.getCurrentUser()
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
    async restoreSession() {
      if (this.initialized) return
      this.token = getToken()
      this.user = getStoredUser()
      try {
        if (this.token && !this.user) await this.fetchCurrentUser()
      } catch (error) {
        this.logout()
        throw error
      } finally {
        this.initialized = true
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
