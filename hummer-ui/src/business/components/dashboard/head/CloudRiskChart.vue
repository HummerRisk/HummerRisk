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
  name: "CloudRiskChart",
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
            trigger: 'item'
          },
          legend: {
            top: '5%',
            left: 'center',
            // doesn't perfectly work with our tricks, disable it
            selectedMode: false
          },
          series: [
            {
              name: this.$t('dashboard.non_compliant_risk'),
              type: 'pie',
              radius: ['45%', '65%'],
              center: ['50%', '70%'],
              // adjust the start angle
              startAngle: 180,
              top: 50,
              label: {
                show: true,
                formatter(param) {
                  // correct the percentage
                  return param.name + ' (' + param.percent * 2 + '%)';
                }
              },
              data: [
                { value: 1048, name: '高危风险' },
                { value: 735, name: '高风险' },
                { value: 580, name: '中风险' },
                { value: 484, name: '低风险' },
                { value: 300, name: '无风险' },
                {
                  // make an record to fill the bottom 50%
                  value: 1048 + 735 + 580 + 484 + 300,
                  itemStyle: {
                    // stop the chart from rendering this piece
                    color: 'none',
                    decal: {
                      symbol: 'none'
                    }
                  },
                  label: {
                    show: false
                  }
                }
              ]
            }
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
