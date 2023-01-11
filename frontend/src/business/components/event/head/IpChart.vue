<template>
  <div align="middle">
    <hr-chart :options="options" :width="1200" :height="280"></hr-chart>
  </div>
</template>

<script>
import HrChart from "@/business/components/common/chart/HrChart";
import echarts from 'echarts';
/* eslint-disable */
export default {
  name: "ButtomChart",
  components: {
    HrChart,
    echarts
  },
  props: {
    ip: String,
    days: Number
  },
  data() {
    return {
      options: {},
    }
  },
  methods: {
    formatDate: function (value) {
      let dt = new Date(value)
      let year = dt.getFullYear();
      let month = (dt.getMonth() + 1).toString().padStart(2, '0');
      let date = dt.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${date}`;
    },

    init() {
      let now =  new Date().getTime()
      let startDate = this.formatDate(now-this.days*24*60*60*1000)
      let endDate = this.formatDate(now)
      this.$get("/cloud/event/ipAccessChart/"+this.ip+"/"+startDate+"/"+endDate, response => {
        let data = response.data;
        this.options = {
          xAxis: {
            type: 'category',
            data: data.xAxis
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              data: data.yAxis,
              type: 'line'
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
