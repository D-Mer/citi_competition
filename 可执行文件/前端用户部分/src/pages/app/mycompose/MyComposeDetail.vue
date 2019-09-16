<template>
  <div>
    <div id="introduce" class="row">
      <span class="col-md-3 datas">购买时间： {{date}}</span>
      <span class="col-md-2 datas">组合编号：{{cumid}}</span>
      <span class="col-md-3 datas">购买金额：{{purchaseAmount}}</span>
      <span class="col-md-2 datas">状态：{{status}}</span>
      <span class="col-md-1 datas"></span>
      <button
        v-if="genreratstatus(status)"
        type="button"
        class="btn btn-primary repay"
        data-toggle="button"
        @click="toggleModal"
      >赎回</button>
      <button
        v-if="genstatus(status)"
        type="button"
        class="btn btn-primary repay"
        data-toggle="button"
        @click="buy"
      >购买</button>
      <modal v-show="showModal" @on-cancel="cancel" v-bind:cosId="cosId"></modal>
    </div>
    <div class="row">
      <div id="pie" class="col-md-6" style="margin-top:20px;height: 300px;"></div>
      <div id="pie1" class="col-md-6" style="  margin-top: 20px;height: 300px;"></div>
    </div>
  </div>
</template>

<script>
/* eslint-disable */
import Modal from "./Modal.vue";
let echarts = require("echarts/lib/echarts");
// 引入饼状图组件
require("echarts/lib/chart/pie");
require("echarts/lib/chart/line");
// 引入提示框和title组件
require("echarts/lib/component/tooltip");
require("echarts/lib/component/title");
export default {
  name: "MyComposeDetail",
  components: {
    Modal: Modal //声明组件
  },
  data() {
    return {
      cumid: 0,
      cosId: "",
      charts: "",
      showModal: false,
      rate: [],
      typerate: [],
      names: [],
      typenames: [],
      date: "",
      purchaseAmount: 0,
      status: ""
    };
  },
  beforemount(){
    this.cosId = this.$route.query.composeId;
  },
  props: ["details", "rates"],
  mounted() {
    this.cosId = this.$route.query.composeId;
    var cusId = localStorage.getItem("customerId");
    cusId = parseInt(cusId);
    this.axios
      .get("/apis//finance/getRecord/purchaseAmount", {
        params: {
          customerId: cusId
        }
      })
      .then(result => {
        console.log(result)
        var res = {};
        for (var i = 0; i < result.data.result.length; i = i + 1) {
          if (result.data.result[i].compositionId == this.cosId) {
            res = result.data.result[i];
          }
        }
        this.date = res.purchaseTime;
        this.cumid = this.cosId;
        this.purchaseAmount = res.purchaseAmount;
        this.purchaseAmount = res.purchaseAmount;
        if (res.purchaseAmount == null) {
          this.status = "未支付";
        } else if (res.soldTime != null) {
          this.status = "已赎回";
        } else if (res.requestTime != null) {
          this.status = "申请赎回中";
        } else if(localStorage.getItem("balance")<res.purchaseAmount){
          this.status = "未支付";
        }
        else{
          this.status = "持有中";
        }
      })
      .catch(err => {
        console.log(err);
      });
    this.axios
      .get("/apis//finance/getRecordById", {
        params: {
          compositionId: this.cosId
        }
      })
      .then(result => {
        console.log(result)
        var datas = result.data.result.fundsWithPercentage;
        var ans = [];
        var typeans = [];
        var nameans = [];
        var typenamesans = [];
        this.id = datas.compositionId;
        for (var i = 0; i < datas.length; i = i + 1) {
          let temp = {};
          temp["value"] = datas[i].percentage;
          temp["name"] = datas[i].fund.abbreviation;
          nameans.push(datas[i].fund.abbreviation);
          ans.push(temp);
          let typetemp = {};
          if (typenamesans.indexOf(datas[i].fund.type) > -1) {
            var index = typenamesans.indexOf(datas[i].fund.type);
            typeans[index].value = typeans[index].value + datas[i].percentage;
          } else {
            typetemp["value"] = datas[i].percentage;
            typetemp["name"] = datas[i].fund.type;
            typenamesans.push(datas[i].fund.type);
            typeans.push(typetemp);
          }
        }
        this.rate = ans;
        this.names = nameans;
        this.typerate = typeans;
        this.typenames = typenamesans;
        this.initData();
        this.initData1();
      })
      .catch(err => {
        console.log(err);
      });
  },
  methods: {
    genstatus(s) {
      if (s == "未支付") return true;
      else return false;
    },
    buy() {
      this.showModal=true;
    },
    cancel() {
      this.showModal = false;
    },
    toggleModal() {
      this.axios
        .post("/apis//finance/sellFundComposition", null, {
          params: {
            compositionId: this.cosId
          }
        })
        .then(result => {
          console.log(result);
          this.$router.push({ path: "../app/MycomposeList" });
          alert("赎回成功！");
        })
        .catch(err => {
          console.log(err);
        });
    },
    genreratstatus(s) {
      if (s == "持有中") return true;
      else return false;
    },
    initData() {
      var myChart = echarts.init(document.getElementById("pie"));
      myChart.setOption({
        title: {
          text: "各基金占比图",
          x: "center"
        },
        tooltip: {
          trigger: "item",
          formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
          orient: "vertical",
          bottom: "bottom",
          data: this.names
        },
        series: [
          {
            name: "基金占比",
            type: "pie",
            radius: "55%",
            center: ["50%", "50%"],
            data: this.rate,
            itemStyle: {
              emphasis: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)"
              }
            }
          }
        ]
      });
    },
    initData1() {
      var myChart = echarts.init(document.getElementById("pie1"));
      myChart.setOption({
        title: {
          text: "各基金类型占比图",
          x: "center"
        },
        tooltip: {
          trigger: "item",
          formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
          orient: "vertical",
          bottom: "bottom",
          data: this.typenames
        },
        series: [
          {
            name: "基金占比",
            type: "pie",
            radius: "55%",
            center: ["50%", "50%"],
            data: this.typerate,
            itemStyle: {
              emphasis: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)"
              }
            }
          }
        ]
      });
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#introduce {
  background-color: bisque;
  height: 30px;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}
.repay {
  height: 30px;
  line-height: 15px;
}
#pie {
  height: 300px;
}
#pillars {
  margin-top: 20px;
  height: 300px;
}
.datas {
  margin-top: 5px;
}
</style>