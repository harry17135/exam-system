<template>
  <div>
    <el-row :gutter="20" style="margin-bottom:20px">
      <el-col :span="6"><div class="page-card stat-card"><div class="stat-value">{{ stats.totalStudents }}</div><div class="stat-label">学生人数</div></div></el-col>
      <el-col :span="6"><div class="page-card stat-card"><div class="stat-value" style="color:#67C23A">{{ stats.totalTeachers }}</div><div class="stat-label">教师人数</div></div></el-col>
      <el-col :span="6"><div class="page-card stat-card"><div class="stat-value" style="color:#E6A23C">{{ stats.totalQuestions }}</div><div class="stat-label">题库总数</div></div></el-col>
      <el-col :span="6"><div class="page-card stat-card"><div class="stat-value" style="color:#409EFF">{{ stats.totalExams }}</div><div class="stat-label">考试次数</div></div></el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="page-card"><h4 style="margin-bottom:16px">考试统计</h4>
          <p>平均分：<strong>{{ stats.averageScore || 0 }}</strong></p>
          <p>通过率：<strong style="color:#67C23A">{{ stats.passRate || 0 }}%</strong></p>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="page-card"><h4 style="margin-bottom:16px">快捷操作</h4>
          <el-button type="primary" @click="$router.push('/admin/users')">用户管理</el-button>
          <el-button type="success" @click="$router.push('/admin/classes')">班级管理</el-button>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStatistics } from '@/api/user'

const stats = ref({})

onMounted(async () => {
  try { const res = await getStatistics(); stats.value = res.data || {} } catch {}
})
</script>
