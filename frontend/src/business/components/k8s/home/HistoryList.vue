<template>
  <el-row :gutter="24">
    <el-col :span="24">
      <el-card class="box-card" shadow="always">
        <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                  @filter-change="filter">
          <el-table-column type="index" min-width="2%"/>
          <el-table-column prop="name" :label="$t('k8s.name')" min-width="15%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
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
          <el-table-column v-slot:default="scope" :label="$t('image.result_status')" min-width="11%" prop="resultStatus" sortable show-overflow-tooltip>
            <el-button @click="showResultLog(scope.row)" plain size="mini" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button @click="showResultLog(scope.row)" plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button @click="showResultLog(scope.row)" plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button @click="showResultLog(scope.row)" plain size="mini" type="success" v-else-if="scope.row.resultStatus === 'FINISHED'">
              <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
            </el-button>
            <el-button @click="showResultLog(scope.row)" plain size="mini" type="danger" v-else-if="scope.row.resultStatus === 'ERROR'">
              <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
            </el-button>
            <el-button @click="showResultLog(scope.row)" plain size="mini" type="warning" v-else-if="scope.row.resultStatus === 'WARNING'">
              <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
            </el-button>
          </el-table-column>
          <el-table-column prop="updateTime" min-width="14%" :label="$t('image.last_modified')" sortable>
            <template v-slot:default="scope">
              <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('resource.resource_result')" min-width="10%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>

      </el-card>

      <!--History statistics-->
      <el-drawer class="rtl" :title="$t('resource.i18n_not_compliance')" :visible.sync="statisticsList" size="85%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div>
          <el-table border :data="statisticsData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName">
            <el-table-column type="index" min-width="2%"/>
            <el-table-column min-width="10%" :label="'Resource'" prop="resource" v-slot:default="scope">
              <span style="font-weight:bold;color: #000000;">{{ scope.row.resource }}</span>
            </el-table-column>
            <el-table-column min-width="10%" :label="'VulnerabilityID'" prop="vulnerabilityId">
            </el-table-column>
            <el-table-column min-width="7%" :label="'Severity'" prop="severity" v-slot:default="scope">
              <span v-if="scope.row.severity === 'CRITICAL'" style="color: #8B0000;">{{ scope.row.severity }}</span>
              <span v-if="scope.row.severity === 'HIGH'" style="color: #FF4D4D;">{{ scope.row.severity }}</span>
              <span v-if="scope.row.severity === 'MEDIUM'" style="color: #FF8000;">{{ scope.row.severity }}</span>
              <span v-if="scope.row.severity === 'LOW'" style="color: #336D9F;">{{ scope.row.severity }}</span>
              <span v-if="scope.row.severity === 'UNKNOWN'" style="color: #67C23A;">{{ scope.row.severity }}</span>
            </el-table-column>
            <el-table-column min-width="5%" :label="'Score'" prop="score" v-slot:default="scope">
              {{ scope.row.score?scope.row.score:'N/A' }}
            </el-table-column>
            <el-table-column :label="'InstalledVersion'" min-width="10%" prop="installedVersion">
            </el-table-column>
            <el-table-column min-width="10%" :label="'FixedVersion'" prop="fixedVersion">
            </el-table-column>
            <el-table-column min-width="25%" :label="'PrimaryURL'" prop="primaryUrl" v-slot:default="scope">
              <span>{{ scope.row.title }}</span>
              <br>
              <el-link type="primary" style="color: #0000e4;" :href="scope.row.primaryUrl" target="_blank">{{ scope.row.primaryUrl }}</el-link>
            </el-table-column>
          </el-table>
        </div>
      </el-drawer>
      <!--History statistics-->

      <!--History status-->
      <el-drawer class="rtl" :title="$t('resource.i18n_resource_scanning_log')" :visible.sync="logVisible" size="85%" :before-close="handleClose" :direction="direction"
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
      <!--History status-->

      <!--History output list-->
      <el-drawer class="rtl" :title="$t('commons.history')" :visible.sync="visibleList" size="85%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div>
          <el-table border :data="outputListData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName">
            <el-table-column prop="name" :label="$t('k8s.name')" min-width="15%" show-overflow-tooltip>
              <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
              </template>
            </el-table-column>
            <el-table-column prop="userName" :label="$t('account.creator')" min-width="8%" show-overflow-tooltip/>
            <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="16%">
              {{ 'C:' + scope.row.critical + ' H:' +  scope.row.high + ' M:' + scope.row.medium + ' L:' + scope.row.low + ' U:' + scope.row.unknown}}
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('image.result_status')" min-width="11%" prop="resultStatus" sortable show-overflow-tooltip>
              <el-button plain size="mini" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
                <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
              </el-button>
              <el-button plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
                <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
              </el-button>
              <el-button plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
                <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
              </el-button>
              <el-button plain size="mini" type="success" v-else-if="scope.row.resultStatus === 'FINISHED'">
                <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
              </el-button>
              <el-button plain size="mini" type="danger" v-else-if="scope.row.resultStatus === 'ERROR'">
                <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
              </el-button>
              <el-button plain size="mini" type="warning" v-else-if="scope.row.resultStatus === 'WARNING'">
                <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
              </el-button>
            </el-table-column>
            <el-table-column prop="updateTime" min-width="14%" :label="$t('image.last_modified')" sortable>
              <template v-slot:default="scope">
                <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
              </template>
            </el-table-column>
            <el-table-column min-width="10%" :label="$t('commons.operating')" fixed="right" show-overflow-tooltip>
              <template v-slot:default="scope">
                <table-operators :buttons="diffButtons" :row="scope.row"/>
              </template>
            </el-table-column>
          </el-table>
          <table-pagination :change="outputListDataSearch" :current-page.sync="outputListPage" :page-size.sync="outputListPageSize" :total="outputListTotal"/>
        </div>
      </el-drawer>
      <!--History output list-->

      <!--History Compared-->
      <el-dialog :title="$t('dashboard.online_comparison')" width="90%" :visible.sync="innerDrawer" :close-on-click-modal="false">
        <el-form>
          <code-diff
            :old-string="oldStr"
            :new-string="newStr"
            outputFormat="side-by-side"
            :isShowNoChange="true"
            :drawFileList="true"
            :context="10"/>
        </el-form>
      </el-dialog>
      <!--History Compared-->

    </el-col>
  </el-row>
