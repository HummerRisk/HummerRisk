<template>
  <el-card class="box-card" shadow="always">
    <dashboard-pie-chart></dashboard-pie-chart>
  </el-card>
</template>
<script>

import DashboardPieChart from "@/business/components/common/chart/DashboardPieChart";
/* eslint-disable */
  export default {
    name: "MdCardContentPie",
    components: {
      DashboardPieChart
    },
    data() {
      return {
      }
    },
    methods: {
      init() {
      },
    },
    mounted() {
      this.init();
    },
  }

</script>

<style scoped>
  .box-card {
    width: 100%;
  }
</style>
