<template>
  <div align="middle">
    <hr-chart :options="options" :width="400" :height="280"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import { dashboardDistributionUrl } from "@/api/cloud/dashboard/dashboard";
/* eslint-disable */
export default {
  name: "RuleRiskChart",
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
      this.$post(dashboardDistributionUrl, {group: "ruleList", limit: 5}, response => {
        let nameData = [];
        let complianceData = [];
        let noComplianceData = [];
        for (let obj of response.data) {
          nameData.push(obj.groupName);
          complianceData.push(obj.yAxis2);
          noComplianceData.push(parseInt(obj.yAxis) - parseInt(obj.yAxis2));
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
