<template>
  <div>
    <hr-chart :options="options" :width="1335" :height="800"></hr-chart>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
export default {
  name: "RbacChart",
  components: {
    HrChart,
    echarts,
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
      this.$get("/k8s/rbacChart", response => {
        let data = {
          "type": "force",
          "nodes": response.data.nodes,
          "links": response.data.links,
          "categories": [
            {
              "name": "ServiceAccount",
              "keyword": {},
              "base": "ServiceAccount"
            },
            {
              "name": "ResourceDetails",
              "keyword": {},
              "base": "ResourceDetails"
            },
            {
              "name": "Role",
              "keyword": {},
              "base": "Role"
            },
            {
              "name": "ClusterRole",
              "keyword": {},
              "base": "ClusterRole"
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
            data: ['ServiceAccount', 'Role', 'ClusterRole', 'Resource', 'ResourceDetails']
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
                fontSize: 14,
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
              edgeSymbol: ["circle", "arrow"], //边两边的类型
              draggable: true,
              data: data.nodes.map(function (node, idx) {
                node.id = idx;
                return node;
              }),
              categories: data.categories,
              force: {
                edgeLength: 250,
                repulsion: 100,
                gravity: 0.01
              },
              edges: data.links
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
