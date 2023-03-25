<template>
  <div>
    <hr-chart :options="options"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {serverSeverityChartUrl} from "@/api/k8s/server/server";
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
      this.$get(serverSeverityChartUrl, response => {
        let data = response.data;
        this.options = {
          legend: {},
          tooltip: {},
          dataset: {
            dimensions: ['product', 'High', 'Medium', 'Low'],
            source: data
          },
          xAxis: { type: 'category' },
          yAxis: {},
          // Declare several bar series, each will be mapped
          // to a column of dataset.source by default.
          series: [{ type: 'bar' }, { type: 'bar' }, { type: 'bar' }],
          color: ['#11cfae', '#009ef0', '#627dec', '#893fdc']
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
