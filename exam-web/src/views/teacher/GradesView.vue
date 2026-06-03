<template>
  <div class="page-card">
    <h4 style="margin-bottom:16px">各班成绩统计</h4>
    <el-table :data="stats" stripe v-loading="loading" border>
      <el-table-column prop="className" label="班级" width="180" />
      <el-table-column prop="grade" label="年级" width="100" />
      <el-table-column prop="studentCount" label="学生人数" width="100" />
      <el-table-column prop="examCount" label="考试次数" width="100" />
      <el-table-column label="平均分" width="100">
        <template #default="{row}">{{ row.averageScore }}</template>
      </el-table-column>
      <el-table-column label="通过率" width="120">
        <template #default="{row}">
          <el-progress :percentage="Math.round(row.passRate)" :color="row.passRate >= 60 ? '#67C23A' : '#F56C6C'" :stroke-width="18" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{row}">
          <el-button size="small" type="primary" @click="showClassDetail(row)">查看学生成绩</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 班级学生详情 -->
    <el-dialog :title="`${currentClass?.className} - 学生成绩`" v-model="detailVisible" width="900px">
      <el-table :data="classRecords" stripe v-loading="detailLoading">
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="paperName" label="试卷" />
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column label="结果" width="80">
          <template #default="{row}">
            <el-tag :type="row.isPassed ? 'success' : 'danger'">{{ row.isPassed ? '通过' : '未通过' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="交卷时间" width="170" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getTeacherClassStatistics, getTeacherExamRecords } from '@/api/exam'

const stats = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const currentClass = ref(null)
const classRecords = ref([])

async function fetchData() {
  loading.value = true
  try {
    const res = await getTeacherClassStatistics()
    stats.value = res.data || []
  } finally { loading.value = false }
}

async function showClassDetail(row) {
  currentClass.value = row
  detailVisible.value = true
  detailLoading.value = true
  try {
    const res = await getTeacherExamRecords({ pageSize: 200, classId: row.classId })
    classRecords.value = res.data?.records || []
  } finally { detailLoading.value = false }
}

onMounted(fetchData)
</script>
