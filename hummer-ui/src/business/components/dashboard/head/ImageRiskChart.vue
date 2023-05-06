<template>
  <div align="middle">
    <hr-chart :options="options" :width="400" :height="280"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {imageChartUrl} from "@/api/cloud/dashboard/dashboard";
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
      this.$post(imageChartUrl, {}, response => {
        let data = response.data;
        this.options = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              // Use axis to trigger tooltip
              type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
            }
          },
          legend: {},
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
            data: ['image1', 'image2', 'image3', 'image4', 'image5', 'image6', 'image7', 'image8', 'image9', 'image10']
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
              data: [66, 65, 50, 55, 40, 39, 35, 25, 20, 44]
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
              data: [10, 30, 45, 50, 55, 56, 60, 70, 75, 85]
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
              data: [85, 65, 50, 45, 40, 39, 35, 25, 20, 66]
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
              data: [10, 30, 45, 50, 55, 56, 60, 70, 75, 29]
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
              data: [85, 65, 50, 45, 40, 39, 35, 25, 20, 33]
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
