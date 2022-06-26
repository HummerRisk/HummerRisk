<template>
  <div>
    <hr-chart :options="options" :width="100" :height="200"></hr-chart>
  </div>
</template>

<script>
import echarts from 'echarts';
import HrChart from "@/business/components/common/chart/HrChart";

const gaugeData = [
  {
    value: 20,
    name: 'Good',
    title: {
      offsetCenter: ['-40%', '80%']
    },
    detail: {
      offsetCenter: ['-40%', '95%']
    }
  },
  {
    value: 40,
    name: 'Better',
    title: {
      offsetCenter: ['0%', '80%']
    },
    detail: {
      offsetCenter: ['0%', '95%']
    }
  },
  {
    value: 60,
    name: 'Perfect',
    title: {
      offsetCenter: ['40%', '80%']
    },
    detail: {
      offsetCenter: ['40%', '95%']
    }
  }
];
let color = ['#11cfae', '#009ef0', '#627dec', '#0051a4', '#893fdc', '#89ffff', '#0099ff'];
/* eslint-disable */
export default {
  name: "ScorePieChart",
  components: {
    HrChart,
    echarts
  },
  data() {
    return {
      options: {},
    }
  },
  methods: {
    init () {
      let data=[];
      this.options = {
        series: [
          {
            type: 'gauge',
            anchor: {
              show: true,
              showAbove: true,
              size: 18,
              itemStyle: {
                color: '#FAC858'
              }
            },
            pointer: {
              icon: 'path://M2.9,0.7L2.9,0.7c1.4,0,2.6,1.2,2.6,2.6v115c0,1.4-1.2,2.6-2.6,2.6l0,0c-1.4,0-2.6-1.2-2.6-2.6V3.3C0.3,1.9,1.4,0.7,2.9,0.7z',
              width: 8,
              length: '80%',
              offsetCenter: [0, '8%']
            },
            progress: {
              show: true,
              overlap: true,
              roundCap: true
            },
            axisLine: {
              roundCap: true
            },
            data: gaugeData,
            title: {
              fontSize: 14
            },
            detail: {
              width: 40,
              height: 14,
              fontSize: 14,
              color: '#fff',
              backgroundColor: 'auto',
              borderRadius: 3,
              formatter: '{value}%'
            }
          }
        ]
      };
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
}

</style>
