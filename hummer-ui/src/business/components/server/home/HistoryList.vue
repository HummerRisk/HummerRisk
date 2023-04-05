<template>
  <main-container v-loading="result.loading">

    <el-card class="table-card">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :items="items" :columnNames="columnNames" :showName="false"
                      :checkedColumnNames="checkedColumnNames" :checkAll="checkAll" :isIndeterminate="isIndeterminate"
                      @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange" @handleCheckAllChange="handleCheckAllChange"/>
      </template>
        <hide-table
            :table-data="tableData"
            @sort-change="sort"
            @filter-change="filter"
            @select-all="select"
            @select="select"
        >
          <el-table-column type="selection" id="selection"  prop="selection" min-width="50">
          </el-table-column>
          <el-table-column type="index" min-width="40"/>
          <el-table-column prop="serverName" :label="$t('server.server_name')" v-if="checkedColumnNames.includes('serverName')" min-width="130" show-overflow-tooltip></el-table-column>
          <el-table-column prop="ip" :label="'IP'" v-if="checkedColumnNames.includes('ip')" min-width="140" show-overflow-tooltip v-slot:default="scope">
            {{ scope.row.ip }} : {{ scope.row.port }}
          </el-table-column>
          <el-table-column prop="ruleName" :label="$t('server.rule_name')" v-if="checkedColumnNames.includes('ruleName')" min-width="160" show-overflow-tooltip></el-table-column>
          <el-table-column prop="isSeverity" :label="$t('server.is_severity')" v-if="checkedColumnNames.includes('severity')" min-width="100" show-overflow-tooltip v-slot:default="scope">
            <el-tooltip class="item" effect="dark" :content="scope.row.returnLog" placement="top">
              <span v-if="scope.row.isSeverity" style="color: #46ad59">{{ $t('resource.risk_free') }}</span>
              <span v-if="!scope.row.isSeverity" style="color: #f84846">{{ $t('resource.risky') }}</span>
            </el-tooltip>
          </el-table-column>
          <el-table-column prop="type" :label="$t('commons.type')" v-if="checkedColumnNames.includes('type')" min-width="70" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span v-if="scope.row.type === 'linux'">Linux</span>
              <span v-if="scope.row.type === 'windows'">Windows</span>
              <span v-if="!scope.row.type">N/A</span>
            </template>
          </el-table-column>
          <el-table-column v-slot:default="scope" :label="$t('server.result_status')" v-if="checkedColumnNames.includes('resultStatus')" min-width="130" prop="resultStatus" sortable show-overflow-tooltip>
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
          <el-table-column prop="isSeverity" v-if="checkedColumnNames.includes('isSeverity')" :label="$t('server.is_severity')" min-width="110" show-overflow-tooltip v-slot:default="scope" sortable>
            <el-tooltip class="item" effect="dark" :content="scope.row.returnLog" placement="top">
              <span v-if="scope.row.isSeverity" style="color: #46ad59">{{ $t('resource.risk_free') }}</span>
              <span v-if="!scope.row.isSeverity" style="color: #f84846">{{ $t('resource.risky') }}</span>
            </el-tooltip>
          </el-table-column>
          <el-table-column prop="updateTime" min-width="160"  v-if="checkedColumnNames.includes('updateTime')" :label="$t('commons.create_time')" sortable>
            <template v-slot:default="scope">
              <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('commons.operating')" min-width="90" show-overflow-tooltip fixed="right">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </hide-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>

      </el-card>

      <!--History statistics-->
      <el-drawer class="rtl" :title="$t('resource.i18n_not_compliance')" :visible.sync="statisticsList" size="85%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div>
          <el-table border :data="statisticsData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName">
            <el-table-column type="index" min-width="2%"/>
            <el-table-column min-width="10%" :label="'PkgName'" prop="pkgName" v-slot:default="scope">
              <span style="font-weight:bold;color: #000000;">{{ scope.row.pkgName }}</span>
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
            <el-table-column :label="'InstalledVersion'" min-width="10%" prop="installedVersion">
            </el-table-column>
            <el-table-column min-width="10%" :label="'FixedVersion'" prop="fixedVersion">
            </el-table-column>
            <el-table-column min-width="25%" :label="'PrimaryURL'" prop="primaryUrl" v-slot:default="scope">
              <span>{{ scope.row.title }}</span>
              <el-link type="primary" style="color: #0000e4;" :href="scope.row.primaryUrl" target="_blank">{{ scope.row.primaryUrl }}</el-link>
            </el-table-column>
          </el-table>
        </div>
      </el-drawer>
      <!--History statistics-->

      <!--Result log-->
      <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="75%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-row class="el-form-item-dev" v-if="logData.length == 0">
          <span>{{ $t('resource.i18n_no_data') }}<br></span>
        </el-row>
        <el-row class="el-form-item-dev" v-if="logData.length > 0">
          <div>
            <el-row>
              <el-col :span="24">
                <div class="grid-content bg-purple-light">
                  <span class="grid-content-log-span"> {{ logForm.ruleName }}</span>
                  <span class="grid-content-log-span">
                      <img :src="require(`@/assets/img/platform/${logForm.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                       &nbsp;&nbsp; {{ logForm.serverGroupName }} : {{ logForm.serverName }}
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
              <el-col :span="24">
                <div class="grid-content bg-purple-light">
                  <span class="grid-content-log-span"> {{ logForm.ruleDesc }}</span>
                  <span class="grid-content-log-span">
                    {{ logForm.ip }}
                    <span v-if="logForm.isSeverity" style="color: #46ad59">({{ $t('resource.risk_free') }})</span>
                    <span v-if="!logForm.isSeverity" style="color: #f84846">({{ $t('resource.risky') }})</span>
                  </span>
                  <span class="grid-content-status-span">
                  <rule-type :row="logForm"/>
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
          <el-form style="margin: 15px 0 0 0">
            <h5 style="margin: 10px;">{{ $t('server.server_rule') }}</h5>
            <el-form-item>
              <codemirror ref="cmEditor" v-model="logForm.rule" class="code-mirror" :options="cmOptions" />
            </el-form-item>
            <h5 style="margin: 10px;">{{ $t('server.server_result') }}</h5>
            <el-form-item>
              <codemirror ref="cmEditor" v-model="logForm.returnLog" class="code-mirror" :options="cmOptions" />
            </el-form-item>
          </el-form>
        </el-row>
        <template v-slot:footer>
          <dialog-footer
            @cancel="logVisible = false"
            @confirm="logVisible = false"/>
        </template>
      </el-drawer>
      <!--Result log-->

      <!--History output list-->
      <el-drawer class="rtl" :title="$t('commons.history')" :visible.sync="visibleList" size="85%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div>
          <el-table border :data="outputListData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName">
            <!-- 展开 start -->
            <el-table-column type="expand" min-width="1%">
              <template slot-scope="props">
                <el-form>
                  <codemirror ref="cmEditor" v-model="props.row.returnLog" class="code-mirror" :options="cmOptions" />
                </el-form>
              </template>
            </el-table-column >
            <!-- 展开 end -->
            <el-table-column type="index" min-width="1%"/>
            <el-table-column prop="serverName" :label="$t('server.server_name')" min-width="10%" show-overflow-tooltip></el-table-column>
            <el-table-column prop="ip" :label="'IP'" min-width="10%" show-overflow-tooltip></el-table-column>
            <el-table-column prop="ruleName" :label="$t('server.rule_name')" min-width="15%" show-overflow-tooltip></el-table-column>
            <el-table-column prop="isSeverity" :label="$t('server.is_severity')" min-width="10%" show-overflow-tooltip v-slot:default="scope">
              <span v-if="scope.row.isSeverity" style="color: #46ad59">{{ $t('resource.risk_free') }}</span>
              <span v-if="!scope.row.isSeverity" style="color: #f84846">{{ $t('resource.risky') }}</span>
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('server.result_status')" min-width="14%" prop="resultStatus" sortable show-overflow-tooltip>
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
            <el-table-column prop="updateTime" min-width="14%" :label="$t('commons.create_time')" sortable>
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

  </main-container>
