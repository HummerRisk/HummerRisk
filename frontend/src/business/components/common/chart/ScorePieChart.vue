<template>
  <div>
    <hr-chart :key="cascaderKey" :options="options" :width="280" :height="300"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
export default {
  name: "ScorePieChart",
  components: {
    HrChart,
  },
  data() {
    return {
      cascaderKey: 1,
      options: {},
    }
  },
  methods: {
    init () {
      this.$get("/dashboard/score", response => {
        let data = response.data;
        let gaugeData = [
          {
            value: data,
            name: this.$t('dashboard.safe_score_all'),
            title: {
              offsetCenter: ['40%', '80%']
            },
            detail: {
              offsetCenter: ['-40%', '95%']
            }
          },
        ];
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
                width: 5,
                length: '60%',
                offsetCenter: [0, '80%']
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
                fontSize: 10
              },
              detail: {
                width: 30,
                height: 10,
                fontSize: 10,
                color: '#fff',
                backgroundColor: 'auto',
                borderRadius: 1,
                formatter: '{value}分'
              }
            }
          ],
          color: ['#009ef0', '#0051a4', '#0099ff', '#11cfae'],
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
}

</style>