</template>

<script>

import {_filter, _sort} from "@/common/js/utils";
import TablePagination from "../../common/pagination/TablePagination";
import DialogFooter from "../../common/components/DialogFooter";
import TableOperators from "../../common/components/TableOperators";
import CodeDiff from 'vue-code-diff';
/* eslint-disable */
  export default {
    name: "HistoryList",
    components: {
      TablePagination,
      DialogFooter,
      TableOperators,
      CodeDiff,
    },
    props: {
      selectNodeIds: Array,
    },
    watch: {
      selectNodeIds() {
        this.search();
      },
    },
    data() {
      return {
        tags: [],
        tableData: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        condition: {
          components: Object
        },
        direction: 'rtl',
        visibleList: false,
        oldStr: 'old code',
        newStr: 'new code',
        innerDrawer: false,
        script: '',
        buttons: [
          {
            tip: this.$t('resource.resource_result'), icon: "el-icon-s-data", type: "success",
            exec: this.handleOpen
          },
          {
            tip: this.$t('resource.delete_result'), icon: "el-icon-delete", type: "danger",
            exec: this.handleDelete
          }
        ],
        diffButtons: [
          {
            tip: this.$t('commons.diff'), icon: "el-icon-sort", type: "primary",
            exec: this.imageDiffOpen
          }
        ],
        outputListData: [],
        outputListPage: 1,
        outputListPageSize: 10,
        outputListTotal: 0,
        outputListSearchData: {},
        statisticsList: false,
        logVisible: false,
        statisticsData: [],
        logForm: {},
        logData: [],
        filterJson: this.filterJsonKeyAndValue,
      }
    },
    computed: {
    },
    methods: {
      init() {
        this.search();
      },
      //查询列表
      async search() {
        let url = "/k8s/history/" + this.currentPage + "/" + this.pageSize;
        if (!!this.selectNodeIds) {
          this.condition.codeId = this.selectNodeIds[0];
        } else {
          this.condition.codeId = null;
        }
        this.result = await this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
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
      handleOpen(item) {
        console.log(item)
        this.outputListSearchData = item;
        this.outputListDataSearch();
        this.oldStr = item.trivyJson;
        this.visibleList =  true;
      },
      handleDelete(obj) {
        this.$alert(this.$t('k8s.delete_confirm') + this.$t('k8s.result') + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$get("/k8s/deleteHistoryK8sResult/" + obj.id,  res => {
                setTimeout(function () {window.location.reload()}, 2000);
                this.$success(this.$t('commons.delete_success'));
              });
            }
          }
        });
      },
      async outputListDataSearch() {
        let item = this.outputListSearchData;
        await this.$post("/k8s/history/" + this.outputListPage + "/" + this.outputListPageSize, {cloudNativeId: item.cloudNativeId}, response => {
          let data = response.data;
          this.outputListTotal = data.itemCount;
          this.outputListData = data.listObject;
        });
      },
      imageDiffOpen(item) {
        this.innerDrawerComparison(item);
      },
      handleClose() {
        this.visibleList = false;
        this.statisticsList = false;
        this.logVisible = false;
      },
      innerDrawerClose() {
        this.innerDrawer = false;
      },
      innerDrawerComparison(item) {
        this.newStr = item.vulnerabilityReport?item.vulnerabilityReport:"[]";
        this.innerDrawer = true;
      },
      goResource(params) {
        if (params.returnSum == 0) {
          this.$warning(this.$t('resource.no_resources_allowed'));
          return;
        }
        let url = "/k8s/historyResultItemList";
        this.result = this.$post(url, {resultId: params.id}, response => {
          let data = response.data;
          this.statisticsData = data;
          this.statisticsList = true;
        });
        this.result = this.$get("/k8s/metricChart/"+ this.resultId, response => {
          this.content = response.data;
        });
      },
      showResultLog (result) {
        let logUrl = "/k8s/log/";
        this.result = this.$get(logUrl + result.id, response => {
          this.logData = response.data;
        });
        let resultUrl = "/k8s/getCloudNativeResultWithBLOBs/";
        this.result = this.$get(resultUrl + result.id, response => {
          this.logForm = response.data;
          this.logForm.vulnerabilityReport = JSON.parse(this.logForm.vulnerabilityReport);
        });
        this.logVisible = true;
      },
      filterJsonKeyAndValue(json) {
        //json is json object , not array -- harris
        let list = json;
        if(typeof json === 'object') {
          list = json;
        } else {
          list = JSON.parse(json);
        }

        let jsonKeyAndValue = [];

        for (let item in list) {
          let flag = true;
          let value = list[item];
          //string && boolean的值直接显示, object是[{}]
          if (typeof (value) === 'number') {
            value = String(value);
          }
          if (typeof (value) === 'object') {
            if (value !== null && JSON.stringify(value) !== '[]' && JSON.stringify(value) !== '{}') {
              flag = false;
            }
            if (JSON.stringify(value) === '[]' || JSON.stringify(value) === '{}') {
              value = "";
            }
          }

          if (item.indexOf('$$') === -1 && item !== 'show') {
            let map = {key: item, value: value, flag: flag};
            jsonKeyAndValue.push(map);
          }
        }
        return jsonKeyAndValue;
      },
    },
    created() {
      this.init();
    },
  }
</script>

<style scoped>
.code-mirror {
  height: 800px !important;
}
.code-mirror >>> .CodeMirror {
  /* Set height, width, borders, and global font properties here */
  height: 800px !important;
}
.el-drawer__header {
  margin-bottom: 0;
}
:focus{outline:0;}
.rtl >>> .el-drawer__body {
  overflow-y: auto;
  padding: 20px;
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
.box-card >>> .el-card__header {
  background-color: aliceblue;
}

.div-desc {
  background-color: #ecebf5;
  color: blueviolet;
  padding: 15px;
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
