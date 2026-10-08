const TOKEN_KEY = 'personal-blog-access-token'
const USER_KEY = 'personal-blog-current-user'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  if (token) localStorage.setItem(TOKEN_KEY, token)
  else localStorage.removeItem(TOKEN_KEY)
}

export function getStoredUser() {
  const value = localStorage.getItem(USER_KEY)
  if (!value) return null
  try {
    return JSON.parse(value)
  } catch {
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export function setStoredUser(user) {
  if (user) localStorage.setItem(USER_KEY, JSON.stringify(user))
  else localStorage.removeItem(USER_KEY)
}

export function clearAuthStorage() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}
