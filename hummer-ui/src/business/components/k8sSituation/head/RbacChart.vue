<template>
  <div>
    <el-col :span="4" :offset="1" style="margin-top: 50px;">
      <el-row :gutter="24">
        <template>
          <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="namespaceAllChange">{{ $t('rule.all') }}{{ $t('k8s.namespace') }}</el-checkbox>
          <div style="margin: 15px 0;"></div>
          <el-checkbox-group v-model="choosedNamespace" @change="handleNamespaceChange">
            <el-checkbox v-for="(item,index) in allNamesapce" :label="item.sourceName" :key="item.id" style="display:block;" checked>{{item.sourceName}}</el-checkbox>
          </el-checkbox-group>
        </template>
      </el-row>
    </el-col>
    <hr-chart :options="options" :width="1335" :height="800"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {rbacChartUrl,
  namespaceTopologyUrl,
  cloudNativeSourceListUrl} from "@/api/k8s/k8s/k8s";
/* eslint-disable */
export default {
  name: "RbacChart",
  components: {
    HrChart,
  },
  props: {
    accountId: '',
  },
  watch: {
    accountId() {
      this.init();
    },
  },
  data() {
    return {
      options: {},
      allNamesapce:{},
      allNamesapceLabel:[],
      initData:[],
      choosedNamespace:[],
      isIndeterminate: true,
      checkAll:true,
      condition: {},
    }
  },
  methods: {
    init() {
      this.initChart()
      this.search()
    },
    initChart(){
      if(!!this.accountId) {
        this.$get(rbacChartUrl + this.accountId, response => {
          let nodes = response.data.nodes;
          for (let obj of nodes) {
            if(!!obj.symbol) {
              obj.symbol = 'image://'+ require('@/assets/img/platform/k8s.png');
              break;
            }
          }
          let data = {
            "type": "force",
            "nodes": nodes,
            "links": response.data.links,
            "categories": [
              {
                "name": "K8S",
                "keyword": {},
                "base": "K8S"
              },
              {
                "name": "ServiceAccount",
                "keyword": {},
                "base": "ServiceAccount"
              },
              {
                "name": "ResourceType",
                "keyword": {},
                "base": "ResourceType"
              },
              {
                "name": "Role",
                "keyword": {},
                "base": "Role"
              },
              {
                "name": "Resource",
                "keyword": {},
                "base": "Resource"
              }
            ]
          };
          this.options = {
            legend: {
              top: 10,
              bottom: 0,
              data: data.categories.map(function (a) {
                return a.name;
              })
            },
            tooltip: {
              formatter(params){
                // console.log(params)
                let tooltipdata = params.data;
                var result = '';
                if(params.dataType == 'node'&&(!!tooltipdata.name)){
                  let nodename = tooltipdata.name.split("/")
                  return `<b>资源名称: <b/>  ${nodename[1]}
                  <br/>命名空间:  ${nodename[0]}
                  <br/>资源类型:  ${tooltipdata.category}`
                }
                if(params.dataType == 'edge'){
                  return `动作：${tooltipdata.relation.name}`
                }
                return result;
              }
            },
            series: [
              {
                type: 'graph',
                layout: 'force',
                animation: false,
                label: {
                  show: true,
                  position: "bottom",
                  distance: 5,
                  fontSize: 12,
                  align: "center",
                  color: "black",
                },
                autoCurveness: 0.01, //多条边的时候，自动计算曲率
                edgeLabel: {//边的设置
                  show: false,
                  position: "middle",
                  fontSize: 12,
                  formatter: (params) => {
                    return params.data.relation.name;
                  },
                },
                edgeSymbol: ["none", "none"], //箭头两端形状
                draggable: true,
                data: data.nodes.map(function (node, idx) {
                  node.id = idx;
                  return node;
                }),
                categories: data.categories,
                force: {
                  edgeLength: 200,//两个节点之间的距离
                  repulsion: 80,//连线距离
                  gravity: 0.05//节点受到的向中心的引力因子。该值越大节点越往中心点靠拢。
                },
                edges: data.links,
                roam:true,
                cursor:'pointer',
                lineStyle: {
                  color: 'source',
                  curveness: 0.3
                },
                emphasis: {
                  focus: 'adjacency',
                  lineStyle: {
                    width: 10
                  }
                }

              }
            ],
            color: ['#11cfae', '#009ef0', '#627dec', '#893fdc', '#0051a4', '#8B0000', '#FF4D4D', '#FF8000', '#336D9F']
          };
          this.initData = this.options.series[0].data
        });
      }
    },
    refreshChart(){
      let resultdata = [this.initData[0]]
      this.initData.forEach(function(item,index){
        if(!!item.name){
          let nodename = item.name.split("/")
          let namepsaceName = nodename[0]
          if(this.choosedNamespace.includes(namepsaceName)){
            resultdata.push(item)
          }
        }
      },this)
      this.options.series[0].data = resultdata
    },
    handleNamespaceChange(val){
      let checkedCount = val.length;
      this.checkAll = checkedCount === this.allNamesapceLabel.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.allNamesapceLabel.length;
      this.refreshChart();

    },
    namespaceAllChange(val){
      this.choosedNamespace = val ? this.allNamesapceLabel : [];
      this.isIndeterminate = false;
      this.refreshChart();
    },
    search() {
      let url = cloudNativeSourceListUrl + "1/9999";
      if (!!this.accountId) {
        this.condition.cloudNativeId = this.accountId;
        this.condition.combine = {};
        this.condition.combine.sourceType = {operator: 'in', value: ['Namespace']};
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.allNamesapce = data.listObject;
          this.allNamesapce.forEach(function(item,index){
            this.allNamesapceLabel.push(item.sourceName)
          },this)
        });
      }
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
  .box-card-top {
  width: 60%;
  background-color: #364f6c;
  color: #FFFFFF;
  border-radius: 10px;
  margin-top: 3%;
}
</style>
