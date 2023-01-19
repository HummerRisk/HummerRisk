<template>
  <hr-chart :options="options"></hr-chart>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
  export default {
    name: "CenterChart",
    components: {
      HrChart
    },
    props: {
      row: String
    },
    data() {
      return {
        options: {},
      }
    },
    methods: {
      init() {
        let data = this.initData(0);
        this.options = {
          backgroundColor: '#fff',
          //第一个图表
          series: [{
            type: 'pie',
            hoverAnimation: false, //鼠标经过的特效
            left: 0,
            right: 0,
            bottom: 0,
            top: 0,
            radius: ['75%', '90%'],
            center: ['50%', '60%'],
            startAngle: 225,
            labelLine: {
              normal: {
                show: false
              }
            },
            label: {
              normal: {
                position: 'center'
              }
            },
            data: [{
              value: 100,
              itemStyle: {
                normal: {
                  color: '#E1E8EE'
                }
              },
            }, {
              value: 35,
              itemStyle: {
                normal: {
                  label: {
                    show: false
                  },
                  labelLine: {
                    show: false
                  },
                  color: "rgba(0,0,0,0)",
                  borderWidth: 0
                },
                emphasis: {
                  color: "rgba(0,0,0,0)",
                  borderWidth: 0
                }
              },
            },

            ]
          },
            //上层环形配置
            {
              type: 'pie',
              hoverAnimation: false, //鼠标经过的特效
              radius: ['75%', '90%'],
              center: ['50%', '60%'],
              startAngle: 225,
              labelLine: {
                normal: {
                  show: false
                }
              },
              label: {
                normal: {
                  position: 'center'
                }
              },
              data: [
                {
                  value: data,
                  itemStyle: {
                    normal: {
                      color: '#6F78CC'
                    }
                  },
                  label: {
                    normal: {
                      formatter: '{c}分',
                      position: 'center',
                      show: true,
                      textStyle: {
                        fontSize: '15',
                        fontWeight: 'normal',
                        color: '#34374E'
                      }
                    }
                  },
                }, {
                  value: 35,
                  itemStyle: {
                    normal: {
                      label: {
                        show: false
                      },
                      labelLine: {
                        show: false
                      },
                      color: "rgba(0,0,0,0)",
                      borderWidth: 0
                    },
                    emphasis: {
                      color: "rgba(0,0,0,0)",
                      borderWidth: 0
                    }
                  },
                },

              ]
            },
          ]
        };
      },
      initData (count) {
        if (!this.row) {
          count++;
          if (count === 10) return 100;
          this.initData(count);
        } else {
          return parseInt(this.row);
        }
      },
    },
    mounted() {
      this.init();
    },
  }
</script>

<style scoped>
  .echarts {
    margin: 0;
    padding: 0;
    max-width: 150px;
    max-height: 100px;
  }
</style>
