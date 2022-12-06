<template>
  <el-col>
    <code-header-menus/>
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

import CodeHeaderMenus from "./head/CodeHeaderMenus";

export default {
    name: "SourceCode",
    components: {CodeHeaderMenus},
    data() {
      return {
        baseUrl: "code"
      }
    }
  }

</script>

<style scoped>

</style>
