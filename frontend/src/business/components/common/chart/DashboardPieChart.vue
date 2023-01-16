<template>
  <div style="margin-top: 10%">
    <hr-chart :options="options"></hr-chart>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import HrChart from "@/business/components/common/chart/HrChart";

let objData = [];
/* eslint-disable */
export default {
  name: "DashboardPieChart",
  components: {
    echarts,
    HrChart
  },
  data() {
    return {
      vulns: {
        name: this.$t('dashboard.i18n_overall_compliance'),
        label: {position: 'outside'},
        url: "/dashboard/distribution",
        param: {group: "overall"},
      },
      options: {
        maxnum: 100,
        tooltip: {
          show: true,
          trigger: "item",
          formatter: function (params) {
            if (params.name == "hide") {
              return params.seriesName + ": 0.00%";
            } else {
              let num = 0;
              if (typeof params.name == 'string' && params.name.indexOf("showtip_") !== -1) {
                num = Number(params.name.split("_")[1]);
              } else {
                num = params.value;
              }
              if (Number(num) == 0) return params.seriesName + ": 0.00%";
              return params.seriesName + ":" + parseFloat(objData[params.seriesName].yAxis / objData[params.seriesName].yAxis2 * 100).toFixed(2) + "%";
            }
          }
        },
        title: [{
          text: '',
          top: 'center',
          left: 'center',
          textStyle: {
            rich: {
              name: {
                fontSize: 12,
                fontWeight: 'normal',
                color: '#444444',
                padding: [0, 0]
              },
              val: {
                fontSize: 13,
                fontWeight: 'bold',
                color: '#333333',
              }
            }
          }
        },
          {
            text: "",
            top: 0,
            left: 0,
            textStyle: {
              fontSize: 12,
              color: '#444444',
              fontWeight: 300
            }
          }],
        legend: {
          show: true,
          top: "center",
          left: '65%',
          data: [],
          itemWidth: 15,
          itemHeight: 10,
          width: 20,
          padding: [0, 5],
          itemGap: 25,
          formatter: function (name) {
            return "{title|" + name + "}\n{value|" + (objData[name].yAxis / objData[name].yAxis2 * 100).toFixed(2) + "%}"
          },
          textStyle: {
            rich: {
              title: {
                fontSize: 10,
                lineHeight: 10,
                color: "rgba(0,0,0,99)"
              },
              value: {
                fontSize: 14,
                lineHeight: 18,
                color: "rgba(0,0,0,75)"
              }
            }
          },
        },
        grid: {
          top: '10%',
          height: 0,
          left: 0,
          right: '0%',
        },
        xAxis: [{
          show: false
        }],
        series: [],
        color: ['#11cfae', '#009ef0', '#627dec', '#0051a4']
      },
    }
  },
  methods: {
    init() {
      this.$post(this.vulns.url, this.vulns.param|| {}, response => {
        let pieChartData = response.data;
        let getData = this.getData(pieChartData);
        objData = this.options.objData = this.array2obj(pieChartData, "groupName");
        this.options.data = this.getArrayValue(pieChartData, "groupName");
        this.options.series = getData.series;
        this.options.yAxis = getData.yAxis;
        this.options.legend.data = getData.legend.data;
        this.options.title[0].text = '{val|' + getData.total + '}';
      });
    },
    getArrayValue (array, key) {
      let hkey = key || "value";
      var res = [];
      if (array) {
        array.forEach(function (t) {
          res.push(t[hkey]);
        });
      }
      return res;
    },
    array2obj (array, key) {
      var resObj = {};
      for (var i = 0; i < array.length; i++) {
        resObj[array[i][key]] = array[i];
      }
      return resObj;
    },
    getData (data) {
      var res = {
        series: [],
        yAxis: [],
        legend: {data: []},
        total: 0
      };
      let totalYaxis1 = 0;
      let totalYaxis2 = 0;
      for (var j in data) {
        totalYaxis1 = totalYaxis1 + data[j]["yAxis"];
        totalYaxis2 = totalYaxis2 + data[j]["yAxis2"];
        if (data[j]["yAxis2"] === 0) {
          // 分母为0时echart图显示异常，赋值一下分母可正常显示
          data[j]["yAxis2"] = 1;
        }
        if (res.legend.data.indexOf(data[j]["groupName"] == -1)) {
          res.legend.data.push({
            'icon': 'rect',
            "name": data[j]["groupName"]
          });
        }
        var ra = data.length - 1 - j;
        res.series.push({
          name: data[j]["groupName"],
          type: 'pie',
          radius: [(ra * 20 + 40) + "%", (24 + ra * 20) + "%"],
          itemStyle: {
            normal: {
              label: {
                show: false
              }
            }
          },
          hoverAnimation: false,
          startAngle: -90,
          center: ["50%", "50%"],
          data: [
            {
              value: data[j]["yAxis"],
              name: data[j]["groupName"],
              label: {
                normal: {
                  postion: "center"
                }
              },
            },
            {
              value: data[j]["yAxis2"] - data[j]["yAxis"],
              itemStyle: {
                normal: {
                  color: 'rgba(203,203,203,0.5)',
                  label: {
                    show: false
                  },
                  labelLine: {
                    show: false
                  }
                },
                emphasis: {
                  color: 'rgba(0,0,0,0)'
                }
              },
              name: data[j]["yAxis"]
            },
            {
              value: data[j]["yAxis2"],
              itemStyle: {
                normal: {
                  color: 'rgba(0,0,0,0)',
                  label: {
                    show: true
                  },
                  labelLine: {
                    show: true
                  }
                },
                emphasis: {
                  color: 'rgba(0,0,0,0)'
                }
              },
              name: 'hide'
            }
          ]
        })
      }
      var initnum = res.series[0].data[0]["yAxis"] == 0 ? 0 : parseFloat(res.series[0].data[0]["yAxis"] * 100 / res.series[0].data[0]["yAxis2"]).toFixed(2);
      res.total = totalYaxis1 == 0 ? 0 : (totalYaxis1 / totalYaxis2 * 100).toFixed(2) + "%";
      res.series.push({
        type: 'gauge',
        z: 4,
        min: 0,
        max: 100,
        splitNumber: 5,
        center: ['50%', '50%'], // 默认全局居中
        radius: '0%',
        endAngle: 0,
        startAngle: 180,
        axisLabel: {
          show: false,
          formatter: "{value}%"
        },
        axisLine: { // 坐标轴线
          show: false,
          lineStyle: {
            color: [
              [1, "rgba(0,0,0,9)"]
            ], //仪表盘颜色
            width: 2, //仪表盘宽度
          }
        },
        axisTick: { // 坐标轴小标记
          show: false,
          length: 5, // 属性length控制线长
          lineStyle: { // 属性lineStyle控制线条样式
            color: 'auto'
          }
        },
        splitLabel: {show: false},
        pointer: {show: false},
        splitLine: { // 分隔线
          show: false,

        },
        title: {
          show: false
        },
        detail: { //显示数据
          show: false,
        },
        itemStyle: {
          normal: {
            color: "#676767", //仪表盘颜色
          }
        },
        data: [{
          value: initnum
        }]

      });
      return res;
    },
  },
  mounted() {
    this.init();
  },
}

</script>

<style scoped>

  .echarts {
    margin: 0 auto;
    max-width: 300px;
    max-height: 200px;
  }

</style>
