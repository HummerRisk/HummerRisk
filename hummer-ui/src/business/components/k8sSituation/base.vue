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
    name: "k8sSituation",
    components: {SituationHeaderMenus},
    data() {
      return {
        baseUrl: "k8sSituation"
      }
    }
  }

</script>

<style scoped>

</style>
