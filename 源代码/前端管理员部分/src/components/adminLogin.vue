<template>
  <div>
    <h2 id="title1">RoboAdiviser</h2>
    <h3>基于机器学习的智能基金投顾系统</h3>
    <el-form ref="loginForm" :model="loginForm" label-width="80px">
      <el-form-item label="用户名">
        <el-input v-model="loginForm.username"></el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input type="password" v-model="loginForm.password"></el-input>
      </el-form-item>
      <el-button type="primary" @click="login">登录</el-button>

    </el-form>
  </div>

</template>

<script>
  import {adminLogin} from '../api.js'
  import {updateOwnPassword} from '../api.js'
  import {mapState} from 'vuex'

  export default {
    data () {
      return {
        loginForm: {
          username: '',
          password: ''
        },
        passwordForm: {
          username: '',
          formerPassword: '',
          laterPassword: ''
        },

      }
    },
    methods: {

      login () {

        adminLogin(
          {
            username: this.loginForm.username,
            password: this.loginForm.password
          }
        ).then(res => {
          console.log(res)
          let status = res.data.status
          if (status) {
            this.$message.success('登录成功')

            console.log(res.headers.token)
            // this.$store.commit('set_token', res.headers.token)
            localStorage.setItem('token',res.headers.token)
            setTimeout(() => {
              this.$router.push('/admin/fundList')
            }, 500)
          } else {
            this.$message.error(res.data.result)
          }
          // console.log(res.data)
        })
      },
    },
  }
</script>
<style lang="scss">
</style>
