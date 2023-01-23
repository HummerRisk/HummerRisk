<template>
  <el-col>
    <sbom-header-menus/>
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

import SbomHeaderMenus from "./head/SbomHeaderMenus";

export default {
    name: "sbom",
    components: {SbomHeaderMenus},
    data() {
      return {
        baseUrl: "sbom"
      }
    }
  }

</script>

<style scoped>

</style>
