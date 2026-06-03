import request from '@/utils/request'

export function getPapers(params) {
  return request({ url: '/papers', method: 'get', params })
}

export function getPaper(id) {
  return request({ url: `/papers/${id}`, method: 'get' })
}

export function getPaperQuestions(id) {
  return request({ url: `/papers/${id}/questions`, method: 'get' })
}

export function createPaper(data) {
  return request({ url: '/papers', method: 'post', data })
}

export function updatePaper(id, data) {
  return request({ url: `/papers/${id}`, method: 'put', data })
}

export function publishPaper(id) {
  return request({ url: `/papers/${id}/publish`, method: 'put' })
}

export function closePaper(id) {
  return request({ url: `/papers/${id}/close`, method: 'put' })
}

export function deletePaper(id) {
  return request({ url: `/papers/${id}`, method: 'delete' })
}
