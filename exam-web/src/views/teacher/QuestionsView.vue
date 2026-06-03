<template>
  <div class="page-card">
    <div class="search-bar">
      <el-input v-model="search.keyword" placeholder="搜索题目" clearable style="width:220px" @change="fetchData" />
      <el-select v-model="search.type" placeholder="试题类型" clearable style="width:130px" @change="fetchData">
        <el-option label="单选题" value="SINGLE" /><el-option label="多选题" value="MULTI" />
        <el-option label="判断题" value="JUDGE" /><el-option label="简答题" value="ESSAY" />
      </el-select>
      <el-select v-model="search.difficulty" placeholder="难度" clearable style="width:120px" @change="fetchData">
        <el-option label="简单" :value="1" /><el-option label="较易" :value="2" /><el-option label="中等" :value="3" /><el-option label="较难" :value="4" /><el-option label="困难" :value="5" />
      </el-select>
      <el-button type="primary" @click="showDialog()">添加试题</el-button>
    </div>

    <el-table :data="list" stripe v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="题目" show-overflow-tooltip />
      <el-table-column label="类型" width="80"><template #default="{row}">{{ typeMap[row.type] }}</template></el-table-column>
      <el-table-column prop="difficulty" label="难度" width="70" />
      <el-table-column prop="score" label="分值" width="60" />
      <el-table-column label="操作" width="150">
        <template #default="{row}">
          <el-button size="small" @click="showDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="pageNum" :total="total" layout="total,prev,pager,next" style="margin-top:16px;justify-content:flex-end" @current-change="fetchData" />

    <!-- 添加/编辑对话框 -->
    <el-dialog :title="editId ? '编辑试题' : '添加试题'" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form :model="form" label-width="80px">
        <el-form-item label="类型"><el-select v-model="form.type"><el-option label="单选题" value="SINGLE" /><el-option label="多选题" value="MULTI" /><el-option label="判断题" value="JUDGE" /><el-option label="简答题" value="ESSAY" /></el-select></el-form-item>
        <el-form-item label="题目"><el-input v-model="form.title" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="选项" v-if="form.type !== 'ESSAY'">
          <div v-for="(opt, i) in form.optionsArr" :key="i" style="display:flex;gap:8px;margin-bottom:4px">
            <el-input v-model="opt.label" placeholder="选项" style="width:60px" /><el-input v-model="opt.content" placeholder="内容" />
          </div>
          <el-button size="small" @click="form.optionsArr.push({label:'',content:''})">+ 添加选项</el-button>
        </el-form-item>
        <el-form-item label="答案">
          <el-input v-model="form.answer" :placeholder="form.type==='MULTI'?'如 AB':'如 A'" />
        </el-form-item>
        <el-form-item label="解析"><el-input v-model="form.analysis" type="textarea" /></el-form-item>
        <el-form-item label="分值"><el-input-number v-model="form.score" :min="1" :max="50" /></el-form-item>
        <el-form-item label="难度"><el-rate v-model="form.difficulty" :max="5" show-score style="margin-top:6px" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getQuestions, createQuestion, updateQuestion, deleteQuestion } from '@/api/question'
import { ElMessage, ElMessageBox } from 'element-plus'

const typeMap = { SINGLE: '单选', MULTI: '多选', JUDGE: '判断', ESSAY: '简答' }
const list = ref([]); const loading = ref(false); const pageNum = ref(1); const total = ref(0)
const dialogVisible = ref(false); const editId = ref(null)
const search = reactive({ keyword: '', type: '', difficulty: null })
const form = reactive({ type: 'SINGLE', title: '', answer: '', analysis: '', score: 5, difficulty: 3, optionsArr: [] })

async function fetchData() {
  loading.value = true
  try {
    const res = await getQuestions({ pageNum: pageNum.value, pageSize: 10, ...search })
    list.value = res.data?.records || []; total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function showDialog(row) {
  if (row) {
    editId.value = row.id
    form.type = row.type; form.title = row.title; form.answer = row.answer; form.analysis = row.analysis || ''
    form.score = row.score; form.difficulty = row.difficulty || 3
    try { form.optionsArr = JSON.parse(row.options || '[]') } catch { form.optionsArr = [] }
  } else {
    editId.value = null; resetForm()
  }
  dialogVisible.value = true
}

function resetForm() {
  editId.value = null
  Object.assign(form, { type: 'SINGLE', title: '', answer: '', analysis: '', score: 5, difficulty: 3, optionsArr: [] })
}

async function handleSave() {
  const data = {
    type: form.type, title: form.title, answer: form.answer,
    analysis: form.analysis, score: form.score, difficulty: form.difficulty,
    options: form.type !== 'ESSAY' ? JSON.stringify(form.optionsArr.filter(o => o.label)) : null
  }
  try {
    if (editId.value) { await updateQuestion(editId.value, data); ElMessage.success('修改成功') }
    else { await createQuestion(data); ElMessage.success('添加成功') }
    dialogVisible.value = false; fetchData()
  } catch (e) { ElMessage.error('操作失败') }
}

async function handleDelete(id) {
  try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteQuestion(id); ElMessage.success('已删除'); fetchData() } catch {}
}

onMounted(fetchData)
</script>
