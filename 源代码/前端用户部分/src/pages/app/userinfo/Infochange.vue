<template>
  <div>
    <el-form
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
      label-width="100px"
      class="demo-ruleForm"
    >
      <el-form-item label="用户名" prop="username" style="width:350px;">
        <el-input v-model="ruleForm.username"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email" style="width:350px;">
        <el-input v-model="ruleForm.email"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">修改信息</el-button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <el-button type="primary" @click="verify">邮箱验证</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  name: "Infochange",
  data() {
    return {
      labelPosition: "right",
      rules: {
        username: [
          { required: false, message: "请输入用户名", trigger: "blur" },
          { min: 4, max: 16, message: "长度在 4 到 16 个字符", trigger: "blur" }
        ],
        email: [{ required: true, message: "请输入邮箱", trigger: "blur" }],
        else: [{ required: false, message: "请输入其他信息", trigger: "blur" }]
      },
      ruleForm: {
        username: "",
        email: "",
        else: ""
      }
    };
  },
  methods: {
    onSubmit() {
      var cusId = localStorage.getItem("customerId");
      cusId = parseInt(cusId);
      this.axios
        .post("/apis//user/changeEmail", null, {
          params: {
            userId: cusId,
            newEmail: this.ruleForm.email
          }
        })
        .then(result => {
          console.log(result);
          this.$router.push({ path: "../app/userinfo" });
          alert("修改成功！");
        })
        .catch(err => {
          console.log(err);
        });
    },
    verify() {
      var cusId = localStorage.getItem("customerId");
      cusId = parseInt(cusId);
      this.axios.defaults.headers.post["Content-Type"] = "application/json";
      this.axios
        .post("/apis//user/verify", cusId)
        .then(result => {
          if(result.data.status==false){
            alert("请勿重复验证！")
          }
          else{
          alert("请注意接收邮件!");}
          console.log(result);
        })
        .catch(err => {
          console.log(err);
        });
    }
  },
  mounted() {
    this.axios.defaults.headers.post["Content-Type"] =
      "application/x-www-form-urlencoded";
  }
};
</script>
<style scoped>
.text {
  width: 300px;
}
</style>