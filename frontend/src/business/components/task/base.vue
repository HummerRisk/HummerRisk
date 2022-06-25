<template>
  <el-col>
    <cloudTask-header-menus/>
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

import TaskHeaderMenus from "./head/TaskHeaderMenus";

export default {
    name: "cloudTask",
    components: {TaskHeaderMenus},
    data() {
      return {
        baseUrl: "cloudTask"
      }
    }
  }

</script>

<style scoped>

</style>
