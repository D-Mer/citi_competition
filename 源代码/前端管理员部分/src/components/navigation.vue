<template>
  <el-container>
    <el-header>
      <el-menu router mode="horizontal"  >
        <el-menu-item index="1" route="/admin/fundList">基金列表</el-menu-item>
        <el-menu-item index="2" route="/admin/adminManage">管理员管理</el-menu-item>
        <el-menu-item index="3" route="/admin/userManage">用户管理</el-menu-item>
        <el-menu-item index="4" route="/admin/modifyPassword">修改密码</el-menu-item>
      </el-menu>
    </el-header>
    <el-main>
      <router-view></router-view>
    </el-main>
  </el-container>

</template>

<script>
  export default {
    name: ''
  }
</script>

<style scoped>

</style>
