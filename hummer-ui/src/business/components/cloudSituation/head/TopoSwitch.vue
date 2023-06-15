<template>
  <el-menu class="header-menu" :unique-opened="true" mode="horizontal" default-active="1" router background-color="aliceblue" active-text-color="red">
    <!-- 不激活项目路由-->
    <el-menu-item index="1" v-show="false">Placeholder</el-menu-item>
    <el-submenu index="2" popper-class="submenu">
      <template v-slot:title>
        <span class="account-name" :title="name">
          {{ $t('account.cloud_account') }}: {{ name }}
        </span>
      </template>
      <search-list @topoSwitch="topoSwitch" @selectAccount="selectAccount"/>
    </el-submenu>
  </el-menu>
</template>

<script>
import SearchList from "@/business/components/common/head/SearchList";

/* eslint-disable */
export default {
  name: "TopoSwitch",
  props: {
    accountName: String
  },
  components: {SearchList},
  data() {
    return {
      name: '',
    }
  },
  watch: {
    accountName() {
      this.name = this.accountName;
    }
  },
  methods: {
    topoSwitch(accountId, accountName) {
      this.name = accountName;
      this.$emit("topoSwitch", accountId);
    },
    selectAccount(accountId, accountName) {
      this.name = accountName;
      this.$emit('selectAccount', accountId, accountName);
    },
  },
}
</script>

<style scoped>
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

.el-btn-btm {
  vertical-align: middle;
  margin: 3px 0;
}
</style>
