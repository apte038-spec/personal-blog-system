<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../store/auth'
const form = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', email: '', password: '', confirmPassword: '' })
const mode = ref('login'), loading = ref(false), router = useRouter(), route = useRoute(), auth = useAuthStore()
async function login() {
  loading.value = true
  try {
    await auth.login(form)
    const redirect = typeof route.query.redirect === 'string' && route.query.redirect.startsWith('/') ? route.query.redirect : null
    router.push(redirect || (auth.isAdmin ? '/admin' : '/'))
  } catch (e) { ElMessage.error(e.message) } finally { loading.value = false }
}
async function register() {
  loading.value = true
  try {
    await auth.register(registerForm)
    ElMessage.success('注册成功，请使用新账号登录')
    form.username = registerForm.username
    form.password = ''
    mode.value = 'login'
  } catch (e) { ElMessage.error(e.message) } finally { loading.value = false }
}
</script>
<template><div class="login"><el-card><template #header><h2>个人博客账号</h2></template><el-tabs v-model="mode" stretch><el-tab-pane label="登录" name="login"><el-form :model="form" @submit.prevent="login"><el-form-item><el-input v-model="form.username" autocomplete="username" placeholder="用户名" /></el-form-item><el-form-item><el-input v-model="form.password" type="password" autocomplete="current-password" show-password placeholder="密码" /></el-form-item><div class="login-helper"><router-link to="/forgot-password">忘记密码？</router-link></div><el-button type="primary" native-type="submit" :loading="loading" style="width:100%">登录</el-button></el-form></el-tab-pane><el-tab-pane label="注册" name="register"><el-form :model="registerForm" @submit.prevent="register"><el-form-item><el-input v-model="registerForm.username" placeholder="用户名（支持中文、字母和数字）"/></el-form-item><el-form-item><el-input v-model="registerForm.email" type="email" autocomplete="email" placeholder="邮箱"/></el-form-item><el-form-item><el-input v-model="registerForm.password" type="password" show-password placeholder="密码（8-64位）"/></el-form-item><el-form-item><el-input v-model="registerForm.confirmPassword" type="password" show-password placeholder="确认密码"/></el-form-item><el-button type="primary" native-type="submit" :loading="loading" style="width:100%">注册</el-button></el-form></el-tab-pane></el-tabs></el-card></div></template>
