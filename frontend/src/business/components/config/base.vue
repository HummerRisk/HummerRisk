<template>
  <el-col>
    <config-header-menus/>
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

import ConfigHeaderMenus from "./head/ConfigHeaderMenus";

export default {
    components: {ConfigHeaderMenus},
    data() {
      return {
        baseUrl: "config"
      }
    }
  }

</script>

<style scoped>

</style>
