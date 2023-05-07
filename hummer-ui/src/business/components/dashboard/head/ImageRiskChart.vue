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
              name: 'Critical',
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
              name: 'High',
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
              name: 'Medium',
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
              name: 'Low',
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
              name: 'Unknown',
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
