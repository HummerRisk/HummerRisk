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

import OssHeaderMenus from "./head/LogHeaderMenus";

export default {
    components: {OssHeaderMenus},
    data() {
      return {
        baseUrl: "image"
      }
    }
  }

</script>

<style scoped>

</style>
