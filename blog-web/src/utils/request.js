import axios from 'axios'
import { clearAuthStorage, getToken } from './token'

// The store supplies callbacks; the transport must not import the store/API cycle.
let authFailureHandlers = {}
export function setAuthFailureHandlers(handlers) { authFailureHandlers = handlers }

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

request.interceptors.request.use((config) => {
  if (typeof FormData !== 'undefined' && config.data instanceof FormData) {
    if (typeof config.headers?.delete === 'function') config.headers.delete('Content-Type')
    else if (config.headers) delete config.headers['Content-Type']
  }
  const token = getToken()
  config.authToken = token
  const publicRequest = config.url?.startsWith('/public/') || config.url === '/auth/login' || config.url === '/auth/register' || config.url?.startsWith('/auth/password/')
  if (token && !publicRequest) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && typeof body.code !== 'undefined') {
      if (body.code === 0) return body.data
      const error = new Error(body.message || '请求失败')
      error.code = body.code
      return Promise.reject(error)
    }
    return body
  },
  async (error) => {
    const status = error.response?.status
    const message = error.response?.data?.message || (status ? `请求失败（${status}）` : '网络连接失败，请检查后端服务')
    const publicRequest = error.config?.url?.startsWith('/public/') || error.config?.url === '/auth/login' || error.config?.url === '/auth/register' || error.config?.url?.startsWith('/auth/password/')
    // A late failure from an old session must not invalidate a new login.
    const currentSession = error.config?.authToken === getToken()
    if (status === 401 && !publicRequest && currentSession) {
      clearAuthStorage()
      authFailureHandlers.unauthorized?.()
      if (error.config?.authFailure !== 'silent' && window.location.pathname !== '/login') {
        const redirect = `${window.location.pathname}${window.location.search}`
        window.location.assign(`/login?redirect=${encodeURIComponent(redirect)}`)
      }
    }
    if (status === 403 && !publicRequest && currentSession && error.config?.refreshPermissions !== false) {
      await authFailureHandlers.forbidden?.()
      if (window.location.pathname.startsWith('/admin')) window.location.assign('/')
    }
    const normalized = new Error(message)
    normalized.status = status
    normalized.code = error.response?.data?.code
    normalized.staleSession = !currentSession && (status === 401 || status === 403)
    return Promise.reject(normalized)
  },
)

export default request
