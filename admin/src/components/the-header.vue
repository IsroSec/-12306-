<template>
  <a-layout-header class="header">
    <div class="logo" />
    <div style="float: right;color: white">您好：{{member.mobile}} &nbsp;&nbsp;
      <router-link to="/login" style="color: white;">
        退出登录
      </router-link></div>
    <a-menu
        v-model:selectedKeys="selectedKeys1"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/welcome"><router-link to="/welcome">欢迎</router-link></a-menu-item>
      <a-menu-item key="/passenger"><router-link to="/passenger">乘车人</router-link></a-menu-item>
    </a-menu>
  </a-layout-header>
</template>

<script>
import {defineComponent, ref, watch} from "vue";
import store from "@/store";
import Router from "@/router";
export default defineComponent({
  name: 'the-header',
  data() {
    const selectedKeys1 = ref(['1']);
    let member= store.state.member;
    watch(()=>Router.currentRoute.value.path, (newValue)=>{
      console.log(newValue);
      selectedKeys1.value=[]
      selectedKeys1.value.push(newValue)
    }, {immediate: true})
    return {
      member,
    selectedKeys1
    }
  }
})
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
