import Vue from 'vue'
import Router from 'vue-router'
import adminLogin from '@/components/adminLogin'
import fundList from '@/components/fundList'
import fundDetails from '@/components/fundDetails'
import adminManage from '@/components/adminManage'
import userManage from '@/components/userManage'
import modifyPassword from '@/components/modifyPassword'
import navigation from '@/components/navigation'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'adminLogin',
      component: adminLogin
    },
    {
      path:'/admin',
      name:'navigation',
      component:navigation,
      children:[
        {
          path: '/admin/fundList',
          name: 'fundList',
          component: fundList
        },
        {
          path: '/admin/fundDetails',
          name: 'fundDetails',
          component: fundDetails
        },
        {
          path: '/admin/adminManage',
          name: 'adminManage',
          component: adminManage
        },
        {
          path: '/admin/userManage',
          name: 'userManage',
          component: userManage
        },
        {
          path:'/admin/modifyPassword',
          name:'modifyPassword',
          component:modifyPassword
        },
      ]
    }
  ]
})
