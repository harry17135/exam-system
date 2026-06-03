<template>
  <el-container style="height:100%">
    <el-aside width="220px" class="sidebar-container">
      <div class="logo">
        <el-icon :size="24"><School /></el-icon>
        <span>在线考试系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
        @select="handleSelect"
      >
        <template v-if="userStore.role === 'STUDENT'">
          <el-menu-item index="/student/dashboard">
            <el-icon><HomeFilled /></el-icon><span>学习首页</span>
          </el-menu-item>
          <el-menu-item index="/student/records">
            <el-icon><Document /></el-icon><span>考试记录</span>
          </el-menu-item>
        </template>
        <template v-if="userStore.role === 'TEACHER' || userStore.role === 'ADMIN'">
          <el-menu-item index="/teacher/dashboard">
            <el-icon><HomeFilled /></el-icon><span>工作台</span>
          </el-menu-item>
          <el-menu-item index="/teacher/questions">
            <el-icon><Collection /></el-icon><span>题库管理</span>
          </el-menu-item>
          <el-menu-item index="/teacher/papers">
            <el-icon><Tickets /></el-icon><span>试卷管理</span>
          </el-menu-item>
          <el-menu-item index="/teacher/arrangements">
            <el-icon><Calendar /></el-icon><span>考试安排</span>
          </el-menu-item>
          <el-menu-item index="/teacher/grading">
            <el-icon><EditPen /></el-icon><span>批阅试卷</span>
          </el-menu-item>
          <el-menu-item index="/teacher/grades">
            <el-icon><DataAnalysis /></el-icon><span>成绩查询</span>
          </el-menu-item>
        </template>
        <template v-if="userStore.role === 'ADMIN'">
          <el-menu-item index="/admin/dashboard">
            <el-icon><Monitor /></el-icon><span>系统概览</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon><span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/classes">
            <el-icon><OfficeBuilding /></el-icon><span>班级管理</span>
          </el-menu-item>
        </template>
        <el-menu-item index="/profile">
          <el-icon><UserFilled /></el-icon><span>个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff;display:flex;align-items:center;justify-content:space-between;padding:0 24px;box-shadow:0 1px 4px rgba(0,0,0,.08);z-index:1">
        <span style="font-size:16px;font-weight:500">{{ pageTitle }}</span>
        <el-dropdown @command="handleCommand">
          <span style="cursor:pointer;display:flex;align-items:center;gap:8px">
            <el-avatar :size="32" icon="UserFilled" />
            <span>{{ userStore.user?.realName || '用户' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '')

function handleCommand(cmd) {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' }).then(() => {
      userStore.logout()
      router.push('/login')
    }).catch(() => {})
  } else if (cmd === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255,255,255,.1);
}
.el-menu { border-right: none; }
.el-main { background: #f5f7fa; padding: 20px; min-height: calc(100vh - 60px); }
</style>
