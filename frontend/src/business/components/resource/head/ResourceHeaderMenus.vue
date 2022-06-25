<template>
  <div id="menu-bar" v-if="isRouterAlive">
    <el-row type="flex">
      <account-change :project-name="currentAccount"/>
      <el-col :span="24">
        <el-menu class="header-menu" :unique-opened="true" mode="horizontal" router :default-active='$route.path'>
          <el-menu-item :index="path">
            {{ $t("resource.cloud_resource_result") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/statistics'">
            {{ $t("resource.cloud_statistics") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/vulnResult'">
            {{ $t("vuln.vuln_result") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/serverResult'">
            {{ $t("server.server_result") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/imageResult'">
            {{ $t("image.image_result") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/packageResult'">
            {{ $t("package.package_result") }}
          </el-menu-item>

        </el-menu>
      </el-col>
    </el-row>
  </div>

</template>

<script>

import SearchList from "@/business/components/common/head/SearchList";
import {LIST_CHANGE, ResourceEvent} from "@/business/components/common/head/ListEvent";
import AccountChange from "@/business/components/common/head/AccountSwitch";

export default {
  name: "HeaderMenus",
  components: {
    SearchList,
    AccountChange,
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
  width: 130px;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
}

.el-divider--horizontal {
  margin: 0;
}
</style>
