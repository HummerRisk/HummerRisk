<template>
  <div>
    <hr-chart :options="options" :width="1335" :height="800"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
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
    }
  },
  methods: {
    init() {
      if(!this.accountId) return;
      this.$get("/k8s/rbacChart/" + this.accountId, response => {
        let nodes = response.data.nodes;
        for (let obj of nodes) {
          if(!!obj.symbol) {
            obj.symbol = 'image://'+ require('../../../../assets/img/platform/k8s.png');
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
            data: ['K8S', 'ServiceAccount', 'Role', 'ResourceType', 'Resource']
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
                show: true,
                position: "middle",
                fontSize: 12,
                formatter: (params) => {
                  return params.data.relation.name;
                },
              },
              edgeSymbol: ["circle", "arrow"], //箭头两端形状
              draggable: true,
              data: data.nodes.map(function (node, idx) {
                node.id = idx;
                return node;
              }),
              categories: data.categories,
              force: {
                edgeLength: 200,//两个节点之间的距离
                repulsion: 80,//连线距离
                gravity: 0.02//节点受到的向中心的引力因子。该值越大节点越往中心点靠拢。
              },
              edges: data.links,
            }
          ],
          color: ['#11cfae', '#009ef0', '#627dec', '#893fdc', '#0051a4', '#8B0000', '#FF4D4D', '#FF8000', '#336D9F']
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
