<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Close, Search } from '@element-plus/icons-vue'
import { adminApi } from '../../api/blog'

const data = ref({ records: [], total: 0 })
const stats = ref({ total: 0, pending: 0, approved: 0, rejected: 0 })
const query = reactive({ page: 1, size: 10, status: 'PENDING', keyword: '' })
const loading = ref(false)
const reviewing = ref(false)
const selected = ref([])
const tableRef = ref()

const statusText = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝' }
const statusType = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }

async function load() {
  loading.value = true
  try {
    const [pageData, statData] = await Promise.all([adminApi.comments(query), adminApi.commentStats()])
    data.value = pageData
    stats.value = statData
    selected.value = []
    tableRef.value?.clearSelection()
  } catch (error) { ElMessage.error(error.message) }
  finally { loading.value = false }
}

function search() { query.page = 1; load() }
function changeStatus(status) { query.status = status; query.page = 1; load() }

async function review(ids, status) {
  if (!ids.length) return ElMessage.warning('请先选择需要审核的评论')
  const action = status === 'APPROVED' ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定要${action}选中的 ${ids.length} 条评论吗？`, `${action}评论`, { type: 'warning' })
    reviewing.value = true
    await adminApi.reviewComments(ids, status)
    ElMessage.success(`已${action} ${ids.length} 条评论`)
    await load()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') ElMessage.error(error.message)
  } finally { reviewing.value = false }
}

onMounted(load)
</script>

<template>
  <div class="comment-manage-page">
    <div class="comment-stat-grid">
      <button :class="{ active: query.status === 'PENDING' }" @click="changeStatus('PENDING')"><span>待审核</span><strong>{{ stats.pending }}</strong></button>
      <button :class="{ active: query.status === 'APPROVED' }" @click="changeStatus('APPROVED')"><span>已通过</span><strong>{{ stats.approved }}</strong></button>
      <button :class="{ active: query.status === 'REJECTED' }" @click="changeStatus('REJECTED')"><span>已拒绝</span><strong>{{ stats.rejected }}</strong></button>
    </div>

    <section class="admin-panel">
      <div class="panel-toolbar comment-toolbar">
        <el-form inline @submit.prevent="search">
          <el-form-item><el-input v-model="query.keyword" clearable placeholder="搜索评论内容" :prefix-icon="Search" @clear="search" /></el-form-item>
          <el-button @click="search">查询</el-button>
        </el-form>
        <div class="batch-actions">
          <span>已选 {{ selected.length }} 条</span>
          <el-button type="success" plain :icon="Check" :disabled="!selected.length" :loading="reviewing" @click="review(selected.map(item => item.id), 'APPROVED')">批量通过</el-button>
          <el-button type="danger" plain :icon="Close" :disabled="!selected.length" :loading="reviewing" @click="review(selected.map(item => item.id), 'REJECTED')">批量拒绝</el-button>
        </div>
      </div>

      <el-table ref="tableRef" v-loading="loading" :data="data.records" row-key="id" @selection-change="selected = $event">
        <el-table-column type="selection" width="48" />
        <el-table-column label="评论用户" width="170">
          <template #default="{ row }"><div class="user-cell"><el-avatar :size="36" :src="row.avatar">{{ row.nickname?.slice(0, 1) }}</el-avatar><div><strong>{{ row.nickname }}</strong><small>@{{ row.username }}</small></div></div></template>
        </el-table-column>
        <el-table-column label="评论内容" min-width="280"><template #default="{ row }"><p class="comment-content-cell">{{ row.content }}</p></template></el-table-column>
        <el-table-column label="所属文章" min-width="210"><template #default="{ row }"><router-link class="admin-article-link" :to="`/articles/${row.articleId}`" target="_blank">{{ row.articleTitle }}</router-link></template></el-table-column>
        <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="statusType[row.status]" effect="light">{{ statusText[row.status] }}</el-tag></template></el-table-column>
        <el-table-column label="提交时间" width="165"><template #default="{ row }">{{ row.createdAt?.replace('T', ' ').slice(0, 16) }}</template></el-table-column>
        <el-table-column label="审核操作" width="145" fixed="right">
          <template #default="{ row }"><el-button v-if="row.status !== 'APPROVED'" link type="success" @click="review([row.id], 'APPROVED')">通过</el-button><el-button v-if="row.status !== 'REJECTED'" link type="danger" @click="review([row.id], 'REJECTED')">拒绝</el-button></template>
        </el-table-column>
      </el-table>

      <div class="table-pagination"><span>共 {{ data.total }} 条评论</span><el-pagination v-model:current-page="query.page" :page-size="query.size" :total="data.total" layout="prev, pager, next" @current-change="load" /></div>
    </section>
  </div>
</template>
