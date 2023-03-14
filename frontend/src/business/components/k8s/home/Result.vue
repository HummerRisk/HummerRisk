<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('k8s.result_list')"
                      :items="items" :columnNames="columnNames"
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
        <el-table-column type="index" min-width="40"/>
        <el-table-column prop="name" v-if="checkedColumnNames.includes('name')" :label="$t('k8s.name')" min-width="160" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" v-if="checkedColumnNames.includes('userName')" :label="$t('account.creator')" min-width="70" show-overflow-tooltip/>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('result')" :label="$t('k8s.vuln_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="200">
          <el-tooltip effect="dark" :content="$t('history.result') + ' CRITICAL:' + scope.row.critical + ' HIGH:' +  scope.row.high + ' MEDIUM:' + scope.row.medium + ' LOW:' + scope.row.low + ' UNKNOWN:' + scope.row.unknown" placement="top">
            <div class="txt-click" @click="goResource(scope.row)">
              <span style="background-color: #8B0000;color: white;padding: 3px;">{{ 'C:' + scope.row.critical }}</span>
              <span style="background-color: #FF4D4D;color: white;padding: 3px;">{{ 'H:' +  scope.row.high }}</span>
              <span style="background-color: #FF8000;color: white;padding: 3px;">{{ 'M:' + scope.row.medium }}</span>
              <span style="background-color: #eeab80;color: white;padding: 3px;">{{ 'L:' + scope.row.low }}</span>
              <span style="background-color: #d5d0d0;color: white;padding: 3px;">{{ 'U:' + scope.row.unknown }}</span>
            </div>
          </el-tooltip>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('configResult')" :label="$t('k8s.config_compliance')" prop="returnConfigSum" sortable show-overflow-tooltip min-width="200">
          <el-tooltip effect="dark" :content="$t('history.config_result') + ' CRITICAL:' + scope.row.configCritical + ' HIGH:' +  scope.row.configHigh + ' MEDIUM:' + scope.row.configMedium + ' LOW:' + scope.row.configLow" placement="top">
            <div class="txt-click" @click="goConfigResource(scope.row)">
              <span style="background-color: #8B0000;color: white;padding: 3px;">{{ 'C:' + scope.row.configCritical }}</span>
              <span style="background-color: #FF4D4D;color: white;padding: 3px;">{{ 'H:' +  scope.row.configHigh }}</span>
              <span style="background-color: #FF8000;color: white;padding: 3px;">{{ 'M:' + scope.row.configMedium }}</span>
              <span style="background-color: #d5d0d0;color: white;padding: 3px;">{{ 'L:' + scope.row.configLow }}</span>
            </div>
          </el-tooltip>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('kubenchResult')" :label="$t('k8s.kubench_compliance')" prop="kubenchResult" sortable show-overflow-tooltip min-width="200">
          <el-tooltip effect="dark" :content="$t('k8s.kubench_result') + ' FAIL:' + scope.row.fail + ' WARN:' +  scope.row.warn + ' INFO:' + scope.row.info + ' PASS:' + scope.row.pass" placement="top">
            <div class="txt-click" @click="goKubenchResource(scope.row)">
              <span style="background-color: #8B0000;color: white;padding: 3px;">{{ 'F:' + scope.row.fail }}</span>
              <span style="background-color: #FF4D4D;color: white;padding: 3px;">{{ 'W:' +  scope.row.warn }}</span>
              <span style="background-color: #FF8000;color: white;padding: 3px;">{{ 'I:' + scope.row.info }}</span>
              <span style="background-color: #d5d0d0;color: white;padding: 3px;">{{ 'P:' + scope.row.pass }}</span>
            </div>
          </el-tooltip>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('status')" :label="$t('image.result_status')" min-width="130" prop="resultStatus" sortable show-overflow-tooltip>
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
        <el-table-column prop="updateTime" v-if="checkedColumnNames.includes('updateTime')" min-width="160" :label="$t('image.last_modified')" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="130" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>
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
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import TableHeader from "@/business/components/common/components/TableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {_filter, _sort} from "@/common/js/utils";
import {K8S_RESULT_CONFIGS} from "../../common/components/search/search-components";
import LogForm from "@/business/components/k8s/home/LogForm";
import {saveAs} from "@/common/js/FileSaver";
import HideTable from "@/business/components/common/hideTable/HideTable";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'k8s.name',
    props: 'name',
    disabled: false
  },
  {
    label: 'account.creator',
    props: 'userName',
    disabled: false
  },
  {
    label: 'k8s.vuln_compliance',
    props: 'result',
    disabled: false
  },
  {
    label: 'k8s.config_compliance',
    props: 'configResult',
    disabled: false
  },
  {
    label: 'k8s.kubench_compliance',
    props: 'kubenchResult',
    disabled: false
  },
  {
    label: 'image.result_status',
    props: 'status',
    disabled: false
  },
  {
    label: 'image.last_modified',
    props: 'updateTime',
    disabled: false
  },
];

