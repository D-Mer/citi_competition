<!--导航栏-->
<template style="margin:0px">
    <div class="container card">
        <div class="row text-center l-size">
            <div class="col-md-9 nav_1">
                <router-link 
                    class="rlink" 
                    v-for= '(u,name,index) in tags' :key="index" 
                    :id="u" 
                    :to="'/app/'+u">
                        {{ name }}
                </router-link>
            </div>
            <div class="col-md-3 nav_1">
            <a class="rlink_2" @click="logout">退出登录</a>
            </div>
        </div>
    </div>
</template>
<script>
export default {
    name: 'Nav',
    data() {
        return {
            homePage:'home'
        }
    },
	methods:{
		logout(){
			localStorage.removeItem("customerId");
			localStorage.removeItem("balance");
			localStorage.removeItem("token");
			localStorage.removeItem('email');			
			localStorage.removeItem('homePage')
			this.$router.push('/auth');
		}
	},
    mounted() {
        this.homePage = localStorage.getItem("homePage");
    },
    computed: {
        tags:function(){
            return {'首页':this.homePage,'所有基金':'allfund','组合详情':'combinedetails','我的组合':'mycomposelist','个人信息':'userinfo'}
        }
    },
} 
</script>
<style scoped>
    .rlink{
        text-decoration:none;
        color: #ddd;
        padding:15px 20px 15px 20px;
        font-family: 'Avenir', Helvetica, Arial, sans-serif;
        border-radius:10px;
    }
    .rlink_2{
        text-decoration:none;
        color: #ddd;
        padding:8px 8px 8px 8px;
        font-family: 'Avenir', Helvetica, Arial, sans-serif;
        border-radius:8px;
    }
    .l-size{
        font-size:large;
    }
    .card{
        padding-top:20px;
        background-color:#1e90ff;
        padding-bottom:20px;
        width:100%;
    }
    
    .rlink:hover{
        background-color: aliceblue;
        color:gray;
    }
    
    .rlink_2:hover{
        background-color: aliceblue;
        color:gray;
    }
    .router-link-active{
        background-color: aliceblue;
        color:#555;
    }
</style>