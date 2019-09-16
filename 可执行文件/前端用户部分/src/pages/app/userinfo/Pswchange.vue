<template>
  <div>
  <div>
    以下密码需包括大小写字母，数字，以及特殊符号
  </div>
    <el-form
      :model="ruleForm"
      status-icon
      :rules="rules"
      ref="ruleForm"
      label-width="100px"
      class="demo-ruleForm"
    >
      <el-form-item label="旧密码" prop="oldpass" style="width:350px;">
        <el-input v-model="ruleForm.oldpass"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="pass" style="width:350px;">
        <el-input type="password" v-model="ruleForm.pass" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="checkPass" style="width:350px;">
        <el-input type="password" v-model="ruleForm.checkPass" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm('ruleForm')">提交</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  name: "Pswchange",
  data() {
    var checkoldpass = (rule, value, callback) => {
      if (!value) {
        this.judge = false;
        return callback(new Error("旧密码不能为空"));
      } else {
        this.judge = true;
      }
    };
    var validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请输入密码"));
        this.judge = false;
      } else {
        if (this.ruleForm.checkPass !== "") {
          this.$refs.ruleForm.validateField("checkPass");
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
        this.judge = false;
      } else if (value !== this.ruleForm.pass) {
        callback(new Error("两次输入密码不一致!"));
        this.judge = false;
      } else {
        callback();
      }
    };
    return {
      userId:0,
      judge: true,
      ruleForm: {
        pass: "",
        checkPass: "",
        oldpass: ""
      },
      rules: {
        pass: [{ validator: validatePass, trigger: "blur" }],
        checkPass: [{ validator: validatePass2, trigger: "blur" }],
        oldpass: [{ validator: checkoldpass, trigger: "blur" }]
      }
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
        } else {
          return false;
        }
      });
      if (this.judge == true) {
        console.log(this.ruleForm.pass);
        console.log(this.ruleForm.oldpass);
        this.axios
          .post("/apis//user/changePassword", null, {
            params: {
              userId: this.userId,
              newPassword: this.ruleForm.pass,
              oldPassword: this.ruleForm.oldpass
            }
          })
          .then(result => {
            console.log(result);
            if(result.data.result=="密码格式错误"){
              alert("密码格式错误，请重新输入！")
            }
            else{
            this.$router.push({ path: "../auth" });
            alert("修改成功！");
            }
          })
          .catch(err => {
            console.log(err);
          });
      } else {
        alert("请按正确要求输入！");
      }
    }
  },
    mounted() {
    this.axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';
                      var cusId = localStorage.getItem("customerId");
            cusId = parseInt(cusId);
            this.userId = cusId
    }
};
</script>
<style scoped>
</style>