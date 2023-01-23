<template>
  <main-container class="card">

    <el-card class="table-card el-row-card">
      <k8s-switch @k8sSwitch="k8sSwitch" @selectAccount="selectAccount"/>
    </el-card>

    <el-card class="table-card" v-loading="result.loading">
      <rbac-chart :accountId="accountId"/>
    </el-card>

    <!--Result log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="85%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-row class="el-form-item-dev" v-if="logData.length == 0">
        <span>{{ $t('resource.i18n_no_data') }}<br></span>
      </el-row>
      <el-row class="el-form-item-dev" v-if="logData.length > 0">
        <div>
          <el-row style="background: #e5e9f2;">
              <el-col :span="8" style="background: #e5e9f2;">
                <span class="grid-content-log-span"> {{ logForm.name }}</span>
              </el-col>
              <el-col :span="8" style="background: #e5e9f2;">
                <span class="grid-content-log-span">
                    <img :src="require(`@/assets/img/platform/${logForm.pluginIcon?logForm.pluginIcon:'k8s.png'}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                  </span>
              </el-col>
              <el-col :span="8" style="background: #e5e9f2;">
                 <span class="grid-content-status-span" v-if="logForm.resultStatus === 'APPROVED'" style="color: #579df8">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                  </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'FINISHED'" style="color: #7ebf50">
                    <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                  </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'ERROR'" style="color: red;">
                    <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                  </span>
              </el-col>
          </el-row>
        </div>
        <el-table :show-header="false" :data="logData" class="adjust-table table-content">
          <el-table-column>
            <template v-slot:default="scope">
              <div class="bg-purple-div">
                <span
                  v-bind:class="{true: 'color-red', false: ''}[scope.row.result == false]">
                      {{ scope.row.createTime | timestampFormatDate }}
                      {{ scope.row.operator }}
                      {{ scope.row.operation }}
                      {{ scope.row.output }}<br>
                </span>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <log-form :logForm="logForm"/>
      </el-row>
      <template v-slot:footer>
        <dialog-footer
          @cancel="logVisible = false"
          @confirm="logVisible = false"/>
      </template>
    </el-drawer>
    <!--Result log-->
  </main-container>
</template>
<script>
import MainContainer from "../../common/components/MainContainer";
import K8sSwitch from "@/business/components/common/head/K8sSwitch";
import SearchList from "@/business/components/k8sSituation/home/SearchList";
import LogForm from "@/business/components/k8s/home/LogForm";
import * as d3 from 'd3';
import htmlToPdf from "@/common/js/htmlToPdf";
import RbacChart from "@/business/components/k8sSituation/head/RbacChart";

/* eslint-disable */
export default {
  components: {
    MainContainer,
    SearchList,
    LogForm,
    d3,
    RbacChart,
    K8sSwitch,
  },
  data() {
    return {
      result: {},
      direction: 'rtl',
      logVisible: false,
      logForm: {},
      logData: [],
      htmlTitle: this.$t('pdf.html_title'),
      option: {},
      accountId: '',
      currentAccount: '',
    };
  },
  methods: {
    init() {
    },

    handleClose() {
      this.logVisible=false;
    },
    handleClick(tab, event) {
    },
    //下载pdf
    pdfDown() {
      htmlToPdf.getPdfById(this.htmlTitle);
    },
    k8sSwitch(accountId) {
      this.accountId = accountId;
      this.init();
    },
    selectAccount(accountId, accountName) {
      this.accountId = accountId;
      this.currentAccount = accountName;
    },
  },

  mounted() {
    this.init();
  }

}
</script>

<style scoped>
.table-card >>> .el-card__body {
  padding: 0;
}
.table-card >>> .el-tabs__content{
  padding: 0;
}
</style>
