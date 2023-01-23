<template>
  <div align="middle">
    <hr-chart :options="options" :width="999" :height="280"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
export default {
  name: "AnalysisTopChart",
  components: {
    HrChart,
  },
  props: {
    sizeForm: {},
  },
  data() {
    return {
      options: {},
    }
  },
  methods: {
    async init() {
      this.$post("/dashboard/analysisChart", {}, response => {
        let chartData = response.data;
        let ids = this.sizeForm.ids;
        if(!!chartData.xAxis && !!chartData.yAxis) {
          for(let i = 0;i < ids.length; i++) {
            if(ids[i]) {
              switch (i) {
                case 0:
                  this.options = {
                    xAxis: {
                      type: 'category',
                      data: chartData.xAxis,
                    },
                    yAxis: {
                      type: 'value'
                    },
                    series: [
                      {
                        data: chartData.yAxis,
                        type: 'bar'
                      }
                    ],
                    grid: {
                      top: '10%',
                      left: '1%',
                      right: '2%',
                      bottom: '2%',
                      containLabel: true
                    },
                    color: this.sizeForm.color
                  };
                  break;
                case 1:
                  this.options = {
                    xAxis: {
                      type: 'category',
                      data: chartData.xAxis,
                    },
                    yAxis: {
                      type: 'value'
                    },
                    series: [
                      {
                        data: chartData.yAxis,
                        type: 'bar',
                        showBackground: true,
                        backgroundStyle: {
                          color: this.sizeForm.color,
                        }
                      }
                    ],
                    grid: {
                      top: '10%',
                      left: '1%',
                      right: '2%',
                      bottom: '2%',
                      containLabel: true
                    },
                    color: this.sizeForm.color
                  };
                  break;
                case 2:
                  this.options = {
                    tooltip: {
                      trigger: 'axis',
                      axisPointer: {
                        type: 'shadow'
                      }
                    },
                    grid: {
                      top: '10%',
                      left: '1%',
                      right: '2%',
                      bottom: '2%',
                      containLabel: true
                    },
                    xAxis: [
                      {
                        type: 'category',
                        data: chartData.xAxis,
                        axisTick: {
                          alignWithLabel: true
                        }
                      }
                    ],
                    yAxis: [
                      {
                        type: 'value'
                      }
                    ],
                    series: [
                      {
                        name: 'Direct',
                        type: 'bar',
                        barWidth: '60%',
                        data: chartData.yAxis,
                      }
                    ],
                    color: this.sizeForm.color
                  };
                  break;
                case 3:
                  this.options = {
                    xAxis: {
                      type: 'category',
                      boundaryGap: false,
                      data: chartData.xAxis,
                    },
                    yAxis: {
                      type: 'value'
                    },
                    series: [
                      {
                        data: chartData.yAxis,
                        type: 'line',
                        areaStyle: {}
                      }
                    ],
                    grid: {
                      top: '10%',
                      left: '1%',
                      right: '2%',
                      bottom: '2%',
                      containLabel: true
                    },
                    color: this.sizeForm.color
                  };
                  break;
                case 4:
                  this.options = {
                    xAxis: {
                      type: 'category',
                      data: chartData.xAxis,
                    },
                    yAxis: {
                      type: 'value'
                    },
                    series: [
                      {
                        data: chartData.yAxis,
                        type: 'line'
                      }
                    ],
                    color: this.sizeForm.color
                  };
                  break;
                case 5:
                  this.options = {
                    xAxis: {
                      type: 'category',
                      data: chartData.xAxis,
                    },
                    yAxis: {
                      type: 'value'
                    },
                    series: [
                      {
                        data: chartData.yAxis,
                        type: 'line',
                        smooth: true
                      }
                    ],
                    color: this.sizeForm.color
                  };
                  break;
                case 6:
                  this.options = {
                    title: {
                      text: 'Score'
                    },
                    tooltip: {
                      trigger: 'axis'
                    },
                    legend: {
                      data: ['Score']
                    },
                    grid: {
                      left: '3%',
                      right: '4%',
                      bottom: '3%',
                      containLabel: true
                    },
                    toolbox: {
                      feature: {
                        saveAsImage: {}
                      }
                    },
                    xAxis: {
                      type: 'category',
                      boundaryGap: false,
                      data: chartData.xAxis,
                    },
                    yAxis: {
                      type: 'value'
                    },
                    series: [
                      {
                        name: 'Score',
                        type: 'line',
                        stack: 'Total',
                        data: chartData.yAxis,
                      },
                    ],
                    color: ['#0051a4', '#89ffff', '#893fdc', '#627dec', '#009ef0', '#11cfae']
                  };
                  break;
                case 7:
                  let len = chartData.xAxis.length;
                  if (len !== 0) {
                    const chartDataX1 = [
                      {
                        name: chartData.xAxis[0],
                        value: chartData.yAxis[0]
                      },
                      {
                        name: chartData.xAxis[1],
                        value: chartData.yAxis[1]
                      },
                      {
                        name: chartData.xAxis[2],
                        value: chartData.yAxis[2]
                      },
                      {
                        name: chartData.xAxis[3],
                        value: chartData.yAxis[3]
                      },
                    ];
                    const chartDataX2 = [
                      {
                        name: chartData.xAxis[4],
                        value: chartData.yAxis[4]
                      },
                      {
                        name: chartData.xAxis[5],
                        value: chartData.yAxis[5]
                      },
                      {
                        name: chartData.xAxis[6],
                        value: chartData.yAxis[6]
                      }
                    ];
                    const chartDataX3 = [
                      {
                        name: chartData.xAxis[7],
                        value: chartData.yAxis[7]
                      },
                      {
                        name: chartData.xAxis[8],
                        value: chartData.yAxis[8]
                      },
                      {
                        name: chartData.xAxis[9],
                        value: chartData.yAxis[9]
                      }
                    ];
                    this.options = {
                      title: [
                        {
                          text: 'Score',
                          left: 'center'
                        },
                        {
                          subtext: 'Score1',
                          left: '16.67%',
                          top: '75%',
                          textAlign: 'center'
                        },
                        {
                          subtext: 'Score2',
                          left: '50%',
                          top: '75%',
                          textAlign: 'center'
                        },
                        {
                          subtext: 'Score3',
                          left: '83.33%',
                          top: '75%',
                          textAlign: 'center'
                        }
                      ],
                      series: [
                        {
                          type: 'pie',
                          radius: '25%',
                          center: ['50%', '50%'],
                          data: chartDataX1,
                          label: {
                            position: 'outer',
                            alignTo: 'none',
                            bleedMargin: 5
                          },
                          left: 0,
                          right: '66.6667%',
                          top: 0,
                          bottom: 0
                        },
                        {
                          type: 'pie',
                          radius: '25%',
                          center: ['50%', '50%'],
                          data: chartDataX2,
                          label: {
                            position: 'outer',
                            alignTo: 'labelLine',
                            bleedMargin: 5
                          },
                          left: '33.3333%',
                          right: '33.3333%',
                          top: 0,
                          bottom: 0
                        },
                        {
                          type: 'pie',
                          radius: '25%',
                          center: ['50%', '50%'],
                          data: chartDataX3,
                          label: {
                            position: 'outer',
                            alignTo: 'edge',
                            margin: 20
                          },
                          left: '66.6667%',
                          right: 0,
                          top: 0,
                          bottom: 0
                        }
                      ],
                      color: ['#0051a4', '#89ffff', '#893fdc', '#627dec', '#009ef0', '#11cfae']
                    };
                  }
                  break;
                default:
                  break
              }
            }
          }
        }
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
