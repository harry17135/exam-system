import request from '@/utils/request'

export function getQuestions(params) {
  return request({ url: '/questions', method: 'get', params })
}

export function getQuestion(id) {
  return request({ url: `/questions/${id}`, method: 'get' })
}

export function createQuestion(data) {
  return request({ url: '/questions', method: 'post', data })
}

export function updateQuestion(id, data) {
  return request({ url: `/questions/${id}`, method: 'put', data })
}

export function deleteQuestion(id) {
  return request({ url: `/questions/${id}`, method: 'delete' })
}
