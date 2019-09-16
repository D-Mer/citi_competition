<template>
  <div class="modal-backdrop row">
    <div class="col-md-4"></div>
    <div id="Modal" class="col-md-4">
      <div class="modal-header">
        <h3 id="header">购买</h3>
      </div>
      <div class="modal-body">
        <div class="input-group" style="margin-top:30px">
          <span class="input-group-addon">购买金额</span>
          <input name="a" type="text" class="form-control" v-model="amount" />
        </div>&nbsp;
        <div style="margin-top:30px">钱包余额:{{balance}}</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn-close" @click="closeSelf">关闭</button>
        <button type="button" class="btn-confirm" @click="confirm">确认</button>
      </div>
    </div>
  </div>
</template>

<style>
.modal-backdrop {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-color: rgba(0, 0, 0, 0.3);
}
#Modal {
  margin-top: 100px;
  background-color: #fff;
  box-shadow: 2px 2px 20px 1px;
  overflow-x: auto;
  display: flex;
  flex-direction: column;
  border-radius: 16px;
  height: 350px;
}
.modal-header {
  border-bottom: 1px solid #eee;
  color: #313131;
  justify-content: space-between;
  padding: 15px;
  display: flex;
}
.modal-footer {
  border-top: 1px solid #eee;
  justify-content: flex-end;
  padding: 15px;
  display: flex;
}
.modal-body {
  position: relative;
  padding: 20px 10px;
}
.btn-close,
.btn-confirm {
  border-radius: 8px;
  margin-left: 16px;
  width: 56px;
  height: 36px;
  border: none;
  cursor: pointer;
}
.btn-close {
  color: #313131;
  background-color: transparent;
}
.btn-confirm {
  color: #fff;
  background-color: #2d8cf0;
}
</style>

<script>
/* eslint-disable */
export default {
  name: "Modal",
  props: ["cosId"],
  data() {
    return {
      balance: 10000,
      amount:0
    };
  },
  mounted() {
    this.balance = localStorage.getItem("balance");
  },
  methods: {
    closeSelf() {
      this.$emit("on-cancel");
    },
    confirm() {
      var cusId = localStorage.getItem("customerId");
      cusId = parseInt(cusId);
      console.log(this.amount);
      console.log(this.balance)
      if(parseInt(this.balance)<parseInt(this.amount)){
        alert("余额不足！");
      }
      else{
      this.axios
        .get("/apis//finance/buyFundComposition", {
          params: {
            customerId: cusId,
            compositionId: this.cosId,
            purchaseAmount: this.amount
          }
        })
        .then(result => {
          console.log(result)
          alert("购买成功！")
          this.$router.push({ path: "../app/MycomposeList" });
        })
        .catch(err => {
          console.log(err);
        });
    }}
  }
};
</script>