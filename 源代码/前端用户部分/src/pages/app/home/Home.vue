<!--新用户首页组合推荐-->
<template>
	<div>

		<div class="lcard">
			<carousel></carousel>
			<el-row style="font-family: monospace; font-weight: 900;color: mediumorchid">
				推荐组合 >>
			</el-row>
			<el-row :gutter="23">
			  <el-col style="width: 420px">
			    <el-card class="card" :body-style="{ padding: '0px' }">
			      <div class="image" id="main0" style="width: 400px;height:400px;"></div>
			      <div>
			        <div class="bottom clearfix">
			          <el-button type="primary" round  class="button mybutton" @click="check(0)">查看详情</el-button>
			        </div>
			      </div>
				 </el-card>
				</el-col>
				<el-col style="width: 420px;">
			    
				<el-card class="card" :body-style="{ padding: '0px' }">
				  <div class="image" id="main1" style="width: 400px;height:400px;"></div>
				  <div>
				    <div class="bottom clearfix">
				      <el-button type="primary" round  class="button mybutton" @click="check(1)">查看详情</el-button>
				    </div>
				  </div>
				  </el-card>
				</el-col>
				<el-col style="width: 420px;">
				
				<el-card class="card" :body-style="{ padding: '0px' }">
				  <div class="image" id="main2" style="width: 400px;height:400px;"></div>
				  <div>
				    <div class="bottom clearfix">
				      <el-button type="primary" round  class="button mybutton" @click="check(2)">查看详情</el-button>
				    </div>
				  </div>
				</el-card>
			  </el-col>
			</el-row>
			
			
		</div>
	</div>
</template>
<script>
	// 引入基本模板
	let echarts = require('echarts/lib/echarts')
	require('echarts/lib/chart/pie')
	require('echarts/lib/component/tooltip')
	
	import Vue from 'vue'
	import carousel from './carousel'
	export default {
		name: 'Home',
		data() {
			return {
				newUserCompUrl: '/apis/fund/newUser/recommendFund',
				compComfirmUrl: '/apis/finance/confirmFundComposition',
				compDetailsUrl: '/apis/finance/getRecordById',
				compDetails0: [{
						"percentage": 0.2,
						"fund": {
							"fundId": 11,
							"abbreviation": "华夏纯债A",
							"type": "债券型",
						}
					},
					{
						"percentage": 0.3,
						"fund": {
							"fundId": 12,
							"abbreviation": "华夏纯债C",
							"type": "债券型",
						}
					},
					{
						"percentage": 0.4,
						"fund": {
							"fundId": 18,
							"abbreviation": "华富安鑫",
							"type": "债券型",
						}
					},
					{
						"percentage": 0.1,
						"fund": {
							"fundId": 22,
							"abbreviation": "易方达信用债A",
							"type": "债券型",
						}
					}
				],
				compDetails1: '',
				compDetails2: ''
			}
		},
		methods: {
			check(index){
				alert(localStorage.getItem('newUserHomeShow').split('_')[index]);
			},
			optionFactory(_data){
				var option = {
					tooltip: {
						trigger: 'item',
						formatter: "{a} <br/>{b} : {c} ({d}%)"
					},
					series: [{
						name: '推荐组合',
						type: 'pie',
						radius: '58%',
						center: ['50%', '50%'],
						data: _data,
						itemStyle: {
							emphasis: {
								shadowBlur: 10,
								shadowOffsetX: 0,
								shadowColor: 'rgba(0, 0, 0, 0.5)'
							}
						}
					}]
				};
				return option;
			},
			dataFactory(compDetails){
				var data = [];
				compDetails.forEach(
					function(v){
						data.push({
							value: v.percentage,
							name: v.fund.abbreviation
						})
					}
				)
				return data;
			},
			chartsFactory(id,data){
				var myChart0 = echarts.init(document.getElementById(id));
				var data0 = this.dataFactory(data);
				var option0 = this.optionFactory(data0);
				myChart0.setOption(option0);
			},
			showCharts() {
				this.chartsFactory('main0',this.compDetails0);
				this.chartsFactory('main1',this.compDetails1);
				this.chartsFactory('main2',this.compDetails2);
			},
			getDetails(id1,id2,id3,compDetailsUrl){
				this.$axios.all([
					this.$axios.get(compDetailsUrl, {
						params: {
							compositionId: id1
						}
					}),
					this.$axios.get(compDetailsUrl, {
						params: {
							compositionId: id2
						}
					}),
					this.$axios.get(compDetailsUrl, {
						params: {
							compositionId: id3
						}
					}),
				]).then(this.$axios.spread((details0, details1, details2) => {
					this.compDetails0 = details0.data.result.fundsWithPercentage;
					this.compDetails1 = details1.data.result.fundsWithPercentage;
					this.compDetails2 = details2.data.result.fundsWithPercentage;
					this.showCharts();
				})).catch((err3) => {
					console.log('err3');
					console.log(err3);
				});
			},
			getData() {
				this.$axios.get(this.newUserCompUrl)
					.then((resp) => {
						var comp0 = resp.data.result[0];
						var comp1 = resp.data.result[1];
						var comp2 = resp.data.result[2];
						console.log(comp2);
						this.$axios.all([
							this.$axios.post(this.compComfirmUrl,
								comp0, {
									params: {
										customerId: localStorage.getItem('customerId'),
									}
								}),
							this.$axios.post(this.compComfirmUrl,
								comp1, {
									params: {
										customerId: localStorage.getItem('customerId'),
									}
								}),
							this.$axios.post(this.compComfirmUrl,
								comp2, {
									params: {
										customerId: localStorage.getItem('customerId'),
									}
								}),
						]).then(this.$axios.spread((resp0, resp1, resp2) => {
							var id1 = resp0.data.result;
							var id2 = resp1.data.result;
							var id3 = resp2.data.result;
							console.log(id1 + " " + id2 + " " + id3);
							this.getDetails(id1,id2,id3,this.compDetailsUrl);
							localStorage.setItem('newUserHomeShow',id1+
							"_"+id2+'_'+id3)
						})).catch((err2) => {
							console.log('err2');
							console.log(err2);
						});
					}).catch((err) => {
						console.log('err1');
						console.log(err);
					});
			}
		},
		components: {
			carousel
		},
		mounted() {
			let a = localStorage.getItem('newUserHomeShow');
			if(a == null){
				this.getData();
			}
			else{
				let list = a.split('_');
				this.getDetails(list[0],list[1],list[2],this.compDetailsUrl);
			}
			
		}
	}
</script>
<style scoped>
	.lcard {
		width: 86%;
		margin-left: 7%;
		margin-top: 50px;
		font-size: 18px;
	}
	  .bottom {
	    margin-top: 13px;
	    line-height: 12px;
		padding: 0 10px 0 10px;
	  }
	
	  .image {
	    width: 100%;
	    display: block;
		background: ghostwhite;
		z-index: 1;
	  }
	
	  .clearfix:before,
	  .clearfix:after {
	      display: table;
	      content: "";
	  }
	  
	  .clearfix:after {
	      clear: both
	  }
	  .card{
		  border-radius: 20px;
		  /* padding: 0 0 30px 0; */
		  margin:30px 0px 50px 0px;
		  background: linear-gradient(60deg, #08089c, transparent);
	  }
	  .mybutton{
		  float: right;
		  margin-bottom: 15px;
	  }
</style>
