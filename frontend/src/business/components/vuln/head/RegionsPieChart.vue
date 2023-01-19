<template>
  <div>
    <hr-chart :options="options"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
export default {
  name: "RegionsPieChart",
  components: {
    HrChart,
  },
  data() {
    return {
      options: {},
      accountId: "all"
    }
  },
  methods: {
    focus(data) {
      this.accountId = data;
      this.init();
    },
    init() {
      let params = this.accountId !== "all"?{group: "countList", accountId: this.accountId, limit: 5}:{group: "countList", limit: 5};
      this.$post("/dashboard/distribution", params, response => {
        let legendData = [];
        let seriesData = [];
        for (let obj of response.data) {
          legendData.push(obj.groupName + " (" + obj.yAxis + "/" + obj.yAxis2 +  ") ");
          seriesData.push({
            name: obj.groupName + " (" + obj.yAxis + "/" + obj.yAxis2 +  ") ",
            value: obj.yAxis
          });
        }
        this.options = {
          title: {
            text: this.$t('vuln.regions_statistics_top'),
              subtext: this.$t('vuln.resource_result_region'),
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
            // data: this.sycData("legendData"),
          },
          series: [
            {
              name: this.$t('vuln.resource_result_region'),
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
          color: ['#11cfae', '#009ef0', '#627dec', '#893fdc', '#89ffff','#0051a4', '#0099ff', '#0033ff', '#006666', '#7700ee', '#00ee00']
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
