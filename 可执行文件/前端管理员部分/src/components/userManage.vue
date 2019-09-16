/* eslint-disable */
<template>
  <div>
    <el-form :inline="true" :model="customer" class="demo-form-inline">
      <el-form-item label="用户编号">
        <el-input v-model="customer.id" placeholder=""></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="findCustomerInfo">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="tableData"
      stripe
      style="width: 100%">
      <el-table-column
        prop="CustomerId"
        label="用户编号"
        width="130"
      >
      </el-table-column>
      <el-table-column
        prop="username"
        label="用户名"
        width="130"
      >
      </el-table-column>
      <el-table-column
        prop="email"
        label="邮箱"
        width="130"
      >
      </el-table-column>
      <el-table-column
        prop="emailVaild"
        label="邮箱是否被验证"
        width="130"
      >
      </el-table-column>
      <el-table-column
        prop="balance"
        label="余额"
        width="130"
      >
      </el-table-column>
      <el-table-column
        prop="bonus"
        label="收益"
        width="130"
      >
      </el-table-column>
      </el-table-column>
      <el-table-column
        prop="banned"
        label="权限管理"
        width="130">
        <template slot-scope="scope">
          <el-button type="primary" plain @click="forbidUser(scope.row)">{{scope.row.banned===true?"解禁":"禁用"}}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-button-group>
      <el-button type="primary" plain @click="lastPage">上一页</el-button>
      <el-button type="primary" plain @click="nextPage">下一页</el-button>
    </el-button-group>

  </div>
</template>
<script>
  import {viewCustomerInfo, viewAllCustomers,releaseCustomer,banCustomer} from '../api.js'
  export default {
    data () {
      return {
        tableData: [
          {
            id:"0001",
            username:"customer1",
            email:"customer1@smail.nju.edu.cn",
            emailVaild:"true",
            balance:"10000.0",
            bonus:"100",
            banned:"false"
          },
          {
            id:"0002",
            username:"customer2",
            email:"customer2@smail.nju.edu.cn",
            emailVaild:"true",
            balance:"20000.0",
            bonus:"200",
            banned:"false"
          },
          {
            id:"0003",
            username:"customer3",
            email:"customer3@smail.nju.edu.cn",
            emailVaild:"true",
            balance:"30000.0",
            bonus:"300",
            banned:"false"
          }
        ],
        customer:{
          id:""
        },
        page: {pageNum: 1},


    }
    },

    methods: {
      editFund () {
      },
      deleteFund () {
      },
      addFund(){
      },
      findCustomerInfo(){
      },
      forbidUser(row){
        console.log("here")

        if (row.banned === false) {
          console.log("禁用")
          console.log(row.customerId)
          banCustomer(
            row.customerId
          ).then(res => {
            if (res.data.status) {
              this.$message.success('已禁用')


            } else {
              this.$message.error(res.data.result)
            }

          })
        } else {
          releaseCustomer(
            row.customerId
          ).then(res => {
            if (res.data.status) {
              this.$message.success('已解禁')
            } else {
              this.$message.error(res.data.result)
            }

          })

        }
        viewAllCustomers(this.page.pageNum, 8).then(res => {
          console.log(res)
          if (res.data.status) {
            this.tableData = res.data.result
            console.log(this.tableData)
          }

        })

      },
      nextPage () {
        this.page.pageNum = this.page.pageNum + 1
        viewAllCustomers(this.page.pageNum, 8).then(res => {
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
        viewAllCustomers(this.page.pageNum, 8).then(res => {
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

    },
    mounted () {
      viewAllCustomers(1, 8).then(res => {
        console.log(res)
        if (res.data.status) {
          this.tableData = res.data.result
          console.log(this.tableData)
        }

      })

    },

  }
</script>
<style lang="scss">
  form{
    display:flex;
  }
</style>
