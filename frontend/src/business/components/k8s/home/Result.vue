<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <k8s-result-header :condition.sync="condition"
                             @search="search"
                             :title="$t('k8s.result_list')"/>
      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                @filter-change="filter">
        <el-table-column type="index" min-width="2%"/>
        <el-table-column prop="name" :label="$t('k8s.name')" min-width="15%" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ $t(scope.row.name) }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" :label="$t('account.creator')" min-width="8%" show-overflow-tooltip/>
        <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="16%">
          <el-tooltip effect="dark" :content="$t('history.result') + ' CRITICAL:' + scope.row.critical + ' HIGH:' +  scope.row.high + ' MEDIUM:' + scope.row.medium + ' LOW:' + scope.row.low + ' UNKNOWN:' + scope.row.unknown" placement="top">
            <el-link type="primary" class="text-click" @click="goResource(scope.row)">
              {{ 'C:' + scope.row.critical + ' H:' +  scope.row.high + ' M:' + scope.row.medium + ' L:' + scope.row.low + ' U:' + scope.row.unknown}}
            </el-link>
          </el-tooltip>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('image.result_status')" min-width="12%" prop="resultStatus" sortable show-overflow-tooltip>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="success" v-else-if="scope.row.resultStatus === 'FINISHED'">
            <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="danger" v-else-if="scope.row.resultStatus === 'ERROR'">
            <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="warning" v-else-if="scope.row.resultStatus === 'WARNING'">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
          </el-button>
        </el-table-column>
        <el-table-column prop="updateTime" min-width="15%" :label="$t('image.last_modified')" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="12%" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Result log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="85%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-row class="el-form-item-dev" v-if="logData.length == 0">
        <span>{{ $t('resource.i18n_no_data') }}<br></span>
      </el-row>
      <el-row class="el-form-item-dev" v-if="logData.length > 0">
        <div>
          <el-row>
            <el-col :span="24">
              <div class="grid-content bg-purple-light">
                <span class="grid-content-log-span"> {{ logForm.name }}</span>
                <span class="grid-content-log-span">
                  <img :src="require(`@/assets/img/platform/${logForm.pluginIcon?logForm.pluginIcon:'k8s.png'}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                </span>
                <span class="grid-content-status-span" v-if="logForm.resultStatus === 'APPROVED'" style="color: #579df8">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'FINISHED'" style="color: #7ebf50">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'ERROR'" style="color: red;">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </span>
              </div>
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

        <div style="margin: 10px;" v-if="logForm.vulnerabilityReport">
          <el-tabs type="border-card">
            <el-tab-pane label="VulnerabilityReport">
              <div style="margin: 10px 0 0 0;">
                <h2>Details:&nbsp;</h2>
                <ul style="margin-left: 60px;">
                  <li><i>Api Version</i>: {{ logForm.vulnerabilityReport.apiVersion }}</li>
                  <li><i>Kind</i>: {{ logForm.vulnerabilityReport.kind }}</li>
                </ul>
                <div style="margin: 10px 0 0 0;">
                  <div style="margin: 10px 0 0 0;" :key="index" v-for="(item, index) in logForm.vulnerabilityReport.items">
                    <el-card class="box-card">
                      <div style="margin: 10px;">
                        <h3>Summary:&nbsp;</h3>
                        <ul style="margin-left: 60px;">
                          <li><i>Name</i>: {{ item.metadata.name }}</li>
                          <li><i>Namespace</i>: {{ item.metadata.namespace }}</li>
                          <li><i>Repository</i>: {{ item.report.artifact.repository }}</li>
                          <li><i>Critical Count</i>: {{ item.report.summary.criticalCount }}</li>
                          <li><i>High Count</i>: {{ item.report.summary.highCount }}</li>
                          <li><i>Low Count</i>:&nbsp;{{ item.report.summary.lowCount }}</li>
                          <li><i>Medium Count</i>:&nbsp;{{ item.report.summary.mediumCount }}</li>
                          <li><i>Unknown Count</i>:&nbsp;{{ item.report.summary.unknownCount }}</li>
                        </ul>
                      </div>
                      <div style="margin: 10px 0 0 0;box-shadow: 1px 1px 1px 1px #e8e8e8;" :key="index" v-for="(vulnerability, index) in item.report.vulnerabilities">
                        <div slot="header" class="clearfix clearfix-dev">
                          <el-row>
                            <el-col class="icon-title" :span="3">
                              <span>{{ vulnerability.severity.substring(0, 1) }}</span>
                            </el-col>
                            <el-col :span="15" style="margin: -7px 0 0 15px;">
                              <span style="font-size: 24px;font-weight: 500;">{{ vulnerability.title }}</span>
                            </el-col>
                            <el-col :span="6" style="float: right;">
                              <span style="font-size: 20px;color: #999;float: right;">{{ 'SCORE' }}</span>
                            </el-col>
                          </el-row>
                          <el-row style="font-size: 18px;padding: 10px;">
                            <el-col :span="20">
                              <span style="color: #888;margin: 5px;">{{ 'VULNERABILITY' }}</span>
                              <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                              <span style="margin: 5px;"><a :href="vulnerability.primaryLink">{{ vulnerability.vulnerabilityID }}</a></span>
                              <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                              <span style="margin: 5px;"><el-button type="danger" size="mini">{{ vulnerability.severity }}</el-button></span>
                              <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                              <span style="color: #444;margin: 5px;">RESOURCE: {{ vulnerability.resource }}</span>
                            </el-col>
                            <el-col :span="4" style="float: right;">
                              <span style="font-size: 20px;color: #000;float: right;">{{ vulnerability.score }}</span>
                            </el-col>
                          </el-row>
                        </div>
                        <div class="text item div-desc">
                          <el-row>
                            <i class="el-icon-s-opportunity"></i> {{ vulnerability.primaryLink }}
                          </el-row>
                        </div>
                        <div class="text div-json">
                          <el-descriptions title="Vulnerability" :column="2">
                            <el-descriptions-item label="fixedVersion">
                              {{ vulnerability.fixedVersion }}
                            </el-descriptions-item>
                            <el-descriptions-item label="installedVersion">
                              {{ vulnerability.installedVersion }}
                            </el-descriptions-item>
                          </el-descriptions>
                        </div>
                      </div>
                    </el-card>
                  </div>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="ConfigAuditReport">
              <div style="margin: 10px 0 0 0;">
                <h2>Details:&nbsp;</h2>
                <ul style="margin-left: 60px;">
                  <li><i>Api Version</i>: {{ logForm.configAuditReport.apiVersion }}</li>
                  <li><i>Kind</i>: {{ logForm.configAuditReport.kind }}</li>
                </ul>
                <div style="margin: 10px 0 0 0;">
                  <div style="margin: 10px 0 0 0;" :key="index" v-for="(item, index) in logForm.configAuditReport.items">
                    <el-card class="box-card">
                      <div style="margin: 10px;">
                        <h3>Summary:&nbsp;</h3>
                        <ul style="margin-left: 60px;">
                          <li><i>Name</i>: {{ item.metadata.name }}</li>
                          <li><i>Namespace</i>: {{ item.metadata.namespace }}</li>
                          <li><i>Critical Count</i>: {{ item.report.summary.criticalCount }}</li>
                          <li><i>High Count</i>: {{ item.report.summary.highCount }}</li>
                          <li><i>Low Count</i>:&nbsp;{{ item.report.summary.lowCount }}</li>
                          <li><i>Medium Count</i>:&nbsp;{{ item.report.summary.mediumCount }}</li>
                        </ul>
                      </div>
                      <div style="margin: 10px 0 10px 0;padding: 5px; box-shadow: 1px 1px 1px 1px #e8e8e8;" :key="index" v-for="(check, index) in item.report.checks">
                        <div slot="header" class="clearfix clearfix-dev">
                          <el-row>
                            <el-col class="icon-title" :span="3">
                              <span>{{ check.severity.substring(0, 1) }}</span>
                            </el-col>
                            <el-col :span="15" style="margin: -7px 0 0 15px;">
                              <span style="font-size: 24px;font-weight: 500;">{{ check.title }}</span>
                            </el-col>
                            <el-col :span="6" style="float: right;">
                              <span style="font-size: 20px;color: #999;float: right;">{{ 'CHECKID' }}</span>
                            </el-col>
                          </el-row>
                          <el-row style="font-size: 18px;padding: 10px;">
                            <el-col :span="20">
                              <span style="color: #888;margin: 5px;">{{ 'CHECKS' }}</span>
                              <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                              <span style="margin: 5px;">{{ check.category }}</span>
                              <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                              <span style="margin: 5px;"><el-button type="danger" size="mini">{{ check.severity }}</el-button></span>
                              <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                              <span style="color: #444;margin: 5px;">SUCCESS: {{ check.success }}</span>
                            </el-col>
                            <el-col :span="4" style="float: right;">
                              <span style="font-size: 20px;color: #000;float: right;">{{ check.checkID }}</span>
                            </el-col>
                          </el-row>
                        </div>
                        <div class="text item div-desc">
                          <el-row>
                            <i class="el-icon-s-opportunity"></i> {{ check.description }}
                          </el-row>
                        </div>
                      </div>
                    </el-card>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
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
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import K8sResultHeader from "../head/K8sResultHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../head/DialogFooter";
import {_filter, _sort} from "@/common/js/utils";
import {K8S_RESULT_CONFIGS} from "../../common/components/search/search-components";

