import request from '../utils/request'

export const publicApi = {
  articles: (params) => request.get('/public/articles', { params }),
  article: (id) => request.get(`/public/articles/${id}`),
  featured: () => request.get('/public/articles/featured'),
  hot: (limit = 5) => request.get('/public/articles/hot', { params: { limit } }),
  latest: (limit = 5) => request.get('/public/articles/latest', { params: { limit } }),
  adjacent: (id) => request.get(`/public/articles/adjacent/${id}`),
  categories: () => request.get('/public/categories'),
  tags: () => request.get('/public/tags'),
  config: () => request.get('/public/site-config'),
}

export const interactionApi = {
  comments: (articleId) => request.get(`/public/articles/${articleId}/comments`),
  comment: (articleId, content) => request.post(`/articles/${articleId}/comments`, { content }),
  likeState: (articleId) => request.get(`/articles/${articleId}/like`),
  like: (articleId) => request.post(`/articles/${articleId}/like`),
  unlike: (articleId) => request.delete(`/articles/${articleId}/like`),
  messages: (params) => request.get('/public/messages', { params }),
  leaveMessage: (content) => request.post('/messages', { content }),
}

export const adminApi = {
  overview: () => request.get('/admin/dashboard/overview'), trend: () => request.get('/admin/dashboard/publish-trend'),
  articles: (params) => request.get('/admin/articles', { params }),
  article: (id) => request.get(`/admin/articles/${id}`),
  saveArticle: (id, data) => id ? request.put(`/admin/articles/${id}`, data) : request.post('/admin/articles', data),
  deleteArticle: (id) => request.delete(`/admin/articles/${id}`), categories: () => request.get('/admin/categories'),
  tags: () => request.get('/admin/tags'),
  saveCategory: (id, data) => id ? request.put(`/admin/categories/${id}`, data) : request.post('/admin/categories', data),
  deleteCategory: (id) => request.delete(`/admin/categories/${id}`), configs: () => request.get('/admin/site-config'),
  saveConfigs: (data) => request.put('/admin/site-config', data),
  users: (params) => request.get('/admin/users', { params }),
  userStats: () => request.get('/admin/users/stats'),
  updateUserRole: (id, role) => request.patch(`/admin/users/${id}/role`, { role }),
  updateUserStatus: (id, status) => request.patch(`/admin/users/${id}/status`, { status }),
  commentStats: () => request.get('/admin/comments/stats'),
  comments: (params) => request.get('/admin/comments', { params }),
  reviewComments: (ids, status) => request.patch('/admin/comments/status', { ids, status }),
  saveTag: (id, data) => id ? request.put(`/admin/tags/${id}`, data) : request.post('/admin/tags', data),
  deleteTag: (id) => request.delete(`/admin/tags/${id}`),
  messageStats: () => request.get('/admin/messages/stats'),
  messages: (params) => request.get('/admin/messages', { params }),
  reviewMessages: (ids, status) => request.patch('/admin/messages/status', { ids, status }),
}
