<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu
        v-model:selectedKeys="selectedKeys2"
        v-model:openKeys="openKeys"
        mode="inline"
        :style="{ height: '100%', borderRight: 0 }"
    >
      <a-sub-menu key="sub1">
        <template #title>
              <span>
                <user-outlined />
                subnav 1
              </span>
        </template>
        <a-menu-item key="/welcome"><router-link to="/welcome">欢迎</router-link></a-menu-item>
        <a-menu-item key="/passenger"><router-link to="/passenger">乘车人</router-link></a-menu-item>
        <a-menu-item key="/ticket"><router-link to="/ticket">选票</router-link></a-menu-item>
        <a-menu-item key="/my-ticket"><router-link to="/my-ticket">我的车票</router-link></a-menu-item>
      </a-sub-menu>
    </a-menu>
  </a-layout-sider>

</template>

<script>
import {defineComponent, ref, watch} from "vue";
import Router from "@/router";
export default defineComponent({
  name: 'the-sider',
  data() {
    const selectedKeys2 = ref([]);
    const openKeys = ref(['sub1']);
    watch(()=>Router.currentRoute.value.path, (newValue)=>{
      console.log(newValue);
      selectedKeys2.value=[]
      selectedKeys2.value.push(newValue)
    }, {immediate: true})
    return {
      selectedKeys2,
      openKeys
    }
  }
})
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
