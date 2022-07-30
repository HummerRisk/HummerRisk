<template>
  <div align="middle">
    <hr-chart :options="options" :width="999" :height="280"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
export default {
  name: "AnalysisTopChart",
  components: {
    HrChart,
  },
  props: {
    sizeForm: {},
  },
  data() {
    return {
      options: {},
    }
  },
  methods: {
    async init() {
      this.$post("/dashboard/analysisChart", {}, response => {
        let data = response.data;
        console.log(data)
        let ids = data.ids;
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
          color: this.sizeForm.color
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
