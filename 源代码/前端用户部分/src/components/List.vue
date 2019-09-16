<!--通用List-->
<template>
    <div class='list1'>
        <table class="table table-hover table-condensed table-striped" style="table-layout:fixed;">
            <caption v-html="ltitle"></caption>
            <thead>
                <tr>
                    <th v-for="i in lhead">{{ i }}</th>
                    <th v-if="bfunc!=undefined && bfunc!=null && bfunc.length!=0">选项</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="i in showpage" @click='whenclick($event)'>
                    <td v-for="j in i">{{ j }}</td>
                    <td style="width:15%">
                        <button type="button"  class="btn btn-default btn-sm" v-for="(item, index) in bfunc" @click.stop='item($event)'>{{ index }}</button>
                    </td>
                </tr>
            </tbody>
        </table>
        <!--<ul class="pagination">
            <li @click="previous()"><a href="#">&laquo;</a></li>
            <li 
                :class="{'active':false}"
                v-for="i in Math.ceil(ldata.length/rowsperpage)" 
                :id="myid+i" 
                @click="changepage(i)">
                <a href="#">{{ i }}</a>
            </li>
            <li @click="next()"><a href="#">&raquo;</a></li>
        </ul>-->
        <div class="pagin">
            <el-pagination
                :current-page.sync="page"
                :page-size="rowsperpage"
                layout="prev, pager, next, jumper"
                :total="totalpages">
            </el-pagination>
        </div>

    </div>
</template>
<script>
export default {
    name: 'List',
    props:['lhead','ldata','bfunc','whenclick','myid','rowsperpage','ltitle'],
    /*
    lhead: ['近一年收益率','风险等级','投资期限','交易费用','起投金额','基金规模','基金公司']
    ldata:[ ['5%','3', ... , 'X公司'] ,
             ...... , 
           ['4.5%','5', ... ,'A公司']
         ]
    bfunc:{
        '购入':function(){alert("购入");},
        '卖出':function(){alert("卖出");}
    }
    whenclick: function(){alert("喵喵喵？");}
    */
    data() {
        return {
            page:1
        }
    },
    methods: {
        
        // changepage: function(i){
        //     this.page = i;
        //     let sil = $("#"+this.myid+i).siblings();
        //     for(var n=1;n<sil.length;n++){
        //         sil[n].classList.remove("active");
        //     }
        //     $("#"+this.myid+i).addClass('active');
        // },
        // previous: function(){
        //     if(this.page>1)this.changepage(this.page-1);
        // },
        // next: function(){
        //     if(this.page<Math.ceil(this.ldata.length/this.rowsperpage))this.changepage(this.page+1);
        // },
    },
    mounted() {
        // $("#"+this.myid+"1").addClass('active');
    },
    computed: {
        totalpages: function(){
            return this.ldata.length;
        },
        showpage: function (){
            return this.ldata.slice((this.page-1)*this.rowsperpage,this.page*this.rowsperpage);
        }
    }
} 
</script>
<style scoped>
    .table-striped > tbody > tr:nth-child(2n+1) > td, 
    .table-striped > tbody > tr:nth-child(2n+1) > th {
        background-color: rgb(242, 245, 247);
    }
    .table-hover > tbody > tr:hover > td,
    .table-hover > tbody > tr:hover > th {
        background-color: paleturquoise;
    }
    button{
        margin-left:2px;
        margin-right:2px;
    }
    /*.pagination{
        float:right;
        margin-right:-14px;
        margin-top:-10px;
    }
    .pagination > li:first-child > a, .pagination > li:first-child > span {
        margin-left: 0;
        border-top-left-radius: 50%;
        border-bottom-left-radius: 50%;
    }
    .pagination > li:last-child > a, .pagination > li:last-child > span {
        border-top-right-radius: 50%;
        border-bottom-right-radius: 50%;
    }
    .pagination > li > a, .pagination > li > span{
        border: 1px solid #aaa;
    }*/
    .list1{
        padding:10px 15px 10px 45px;
        border:1px solid #aaa;
        border-radius:10px;
        background-color:transparent
    }
    caption{
        font-size: xx-large;
        font-family: KaiTi;
    }
    .pagin{
        border:1px solid #aaa;
        border-radius:20px;
        padding:5px 10px 5px 10px;
        float:right;
        background-color:white;
        margin-top:-10px
    }
</style>