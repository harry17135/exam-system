import request from '@/utils/request'

export function getUsers(params) {
  return request({ url: '/users', method: 'get', params })
}

export function getProfile() {
  return request({ url: '/users/profile', method: 'get' })
}

export function updateProfile(data) {
  return request({ url: '/users/profile', method: 'put', data })
}

export function createUser(data) {
  return request({ url: '/users', method: 'post', data })
}

export function updateUser(id, data) {
  return request({ url: `/users/${id}`, method: 'put', data })
}

export function deleteUser(id) {
  return request({ url: `/users/${id}`, method: 'delete' })
}

export function getClasses(params) {
  return request({ url: '/classes', method: 'get', params })
}

export function getAllClasses() {
  return request({ url: '/classes/all', method: 'get' })
}

export function createClass(data) {
  return request({ url: '/classes', method: 'post', data })
}

export function updateClass(id, data) {
  return request({ url: `/classes/${id}`, method: 'put', data })
}

export function deleteClass(id) {
  return request({ url: `/classes/${id}`, method: 'delete' })
}

export function getStatistics() {
  return request({ url: '/statistics/overview', method: 'get' })
}
