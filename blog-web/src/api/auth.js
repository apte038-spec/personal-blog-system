import request from '../utils/request'

export const authApi = {
  register: (data) => request.post('/auth/register', data),
  login: (data) => request.post('/auth/login', data),
  getCurrentUser: () => request.get('/auth/me'),
  sendPasswordResetCode: (email) => request.post('/auth/password/reset-code', { email }),
  resetPassword: (data) => request.post('/auth/password/reset', data),
}
