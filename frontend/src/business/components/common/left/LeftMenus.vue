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
          <i class="iconfont icon-shuzihua"></i>
          <span slot="title">{{ $t('dashboard.dashboard') }}</span>
        </el-menu-item>
        <el-submenu index="1">
          <template slot="title">
            <i class="iconfont icon-guanyuyunguanjia"></i>
            <span slot="title">{{ $t('commons.cloud_mix_scan') }}</span>
          </template>
          <el-menu-item index="/account" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-gongyouyunzengzhifuwushenqingliucheng-06"></i>
            <span slot="title">{{ $t('commons.cloud_scan') }}</span>
          </el-menu-item>
          <el-menu-item index="/vuln" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-erji-loudongsaomiaoguanli"></i>
            <span slot="title">{{ $t('vuln.vuln_setting') }}</span>
          </el-menu-item>
          <el-menu-item index="/report" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-xitonganquanheguibaobiao"></i>
            <span slot="title">{{ $t('resource.statistics') }}</span>
          </el-menu-item>
          <el-menu-item index="/oss">
            <i class="iconfont icon-ossduixiangcunchuOSS"></i>
            <span slot="title">{{ $t('oss.oss_setting') }}</span>
          </el-menu-item>
        </el-submenu>
        <el-submenu index="2">
          <template slot="title">
            <i class="iconfont icon-adsyunyuanshengshujuku"></i>
            <span slot="title">{{ $t('commons.cloud_native') }}</span>
          </template>
          <el-menu-item index="/situation" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-taishiganzhi"></i>
            <span slot="title">{{ $t('commons.resource_situation') }}</span>
          </el-menu-item>
          <el-menu-item index="/server" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-fuwuqi"></i>
            <span slot="title">{{ $t('server.server_setting') }}</span>
          </el-menu-item>
          <el-menu-item index="/k8s" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-a-cskrongqifuwuKubernetesban"></i>
            <span slot="title">{{ $t('commons.k8s_scan') }}</span>
          </el-menu-item>
          <el-menu-item index="/image" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-jingxiang2"></i>
            <span slot="title">{{ $t('image.image_scan') }}</span>
          </el-menu-item>
          <el-menu-item index="/package" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-dicengjiagou"></i>
            <span slot="title">{{ $t('commons.deployment_detection') }}</span>
          </el-menu-item>
        </el-submenu>
        <el-submenu index="3">
          <template slot="title">
            <i class="iconfont icon-erji-xunijianquanjiancha"></i>
            <span slot="title">{{ $t('commons.software_security') }}</span>
          </template>
          <el-menu-item index="/sbom" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-tongjijieguo"></i>
            <span slot="title">{{ $t('commons.sbom_dependency') }}</span>
          </el-menu-item>
          <el-menu-item index="/code" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-yuandaimaxiayoudaima"></i>
            <span slot="title">{{ $t('commons.code_scan') }}</span>
          </el-menu-item>
          <el-menu-item index="/image" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-rongqijingxiangzhongxin"></i>
            <span slot="title">{{ $t('commons.image_scan') }}</span>
          </el-menu-item>
          <el-menu-item index="/package" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-ruanjiankaifabao"></i>
            <span slot="title">{{ $t('commons.package_scan') }}</span>
          </el-menu-item>
        </el-submenu>
        <el-submenu index="4">
          <template slot="title">
            <i class="iconfont icon-menu"></i>
            <span slot="title">{{ $t('task.task_setting') }}</span>
          </template>
          <el-menu-item index="/task" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-history"></i>
            <span slot="title">{{ $t('task.task_list') }}</span>
          </el-menu-item>
          <el-menu-item index="/taskReport" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-image-text"></i>
            <span slot="title">{{ $t('task.task_report') }}</span>
          </el-menu-item>
        </el-submenu>
        <el-submenu index="5">
          <template slot="title">
            <i class="iconfont icon-xitongcaidan"></i>
            <span slot="title">{{ $t('commons.conf_setting') }}</span>
          </template>
          <el-menu-item index="/rule" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-guizeyinqing"></i>
            <span slot="title">{{ $t('rule.rule') }}</span>
          </el-menu-item>
          <el-menu-item index="/resource" onselectstart="return false" v-permission="['admin']">
            <i class="iconfont icon-jishufuwu"></i>
            <span slot="title">{{ $t('resource.resource_result') }}</span>
          </el-menu-item>
        </el-submenu>
        <el-menu-item index="/setting" onselectstart="return false" v-permission="['admin']">
          <i class="iconfont icon-guanli"></i>
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

.iconfont {
  vertical-align: middle;
  margin-right: 10px;
  width: 24px;
  height: 18px;
  text-align: center;
  font-size: 18px;
}

</style>
