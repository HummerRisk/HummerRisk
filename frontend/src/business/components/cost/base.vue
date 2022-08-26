<template>
  <el-col>
    <cost-header-menus/>
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

import CostHeaderMenus from "./head/CostHeaderMenus";

export default {
    components: {CostHeaderMenus},
    data() {
      return {
        baseUrl: "cost"
      }
    }
  }

</script>

<style scoped>

</style>
