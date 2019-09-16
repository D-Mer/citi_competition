// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './pages/App'
import router from './router/router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'
import VueAxios from 'vue-axios'

Vue.use(VueAxios, axios)
Vue.prototype.$axios = axios

Vue.use(ElementUI)

Vue.config.productionTip = false
// Vue.use(router)
/* eslint-disable no-new */
new Vue({
  el:'#app',
  router,
  components: { App },
  template: '<App/>'
})

// http request 请求拦截器
axios.interceptors.request.use(config => {
	// 在发送请求之前做些什么
	let pathname = location.pathname;
	if(localStorage.getItem('token')){
		if(pathname != '/auth'){
			config.headers.common['token'] = localStorage.getItem('token');
		}
	} else {
		router.push('/auth');
	} 
	return config;
}, error => {
	// 对请求错误做些什么
	console.log(error);
	router.push('/auth');
});

//路由拦截
// router.beforeEach((to,from,next)=>{
// 	if(to.meta.requireAuth){
// 		if(localStorage.getItem('token')){
// 			next();
// 		}
// 		else {
// 			next({
// 				path:'/auth/login',
// 				query:{redirect:to.fullPath} 
// 			})
// 		}
// 	}
// })
