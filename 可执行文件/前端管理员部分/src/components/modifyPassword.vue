<template>
  <div>
    <el-form erf="passwordForm" :model="passwordForm" label-with="80px">
      <el-form-item label="用户名">
        <el-input v-model="passwordForm.username"></el-input>
      </el-form-item>
      <el-form-item label="原密码">
        <el-input v-model="passwordForm.formerPassword"></el-input>
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="passwordForm.laterPassword"></el-input>
      </el-form-item>
      <el-button type="primary" @click="modifyPassword">提交</el-button>
    </el-form>
  </div>
</template>

<script>
import {updateOwnPassword} from '../api.js'
  export default {
    data(){
      return{
        passwordForm: {
          username: '',
          formerPassword: '',
          laterPassword: ''
        }
      }
    },
    methods:{
      modifyPassword () {
        updateOwnPassword({
          username: this.passwordForm.username,
          formerPassword: this.passwordForm.formerPassword,
          laterPassword: this.passwordForm.laterPassword
        }).then(res=>{
          console.log(res.data)
        })

      },

    }
  }
</script>

<style scoped>

</style>
