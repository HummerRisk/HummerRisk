<template>
  <el-menu class="header-menu" :unique-opened="true" mode="horizontal" default-active="1" router background-color="aliceblue" active-text-color="red">
    <!-- 不激活项目路由-->
    <el-menu-item index="1" v-show="false">Placeholder</el-menu-item>
    <el-submenu index="2" popper-class="submenu">
      <template v-slot:title>
        <span class="account-name" :title="currentAccount">
          {{ $t('account.cloud_account') }}: {{ currentAccount }} | {{ $t('commons.last_scan_time') }}: {{ createTime | timestampFormatDate }}
        </span>
      </template>
      <project-search-list @projectSwitch="projectSwitch" @selectProject="selectProject"/>

      <el-divider/>

      <el-menu-item :index="'/account/cloud-account'">
        <font-awesome-icon :icon="['fa', 'plus']"/>
        <span style="padding-left: 7px;">{{ $t("account.create") }}</span>
      </el-menu-item>
      <el-menu-item :index="'/account/cloud-account'">
        <font-awesome-icon :icon="['fa', 'list-ul']"/>
        <span style="padding-left: 7px;">{{ $t('commons.show_all') }}</span>
      </el-menu-item>
    </el-submenu>
  </el-menu>
</template>

<script>
import ProjectSearchList from "@/business/components/common/head/ProjectSearchList";

/* eslint-disable */
export default {
  name: "ProjectSwitch",
  props: {
    accountName: String
  },
  components: {ProjectSearchList},
  data() {
    return {
      currentAccount: '',
      createTime: '',
    }
  },
  methods: {
    projectSwitch(accountId, accountName, createTime) {
      this.currentAccount = accountName;
      this.createTime = createTime;
      this.$emit("projectSwitch", accountId, createTime);
    },
    selectProject(accountId, accountName) {
      this.currentAccount = accountName;
      this.$emit('selectProject', accountId, accountName);
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
