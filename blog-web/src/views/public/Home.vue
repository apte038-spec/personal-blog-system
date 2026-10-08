<script setup>
import { onMounted, ref } from 'vue'
import { publicApi } from '../../api/blog'
import ArticleCard from '../../components/ArticleCard.vue'

const config = ref({ siteName: '行迹', homeIntro: '写代码，也写下沿途的思考。这里分享前端、后端与独立开发实践。' })
const latest = ref([]), popular = ref([]), loading = ref(true)

onMounted(async () => {
  const [site, articles, hot] = await Promise.allSettled([publicApi.config(), publicApi.latest(5), publicApi.hot(3)])
  if (site.status === 'fulfilled') config.value = { ...config.value, ...site.value }
  if (articles.status === 'fulfilled') {
    latest.value = articles.value
  }
  if (hot.status === 'fulfilled') popular.value = hot.value
  loading.value = false
})
</script>

<template>
  <div class="home-page">
    <section class="home-hero">
      <div class="hero-copy"><span class="eyebrow">HELLO, WORLD</span><h1>把复杂的技术，<br><em>写得清楚一点。</em></h1><p>{{ config.homeIntro }}</p><div class="hero-actions"><router-link class="primary-link" to="/articles">浏览文章 →</router-link><router-link class="text-link" to="/about">认识作者</router-link></div></div>
      <div class="hero-code" aria-hidden="true"><div class="code-bar"><i></i><i></i><i></i></div><pre><span>const</span> journal = {
  craft: <b>'code'</b>,
  share: <b>'ideas'</b>,
  stay: <b>'curious'</b>
}

journal.<strong>write</strong>()</pre></div>
    </section>

    <section class="content-section"><div class="section-heading"><div><span class="eyebrow">RECENT POSTS</span><h2>最近更新</h2></div><router-link to="/articles">查看全部 →</router-link></div><div v-loading="loading" class="post-list"><ArticleCard v-for="article in latest" :key="article.id" :article="article"/><el-empty v-if="!loading && !latest.length" description="还没有发布文章"/></div></section>

    <section class="popular-section"><div class="section-heading"><div><span class="eyebrow">MOST READ</span><h2>热门文章</h2></div></div><div class="popular-grid"><ArticleCard v-for="article in popular" :key="article.id" :article="article" featured/><el-empty v-if="!loading && !popular.length" description="暂无热门文章"/></div></section>
  </div>
</template>
