<template>
  <div class="page-card" style="max-width:600px">
    <h3 style="margin-bottom:20px">个人中心</h3>
    <el-form :model="form" label-width="80px" size="large">
      <el-form-item label="用户名"><el-input v-model="form.username" disabled /></el-form-item>
      <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
      <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
      <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSave">保存修改</el-button>
        <el-button type="warning" @click="showPwdDialog">修改密码</el-button>
      </el-form-item>
    </el-form>

    <!-- 修改密码弹窗 -->
    <el-dialog title="修改密码" v-model="pwdDialogVisible" width="420px">
      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="handleChangePwd">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', realName: '', phone: '', email: '' })

onMounted(async () => {
  await userStore.fetchProfile()
  const u = userStore.user || {}
  Object.assign(form, { username: u.username || '', realName: u.realName || '', phone: u.phone || '', email: u.email || '' })
})

async function handleSave() {
  loading.value = true
  try {
    await userStore.updateProfile({ realName: form.realName, phone: form.phone, email: form.email })
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    loading.value = false
  }
}

// 修改密码
const pwdDialogVisible = ref(false)
const pwdLoading = ref(false)
const pwdFormRef = ref()
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const validateConfirm = (_rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的新密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

function showPwdDialog() {
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
  pwdDialogVisible.value = true
}

async function handleChangePwd() {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  pwdLoading.value = true
  try {
    await request({ url: '/users/change-password', method: 'put', data: { oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword } })
    ElMessage.success('密码修改成功，请重新登录')
    pwdDialogVisible.value = false
    userStore.logout()
    setTimeout(() => window.location.href = '/login', 1000)
  } catch (e) {
    ElMessage.error(e.message || '修改失败')
  } finally {
    pwdLoading.value = false
  }
}
</script>
