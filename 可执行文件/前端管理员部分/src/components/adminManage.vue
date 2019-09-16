/*eslint-disable*/
<template>
  <div>
    <el-form :inline="true" :model="form" class="demo-form-inline">
      <el-form-item label="管理员账号">
        <el-input v-model="form.account" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" placeholder=""></el-input>
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="form.banned">禁用</el-checkbox>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="add">添加</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="tableData"
      stripe
      style="width: 100%">
      <el-table-column
        prop="username"
        label="管理员账号"
        width="300">
      </el-table-column>
      <el-table-column
        prop="password"
        label="密码"
        width="300">
      </el-table-column>
      <el-table-column
        prop="banned"
        label="权限管理"
        width="300">
        <template slot-scope="scope">
          <el-button type="primary" plain @click="forbidAdmin(scope.row)">{{scope.row.banned===true?"解禁":"禁用"}}</el-button>
        </template>
      </el-table-column>
      <el-table-column
        label="修改密码"
        width="300">
        <el-button type="primary" icon="el-icon-edit" @click="modifyPass"></el-button>
      </el-table-column>
      </el-table-column>
    </el-table>
    <el-button-group>
      <el-button type="primary" plain @click="lastPage">上一页</el-button>
      <el-button type="primary" plain @click="nextPage">下一页</el-button>
    </el-button-group>

  </div>
</template>
<script>
  import {addAdmin, viewAllManagers,ban,unban,updatePassword} from '../api.js'

  export default {
    data () {
      return {
        tableData: [
          // {
          //   adminAccount: 'admin1',
          //   adminPassword: '123',
          //
          // },
          // {
          //   adminAccount: 'admin2',
          //   adminPassword: '123'
          // },
          // {
          //   adminAccount: 'admin3',
          //   adminPassword: '123'
          // }
        ],
        form: {
          account: '',
          password: '',
          banned: false
        },
        page: {pageNum: 1},

      }

    },

    mounted () {
      viewAllManagers(1, 3).then(res => {
        console.log(res)
        if (res.data.status) {
          this.tableData = res.data.result
          console.log(this.tableData)
        }

      })

    },
    methods: {
      forbidAdmin (row) {
        console.log("here")

        if (row.banned === false) {
          console.log("禁用")
          console.log(row.managerId)
          ban(
            row.managerId
          ).then(res => {
            if (res.data.status) {
              this.$message.success('已禁用')


            } else {
              this.$message.error(res.data.result)
            }

          })
        } else {
          unban(
            row.managerId
          ).then(res => {
            if (res.data.status) {
              this.$message.success('已解禁')
            } else {
              this.$message.error(res.data.result)
            }

          })

        }
        viewAllManagers(this.page.pageNum, 3).then(res => {
          console.log(res)
          if (res.data.status) {
            this.tableData = res.data.result
            console.log(this.tableData)
          }

        })
      },

      add () {
        addAdmin({
          username: this.form.account,
          password: this.form.password,
          banned: this.form.banned
        }).then(res => {
          console.log(res.data)
          let status = res.data.status
          if (status) {
            this.$message.success('已添加')
            setTimeout(() => {
              this.$router.push('/admin/adminManage')
            }, 500)
          } else {
            this.$message.error(res.data.result)
          }

        })

      },
      modifyPass () {

      },
      nextPage () {
        this.page.pageNum = this.page.pageNum + 1
        viewAllManagers(this.page.pageNum, 3).then(res => {
          console.log(res)
          let status = res.data.status

          if (status) {
            this.tableData = res.data.result
            //for (let i = 0; i < fundList.length; i++) {
            //console.log("be ready to push")
            //this.tableData.push(fundList)
            //console.log("table")
            //console.log(this.tableData[0]['fundId'])

          } else {
            this.$message.error(res.data.result)
          }
        })
      },
      lastPage () {
        if (this.page.pageNum > 1) {
          this.page.pageNum = this.page.pageNum - 1
        } else {
          return null
        }
        viewAllManagers(this.page.pageNum, 3).then(res => {
          console.log(res)
          let status = res.data.status

          if (status) {
            this.tableData = res.data.result
            //for (let i = 0; i < fundList.length; i++) {
            //console.log("be ready to push")
            //this.tableData.push(fundList)
            //console.log("table")
            //console.log(this.tableData[0]['fundId'])

          } else {
            this.$message.error(res.data.result)
          }
        })

      }

    }
    ,

    components: {}
    ,

  }
</script>
<style lang="scss">

</style>
