<template>
  <div>
    <div id="backs" class="row">
      <div class="col-md-1"></div>
      <div id="container" class="col-md-10 row">
        <div class="col-md-1"></div>
        <div class="col-md-10">
          <div id="header">风险评估问卷</div>
          <div v-for="(item,index) in quesandans" :key="(item,index)">
            <div v-bind:id="'question'+index">
              <p>{{item.question}}</p>
              <div v-for="(item1,index1) in item.answer" :key="(item1,index1)">
                <el-radio v-model="radio[index]" v-bind:label="index*10+index1">{{item1}}</el-radio>
              </div>
            </div>
          </div>
          <div id="subbutton">
            <button
              type="button"
              class="btn btn-primary repay"
              data-toggle="button"
              @click="submits"
            >提交</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: "Questionaire",
  data() {
    return {
      radio: [
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        ""
      ],
      quesandans: [
        {
          question: "1. 您的年龄是？",
          answer: ["A. 61岁及以上", "B. 41-60岁", "C. 18-40岁"]
        },
        {
          question: "2．您目前的家庭财务状况属于以下哪一种？",
          answer: [
            "A. 有长期定额债务，如住房抵押贷款等",
            "B. 有短期信用债务，如信用卡欠款、亲朋之间的借款等",
            "C. 收入和支出基本相抵",
            "D. 收入来源稳定，且有一定的积蓄和一定的投资",
            "E. 积蓄丰厚，且制定了完备的理财计划"
          ]
        },
        {
          question: "3. 您家庭的财富总额大致为？",
          answer: [
            "A. 50万及以下",
            "B. 50-200万",
            "C. 200-500万",
            "D. 500-1000万",
            "E. 1000万以上"
          ]
        },
        {
          question: "4. 您的家庭年收入为？",
          answer: [
            "A. 5万元以下",
            "B. 5-20万元",
            "C. 21-50万元",
            "D. 51-100万元",
            "E. 100万元以上"
          ]
        },
        {
          question: "5. 目前，您的各项开支总和占全部收入的比例是多少？",
          answer: [
            "A．80%以上",
            "B．60%-80%",
            "C．40%-60%",
            "D．20%-40%",
            "E．20%以下"
          ]
        },
        {
          question:
            "6. 请问您的家庭备用金（以备不时之需，取出时不会造成较大的本金损失）大约相当于您几个月的家庭开销？",
          answer: [
            "A. 没有准备备用金",
            "B. 3个月以下",
            "C. 3-6个月",
            "D. 6-12个月",
            "E. 12个月以上"
          ]
        },
        {
          question: "7. 在您每月的家庭收入中，大约有多少收入可用于投资？",
          answer: [
            "A. 几乎不投资",
            "B. 0%-10%",
            "C. 10%-25%",
            "D. 25%-50%",
            "E. 50%以上"
          ]
        },
        {
          question:
            "8. 您有多少年投资股票、基金、金融衍生产品等风险产品的经验？",
          answer: [
            "A. 没有经验",
            "B. 少于1年",
            "C. 1至3年",
            "D. 3至5年",
            "E. 大于5年"
          ]
        },
        {
          question:
            "9. 您现在是否持有以下任何投资产品？（可多选）（计分时仅计算分数最高的选项）",
          answer: [
            "A. 现金、存款",
            "B. 债券、债券型基金 ",
            "C. 混合型基金 ",
            "D. 股票、股票型基金 ",
            "E. 权证、期货 "
          ]
        },
        {
          question:
            "10. 您现在已经投资的高风险产品（如股票、股票型基金、期货等）大约占您所有的金融资产的百分比？",
          answer: [
            "A. 低于5%",
            "B. 5%-10% ",
            "C. 10%-25%",
            "D. 25%-50%",
            "E. 大于50%"
          ]
        },
        {
          question: "11．您投资基金主要出于什么目的？",
          answer: [
            "A. 合理利用流动资金，同时获得少量的固定收益",
            "B. 希望资产稳健增长，作为日常收入的补充",
            "C. 希望资产快速增长，作为主要收入来源"
          ]
        },
        {
          question: "12. 以下哪一项描述比较接近你对投资的态度？",
          answer: [
            "A. 不愿意承受资产亏损的风险或者愿意承担较小的风险，收益率可以低一些 ---1分",
            "B. 愿意承担一定的风险，获取一定的收益 ---3分",
            "C. 可以承担较大的投资风险以寻求长期投资报酬最大化 ---5分"
          ]
        },
        {
          question:
            "13．一般情况下，投资期限越长，投资风险越高。您计划中的基金投资期限是多长？",
          answer: [
            "A. 1年以下",
            "B. 1-3年",
            "C. 3-5年",
            "D. 5-10年 ",
            "E. 10年以上"
          ]
        },
        {
          question: "14. 下面哪项最符合您对某项投资在未来1年内亏损所持的态度？",
          answer: [
            "A. 几乎不能承受任何亏损",
            "B. 我只能承受本金10%以内的亏损",
            "C. 我只能承受本金10%-20%的亏损 ",
            "D. 我能承受本金20%-50%的亏损",
            "E. 我能承受本金50%以上的亏损 "
          ]
        },
        {
          question:
            "15. 假设您的一大笔投资金额出现持续上涨，比如一个月增长了20%。您会怎么做？",
          answer: [
            "A. 立即全部抛出",
            "B. 抛出大部分，实现大部分投资获利",
            "C. 抛出小部分，实现部分获利",
            "D. 不立刻行动，继续观望一段时间",
            "E. 投入更多的资金"
          ]
        },
        {
          question:
            "16. 假设您的一大笔投资金额出现持续下跌，比如一个月下跌了20%。您会怎么做？",
          answer: [
            "A. 立即全部抛出",
            "B. 抛出大部分",
            "C. 抛出小部分",
            "D. 不立刻行动，继续观望一段时间",
            "E. 投入更多的资金"
          ]
        },
        {
          question: "17. 以下情况您会选择哪一种？",
          answer: [
            "A. 100%的概率获得5万元",
            "B. 有50%的概率获得10万元",
            "C. 有25%的概率获得20万元",
            "D. 有10%的概率获得50万元",
            "E. 有5%的概率获得100万元"
          ]
        },
        {
          question:
            "18. 假设现在有几个投资品种，您必须选中其中一个投资，那么您会选择哪个？",
          answer: [
            "A. 年化收益率固定为4%，不亏损",
            "B. 年化收益在0～8%之间，不亏损",
            "C. 年化收益率可能在10%以上，同时本金也有可能亏损10%以上 ",
            "D. 年化收益率可能在20%以上，同时本金也有可能亏损20%以上 ",
            "E. 年化收益率可能在30%以上，同时本金也有可能亏损30%以上"
          ]
        }
      ]
    };
  },
  methods: {
    submits() {
      var cusId = localStorage.getItem("customerId");
      cusId = parseInt(cusId);
      var data = this.radio;
      var ans = [];
      var i;
      var temp1 = true;
      for (i = 0; i < 18; i++) {
        if (data[i] === "") {
          temp1 = false;
        } else {
          var temp = data[i] % 10;
          if (temp == 0) ans.push("A");
          if (temp == 1) ans.push("B");
          if (temp == 2) ans.push("C");
          if (temp == 3) ans.push("D");
          if (temp == 4) ans.push("E");
        }
      }
      if (temp1 == false) {
        alert("您还有题目尚未完成！");
      } else {
        this.axios
          .post("/apis//user/questionnaire/update", {
            customerId: cusId,
            answerList: ans
          })
          .then(result => {
            //this.$router.push({ path: "../app/userinfo" });
            var url = localStorage.getItem("homePage")
            if (result.data.status == false) {
              this.axios
                .post("/apis//user/questionnaire/add", {
                  customerId: cusId,
                  answerList: ans
                })
                .then(result => {
                  this.$router.push({ name:url });
                  alert("修改成功！");
                })
                .catch(err => {
                  console.log(err);
                });
            } else {
              console.log(url);
              this.$router.push({ name:localStorage.getItem("homePage") });
              alert("修改成功！");
            }
          })
          .catch(err => {
            console.log(err);
          });
      }
    },
    getdata() {
      var cusId = localStorage.getItem("customerId");
      cusId = parseInt(cusId);
      this.axios
        .get("/apis//user/questionnaire/get", {
          params: {
            customerId: cusId
          }
        })
        .then(result => {
          console.log(this.radio);
        })
        .catch(err => {
          console.log("error");
        });
    }
  },
  mounted() {
    this.axios.defaults.headers.post["Content-Type"] = "application/json";
  }
};
</script>
<style scoped>
.repay {
  width: 100px;
}
#subbutton {
  margin-top: 30px;
  margin-bottom: 30px;
  text-align: center;
}
#header {
  margin-top: 30px;
  text-align: center;
  font-size: 30px;
  font-family: KaiTi;
}
#backs {
  background-color: #f6f6f6;
  margin: 0px;
  padding: 0px;
  height: 100%;
}
#container {
  margin-top: 10px;
  background-color: white;
  border: 2px solid;
  border-radius: 10px;
  border-color: #f4f4f4;
  padding: 0px;
  margin-bottom: 30px;
}
</style>