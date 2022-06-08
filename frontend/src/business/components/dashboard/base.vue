<template>
  <el-col>
    <header-menus/>
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

import HeaderMenus from "./head/HeaderMenus";

export default {
    name: "dashboard",
    components: {HeaderMenus},
    data() {
      return {
        baseUrl: "dashboard"
      }
    }
  }

</script>

<style scoped>

</style>
