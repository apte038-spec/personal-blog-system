<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { interactionApi, publicApi } from '../../api/blog'
import { useAuthStore } from '../../store/auth'

const route = useRoute(), router = useRouter(), auth = useAuthStore()
const article = ref(null), adjacent = ref({}), comments = ref([]), commentText = ref('')
const loading = ref(true), submitting = ref(false), liked = ref(false), socialAvailable = ref(true)
const renderedContent = computed(() => DOMPurify.sanitize(marked.parse(article.value?.content || '')))

async function load() {
  loading.value = true; socialAvailable.value = true
  try {
    const id = route.params.id
    const [detail, near] = await Promise.all([publicApi.article(id), publicApi.adjacent(id).catch(() => ({}))])
    article.value = detail; adjacent.value = near
    const result = await interactionApi.comments(id).catch(() => { socialAvailable.value = false; return [] })
    comments.value = result.records || result || []
    if (auth.isAuthenticated) {
      const state = await interactionApi.likeState(id).catch(() => null)
      liked.value = Boolean(state?.liked)
    }
  } catch (error) { ElMessage.error(error.message); router.push('/articles') }
  finally { loading.value = false }
}
async function toggleLike() {
  if (!auth.isAuthenticated) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  try { liked.value ? await interactionApi.unlike(article.value.id) : await interactionApi.like(article.value.id); liked.value = !liked.value; article.value.likeCount += liked.value ? 1 : -1 }
  catch (error) { ElMessage.error(error.message) }
}
async function submitComment() {
  if (!auth.isAuthenticated) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  if (!commentText.value.trim()) return
  submitting.value = true
  try { await interactionApi.comment(article.value.id, commentText.value.trim()); commentText.value = ''; ElMessage.success('评论已提交，审核后展示') }
  catch (error) { ElMessage.error(error.message) }
  finally { submitting.value = false }
}
watch(() => route.params.id, load, { immediate: true })
</script>

<template>
  <div v-loading="loading" class="detail-page">
    <article v-if="article" class="article-paper">
      <header class="article-header"><div class="post-meta"><span>{{ article.categoryName }}</span><time>{{ article.publishedAt?.slice(0,10) }}</time></div><h1>{{ article.title }}</h1><p class="article-summary">{{ article.summary }}</p><div class="article-stats"><span>{{ article.authorName }}</span><span>◉ {{ article.viewCount }} 阅读</span><span>♥ {{ article.likeCount }} 喜欢</span><span>✦ {{ article.commentCount }} 评论</span></div><div class="tag-row"><span v-for="tag in article.tags" :key="tag.id"># {{ tag.name }}</span></div></header>
      <div class="markdown-body" v-html="renderedContent"></div>
      <footer class="article-actions"><button class="like-button" :class="{ liked }" @click="toggleLike">{{ liked ? '♥ 已喜欢' : '♡ 喜欢这篇文章' }}</button></footer>
    </article>

    <nav v-if="article" class="adjacent-nav"><router-link v-if="adjacent.previous" :to="`/articles/${adjacent.previous.id}`"><small>上一篇</small><strong>{{ adjacent.previous.title }}</strong></router-link><span v-else></span><router-link v-if="adjacent.next" :to="`/articles/${adjacent.next.id}`"><small>下一篇</small><strong>{{ adjacent.next.title }}</strong></router-link></nav>

    <section v-if="article" class="comment-section"><div class="section-heading"><div><span class="eyebrow">DISCUSSION</span><h2>评论</h2></div><span>{{ comments.length }} 条</span></div><div class="comment-editor"><el-input v-model="commentText" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="写下你的想法…"/><div><span v-if="!auth.isAuthenticated">登录后参与讨论</span><el-button type="primary" :loading="submitting" @click="submitComment">发表评论</el-button></div></div><div class="comment-list"><div v-for="item in comments" :key="item.id" class="comment-item"><div class="avatar">{{ (item.nickname || item.user?.nickname || '访').slice(0,1) }}</div><div><strong>{{ item.nickname || item.user?.nickname || '访客' }}</strong><time>{{ item.createdAt?.replace('T',' ').slice(0,16) }}</time><p>{{ item.content }}</p></div></div><el-empty v-if="!comments.length" description="还没有评论，来聊聊吧"/></div></section>
  </div>
</template>
