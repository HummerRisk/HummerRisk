<template>
  <el-col>
    <rule-header-menus/>
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

import RuleHeaderMenus from "./head/RuleHeaderMenus";

export default {
    name: "rule",
    components: {RuleHeaderMenus},
    data() {
      return {
        baseUrl: "rule"
      }
    }
  }

</script>

<style scoped>

</style>
