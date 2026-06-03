<template>
  <div class="page-card">
    <div class="search-bar">
      <el-button type="primary" @click="showDialog()">新建考试安排</el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="examName" label="考试名称" />
      <el-table-column label="试卷ID" prop="paperId" width="80" />
      <el-table-column label="班级ID" prop="classId" width="80" />
      <el-table-column prop="startTime" label="开始时间" width="170" />
      <el-table-column prop="endTime" label="结束时间" width="170" />
      <el-table-column label="状态" width="100">
        <template #default="{row}">
          <el-tag v-if="row.status==='PENDING'" type="info">未开始</el-tag>
          <el-tag v-else-if="row.status==='ONGOING'" type="warning">进行中</el-tag>
          <el-tag v-else type="success">已结束</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{row}">
          <el-button size="small" @click="showDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="pageNum" :total="total" layout="total,prev,pager,next" style="margin-top:16px;justify-content:flex-end" @current-change="fetchData" />

    <el-dialog :title="editId?'编辑安排':'新建安排'" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="考试名称"><el-input v-model="form.examName" /></el-form-item>
        <el-form-item label="试卷ID"><el-input-number v-model="form.paperId" :min="1" /></el-form-item>
        <el-form-item label="班级ID"><el-input-number v-model="form.classId" :min="1" /></el-form-item>
        <el-form-item label="开始时间"><el-input v-model="form.startTime" placeholder="yyyy-MM-dd HH:mm:ss" /></el-form-item>
        <el-form-item label="结束时间"><el-input v-model="form.endTime" placeholder="yyyy-MM-dd HH:mm:ss" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getArrangements, createArrangement, updateArrangement, deleteArrangement } from '@/api/exam'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([]); const loading = ref(false); const pageNum = ref(1); const total = ref(0)
const dialogVisible = ref(false); const editId = ref(null)
const form = reactive({ examName: '', paperId: null, classId: null, startTime: '', endTime: '' })

async function fetchData() {
  loading.value = true
  try {
    const res = await getArrangements({ pageNum: pageNum.value, pageSize: 10 })
    list.value = res.data?.records || []; total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function showDialog(row) {
  if (row) {
    editId.value = row.id
    form.examName = row.examName; form.paperId = row.paperId; form.classId = row.classId
    form.startTime = row.startTime; form.endTime = row.endTime
  } else { editId.value = null; Object.assign(form, { examName: '', paperId: null, classId: null, startTime: '', endTime: '' }) }
  dialogVisible.value = true
}

async function handleSave() {
  try {
    if (editId.value) { await updateArrangement(editId.value, form); ElMessage.success('修改成功') }
    else { await createArrangement(form); ElMessage.success('创建成功') }
    dialogVisible.value = false; fetchData()
  } catch (e) { ElMessage.error('操作失败') }
}

async function handleDelete(id) {
  try { await ElMessageBox.confirm('确定删除？','提示',{type:'warning'}); await deleteArrangement(id); fetchData() } catch {}
}

onMounted(fetchData)
</script>
