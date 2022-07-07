<template>
  <div id="menu-bar" v-if="isRouterAlive">
    <el-row type="flex">
      <el-col :span="24">
        <el-menu class="header-menu" :unique-opened="true" mode="horizontal" router :default-active='$route.path'>

          <el-menu-item :index="'/account/clouddashboard'">
            {{ $t("account.overview") }}
          </el-menu-item>

          <el-menu-item :index="'/account/cloudaccount'">
            {{ $t("account.cloud_account_setting") }}
          </el-menu-item>

          <el-menu-item :index="'/account/quartztask'">
            {{ $t("account.quartz_task") }}
          </el-menu-item>

          <el-menu-item :index="'/account/rule'">
            {{ $t("rule.cloud_rule") }}
          </el-menu-item>

          <el-menu-item :index="'/account/result'">
            {{ $t("resource.cloud_resource_result") }}
          </el-menu-item>

          <el-menu-item :index="'/account/history'">
            {{ $t("account.history") }}
          </el-menu-item>

          <el-menu-item :index="'/account/accountoverview'">
            {{ $t("account.statistical_analysis") }}
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
