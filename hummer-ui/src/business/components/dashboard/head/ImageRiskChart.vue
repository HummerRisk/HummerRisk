<template>
  <div align="middle">
    <hr-chart :options="options" :width="400" :height="280"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {imageChartUrl, imageRiskChartUrl} from "@/api/cloud/dashboard/dashboard";
/* eslint-disable */
export default {
  name: "ImageRiskChart",
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
      this.$post(imageRiskChartUrl, {limit: 10}, response => {
        let nameData = [];
        let criticalData = [];
        let highData = [];
        let mediumData = [];
        let lowData = [];
        let unknownData = [];
        for (let obj of response.data) {
          nameData.push(obj.name);
          criticalData.push(obj.critical);
          highData.push(obj.high);
          mediumData.push(obj.medium);
          lowData.push(obj.low);
          unknownData.push(obj.unknown);
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
            data: nameData
          },
          series: [
            {
              name: 'CRITICAL',
              type: 'bar',
              stack: 'total',
              label: {
                show: false
              },
              emphasis: {
                focus: 'series'
              },
              data: criticalData
            },
            {
              name: 'HIGH',
              type: 'bar',
              stack: 'total',
              label: {
                show: false
              },
              emphasis: {
                focus: 'series'
              },
              data: highData
            },
            {
              name: 'MEDIUM',
              type: 'bar',
              stack: 'total',
              label: {
                show: false
              },
              emphasis: {
                focus: 'series'
              },
              data: mediumData
            },
            {
              name: 'LOW',
              type: 'bar',
              stack: 'total',
              label: {
                show: false
              },
              emphasis: {
                focus: 'series'
              },
              data: lowData
            },
            {
              name: 'UNKNOWN',
              type: 'bar',
              stack: 'total',
              label: {
                show: false
              },
              emphasis: {
                focus: 'series'
              },
              data: unknownData
            },
          ],
          color: ['#8B0000', '#FF4D4D', '#FF8000', '#336D9F', '#67C23A']
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
