<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu
        v-model:selectedKeys="selectedKeys2"
        :openKeys="['batch', 'base','business']"
        mode="inline"
        :style="{ height: '100%', borderRight: 0 }"
    >
      <a-menu-item key="/welcome"><router-link to="/welcome">欢迎</router-link></a-menu-item>
      <a-menu-item key="/about"><router-link to="/about">关于</router-link></a-menu-item>
      <a-sub-menu key="/member">
        <template #title>
          <span>
            <UnorderedListOutlined/>
            会员管理
          </span>
        </template>
        <a-menu-item key="/member/member"><router-link to="/member/ticket"><MenuUnfoldOutlined/>会员车票</router-link></a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="business">
        <template #title>
          <span>
            <user-outlined />
            每日管理
          </span>
        </template>
        <a-menu-item key="/business/confirm_order"><router-link to="/business/confirm-order">订单信息</router-link></a-menu-item>
        <a-menu-item key="/business/daily_train_ticket"><router-link to="/business/daily-train-ticket">余票</router-link></a-menu-item>
        <a-menu-item key="/business/daily_train"><router-link to="/business/daily-train">每日车次</router-link></a-menu-item>
        <a-menu-item key="/business/daily_train_station"><router-link to="/business/daily-train-station">每日车站</router-link></a-menu-item>
        <a-menu-item key="/business/daily_train_carriage"><router-link to="/business/daily-train-carriage">每日车厢</router-link></a-menu-item>
        <a-menu-item key="/business/daily_train_seat"><router-link to="/business/daily-train-seat">每日座位</router-link></a-menu-item>
        <a-menu-item key="/business/sk_token"><router-link to="/business/sk-token">令牌余量</router-link></a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="base">
        <template #title>
              <span>
                <user-outlined />
                基础数据
              </span>
        </template>
        <a-menu-item key="/base/station"><router-link to="/base/station">车站管理</router-link></a-menu-item>
        <a-menu-item key="/base/train"><router-link to="/base/train">车次管理</router-link></a-menu-item>
        <a-menu-item key="/base/train-station"><router-link to="/base/train-station">火车管理</router-link></a-menu-item>
        <a-menu-item key="/base/train_carriage"><router-link to="/base/train_carriage">车厢管理</router-link></a-menu-item>
        <a-menu-item key="/base/train_seat"><router-link to="/base/train_seat">座位管理</router-link></a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="batch">
        <template #title>
          <span>
            <UnorderedListOutlined />
            跑批管理
          </span>
        </template>
        <a-menu-item key="/batch/job">
          <router-link to="/batch/job">
            <MenuUnfoldOutlined /> &nbsp; 任务管理
          </router-link>
        </a-menu-item>
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
    watch(()=>Router.currentRoute.value.path, (newValue)=>{
      console.log(newValue);
      selectedKeys2.value=[]
      selectedKeys2.value.push(newValue)
    }, {immediate: true})
    return {
      selectedKeys2
    }
  }
})
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
