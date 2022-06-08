<template>
  <el-col>
    <package-header-menus/>
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

import PackageHeaderMenus from "./head/PackageHeaderMenus";

export default {
    name: "package",
    components: {PackageHeaderMenus},
    data() {
      return {
        baseUrl: "package"
      }
    }
  }

</script>

<style scoped>

</style>
