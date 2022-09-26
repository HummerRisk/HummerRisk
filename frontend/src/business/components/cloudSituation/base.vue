<template>
  <el-col>
    <situation-header-menus/>
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

import SituationHeaderMenus from "./head/SituationHeaderMenus";

export default {
    name: "cloudSituation",
    components: {SituationHeaderMenus},
    data() {
      return {
        baseUrl: "cloudSituation"
      }
    }
  }

</script>

<style scoped>

</style>
