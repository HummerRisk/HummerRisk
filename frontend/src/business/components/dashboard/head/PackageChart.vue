<template>
  <div>
    <hr-chart :options="options" :width="740" :height="220"></hr-chart>
  </div>
</template>

<script>
import echarts from 'echarts';
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
export default {
  name: "PackageChart",
  components: {
    HrChart,
    echarts,
  },
  props: {
    data: {},
  },
  data() {
    return {
      options: {},
    }
  },
  methods: {
    init() {
      this.options = {
        xAxis: {
          type: 'category',
          data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: [120, 200, 150, 80, 70, 110, 130],
            type: 'bar'
          }
        ]
      };
    },
  },
  created() {
    this.init();
  },
}

</script>

<style scoped>

</style>
