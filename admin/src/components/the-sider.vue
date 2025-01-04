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
        <a-menu-item key="/about"><router-link to="/about">关于</router-link></a-menu-item>
        <a-menu-item key="/station"><router-link to="/station">车站管理</router-link></a-menu-item>
        <a-menu-item key="/train"><router-link to="/train">车次管理</router-link></a-menu-item>
        <a-menu-item key="/train-station"><router-link to="/train-station">火车管理</router-link></a-menu-item>
        <a-menu-item key="/train_carriage"><router-link to="/train_carriage">车厢管理</router-link></a-menu-item>
        <a-menu-item key="/train_seat"><router-link to="/train_seat">座位管理</router-link></a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="sub2">
        <template #title>
              <span>
                <laptop-outlined />
                subnav 2
              </span>
        </template>
        <a-menu-item key="5">option5</a-menu-item>
        <a-menu-item key="6">option6</a-menu-item>
        <a-menu-item key="7">option7</a-menu-item>
        <a-menu-item key="8">option8</a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="sub3">
        <template #title>
              <span>
                <notification-outlined />
                subnav 3
              </span>
        </template>
        <a-menu-item key="9">option9</a-menu-item>
        <a-menu-item key="10">option10</a-menu-item>
        <a-menu-item key="11">option11</a-menu-item>
        <a-menu-item key="12">option12</a-menu-item>
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
