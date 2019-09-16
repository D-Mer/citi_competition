import Vue from 'vue'
import Router from 'vue-router'
// import Home from '@/components/basic/Home'

const Home = r => require.ensure([], () => r(require('../pages/app/home/Home')), 'Home')
const AllFund = r => require.ensure([], () => r(require('../pages/app/allfund/AllFund')), 'AllFund')
const Details = r => require.ensure([], () => r(require('../pages/app/details/Details')), 'Details')
const CombineDetails = r => require.ensure([], () => r(require('../pages/app/combinedetails/CombineDetails')), 'CombineDetails')
const Questionaire = r => require.ensure([], () => r(require('../pages/app/questionaire/Questionaire')), 'Questionaire')
const Mycompose = r => require.ensure([], () => r(require('../pages/app/mycompose/Mycompose')), 'Mycompose')
const MyComposeList = r => require.ensure([], () => r(require('../pages/app/mycompose/MyComposeList')), 'MyComposeList')
const Userinfo = r => require.ensure([], () => r(require('../pages/app/userinfo/Userinfo')), 'Userinfo')
const Charge = r => require.ensure([], () => r(require('../pages/app/charge/Charge')), 'Charge')
const Verify = r => require.ensure([], () => r(require('../pages/app/userinfo/Verify')), 'Verify')
//const Login = r => require.ensure([], () => r(require('../pages/auth/Login')), 'Login')
//const Register = r => require.ensure([], () => r(require('../pages/auth/Register')), 'Register')
 //const Login = r => require.ensure([], () => r(require('../pages/auth/Login')), 'Login')
// const Register = r => require.ensure([], () => r(require('../pages/auth/Register')), 'Register')
const Auth = r => require.ensure([], () => r(require('../pages/auth/Auth')), 'Auth')
const Head = r => require.ensure([], () => r(require('../pages/app/Head')), 'Head')
const App = r => require.ensure([], () => r(require('../pages/App')), 'App')

//For Test
const HomeTest = r => require.ensure([], () => r(require('../pages/app/home/HomeTest')), 'HomeTest')


Vue.use(Router)

// 注意：
//1. RouterView 显示的内容一律定义为子组件
//2. 根组件为App, App为空白组件，里面只包含一个RouterView标签
//3. App下细分两个组件，分别是认证组件（Auth）【用于登陆注册】和应用组件（Head）【具体功能】
//4.Auth包含子组件login ，register ； Head包含子组件若干（userifo\home\allfund  etc.）
export default new Router({
  mode:'history',
  routes: [
    {
      path:'/',
      component: App,
      children:[
        {
          path:'',
          redirect:'/auth'
        }, 
        {
          path: 'app',
          name: 'Head',
          component: Head,
          children:[
            // {
            //   path: '',
            //   redirect:'home'
            // },
            {
              path: '',
              redirect:'home'
            },
            {
              path: 'userinfo',
              name: 'Userinfo',
              component: Userinfo
            },
            {
              path: 'home',
              name: 'Home',
              component: Home
            },
            {
              path: 'hometest',
              name: 'HomeTest',
              component: HomeTest
            },
            {
              path: 'allfund',
              name: 'AllFund',
              component: AllFund
            },
            {
              path: 'combinedetails',
              name: 'CombineDetails',
              component: CombineDetails
            },
            {
              path: 'details',
              name: 'Details',
              component: Details
            },
            {
              path: 'mycompose',
              name: 'Mycompose',
              component: Mycompose
            },
            {
              path: 'mycomposelist',
              name: 'MyComposeList',
              component: MyComposeList
            },
            {
              path: 'questionaire',
              name: 'Questionaire',
              component: Questionaire
            },
            {
              path: 'charge',
              name: 'Charge',
              component: Charge
            },
            {
              path: 'verify',
              name: 'Verify',
              component: Verify
            }
          ]
        },
        {
          path: 'auth',
          name: 'Auth',
          component: Auth
        }
      ]
    }
  ]
})
