<template>
  <div class="page-card" v-loading="loading">
    <template v-if="paper">
      <div class="exam-header">
        <h3>{{ paper.paperName }}</h3>
        <div>
          <span>总分：{{ paper.totalScore }}分</span>
          <span style="margin-left:20px">剩余时间：<el-tag :type="remaining < 300 ? 'danger' : 'warning'">{{ formatTime(remaining) }}</el-tag></span>
          <el-button type="danger" style="margin-left:20px" @click="handleSubmit" :loading="submitting">交卷</el-button>
        </div>
      </div>
      <el-divider />

      <div v-for="(q, idx) in paper.questions" :key="q.questionId" class="question-item">
        <div class="question-title">
          <el-tag size="small" :type="typeColor(q.type)" style="margin-right:8px">{{ typeLabel(q.type) }}</el-tag>
          <span>{{ idx + 1 }}. {{ q.title }} ({{ q.score }}分)</span>
        </div>

        <!-- 单选题 / 判断题 -->
        <el-radio-group v-if="q.type === 'SINGLE' || q.type === 'JUDGE'" v-model="answers[q.questionId]" style="margin-left:40px">
          <el-radio v-for="opt in parseOptions(q.options, q.type)" :key="opt.label" :label="opt.label" style="display:block;margin:8px 0">
            {{ opt.label }}. {{ opt.content }}
          </el-radio>
        </el-radio-group>

        <!-- 多选题 -->
        <el-checkbox-group v-if="q.type === 'MULTI'" v-model="multiAnswers[q.questionId]" style="margin-left:40px">
          <el-checkbox v-for="opt in parseOptions(q.options, q.type)" :key="opt.label" :label="opt.label" style="display:block;margin:8px 0">
            {{ opt.label }}. {{ opt.content }}
          </el-checkbox>
        </el-checkbox-group>

        <!-- 简答题 -->
        <el-input v-if="q.type === 'ESSAY'" v-model="answers[q.questionId]" type="textarea" :rows="4" placeholder="请输入答案" style="margin-left:40px;width:95%" />
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { startExam, submitExam } from '@/api/exam'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const paper = ref(null)
const loading = ref(false)
const submitting = ref(false)
const answers = reactive({})
const multiAnswers = reactive({})
const remaining = ref(0)
let timer = null

function parseOptions(opts, qtype) {
  try {
    let options = typeof opts === 'string' ? JSON.parse(opts) : opts
    if (!options || options.length === 0) {
      if (qtype === 'JUDGE') {
        return [{ label: 'A', content: '正确' }, { label: 'B', content: '错误' }]
      }
      return []
    }
    return options
  } catch { return [] }
}

function typeLabel(t) { return { SINGLE: '单选', MULTI: '多选', JUDGE: '判断', ESSAY: '简答' }[t] || t }
function typeColor(t) { return { SINGLE: '', MULTI: 'warning', JUDGE: 'info', ESSAY: 'success' }[t] || '' }
function formatTime(s) { const m = Math.floor(s / 60); const sec = s % 60; return `${m}:${sec.toString().padStart(2, '0')}` }

onMounted(async () => {
  loading.value = true
  try {
    const res = await startExam({ arrangementId: Number(route.params.id) })
    paper.value = res.data
    remaining.value = (paper.value.duration || 120) * 60
    timer = setInterval(() => { if (remaining.value > 0) remaining.value--; else handleSubmit() }, 1000)
  } catch (e) {
    ElMessage.error(e.message || '加载考试失败')
    router.back()
  } finally { loading.value = false }
})

onBeforeUnmount(() => { if (timer) clearInterval(timer) })

async function handleSubmit() {
  if (submitting.value) return
  try {
    await ElMessageBox.confirm('确定要交卷吗？交卷后不可修改。', '确认交卷', { confirmButtonText: '确定', cancelButtonText: '继续答题', type: 'warning' })
  } catch { return }

  submitting.value = true
  if (timer) clearInterval(timer)

  const submitAnswers = Object.keys(answers).map(qid => {
    const ans = multiAnswers[qid] ? multiAnswers[qid].sort().join('') : (answers[qid] || '')
    return { questionId: Number(qid), answer: ans }
  })

  try {
    const res = await submitExam({ recordId: paper.value.recordId, answers: submitAnswers })
    ElMessage.success(`交卷成功！客观题得分：${res.data || res.message}`)
    router.push('/student/records')
  } catch (e) {
    ElMessage.error('交卷失败')
  } finally { submitting.value = false }
}
</script>

<style scoped>
.exam-header { display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:12px }
.question-item { margin-bottom:24px; padding-bottom:16px; border-bottom:1px solid #ebeef5 }
.question-title { margin-bottom:8px; font-weight:500 }
</style>
