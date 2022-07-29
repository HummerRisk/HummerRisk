<template>
  <el-menu class="header-menu" :unique-opened="true" mode="horizontal" default-active="1" router background-color="aliceblue" active-text-color="red">
    <!-- 不激活项目路由-->
    <el-menu-item index="1" v-show="false">Placeholder</el-menu-item>
    <el-submenu index="2" popper-class="submenu">
      <template v-slot:title>
        <span class="account-name" :title="currentAccount" style="width: 250px;">
          {{ $t('account.cloud_account') }}: {{ currentAccount }}
        </span>
      </template>
      <search-list @cloudAccountSwitch="cloudAccountSwitch"/>

      <el-divider/>

      <el-menu-item :index="'/account/cloudaccount'">
        <font-awesome-icon :icon="['fa', 'plus']"/>
        <span style="padding-left: 7px;">{{ $t("account.create") }}</span>
      </el-menu-item>
      <el-menu-item :index="'/account/cloudaccount'">
        <font-awesome-icon :icon="['fa', 'list-ul']"/>
        <span style="padding-left: 7px;">{{ $t('commons.show_all') }}</span>
      </el-menu-item>
    </el-submenu>

  </el-menu>
</template>

<script>
import SearchList from "@/business/components/common/head/SearchList";
import {ACCOUNT_NAME} from "../../../../common/js/constants";

/* eslint-disable */
export default {
  name: "AccountSwitch",
  props: {
    accountName: String
  },
  components: {SearchList},
  data() {
    return {
      currentAccount: this.accountName?this.accountName:localStorage.getItem(ACCOUNT_NAME)
    }
  },
  methods: {
    cloudAccountSwitch(accountId, accountName) {
      console.log(this.currentAccount)
      this.currentAccount = accountName;
      console.log(this.currentAccount)
      this.$emit("cloudAccountSwitch", accountId);
    },
  },
}
</script>

<style scoped>
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
