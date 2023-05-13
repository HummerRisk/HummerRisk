<template>
  <el-col>
    <report-center-header-menus/>
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

import ReportCenterHeaderMenus from "./head/ReportCenterHeaderMenus";

export default {
    components: {ReportCenterHeaderMenus},
    data() {
      return {
        baseUrl: "ReportCenter"
      }
    }
  }

</script>

<style scoped>

</style>
