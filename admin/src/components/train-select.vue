<template>
  <a-select v-model:value="trainCode" show-search allowClear
            :filter-option="filterTrainCodeOption"
            @change="OnChange" placeholder="请选择车次"
            :style="'width: ' + localWidth"
  >
    <a-select-option v-for="item in trains" :key="item.code" :value="item.code" :label="item.code+item.start+item.end">
      {{item.code}} | {{item.start}} ~ {{item.end}}
    </a-select-option>
  </a-select>
</template>

<script>
import {defineComponent, onMounted, ref, watch} from "vue";
import axios from "axios";
export default defineComponent({
  name: "train-select-view",
  props:["modelValue", "width"],
  emits:["update:modelValue", "change"],
  setup(props, {emit}){
    const trainCode = ref();
    const trains = ref([]);
    const localWidth = ref(props.width);
    if (Tool.isEmpty(props.width)) {
      localWidth.value = "100%";
    }
    watch(() => props.modelValue, ()=>{
      console.log("props.modelValue", props.modelValue);
      trainCode.value = props.modelValue;
    }, {immediate: true});
    /**
     * 查询所有的车次，用于车次下拉框
     */
    const queryAllTrain = () => {
      let list=SessionStorage.get(SESSION_ALL_TRAIN)
      if (Tool.isNotEmpty(list)){
        console.log("从缓存中获取车次列表")
        trains.value = list;
      }else {
        axios.get("/business/admin/train/query-all").then((response) => {
        let data = response.data;
        if (data.success) {
          trains.value = data.content;
          console.log("SESSION_ALL_TRAIN保存缓存")
          SessionStorage.set(SESSION_ALL_TRAIN, data.content);
        } else {
          notification.error({description: data.message});
        }
      });
      }


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
      let train = trains.value.filter(item => item.code === value)[0];
      if (Tool.isEmpty(train)) {
        train = {};
      }
      emit('change', train);
    };

    onMounted(() => {
      queryAllTrain();
    });
    return {
      trainCode,
      trains,
      filterTrainCodeOption,
      OnChange,
      localWidth
    }
  }
})

</script>
<style scoped>

</style>