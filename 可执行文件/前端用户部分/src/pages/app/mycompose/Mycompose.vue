<template>
  <div>
    <div id="mycompose" class="row">
      <div class="col-md-1"></div>
      <div class="col-md-10 container">
        <my-compose-detail v-bind:details="info.intro" v-bind:rates="info.rates"></my-compose-detail>
        <my-compose-info v-bind:rates="info.rates" v-bind:detailrates="info.detailrates"></my-compose-info>
      </div>
    </div>
  </div>
</template>
<script>
/* eslint-disable */
import Vue from 'vue'
import MyComposeDetail from "./MyComposeDetail";
import MyComposeInfo from "./MyComposeInfo";
export default {
  name: "MyCompose",
  components: {
    "my-compose-detail": MyComposeDetail,
    "my-compose-info": MyComposeInfo
  },

  data() {
    return {
      info: {
        intro: {
          date: "2019/8/20",
          achievement: "100",
          volatility: "5%",
          status: "持有中",
          recently: [1024.1, 1054.2, 1078.3]
        },
        rates: [
          {fixed: 0.25},
          {shares: 0.25},
          {cash: 0.25},
          {elses: 0.25},
        ],
        detailrates: {
          fixedrates: [
            "16.38% 大摩双利增强C(000025)",
            "11.37% 长信借债壹号C(004220)"
          ],
          sharesrates: [
            "16.30% 大摩双利增强C(000025)",
            "11.39% 长信借债壹号C(004220)"
          ],
          cashrates: [
            "16.39% 大摩双利增强C(000025)",
            "11.224% 长信借债壹号C(004220)"
          ],
          elsesrates: [
            "16.35% 大摩双利增强C(000025)",
            "11.54% 长信借债壹号C(004220)"
          ]
        }
      }
    };
  }
};
</script>
<style scoped>
.container {
  margin-top: 10px;
  background-color: white;
  border: 2px solid;
  border-radius: 10px;
  border-color: #f4f4f4;
  margin-bottom: 10px;
}
#mycompose {
  background-color: #f6f6f6;
  height: 100%;
  margin: 0px;
}
</style>
