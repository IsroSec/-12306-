<template>
  <a-button type="primary" @click="showModel">新增</a-button>
  <a-modal v-model:visible="visible" title="新增乘客" @ok="handleOk" @cancel="handleCancel"
  ok-text="保存" cancel-text="取消">
    <a-form :model="passenger" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="乘客姓名">
        <a-input v-model:value="passenger.name" placeholder="请输入乘客姓名" />
      </a-form-item>
      <a-form-item label="身份证">
        <a-input v-model:value="passenger.idCard" />
      </a-form-item>
      <a-form-item label="旅客类型">
        <a-select v-model:value="passenger.type">
          <a-select-option value="1">成人</a-select-option>
          <a-select-option value="2">儿童</a-select-option>
          <a-select-option value="3">学生</a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script>
import { defineComponent,ref } from 'vue';
import axios from "axios";
import {notification} from "ant-design-vue";

export default defineComponent({
  setup() {
    const visible = ref(false);
    let passenger = ref({
      id: undefined,
      memberId: undefined,
      name: undefined,
      idCard: undefined,
      type: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const showModel=()=>{
      visible.value=true;
    }
    const handleOk=()=>{
      axios.post("/member/passenger/save",passenger.value).then(response=>{
        let data=response.data
        if (data.success){
          notification.success({ description: '新增乘客成功！' })
          visible.value=false;
        }else {
          notification.error({ description: data.message })
        }
      })
    }

    return {
      passenger,
      visible,
      showModel,
      handleOk
    };
  },
});
</script>
<style>
</style>
