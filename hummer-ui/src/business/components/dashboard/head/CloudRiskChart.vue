<template>
  <div align="middle">
    <hr-chart :options="options" :width="400" :height="280"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {dashboardRiskListUrl, dashboardSeverityUrl, imageChartUrl} from "@/api/cloud/dashboard/dashboard";
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
      this.$post(dashboardRiskListUrl, {}, response => {
        let seriesData = [];
        let sum = 0;
        for (let obj of response.data) {
          seriesData.push({
            name: this.$t('rule.' + obj.name),
            value: obj.value
          });
          sum += obj.value;
        }
        seriesData.push(
          {
            value: sum,
            itemStyle: {
              // stop the chart from rendering this piece
              color: 'none',
              decal: {
                symbol: 'none'
              }
            }, label: {
              show: false
            }
          }
        );
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
              data: seriesData
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
