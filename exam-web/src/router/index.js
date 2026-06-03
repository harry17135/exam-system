import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/RegisterView.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/student/dashboard',
    children: [
      // 学生端
      { path: 'student/dashboard', component: () => import('@/views/student/DashboardView.vue'), meta: { title: '学生首页', role: 'STUDENT' } },
      { path: 'student/exam/:id', component: () => import('@/views/student/ExamView.vue'), meta: { title: '在线考试', role: 'STUDENT' } },
      { path: 'student/records', component: () => import('@/views/student/RecordsView.vue'), meta: { title: '考试记录', role: 'STUDENT' } },
      // 教师端
      { path: 'teacher/dashboard', component: () => import('@/views/teacher/DashboardView.vue'), meta: { title: '教师首页', role: 'TEACHER' } },
      { path: 'teacher/questions', component: () => import('@/views/teacher/QuestionsView.vue'), meta: { title: '题库管理', role: 'TEACHER' } },
      { path: 'teacher/papers', component: () => import('@/views/teacher/PapersView.vue'), meta: { title: '试卷管理', role: 'TEACHER' } },
      { path: 'teacher/arrangements', component: () => import('@/views/teacher/ArrangementsView.vue'), meta: { title: '考试安排', role: 'TEACHER' } },
      { path: 'teacher/grading', component: () => import('@/views/teacher/GradingView.vue'), meta: { title: '批阅试卷', role: 'TEACHER' } },
      { path: 'teacher/grades', component: () => import('@/views/teacher/GradesView.vue'), meta: { title: '成绩查询', role: 'TEACHER' } },
      // 管理员端
      { path: 'admin/dashboard', component: () => import('@/views/admin/DashboardView.vue'), meta: { title: '管理后台', role: 'ADMIN' } },
      { path: 'admin/users', component: () => import('@/views/admin/UsersView.vue'), meta: { title: '用户管理', role: 'ADMIN' } },
      { path: 'admin/classes', component: () => import('@/views/admin/ClassesView.vue'), meta: { title: '班级管理', role: 'ADMIN' } },
      // 个人中心
      { path: 'profile', component: () => import('@/views/login/ProfileView.vue'), meta: { title: '个人中心' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const roleRoutes = {
  STUDENT: '/student/dashboard',
  TEACHER: '/teacher/dashboard',
  ADMIN: '/admin/dashboard'
}

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 在线考试系统` : '在线考试系统'
  const token = getToken()

  if (to.path === '/login' || to.path === '/register') {
    if (token) return next('/')
    return next()
  }

  if (!token) return next('/login')

  // 简单角色路由守卫
  const userStr = localStorage.getItem('exam_user')
  if (userStr) {
    const user = JSON.parse(userStr)
    if (to.meta.role && to.meta.role !== user.role && user.role !== 'ADMIN') {
      return next(roleRoutes[user.role] || '/')
    }
  }

  next()
})

export default router
