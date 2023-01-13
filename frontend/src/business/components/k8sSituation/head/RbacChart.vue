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
      this.$get("/fs/severityChart", response => {
        let data = {
          "type": "force",
          "nodes": [
            {
              "id": "0",
              "name": "AnalyserNode",
              "value": 1,
              "symbolSize": 60,
              "category": "HTMLElement"
            },
            {
              "id": "1",
              "name": "AudioNode",
              "value": 1,
              "symbolSize": 80,
              "category": "HTMLElement"
            },
            {
              "id": "2",
              "name": "Uint8Array",
              "value": 1,
              "symbolSize": 100,
              "category": "HTMLElement"
            },
            {
              "id": "3",
              "name": "Float32Array",
              "value": 1,
              "symbolSize": 60,
              "category": 4
            },
            {
              "id": "4",
              "name": "ArrayBuffer",
              "value": 1,
              "symbolSize": 80,
              "category": 4
            },
            {
              "id": "5",
              "name": "ArrayBufferView",
              "value": 1,
              "symbolSize": 100,
              "category": "WebGL"
            },
            {
              "id": "6",
              "name": "Attr",
              "value": 1,
              "symbolSize": 60,
              "category": "CSS"
            },
            {
              "id": "7",
              "name": "Node",
              "value": 1,
              "symbolSize": 80,
              "category": "CSS"
            },
            {
              "id": "8",
              "name": "Element",
              "value": 1,
              "symbolSize": 100,
              "category": "SVG"
            },
            {
              "id": "9",
              "name": "AudioBuffer",
              "value": 1,
              "symbolSize": 60,
              "category": "SVG"
            },
          ],
          "links": [
            {
              "source": "0",
              "target": "1",
              relation: {
                name: "兄弟",
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
                name: "兄弟",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "5",
              relation: {
                name: "兄弟",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "6",
              relation: {
                name: "兄弟",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "7",
              relation: {
                name: "兄弟",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "8",
              relation: {
                name: "兄弟",
                id: "1",
              },
            },
            {
              "source": "0",
              "target": "9",
              relation: {
                name: "兄弟",
                id: "1",
              },
            }
          ],
          "categories": [
            {
              "name": "HTMLElement",
              "keyword": {},
              "base": "HTMLElement"
            },
            {
              "name": "WebGL",
              "keyword": {},
              "base": "WebGLRenderingContext"
            },
            {
              "name": "SVG",
              "keyword": {},
              "base": "SVGElement"
            },
            {
              "name": "CSS",
              "keyword": {},
              "base": "CSSRule"
            },
            {
              "name": "Other",
              "keyword": {}
            }
          ]
        };
        this.options = {
          legend: {
            top: 10,
            bottom: 0,
            data: ['HTMLElement', 'WebGL', 'SVG', 'CSS', 'Other']
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
                repulsion: 1000,
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
