<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <package-result-header :condition.sync="condition"
                              @search="search"
                              :title="$t('package.result_list')"/>
      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                @filter-change="filter">
        <el-table-column type="index" min-width="3%"/>
        <el-table-column prop="name" :label="$t('package.name')" min-width="10%" show-overflow-tooltip></el-table-column>
        <el-table-column prop="packageName" :label="$t('package.package_name')" min-width="10%" show-overflow-tooltip></el-table-column>
        <el-table-column prop="size" :label="$t('package.size')" min-width="10%" show-overflow-tooltip></el-table-column>
        <el-table-column prop="ruleName" :label="$t('package.rule_name')" min-width="10%" show-overflow-tooltip></el-table-column>
        <el-table-column min-width="6%" :label="$t('package.severity')" column-key="severity">
          <template v-slot:default="{row}">
            <rule-type :row="row"/>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="6%">
          <el-tooltip effect="dark" :content="$t('history.resource_result')" placement="top">
            <el-link type="primary" class="text-click" @click="goResource(scope.row)">{{ scope.row.returnSum }}</el-link>
          </el-tooltip>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('package.result_status')" min-width="14%" prop="resultStatus" sortable show-overflow-tooltip>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
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
        <el-table-column prop="updateTime" min-width="15%" :label="$t('package.last_modified')" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="15%" :label="$t('commons.operating')" fixed="right">
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
                <span class="grid-content-log-span"> {{ logForm.ruleName }} | {{ logForm.name }}</span>
                <span class="grid-content-log-span">
                  <img :src="require(`@/assets/img/platform/package.png`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ logForm.packageName }}
                </span>
                <span class="grid-content-status-span" v-if="logForm.resultStatus === 'APPROVED'" style="color: #579df8">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
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
        <div style="margin: 10px;">
          <h2>Summary:&nbsp;</h2>
          <ul style="margin-left: 60px;">
            <li><i>Scan Name</i>: {{ logForm.name }}</li>
            <li><i>Package Name</i>: {{ logForm.packageName }}</li>
            <li><i>Package Size</i>:&nbsp;{{ logForm.size }}</li>
            <li><i>Rule Name</i>: {{ logForm.ruleDesc }}</li>
            <li><i>Scan User</i>:&nbsp;{{ logForm.userName }}</li>
            <li><i>Severity</i>:&nbsp;{{ logForm.severity }}</li>
            <li><i>Create Time</i>:&nbsp;{{ logForm.createTime | timestampFormatDate }}</li>
            <li><i>Result Status</i>:&nbsp;{{ logForm.resultStatus }}</li>
            <li><i>Vulnerabilities Found</i>: {{ logForm.returnSum }}</li>
          </ul>
        </div>
        <el-tabs type="border-card">
          <el-tab-pane :label="$t('package.result_list_vuln')">
            <h3>Vuln:&nbsp;</h3>
            <el-table :data="logForm.packageDependencyJsonList" border stripe style="width: 100%">
              <el-table-column type="index" min-width="5%"/>
              <el-table-column :label="'FileName'" min-width="20%" prop="fileName">
              </el-table-column>
              <el-table-column :label="'FilePath'" min-width="25%" prop="filePath">
              </el-table-column>
              <el-table-column min-width="5%" :label="'IsVirtual'" prop="isVirtual">
              </el-table-column>
              <el-table-column min-width="10%" :label="'Md5'" prop="md5">
              </el-table-column>
              <el-table-column min-width="15%" :label="'Sha1'" prop="sha1">
              </el-table-column>
              <el-table-column min-width="15%" :label="'Sha256'" prop="sha256">
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane :label="$t('package.result_list_sbom')" class="el-card">
            <div style="margin: 10px 0 0 0;" v-if="JSON.stringify(logForm.packageDependencyJsonList) !== '[]'">
              <div v-for="(packageDependencyJson, index) in logForm.packageDependencyJsonList" :key="index">
                <h3>Vuln:&nbsp;{{ index+1 }}</h3>
                <ul style="margin-left: 60px;">
                  <li><i>FileName</i>: {{ packageDependencyJson.fileName }}</li>
                  <li><i>FilePath</i>: {{ packageDependencyJson.filePath }}</li>
                  <li><i>IsVirtual</i>:&nbsp;{{ packageDependencyJson.isVirtual }}</li>
                  <li><i>Md5</i>: {{ packageDependencyJson.md5 }}</li>
                  <li><i>Sha1</i>:&nbsp;{{ packageDependencyJson.sha1 }}</li>
                  <li><i>Sha256</i>:&nbsp;{{ packageDependencyJson.sha256 }}</li>
                </ul>
                <div style="margin: 10px;">
                  <vue-okr-tree
                    ref="tree"
                    :data="packageDependencyJson.vulnerabilities?packageDependencyJson.vulnerabilities:'[]' | packageDependencyJsonRight"
                    :left-data="packageDependencyJson.vulnerabilities?packageDependencyJson.vulnerabilities:'[]' | packageDependencyJsonLeft"
                    only-both-tree
                    direction="horizontal"
                    show-collapsable
                    node-key="id"
                    label-class-name='no-padding'
                    default-expand-all
                    :render-content="renderContent"
                    :filter-node-method="filterNode"
                  ></vue-okr-tree>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane :label="$t('package.result_list_depen')">
            <div style="margin: 10px 0 0 0;" v-if="JSON.stringify(logForm.packageDependencyJsonList) !== '[]'">
              <div style="margin: 10px 0 0 0;" :key="index" v-for="(packageDependencyJson,index) in logForm.packageDependencyJsonList">
                <el-card class="box-card">
                  <div slot="header" class="clearfix">
                    <el-row>
                      <el-col :span="24" style="margin: -7px 0 0 15px;">
                        <span style="font-size: 24px;font-weight: 500;">{{ packageDependencyJson.fileName }}</span>
                        <span style="font-size: 20px;color: #888;margin-left: 5px;">- IsVirtual:  {{ packageDependencyJson.isVirtual }}</span>
                      </el-col>
                    </el-row>
                    <el-row style="font-size: 18px;padding: 10px;">
                      <el-col :span="20">
                        <span style="color: #888;margin: 5px;">{{ 'SBOM' }}</span>
                        <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                        <span style="color: #444;margin: 5px;">FilePath: {{ packageDependencyJson.filePath }}</span>
                      </el-col>
                    </el-row>
                  </div>
                  <div class="text div-json">
                    <el-descriptions :column="2" v-for="(packagex, index) in JSON.parse(packageDependencyJson.packages)" :key="index" :title="'Package'+(index+1)">
                      <el-descriptions-item v-for="(meta, index) in filterJson(packagex?packagex:{package: 'N/A'})" :key="index" :label="meta.key">
                                  <span v-if="!meta.flag" show-overflow-tooltip>
                                    <el-tooltip class="item" effect="dark" :content="JSON.stringify(meta.value)" placement="top-start">
                                      <el-link type="primary" style="color: #0000e4;">{{ 'Details' }}</el-link>
                                    </el-tooltip>
                                  </span>
                        <el-tooltip v-if="meta.flag && meta.value" class="item" effect="light" :content="typeof(meta.value) === 'boolean'?meta.value.toString():meta.value" placement="top-start">
                                    <span class="table-expand-span-value">
                                        {{ meta.value }}
                                    </span>
                        </el-tooltip>
                        <span v-if="meta.flag && !meta.value"> N/A</span>
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                  <div class="text div-json">
                    <el-descriptions :column="2" v-for="(vulnerability, index) in JSON.parse(packageDependencyJson.vulnerabilities)" :key="index" :title="'Vulnerabilitie'+(index+1)">
                      <el-descriptions-item v-for="(meta, index) in filterJson(vulnerability?vulnerability:{vulnerability: 'N/A'})" :key="index" :label="meta.key">
                                  <span v-if="!meta.flag" show-overflow-tooltip>
                                    <el-tooltip class="item" effect="dark" :content="JSON.stringify(meta.value)" placement="top-start">
                                      <el-link type="primary" style="color: #0000e4;">{{ 'Details' }}</el-link>
                                    </el-tooltip>
                                  </span>
                        <el-tooltip v-if="meta.flag && meta.value" class="item" effect="light" :content="typeof(meta.value) === 'boolean'?meta.value.toString():meta.value" placement="top-start">
                                    <span class="table-expand-span-value">
                                      {{ meta.value }}
                                    </span>
                        </el-tooltip>
                        <span v-if="meta.flag && !meta.value"> N/A</span>
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                </el-card>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
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
import PackageResultHeader from "../head/PackageResultHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../head/DialogFooter";
import {_filter, _sort} from "@/common/js/utils";
import RuleType from "./RuleType";
import {PACKAGE_RESULT_CONFIGS} from "../../common/components/search/search-components";
import {VueOkrTree} from 'vue-okr-tree';

