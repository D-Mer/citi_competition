<template>
  <div>
    <div class="mon">
      账户余额：{{datas.balance}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <el-button type="primary" @click="charge">充值</el-button>
      <el-button type="primary" @click="withdraw">提现</el-button>
    </div>
    <div>
      <list
        style="background-color: white;margin-top:20px;"
        :lhead="listHead"
        :ldata="listData"
        :rowsperpage= "12"
        ltitle="交易记录<span style='font-size:20px'>"
      ></list>
    </div>
    <mfc v-show="showmfc" @on-cancel="cancel1"></mfc>
    <mfd v-show="showmfd" @on-cancel="cancel2"></mfd>
  </div>
</template>
<script>
import List from "@/components/List.vue";
import Modalforcharge from "./Modalforcharge.vue";
import Modalforwithdraw from "./Modalforwithdraw.vue";
export default {
  name: "Mymoney",
  components: {
    mfc: Modalforcharge,
    mfd: Modalforwithdraw,
    list: List
  },
  data() {
    return {
      userId: 0,
      listHead: ["操作时间", "执行操作", "操作金额", "当前状态"],
      listData: ["", "", "", ""],
      datas: {
        balance: 10000,
        bonus: 2
      },
      showmfc: false,
      showmfd: false
    };
  },
  mounted() {
    var cusId = localStorage.getItem("customerId");
    cusId = parseInt(cusId);
    this.userId = cusId;
    this.datas.balance = parseInt(localStorage.getItem("balance"));
    this.getdata();
  },
  methods: {
    cancel1() {
      this.showmfc = false;
    },
    cancel2() {
      this.showmfd = false;
    },
    charge() {
      this.showmfc = true;
      /*console.log(233);
      this.axios
        .get("/apis//finance/recharge", {
          params: {
            customerId: 1840,
            amount: 2
          },
          headers: {
            token:
              "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiNTgxIiwi5raI6LS56ICFIl19.WqKdDNQbhNJc6Sh4MFdARTVOgVu4LsLYc122lNwLxV8"
          }
        })
        .then(result => {
          console.log(result);
          if (result.request.status != 200) {
            return this.tipWarning(result.data.msg);
          }
          console.log(1);
          var payUrl = result.data;
          console.log(payUrl);
          let routerData = this.$router.resolve({
            path: "../app/charge",
            query: { htmls: payUrl }
          });
          console.log(2);
          window.open(routerData.href, "_blank");
        })
        .catch(err => {
          console.log(err);
        });*/
    },
    withdraw() {
      this.showmfd = true;
      /*this.axios
        .post("/apis//finance/withdraw", null, {
          params: {
            customerId: 1840,
            amount: 2
          },
          headers: {
            token:
              "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiNTgxIiwi5raI6LS56ICFIl19.WqKdDNQbhNJc6Sh4MFdARTVOgVu4LsLYc122lNwLxV8"
          }
        })
        .then(result => {
          console.log(result);
        })
        .catch(err => {
          console.log(err);
        });*/
    },
    getdata() {
      this.axios
        .get("/apis//finance/records", {
          params: {
            customerId: this.userId
          }
        })
        .then(result => {
          var i = 0;
          var temp = result.data.result;
          var len = temp.length;
          var arr = [];
          var n = 1;
          for (var i = 0; i < len; i++) {
            arr[i] = []; //每行有10列
            for (var j = 0; j < 4; j++) {
              arr[i][j] = n;
              n++;
            }
          }
          this.listData = arr;
          i = 0;
          temp.forEach(item => {
            this.listData[i][0] = item.tradeTime;
            this.listData[i][1] = item.remark;
            this.listData[i][2] = item.amount;
            this.listData[i][3] = item.state;
            i = i + 1;
          });
        })
        .catch(err => {
          console.log(err);
        });
    }
  }
};
</script>
<style scoped>
.mon {
  font-size: 18px;
  padding: 20px 20px 20px 20px;
}
</style>