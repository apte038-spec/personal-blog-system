import request from '../utils/request'

export const authApi = {
  register: (data) => request.post('/auth/register', data),
  login: (data) => request.post('/auth/login', data),
  getCurrentUser: (config = {}) => request.get('/auth/me', config),
  sendPasswordResetCode: (email) => request.post('/auth/password/reset-code', { email }),
  resetPassword: (data) => request.post('/auth/password/reset', data),
}
