<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../store/auth'
import { profileApi } from '../../api/profile'
import ArticleCard from '../../components/ArticleCard.vue'

const auth = useAuthStore()
const router = useRouter()
const saving = ref(false)
const uploadingAvatar = ref(false)
const loading = ref(false)
const avatarBroken = ref(false)
const avatarInput = ref(null)
const selectedAvatar = ref(null)
const avatarPreviewUrl = ref('')
const likes = ref({ records: [], total: 0 })
const page = ref(1)
const form = reactive({ username: auth.user?.username || '' })
const passwordForm = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' })
const changingPassword = ref(false)

const firstLetter = computed(() => (auth.user?.username || '用').trim().slice(0, 1).toUpperCase())
const displayedAvatar = computed(() => avatarPreviewUrl.value || auth.user?.avatar || '')
const avatarVisible = computed(() => Boolean(displayedAvatar.value) && !avatarBroken.value)

watch(() => auth.user?.avatar, () => { avatarBroken.value = false })

function chooseAvatar() {
  avatarInput.value?.click()
}

function clearAvatarSelection() {
  if (avatarPreviewUrl.value) URL.revokeObjectURL(avatarPreviewUrl.value)
  avatarPreviewUrl.value = ''
  selectedAvatar.value = null
  avatarBroken.value = false
  if (avatarInput.value) avatarInput.value.value = ''
}

function onAvatarSelected(event) {
  const file = event.target.files?.[0]
  if (!file) return
  const validName = /\.(jpe?g|png|webp)$/i.test(file.name)
  const validType = ['image/jpeg', 'image/png', 'image/webp'].includes(file.type)
  if (!validName || !validType) {
    event.target.value = ''
    return ElMessage.warning('请选择 JPG、PNG 或 WebP 格式的图片')
  }
  if (file.size > 5 * 1024 * 1024) {
    event.target.value = ''
    return ElMessage.warning('头像图片不能超过 5MB')
  }
  clearAvatarSelection()
  selectedAvatar.value = file
  avatarPreviewUrl.value = URL.createObjectURL(file)
}

async function saveAvatar() {
  if (!selectedAvatar.value) return
  uploadingAvatar.value = true
  try {
    await auth.uploadAvatar(selectedAvatar.value)
    clearAvatarSelection()
    ElMessage.success('头像修改成功')
  } catch (error) { ElMessage.error(error.message) }
  finally { uploadingAvatar.value = false }
}

async function loadLikes() {
  loading.value = true
  try { likes.value = await profileApi.likedArticles({ page: page.value, size: 6 }) }
  catch (error) { ElMessage.error(error.message) }
  finally { loading.value = false }
}

async function saveProfile() {
  if (!form.username.trim()) return ElMessage.warning('请输入用户名')
  saving.value = true
  try {
    await auth.updateProfile({ username: form.username.trim() })
    form.username = auth.user.username
    ElMessage.success('个人资料已保存')
  } catch (error) { ElMessage.error(error.message) }
  finally { saving.value = false }
}

function logout() {
  auth.logout()
  router.replace('/')
}

async function changePassword() {
  if (!passwordForm.currentPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) return ElMessage.warning('请完整填写密码信息')
  changingPassword.value = true
  try {
    await profileApi.changePassword(passwordForm)
    ElMessage.success('密码已修改，请重新登录')
    auth.logout()
    router.replace('/login')
  } catch (error) { ElMessage.error(error.message) }
  finally { changingPassword.value = false }
}

onMounted(loadLikes)
onBeforeUnmount(clearAvatarSelection)
</script>

<template>
  <section class="profile-page">
    <div class="profile-hero">
      <div class="profile-avatar-editor">
        <div class="profile-avatar large">
          <img v-if="avatarVisible" :src="displayedAvatar" alt="用户头像" @error="avatarBroken = true">
          <span v-else>{{ firstLetter }}</span>
        </div>
        <input ref="avatarInput" class="avatar-picker-input" type="file" accept=".jpg,.jpeg,.png,.webp,image/jpeg,image/png,image/webp" @change="onAvatarSelected">
        <div class="avatar-action-row">
          <el-button v-if="!selectedAvatar" size="small" plain @click="chooseAvatar">更换头像</el-button>
          <template v-else>
            <el-button size="small" type="primary" :loading="uploadingAvatar" @click="saveAvatar">保存头像</el-button>
            <el-button size="small" :disabled="uploadingAvatar" @click="clearAvatarSelection">取消</el-button>
          </template>
        </div>
      </div>
      <div>
        <span class="eyebrow">PERSONAL SPACE</span>
        <h1>{{ auth.user?.username }}</h1>
        <p>{{ auth.user?.email }} · {{ auth.isAdmin ? '管理员' : '博客用户' }}</p>
      </div>
      <router-link v-if="auth.isAdmin" to="/admin" class="profile-admin-link">进入管理后台 →</router-link>
    </div>

    <div class="profile-grid">
      <article class="profile-panel">
        <div class="profile-panel-title"><div><span class="eyebrow">PROFILE</span><h2>个人资料</h2></div><p>头像可在上方单独更换</p></div>
        <el-form label-position="top" @submit.prevent="saveProfile">
          <el-form-item label="用户名" required><el-input v-model="form.username" maxlength="50" show-word-limit /></el-form-item>
          <div class="profile-form-actions">
            <el-button type="primary" :loading="saving" @click="saveProfile">保存修改</el-button>
            <el-button type="danger" plain @click="logout">退出登录</el-button>
          </div>
        </el-form>
      </article>

      <article class="profile-panel">
        <div class="profile-panel-title"><div><span class="eyebrow">SECURITY</span><h2>修改密码</h2></div><p>修改成功后需要重新登录</p></div>
        <el-form label-position="top" @submit.prevent="changePassword">
          <el-form-item label="当前密码" required><el-input v-model="passwordForm.currentPassword" type="password" show-password autocomplete="current-password" /></el-form-item>
          <el-form-item label="新密码" required><el-input v-model="passwordForm.newPassword" type="password" show-password autocomplete="new-password" placeholder="8-64位" /></el-form-item>
          <el-form-item label="确认新密码" required><el-input v-model="passwordForm.confirmPassword" type="password" show-password autocomplete="new-password" /></el-form-item>
          <el-button type="primary" :loading="changingPassword" @click="changePassword">修改密码</el-button>
        </el-form>
      </article>

      <article class="profile-panel liked-panel">
        <div class="profile-panel-title"><div><span class="eyebrow">FAVORITES</span><h2>我喜欢的文章</h2></div><p>共 {{ likes.total }} 篇</p></div>
        <div v-loading="loading" class="profile-like-list">
          <ArticleCard v-for="article in likes.records" :key="article.id" :article="article" />
          <el-empty v-if="!loading && !likes.records.length" description="还没有喜欢的文章" />
        </div>
        <div v-if="likes.total > 6" class="pagination-wrap">
          <el-pagination v-model:current-page="page" :page-size="6" :total="likes.total" layout="prev, pager, next" @current-change="loadLikes" />
        </div>
      </article>
    </div>
  </section>
</template>
