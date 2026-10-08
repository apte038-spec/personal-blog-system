<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '../../api/blog'

const categories = ref([])
const tags = ref([])
const config = reactive({})
const dialog = ref(false)
const loading = ref(false)
const saving = ref(false)
const form = reactive({ id: null, name: '', description: '', sortOrder: 0, status: 'ACTIVE' })
const tagDialog = ref(false)
const tagForm = reactive({ id: null, name: '', slug: '', description: '', status: 'ACTIVE' })

async function load() {
  loading.value = true
  try {
    const [categoryList, tagList, configList] = await Promise.all([adminApi.categories(), adminApi.tags(), adminApi.configs()])
    categories.value = categoryList
    tags.value = tagList
    Object.keys(config).forEach((key) => delete config[key])
    configList.forEach((item) => { config[item.configKey] = item.configValue })
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

function open(category) {
  Object.assign(form, { id: null, name: '', description: '', sortOrder: 0, status: 'ACTIVE' }, category || {})
  dialog.value = true
}

async function saveCategory() {
  if (!form.name.trim()) return ElMessage.warning('请输入分类名称')
  saving.value = true
  try {
    await adminApi.saveCategory(form.id, form)
    dialog.value = false
    await load()
    ElMessage.success('分类已保存')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    saving.value = false
  }
}

async function removeCategory(id) {
  try {
    await ElMessageBox.confirm('确认删除该分类？被文章使用的分类无法删除。', '删除分类', { type: 'warning' })
    await adminApi.deleteCategory(id)
    await load()
    ElMessage.success('分类已删除')
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') ElMessage.error(error.message)
  }
}

async function saveConfig() {
  saving.value = true
  try {
    await adminApi.saveConfigs(config)
    ElMessage.success('配置已保存')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    saving.value = false
  }
}

function openTag(tag) {
  Object.assign(tagForm, { id: null, name: '', slug: '', description: '', status: 'ACTIVE' }, tag || {})
  tagDialog.value = true
}

async function saveTag() {
  if (!tagForm.name.trim() || !tagForm.slug.trim()) return ElMessage.warning('请输入标签名称和别名')
  saving.value = true
  try { await adminApi.saveTag(tagForm.id, tagForm); tagDialog.value = false; await load(); ElMessage.success('标签已保存') }
  catch (error) { ElMessage.error(error.message) }
  finally { saving.value = false }
}

async function removeTag(id) {
  try {
    await ElMessageBox.confirm('确认删除该标签？被文章使用的标签无法删除。', '删除标签', { type: 'warning' })
    await adminApi.deleteTag(id); await load(); ElMessage.success('标签已删除')
  } catch (error) { if (error !== 'cancel' && error !== 'close') ElMessage.error(error.message) }
}

onMounted(load)
</script>

<template>
  <section v-loading="loading" class="admin-panel">
    <el-tabs>
      <el-tab-pane label="文章分类">
        <div class="panel-toolbar">
          <span class="table-count">维护文章所属分类</span>
          <el-button type="primary" @click="open()">新增分类</el-button>
        </div>
        <el-table :data="categories" row-key="id">
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="sortOrder" label="排序" width="100" />
          <el-table-column label="状态" width="110">
            <template #default="{ row }"><el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status === 'ACTIVE' ? '启用' : '停用' }}</el-tag></template>
          </el-table-column>
          <el-table-column label="操作" width="140">
            <template #default="{ row }"><el-button link @click="open(row)">编辑</el-button><el-button link type="danger" @click="removeCategory(row.id)">删除</el-button></template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="文章标签">
        <div class="panel-toolbar"><span class="table-count">维护文章标签；已引用标签请停用而非删除</span><el-button type="primary" @click="openTag()">新增标签</el-button></div>
        <el-table :data="tags" row-key="id"><el-table-column prop="name" label="名称"/><el-table-column prop="slug" label="别名"/><el-table-column prop="description" label="描述"/><el-table-column label="状态" width="110"><template #default="{ row }"><el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status === 'ACTIVE' ? '启用' : '停用' }}</el-tag></template></el-table-column><el-table-column label="操作" width="140"><template #default="{ row }"><el-button link @click="openTag(row)">编辑</el-button><el-button link type="danger" @click="removeTag(row.id)">删除</el-button></template></el-table-column></el-table>
      </el-tab-pane>
      <el-tab-pane label="站点配置">
        <el-form label-width="100px" style="max-width: 700px; padding-top: 18px">
          <el-form-item label="博客名称"><el-input v-model="config.siteName" /></el-form-item>
          <el-form-item label="副标题"><el-input v-model="config.siteSubtitle" /></el-form-item>
          <el-form-item label="首页简介"><el-input v-model="config.homeIntro" type="textarea" /></el-form-item>
          <el-form-item label="关于我"><el-input v-model="config.aboutContent" type="textarea" :rows="6" /></el-form-item>
          <el-form-item label="联系邮箱"><el-input v-model="config.email" /></el-form-item>
          <el-form-item><el-button type="primary" :loading="saving" @click="saveConfig">保存配置</el-button></el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </section>
  <el-dialog v-model="dialog" title="分类" width="min(520px, 92%)">
    <el-form label-width="70px">
      <el-form-item label="名称" required><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
      <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
      <el-form-item label="状态"><el-switch v-model="form.status" active-value="ACTIVE" inactive-value="INACTIVE" /></el-form-item>
    </el-form>
    <template #footer><el-button @click="dialog = false">取消</el-button><el-button type="primary" :loading="saving" @click="saveCategory">保存</el-button></template>
  </el-dialog>
  <el-dialog v-model="tagDialog" title="标签" width="min(520px, 92%)"><el-form label-width="70px"><el-form-item label="名称" required><el-input v-model="tagForm.name"/></el-form-item><el-form-item label="别名" required><el-input v-model="tagForm.slug" placeholder="lowercase-slug"/></el-form-item><el-form-item label="描述"><el-input v-model="tagForm.description"/></el-form-item><el-form-item label="状态"><el-switch v-model="tagForm.status" active-value="ACTIVE" inactive-value="INACTIVE"/></el-form-item></el-form><template #footer><el-button @click="tagDialog=false">取消</el-button><el-button type="primary" :loading="saving" @click="saveTag">保存</el-button></template></el-dialog>
</template>
