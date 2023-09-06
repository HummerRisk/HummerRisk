<template>
  <div id="menu-bar" v-if="isRouterAlive">
    <el-row type="flex">
      <el-col :span="24">
        <el-menu class="header-menu" :unique-opened="true" mode="horizontal" router :default-active='$route.path'>

          <el-menu-item :index="'/k8s-situation/k8s-situation'">
            {{ $t("k8s.resource_situation") }}
          </el-menu-item>

          <el-menu-item :index="'/k8s-situation/k8s-topology'">
            {{ $t("k8s.cloud_topology") }}
          </el-menu-item>

          <el-menu-item :index="'/k8s-situation/rbac-topology'">
            {{ $t("k8s.rbac_topology") }}
          </el-menu-item>

          <el-menu-item :index="'/k8s-situation/k8s-sync-log'">
            {{ $t("k8s.sync_log") }}
          </el-menu-item>

        </el-menu>
      </el-col>
    </el-row>
  </div>

</template>

<script>

export default {
  name: "HeaderMenus",
  components: {},
  data() {
    return {
      isRouterAlive: true,
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
    },
  },
  watch: {
    '$route'(to) {
      this.init();
    }
  },
  mounted() {
    this.init();
  }
}

</script>

<style scoped>
#menu-bar {
  border-bottom: 1px solid #E6E6E6;
  background-color: #FFF;
}

</style>
