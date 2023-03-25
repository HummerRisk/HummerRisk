<template>
  <el-col>
    <event-header-menus/>
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

import EventHeaderMenus from "./head/EventHeaderMenus.vue";

export default {
    components: {EventHeaderMenus},
    data() {
      return {
        baseUrl: "event"
      }
    }
  }

</script>

<style scoped>

</style>
