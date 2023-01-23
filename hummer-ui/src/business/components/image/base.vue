<template>
  <el-col>
    <image-header-menus/>
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

import ImageHeaderMenus from "./head/ImageHeaderMenus";

export default {
    components: {ImageHeaderMenus},
    data() {
      return {
        baseUrl: "image"
      }
    }
  }

</script>

<style scoped>

</style>
