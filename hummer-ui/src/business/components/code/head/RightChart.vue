<template>
  <div>
    <hr-chart :options="options"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {codeSeverityChartUrl} from "@/api/k8s/k8s";
/* eslint-disable */
export default {
  name: "RightChart",
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
      this.$get(codeSeverityChartUrl, response => {
        let data = response.data;
        this.options = {
          legend: {},
          tooltip: {},
          dataset: {
            dimensions: ['product', 'Critical', 'High', 'Medium', 'Low', 'Unknown'],
            source: data
          },
          xAxis: { type: 'category' },
          yAxis: {},
          // Declare several bar series, each will be mapped
          // to a column of dataset.source by default.
          series: [{ type: 'bar' }, { type: 'bar' }, { type: 'bar' }, { type: 'bar' }, { type: 'bar' }],
          color: ['#11cfae', '#009ef0', '#627dec', '#893fdc', '#89ffff','#0051a4', '#8B0000', '#FF4D4D', '#FF8000', '#336D9F']
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

  .echarts {
    margin: 0 auto;
    min-width: 300px;
    min-height: 200px;
  }

</style>
