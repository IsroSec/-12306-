<template>
  <a-layout-header class="header">
    <div class="logo" />
    <div style="float: right;color: white">
      欢迎使用控制台
    </div>
    <a-menu
        v-model:selectedKeys="selectedKeys1"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/welcome"><router-link to="/welcome">欢迎</router-link></a-menu-item>
      <a-menu-item key="/about"><router-link to="/about">关于</router-link></a-menu-item>
    </a-menu>
  </a-layout-header>
</template>

<script>
import {defineComponent, ref, watch} from "vue";
import Router from "@/router";
export default defineComponent({
  name: 'the-header',
  data() {
    const selectedKeys1 = ref(['1']);
    watch(()=>Router.currentRoute.value.path, (newValue)=>{
      console.log(newValue);
      selectedKeys1.value=[]
      selectedKeys1.value.push(newValue)
    }, {immediate: true})
    return {
    selectedKeys1
    }
  }
})
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
