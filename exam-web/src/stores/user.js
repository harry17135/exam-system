import { defineStore } from 'pinia'
import { getToken, setToken, removeToken, getUser, setUser } from '@/utils/auth'
import { login as loginApi, register as registerApi } from '@/api/auth'
import { getProfile, updateProfile } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    user: getUser()
  }),
  getters: {
    isLoggedIn: state => !!state.token,
    role: state => state.user?.role || ''
  },
  actions: {
    async login(credentials) {
      const res = await loginApi(credentials)
      this.token = res.data.token
      this.user = { userId: res.data.userId, username: res.data.username, realName: res.data.realName, role: res.data.role }
      setToken(res.data.token)
      setUser(this.user)
      return res.data
    },
    async register(data) {
      const res = await registerApi(data)
      this.token = res.data.token
      this.user = { userId: res.data.userId, username: res.data.username, realName: res.data.realName, role: res.data.role }
      setToken(res.data.token)
      setUser(this.user)
      return res.data
    },
    async fetchProfile() {
      const res = await getProfile()
      if (res.code === 200) {
        this.user = { ...this.user, ...res.data }
        setUser(this.user)
      }
    },
    async updateProfile(data) {
      await updateProfile(data)
      if (this.user) {
        this.user = { ...this.user, ...data }
        setUser(this.user)
      }
    },
    logout() {
      this.token = null
      this.user = null
      removeToken()
    }
  }
})
