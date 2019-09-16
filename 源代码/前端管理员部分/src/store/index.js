import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const state = {
  token: localStorage.getItem('token') ? localStorage.getItem('token') : ''
}
const store = new Vuex.Store({
  state,
  mutations: {
    set_token (state, token) {
      state.token = token
      localStorage.setItem('token', token.token)
    },
    del_token (state) {
      state.token = ''
      localStorage.removeItem('token')
    }
  }
})
export default store