</template>

<script>

import {_filter, _sort} from "@/common/js/utils";
import MainContainer from "@/business/components/common/components/MainContainer";
import TablePagination from "@/business/components/common/pagination/TablePagination";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import TableOperators from "@/business/components/common/components/TableOperators";
import TableHeader from "@/business/components/common/components/TableHeader";
import HideTable from "@/business/components/common/hideTable/HideTable";
import RuleType from "./RuleType";
import CodeDiff from 'vue-code-diff';
import {getServerResultUrl, serverLogUrl} from "@/api/k8s/server/server";
import {serverHistoryUrl, serverDeleteHistoryResultUrl} from "@/api/system/history";
import {SERVER_RESULT_CONFIGS} from "@/business/components/common/components/search/search-components";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'server.server_name',
    props: 'serverName',
    disabled: false
  },
  {
    label: 'IP',
    props: 'ip',
    disabled: false
  },
  {
    label: 'server.rule_name',
    props: 'ruleName',
    disabled: false
  },
  {
    label: 'server.severity',
    props: 'severity',
    disabled: false
  },
  {
    label: 'commons.type',
    props: 'type',
    disabled: false
  },
  {
    label: 'server.result_status',
    props: 'resultStatus',
    disabled: false
  },
  {
    label: 'server.is_severity',
    props: 'isSeverity',
    disabled: false
  },
  {
    label: 'server.last_modified',
    props: 'updateTime',
    disabled: false
  },
];

