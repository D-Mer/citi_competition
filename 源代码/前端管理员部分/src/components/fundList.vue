/* eslint-disable */
<template>
  <div>
    <el-form :inline="true" :model="newFundInfo" class="demo-form-inline">
      <el-form-item label="代码">
        <el-input v-model="newFundInfo.fundCode" placeholder=""></el-input>
      </el-form-item>
      </el-form-item>
      <el-form-item label="最低申购金额">
        <el-input v-model="newFundInfo.minPurchaseAmount" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="最低持有份额">
        <el-input v-model="newFundInfo.minPart" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="详情链接">
        <el-input v-model="newFundInfo.url" placeholder=""></el-input>
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
        prop="fundCode"
        label="代码"
        width="120"
      >
      </el-table-column>
      <el-table-column
        prop="fundName"
        label="基金名称"
        width="120"
      >
      </el-table-column>
      <el-table-column
        prop="type"
        label="基金类型"
        width="120"
      >
      </el-table-column>
      <el-table-column
        prop="investType"
        label="投资类型"
        width="120"
      >
      </el-table-column>
      <el-table-column
        prop="manager"
        label="基金经理"
        width="120"
      >
      </el-table-column>
      <el-table-column
        prop="managerBank"
        label="基金管理银行"
        width="120"
      >
      </el-table-column>
      <el-table-column
        prop="minPurchaseAmount"
        label="最低申购金额"
        width="120"
      >
      </el-table-column>
      <el-table-column
        prop="minPart"
        label="最低持有份额"
        width="120"
      >
      </el-table-column>
      <el-table-column
        label="详情链接"
        width="200"
      >
        <template slot-scope="scope">
          <router-link to="/">{{ scope.row.url}}</router-link>
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
  import {addFund} from '../api.js'
  import {viewAllFunds} from '../api.js'

  export default {
    data () {
      return {
        tableData: [],
        newFundInfo: {
          fundCode: '',
          minPurchaseAmount: '',
          minPart: '',
          url: ''
        },
        page: {pageNum: 1}
      }
    },
    mounted () {
      viewAllFunds(this.page.pageNum, 8).then(res => {
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
    methods: {
      getDetails () {
      },
      add () {

        console.log(this.newFundInfo)
        //todo() 数据传给后端
        addFund(
          {
            fundCode: this.newFundInfo.fundCode,
            minPurchaseAmount: this.newFundInfo.minPurchaseAmount,
            minPart: this.newFundInfo.minPart,
            url: this.newFundInfo.url
          }
        ).then(res => {
          //console.log(res.data)
          let status = res.data.status
          if (status) {
            console.log(res.data)


          } else {
            this.$message.error(res.data.result)
          }
        })
        let emptyNewFundInfo = {
          fundCode: '',
          minPurchaseAmount: '',
          minPart: '',
          url: ''
        }

        //todo() 数据传给后端

        this.newFundInfo = emptyNewFundInfo
      },
      nextPage () {
        this.page.pageNum = this.page.pageNum + 1
        viewAllFunds(this.page.pageNum, 8).then(res => {
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
        viewAllFunds(this.page.pageNum, 8).then(res => {
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
  }
</script>
<style lang="scss">
  form {
    display: flex;
  }
</style>
