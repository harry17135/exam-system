<template>
  <div>
    <el-row :gutter="20" style="margin-bottom:20px">
      <el-col :span="6">
        <div class="page-card stat-card">
          <div class="stat-value" style="color:#E6A23C">{{ inProgressExams.length }}</div>
          <div class="stat-label">进行中</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="page-card stat-card">
          <div class="stat-value" style="color:#409EFF">{{ pendingExams.length }}</div>
          <div class="stat-label">待参加</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="page-card stat-card">
          <div class="stat-value" style="color:#67C23A">{{ passedCount }}</div>
          <div class="stat-label">已通过</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="page-card stat-card">
          <div class="stat-value" style="color:#909399">{{ avgScore }}</div>
          <div class="stat-label">平均分</div>
        </div>
      </el-col>
    </el-row>

    <!-- 进行中的考试 -->
    <div class="page-card" v-if="inProgressExams.length > 0">
      <h4 style="margin-bottom:16px;color:#E6A23C">⚠️ 进行中的考试</h4>
      <el-table :data="inProgressExams" stripe style="width:100%">
        <el-table-column prop="examName" label="考试名称" />
        <el-table-column label="状态" width="120"><el-tag type="warning">进行中</el-tag></el-table-column>
        <el-table-column label="操作" width="130">
          <template #default="{row}">
            <el-button type="warning" size="small" @click="enterExam(row)">继续考试</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 待参加的考试 -->
    <div class="page-card">
      <h4 style="margin-bottom:16px">📋 待参加的考试</h4>
      <el-table :data="pendingExams" stripe style="width:100%">
        <el-table-column prop="examName" label="考试名称" />
        <el-table-column label="状态" width="120"><el-tag type="info">待考</el-tag></el-table-column>
        <el-table-column label="操作" width="130">
          <template #default="{row}">
            <el-button type="primary" size="small" @click="enterExam(row)">进入考试</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="pendingExams.length === 0" description="暂无待考科目" />
    </div>

    <!-- 已完成的考试 -->
    <div class="page-card" style="margin-top:20px" v-if="finishedExams.length > 0">
      <h4 style="margin-bottom:16px">✅ 已完成的考试</h4>
      <el-table :data="finishedExams" stripe style="width:100%">
        <el-table-column prop="examName" label="考试名称" />
        <el-table-column label="成绩" width="100">
          <template #default="{row}">
            <span :style="{color: row.isPassed ? '#67C23A' : '#F56C6C', fontWeight:'bold'}">{{ row.totalScore }}分</span>
          </template>
        </el-table-column>
        <el-table-column label="结果" width="100">
          <template #default="{row}">
            <el-tag :type="row.isPassed ? 'success' : 'danger'">{{ row.isPassed ? '通过' : '未通过' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130">
          <template #default>
            <el-button type="info" size="small" disabled>已完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getArrangements, getMyRecords } from '@/api/exam'

const router = useRouter()
const allArrangements = ref([])
const inProgressExams = ref([])
const pendingExams = ref([])
const finishedExams = ref([])

const passedCount = computed(() => finishedExams.value.filter(r => r.isPassed).length)
const avgScore = computed(() => {
  if (!finishedExams.value.length) return 0
  return Math.round(finishedExams.value.reduce((s, r) => s + r.totalScore, 0) / finishedExams.value.length)
})

function enterExam(row) {
  router.push(`/student/exam/${row.id}`)
}

onMounted(async () => {
  try {
    const [aRes, rRes] = await Promise.all([
      getArrangements({ pageSize: 100 }),
      getMyRecords({ pageSize: 100 })
    ])
    allArrangements.value = aRes.data?.records || []
    const records = rRes.data?.records || []

    // 建立 record 映射：arrangementId → record
    const recordMap = {}
    records.forEach(r => { if (r.arrangementId) recordMap[r.arrangementId] = r })

    // 进行中
    const inProgressArrIds = new Set(
      records.filter(r => r.status === 'IN_PROGRESS').map(r => r.arrangementId)
    )
    inProgressExams.value = allArrangements.value
      .filter(a => inProgressArrIds.has(a.id))
      .map(a => ({ ...a, recordId: recordMap[a.id]?.id }))

    // 已完成
    const finishedArrIds = new Set(
      records.filter(r => r.status === 'FINISHED').map(r => r.arrangementId)
    )
    finishedExams.value = allArrangements.value
      .filter(a => finishedArrIds.has(a.id))
      .map(a => ({
        ...a,
        totalScore: recordMap[a.id]?.totalScore || 0,
        isPassed: recordMap[a.id]?.isPassed || 0,
        endTime: recordMap[a.id]?.endTime
      }))

    // 待参加
    pendingExams.value = allArrangements.value.filter(
      a => a.status !== 'FINISHED' && !finishedArrIds.has(a.id) && !inProgressArrIds.has(a.id)
    )
  } catch (e) { /* ignore */ }
})
</script>
