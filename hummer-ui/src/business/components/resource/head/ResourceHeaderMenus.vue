<template>
  <div id="menu-bar" v-if="isRouterAlive">
    <el-row type="flex">
      <el-col :span="24">
        <el-menu class="header-menu" :unique-opened="true" mode="horizontal" router :default-active='$route.path'>

          <el-menu-item :index="'/resource/cloud-dashboard'">
            {{ $t("account.overview") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/result'">
            {{ $t("resource.cloud_resource_result") }}
          </el-menu-item>

          <el-menu-item :index="'/resource/cloud-history'">
            {{ $t("account.history") }}
          </el-menu-item>

        </el-menu>
      </el-col>
    </el-row>
  </div>

</template>

<script>

import {LIST_CHANGE, ResourceEvent} from "@/common/js/ListEvent";

export default {
  name: "HeaderMenus",
  components: {
  },
  data() {
    return {
      isRouterAlive: true,
      currentAccount: '',
    }
  },
  methods: {
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
  },
  beforeDestroy() {
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
