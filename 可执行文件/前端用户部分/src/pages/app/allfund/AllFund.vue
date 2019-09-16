<!--新用户首页组合推荐-->
<template>
    <div>
        <div class="lcard">
            <listWithAxios
            :listprops="listprops" 
            :url="reqUrl"
            :pageSize='6'>
        </listWithAxios>
        </div>
    </div>
</template>
<script>
import listWithAxios from './listWithAxios'
export default {
    name: 'AllFund',
    data() {
        return {
            listprops:[
                {
                    listprop:'fundCode',
                    listlabel:'基金代码'
                },
                {
                    listprop:'fundName',
                    listlabel:'基金名称'
                },
                {
                    listprop:'abbreviation',
                    listlabel:'基金简称'
                },
                {
                    listprop:'startTime',
                    listlabel:'建立时间'
                },
                {
                    listprop:'scale',
                    listlabel:'基金规模'
                },
                
                {
                    listprop:'type',
                    listlabel:'基金类型'
                },
                {
                    listprop:'managerCompany',
                    listlabel:'管理公司'
                },
                {
                    listprop:'managerBank',
                    listlabel:'管理银行'
                },
                {
                    listprop:'manager',
                    listlabel:'基金管理员'
                },
                
                
            ],
            reqUrl:'/apis/fund/viewAllFunds',
        }
    },
    methods: {
        
    },
    components:{
        listWithAxios
    }
}
</script>
<style scoped>
    .lcard{
        width:86%;
        margin-left:7%;
        margin-top:50px;
        font-size:18px;
    }
</style>