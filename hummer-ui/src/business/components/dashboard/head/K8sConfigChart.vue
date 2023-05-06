<template>
  <div align="middle">
    <hr-chart :options="options" :width="400" :height="280"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import {imageChartUrl} from "@/api/cloud/dashboard/dashboard";

/* eslint-disable */
export default {
  name: "K8sConfigChart",
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
        let data = response.data;
        this.options = {
          tooltip: {
            trigger: 'item'
          },
          legend: {
            top: '5%',
            left: 'center'
          },
          series: [
            {
              name: this.$t('dashboard.non_compliant_assets'),
              type: 'pie',
              radius: ['45%', '65%'],
              avoidLabelOverlap: false,
              top: 50,
              itemStyle: {
                borderRadius: 5,
                borderColor: '#fff',
                borderWidth: 2
              },
              label: {
                show: false,
                position: 'center'
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: 40,
                  fontWeight: 'bold'
                }
              },
              labelLine: {
                show: false
              },
              data: [
                { value: 1048, name: 'aliyun' },
                { value: 735, name: 'aws' },
                { value: 580, name: 'huawei' },
                { value: 484, name: 'tencent' },
                { value: 300, name: 'huoshan' },
                { value: 1048, name: 'aliyun-test' },
                { value: 735, name: 'aws2' },
                { value: 580, name: 'jinshan' },
                { value: 484, name: 'azure' },
                { value: 300, name: 'jd' }
              ]
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
