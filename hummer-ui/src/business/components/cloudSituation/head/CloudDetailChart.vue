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
          },
          series: [
            {
              type: 'graph',
              layout: 'force',
              label: {
                show: true,
                distance: 20,
                textStyle: { fontSize: 12, color: '#000' },
                formatter: params => {
                  return `${params.name}\n\n\n\n\n\n\n${params.value}`
                },
              },
              tooltip: {
                tooltip:{
                  formatter(params){
                    let tooltipdata = params.data;
                    var result = '';
                    if(params.dataType == 'node'){
                      let nodename = tooltipdata.name.split("\n")
                      return `<b>资源名称: <b/>  ${nodename[1]}
                              <br/>命名空间:  ${nodename[0]}
                              <br/>资源类型:  ${params.value}`
                    }
                    if(params.dataType == 'edge'){
                      return ``;
                    }
                    return result;
                  }
                },
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
                repulsion: 60,
                friction: 0.3,
                edgeLength: 150
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
