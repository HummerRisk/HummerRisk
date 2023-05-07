<template>
  <div align="middle">
    <hr-chart :options="options" :width="400" :height="280"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {k8sKubenchRiskChartUrl} from "@/api/cloud/dashboard/dashboard";

/* eslint-disable */
export default {
  name: "K8sConfigBarChart",
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
      this.$post(k8sKubenchRiskChartUrl, {limit: 10}, response => {
        let data = response.data;
        this.options = {
          legend: {
            top: '5%',
            left: 'center'
          },
          tooltip: {},
          dataset: {
            dimensions: ['product', 'Fail', 'Warn', 'Info', 'Pass'],
            source: data
          },
          xAxis: { type: 'category' },
          yAxis: {},
          // Declare several bar series, each will be mapped
          // to a column of dataset.source by default.
          series: [{ type: 'bar' }, { type: 'bar' }, { type: 'bar' }, { type: 'bar' }],
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

</style>
