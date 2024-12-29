<template>
  <a-layout>
    <the-header></the-header>
    <a-layout>
      <the-sider></the-sider>
      <a-layout style="padding: 0 24px 24px">
        <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >
          <router-view></router-view>
        </a-layout-content>
      </a-layout>
    </a-layout>
  </a-layout>
</template>
<script>
import TheHeader from "@/components/the-header.vue";
import TheSider from "@/components/the-sider.vue";
import {defineComponent} from "vue";
export default defineComponent({
  components: {
    TheSider,
    TheHeader,
  },
  setup(){
      // axios.get("/member/member/count").then(response=>{
      //   let data=response.data;
      //   if (data.success){
      //     count.value=data.content;
      //   }else{
      //     notification.error({ description: data.message });
      //   }
      // });
      return {
      };
  }
})
</script>
<style scoped>
#components-layout-demo-top-side-2 .logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  background: rgba(255, 255, 255, 0.3);
}

.ant-row-rtl #components-layout-demo-top-side-2 .logo {
  float: right;
  margin: 16px 0 16px 24px;
}

.site-layout-background {
  background: #fff;
}
</style>