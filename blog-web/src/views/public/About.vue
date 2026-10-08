<script setup>
import { onMounted, ref } from 'vue'
import { publicApi } from '../../api/blog'

const config = ref({ aboutContent: '热爱技术，也保持对产品和生活的好奇。这个博客用于记录真实的学习过程，以及那些值得反复琢磨的问题。', email: 'hello@example.com' })
onMounted(async () => { try { config.value = { ...config.value, ...(await publicApi.config()) } } catch { /* 使用默认资料 */ } })
</script>

<template>
  <div class="about-page">
    <section class="about-hero"><div class="portrait">L</div><div><span class="eyebrow">ABOUT ME</span><h1>你好，我是这个博客的作者。</h1><p>{{ config.aboutContent }}</p></div></section>
    <section class="about-grid"><article><span>01</span><h2>我在做什么</h2><p>关注 Web 开发、软件工程与独立产品。从需求到上线，享受把想法一点点变成现实的过程。</p></article><article><span>02</span><h2>为什么写作</h2><p>写作迫使思考变得清晰。这里不追求标准答案，只记录被实践验证过的方法和仍在探索的问题。</p></article><article><span>03</span><h2>保持联系</h2><p>如果你对文章有想法，欢迎在留言板交流，或发送邮件至 <a :href="`mailto:${config.email}`">{{ config.email }}</a>。</p></article></section>
  </div>
</template>
