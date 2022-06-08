<template>
  <hr-chart :options="options"></hr-chart>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
  export default {
    name: "ScoreChart",
    components: {HrChart},
    data() {
      return {
        options: {
          backgroundColor: "#FFF",
          tooltip: {
            formatter: "{b}{c}"
          },
          series: [
            {
            tooltip: {
              show: false
            },
            "name": 'wrap',
            "type": 'pie',
            "hoverAnimation": false,
            "legendHoverLink": false,
            center: ['50%', '75%'],
            "radius": ['0%', '12%'],
            "z": 5,
            "label": {
              "normal": {
                "show": false,
                "position": 'center'
              },
              "emphasis": {
                "show": false
              }
            },
            "labelLine": {
              "normal": {
                "show": false
              }
            },
            "data": [{
              "value": 100,
              itemStyle: {
                normal: {
                  color: "#072B79"
                },
                emphasis: {
                  color: "#072B79"
                }
              }
            }]
          },
            {
            tooltip: {
              show: false
            },
            "name": 'wrap',
            "type": 'pie',
            "hoverAnimation": false,
            "legendHoverLink": false,
            center: ['50%', '75%'],
            "radius": ['6%', '14%'],
            "z": 5,
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            "label": {
              "normal": {
                "show": false,
                "position": 'center'
              },
              "emphasis": {
                "show": false
              }
            },
            "labelLine": {
              "normal": {
                "show": false
              }
            },
            "data": [{
              "value": 100,
              itemStyle: {
                normal: {
                  color: "#24D8E7"
                },
                emphasis: {
                  color: "#24D8E7"
                }
              }
            }]
          },
            {
            tooltip: {
              show: false
            },
            name: '刻度',
            type: 'gauge',
            radius: '120%',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            z: 1,
            min: 0,
            max: 1,
            center: ['50%', '75%'],
            splitNumber: 5, //刻度数量
            startAngle: 180,
            endAngle: 0,
            axisLine: {
              show: true,
              lineStyle: {
                width: 5,
                color: [
                  [0.12, '#11cfae'],
                  [0.35, '#009ef0'],
                  [0.63, '#627dec'],
                  [0.8, '#0051a4'],
                  [1, '#072B79'],
                ]
              }
            }, //仪表盘轴线
            axisLabel: {
              show: false
            }, //刻度标签。
            axisTick: {
              show: true,
              lineStyle: {
                color: 'auto',
                width: 0
              },
              length: -15
            }, //刻度样式
            splitLine: {
              show: true,
              length: 0,
              lineStyle: {
                color: 'auto',
                width: 2
              }
            }, //分隔线样式
            detail: {
              show: false
            },
            pointer: {
              show: false
            }
          },
            {
            name: '扫描结果',
            type: 'gauge',
            radius: '120%',
            min: 0,
            max: 1,
            center: ['50%', '75%'],
            data: [{
              value: 0.8912,
              name: '扫描结果'
            }],
            splitNumber: 12, //刻度数量
            startAngle: 180,
            endAngle: 0,
            z: 5,
            axisLine: {
              show: true,
              lineStyle: {
                width: 0,
                color: [
                  [0.12, '#11cfae'],
                  [0.35, '#009ef0'],
                  [0.63, '#627dec'],
                  [0.8, '#0051a4'],
                  [1, '#072B79'],
                ]
              }
            }, //仪表盘轴线
            axisLabel: {
              show: true,
              color: '#072B79',
              fontSize: 15,
              distance: -50,
              formatter: function(params) {
                var value = params.toFixed(2)

                if (value === 0.00) {
                  return '危'
                } else if (value === 0.25) {
                  return '差'
                } else if (value === 0.50) {
                  return '中'
                } else if (value === 0.75) {
                  return '良'
                } else if (value === 1.00) {
                  return '优'
                } else {
                  return ''
                }
              }
            }, //刻度标签。
            axisTick: {
              splitNumber: 10,
              show: true,
              lineStyle: {
                color: 'auto',
                width: 2
              },
              length: 20,
            }, //刻度样式
            splitLine: {
              show: true,
              length: 25,
              lineStyle: {
                color: 'auto',
                width: 5
              }
            }, //分隔线样式

            "itemStyle": {
              "normal": {
                "color": "#24D8E7" //指针颜色
              }
            },
            pointer: {
              width: 10,
              length: '80%'
            },
            detail: {
              formatter: function(params) {
                return (params * 100).toFixed(0) + '分';
              },
              fontSize: 25,
              color: "#072B79",
              offsetCenter: ['0%', '-35%']
            },
            title: {
              offsetCenter: ['0', '-60%'],
              fontSize: 15,
              color: "#072B79",
              show: true
            },
          }
          ]
        },
      }
    }
  }
</script>

<style scoped>
  .echarts {
    margin: 0;
    padding: 0;
    max-width: 300px;
    max-height: 200px;
  }
</style>
