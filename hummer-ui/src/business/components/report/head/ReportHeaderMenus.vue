<template>
  <div id="menu-bar" v-if="isRouterAlive">
    <el-row type="flex">
      <el-col :span="24">
        <el-menu class="header-menu" :unique-opened="true" mode="horizontal" router :default-active='$route.path'>

          <el-menu-item :index="'/report/cloudReport'">
            {{ $t("resource.cloud_statistics") }}
          </el-menu-item>

          <el-menu-item :index="'/report/cloudHistory'">
            {{ $t("account.history") }}
          </el-menu-item>

          <el-menu-item :index="'/report/accountOverview'">
            {{ $t("account.statistical_analysis") }}
          </el-menu-item>

          <el-menu-item :index="'/report/vulnReport'">
            {{ $t("vuln.vuln_statistics") }}
          </el-menu-item>

          <el-menu-item :index="'/report/vulnHistory'">
            {{ $t("vuln.history") }}
          </el-menu-item>

          <el-menu-item :index="'/report/vlunOverview'">
            {{ $t("vuln.statistical_analysis") }}
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
