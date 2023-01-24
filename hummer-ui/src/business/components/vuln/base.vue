<template>
  <el-col>
    <vuln-header-menus/>
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

import VulnHeaderMenus from "./head/VulnHeaderMenus";

export default {
    name: "vuln",
    components: {VulnHeaderMenus},
    data() {
      return {
        baseUrl: "vuln"
      }
    }
  }

</script>

<style scoped>

</style>
