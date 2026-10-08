<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <main class="placeholder-view">
    <h1>{{ route.meta.title }}</h1>
    <p>页面将在后续开发阶段实现。</p>
  </main>
</template>

<style scoped>
.placeholder-view {
  min-height: 100vh;
  display: grid;
  place-content: center;
  text-align: center;
  color: #303133;
}

p { color: #909399; }
</style>
