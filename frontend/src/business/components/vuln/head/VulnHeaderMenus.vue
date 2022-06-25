<template>
  <div id="menu-bar" v-if="isRouterAlive">
    <el-row type="flex">
      <el-col :span="24">
        <el-menu class="header-menu" :unique-opened="true" mode="horizontal" router :default-active='$route.path'>

          <el-menu-item :index="'/vuln/vuln'">
            {{ $t("vuln.vuln_settings") }}
          </el-menu-item>

          <el-menu-item :index="'/vuln/rule'">
            {{ $t("vuln.vuln_rule") }}
          </el-menu-item>

          <el-menu-item :index="'/vuln/result'">
            {{ $t("vuln.vuln_result") }}
          </el-menu-item>

          <el-menu-item :index="'/vuln/history'">
            {{ $t("vuln.history") }}
          </el-menu-item>

          <el-menu-item :index="'/vuln/vlunOverview'">
            {{ $t("vuln.statistical_analysis") }}
          </el-menu-item>

        </el-menu>
      </el-col>
      <el-col :span="12"/>
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
