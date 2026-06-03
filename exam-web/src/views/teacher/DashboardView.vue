<template>
  <div>
    <el-row :gutter="20" style="margin-bottom:20px">
      <el-col :span="6">
        <div class="page-card stat-card">
          <div class="stat-value" style="color:#409EFF">{{ stats.studentCount }}</div>
          <div class="stat-label">学生总数</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="page-card stat-card">
          <div class="stat-value" style="color:#67C23A">{{ stats.questionCount }}</div>
          <div class="stat-label">题库总量</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="page-card stat-card">
          <div class="stat-value" style="color:#E6A23C">{{ stats.paperCount }}</div>
          <div class="stat-label">试卷数量</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="page-card stat-card">
          <div class="stat-value" style="color:#F56C6C">{{ stats.averageScore || 0 }}</div>
          <div class="stat-label">学生平均分</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="8">
        <div class="page-card">
          <h4>通过率</h4>
          <el-progress type="dashboard" :percentage="Math.round(stats.passRate || 0)" :color="customColors" style="margin:20px auto;display:block" />
        </div>
      </el-col>
      <el-col :span="16">
        <div class="page-card">
          <h4 style="margin-bottom:16px">快捷操作</h4>
          <el-space wrap>
            <el-button type="primary" @click="$router.push('/teacher/questions')">📝 管理题库</el-button>
            <el-button type="success" @click="$router.push('/teacher/papers')">📄 创建试卷</el-button>
            <el-button type="warning" @click="$router.push('/teacher/arrangements')">📅 安排考试</el-button>
            <el-button type="info" @click="$router.push('/teacher/grading')">📋 批阅试卷</el-button>
            <el-button type="danger" @click="$router.push('/teacher/grades')">📊 班级成绩</el-button>
          </el-space>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getTeacherOverview } from '@/api/exam'

const stats = ref({ studentCount: 0, questionCount: 0, paperCount: 0, averageScore: 0, passRate: 0 })
const customColors = [
  { color: '#f56c6c', percentage: 20 },
  { color: '#e6a23c', percentage: 50 },
  { color: '#67c23a', percentage: 80 }
]

onMounted(async () => {
  try {
    const res = await getTeacherOverview()
    if (res.code === 200) stats.value = res.data
  } catch (e) { /* */ }
})
</script>
