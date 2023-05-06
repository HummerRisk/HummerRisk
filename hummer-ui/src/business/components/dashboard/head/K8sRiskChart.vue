<template>
  <div align="middle">
    <hr-chart :options="options" :width="800" :height="280"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {imageChartUrl} from "@/api/cloud/dashboard/dashboard";
/* eslint-disable */
export default {
  name: "K8sRiskChart",
  components: {
    HrChart,
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
      this.$post(imageChartUrl, {}, response => {
        let data = [
          {
            name: 'CRITICAL',
            value: 70
          },
          {
            name: 'HIGH',
            value: 68
          },
          {
            name: 'MEDIUM',
            value: 48
          },
          {
            name: 'LOW',
            value: 40
          },
          {
            name: 'UNKNOWN',
            value: 32
          },
        ];
        this.options = {
          title: [
            {
              text: this.$t('dashboard.k8s_risk'),
              left: 'center'
            },
            {
              subtext: this.$t('k8s.vuln_compliance'),
              left: '10%',
              top: '75%',
              textAlign: 'center'
            },
            {
              subtext: this.$t('k8s.config_compliance'),
              left: '35%',
              top: '75%',
              textAlign: 'center'
            },
            {
              subtext: this.$t('k8s.kubench_compliance'),
              left: '60%',
              top: '75%',
              textAlign: 'center'
            },
            {
              subtext: this.$t('dashboard.k8s_compliance_scan_statistics'),
              left: '85%',
              top: '75%',
              textAlign: 'center'
            }
          ],
          series: [
            {
              type: 'pie',
              radius: '25%',
              center: ['50%', '50%'],
              data: data,
              label: {
                position: 'outer',
                alignTo: 'none',
                bleedMargin: 5,
                fontSize: 10
              },
              left: 0,
              right: '75%',
              top: 0,
              bottom: 0
            },
            {
              type: 'pie',
              radius: '25%',
              center: ['50%', '50%'],
              data: data,
              label: {
                position: 'outer',
                alignTo: 'labelLine',
                bleedMargin: 5,
                fontSize: 10
              },
              left: '25%',
              right: '50%',
              top: 0,
              bottom: 0
            },
            {
              type: 'pie',
              radius: '25%',
              center: ['50%', '50%'],
              data: data,
              label: {
                position: 'outer',
                alignTo: 'labelLine',
                margin: 20,
                fontSize: 10
              },
              left: '50%',
              right: '25%',
              top: 0,
              bottom: 0
            },
            {
              type: 'pie',
              radius: '25%',
              center: ['50%', '50%'],
              data: data,
              label: {
                position: 'outer',
                alignTo: 'edge',
                margin: 20,
                fontSize: 10
              },
              left: '75%',
              right: 0,
              top: 0,
              bottom: 0
            }
          ],
          color: ['#11cfae', '#009ef0', '#627dec', '#893fdc', '#89ffff','#0051a4', '#8B0000', '#FF4D4D', '#FF8000', '#336D9F']
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

</style>
