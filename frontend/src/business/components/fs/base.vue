<template>
  <el-col>
    <fs-header-menus/>
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

import FsHeaderMenus from "./head/FsHeaderMenus";

export default {
    components: {FsHeaderMenus},
    data() {
      return {
        baseUrl: "fs"
      }
    }
  }

</script>

<style scoped>

</style>