/* eslint-disable */
export default {
  components: {
    TableOperators,
    MainContainer,
    Container,
    TableHeader,
    TablePagination,
    TableOperator,
    DialogFooter,
    LogForm,
    HideTable,
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
          tip: this.$t('resource.download_report'), icon: "el-icon-bottom", type: "warning",
          exec: this.handleDownload
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
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'k8s.name',
          id: 'name'
        },
        {
          name: 'account.creator',
          id: 'userName'
        }
      ],
      checkAll: true,
      isIndeterminate: false,
    }
  },
  watch: {
    '$route': 'init'
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
    select(selection) {
    },
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
      } else {
        for (let data of this.tableData) {
          let url = "/k8s/getCloudNativeResult/";
          this.$get(url + data.id, response => {
            let result = response.data;
            if (data.resultStatus !== result.resultStatus) {
              data.resultStatus = result.resultStatus;
              data.returnSum = result.returnSum;
              data.critical = result.critical;
              data.high = result.high;
              data.medium = result.medium;
              data.low = result.low;
              data.unknown = result.unknown;
              data.fail = result.fail;
              data.warn = result.warn;
              data.info = result.info;
              data.pass = result.pass;
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
      let path = this.$route.path;
      if (path.indexOf("/k8s") >= 0) {
        let p = '/k8s/resultdetails/' + params.id;
        this.$router.push({
          path: p
        }).catch(error => error);
      } else if (path.indexOf("/resource") >= 0) {
        let p = '/resource/K8sResultdetails/' + params.id;
        this.$router.push({
          path: p
        }).catch(error => error);
      }
    },
    goConfigResource (params) {
      if (params.returnConfigSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      let path = this.$route.path;
      if (path.indexOf("/k8s") >= 0) {
        let p = '/k8s/resultconfigdetails/' + params.id;
        this.$router.push({
          path: p
        }).catch(error => error);
      } else if (path.indexOf("/resource") >= 0) {
        let p = '/resource/K8sResultConfigdetails/' + params.id;
        this.$router.push({
          path: p
        }).catch(error => error);
      }
    },
    goKubenchResource (params) {
      if (params.returnConfigSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      let path = this.$route.path;
      if (path.indexOf("/k8s") >= 0) {
        let p = '/k8s/resultkubenchdetails/' + params.id;
        this.$router.push({
          path: p
        }).catch(error => error);
      } else if (path.indexOf("/resource") >= 0) {
        let p = '/resource/K8sResultKubenchdetails/' + params.id;
        this.$router.push({
          path: p
        }).catch(error => error);
      }
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
      this.logVisible = false;
    },
    handleDownload(item) {
      this.$post("/k8s/download", {
        id: item.id
      }, response => {
        if (response.success) {
          let blob = new Blob([response.data], { type: "application/json" });
          saveAs(blob, item.name + ".json");
        }
      }, error => {
        console.log("下载报错", error);
      });
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
  width: 20%;
  float: left;
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

.grid-content-log-span {
  width: 39%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0 6px 2px;
  color: #606266;
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
.txt-click {
  cursor:pointer;
}
.txt-click:hover {
  color: aliceblue;
  text-shadow: 1px 1px 1px #000;
}
</style>

