<template>
  <div>
    <!-- :collapse="isCollapse"   class="el-menu-vertical" 动态控制导航菜单的收起与展开  router：让index作为 path 进行路由跳转 -->
    <el-menu menu-trigger="click"
             class="el-menu-vertical-demo"
             @open="handleOpen"
             @close="handleClose"
             :default-active="activeIndex"
             @select="handleSelect"
             :collapse="collapse"
             router>

      <el-menu-item index="/dashboard" onselectstart="return false" v-permission="['admin']">
        <i class="el-icon-pie-chart"></i>
        <span slot="title">{{ $t('dashboard.dashboard') }}</span>
      </el-menu-item>
      <el-menu-item index="/rule" onselectstart="return false" v-permission="['admin']">
        <i class="el-icon-collection"></i>
        <span slot="title">{{ $t('rule.rule') }}</span>
      </el-menu-item>
      <el-menu-item index="/resource" onselectstart="return false" v-permission="['admin']">
        <i class="el-icon-postcard"></i>
        <span slot="title">{{ $t('resource.resource_result') }}</span>
      </el-menu-item>
      <el-menu-item index="/account" onselectstart="return false" v-permission="['admin']">
        <i class="el-icon-cloudy"></i>
        <span slot="title">{{ $t('account.account_setting') }}</span>
      </el-menu-item>
      <el-menu-item index="/vuln" onselectstart="return false" v-permission="['admin']">
        <i class="el-icon-crop"></i>
        <span slot="title">{{ $t('vuln.vuln_setting') }}</span>
      </el-menu-item>
      <el-menu-item index="/server" onselectstart="return false" v-permission="['admin']">
        <i class="el-icon-monitor"></i>
        <span slot="title">{{ $t('server.server_setting') }}</span>
      </el-menu-item>
      <el-menu-item index="/image" onselectstart="return false" v-permission="['admin']">
        <i class="el-icon-picture-outline-round"></i>
        <span slot="title">{{ $t('image.image_scan') }}</span>
      </el-menu-item>
      <el-menu-item index="/package" onselectstart="return false" v-permission="['admin']">
        <i class="el-icon-box"></i>
        <span slot="title">{{ $t('package.package_scan') }}</span>
      </el-menu-item>
      <el-menu-item index="/oss" onselectstart="return false" v-permission="['admin']">
        <i class="el-icon-folder-opened"></i>
        <span slot="title">{{ $t('oss.oss_setting') }}</span>
      </el-menu-item>
      <el-menu-item index="/task" onselectstart="return false" v-permission="['admin']">
        <i class="el-icon-s-operation"></i>
        <span slot="title">{{ $t('task.task_setting') }}</span>
      </el-menu-item>
      <el-menu-item index="/setting" onselectstart="return false" v-permission="['admin']">
        <i class="el-icon-setting"></i>
        <span slot="title">{{ $t('commons.system_setting') }}</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script>
  export default {
    name: "LeftMenus",
    props: {
      collapse: Boolean,
    },
    data() {
      return {
        activeIndex: "/",
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
        this.activeIndex = index;
      },
      handleOpen(key, keyPath) {
      },
      handleClose(key, keyPath) {
      }
    }
  }
</script>
<style scoped>

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}

</style>
