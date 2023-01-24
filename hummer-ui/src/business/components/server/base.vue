<template>
  <el-col>
    <server-header-menus/>
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

import ServerHeaderMenus from "./head/ServerHeaderMenus";

export default {
    name: "server",
    components: {ServerHeaderMenus},
    data() {
      return {
        baseUrl: "server"
      }
    }
  }

</script>

<style scoped>

</style>
