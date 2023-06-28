<template>
  <div align="middle">
    <hr-chart :options="options" :width="800" :height="450"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {cloudTopologyresourceRelaUrl} from "@/api/cloud/sync/sync";
/* eslint-disable */
export default {
  name: "CloudDetailChart",
  components: {
    HrChart,
  },
  props: {
    resourceItemId: '',
  },
  data() {
    return {
      options: {},
    }
  },
  methods: {
    init() {
      this.$get(cloudTopologyresourceRelaUrl + this.resourceItemId, response => {
        let data = response.data;
        this.options = {
          title: {
            text: this.$t('cloud_topo.resource_relation')
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
                show: true,
                formatter: params => {
                  return `类型: ${params.name}\n\n\n\n\n\n\n${params.value}`
                },
              },
              edgeSymbol: ['circle', 'arrow'],
              edgeSymbolSize: [4, 10],
              data: data.cloudResourceRelaList,
              links: data.cloudResourceRelaLinkList,
              lineStyle: {
                color: '#2f4554'
              },
            },
          ],
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
