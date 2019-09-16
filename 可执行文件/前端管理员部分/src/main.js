// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui'
import axios from 'axios'
import 'element-ui/lib/theme-chalk/index.css'
import promise from 'es6-promise'
import store from './store/index'
import vueResource from 'vue-resource'

Vue.config.productionTip = false
Vue.use(ElementUI, {size: 'small', zIndex: 3000})
Vue.use(vueResource)
/* eslint-disable no-new */

axios.default.baseURL = 'http://jh.dwxh.xyz:8080'
//axios.default.headers.post['Content-Type'] = 'aplication/json'
axios.defaults.withCredentials = true
axios.defaults.headers.token=localStorage.getItem('token')
Vue.prototype.$axios = axios

new Vue({
  el: '#app',
  render: h => h(App),
  router,
  store,
  components: {App},
  template: '<App/>'
})

//添加请求拦截器
axios.interceptors.request.use(config => {
  console.log('发送请求')
  //在发送请求之前做什么
  //判断是否存在token,如果存在将每个页面header都添加token
  let token = localStorage.getItem('token')
  // if (store.state.token) {
    console.log('把token存下来了')
    config.headers.token = token
// //
//   }
  return config
// }, error => {
//   console.log('确实没有token')
//   //对请求错误做些什么
//   return
})
//
// axios.interceptors.response.use(
//   response => {
//     return response
//   },
//   error => {
//     if (error.response) {
//       switch (error.response.status) {
//         case 401:
//           this.$store.commit(del_token)
//           router.replace({
//             path: '/',
//             query: {redirect: router.curentRoute.fullPath}
//           })
//       }
//     }
//     return
//   })
