<template>
  <div>
    <hr-chart :options="options" :width="1700" :height="280"></hr-chart>
  </div>
</template>

<script>
import echarts from 'echarts';
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
export default {
  name: "ImageChart",
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
          boundaryGap: false,
          data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: [820, 932, 901, 934, 1290, 1330, 1320],
            type: 'line',
            areaStyle: {}
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
