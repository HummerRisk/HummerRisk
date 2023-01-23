<template>
  <el-menu class="header-menu" :unique-opened="true" mode="horizontal" default-active="1" router background-color="aliceblue" active-text-color="red">
    <!-- 不激活项目路由-->
    <el-menu-item index="1" v-show="false">Placeholder</el-menu-item>
    <el-submenu index="2" popper-class="submenu">
      <template v-slot:title>
        <span class="account-name" :title="currentAccount">
          {{ $t('k8s.k8s_setting') }}: {{ currentAccount }}
        </span>
      </template>
      <k8s-search-list @k8sSwitch="k8sSwitch" @selectAccount="selectAccount"/>

      <el-divider/>

      <el-menu-item :index="'/k8s/k8s'">
        <font-awesome-icon :icon="['fa', 'plus']"/>
        <span style="padding-left: 7px;">{{ $t("k8s.k8s_create") }}</span>
      </el-menu-item>
      <el-menu-item :index="'/k8s/k8s'">
        <font-awesome-icon :icon="['fa', 'list-ul']"/>
        <span style="padding-left: 7px;">{{ $t('commons.show_all') }}</span>
      </el-menu-item>
    </el-submenu>
  </el-menu>
</template>

<script>
import K8sSearchList from "@/business/components/common/head/K8sSearchList";
import {K8S_NAME} from "../../../../common/js/constants";

/* eslint-disable */
export default {
  name: "K8sSwitch",
  props: {
    accountName: String
  },
  components: {K8sSearchList},
  data() {
    return {
      currentAccount: this.accountName?this.accountName:localStorage.getItem(K8S_NAME)
    }
  },
  methods: {
    k8sSwitch(accountId, accountName) {
      this.currentAccount = accountName;
      this.$emit("k8sSwitch", accountId);
    },
    selectAccount(accountId, accountName) {
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
