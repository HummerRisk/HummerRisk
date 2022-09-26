<template>
  <div align="middle">
    <hr-chart :options="options" :width="1335" :height="280"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
export default {
  name: "K8sChart",
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
      this.$post("/dashboard/cloudNativeChart", {}, response => {
        let data = response.data;
        this.options = {
          xAxis: {
            type: 'category',
            data: data.xAxis
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              data: data.yAxis,
              type: 'bar'
            }
          ],
          grid: {
            top: '10%',
            left: '1%',
            right: '2%',
            bottom: '2%',
            containLabel: true
          },
          color: ['#009ef0', '#627dec', '#11cfae', '#893fdc', '#89ffff','#0051a4']
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
