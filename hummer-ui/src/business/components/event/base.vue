<template>
  <el-col>
    <log-header-menus/>
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

import LogHeaderMenus from "./head/LogHeaderMenus";

export default {
    components: {LogHeaderMenus},
    data() {
      return {
        baseUrl: "image"
      }
    }
  }

</script>

<style scoped>

</style>
