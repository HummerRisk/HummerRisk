<template>
  <div align="middle">
    <hr-chart :options="options" :width="400" :height="280"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {serverRiskChartUrl} from "@/api/cloud/dashboard/dashboard";
/* eslint-disable */
export default {
  name: "ServerRiskChart",
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
      this.$post(serverRiskChartUrl, {limit: 10}, response => {
        let nameData = [];
        let complianceData = [];
        let noComplianceData = [];
        for (let obj of response.data) {
          nameData.push(obj.name);
          complianceData.push(obj.compliance);
          noComplianceData.push(obj.noCompliance);
        }
        this.options = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              // Use axis to trigger tooltip
              type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
            }
          },
          legend: {
            top: '5%',
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'value',
            show: false,
          },
          yAxis: {
            type: 'category',
            axisLine: {
              show: false,
              symbol: 'none',
            },
            axisTick: {
              show: false,
            },
            data: nameData
          },
          series: [
            {
              name: '合规',
              type: 'bar',
              stack: 'total',
              label: {
                show: true
              },
              emphasis: {
                focus: 'series'
              },
              data: complianceData
            },
            {
              name: '不合规',
              type: 'bar',
              stack: 'total',
              label: {
                show: true
              },
              emphasis: {
                focus: 'series'
              },
              data: noComplianceData
            },
          ],
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
