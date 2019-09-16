<template>
	<el-card class="box-card" style="font-size: larger;">
	  <div slot="header" class="clearfix">
		<span style="font-weight: 700;">基金详情</span>
		<!-- <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button> -->
	  </div>
	  <div class="text item row">
		<span class="col-md-3">基金代码: {{ fundDetails.fundCode}}</span>
		<span class="col-md-3">基金名称: {{ fundDetails.fundName }}</span>
		<span class="col-md-3">基金简称: {{ fundDetails.abbreviation}}</span>
		<span class="col-md-3">基金简称拼音: {{ fundDetails.pinyin }}</span>
	  </div>
	  <div class="text item row">
		<span class="col-md-3">设立日期: {{ fundDetails.startTime}}</span>
		<span class="col-md-3">基金类型: {{ fundDetails.type }}</span>
		<span class="col-md-3">投资类型: {{ fundDetails.investType }}</span>
		<span class="col-md-3">基金设立规模: {{ fundDetails.scale }}</span>
	  </div>
	  <div class="text item row">
		<span class="col-md-3">基金管理公司（基金管理人）: {{ fundDetails.managerCompany }}</span>
		<span class="col-md-3">基金管理银行（基金托管人）: {{ fundDetails.managerBank }}</span>
		<span class="col-md-3">管理人员: <a :href="fundDetails.managerLink" style="color: darkorange;" target="_blank">{{ fundDetails.manager }}</a></span>
		<span class="col-md-3">最小购买数量: {{ fundDetails.minPurchaseAmount }}</span>
	  </div>
	  <div class="text item row">
		<span class="col-md-12">投资目标: {{ fundDetails.target }}</span>
	  </div>
	  <div class="text item row">
	  	<span class="col-md-12">投资范围: {{ fundDetails.fundRange }}</span>
	  </div>
	  <div class="text item row">
	  	<span class="col-md-12">基金历史: {{ fundDetails.history }}</span>
	  </div>
	</el-card>
</template>

<script>
	export default {
		props: ['fundDetails'],
		data() {
			return {}
		},
		methods: {

		}
	}
</script>

<style scoped="scoped">
	.tabsFont{
		font-size: medium;
		/* color: #1E90FF; */
	}
	 .text {
	    font-size: 14px;
	  }
	
	  .item {
	    margin-bottom: 18px;
		color: dimgrey;
		font-weight: 700;
	  }
	
	  .clearfix:before,
	  .clearfix:after {
	    display: table;
	    content: "";
	  }
	  .clearfix:after {
	    clear: both
	  }
	
	  .box-card {
	    width: 86%;
		margin-left: 7%;
	  }
	  
</style>
