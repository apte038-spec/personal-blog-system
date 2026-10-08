<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { interactionApi } from '../../api/blog'
import { useAuthStore } from '../../store/auth'

const auth = useAuthStore(), route = useRoute(), router = useRouter()
const data = ref({ records: [], total: 0 }), page = ref(1), size = 10
const content = ref(''), loading = ref(true), submitting = ref(false)

async function load() {
  loading.value = true
  try { data.value = await interactionApi.messages({ page: page.value, size }) }
  catch (error) { ElMessage.error(error.message) }
  finally { loading.value = false }
}

async function submit() {
  if (!auth.isAuthenticated) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  if (!content.value.trim()) return ElMessage.warning('请输入留言内容')
  submitting.value = true
  try {
    await interactionApi.leaveMessage(content.value.trim())
    content.value = ''
    ElMessage.success('留言已提交，审核后展示')
  } catch (error) { ElMessage.error(error.message) }
  finally { submitting.value = false }
}

onMounted(load)
</script>

<template>
  <div class="message-page">
    <header class="page-intro centered"><span class="eyebrow">GUESTBOOK</span><h1>留言板</h1><p>不必拘泥于主题，分享近况、建议，或者简单打个招呼。</p></header>
    <section class="message-board">
      <div class="message-editor"><el-input v-model="content" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="留下你想说的话…"/><div><span>{{ auth.isAuthenticated ? `以 ${auth.user?.nickname} 的身份留言` : '登录后可以留言' }}</span><el-button type="primary" :loading="submitting" @click="submit">发布留言</el-button></div></div>
      <div v-loading="loading" class="message-grid"><article v-for="item in data.records" :key="item.id" class="message-card"><div class="avatar">{{ (item.nickname || '访').slice(0,1) }}</div><div><strong>{{ item.nickname || '访客' }}</strong><time>{{ item.createdAt?.replace('T',' ').slice(0,16) }}</time><p>{{ item.content }}</p></div></article><el-empty v-if="!loading && !data.records?.length" description="等待第一条留言"/></div>
      <div v-if="data.total > size" class="pagination-wrap"><el-pagination v-model:current-page="page" :page-size="size" :total="data.total" layout="prev, pager, next" @current-change="load" /></div>
    </section>
  </div>
</template>
