import { createRouter, createWebHistory } from 'vue-router'
import pinia from '../store'
import { useAuthStore } from '../store/auth'

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior: () => ({ top: 0, behavior: 'smooth' }),
  routes: [
    {
      path: '/', component: () => import('../layouts/PublicLayout.vue'),
      children: [
        { path: '', name: 'home', component: () => import('../views/public/Home.vue'), meta: { title: '首页' } },
        { path: 'articles', name: 'articles', component: () => import('../views/public/ArticleList.vue'), meta: { title: '文章' } },
        { path: 'articles/:id', name: 'article-detail', component: () => import('../views/public/ArticleDetail.vue'), meta: { title: '文章详情' } },
        { path: 'about', name: 'about', component: () => import('../views/public/About.vue'), meta: { title: '关于我' } },
        { path: 'message', name: 'message', component: () => import('../views/public/Message.vue'), meta: { title: '留言板' } },
        { path: 'profile', name: 'profile', component: () => import('../views/public/Profile.vue'), meta: { title: '个人主页', requiresAuth: true } },
        { path: ':pathMatch(.*)*', name: 'not-found', component: () => import('../views/NotFound.vue'), meta: { title: '页面不存在' } },
      ],
    },
    { path: '/login', name: 'login', component: () => import('../views/admin/Login.vue'), meta: { title: '用户登录', guestOnly: true } },
    { path: '/forgot-password', name: 'forgot-password', component: () => import('../views/public/ForgotPassword.vue'), meta: { title: '找回密码', guestOnly: true } },
    { path: '/admin', component: () => import('../layouts/AdminLayout.vue'), meta: { requiresAuth: true, roles: ['ADMIN'] }, children: [
      { path: '', name: 'admin', component: () => import('../views/admin/AdminDashboard.vue'), meta: { title: '数据概览', requiresAuth: true, roles: ['ADMIN'] } },
      { path: 'articles', name: 'admin-articles', component: () => import('../views/admin/ArticleManage.vue'), meta: { title: '文章管理', requiresAuth: true, roles: ['ADMIN'] } },
      { path: 'users', name: 'admin-users', component: () => import('../views/admin/UserManage.vue'), meta: { title: '用户管理', requiresAuth: true, roles: ['ADMIN'] } },
      { path: 'comments', name: 'admin-comments', component: () => import('../views/admin/CommentManage.vue'), meta: { title: '评论管理', requiresAuth: true, roles: ['ADMIN'] } },
      { path: 'messages', name: 'admin-messages', component: () => import('../views/admin/MessageManage.vue'), meta: { title: '留言管理', requiresAuth: true, roles: ['ADMIN'] } },
      { path: 'settings', name: 'admin-settings', component: () => import('../views/admin/SystemManage.vue'), meta: { title: '系统管理', requiresAuth: true, roles: ['ADMIN'] } },
    ] },
  ],
})

router.beforeEach(async (to) => {
  const auth = useAuthStore(pinia)
  try { await auth.restoreSession() } catch { if (to.meta.requiresAuth) return { name: 'login', query: { redirect: to.fullPath } } }
  if (to.meta.requiresAuth && !auth.isAuthenticated) return { name: 'login', query: { redirect: to.fullPath } }
  if (to.meta.roles?.length && !to.meta.roles.includes(auth.user?.role)) return { name: 'home' }
  if (to.meta.guestOnly && auth.isAuthenticated) return { name: auth.isAdmin ? 'admin' : 'home' }
  return true
})

router.afterEach((to) => { document.title = `${to.meta.title || '个人博客'} · Personal Blog` })

export default router
