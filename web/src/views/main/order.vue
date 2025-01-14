<template>
<p>
  {{dailyTrainTicket}}
</p>
</template>

<script>
import {defineComponent} from "vue";

export default defineComponent({
  name: 'order-view',
  setup() {
    const dailyTrainTicket = SessionStorage.get(SESSION_ORDER) || {};
    return {dailyTrainTicket}
  }
});
</script>

<style scoped>

</style>