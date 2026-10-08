<script setup>
import { onBeforeUnmount, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Message, Lock, Key } from '@element-plus/icons-vue'
import { authApi } from '../../api/auth'

const router = useRouter()
const formRef = ref()
const sending = ref(false)
const resetting = ref(false)
const countdown = ref(0)
let timer = null
const form = reactive({ email: '', code: '', newPassword: '', confirmPassword: '' })

const validateConfirmPassword = (_rule, value, callback) => {
  if (!value) callback(new Error('请再次输入新密码'))
  else if (value !== form.newPassword) callback(new Error('两次输入的密码不一致'))
  else callback()
}

const rules = {
  email: [{ required: true, message: '请输入注册邮箱', trigger: 'blur' }, { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }, { pattern: /^\d{6}$/, message: '验证码必须为6位数字', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 8, max: 64, message: '密码须为8-64位', trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: ['blur', 'change'] }],
}

function startCountdown() {
  countdown.value = 60
  clearInterval(timer)
  timer = setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0) { clearInterval(timer); timer = null }
  }, 1000)
}

async function sendCode() {
  if (countdown.value > 0) return
  try { await formRef.value.validateField('email') } catch { return }
  sending.value = true
  try { await authApi.sendPasswordResetCode(form.email.trim()); ElMessage.success('验证码已发送，请检查邮箱'); startCountdown() }
  catch (error) { ElMessage.error(error.message) }
  finally { sending.value = false }
}

async function resetPassword() {
  try { await formRef.value.validate() } catch { return }
  resetting.value = true
  try {
    await authApi.resetPassword({ ...form, email: form.email.trim(), code: form.code.trim() })
    ElMessage.success('密码修改成功，请使用新密码登录')
    router.replace('/login')
  } catch (error) { ElMessage.error(error.message) }
  finally { resetting.value = false }
}

onBeforeUnmount(() => clearInterval(timer))
</script>

<template>
  <div class="login forgot-password-page"><el-card><template #header><div class="forgot-title"><span>RESET PASSWORD</span><h2>找回密码</h2><p>通过注册邮箱验证身份并设置新密码</p></div></template><el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="resetPassword"><el-form-item label="注册邮箱" prop="email"><el-input v-model="form.email" type="email" autocomplete="email" :prefix-icon="Message" placeholder="name@example.com" /></el-form-item><el-form-item label="邮箱验证码" prop="code"><div class="verification-row"><el-input v-model="form.code" maxlength="6" inputmode="numeric" :prefix-icon="Key" placeholder="6位数字验证码"/><el-button :disabled="countdown > 0" :loading="sending" @click="sendCode">{{ countdown > 0 ? `${countdown}秒后重发` : '发送验证码' }}</el-button></div></el-form-item><el-form-item label="新密码" prop="newPassword"><el-input v-model="form.newPassword" type="password" autocomplete="new-password" show-password :prefix-icon="Lock" placeholder="8-64位密码" /></el-form-item><el-form-item label="确认新密码" prop="confirmPassword"><el-input v-model="form.confirmPassword" type="password" autocomplete="new-password" show-password :prefix-icon="Lock" placeholder="再次输入新密码" /></el-form-item><el-button type="primary" native-type="submit" :loading="resetting" style="width:100%">确认修改</el-button><el-button class="back-login-button" @click="router.push('/login')">返回登录</el-button></el-form></el-card></div>
</template>
