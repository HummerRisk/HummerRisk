<template>
  <el-col>
    <resource-header-menus/>
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

import ResourceHeaderMenus from "./head/ResourceHeaderMenus";

export default {
    name: "resource",
    components: {ResourceHeaderMenus},
    data() {
      return {
        baseUrl: "resource"
      }
    }
  }

</script>

<style scoped>

</style>
