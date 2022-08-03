<template>
  <el-col>
    <situation-header-menus/>
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

import SituationHeaderMenus from "./head/SituationHeaderMenus";

export default {
    name: "situation",
    components: {SituationHeaderMenus},
    data() {
      return {
        baseUrl: "situation"
      }
    }
  }

</script>

<style scoped>

</style>
