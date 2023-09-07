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

        <el-menu-item index="/dashboard" onselectstart="return false">
          <i class="iconfont icon-shuzihua"></i>
          <span slot="title">{{ $t('dashboard.dashboard') }}</span>
        </el-menu-item>
        <el-submenu index="1">
          <template slot="title">
            <i class="iconfont icon-guanyuyunguanjia"></i>
            <span slot="title">{{ $t('commons.cloud_mix_scan') }}</span>
          </template>
          <el-menu-item index="/account" onselectstart="return false">
            <i class="iconfont icon-gongyouyunzengzhifuwushenqingliucheng-06"></i>
            <span slot="title">{{ $t('commons.cloud_manage') }}</span>
          </el-menu-item>
          <el-menu-item index="/cloud-situation" onselectstart="return false">
            <i class="iconfont icon-taishifenxi_0"></i>
            <span slot="title">{{ $t('commons.resource_situation') }}</span>
          </el-menu-item>
          <el-menu-item index="/rule" onselectstart="return false">
            <i class="iconfont icon-guizeyinqing"></i>
            <span slot="title">{{ $t('rule.rule_manage') }}</span>
          </el-menu-item>
          <el-menu-item index="/resource" onselectstart="return false">
            <i class="iconfont icon-jishufuwu"></i>
            <span slot="title">{{ $t('resource.resource_result') }}</span>
          </el-menu-item>
          <el-menu-item index="/report" onselectstart="return false">
            <i class="iconfont icon-shujujianguan"></i>
            <span slot="title">{{ $t('reportcenter.resource_analysis') }}</span>
          </el-menu-item>
          <el-menu-item index="/event" onselectstart="return false">
            <i class="iconfont icon-anquanhegui"></i>
            <span slot="title">{{ $t('event.audit') }}</span>
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
          <el-menu-item index="/k8s-situation" onselectstart="return false">
            <i class="iconfont icon-taishiganzhi"></i>
            <span slot="title">{{ $t('commons.resource_situation') }}</span>
          </el-menu-item>
          <el-menu-item index="/server" onselectstart="return false">
            <i class="iconfont icon-fuwuqi"></i>
            <span slot="title">{{ $t('server.server_setting') }}</span>
          </el-menu-item>
          <el-menu-item index="/k8s" onselectstart="return false">
            <i class="iconfont icon-a-cskrongqifuwuKubernetesban"></i>
            <span slot="title">{{ $t('commons.k8s_scan') }}</span>
          </el-menu-item>
          <el-menu-item index="/config" onselectstart="return false">
            <i class="iconfont icon-dicengjiagou"></i>
            <span slot="title">{{ $t('commons.deployment_detection') }}</span>
          </el-menu-item>
          <el-menu-item index="/image" onselectstart="return false">
            <i class="iconfont icon-jingxiang2"></i>
            <span slot="title">{{ $t('image.image_scan') }}</span>
          </el-menu-item>
          <el-menu-item index="/code" onselectstart="return false">
            <i class="iconfont icon-yuandaimaxiayoudaima"></i>
            <span slot="title">{{ $t('commons.code_scan') }}</span>
          </el-menu-item>
          <el-menu-item index="/fs" onselectstart="return false">
            <i class="iconfont icon-wendang"></i>
            <span slot="title">{{ $t('fs.file_system') }}</span>
          </el-menu-item>
          <el-menu-item index="/sbom" onselectstart="return false">
            <i class="iconfont icon-tongjijieguo"></i>
            <span slot="title">{{ $t('commons.sbom_dependency') }}</span>
          </el-menu-item>
        </el-submenu>
        <el-menu-item index="/report-center" onselectstart="return false">
          <i class="iconfont icon-shujujianguan"></i>
          <span slot="title">{{ $t('reportcenter.reportcenter') }}</span>
        </el-menu-item>
        <el-menu-item index="/setting" onselectstart="return false">
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
