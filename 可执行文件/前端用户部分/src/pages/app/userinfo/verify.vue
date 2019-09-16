<template>
  <div>
    <div id="status">
      <el-progress type="circle" :percentage="100" status="success"></el-progress>
      <div id="tips">您的邮箱正在验证，请稍后</div>
    </div>
  </div>
</template>
<script>
export default {
  name: "Verify",
  mounted() {
    var emails = this.getQueryVariable("email");
    var reemails = emails.split('').reverse().join(''); 
    reemails=reemails.replace("04%","@")
    emails=reemails.split('').reverse().join(''); 
    var codes = this.getQueryVariable("code");
    this.axios
      .get("/apis//user/verifyEmail", {
        params: {
          email: emails,
          code: codes
        },
        headers: {
          token:
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiMTg0MCIsIua2iOi0ueiAhSJdfQ.4vAPswSrJ1_d12pw7ZbBsme-_-LGlN_zwlpoEzVCScE"
        }
      })
      .then(result => {
        console.log(result);
        if ((result.data.state=false)) {
          alert("验证链接错误或者已经过期，请重新验证");
        } else {
          alert("验证成功！请关闭此页面！");
        }
      })
      .catch(err => {
        console.log(err);
      });
  },
  methods: {
    getQueryVariable(variable) {
      var query = window.location.search.substring(1);
      var vars = query.split("&");
      for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] == variable) {
          return pair[1];
        }
      }
      return false;
    }
  }
};
</script>
<style scoped>
#status {
  margin-right: auto;
  margin-left: auto;
  margin-top: 200px;
  width: 250px;
}
#tips {
  margin-top: 20px;
  color: green;
}
</style>