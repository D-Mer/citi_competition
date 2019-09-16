<template>
	<el-card class="box-card" style="font-size: larger;">
	  <div slot="header" class="clearfix">
		<span style="font-weight: 700;">费率显示</span>
		<!-- <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button> -->
	  </div>
	  <div v-for="item in fundBuyRateData" >
		  <div class="text item row">
		  		<span class="col-md-6">起投金额: {{ item.startAmount}}</span>
		  		<span class="col-md-6">最高费率({{ item.descriptionType===1?'元':'%' }}): {{ item.rate }}</span>
		  </div>
	  </div>
	</el-card>
</template>

<script>
	export default {
		props: ['fundBuyRateData'],
		data() {
			return {
			}
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
		color: cornflowerblue;
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
	    width: 450px;
	  }
	  
</style>
