<template>
    <div>
        <div class="tip">您已经进行过风险测评，若您的条件有变，请重新填写问卷</div>
        <el-button type="primary" @click="revise" id='but'>重填问卷</el-button>
    </div>
</template>
<script>
export default {
    name: 'Question',
    methods:{
        revise(){
            this.$router.push({ path: "../app/questionaire" });
        }
    }
}
</script>
<style scoped>
.tip{
    height:70px;
    padding:20px 0 20px 20px;
}
#but{
    margin-left:20px;
}
</style>