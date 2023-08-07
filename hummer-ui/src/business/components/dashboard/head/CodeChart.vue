<template>
  <div>
    <hr-chart :options="options" :width="width" :height="256"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
export default {
  name: "CodeChart",
  components: {
    HrChart,
  },
  props: {
    data: {},
    width: [Number, String],
  },
  watch: {
    width() {
    },
  },
  data() {
    return {
      options: {},
    }
  },
  methods: {
    init() {
    },
  },
  created() {
    this.init();
  },
}

</script>

<style scoped>
</style>