/* eslint-disable */
export default {
  components: {
    TableOperators,
    MainContainer,
    Container,
    PackageResultHeader,
    TablePagination,
    TableOperator,
    DialogFooter,
    RuleType,
    VueOkrTree,
  },
  data() {
    return {
      result: {},
      condition: {
        components: PACKAGE_RESULT_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      severityOptions: [],
      direction: 'rtl',
      logVisible: false,
      detailVisible: false,
      logForm: {},
      logData: [],
      detailForm: {},
      buttons: [
        {
          tip: this.$t('resource.scan_vuln_search'), icon: "el-icon-share", type: "primary",
          exec: this.handleVuln
        },
        {
          tip: this.$t('package.result_tip'), icon: "el-icon-postcard", type: "warning",
          exec: this.handleResult
        },
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
      filterJson: this.filterJsonKeyAndValue,
    }
  },

  methods: {
    handleVuln() {
      window.open('http://www.cnnvd.org.cn/web/vulnerability/queryLds.tag','_blank','');
    },
    //查询列表
    search() {
      let url = "/package/resultList/" + this.currentPage + "/" + this.pageSize;
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
          let url = "/package/getPackageResult/";
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
        if (row.status != 'ERROR' && row.status != 'FINISHED' && row.status != 'WARNING') {
          sum++;
        }
      }
      return sum == 0;
    },
    severityOptionsFnc () {
      this.severityOptions = [
        {key: '低风险', value: "LowRisk"},
        {key: '中风险', value: "MediumRisk"},
        {key: '高风险', value: "HighRisk"}
      ];
    },
    init() {
      this.severityOptionsFnc();
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
    showResultLog (result) {
      let url = "/package/log/";
      this.result = this.$get(url + result.id, response => {
        this.logData = response.data;
      });
      this.result = this.$get("/package/getPackageResultDto/"+ result.id, response => {
        this.logForm = response.data;
      });
      this.logVisible = true;
    },
    handleClose() {
      this.logVisible=false;
      this.detailVisible=false;
    },
    handleScans (item) {
      this.$alert(this.$t('resource.handle_scans'), '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$get("/package/reScan/" + item.id, response => {
              if (response.success) {
                this.search();
              }
            });
          }
        }
      });
    },
    handleDelete(obj) {
      this.$alert(this.$t('package.delete_confirm') + this.$t('package.result') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/package/deletePackageResult/" + obj.id,  res => {
              setTimeout(function () {window.location.reload()}, 2000);
              this.$success(this.$t('commons.delete_success'));
            });
          }
        }
      });
    },
    handleResult(item) {
      if(item.resultStatus === "APPROVED") {
        this.$warning(this.$t('package.result_still_run'));
        return;
      }
      window.open(this.location + item.returnHtml, '_blank');
    },
    goResource (params) {
      if (params.returnSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      let p = '/package/resultdetails/' + params.id;
      this.$router.push({
        path: p
      }).catch(error => error);
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    renderContent (h, node) {
      const cls = ['diy-wrapper']
      if (node.isCurrent) {
        cls.push('current-select')
      }
      if (node.isLeftChild) {
        cls.push('left-child')
      }
      let spanLeft = 8;
      let spanRight = 16;
      if (node.level === 1) {
        return (
          <div class={cls}>
            <div class="diy-con-name">
              Package Scan: {node.data.name}
            </div>
            <div class="diy-con-content">
              Description: {node.data.description}
            </div>
          </div>
        )
      } else {
        return (
          <div class={cls}>
            <div class="diy-con-name">
              <el-row>
                <el-col span={spanLeft} class="diy-con-left">Name</el-col>
                <el-col span={spanRight} class="diy-con-right-cve">{node.data.name}</el-col>
              </el-row>
            </div>
            <div class="diy-con-content">
              <el-row>
                <el-col span={spanLeft} class="diy-con-left">Severity</el-col>
                <el-col span={spanRight} class="diy-con-right">{node.data.severity}</el-col>
              </el-row>
            </div>
            <div class="diy-con-name">
              <el-row>
                <el-col span={spanLeft} class="diy-con-left">Source</el-col>
                <el-col span={spanRight} class="diy-con-right">{node.data.source}</el-col>
              </el-row>
            </div>
            <div class="diy-con-content">
              <el-row>
                <el-col span={spanLeft} class="diy-con-left">Notes</el-col>
                <el-col span={spanRight} class="diy-con-right">{!!node.data.description?node.data.description.substr(0,60):'N/A'}</el-col>
              </el-row>
            </div>
          </div>
        )
      }
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
  computed: {
    codemirror() {
      return this.$refs.cmEditor.codemirror;
    }
  },
  activated() {
    this.init();
    this.location = window.location.href.split("#")[0];
    this.timer = setInterval(this.getStatus,10000);
  },
  created() {
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
.code-mirror {
  width: 100%;
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

.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
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
* { touch-action: pan-y; }
/deep/ :focus{outline:0;}
</style>
