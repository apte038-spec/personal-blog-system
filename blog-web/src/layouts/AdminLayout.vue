<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DataAnalysis, Document, User, Setting, Fold, Expand, ChatDotRound } from '@element-plus/icons-vue'
import { useAuthStore } from '../store/auth'

const collapsed = ref(false), route = useRoute(), router = useRouter(), auth = useAuthStore()
function logout() { auth.logout(); router.push('/login') }
</script>

<template>
  <el-container class="admin-shell">
    <el-aside :width="collapsed ? '72px' : '230px'" class="admin-aside">
      <div class="admin-logo"><span>PB</span><strong v-show="!collapsed">博客管理</strong></div>
      <el-menu router :default-active="route.path" :collapse="collapsed" :collapse-transition="false">
        <el-menu-item index="/admin"><el-icon><DataAnalysis/></el-icon><template #title>数据概览</template></el-menu-item>
        <el-menu-item index="/admin/articles"><el-icon><Document/></el-icon><template #title>文章管理</template></el-menu-item>
        <el-menu-item index="/admin/users"><el-icon><User/></el-icon><template #title>用户管理</template></el-menu-item>
        <el-menu-item index="/admin/comments"><el-icon><ChatDotRound/></el-icon><template #title>评论管理</template></el-menu-item>
        <el-menu-item index="/admin/messages"><el-icon><ChatDotRound/></el-icon><template #title>留言管理</template></el-menu-item>
        <el-menu-item index="/admin/settings"><el-icon><Setting/></el-icon><template #title>系统管理</template></el-menu-item>
      </el-menu>
      <button class="collapse-button" @click="collapsed=!collapsed"><el-icon><component :is="collapsed ? Expand : Fold"/></el-icon><span v-if="!collapsed">收起菜单</span></button>
    </el-aside>
    <el-container>
      <el-header class="admin-header"><div><h1>{{ route.meta.title }}</h1><span>Personal Blog Console</span></div><el-dropdown><div class="admin-user"><el-avatar :size="36" :src="auth.user?.avatar">{{ auth.user?.nickname?.slice(0,1) || 'A' }}</el-avatar><span>{{ auth.user?.nickname || '管理员' }}</span></div><template #dropdown><el-dropdown-menu><el-dropdown-item @click="router.push('/')">返回博客</el-dropdown-item><el-dropdown-item divided @click="logout">退出登录</el-dropdown-item></el-dropdown-menu></template></el-dropdown></el-header>
      <el-main class="admin-main"><router-view/></el-main>
    </el-container>
  </el-container>
</template>
