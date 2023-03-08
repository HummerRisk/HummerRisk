<template>
  <div align="middle">
    <hr-chart :options="options" :width="1335" :height="280"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {codeChartUrl} from "@/api/cloud/dashboard/dashboard";
/* eslint-disable */
export default {
  name: "CodeChart",
  components: {
    HrChart,
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
      this.$post(codeChartUrl, {}, response => {
        let data = response.data;
        this.options = {
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: data.xAxis
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              data: data.yAxis,
              type: 'line',
              areaStyle: {}
            }
          ],
          grid: {
            top: '10%',
            left: '1%',
            right: '2%',
            bottom: '2%',
            containLabel: true
          },
          color: ['#0051a4']
        };
      });
    },
  },
  created() {
    this.init();
  },
}

</script>

<style scoped>

</style>
