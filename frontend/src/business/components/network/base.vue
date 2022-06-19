<template>
  <el-col>
    <network-header-menus/>
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

import NetworkHeaderMenus from "./head/NetworkHeaderMenus";

export default {
    name: "network",
    components: {NetworkHeaderMenus},
    data() {
      return {
        baseUrl: "network"
      }
    }
  }

</script>

<style scoped>

</style>
