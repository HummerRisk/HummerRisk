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
          if(!!obj.symbol) {
            obj.symbol = 'image://'+ require(obj.symbol);
            break;
          }
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
              console.log(params)
              return `<b>资源名称: <b/>  ${params.name}
                <br/>资源标识:  ${params.value}
                <br/>资源类型:  ${params.resourceType}`;
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
                  console.log(111, params)
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
