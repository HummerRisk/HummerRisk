<template>
  <div id="menu-bar">
    <el-row type="flex">
      <el-col :span="24">
        <el-menu class="header-menu" :unique-opened="true" mode="horizontal" router :default-active='$route.path'>

          <el-menu-item :index="'/rule/rule-group'">
            {{ $t("rule.rule_group") }}
          </el-menu-item>

          <el-menu-item :index="'/rule/rule'">
            {{ $t("rule.cloud_rule") }}
          </el-menu-item>

          <el-menu-item :index="'/rule/regulation'">
            {{ $t("resource.regulation") }}
          </el-menu-item>

          <el-menu-item :index="'/rule/rule-tag'">
            {{ $t("rule.rule_tag") }}
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
    }
  },
  methods: {
  },
  mounted() {
  }
}

</script>

<style scoped>
#menu-bar {
  border-bottom: 1px solid #E6E6E6;
  background-color: #fff;
}

</style>
