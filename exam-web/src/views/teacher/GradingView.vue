<template>
  <div class="page-card">
    <div class="search-bar">
      <el-select v-model="classFilter" placeholder="按班级筛选" clearable style="width:180px" @change="fetchData">
        <el-option v-for="c in classes" :key="c.id" :label="c.className" :value="c.id" />
      </el-select>
      <el-button @click="fetchData">刷新</el-button>
    </div>

    <el-table :data="records" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="studentName" label="学生姓名" width="100" />
      <el-table-column prop="className" label="班级" width="150" />
      <el-table-column prop="paperName" label="试卷" />
      <el-table-column prop="totalScore" label="总分" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{row}">
          <el-tag v-if="row.status==='FINISHED'" type="success">已交卷</el-tag>
          <el-tag v-else type="warning">进行中</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{row}">
          <el-button size="small" type="primary" @click="showDetail(row)" :disabled="row.status !== 'FINISHED'">批阅</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="pageNum" :total="total" layout="total,prev,pager,next" style="margin-top:16px;justify-content:flex-end" @current-change="fetchData" />

    <!-- 批阅详情对话框 -->
    <el-dialog title="批阅详情" v-model="dialogVisible" width="800px" @close="dialogVisible=false">
      <template v-if="detail">
        <div style="margin-bottom:16px">
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="学生">{{ detail.studentName }}</el-descriptions-item>
            <el-descriptions-item label="试卷">{{ detail.paperName }}</el-descriptions-item>
            <el-descriptions-item label="总分">{{ detail.totalScore }}分</el-descriptions-item>
          </el-descriptions>
        </div>
        <div v-for="(a, i) in detail.answers" :key="a.id" style="margin-bottom:12px;padding:12px;background:#f5f7fa;border-radius:8px">
          <p><strong>{{ i+1 }}. [{{ {SINGLE:'单选',MULTI:'多选',JUDGE:'判断',ESSAY:'简答'}[a.questionType] }}]</strong> {{ a.questionTitle }}</p>
          <p style="color:#909399">正确答案：{{ a.questionType==='JUDGE' ? (a.correctAnswer==='A'?'正确':'错误') : a.correctAnswer }}</p>
          <p>学生答案：<span :style="{color: a.isCorrect ? '#67C23A' : '#F56C6C'}">{{ a.studentAnswer || '未作答' }}</span></p>
          <div style="margin-top:8px" v-if="a.questionType === 'ESSAY' && !a.marked">
            <el-input-number v-model="essayScores[a.id]" :min="0" :max="a.maxScore" size="small" />
            <span style="margin-left:8px;color:#909399">/ {{ a.maxScore }}分</span>
            <el-button size="small" type="primary" style="margin-left:12px" @click="submitGrade(a)">✅ 提交评分</el-button>
          </div>
          <div v-else style="margin-top:4px">
            <el-tag :type="a.isCorrect?'success':'danger'" size="small">
              {{ a.isCorrect ? '✓ 正确' : '✗ 错误' }} ({{ a.score }}/{{ a.maxScore }}分)
            </el-tag>
            <el-tag v-if="a.questionType==='ESSAY' && a.marked" type="info" size="small" style="margin-left:4px">已批阅</el-tag>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getTeacherExamRecords, getTeacherRecordDetail, gradeEssay, getTeacherClasses } from '@/api/exam'
import { ElMessage } from 'element-plus'

const records = ref([])
const loading = ref(false)
const pageNum = ref(1)
const total = ref(0)
const classFilter = ref(null)
const classes = ref([])

const dialogVisible = ref(false)
const detail = ref(null)
const essayScores = reactive({})

async function fetchData() {
  loading.value = true
  try {
    const res = await getTeacherExamRecords({ pageNum: pageNum.value, pageSize: 20, classId: classFilter.value || undefined })
    records.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

async function showDetail(row) {
  try {
    const res = await getTeacherRecordDetail(row.id)
    detail.value = res.data
    dialogVisible.value = true
  } catch (e) { ElMessage.error('加载失败') }
}

async function submitGrade(answer) {
  try {
    await gradeEssay(detail.value.recordId, answer.questionId, essayScores[answer.id])
    ElMessage.success('评分成功')
    answer.score = essayScores[answer.id]
    answer.marked = 1
  } catch { ElMessage.error('评分失败') }
}

onMounted(async () => {
  try {
    const res = await getTeacherClasses()
    classes.value = res.data || []
  } catch {}
  fetchData()
})
</script>
