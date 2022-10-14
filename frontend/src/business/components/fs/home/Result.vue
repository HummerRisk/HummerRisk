<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition"
                               @search="search"
                               :title="$t('fs.result_list')"/>
      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                @filter-change="filter">
        <el-table-column type="index" min-width="2%"/>
        <el-table-column prop="name" :label="$t('image.image_name')" min-width="10%" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 30px; height: 25px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('image.image_url')" min-width="20%" show-overflow-tooltip>
          <el-row v-if="scope.row.type==='repo'">{{ scope.row.imageUrl }}:{{ scope.row.imageTag }}</el-row>
          <el-row v-if="scope.row.type==='image'">{{ scope.row.imageUrl }}:{{ scope.row.imageTag }}</el-row>
          <el-row v-if="scope.row.type==='tar'">{{ scope.row.path }}</el-row>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="15%">
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
        <el-table-column prop="updateTime" min-width="14%" :label="$t('image.last_modified')" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="14%" :label="$t('commons.operating')" fixed="right">
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
                  <img :src="require(`@/assets/img/platform/docker.png`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ logForm.imageName }}
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
        <div style="margin: 10px;" v-if="logForm.resultJson">
          <h2>Summary:&nbsp;</h2>
          <ul style="margin-left: 60px;">
            <li><i>Scan Name</i>: {{ logForm.name }}</li>
            <li><i>Scan User</i>:&nbsp;{{ logForm.userName }}</li>
            <li><i>ArtifactType</i>:&nbsp;{{ logForm.resultJson.ArtifactType }}</li>
            <li><i>ArtifactName</i>:&nbsp;{{ logForm.resultJson.ArtifactName }}</li>
            <li><i>SchemaVersion</i>:&nbsp;{{ logForm.resultJson.SchemaVersion }}</li>
            <li><i>Architecture</i>:&nbsp;{{ logForm.resultJson.Metadata.ImageConfig.architecture }}</li>
            <li><i>Create Time</i>:&nbsp;{{ logForm.createTime | timestampFormatDate }}</li>
            <li><i>Result Status</i>:&nbsp;{{ logForm.resultStatus }}</li>
            <li><i>Vulnerabilities Found</i>: {{ logForm.returnSum }}</li>
          </ul>
        </div>
        <div style="margin: 10px;" v-if="logForm.resultJson">
          <div style="margin: 10px 0 0 0;">
            <h2>Details:&nbsp;</h2>
            <div style="margin: 10px 0 0 0;">
              <div style="margin: 10px 0 0 0;" :key="index" v-for="(result, index) in logForm.resultJson.Results">
                <div style="margin: 10px;" v-if="result">
                  <h3>Summary:&nbsp;</h3>
                  <ul style="margin-left: 60px;">
                    <li><i>Target</i>: {{ result.Target }}</li>
                    <li><i>Class</i>:&nbsp;{{ result.Class }}</li>
                    <li><i>Type</i>:&nbsp;{{ result.Type }}</li>
                  </ul>
                </div>
                <div style="margin: 10px 0 0 0;" :key="index" v-for="(vulnerabilitiy, index) in result.Vulnerabilities">
                  <el-card class="box-card">
                    <div slot="header" class="clearfix">
                      <el-row>
                        <el-col class="icon-title" :span="3">
                          <span>{{ vulnerabilitiy.Severity.substring(0, 1) }}</span>
                        </el-col>
                        <el-col :span="15" style="margin: -7px 0 0 15px;">
                          <span style="font-size: 24px;font-weight: 500;">{{ vulnerabilitiy.Title }}</span>
                        </el-col>
                        <el-col :span="6" style="float: right;">
                          <span style="font-size: 20px;color: #999;float: right;">{{ 'SEVERITY SOURCE' }}</span>
                        </el-col>
                      </el-row>
                      <el-row style="font-size: 18px;padding: 10px;">
                        <el-col :span="20">
                          <span style="margin: 5px;"><a :href="vulnerabilitiy.PrimaryURL">{{ vulnerabilitiy.VulnerabilityID }}</a></span>
                          <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                          <span style="margin: 5px;"><el-button type="danger" size="mini">{{ vulnerabilitiy.Severity }}</el-button></span>
                          <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                          <span style="color: #888;margin: 5px;">INSTALLED VERSION: {{ vulnerabilitiy.InstalledVersion }}</span>
                          <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                          <span style="color: #444;margin: 5px;">PkgName: {{ vulnerabilitiy.PkgName }}</span>
                        </el-col>
                        <el-col :span="4" style="float: right;">
                          <span style="font-size: 20px;color: #000;float: right;">{{ vulnerabilitiy.SeveritySource }}</span>
                        </el-col>
                      </el-row>
                    </div>
                    <div class="text item div-desc">
                      <el-row>
                        <span style="color: red;"><i class="el-icon-s-opportunity"></i>PrimaryURL:</span> &nbsp;{{ vulnerabilitiy.PrimaryURL }}
                      </el-row>
                      <el-row>
                        <span style="color: red;">Description:</span> {{ vulnerabilitiy.Description }}
                      </el-row>
                      <el-row>
                        <span style="color: red;">PublishedDate:</span> {{ vulnerabilitiy.PublishedDate }}
                      </el-row>
                      <el-row>
                        <span style="color: red;">LastModifiedDate:</span> {{ vulnerabilitiy.LastModifiedDate }}
                      </el-row>
                      <el-row>
                        <span style="color: red;">FixedVersion:</span> {{ vulnerabilitiy.FixedVersion }}
                      </el-row>
                      <el-row>
                        <span style="color: red;">DataSource:</span> {{ vulnerabilitiy.DataSource.ID }} | {{ vulnerabilitiy.DataSource.Name }} | {{ vulnerabilitiy.DataSource.URL }}
                      </el-row>
                    </div>
                    <div class="text div-json">
                      <el-descriptions title="Layer" :column="2">
                        <el-descriptions-item v-for="(vuln, index) in filterJson(vulnerabilitiy.Layer)" :key="index" :label="vuln.key">
                          <span v-if="!vuln.flag" show-overflow-tooltip>
                            <el-tooltip class="item" effect="dark" :content="JSON.stringify(vuln.value)" placement="top-start">
                              <el-link type="primary" style="color: #0000e4;">{{ 'Details' }}</el-link>
                            </el-tooltip>
                          </span>
                          <el-tooltip v-if="vuln.flag && vuln.value" class="item" effect="light" :content="typeof(vuln.value) === 'boolean'?vuln.value.toString():vuln.value" placement="top-start">
                            <span class="table-expand-span-value">
                                {{ vuln.value }}
                            </span>
                          </el-tooltip>
                          <span v-if="vuln.flag && !vuln.value"> N/A</span>
                        </el-descriptions-item>
                      </el-descriptions>
                    </div>
                    <div class="text div-json">
                      <el-descriptions title="CweIDs" :column="2">
                        <el-descriptions-item v-for="(CweID, index) in vulnerabilitiy.CweIDs" :key="index" :label="index">
                          <span> {{ CweID }}</span>
                        </el-descriptions-item>
                      </el-descriptions>
                    </div>
                    <div class="text div-json">
                      <el-descriptions title="References" :column="2">
                        <el-descriptions-item v-for="(Reference, index) in vulnerabilitiy.References" :key="index" :label="index">
                          <span> {{ Reference }}</span>
                        </el-descriptions-item>
                      </el-descriptions>
                    </div>
                  </el-card>
                </div>
              </div>
            </div>
          </div>
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
import TableHeader from "@/business/components/common/components/TableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {_filter, _sort} from "@/common/js/utils";
import RuleType from "./RuleType";
import {IMAGE_RESULT_CONFIGS} from "../../common/components/search/search-components";
import {saveAs} from "@/common/js/FileSaver";

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
    RuleType
  },
  data() {
    return {
      result: {},
      condition: {
        components: IMAGE_RESULT_CONFIGS
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
      activeNames: ['1','2','3','4','5','6','7','8','9'],
      filterJson: this.filterJsonKeyAndValue,
    }
  },

  methods: {
    handleVuln() {
      window.open('http://www.cnnvd.org.cn/web/vulnerability/querylist.tag','_blank','');
    },
    //查询列表
    search() {
      let url = "/image/resultList/" + this.currentPage + "/" + this.pageSize;
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
          let url = "/image/getImageResult/";
          this.$get(url + data.id, response => {
            let result = response.data;
            if (data.resultStatus !== result.resultStatus) {
              data.resultStatus = result.resultStatus;
              data.returnSum = result.returnSum;
              data.critical = result.critical?result.critical:0;
              data.high = result.high?result.high:0;
              data.medium = result.medium?result.medium:0;
              data.low = result.low?result.low:0;
              data.unknown = result.unknown?result.unknown:0;
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
      let logUrl = "/image/log/";
      this.result = this.$get(logUrl + result.id, response => {
        this.logData = response.data;
      });
      let resultUrl = "/image/getImageResultWithBLOBs/";
      this.result = this.$get(resultUrl + result.id, response => {
        this.logForm = response.data;
        this.logForm.resultJson = JSON.parse(this.logForm.resultJson);
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
            this.$get("/image/reScan/" + item.id, response => {
              if (response.success) {
                this.search();
              }
            });
          }
        }
      });
    },
    handleDelete(obj) {
      this.$alert(this.$t('image.delete_confirm') + this.$t('image.result') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/image/deleteImageResult/" + obj.id,  res => {
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
      let p = '/image/resultdetails/' + params.id;
      this.$router.push({
        path: p
      }).catch(error => error);
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
    handleDownload(item) {
      this.$post("/image/download", {
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
  computed: {
    codemirror() {
      return this.$refs.cmEditor.codemirror;
    }
  },
  activated() {
    this.init();
    this.location = window.location.href.split("#")[0];
    this.timer = setInterval(this.getStatus, 10000);
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
* { touch-action: pan-y; }
/deep/ :focus{outline:0;}
</style>
