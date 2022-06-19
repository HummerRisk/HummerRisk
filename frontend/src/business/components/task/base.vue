<template>
  <el-col>
    <task-header-menus/>
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
    name: "network",
    components: {TaskHeaderMenus},
    data() {
      return {
        baseUrl: "network"
      }
    }
  }

</script>

<style scoped>

</style>
