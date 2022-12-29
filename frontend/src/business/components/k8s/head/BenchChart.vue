<template>
  <div class="metric-container">
    <el-row type="flex" align="middle">
      <div style="width: 100%">
        <el-row type="flex" justify="center" align="middle">
          <hr-chart id="chart" ref="chart" :options="options" :autoresize="true" :width="140" :height="140"></hr-chart>
          <el-row type="flex" justify="center" align="middle">
            <i class="circle fail"/>
            <div class="metric-box">
              <div class="value">{{ content.fail }}</div>
              <div class="name">{{ 'FAIL' }}</div>
            </div>
            <div style="width: 80px"></div>
            <i class="circle warning"/>
            <div class="metric-box">
              <div class="value">{{ content.warn }}</div>
              <div class="name">{{ 'WARN' }}</div>
            </div>
            <div style="width: 80px"></div>
            <i class="circle primary"/>
            <div class="metric-box">
              <div class="value">{{ content.info }}</div>
              <div class="name">{{ 'INFO' }}</div>
            </div>
            <div style="width: 80px"></div>
            <i class="circle success"/>
            <div class="metric-box">
              <div class="value">{{ content.pass }}</div>
              <div class="name">{{ 'PASS' }}</div>
            </div>
          </el-row>
        </el-row>
      </div>
    </el-row>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import echarts from 'echarts';
/* eslint-disable */
export default {
  name: "BenchChart",
  components: {HrChart,echarts},
  props: {
    content: Object
  },
  data() {
    return {}
  },
  created() {
  },
  methods: {},
  computed: {
    options() {
      return {
        color: ['#8B0000', '#FF4D4D', '#FF8000', '#336D9F', '#67C23A'],
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        title: [{
          text: this.content.total,
          subtext: this.$t('sbom.total'),
          top: 'center',
          left: 'center',
          textStyle: {
            rich: {
              align: 'center',
              value: {
                fontSize: 32,
                fontWeight: 'bold',
                padding: [10, 0]
              },
              name: {
                fontSize: 14,
                fontWeight: 'normal',
                color: '#7F7F7F',
              }
            }
          }
        }],
        series: [
          {
            type: 'pie',
            radius: ['80%', '90%'],
            avoidLabelOverlap: false,
            hoverAnimation: false,
            itemStyle: {
              normal: {
                borderColor: "#FFF",
                shadowColor: '#E1E1E1',
                shadowBlur: 10
              }
            },
            labelLine: {
              show: false
            },
            data: [
              {value: this.content.fail},
              {value: this.content.warn},
              {value: this.content.info},
              {value: this.content.pass},
            ]
          }
        ]
      };
    },
  },
}
</script>

<style scoped>
.metric-container {
  padding: 20px;
}

.metric-container #chart {
  width: 140px;
  height: 140px;
  margin-right: 40px;
}

.metric-container .split {
  margin: 20px;
  height: 100px;
  border-left: 1px solid #D8DBE1;
}

.metric-container .circle {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 0 20px 1px rgba(200, 216, 226, .42);
  display: inline-block;
  margin-right: 10px;
  vertical-align: middle;
}

.metric-container .circle.fail {
  background-color: #8B0000;
}

.metric-container .circle.warning {
  background-color: #ff4d4d;
}

.metric-container .circle.primary {
  background-color: #ff8000;
}

.metric-container .circle.success {
  background-color: #336d9f;
}

.metric-container .circle.info {
  background-color: #67C23A;
}

.metric-box {
  display: inline-block;
  text-align: center;
}

.metric-box .value {
  font-size: 32px;
  font-weight: 600;
  letter-spacing: -.5px;
}

.metric-box .name {
  font-size: 16px;
  letter-spacing: -.2px;
  color: #404040;
}

</style>