/* eslint-disable */
export default {
  components: {
    TableOperators,
    MainContainer,
    Container,
    K8sResultHeader,
    TablePagination,
    TableOperator,
    DialogFooter,
  },
  data() {
    return {
      result: {},
      condition: {
        components: K8S_RESULT_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      severityOptions: [],
      direction: 'rtl',
      logVisible: false,
      logForm: {},
      logData: [],
      detailForm: {},
      buttons: [
        {
          tip: this.$t('resource.scan'), icon: "el-icon-refresh-right", type: "success",
          exec: this.handleScans
        },
        {
          tip: this.$t('resource.delete_result'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      cmOptions: {
        tabSize: 4,
        mode: {
          name: 'shell',
          json: true
        },
        theme: 'bespin',
        lineNumbers: true,
        line: true,
        indentWithTabs: true,
        location: "",
      },
    }
  },
  watch: {
    '$route': 'init'
  },
  methods: {
    //查询列表
    search() {
      let url = "/k8s/resultList/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    getStatus () {
      if (this.checkStatus(this.tableData)) {
        this.search();
        clearInterval(this.timer);
        this.timer = setInterval(this.getStatus,60000);
      } else {
        for (let data of this.tableData) {
          let url = "/k8s/getCloudNativeResult/";
          this.$get(url + data.id, response => {
            let result = response.data;
            if (data.resultStatus !== result.resultStatus) {
              data.resultStatus = result.resultStatus;
              data.returnSum = result.returnSum;
            }
          });
        }
      }
    },
    //是否是结束状态，返回false代表都在运行中，true代表已结束
    checkStatus (tableData) {
      let sum = 0;
      for (let row of tableData) {
        if (row.resultStatus != 'ERROR' && row.resultStatus != 'FINISHED' && row.resultStatus != 'WARNING') {
          sum++;
        }
      }
      return sum == 0;
    },
    init() {
      this.search();
    },
    sort(column) {
      _sort(column, this.condition);
      this.init();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.init();
    },
    tableRowClassName({row, rowIndex}) {
      if (rowIndex%4 === 0) {
        return 'success-row';
      } else if (rowIndex%2 === 0) {
        return 'warning-row';
      } else {
        return '';
      }
    },
    handleScans (item) {
      this.$alert(this.$t('resource.handle_scans'), '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$get("/k8s/reScan/" + item.id, response => {
              if (response.success) {
                this.search();
              }
            });
          }
        }
      });
    },
    handleDelete(obj) {
      this.$alert(this.$t('image.delete_confirm') + this.$t('k8s.result') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/k8s/deleteCloudNativeResult/" + obj.id,  res => {
              setTimeout(function () {window.location.reload()}, 2000);
              this.$success(this.$t('commons.delete_success'));
            });
          }
        }
      });
    },
    goResource (params) {
      if (params.returnSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      let p = '/k8s/resultdetails/' + params.id;
      this.$router.push({
        path: p
      }).catch(error => error);
    },
    async showResultLog (result) {
      let logUrl = "/k8s/log/";
      this.result = this.$get(logUrl + result.id, response => {
        this.logData = response.data;
      });
      let resultUrl = "/k8s/getCloudNativeResultWithBLOBs/";
      await this.$get(resultUrl + result.id, response => {
        this.logForm = response.data;
        this.logForm.vulnerabilityReport = JSON.parse(this.logForm.vulnerabilityReport);
        this.logForm.configAuditReport = JSON.parse(this.logForm.configAuditReport);
      });
      this.logVisible = true;
    },
    handleClose() {
      this.logVisible=false;
    },
  },
  activated() {
    this.init();
    this.timer = setInterval(this.getStatus, 10000);
  },
  beforeDestroy() {
    clearInterval(this.timer);
  }
}
</script>

