<template>
  <div align="middle">
    <hr-chart :options="options" :width="999" :height="280"></hr-chart>
  </div>
</template>

<script>
import echarts from 'echarts';
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
export default {
  name: "AnalysisTopChart",
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
      this.$post("/dashboard/imageChart", {}, response => {
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
          color: ['#009ef0', '#11cfae', '#627dec', '#893fdc', '#89ffff','#0051a4']
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
