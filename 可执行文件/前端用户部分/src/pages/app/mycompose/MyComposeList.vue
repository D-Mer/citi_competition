<template>
  <div id="backs" class="row">
    <div class="col-md-2"></div>
    <div id="composelist" class="col-md-8">
      <list
        style="background-color: white;margin-top:20px;"
        :lhead="listHead"
        :ldata="listData"
        :bfunc="buttonFunction"
        myid="mycomposelist"
        :rowsperpage= "12"
        ltitle="我的组合<span style='font-size:20px'>"
        v-if="reload"
      ></list>
    </div>
  </div>
</template>
<script>
import List from "@/components/List.vue";
export default {
  name: "MyComposeList",
  components: {
    list: List
  },
  data() {
    return {
      reload: true,
      listHead: ["购买时间", "组合编号", "购买金额", "当前状态", "卖出金额"],
      getdatas: [],
      datas: [],
      listData: [],
      buttonFunction: {
        详情: e => {
          this.jump(e);
        }
      }
    };
  },
  mounted() {
    this.getdata();
  },
  methods: {
    jump(e) {
      var id = e.target.parentNode.parentNode.children[1].innerHTML;
      this.$router.push({ name : "Mycompose", query: { composeId: id } });
    },
    getdata() {
      var cusId = localStorage.getItem("customerId");
      cusId = parseInt(cusId)
      this.axios
        .get("/apis//finance/getRecord/purchaseAmount", {
          params: {
            customerId: cusId
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
            for (var j = 0; j < 5; j++) {
              arr[i][j] = n;
              n++;
            }
          }
          this.listData = arr;
          i = 0;
          temp.forEach(item => {
            this.listData[i][0] = item.purchaseTime;
            this.listData[i][1] = item.compositionId;
            if (item.purchaseAmount == null) {
              this.listData[i][2] = "未支付";
            } else {
              this.listData[i][2] = item.purchaseAmount;
            }
            if (item.purchaseAmount == null) {
              this.listData[i][3] = "未支付";}
             else if (item.soldTime != null) {
              this.listData[i][3] = "已赎回";
            } else if (item.requestTime != null) {
              this.listData[i][3] = "申请赎回中";
            } else {
              this.listData[i][3] = "持有中";
            }
            if (item.soldAmount == null) {
              this.listData[i][4] = 0;
            } else {
              this.listData[i][4] = soldAmount;
            }
            i = i + 1;
          });
          this.reload = false;
          this.reload = true;
        })
        .catch(err => {
          console.log("error");
        });
    }
  }
};
</script>
<style scoped>
.composelist {
  height: 100%;
  margin-top: 10px;
  background-color: white;
  border: 2px solid;
  border-radius: 10px;
  border-color: #f4f4f4;
  padding: 0px;
  margin-bottom: 10px;
}
#backs {
  background-color: #f6f6f6;
  position: absolute;
  top: 60px;
  bottom: 0px;
  right: 15px;
}
</style>