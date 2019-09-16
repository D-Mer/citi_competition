<template>
  <div>
    <Nav></Nav>
    <router-view/>
  </div>
</template>

<script>
import Nav from '@/components/Nav'
export default {
  name: 'Head',
  components:{
    Nav
  }
}
</script>

<style>
</style>
