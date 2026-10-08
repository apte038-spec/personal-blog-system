<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { publicApi } from '../api/blog'
import { useAuthStore } from '../store/auth'

const config = ref({ siteName: '行迹', siteSubtitle: '记录代码、思考与生活' })
const menuOpen = ref(false)
const auth = useAuthStore()
const avatarBroken = ref(false)
const avatarLetter = computed(() => (auth.user?.username || '用').trim().slice(0, 1).toUpperCase())
watch(() => auth.user?.avatar, () => { avatarBroken.value = false })
onMounted(async () => { try { config.value = { ...config.value, ...(await publicApi.config()) } } catch { /* 使用默认站点资料 */ } })
</script>

<template>
  <div class="site-shell">
    <header class="site-header">
      <router-link to="/" class="site-brand"><span class="brand-mark">L</span><span>{{ config.siteName }}</span></router-link>
      <button class="menu-button" aria-label="打开菜单" @click="menuOpen = !menuOpen">☰</button>
      <nav :class="{ open: menuOpen }" @click="menuOpen = false">
        <router-link to="/">首页</router-link><router-link to="/articles">文章</router-link>
        <router-link to="/message">留言</router-link><router-link to="/about">关于</router-link>
        <router-link v-if="!auth.isAuthenticated" to="/login" class="account-link">登录 / 注册</router-link>
        <router-link v-if="auth.isAuthenticated && auth.isAdmin" to="/admin" class="account-link">管理后台</router-link>
        <router-link v-if="auth.isAuthenticated" to="/profile" class="header-avatar" :title="`${auth.user?.username || '用户'}的个人主页`" aria-label="打开个人主页">
          <img v-if="auth.user?.avatar && !avatarBroken" :src="auth.user.avatar" alt="用户头像" @error="avatarBroken = true">
          <span v-else>{{ avatarLetter }}</span>
        </router-link>
      </nav>
    </header>
    <main class="site-main"><router-view /></main>
    <footer class="site-footer"><strong>{{ config.siteName }}</strong><span>{{ config.siteSubtitle }}</span><small>© {{ new Date().getFullYear() }} Built with Vue 3</small></footer>
  </div>
</template>
