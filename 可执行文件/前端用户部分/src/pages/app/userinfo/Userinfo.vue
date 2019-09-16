<template>
<div id="userinfo">
  <div class="row">
      <div class="col-md-1"></div>
      <div class="col-md-10" id="container">
        <el-container style="height:100%; border: 2px solid #eee;">
          <el-aside width="200px" style="background-color:#F5F5F5">
            <el-menu :default-openeds="['1', '1']">
              <el-submenu index="1">
                <template slot="title">
                  <i class="el-icon-message"></i>个人信息
                </template>
                <el-menu-item-group>
                  <el-menu-item index="1-1" @click="infochange">信息修改</el-menu-item>
                  <el-menu-item index="1-2" @click="questionaire">问卷分析</el-menu-item>
                  <el-menu-item index="1-3" @click="pswchange">密码修改</el-menu-item>
                  <el-menu-item index="1-4" @click="mymoney">我的钱包</el-menu-item>
                </el-menu-item-group>
              </el-submenu>
            </el-menu>
          </el-aside>

          <el-container>
            <el-main>
              <info-change v-show="showinfochange"></info-change>
              <my-money v-show="showmymoney"></my-money>
              <psw-change v-show="showpswchange"></psw-change>
              <question v-show="showques"></question>
            </el-main>
          </el-container>
        </el-container>
      </div>
      <div class='col-md-1'></div>
  </div>
</div>
</template>
<script>
import Infochange from "./Infochange";
import Mymoney from "./Mymoney";
import Pswchange from "./Pswchange";
import Question from "./Question";
/* eslint-disable */
export default {
  name: "Userinfo",
  components: {
    "info-change": Infochange,
    "my-money": Mymoney,
    "psw-change": Pswchange,
    "question": Question
  },
  data() {
    return {
      showinfochange: true,
      showques: false,
      showpswchange: false,
      showmymoney: false
    };
  },
  methods: {
    infochange() {
      this.showinfochange = true;
        this.showques = false;
        this.showpswchange = false;
        this.showmymoney = false;
    },
    questionaire() {
      this.showinfochange = false;
        this.showques = true;
        this.showpswchange = false;
        this.showmymoney = false;
    },
    pswchange() {
      this.showinfochange = false;
        this.showques = false;
        this.showpswchange = true;
        this.showmymoney = false;
    },
    mymoney() {
      this.showinfochange = false;
        this.showques = false;
        this.showpswchange = false;
        this.showmymoney = true;
    },
    //revques() {
      //this.$router.push({ path: "../app/questionaire" });
    //}
  }
};
</script>
<style scoped>
#container {
  height: 450px;
  margin-top: 10px;
  background-color: white;
  border: 2px solid;
  border-radius: 10px;
  border-color: #f4f4f4;
  padding: 0px;
  margin-bottom: 10px;
  overflow: hidden;
}
#userinfo {
  background-color: #f6f6f6;
    position: absolute;
  top:65px;
  bottom:0px;
  right:0px;
  left:0;
  width:100%;
  overflow-y:hidden; 
  overflow:hidden;
}
.el-header {
  background-color: #b3c0d1;
  color: #333;
  line-height: 60px;
}
.row{
  height:100%;
}
.el-aside {
  color: #333;
}
div{
  height:100%;
}
</style>