<script setup>
defineProps({ article: { type: Object, required: true }, featured: Boolean })
</script>

<template>
  <article class="post-card" :class="{ featured }">
    <router-link :to="`/articles/${article.id}`" class="post-cover" :style="article.coverUrl ? { backgroundImage: `url(${article.coverUrl})` } : {}">
      <span v-if="!article.coverUrl">{{ article.title?.slice(0, 1) }}</span>
    </router-link>
    <div class="post-body">
      <div class="post-meta"><span>{{ article.categoryName || '随笔' }}</span><time>{{ article.publishedAt?.slice(0, 10) || '尚未发布' }}</time></div>
      <h3><router-link :to="`/articles/${article.id}`">{{ article.title }}</router-link></h3>
      <p>{{ article.summary || '作者暂未填写文章摘要。' }}</p>
      <div class="post-footer"><div class="tag-row"><span v-for="tag in article.tags?.slice(0, 3)" :key="tag.id"># {{ tag.name }}</span></div><span>◉ {{ article.viewCount || 0 }}</span></div>
    </div>
  </article>
</template>
