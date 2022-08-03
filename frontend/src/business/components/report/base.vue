<template>
  <el-col>
    <report-header-menus/>
    <div>
      <transition>
        <keep-alive>
          <router-view :baseUrl="baseUrl"/>
        </keep-alive>
      </transition>
    </div>
  </el-col>
</template>
<script>

import ReportHeaderMenus from "./head/ReportHeaderMenus";

export default {
    name: "report",
    components: {ReportHeaderMenus},
    data() {
      return {
        baseUrl: "report"
      }
    }
  }

</script>

<style scoped>

</style>
