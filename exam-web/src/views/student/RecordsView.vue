<template>
  <div class="page-card">
    <h4 style="margin-bottom:16px">我的考试记录</h4>
    <el-table :data="records" stripe v-loading="loading" style="width:100%">
      <el-table-column prop="examName" label="考试名称" />
      <el-table-column prop="paperName" label="试卷名称" />
      <el-table-column prop="totalScore" label="得分" width="100" />
      <el-table-column label="结果" width="100">
        <template #default="{row}">
          <el-tag :type="row.isPassed ? 'success' : 'danger'">{{ row.isPassed ? '通过' : '未通过' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="endTime" label="交卷时间" width="180" />
      <el-table-column label="状态" width="100">
        <template #default="{row}">
          <el-tag v-if="row.status === 'FINISHED'" type="info">已完成</el-tag>
          <el-tag v-else type="warning">进行中</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="pageNum" v-model:page-size="pageSize"
      :total="total" layout="total, prev, pager, next" style="margin-top:16px;justify-content:flex-end"
      @current-change="fetchData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyRecords } from '@/api/exam'

const records = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function fetchData() {
  loading.value = true
  try {
    const res = await getMyRecords({ pageNum: pageNum.value, pageSize: pageSize.value })
    records.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

onMounted(fetchData)
</script>