<style scoped>
.table-content {
  width: 100%;
}

.el-table {
  cursor: pointer;
}

.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  padding: 10px 10%;
  width: 47%;
}

.rtl >>> .el-drawer__body {
  overflow-y: auto;
  padding: 20px;
}
.rtl >>> input {
  width: 100%;
}
.rtl >>> .el-select {
  width: 80%;
}
.rtl >>> .el-form-item__content {
  width: 75%;
}
.code-mirror {
  height: 600px !important;
}
.code-mirror >>> .CodeMirror {
  /* Set height, width, borders, and global font properties here */
  height: 600px !important;
}
/deep/ :focus{outline:0;}
.el-box-card {
  margin: 10px 0;
}
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
}
.grid-content-log-span {
  width: 39%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0 6px 2px;
  color: #606266;
}

.grid-content-status-span {
  width: 20%;float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
}

.el-form-item-dev >>> .bg-purple-light {
  background: #f2f2f2;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
}
.box-card >>> .clearfix-dev {
  background-color: aliceblue;
  padding: 18px 20px;
  border-bottom: 1px solid #ebeef5;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}

.table-content {
  width: 100%;
}
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
}
.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  padding: 10px 10%;
  width: 47%;
}
.tag-v{
  margin: 10px;
  cursor:pointer;
}
.rtl >>> .el-drawer__body {
  overflow-y: auto;
  padding: 20px;
}
.rtl >>> input {
  width: 100%;
}
.rtl >>> .el-select {
  width: 80%;
}
.rtl >>> .el-form-item__content {
  width: 75%;
}
.bg-purple-dark {
  background: #99a9bf;
}
.bg-purple {
  background: #d3dce6;
}
.bg-purple-light {
  background: #f2f2f2;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
}

