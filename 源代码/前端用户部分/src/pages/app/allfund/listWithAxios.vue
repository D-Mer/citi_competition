<template>
    <div class="block">
      <div class="table-card">
        <el-table
        :data="tableData"
        border
        stripe
        style="width: 100%"
        height="500px">
        <el-table-column
          fixed
          :prop="listprops[0].listprop"
          :label="listprops[0].listlabel"
          width="150">
        </el-table-column>
        <el-table-column 
          v-for="(item, index) in listprops.slice(1)" :key="index" 
          :prop="item.listprop"
          :label="item.listlabel"
          width="125">
        </el-table-column>
        <el-table-column
          fixed="right"
          label="操作"
          width="100">
          <template slot-scope="scope">
            <el-button @click="handleClick(scope.row)" type="text" size="small">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      </div>
      <div class='list2 pagin'>
        <el-pagination
          background 
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange" 
          :current-page.sync="currentPage" 
          :page-size="pageSize"
          layout="prev, pager, next, jumper"
          :total="totalLogs"
          >
        </el-pagination>
      </div>
    </div>
</template>

<script>
  export default {
    props:["url","listprops","pageSize"],
    // listprops:[
    // {
    //     listprop:'name',
    //     listlabel:'名称'
    // },
    //     {
    //         listprop:'id',
    //         listlabel:'编号'
    //     },
    // ]
    //url: "http://172.19.240.254:8080/fund/viewAllFunds"
    // <listWithAxios
    //     :listprops="listprops" 
    //     :url="reqUrl">
    // </listWithAxios>
    methods: {
      handleClick(row) {
        // alert(row.fundCode);
        console.log(row.fundId);
		this.$router.push({
			name:"Details",
			params:{
				fundId : row.fundId
			}
		})
      },
      handleSizeChange(val) {
        // console.log(`每页 ${val} 条`);
      },
      handleCurrentChange(val) {
        // console.log(`当前页: ${val}`);
        //向后台发起请求，数据存进 tableData
        this.getData();
      },
      getData(){
          this.$axios.get(this.url,{
              params:{
                  pageNum:this.currentPage,
                  pageSize:this.pageSize
              },
              // headers:{
              //     'token':'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiNTc5Iiwi5raI6LS56ICFIl19.WOVw5iHNnwVuBh2lTw1ibfSXoaCvQscBR35jMVI1FfM'
              // }
          }).then((result) => {
              // console.log(result.data.result);
              this.tableData = result.data.result;
			  localStorage.setItem('currentPage',this.currentPage);
          }).catch((err) => {
              console.log("Error occured in listWithAxios!");
          });
      }
    },

    data() {
      return {
          currentPage: 1,
          totalLogs: 6811,
          tableData: []

      }
    },
	beforeMount() {
		
	},
    mounted() {
		if(localStorage.getItem("currentPage")!=undefined){
			this.currentPage = parseInt(localStorage.getItem("currentPage"));			
		}
        this.getData();
    },
  }
</script>
<style scope>
.list2{
  border: 1px solid #ccc;
  border-radius: 20px;
  margin-top: 10px;
  padding: 6px 30px 6px 30px;
  float: right;
  margin-right: 10%;
}
.el-table--striped .el-table__body tr.el-table__row--striped td {
    background:rgb(242, 245, 247);
}
.hover-row{
  color:darkorange;
}
.el-table--border td, .el-table--border th, .el-table__body-wrapper .el-table--border.is-scrolling-left ~ .el-table__fixed {
    border-right: 0px;
}
.el-table--border{
  border:0;
  border-radius: 10px;
}
.table-card{
  border:1px solid #aaaaaa;
  border-radius: 10px;
  padding:30px 10px 35px 10px;
}
.pagin{
        border:1px solid #aaa;
        border-radius:20px;
        padding:5px 10px 5px 10px;
        float:right;
        background-color:white;
        margin-top:-20px
    }
</style>