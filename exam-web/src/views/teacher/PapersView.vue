<template>
  <div class="page-card">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索试卷" clearable style="width:220px" @change="fetchData" />
      <el-button type="primary" @click="showCreate">创建试卷</el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="paperName" label="试卷名称" />
      <el-table-column prop="duration" label="时长(分)" width="90" />
      <el-table-column prop="totalScore" label="总分" width="70" />
      <el-table-column prop="passScore" label="及格分" width="70" />
      <el-table-column label="状态" width="100">
        <template #default="{row}">
          <el-tag v-if="row.status==='DRAFT'" type="info">草稿</el-tag>
          <el-tag v-else-if="row.status==='PUBLISHED'" type="success">已发布</el-tag>
          <el-tag v-else type="danger">已关闭</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{row}">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button v-if="row.status==='DRAFT'" size="small" type="success" @click="publish(row.id)">发布</el-button>
          <el-button v-if="row.status==='PUBLISHED'" size="small" type="warning" @click="closeExam(row.id)">关闭</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="pageNum" :total="total" layout="total,prev,pager,next" style="margin-top:16px;justify-content:flex-end" @current-change="fetchData" />

    <!-- 创建/编辑对话框 -->
    <el-dialog :title="editId?'编辑试卷':'创建试卷'" v-model="dialogVisible" width="700px" @close="resetForm">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.paperName" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-row>
          <el-col :span="8"><el-form-item label="时长"><el-input-number v-model="form.duration" :min="1" /> 分钟</el-form-item></el-col>
          <el-col :span="8"><el-form-item label="总分"><el-input-number v-model="form.totalScore" :min="1" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="及格分"><el-input-number v-model="form.passScore" :min="1" /></el-form-item></el-col>
        </el-row>
        <el-divider>选择试题</el-divider>
        <div style="margin-bottom:8px">
          <el-select v-model="selectedType" placeholder="筛选类型" clearable style="width:120px;margin-right:8px">
            <el-option label="单选题" value="SINGLE" /><el-option label="多选题" value="MULTI" /><el-option label="判断题" value="JUDGE" /><el-option label="简答题" value="ESSAY" />
          </el-select>
          <el-input v-model="questionKeyword" placeholder="搜索题目" style="width:200px" />
        </div>
        <el-table :data="availableQuestions" @selection-change="onSelect" max-height="300" stripe>
          <el-table-column type="selection" width="50" />
          <el-table-column prop="title" label="题目" show-overflow-tooltip />
          <el-table-column label="类型" width="70"><template #default="{row}">{{ typeMap[row.type] }}</template></el-table-column>
          <el-table-column label="分值" width="80"><template #default="{row}"><el-input-number v-model="qScores[row.id]" :min="1" :max="50" size="small" style="width:70px" /></template></el-table-column>
        </el-table>
        <div style="margin-top:8px;color:#909399">已选 {{ selectedQuestions.length }} 题</div>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getPapers, createPaper, updatePaper, deletePaper, publishPaper, closePaper } from '@/api/paper'
import { getQuestions } from '@/api/question'
import { ElMessage, ElMessageBox } from 'element-plus'

const typeMap = { SINGLE: '单选', MULTI: '多选', JUDGE: '判断', ESSAY: '简答' }
const list = ref([]); const loading = ref(false); const pageNum = ref(1); const total = ref(0)
const keyword = ref(''); const dialogVisible = ref(false); const editId = ref(null)
const form = reactive({ paperName: '', description: '', duration: 120, totalScore: 100, passScore: 60 })

const availableQuestions = ref([]); const selectedQuestions = ref([])
const qScores = reactive({})
const selectedType = ref(''); const questionKeyword = ref('')

async function fetchData() {
  loading.value = true
  try {
    const res = await getPapers({ pageNum: pageNum.value, pageSize: 10, keyword: keyword.value })
    list.value = res.data?.records || []; total.value = res.data?.total || 0
  } finally { loading.value = false }
}

async function loadQuestions() {
  const res = await getQuestions({ pageSize: 100, type: selectedType.value, keyword: questionKeyword.value })
  availableQuestions.value = (res.data?.records || []).map(q => { qScores[q.id] = q.score || 5; return q })
}

async function showCreate() { editId.value = null; resetForm(); dialogVisible.value = true; await loadQuestions() }
async function showEdit(row) {
  editId.value = row.id
  form.paperName = row.paperName; form.description = row.description || ''
  form.duration = row.duration; form.totalScore = row.totalScore; form.passScore = row.passScore
  dialogVisible.value = true
  await loadQuestions()
}
function resetForm() { Object.assign(form, { paperName: '', description: '', duration: 120, totalScore: 100, passScore: 60 }); selectedQuestions.value = [] }
function onSelect(rows) { selectedQuestions.value = rows }

async function handleSave() {
  const questions = selectedQuestions.value.map((q, i) => ({ questionId: q.id, score: qScores[q.id] || 5, sortOrder: i + 1 }))
  const data = { ...form, questions }
  try {
    if (editId.value) { await updatePaper(editId.value, data); ElMessage.success('修改成功') }
    else { await createPaper(data); ElMessage.success('创建成功') }
    dialogVisible.value = false; fetchData()
  } catch (e) { ElMessage.error('操作失败') }
}

async function publish(id) { try { await publishPaper(id); ElMessage.success('已发布'); fetchData() } catch {} }
async function closeExam(id) { try { await closePaper(id); ElMessage.success('已关闭'); fetchData() } catch {} }
async function handleDelete(id) { try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deletePaper(id); fetchData() } catch {} }

onMounted(fetchData)
</script>
