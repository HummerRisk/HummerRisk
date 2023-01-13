<template>
  <div>
    <hr-chart :options="options" :width="1335" :height="800"></hr-chart>
  </div>
</template>

<script>
import echarts from 'echarts';
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
          "nodes": [
            {
              "id": "0",
              "name": "AnalyserNode",
              "value": 1,
              "symbolSize": 60,
              "category": "Resource"
            },
            {
              "id": "1",
              "name": "AudioNode",
              "value": 1,
              "symbolSize": 80,
              "category": "ClusterRole"
            },
            {
              "id": "2",
              "name": "Uint8Array",
              "value": 1,
              "symbolSize": 100,
              "category": "ServiceAccount"
            },
            {
              "id": "3",
              "name": "Float32Array",
              "value": 1,
              "symbolSize": 60,
              "category": "Resource"
            },
            {
              "id": "4",
              "name": "ArrayBuffer",
              "value": 1,
              "symbolSize": 80,
              "category": "ClusterRole"
            },
            {
              "id": "5",
              "name": "ArrayBufferView",
              "value": 1,
              "symbolSize": 100,
              "category": "ServiceAccount"
            },
            {
              "id": "6",
              "name": "Attr",
              "value": 1,
              "symbolSize": 60,
              "category": "ResourceDetails"
            },
            {
              "id": "7",
              "name": "Node",
              "value": 1,
              "symbolSize": 80,
              "category": "Role"
            },
            {
              "id": "8",
              "name": "Element",
              "value": 1,
              "symbolSize": 100,
              "category": "ServiceAccount"
            },
            {
              "id": "9",
              "name": "AudioBuffer",
              "value": 1,
              "symbolSize": 60,
              "category": "ResourceDetails"
            },
            {
              "id": "10",
              "name": "AnalyserNode",
              "value": 1,
              "symbolSize": 60,
              "category": "Resource"
            },
            {
              "id": "11",
              "name": "AnalyserNode",
              "value": 1,
              "symbolSize": 60,
              "category": "Resource"
            },
          ],
          "links": [
            {
              "source": "0",
              "target": "1",
              "relation": {
                name: "get",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "1",
              "relation": {
                name: "post",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "2",
              relation: {
                name: "兄弟",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "3",
              relation: {
                name: "兄弟",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "4",
              relation: {
                name: "get",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "5",
              relation: {
                name: "api",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "6",
              relation: {
                name: "post",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "7",
              relation: {
                name: "delete",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "8",
              relation: {
                name: "role",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "9",
              relation: {
                name: "get/post",
                id: "1",
              },
            },
            {
              "source": "10",
              "target": "11",
              relation: {
                name: "get/post",
                id: "1",
              },
            }
          ],
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
            data: ['ServiceAccount', 'Resource', 'Role', 'ClusterRole', 'ResourceDetails']
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
