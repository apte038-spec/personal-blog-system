import request from '../utils/request'

export const profileApi = {
  update: (data) => request.put('/users/me', data),
  uploadAvatar: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/users/me/avatar', formData)
  },
  likedArticles: (params) => request.get('/users/me/likes', { params }),
  changePassword: (data) => request.put('/users/me/password', data),
}
