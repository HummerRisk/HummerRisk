<template>
  <div>
    <hr-chart :options="options"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {projectChartUrl} from "@/api/k8s/code/code";
/* eslint-disable */
export default {
  name: "LeftChart",
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
      this.$get(projectChartUrl, response => {
        let data = response.data;
        this.options = {
          title: {
            text: this.$t('code.code_project_chart'),
            subtext: this.$t('code.code_project_chart_vuln'),
            left: 'center'
          },
          tooltip: {
            trigger: 'item'
          },
          legend: {
            orient: 'vertical',
            left: 'left'
          },
          series: [
            {
              name: 'Code',
              type: 'pie',
              radius: '50%',
              data: data,
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
