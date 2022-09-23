<template>
  <div id="menu-bar" v-if="isRouterAlive">
    <el-row type="flex">
      <el-col :span="24">
        <el-menu class="header-menu" :unique-opened="true" mode="horizontal" router :default-active='$route.path'>

          <el-menu-item :index="'/image/overview'">
            {{ $t("image.overview") }}
          </el-menu-item>

          <el-menu-item :index="'/image/imageRepo'">
            {{ $t("image.image_repo") }}
          </el-menu-item>

          <el-menu-item :index="'/image/image'">
            {{ $t("image.image_setting") }}
          </el-menu-item>

          <el-menu-item :index="'/image/result'">
            {{ $t("image.image_result") }}
          </el-menu-item>

          <el-menu-item :index="'/image/history'">
            {{ $t("image.history") }}
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
