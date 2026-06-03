<template>
  <div class="login-container">
    <div class="login-card">
      <h2 style="text-align:center;margin-bottom:24px;color:#303133">注册账号</h2>
      <el-form :model="form" :rules="rules" ref="formRef" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="realName">
          <el-input v-model="form.realName" placeholder="真实姓名" prefix-icon="UserFilled" />
        </el-form-item>
        <el-form-item prop="role">
          <el-select v-model="form.role" placeholder="选择角色" style="width:100%">
            <el-option label="学生" value="STUDENT" />
            <el-option label="教师" value="TEACHER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" :loading="loading" @click="handleRegister">注 册</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align:center">
        <span style="color:#909399">已有账号？</span>
        <el-link type="primary" @click="$router.push('/login')">去登录</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '', realName: '', role: 'STUDENT' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.register(form)
    ElMessage.success('注册成功')
    router.push('/')
  } catch (e) {
    ElMessage.error(e.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container { height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.login-card { width: 420px; padding: 40px; background: #fff; border-radius: 12px; box-shadow: 0 20px 60px rgba(0,0,0,.15); }
</style>
