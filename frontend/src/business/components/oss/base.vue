<template>
  <el-col>
    <oss-header-menus/>
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

import OssHeaderMenus from "./head/OssHeaderMenus";

export default {
    components: {OssHeaderMenus},
    data() {
      return {
        baseUrl: "oss"
      }
    }
  }

</script>

<style scoped>

</style>
