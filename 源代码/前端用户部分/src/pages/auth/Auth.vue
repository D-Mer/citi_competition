<template>
<div id="back">
<div :style="back" id="backs">
  <div id="register" style="text-align:center ">
    <h1>RoboAdviser</h1>
    <p>基于机器学习的智能基金投顾系统</p>
    <el-form action v-if="isLogin" style="width: 270px;margin-top:20px" >
      <el-input
        type="text"
        name="username"
        placeholder="请输入用户名"
        v-model="username"
        @keyup.enter.native="real_login()"
      />
      <br />
      <el-input
        type="password"
        name="password"
        placeholder="请输入密码"
        v-model="password"
        @keyup.enter.native="real_login()"
        style="margin-top:20px;"
      />
      <div style="margin-top:20px;">
      <el-button @click="real_login()">登录</el-button>
      <el-button @click="sign()">注册</el-button>
      <el-button type="text" @click="forget()">管理员登陆</el-button>
      </div>
    </el-form>
    <el-form action v-else-if="isRegister" style="width: 270px;">
      <el-input style="margin-bottom:10px;" type="text" placeholder="请输入用户名" v-model="username" />
      <br />
      <el-input style="margin-bottom:10px;" type="password" placeholder="请输入密码" v-model="password" />
      <br />
      <el-input style="margin-bottom:10px;" type="password" placeholder="请再次输入密码" v-model="confirmPassword" />
      <br />
      <el-input style="margin-bottom:10px;" type="text" placeholder="请输入邮箱" v-model="email" />
      <br />
      <el-button @click="createUser()">确认注册</el-button>
      <el-button @click="login()">取消</el-button>
    </el-form>
    <el-form action v-else>
      <el-input type="text" placeholder="请输入用户名" v-model="username" />
      <br />
      <el-button @click="reset()">确定</el-button>
      <el-button @click="login()">取消</el-button>
    </el-form>
  </div>
  </div>
</div>
</template>

<script>
export default {
  name: "Auth",
  data() {
    return {
      isLogin: true,
      isRegister: false,
      username: "",
      password: "",
      confirmPassword: "",
      email: "",
      loginUrl: "/apis/user/login",
      verifyUsernameUrl: "/apis/user/verifyUsername",
      registerUrl: "/apis/user/register",
      back: {
        backgroundImage: "url(" + require("@/../static/6.jpg") + ")",
        backgroundRepeat: "no-repeat",
        backgroundSize:"100% 100%",
      }
    };
  },
  methods: {
    real_login() {
      // todo
      if (this.loginVerify()) {
        this.$axios
          .post(this.loginUrl, {
            username: this.username, // 参数 firstName
            password: this.password,
            email: null // 参数 lastName
          })
          .then(response => {
            if (!response.data.status) {
              alert("用户名或者密码错误");
            } else {
              var tmp = response.data.result;
              var homePage =
                response.headers.isoldcustomer == "old" ? "hometest" : "home";
              // console.log(response.headers);
              localStorage.setItem("customerId", tmp.customerId);
              localStorage.setItem("balance", tmp.balance);
              localStorage.setItem("token", response.headers.token);
              localStorage.setItem("email", tmp.email);
              localStorage.setItem("homePage", homePage);
              this.$router.push("/app/" + homePage);
            }
          })
          .catch(error => {
            // alert("用户名或者密码错误");
            console.log(error);
          });
      }
    },
    loginVerify() {
      console.log("喵喵喵？");
      var flag = true;
      if (this.username === "") {
        alert("用户名不能为空！");
        flag = false;
      } else if (this.password === "") {
        alert("密码不能为空！");
        flag = false;
      }
      return flag;
    },
    sign() {
      this.isLogin = false;
      this.isRegister = true;
      this.username = "";
      this.password = "";
    },
    createUser() {
      if (this.username === "") {
        alert("用户名不能为空！");
      } else if (this.password === "") {
        alert("密码不能为空！");
      } else if (this.email === "") {
        alert("邮箱不能为空！");
      } else if (!/^[A-Za-z0-9][A-Za-z0-9_.]{4,14}$/g.test(this.username)) {
        alert(
          "用户名需为字母或者数字开头；允许字母，数字，下划线和点(.)；5~15位之间"
        );
      } else if (
        !/^(?![A-Za-z0-9_]+$)(?![a-z0-9\\W]+$)(?![A-Za-z\\W]+$)(?![A-Z0-9\\W]+$).{8,20}$/g.test(
          this.password
        )
      ) {
        alert(
          "密码需为包含大写字母、小写字母、数字、特殊符号（不是字母，数字，下划线，汉字的字符）的8位以上组合"
        );
      } else if (
        !/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/g.test(this.email)
      ) {
        alert("邮箱格式不正确！");
      } else if (this.password !== this.confirmPassword) {
        alert("两次密码输入不一致！");
        this.confirmPassword = "";
        this.password = "";
      } else {
        // todo
        this.$axios
          .post(this.registerUrl, {
            username: this.username, // 参数 firstName
            password: this.password,
            email: this.email // 参数 lastName
          })
          .then(response => {
            if (!response.data.status) {
              alert(response.data.result);
            } else {
              alert("注册成功！");
              this.$router.push("/app/" + homePage);
            }
          })
          .catch(error => {
            // alert("用户名或者密码错误");
            console.log(error);
          });
        this.isLogin = true;
        this.isRegister = false;
        this.confirmPassword = "";
        this.password = "";
      }
    },
    login() {
      this.isLogin = true;
      this.isRegister = false;
      this.username = "";
      this.password = "";
      this.confirmPassword = "";
      this.email = "";
    },
    forget() {
      this.isLogin = false;
      this.isRegister = false;
      this.username = "";
      this.password = "";
      this.confirmPassword = "";
    },
    reset() {
      // todo
      if (this.username === "123") {
        alert("已向您的邮箱发送重置密码邮件！");
      } else {
        alert("用户名不存在！");
        this.username = "";
      }
    }
  }
};
</script>

<style scoped>
#register{
padding:20px 30px 20px 30px;
  margin-left: 250px;
  background-color: white;
  border:2px solid #eee;
  border-radius: 15px;
}
#backs {
   display: flex;
        align-items: center;
  height:100%;
  width:100%
}
h1{
  font-size: 40px;
  font-weight:bold;
}
#back{
  position: absolute;
  top:0;
  bottom:0 !important;
  left:0;
  right:0;
}
</style>
