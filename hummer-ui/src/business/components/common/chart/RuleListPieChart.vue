<template>
  <div>
    <hr-chart :options="options"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {dashboardDistributionUrl} from "@/api/cloud/dashboard/dashboard";

let opt = {
  index: 0
};
let color = ['#11cfae', '#009ef0', '#627dec', '#0051a4', '#893fdc', '#89ffff', '#0099ff'];
/* eslint-disable */
export default {
  name: "RuleListPieChart",
  components: {
    HrChart,
  },
  data() {
    return {
      options: {},
    }
  },
  methods: {
    contains(arr, dst) {
      var i = arr.length;
      while ((i -= 1)) {
        if (arr[i] == dst) {
          return i;
        }
      }
      return false;
    },
    attackSourcesDataFmt(sData) {
      var sss = [];
      sData.forEach(function(item, i) {
        let itemStyle = {
          color: i > 3 ? color[3] : color[i]
        };
        sss.push({
          value: item,
          itemStyle: itemStyle
        });
      });
      return sss;
    },
    init () {
      let data=[];
      this.$post(dashboardDistributionUrl, {group: "ruleList", limit: 5}, response => {
        for (let obj of response.data) {
          let param = {
            code: obj.groupName,
            fundPost: obj.yAxis,
            stock: this.$t('history.resource_result') + ': ' + obj.yAxis + '/' + obj.yAxis2
          };
          data.push(param);
        }
        let attaData = [];
        let attaName = [];
        let topName=[]
        data.forEach((it, index) => {
          attaData[index] = it.fundPost;
          attaName[index] = it.stock;
          topName[index] = `${it.code}`
        });
        let salvProMax = []; //背景按最大值
        for (let i = 0; i < attaData.length; i++) {
          salvProMax.push(attaData[0]);
        }
        topName.reverse();//翻转数组，列表的名称反了
        this.options = {
          backgroundColor: "#fff",
          tooltip: {
            show: false,
            backgroundColor: "rgba(3,169,244, 0.5)", //背景颜色（此时为默认色）
            textStyle: {
              fontSize: 24
            }
          },
          color: color,
          legend: {
            pageIconSize: [12, 12],
            itemWidth: 20,
            itemHeight: 10,
            textStyle: {
              //图例文字的样式
              fontSize: 16,
              color: "#444"
            },
            selectedMode: false,
            data: [this.$t('dashboard.rule_detail_top')]
          },
          xAxis: {
            type: "value",

            splitLine: {
              show: false
            },
            axisLabel: {
              show: false
            },
            axisTick: {
              show: false
            },
            axisLine: {
              show: false
            }
          },
          yAxis: [
            {
              type: "category",
              inverse: true,
              axisLine: {
                show: false
              },
              axisTick: {
                show: false
              },
              axisPointer: {
                label: {
                  show: true,
                  //margin: 30
                }
              },
              pdaaing: [5, 0, 0, 0],
              postion: "right",
              data: attaName,
              axisLabel: {
                margin: 30,
                fontSize: 14,
                align: "left",
                padding: [2, 0, 0, 0],
                color: "#000",
                rich: {
                  nt1: {
                    color: "#fff",
                    backgroundColor: '#89ffff',
                    width: 13,
                    height: 13,
                    fontSize: 10,
                    align: "center",
                    borderRadius: 100,
                    lineHeight: "5",
                    padding: [0, 1, 2, 1]
                  },
                  nt2: {
                    color: "#fff",
                    backgroundColor: '#11cfae',
                    width: 13,
                    height: 13,
                    fontSize: 10,
                    align: "center",
                    borderRadius: 100,
                    padding: [0, 1, 2, 1]
                  },
                  nt3: {
                    color: "#fff",
                    backgroundColor: '#893fdc',
                    width: 13,
                    height: 13,
                    fontSize: 10,
                    align: "center",
                    borderRadius: 100,
                    padding: [0, 1, 2, 1]
                  },
                  nt: {
                    color: "#fff",
                    backgroundColor: '#009ef0',
                    width: 13,
                    height: 13,
                    fontSize: 10,
                    align: "center",
                    borderRadius: 100,
                    padding: [0, 1, 2, 1],
                    lineHeight: 5
                  }
                },
                formatter: function(value, index) {
                  index++;
                  if (index - 1 < 3) {
                    return ["{nt" + index + "|" + index + "}"].join("\n");
                  } else {
                    return ["{nt|" + index + "}"].join("\n");
                  }
                }
              }
            },
            {
              type: "category",
              inverse: true,
              axisTick: "none",
              axisLine: "none",
              show: true,
              axisLabel: {
                textStyle: {
                  color: "#444",
                  fontSize: "14"
                }
              },
              data: this.attackSourcesDataFmt(attaName)
            },
            {//名称
              type: 'category',
              offset: -10,
              position: "left",
              axisLine: {
                show: false
              },
              inverse: false,
              axisTick: {
                show: false
              },
              axisLabel: {
                interval: 0,
                color: ["#000"],
                align: "left",
                verticalAlign: "bottom",
                lineHeight: 32,
                fontSize: 12
              },
              data: topName
            },
          ],
          series: [
            {
              zlevel: 1,
              name: this.$t('rule.rule_detail'),
              type: "bar",
              barWidth: "15px",
              animationDuration: 1500,
              data: this.attackSourcesDataFmt(attaData),
              align: "center",
              itemStyle: {
                normal: {
                  barBorderRadius: 10
                }
              },
              label: {
                show: true,
                fontSize: 14,
                color: "#fff",
                textBorderWidth: 2,
                padding: [2, 0, 0, 0]
              }
            },
            {
              name: this.$t('rule.rule_detail'),
              type: "bar",
              barWidth: 15,
              barGap: "-100%",
              margin: "20",
              data: salvProMax,
              textStyle: {
                //图例文字的样式
                fontSize: 16,
                color: "#000"
              },
              itemStyle: {
                normal: {
                  color: "#eee",
                  fontSize: 16,
                  barBorderRadius: 30
                },
              }
            }
          ],
          grid: {
            left: "-35%",
            right: "0%",
            width:"130%",
            bottom: "0%",
            top: "0%",
            containLabel: true
          },
        };
      });
    },
    getArrByKey (data, k) {
      let key = k || "value";
      let res = [];
      if (data) {
        data.forEach(function (t) {
          res.push(t[key]);
        });
      }
      return res;
    }
  },
  created() {
    this.init();
  },
}

</script>

<style scoped>

.echarts {
  margin: 0 auto;
  max-width: 650px;
  max-height: 300px;
}

</style>
