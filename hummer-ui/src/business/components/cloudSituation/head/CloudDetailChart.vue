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
          series: [
            {
              type: 'graph',
              layout: 'force',

              label: {
                show: true,
                textStyle: { fontSize: 12, color: '#000' },
                formatter: params => {
                  return `${params.name}\n\n\n\n\n\n\n${params.value}`
                },
              },
              tooltip: {
                formatter(params){
                  let strs = params.name.split("\n");
                  return `<b>资源类型: <b/> ${strs[0]}
                <br/><b>资源名称: <b/> ${strs[1]}
                <br/><b>资源标识: <b/> ${params.value}`;
                }
              },
              emphasis:{
                scale : true,
                focus : 'adjacency'
              },
              legendHoverLink : true,
              roam: true,
              edgeSymbol: ['none', 'arrow'],
              edgeSymbolSize: 15,
              force: {
                repulsion: 100,
                friction: 0.3,
                edgeLength:200
              },
              lineStyle:{
                'color':'blue',
                'width':3
              },
              symbolSize: 100,
              data: cloudResourceRelaList,
              links: cloudResourceRelaLinkList,
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
