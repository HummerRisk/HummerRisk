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
  name: "AccountPieChart",
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
      this.$post("/dashboard/distribution", {group: "accountList", limit: 5}, response => {
        let legendData = [];
        let seriesData = [];
        for (let obj of response.data) {
          legendData.push(obj.groupName + ' ' + obj.yAxis + '/' + obj.yAxis2 + '(' + obj.xAxis + '分)');
          seriesData.push({
            name: obj.groupName + ' ' + obj.yAxis + '/' + obj.yAxis2 + '(' + obj.xAxis + '分)',
            value: obj.yAxis
          });
        }
        this.options = {
          title: {
            text: this.$t('dashboard.cloud_account_statistics_top'),
            subtext: this.$t('resource.resource_result_score'),
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)'
          },
          legend: {
            type: 'scroll',
            orient: 'vertical',
            right: 10,
            top: 20,
            bottom: 20,
            data: legendData,

          },
          series: [
            {

              name: this.$t('resource.resource_result_score'),
              type: 'pie',
              radius: '55%',
              center: ['40%', '50%'],
              data: seriesData,
              // data: this.sycData("seriesData"),
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
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

.echarts {
  margin: 0 auto;
  min-width: 300px;
  min-height: 200px;
}

</style>
