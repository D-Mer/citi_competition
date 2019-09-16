<template>
  <div>
    <div id="text-title">方案详情</div>
    <div id="composelist">
      <list
        :lhead="listHead"
        :ldata="listData"
        :bfunc="buttonFunction"
        myid="mycomposelist"
        :rowsperpage="12"
      ></list>
    </div>
  </div>
</template>
<script>
import List from "@/components/List.vue";
/* eslint-disable */
export default {
  name: "MyComposeInfo",
  components: {
    list: List
  },
  props: ["rates", "detailrates"],
  data() {
    return {
      listHead: [
        "基金编码",
        "基金名称",
        "基金类型",
        "基金公司",
        "基金经理",
        "基金编号",
        "所占比例"
      ],
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
  methods: {
    jump(e) {
      var id = e.target.parentNode.parentNode.children[5].innerHTML;
      this.$router.push({ name:"Details", params: { fundId: id } });
    }
  },
  mounted() {
    var cosId = this.$route.query.composeId;
    this.axios
      .get("/apis//finance/getRecordById", {
        params: {
          compositionId: cosId
        },
      })
      .then(result => {
        var i = 0;
        var temp = result.data.result.fundsWithPercentage;
        var len = temp.length;
        var arr = [];
        var n = 1;
        for (var i = 0; i < len; i++) {
          arr[i] = []; //每行有7列
          for (var j = 0; j < 7; j++) {
            arr[i][j] = n;
            n++;
          }
        }
        this.listData = arr;
        i = 0;
        temp.forEach(item => {
          var items = item.fund;
          this.listData[i][0] = items.fundCode;
          this.listData[i][1] = items.abbreviation;
          this.listData[i][2] = items.type;
          this.listData[i][3] = items.managerCompany;
          this.listData[i][4] = items.manager;
          this.listData[i][5] = items.fundId;
          this.listData[i][6] = (item.percentage * 100).toString() + "%";
          i = i + 1;
        });
        this.reload = false;
        this.reload = true;
      })
      .catch(err => {
        console.log(err);
      });
  }
};
</script>
<style scoped>
body {
  margin: 0px;
}
#fixeds {
  background-color: cornsilk;
  height: 30px;
  text-align: left;
  font-size: 20px;
}
#text-title {
  text-align: left;
  font-size: 25px;
}
.texts {
  text-align: left;
}
</style>