<template>
  <div align="middle">
    <hr-chart :options="options" :width="width" :height="256"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import * as echarts from 'echarts';
/* eslint-disable */
export default {
  name: "VulnChart",
  components: {
    HrChart,
    echarts
  },
  props: {
    data: {},
    width: [Number, String],
  },
  watch: {
    width() {
    },
  },
  data() {
    return {
      options: {},
    }
  },
  methods: {
    init() {
      this.$post("/dashboard/distribution", {group: "vulnList", limit: 10}, response => {
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
            text: this.$t('dashboard.vuln_statistics_top10'),
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
            right: -20,
            top: 50,
            bottom: 20,
            data: legendData,

          },
          series: [
            {
              name: this.$t('resource.resource_result_score'),
              type: 'pie',
              radius: '55%',
              center: ['38%', '60%'],
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
          grid: {
            top: '10%',
            left: '1%',
            right: '2%',
            bottom: '2%',
            containLabel: true
          },
          color: ['#11cfae', '#009ef0', '#627dec', '#893fdc', '#89ffff','#0051a4']
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
