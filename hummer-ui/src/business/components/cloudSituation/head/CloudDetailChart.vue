<template>
  <div align="middle">
    <hr-chart :options="options" :width="800" :height="450"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {serverChartUrl} from "@/api/k8s/server/server";
/* eslint-disable */
export default {
  name: "CloudDetailChart",
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
      // this.$get(serverChartUrl, response => {
      //   let data = response.data;
      const axisData = ['Internet', '阿里云', '北京1区', 'aws.ec2', '有风险', '资源名称: bj', '资源标识:i-fhbeiwfb78'];
      const data = [1000, 500, 500, 500, 500, 500, 500];
      const links = data.map(function (item, i) {
        return {
          source: i,
          target: i + 1
        };
      });
      links.pop();
        this.options = {
          title: {
            text: '资源关系'
          },
          tooltip: {},
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: axisData,
            show: false,
          },
          yAxis: {
            type: 'value',
            show: false,
          },
          series: [
            {
              type: 'graph',
              layout: 'none',
              coordinateSystem: 'cartesian2d',
              symbolSize: 40,
              label: {
                show: true
              },
              edgeSymbol: ['circle', 'arrow'],
              edgeSymbolSize: [4, 10],
              data: data,
              links: links,
              lineStyle: {
                color: '#2f4554'
              },
            },
          ],
        };
      // });
    },
  },
  created() {
    this.init();
  },
}

</script>

<style scoped>

</style>
