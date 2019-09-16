<template>
	<div class="mycontainer">
		<div class="return" @click="back">
			<img src = '@/../static/return.jpg' height="30px" width="30px"/> 返回上一页
		</div>
		<div class='lcard'>
			<el-row>
				<el-col :span="15">
					<div class="grid-content bg-purple" style="margin-top: 20px;margin-left:40px">
						<div id="detailsChart" style="height:400px;width:710px"></div>
					</div>
				</el-col>
				<el-col :span="9">
					<div class="grid-content bg-purple-light" style="position: absolute;bottom: 10%;margin-left:-80px">
						<tabs :fundBuyRateData="fundBuyRateData" ></tabs>
					</div>
				</el-col>
			</el-row>
			<div style="margin-top:35px">
				<detailCard :fundDetails="detailsData"></detailCard>
			</div>
		</div>
		
	</div>
</template>

<script>
	// 引入基本模板
	let echarts = require('echarts/lib/echarts')
	require('echarts/lib/chart/bar')
	require('echarts/lib/chart/line')
	require('echarts/lib/component/tooltip')
	require('echarts/lib/component/toolbox')
	require('echarts/lib/component/dataZoom')
	require('echarts/lib/component/title')
	require('echarts/lib/component/legend')
	require('echarts/lib/component/axis')

	import tabs from './tabs'
	import detailCard from './detailCard'

	export default {
		name: "Details",
		components: {
			tabs,
			detailCard
		},
		data() {
			return {
				url: "/apis/fund/getFundNetValue",
				url2: "/apis/fund/getFundDetailedInfo",
				url3: '/apis/fund/getFundBuyRate',
				fundId: 1,
				chartData: "",
				detailsData: "",
				fundName: "123321",
				fundBuyRateData: "",
				fromPage:""
			}
		},
		methods: {
			back(){
				this.$router.go(-1);//返回上一层
				// this.$router.push({
				// 	name:this.fromPage.name,
				// 	params:{
				// 		currentPage : this.$route.params.currentPage
				// 	}
				// })
			},
			showChart() {
				var base = +new Date(2019, 3, 4);
				var oneDay = 24 * 3600 * 1000;
				var date = [];
				var daily = [this.chartData[0].dailyReturn];
				var weekly = [this.chartData[0].weeklyReturn];
				var monthly = [this.chartData[0].monthlyReturn];
				var threeMonths = [this.chartData[0].threeMonthsReturn];

				for (var i = 1; i < this.chartData.length; i++) {
					var now = new Date(base += oneDay);
					// date.push([now.getFullYear(), now.getMonth() + 1, now.getDate()].join('/'));
					date.push(this.chartData[i].tradingTime);
					daily.push(this.chartData[i].dailyReturn);
					weekly.push(this.chartData[i].weeklyReturn);
					monthly.push(this.chartData[i].monthlyReturn);
					threeMonths.push(this.chartData[i].threeMonthsReturn);
				}
				// console.log(data);
				var myChart = echarts.init(document.getElementById('detailsChart'));
				var option = {
					tooltip: {
						trigger: 'axis',
						position: function(pt) {
							return [pt[0], '10%'];
						}
					},
					legend: {
						data: ['日增长率(%)', '周增长率(%)', '月增长率(%)', '三月增长率(%)'],
						top: "8%"
					},
					title: {
						left: 'center',
						text: this.fundName + "走势",
					},
					toolbox: {
						feature: {
							dataZoom: {
								yAxisIndex: 'none'
							},
							restore: {},
							saveAsImage: {}
						}
					},
					xAxis: {
						type: 'category',
						boundaryGap: false,
						data: date
					},
					yAxis: {
						type: 'value',
						boundaryGap: false
					},
					dataZoom: [{
						type: 'inside',
						start: 0,
						end: 120
					}, {
						start: 0,
						end: 10,
						handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
						handleSize: '80%',
						handleStyle: {
							color: '#fff',
							shadowBlur: 3,
							shadowColor: 'rgba(0, 0, 0, 0.6)',
							shadowOffsetX: 2,
							shadowOffsetY: 2
						}
					}],
					series: [{
						name: '日增长率(%)',
						type: 'line',
						smooth: true,
						symbol: 'none',
						sampling: 'average',
						itemStyle: {
							color: 'rgb(255, 70, 131)'
						},
						data: daily
					}, {
						name: '周增长率(%)',
						type: 'line',
						smooth: true,
						symbol: 'none',
						sampling: 'average',
						itemStyle: {
							color: 'lightgreen'
						},
						data: weekly
					}, {
						name: '月增长率(%)',
						type: 'line',
						smooth: true,
						symbol: 'none',
						sampling: 'average',
						itemStyle: {
							color: 'darkblue'
						},
						data: monthly
					}, {
						name: '三月增长率(%)',
						type: 'line',
						smooth: true,
						symbol: 'none',
						sampling: 'average',
						itemStyle: {
							color: 'darkorange'
						},
						data: threeMonths
					}]
				};
				myChart.setOption(option);
			},
			getData() {
				if (this.$route.params.fundId != null || this.$route.params.fundId != undefined)
					this.fundId = this.$route.params.fundId;
				this.$axios.all([
					this.$axios.get(this.url, {
						params: {
							fundId: this.fundId,
						},
						// headers: {
						// 	'token': this.token
						// },
					}),
					this.$axios.get(this.url2, {
						params: {
							fundId: this.fundId,
						},
						// headers: {
						// 	'token': this.token
						// },
					}),
					this.$axios.get(this.url3, {
						params: {
							fundId: this.fundId,
						},
						// headers: {
						// 	'token': this.token
						// },
					})
				]).then(this.$axios.spread((chartResp, detailInfoResp, fundBuyRateResp) => {
					// 上面两个请求都完成后，才执行这个回调方法
					(this.chartData = chartResp.data.result) &&
					(this.detailsData = detailInfoResp.data.result) &&
					(this.fundName = this.detailsData.fundName) &&
					(this.fundBuyRateData = fundBuyRateResp.data.result) &&
					(this.showChart());
					// console.log(chartResp.data.result)
				})).catch((err) => {
					console.log(err);
				});
			}
		},
		mounted: function() {
			this.getData();
		},
		// computed: {
		// 	token: function() {
		// 		return 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiNTc5Iiwi5raI6LS56ICFIl19.WOVw5iHNnwVuBh2lTw1ibfSXoaCvQscBR35jMVI1FfM'
		// 	}
		// },
		beforeRouteEnter(to, from, next){
		   // this.fromPage = from;
		   next(vm => {
			   vm.fromPage = from;
		   });
		}
	}
</script>

<style scoped="scoped">
	.lcard {
		width: 86%;
		margin-left: 7%;
		margin-top: 50px;
		font-size: 18px;
		border: #cccccc 1px solid;
		border-radius: 10px;
		background: white;;
	}

	.mycontainer {
		background: #eeeeee;
		position:absolute; 
		top: 65.6px;
		right: 0;
		left:0
	}
	.return{
		position: fixed;
		right:10%;
		top:20%;
		z-index: 1;
		cursor:pointer;
	}
	.return:hover{
		background:#DDDDDD;
		
	}
</style>