.grid-content-log-span {
  width: 39%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0 6px 2px;
  color: #606266;
}

.grid-content-status-span {
  width: 20%;float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
}
.pure-span {
  color: #606266;
  margin: 10px 0;
}

.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  padding: 10px 2%;
  width: 46%;
}

.div-desc {
  background-color: #ecebf5;
  color: blueviolet;
  padding: 15px;
  margin: 15px;
}

.div-json {
  padding: 15px;
}

.box-card {
  width: 99%;
  border-top-color: #ff0000;
  border-top-width: 5px;
}

.icon-title {
  color: #fff;
  width: 30px;
  background-color: #32CD32;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border-radius: 30px;
  font-size: 14px;
}
.el-card >>> .diy-con-name {
  margin: 8px 3px;
}

.el-card >>> .diy-con-content {
  margin: 8px 3px;
}

.el-card >>> .diy-con-left {
  text-align: left;
  color: tomato;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
  font-size: 14px;
}

.el-card >>> .diy-con-right {
  text-align: right;
  color: #888888;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
  font-size: 12px;
}

.el-card >>> .diy-con-right-cve {
  text-align: right;
  color: #32CD32;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
  cursor:pointer;
  font-size: 12px;
}
.el-card >>> .label-class-blue {
  color: #1989fa;
}
.el-card >>> .label-bg-blue {
  background: #1989fa;
  color: #fff;
}
.el-card >>> .diy-wrapper {
  padding:10px
}
.el-card >>> .no-padding {
  padding: 0 !important;
}
.diy-wrapper >>> .left-child {
  border: 1px solid red;
}
.el-card >>> .org-chart-node-label-inner {
  border-style: solid;
  border-left-color: #ff0000;
  border-left-width: 5px;
  border-right-color:#fff;
  border-top-color:#fff;
  border-bottom-color:#fff;
}
</style>

