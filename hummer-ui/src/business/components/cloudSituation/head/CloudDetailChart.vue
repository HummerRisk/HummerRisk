<template>
  <div align="middle">
    <hr-chart :options="options" :width="1200" :height="450"></hr-chart>
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
        let cloudResourceRelaList = data.cloudResourceRelaList;
        let cloudResourceRelaLinkList = data.cloudResourceRelaLinkList;

        for (let obj of cloudResourceRelaList) {
          obj.symbol = 'image://'+ require('@/assets/img/rela/' + obj.symbol);
        }

        this.options = {
          title: {
            text: this.$t('cloud_topo.resource_relation')
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            show: false,
          },
          yAxis: {
            type: 'value',
            show: false,
          },
          tooltip: {
            formatter(params){
              let strs = params.name.split("\n");
              return `<b>资源类型: <b/> ${strs[0]}
                <br/><b>资源名称: <b/> ${strs[1]}
                <br/><b>资源标识: <b/> ${params.value}`;
            }
          },
          series: [
            {
              type: 'graph',
              layout: 'none',

              symbolSize: 40,
              label: {
                show: true,
                textStyle: { fontSize: 12, color: '#000' },
                formatter: params => {
                  return `${params.name}\n\n\n\n\n\n\n${params.value}`
                },
              },
              edgeSymbol: ['circle', 'arrow'],
              edgeSymbolSize: [4, 10],
              data: cloudResourceRelaList,
              links: cloudResourceRelaLinkList,
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
