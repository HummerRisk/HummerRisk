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
        this.options = {
          title: {
            text: '资源关系'
          },
          tooltip: {},
          xAxis: {
            type: 'category',
            boundaryGap: false,
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

              symbolSize: 40,
              label: {
                show: true
              },
              edgeSymbol: ['circle', 'arrow'],
              edgeSymbolSize: [4, 10],
              data: [
                {
                  id: '1',
                  resourceItemId: '03bfed6b-de98-410b-95b3-958f1953e370',
                  accountId: '9555c82b-0ce7-4fb3-b0c8-ec79d6dbac12',
                  pluginId: 'hummer-aws-plugin',
                  regionId: 'ap-northeast-3',
                  resourceType: '',
                  hummerId: '',
                  name: 'Internet',
                  x: 100,
                  y: 100
                },
                {
                  id: '2',
                  resourceItemId: '03bfed6b-de98-410b-95b3-958f1953e370',
                  accountId: '9555c82b-0ce7-4fb3-b0c8-ec79d6dbac12',
                  pluginId: 'hummer-aws-plugin',
                  regionId: 'ap-northeast-3',
                  resourceType: '',
                  hummerId: '',
                  name: 'vpc-2zeuphj08tt7q3brd',
                  x: 100,
                  y: 200
                },
                {
                  id: '3',
                  resourceItemId: '03bfed6b-de98-410b-95b3-958f1953e370',
                  accountId: '9555c82b-0ce7-4fb3-b0c8-ec79d6dbac12',
                  pluginId: 'hummer-aws-plugin',
                  regionId: 'ap-northeast-3',
                  resourceType: 'aws.eip',
                  hummerId: '5ee250a6-c543-4287-8074-6b428d88e478',
                  name: '13.134.56.192',
                  x: 200,
                  y: 200
                },
                {
                  id: '4',
                  resourceItemId: '03bfed6b-de98-410b-95b3-958f1953e370',
                  accountId: '9555c82b-0ce7-4fb3-b0c8-ec79d6dbac12',
                  pluginId: 'hummer-aws-plugin',
                  regionId: 'ap-northeast-3',
                  resourceType: 'aws.eip',
                  hummerId: '5ee250a6-c543-4287-8074-6b428d88e478',
                  name: '13.134.56.190',
                  x: 200,
                  y: 300
                },
                {
                  id: '5',
                  resourceItemId: '03bfed6b-de98-410b-95b3-958f1953e370',
                  accountId: '9555c82b-0ce7-4fb3-b0c8-ec79d6dbac12',
                  pluginId: 'hummer-aws-plugin',
                  regionId: 'ap-northeast-3',
                  resourceType: 'aws.elb',
                  hummerId: '5ee250a6-c543-4287-8074-6b428d88e478',
                  name: 'elb-f32434greg456',
                  x: 300,
                  y: 200
                },
                {
                  id: '6',
                  resourceItemId: '03bfed6b-de98-410b-95b3-958f1953e370',
                  accountId: '9555c82b-0ce7-4fb3-b0c8-ec79d6dbac12',
                  pluginId: 'hummer-aws-plugin',
                  regionId: 'ap-northeast-3',
                  resourceType: 'aws.eip',
                  hummerId: '5ee250a6-c543-4287-8074-6b428d88e478',
                  name: 'ecs-i005442350034',
                  x: 400,
                  y: 200
                }
              ],
              links: [
                {
                  source: '1',
                  target: '2'
                },
                {
                  source: '2',
                  target: '3'
                },
                {
                  source: '2',
                  target: '4'
                },
                {
                  source: '3',
                  target: '5'
                },
                {
                  source: '5',
                  target: '6'
                },
                {
                  source: '4',
                  target: '6'
                }
              ],
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
