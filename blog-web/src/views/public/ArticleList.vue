<script setup>
import { onMounted, ref, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { publicApi } from '../../api/blog'
import ArticleCard from '../../components/ArticleCard.vue'

const route = useRoute(), router = useRouter()
const data = ref({ records: [], total: 0 }), categories = ref([]), tags = ref([]), loading = ref(false)
const page = ref(Number(route.query.page) || 1), categoryId = ref(route.query.categoryId ? Number(route.query.categoryId) : null), tagId = ref(route.query.tagId ? Number(route.query.tagId) : null)
const keyword = ref(String(route.query.keyword || ''))

async function load() {
  loading.value = true
  try { data.value = await publicApi.articles({ page: page.value, size: 8, keyword: keyword.value.trim() || undefined, categoryId: categoryId.value || undefined, tagId: tagId.value || undefined }) }
  finally { loading.value = false }
}
async function loadFilters() {
  const [categoryResult, tagResult] = await Promise.allSettled([publicApi.categories(), publicApi.tags()])
  if (categoryResult.status === 'fulfilled') categories.value = categoryResult.value
  if (tagResult.status === 'fulfilled') tags.value = tagResult.value
}
function selectCategory(id) { categoryId.value = categoryId.value === id ? null : id; page.value = 1 }
function selectTag(id) { tagId.value = tagId.value === id ? null : id; page.value = 1 }
function search() { page.value = 1; router.replace({ query: { keyword: keyword.value.trim() || undefined, categoryId: categoryId.value || undefined, tagId: tagId.value || undefined } }); load() }
watch([page, categoryId, tagId], () => { router.replace({ query: { page: page.value > 1 ? page.value : undefined, keyword: keyword.value.trim() || undefined, categoryId: categoryId.value || undefined, tagId: tagId.value || undefined } }); load() })
onMounted(() => { loadFilters(); load() })
</script>

<template>
  <div class="archive-page">
    <header class="page-intro"><span class="eyebrow">WRITING</span><h1>所有文章</h1><p>关于工程实践、产品思考，以及持续学习的记录。</p></header>
    <section class="filter-panel"><el-input v-model="keyword" clearable placeholder="搜索文章标题" :prefix-icon="Search" @keyup.enter="search" @clear="search"><template #append><el-button @click="search">搜索</el-button></template></el-input><div class="filter-line"><strong>分类</strong><button :class="{ active: !categoryId }" @click="categoryId=null;page=1">全部</button><button v-for="item in categories" :key="item.id" :class="{ active: categoryId===item.id }" @click="selectCategory(item.id)">{{ item.name }}</button></div><div class="filter-line"><strong>标签</strong><button :class="{ active: !tagId }" @click="tagId=null;page=1">全部</button><button v-for="tag in tags" :key="tag.id" :class="{ active: tagId===tag.id }" @click="selectTag(tag.id)"># {{ tag.name }}</button></div></section>
    <div v-loading="loading" class="post-list archive-list"><ArticleCard v-for="article in data.records" :key="article.id" :article="article"/><el-empty v-if="!loading && !data.records.length" description="这个筛选条件下还没有文章"/></div>
    <div class="pagination-wrap"><el-pagination v-model:current-page="page" :page-size="8" :total="data.total" layout="prev, pager, next" background/></div>
  </div>
</template>
