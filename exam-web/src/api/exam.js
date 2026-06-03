import request from '@/utils/request'

// ===== 学生端 =====
export function startExam(data) {
  return request({ url: '/exam/start', method: 'post', data })
}

export function submitExam(data) {
  return request({ url: '/exam/submit', method: 'post', data })
}

export function getMyRecords(params) {
  return request({ url: '/exam/records', method: 'get', params })
}

export function getRecordDetail(id) {
  return request({ url: `/exam/record/${id}`, method: 'get' })
}

export function getAnswerDetails(recordId) {
  return request({ url: `/exam/record/${recordId}/answers`, method: 'get' })
}

export function gradeEssay(recordId, questionId, score) {
  return request({ url: `/exam/grade/${recordId}/${questionId}`, method: 'post', params: { score } })
}

// ===== 考试安排 =====
export function getArrangements(params) {
  return request({ url: '/arrangements', method: 'get', params })
}

export function createArrangement(data) {
  return request({ url: '/arrangements', method: 'post', data })
}

export function updateArrangement(id, data) {
  return request({ url: `/arrangements/${id}`, method: 'put', data })
}

export function deleteArrangement(id) {
  return request({ url: `/arrangements/${id}`, method: 'delete' })
}

// ===== 教师端 =====
export function getTeacherOverview() {
  return request({ url: '/teacher/overview', method: 'get' })
}

export function getTeacherExamRecords(params) {
  return request({ url: '/teacher/exam-records', method: 'get', params })
}

export function getTeacherRecordDetail(recordId) {
  return request({ url: `/teacher/exam-record/${recordId}/details`, method: 'get' })
}

export function getTeacherClassStatistics() {
  return request({ url: '/teacher/class-statistics', method: 'get' })
}

export function getTeacherClasses() {
  return request({ url: '/teacher/classes', method: 'get' })
}
