<template>
  <div class="page-card">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索班级" clearable style="width:220px" @change="fetchData" />
      <el-button type="primary" @click="showDialog()">添加班级</el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="className" label="班级名称" />
      <el-table-column prop="grade" label="年级" width="100" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column label="操作" width="240">
        <template #default="{row}">
          <el-button size="small" type="success" @click="showStudents(row)">查看学生</el-button>
          <el-button size="small" @click="showDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="pageNum" :total="total" layout="total,prev,pager,next" style="margin-top:16px;justify-content:flex-end" @current-change="fetchData" />

    <!-- 添加/编辑班级对话框 -->
    <el-dialog :title="editId?'编辑班级':'添加班级'" v-model="dialogVisible" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="班级名称"><el-input v-model="form.className" /></el-form-item>
        <el-form-item label="年级"><el-input v-model="form.grade" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>

    <!-- 查看学生对话框 -->
    <el-dialog :title="studentDialogTitle" v-model="studentDialogVisible" width="700px">
      <el-table :data="classStudents" stripe v-loading="studentLoading" max-height="500">
        <el-table-column prop="username" label="学号" width="130" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column label="状态" width="80">
          <template #default="{row}">
            <el-tag :type="row.status ? 'success' : 'danger'">{{ row.status ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:12px;color:#909399">共 {{ classStudents.length }} 名学生</div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getClasses, createClass, updateClass, deleteClass, getUsers } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([]); const loading = ref(false); const pageNum = ref(1); const total = ref(0)
const keyword = ref(''); const dialogVisible = ref(false); const editId = ref(null)
const form = reactive({ className: '', grade: '', description: '' })

// 学生弹窗
const studentDialogVisible = ref(false)
const studentDialogTitle = ref('')
const classStudents = ref([])
const studentLoading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const res = await getClasses({ pageNum: pageNum.value, pageSize: 10, keyword: keyword.value })
    list.value = res.data?.records || []; total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function showDialog(row) {
  if (row) { editId.value = row.id; form.className = row.className; form.grade = row.grade || ''; form.description = row.description || '' }
  else { editId.value = null; Object.assign(form, { className: '', grade: '', description: '' }) }
  dialogVisible.value = true
}

async function showStudents(row) {
  studentDialogTitle.value = `${row.className} - 学生列表`
  studentDialogVisible.value = true
  studentLoading.value = true
  try {
    const res = await getUsers({ pageSize: 100, role: 'STUDENT' })
    const allStudents = res.data?.records || []
    classStudents.value = allStudents.filter(s => s.classId === row.id)
  } finally { studentLoading.value = false }
}

async function handleSave() {
  try {
    if (editId.value) { await updateClass(editId.value, form); ElMessage.success('修改成功') }
    else { await createClass(form); ElMessage.success('创建成功') }
    dialogVisible.value = false; fetchData()
  } catch (e) { ElMessage.error('操作失败') }
}

async function handleDelete(id) {
  try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteClass(id); fetchData() } catch {}
}

onMounted(fetchData)
</script>
