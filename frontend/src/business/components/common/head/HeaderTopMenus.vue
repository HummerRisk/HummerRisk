<template>
  <el-menu mode="horizontal" menu-trigger="click"
           background-color="#df913c"
           class="el-menu-vertical-demo"
           text-color="#F2F2F2"
           active-text-color="#FFF"
           :default-active="activeIndex"
           @select="handleSelect"
           :collapse="isCollapse"
           @open="handleOpen"
           @close="handleClose"
           router>

    <el-menu-item index="/dashboard" v-permission="['admin']">
      <i class="el-icon-menu"></i>
      <span slot="title">{{ $t('dashboard.dashboard') }}</span>
    </el-menu-item>
    <el-menu-item index="/rule" v-permission="['admin']">
      {{ $t('rule.rule') }}
    </el-menu-item>
    <el-menu-item index="/resource" onselectstart="return false" v-permission="['admin']">
      {{ $t('resource.resource_result') }}
    </el-menu-item>
    <el-menu-item index="/account" onselectstart="return false" v-permission="['admin']">
      {{ $t('account.cloud_account') }}
    </el-menu-item>
    <el-menu-item index="/setting" onselectstart="return false">
      {{ $t('commons.system_setting') }}
    </el-menu-item>
  </el-menu>
</template>

<script>
  export default {
    name: "TopMenus",
    data() {
      return {
        activeIndex: "/",
        isCollapse: true
      }
    },
    watch: {
      '$route'(to) {
        if (to.matched.length > 0) {
          this.activeIndex = to.matched[0].path;
        }
        this.handleSelect(this.activeIndex);
      }
    },
    mounted() {
      if (this.$route.matched.length > 0) {
        this.activeIndex = this.$route.matched[0].path;
      }
    },
    methods: {
      handleSelect(index) {
        this.activeIndex = index
      },
      handleOpen(key, keyPath) {
      },
      handleClose(key, keyPath) {
      }
    }
  }
</script>
<style scoped>

  .el-menu >>> .el-menu-item {
    box-sizing: border-box;
    height: 37px;
  }

  .el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 400px;
  }

</style>
