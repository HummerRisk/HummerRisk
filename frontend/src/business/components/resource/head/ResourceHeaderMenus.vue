<template>
  <div id="menu-bar" v-if="isRouterAlive">
    <el-row type="flex">
      <el-col :span="24">
        <el-menu class="header-menu" :unique-opened="true" mode="horizontal" router :default-active='$route.path'>
          <el-menu-item :index="path">
            {{ $t("resource.cloud_resource_result") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/VulnResult'">
            {{ $t("vuln.vuln_result") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/ServerResult'">
            {{ $t("server.server_result") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/K8sResult'">
            {{ $t("k8s.result") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/ConfigResult'">
            {{ $t("config.config_result") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/ImageResult'">
            {{ $t("image.image_result") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/CodeResult'">
            {{ $t("code.code_result") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/FsResult'">
            {{ $t("fs.fs_result") }}
          </el-menu-item>
        </el-menu>
      </el-col>
    </el-row>
  </div>

</template>

<script>

import {LIST_CHANGE, ResourceEvent} from "@/business/components/common/head/ListEvent";

export default {
  name: "HeaderMenus",
  components: {
  },
  data() {
    return {
      path: '/resource/result',
      isRouterAlive: true,
      currentAccount: '',
    }
  },
  methods: {
    reload() {
      this.isRouterAlive = false;
      this.$nextTick(function () {
        this.isRouterAlive = true;
      });
    },
    init() {
      let path = this.$route.path;
      if (path.indexOf("/resource/result") >= 0 || path.indexOf("/resource/ResultDetails") >= 0) {
        this.path = path;
        this.reload();
      }
    },
    registerEvents() {
      ResourceEvent.$on(LIST_CHANGE, () => {
        this.$refs.planRecent.recent();
        this.$refs.caseRecent.recent();
      });
    }
  },
  watch: {
    '$route'(to) {
      this.init();
    }
  },
  mounted() {
    this.init();
    this.registerEvents();
  },
  beforeDestroy() {
    ResourceEvent.$off(LIST_CHANGE);
  }
}

</script>

<style scoped>
#menu-bar {
  border-bottom: 1px solid #E6E6E6;
  background-color: #FFF;
}
.account-name {
  display: inline-block;
  width: 250px;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
}

.el-divider--horizontal {
  margin: 0;
}
</style>
