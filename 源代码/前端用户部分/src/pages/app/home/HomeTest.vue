<!--老用户首页基金推荐-->
<template>
    <div class='lcard'>
    <carousel></carousel>
        <div  id="home2">
        <table class="table table-hover table-condensed table-striped" style="table-layout:fixed;">
            <caption>基金推荐<span style="font-size:20px"> 👉请选择五个您喜爱的基金，我们帮您生成合适的组合哦~</span></caption>            
            <thead>
                <tr>
                    <th v-for="i in lhead">{{ i }}</th>
                </tr>
            </thead>
            <tbody>
                <tr 
                v-for="i in ldata" @click='whenclick($event)' 
                >
                    <td v-for="j in i">{{ j }}</td>
                </tr>
            </tbody>
        </table>
        <div>
            <button type="button" class="btn btn-primary btn-lg" style="float: right; margin-top: -20px;" @click="submit()">我选好了~</button>
        </div>
        </div>
    </div>
</template>
<script>
import Vue from 'vue'
import carousel from './carousel'
export default {
    data() {
        return {
            lhead: ['基金代码','基金名称','创立时间','基金类型','基金规模','管理公司','管理员','ID'],
            ldata:[],
            datachoose:[],
            url: "/apis/fund/user/recommendFund"
        }
    },
    methods: {
        whenclick: function(e){
            var obj = e.currentTarget.lastChild.innerHTML;
            var index = jQuery.inArray(obj,this.datachoose);
            if(-1===index){
                this.datachoose.push(obj);
                e.currentTarget.classList.add("lactive");
            }
            else{
                this.datachoose.splice(index,1);
                e.currentTarget.classList.remove("lactive");
            }
        },
        submit: function(){
            alert(this.datachoose);
            //调用后端接口，返回数据，将数据传到combinedetails页面
            this.$router.push({name:'CombineDetails',params:{fundId: "1"}})
        },
        getData: function(){
            this.$axios.get(this.url,{
              params:{
                  'customerId':this.customerId
              },
            //   headers:{
            //       'token':'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiNTc5Iiwi5raI6LS56ICFIl19.WOVw5iHNnwVuBh2lTw1ibfSXoaCvQscBR35jMVI1FfM'
            //   }
          }).then((result) => {
                // console.log(result.data.result);
                var r = result.data.result;
                  this.tableData = result.data.result;
                for(var i=0;i<r.length;i++){
                    // ['ID',基金代码','基金名称','创立时间','基金类型','基金规模','管理公司','管理员']
                    Vue.set(this.ldata,i,[
                        
                        r[i].fundCode,
                        r[i].fundName,
                        r[i].startTime,
                        r[i].type,
                        r[i].scale,
                        r[i].managerCompany,
                        r[i].manager,
                        r[i].fundId
                    ])
                    // console.log(r[i].fundCode)
                }
          }).catch((err) => {
              console.log("Error occured in hometest!");
          });
        }
    },
    mounted() {
        this.getData();
    },
    components: {
        carousel
    },
    computed: {
        customerId:function(){
            return localStorage.getItem('customerId');
        }
    },
} 
</script>
<style scoped>
    
    .table-hover > tbody > tr:hover > td,
    .table-hover > tbody > tr:hover > th {
        background-color: paleturquoise;
    }
    .table-hover > tbody > tr:active > td,
    .table-hover > tbody > tr:active > th  {
        background-color: cadetblue;
    }
    #home2{
        padding:10px 15px 40px 45px;
        border:1px solid #aaa;
        border-radius:10px;
        background-color:transparent
    }
    caption{
        font-size: xx-large;
        font-family: KaiTi;
    }
    .lcard{
        width:86%;
        margin-left:7%;
        margin-top:50px;
        margin-bottom: 20px;
        font-size:15px;
    }
    .lactive{
        background-color: cadetblue !important;
    }
</style>