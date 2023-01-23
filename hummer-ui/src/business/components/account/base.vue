<template>
  <el-col>
    <account-header-menus/>
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

import AccountHeaderMenus from "./head/AccountHeaderMenus";

export default {
    name: "account",
    components: {AccountHeaderMenus},
    data() {
      return {
        baseUrl: "account"
      }
    }
  }

</script>

<style scoped>

</style>