/* eslint-disable */
  export default {
    name: "HistoryList",
    components: {
      TablePagination,
      DialogFooter,
      TableOperators,
      RuleType,
      CodeDiff,
      TableHeader,
      HideTable,
      MainContainer,
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
        result: {},
        tags: [],
        tableData: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        condition: {
          components: SERVER_RESULT_CONFIGS
        },
        direction: 'rtl',
        visibleList: false,
        oldStr: 'old code',
        newStr: 'new code',
        innerDrawer: false,
        script: '',
        buttons: [
          {
            tip: this.$t('resource.delete_result'), icon: "el-icon-delete", type: "danger",
            exec: this.handleDelete
          }
        ],
        diffButtons: [
          {
            tip: this.$t('commons.diff'), icon: "el-icon-sort", type: "primary",
            exec: this.codeDiffOpen
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
        },
        selectIds: new Set(),
        checkedColumnNames: columnOptions.map((ele) => ele.props),
        columnNames: columnOptions,
        //名称搜索
        items: [
          {
            name: 'server.server_name',
            id: 'serverName'
          },
          {
            name: 'IP',
            id: 'ip',
          },
          {
            name: 'server.rule_name',
            id: 'ruleName',
          },
        ],
        checkAll: true,
        isIndeterminate: false,
      }
    },
    methods: {
      handleCheckedColumnNamesChange(value) {
        const checkedCount = value.length;
        this.checkAll = checkedCount === this.columnNames.length;
        this.isIndeterminate = checkedCount > 0 && checkedCount < this.columnNames.length;
        this.checkedColumnNames = value;
      },
      handleCheckAllChange(val) {
        this.checkedColumnNames = val ? this.columnNames.map((ele) => ele.props) : [];
        this.isIndeterminate = false;
        this.checkAll = val;
      },
      init() {
        this.search();
      },
      select(selection) {
        this.selectIds.clear();
        selection.forEach(s => {
          this.selectIds.add(s.id)
        });
      },
      //查询列表
      async search() {
        let url = serverHistoryUrl + this.currentPage + "/" + this.pageSize;
        if (!!this.selectNodeIds) {
          this.condition.serverId = this.selectNodeIds[0];
        } else {
          this.condition.serverId = null;
        }
        this.result = await this.$post(url, this.condition, response => {
          console.log(response)
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
      handleDelete(obj) {
        this.$alert(this.$t('code.delete_confirm') + this.$t('code.result') + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$get(serverDeleteHistoryResultUrl + obj.id,  res => {
                setTimeout(function () {window.location.reload()}, 2000);
                this.$success(this.$t('commons.delete_success'));
              });
            }
          }
        });
      },
      async outputListDataSearch() {
        let item = this.outputListSearchData;
        await this.$post(serverHistoryUrl + this.outputListPage + "/" + this.outputListPageSize, {serverId: item.serverId}, response => {
          let data = response.data;
          this.outputListTotal = data.itemCount;
          this.outputListData = data.listObject;
        });
      },
      codeDiffOpen(item) {
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
        this.newStr = item.returnLog?item.returnLog:"[]";
        this.innerDrawer = true;
      },
      showResultLog (result) {
        this.logForm = result;
        this.$get(serverLogUrl + result.id, response => {
          this.logData = response.data;
          this.logVisible = true;
        });
        this.$get(getServerResultUrl + result.id, response => {
          this.logForm = response.data;
        });
      },
    },
    computed: {
      codemirror() {
        return this.$refs.cmEditor.codemirror;
      }
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
}

.icon-title {
  color: #fff;
  width: 30px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border-radius: 30px;
  font-size: 14px;
  margin: -7px 0 0 15px;
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
