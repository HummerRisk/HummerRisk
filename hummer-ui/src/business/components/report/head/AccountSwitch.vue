<template>
  <el-menu class="header-menu" :unique-opened="true" mode="horizontal" default-active="1" router background-color="aliceblue" active-text-color="red">
    <!-- 不激活项目路由-->
    <el-menu-item index="1" v-show="false">Placeholder</el-menu-item>
    <el-submenu index="2" popper-class="submenu">
      <template v-slot:title>
        <span class="account-name" :title="currentAccount">
          {{ $t('account.cloud_account') }}: {{ currentAccount }}
        </span>
      </template>
      <search-list :accountId="accountId" @cloudAccountSwitch="cloudAccountSwitch"/>

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

    <el-button class="el-btn-btm" type="warning" plain size="small" @click="pdfDown">{{ $t('pdf.export_pdf') }}</el-button>
    <el-button class="el-btn-btm" type="success" plain size="small" @click="openDownload">{{ $t('report.download_account') }}</el-button>

  </el-menu>
</template>

<script>
import SearchList from "@/business/components/common/head/SearchList";
import htmlToPdf from "@/common/js/htmlToPdf";

/* eslint-disable */
export default {
  name: "AccountSwitch",
  props: {
    accountId: String
  },
  components: {SearchList},
  data() {
    return {
      htmlTitle: this.$t('pdf.html_title'),
      currentAccount: ''
    }
  },
  methods: {
    cloudAccountSwitch(accountId, accountName) {
      this.currentAccount = accountName;
      this.$emit("cloudAccountSwitch", accountId, accountName);
    },
    openDownload() {
      this.$emit('openDownload');
    },
    //下载pdf
    pdfDown() {
      htmlToPdf.getPdfById(this.htmlTitle);
    },
  },
}
</script>

<style scoped>
.account-name {
  display: inline-block;
  width: 199px;
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
