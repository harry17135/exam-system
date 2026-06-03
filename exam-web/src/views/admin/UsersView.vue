<template>
  <div class="page-card">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索用户名/姓名" clearable style="width:220px" @change="fetchData" />
      <el-select v-model="roleFilter" placeholder="角色" clearable style="width:120px" @change="fetchData">
        <el-option label="学生" value="STUDENT" /><el-option label="教师" value="TEACHER" /><el-option label="管理员" value="ADMIN" />
      </el-select>
      <el-button type="primary" @click="showDialog()">添加用户</el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="姓名" />
      <el-table-column prop="role" label="角色" width="80">
        <template #default="{row}"><el-tag :type="row.role==='ADMIN'?'danger':row.role==='TEACHER'?'warning':''">{{ roleMap[row.role] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="status" label="状态" width="70">
        <template #default="{row}"><el-tag :type="row.status?'success':'danger'">{{ row.status?'正常':'禁用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{row}">
          <el-button size="small" @click="showDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="pageNum" :total="total" layout="total,prev,pager,next" style="margin-top:16px;justify-content:flex-end" @current-change="fetchData" />

    <el-dialog :title="editId?'编辑用户':'添加用户'" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="!!editId" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" :placeholder="editId?'不修改请留空':'请输入密码'" show-password /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="角色"><el-select v-model="form.role"><el-option label="学生" value="STUDENT" /><el-option label="教师" value="TEACHER" /><el-option label="管理员" value="ADMIN" /></el-select></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getUsers, createUser, updateUser, deleteUser } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const roleMap = { STUDENT: '学生', TEACHER: '教师', ADMIN: '管理员' }
const list = ref([]); const loading = ref(false); const pageNum = ref(1); const total = ref(0)
const keyword = ref(''); const roleFilter = ref('')
const dialogVisible = ref(false); const editId = ref(null)
const form = reactive({ username: '', password: '', realName: '', role: 'STUDENT', phone: '', email: '' })

async function fetchData() {
  loading.value = true
  try {
    const res = await getUsers({ pageNum: pageNum.value, pageSize: 10, keyword: keyword.value, role: roleFilter.value })
    list.value = res.data?.records || []; total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function showDialog(row) {
  if (row) {
    editId.value = row.id
    form.username = row.username; form.password = ''; form.realName = row.realName
    form.role = row.role; form.phone = row.phone || ''; form.email = row.email || ''
  } else { editId.value = null; Object.assign(form, { username: '', password: '', realName: '', role: 'STUDENT', phone: '', email: '' }) }
  dialogVisible.value = true
}

async function handleSave() {
  try {
    if (editId.value) { await updateUser(editId.value, { ...form, password: form.password || undefined }); ElMessage.success('修改成功') }
    else { await createUser(form); ElMessage.success('创建成功') }
    dialogVisible.value = false; fetchData()
  } catch (e) { ElMessage.error(e.message || '操作失败') }
}

async function handleDelete(id) {
  try { await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }); await deleteUser(id); fetchData() } catch {}
}

onMounted(fetchData)
</script>
