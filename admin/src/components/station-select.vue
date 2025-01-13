<template>
  <a-select v-model:value="name" show-search allowClear
            :filter-option="filterTrainCodeOption"
            @change="OnChange" placeholder="请选择车站"
            :style="'width: ' + localWidth"
  >
    <a-select-option v-for="item in stations" :key="item.name" :value="item.name" :label="item.name + item.namePinyin + item.namePy">
      {{item.name}} | {{item.namePinyin}} ~ {{item.namePy}}
    </a-select-option>
  </a-select>
</template>

<script>
import {defineComponent, onMounted, ref, watch} from "vue";
import axios from "axios";
export default defineComponent({
  name: "station-select-view",
  props:["modelValue", "width"],
  emits:["update:modelValue", "change"],
  setup(props, {emit}){
    const name = ref();
    const stations = ref([]);
    const localWidth = ref(props.width);
    if (Tool.isEmpty(props.width)) {
      localWidth.value = "100%";
    }
    watch(() => props.modelValue, ()=>{
      console.log("props.modelValue", props.modelValue);
      name.value = props.modelValue;
    }, {immediate: true});
    /**
     * 查询所有的车次，用于车次下拉框
     */
    const queryAllTrain = () => {
      axios.get("/business/admin/station/query-all").then((response) => {
        let data = response.data;
        if (data.success) {
          stations.value = data.content;
        } else {
          notification.error({description: data.message});
        }
      });

    };

    /**
     * 车次下拉框筛选
     */
    const filterTrainCodeOption = (input, option) => {
      console.log(input, option);
      return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    };
    const OnChange=(value)=>{
      emit('update:modelValue', value);
      let train = stations.value.filter(item => item.code === value)[0];
      if (Tool.isEmpty(train)) {
        train = {};
      }
      emit('change', train);
    };

    onMounted(() => {
      queryAllTrain();
    });
    return {
      name,
      stations,
      filterTrainCodeOption,
      OnChange,
      localWidth
    }
  }
})

</script>
<style scoped>

</style>