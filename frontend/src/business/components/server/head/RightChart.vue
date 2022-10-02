<template>
  <div>
    <hr-chart :options="options"></hr-chart>
  </div>
</template>

<script>
import echarts from 'echarts';
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
export default {
  name: "RightChart",
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
      this.$get("/server/severityChart", response => {
        let data = response.data;
        console.log(data)
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
