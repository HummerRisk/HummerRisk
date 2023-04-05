<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('fs.fs_settings_list')"
                      @create="create" :createTip="$t('fs.fs_create')"
                      :show-create="true"
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
        <el-table-column prop="name" :label="$t('fs.name')" v-if="checkedColumnNames.includes('name')" min-width="180" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/fs/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="fileName" v-if="checkedColumnNames.includes('fileName')" :label="$t('fs.file_name')" min-width="180" show-overflow-tooltip/>
        <el-table-column prop="size" v-if="checkedColumnNames.includes('size')" :label="$t('fs.size')" min-width="100" show-overflow-tooltip/>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('returnSum')" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="200">
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
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('resultStatus')" :label="$t('image.result_status')" min-width="150" prop="resultStatus" sortable show-overflow-tooltip>
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
          <el-button @click="noWarnLog(scope.row)" plain size="medium" type="info" v-else-if="scope.row.resultStatus === null">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_no_warn') }}
          </el-button>
        </el-table-column>
        <el-table-column prop="scanTime" min-width="200" v-if="checkedColumnNames.includes('scanTime')" :label="$t('commons.last_scan_time')" sortable>
          <template v-slot:default="scope">
            <span v-if="scope.row.resultStatus !== null"><i class="el-icon-time"/> {{ scope.row.scanTime | timestampFormatDate }}</span>
            <span v-if="scope.row.resultStatus === null">--</span>
          </template>
        </el-table-column>
        <el-table-column min-width="160" v-if="checkedColumnNames.includes('updateTime')" :label="$t('commons.update_time')" sortable
                         prop="updateTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" :label="$t('account.creator')" v-if="checkedColumnNames.includes('userName')" min-width="100" show-overflow-tooltip/>
        <el-table-column min-width="230" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create fs-->
    <el-drawer class="rtl" :title="$t('fs.fs_create')" :visible.sync="createVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="addAccountForm" label-position="right" label-width="150px" size="medium" :rules="rule" ref="addAccountForm">
          <el-form-item :label="$t('sbom.sbom_project')" :rules="{required: true, message: $t('sbom.sbom_project') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="addAccountForm.sbomId" :placeholder="$t('sbom.sbom_project')" @change="changeSbom(addAccountForm)">
              <el-option
                v-for="item in sboms"
                :key="item.id"
                :label="item.name"
                :value="item.id">
                <i class="iconfont icon-SBOM sbom-icon"></i>
                {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('sbom.sbom_project_version')" :rules="{required: true, message: $t('sbom.sbom_project_version') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="addAccountForm.sbomVersionId" :placeholder="$t('sbom.sbom_project_version')">
              <el-option
                v-for="item in versions"
                :key="item.id"
                :label="item.name"
                :value="item.id">
                <i class="iconfont icon-lianmenglian sbom-icon-2"></i>
                {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('fs.name')" ref="name" prop="name">
            <el-input v-model="addAccountForm.name" autocomplete="off" :placeholder="$t('fs.name')"/>
          </el-form-item>
          <el-form-item :label="$t('fs.file')" ref="type" prop="type">
            <tar-upload v-on:appendTar="appendTar" v-model="addAccountForm.path" :param="addAccountForm.path"/>
          </el-form-item>
          <el-form-item :label="$t('proxy.is_proxy')">
            <el-switch v-model="addAccountForm.isProxy"></el-switch>
          </el-form-item>
          <el-form-item v-if="addAccountForm.isProxy && addAccountForm.pluginId" :label="$t('commons.proxy')">
            <el-select style="width: 100%;" v-model="addAccountForm.proxyId" :placeholder="$t('commons.proxy')">
              <el-option
                v-for="item in proxys"
                :key="item.id"
                :label="item.proxyIp"
                :value="item.id">
                &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      <dialog-footer
        @cancel="createVisible = false"
        @confirm="saveAccount('addAccountForm')"/>
    </el-drawer>
    <!--Create fs-->

    <!--Update fs-->
    <el-drawer class="rtl" :title="$t('code.code_update')" :visible.sync="updateVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="small" :rules="rule" ref="form">
        <el-form-item :label="$t('sbom.sbom_project')" :rules="{required: true, message: $t('sbom.sbom_project') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" v-model="form.sbomId" :placeholder="$t('sbom.sbom_project')" @change="changeSbom(form)">
            <el-option
              v-for="item in sboms"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              <i class="iconfont icon-SBOM sbom-icon"></i>
              {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('sbom.sbom_project_version')" :rules="{required: true, message: $t('sbom.sbom_project_version') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" v-model="form.sbomVersionId" :placeholder="$t('sbom.sbom_project_version')">
            <el-option
              v-for="item in versions"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              <i class="iconfont icon-lianmenglian sbom-icon-2"></i>
              {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('fs.name')"  ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('fs.name')"/>
        </el-form-item>
        <el-form-item :label="$t('fs.file')" ref="type" prop="type">
          <tar-upload v-on:appendTar="appendTar" v-model="form.path" :param="form.path"/>
        </el-form-item>
        <el-form-item :label="$t('proxy.is_proxy')">
          <el-switch v-model="form.isProxy"></el-switch>
        </el-form-item>
        <el-form-item v-if="form.isProxy && form.pluginId" :label="$t('commons.proxy')">
          <el-select style="width: 100%;" v-model="form.proxyId" :placeholder="$t('commons.proxy')">
            <el-option
              v-for="item in proxys"
              :key="item.id"
              :label="item.proxyIp"
              :value="item.id">
              &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <dialog-footer
        @cancel="updateVisible = false"
        @confirm="editAccount('form')"/>
    </el-drawer>
    <!--Update fs-->

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
                <span class="grid-content-log-span">
                  {{ logForm.name }}
                  <i class="el-icon-document-copy" @click="copy(logForm)" style="display: none;"></i>
                </span>
                <span class="grid-content-log-span">
                  <img :src="require(`@/assets/img/fs/fs.png`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ logForm.fileName }} | {{ logForm.size }}
                </span>
                <span class="grid-content-status-span-approved" v-if="logForm.resultStatus === 'APPROVED'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </span>
                <span class="grid-content-status-span-finished" v-else-if="logForm.resultStatus === 'FINISHED'">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </span>
                <span class="grid-content-status-span-error" v-else-if="logForm.resultStatus === 'ERROR'">
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
                <span v-bind:class="{true: 'color-red', false: ''}[scope.row.result == false]">
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
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperator from "../../common/components/TableOperator";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {FS_CONFIGS} from "../../common/components/search/search-components";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import TarUpload from "../head/TarUpload";
import HideTable from "@/business/components/common/hideTable/HideTable";
import LogForm from "@/business/components/fs/home/LogForm";
import {allSbomListUrl, allSbomVersionListUrl} from "@/api/k8s/sbom/sbom";
import {proxyListAllUrl} from "@/api/system/system";
import {
  addFsUrl,
  deleteFsUrl,
  fsDownloadUrl,
  fsListUrl,
  getFsResultUrl,
  logFsUrl,
  scanFsUrl,
  updateFsUrl
} from "@/api/k8s/fs/fs";
import {saveAs} from "@/common/js/FileSaver";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'fs.name',
    props: 'name',
    disabled: false
  },
  {
    label: 'fs.file_name',
    props: 'fileName',
    disabled: false
  },
  {
    label: 'fs.size',
    props: 'size',
    disabled: false
  },
  {
    label: 'resource.i18n_not_compliance',
    props: 'returnSum',
    disabled: false
  },
  {
    label: 'code.result_status',
    props: 'resultStatus',
    disabled: false
  },
  {
    label: 'commons.last_scan_time',
    props: 'scanTime',
    disabled: false
  },
  {
    label: 'commons.update_time',
    props: 'updateTime',
    disabled: false
  },
  {
    label: 'account.creator',
    props: 'userName',
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
    TarUpload,
    HideTable,
    LogForm,
  },
  provide() {
    return {
      search: this.search,
    }
  },
  data() {
    return {
      credential: {},
      result: {},
      condition: {
        components: FS_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      createVisible: false,
      updateVisible: false,
      proxys: [],
      item: {},
      form: {},
      addAccountForm: {},
      proxyForm: {},
      script: '',
      direction: 'rtl',
      rule: {
        name: [
          {required: true, message: this.$t('commons.input_name'), trigger: 'blur'},
          {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
          {
            required: true,
            message: this.$t("workspace.special_characters_are_not_supported"),
            trigger: 'blur'
          }
        ],
      },
      buttons: [
        {
          tip: this.$t('k8s.execute_scan'), icon: "el-icon-s-promotion", type: "success",
          exec: this.handleScan
        }, {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        }, {
          tip: this.$t('resource.scan_vuln_search'), icon: "el-icon-share", type: "info",
          exec: this.handleVuln
        }, {
          tip: this.$t('resource.download_report'), icon: "el-icon-bottom", type: "warning",
          exec: this.handleDownload
        }, {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      proxyType: [
        {id: 'Http', value: "Http"},
        {id: 'Https', value: "Https"},
      ],
      sboms: [],
      versions: [],
      tarFile: Object,
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'fs.name',
          id: 'name'
        },
        {
          name: 'fs.file_name',
          id: 'fileName'
        },
        {
          name: 'account.creator',
          id: 'userName'
        },
      ],
      checkAll: true,
      isIndeterminate: false,
      logVisible: false,
      detailVisible: false,
      logForm: {},
      logData: [],
      detailForm: {},
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
    handleVuln() {
      window.open('http://www.cnnvd.org.cn/','_blank','');
    },
    select(selection) {
    },
    create() {
      this.addAccountForm = {};
      if(this.sboms && this.sboms.length > 0) {
        this.addAccountForm.sbomId = this.sboms[0].id;
        this.initSbom({sbomId: this.addAccountForm.sbomId});
      }
      this.createVisible = true;
      this.activeProxy();
    },
    async initSbom(params) {
      await this.$post(allSbomVersionListUrl, params,response => {
        this.versions = response.data;
        if(this.versions && this.versions.length > 0) this.addAccountForm.sbomVersionId = this.versions[0].id;
      });
    },
    initSboms() {
      this.result = this.$post(allSbomListUrl, {},response => {
        this.sboms = response.data;
      });
    },
    changeSbom(item) {
      let params = {
        sbomId: item.sbomId
      };
      this.result = this.$post(allSbomVersionListUrl, params,response => {
        this.versions = response.data;
      });
    },
    //查询代理
    activeProxy() {
      this.result = this.$get(proxyListAllUrl, response => {
        this.proxys = response.data;
      });
    },
    appendTar(file) {
      this.tarFile = file;
    },
    //查询列表
    search() {
      let url = fsListUrl + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleEdit(tmp) {
      this.form = tmp;
      this.item.credential = tmp.credential;
      if (!this.form.proxyId) {
        this.form.proxyId = "";
      }
      this.changeSbom({sbomId: tmp.sbomId});
      this.updateVisible = true;
      this.activeProxy();
    },
    handleClose() {
      this.createVisible =  false;
      this.updateVisible =  false;
      this.logVisible=false;
      this.detailVisible=false;
    },
    handleDelete(obj) {
      this.$alert(this.$t('commons.delete_confirm') + obj.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(deleteFsUrl + obj.id, () => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    change(e) {
      this.$forceUpdate();
    },
    init() {
      this.search();
      this.initSboms();
    },
    sort(column) {
      _sort(column, this.condition);
      this.search();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.search();
    },
    //保存fs
    saveAccount(addAccountForm){
      this.$refs[addAccountForm].validate(valid => {
        if (valid) {
          let formData = new FormData();
          if (this.tarFile) {
            formData.append("tarFile", this.tarFile);
          }
          formData.append("request", new Blob([JSON.stringify(this.addAccountForm)], {type: "application/json"}));
          let axiosRequestConfig = {
            method: "POST",
            url: addFsUrl,
            data: formData,
            headers: {
              "Content-Type": 'multipart/form-data'
            }
          };
          this.result = this.$request(axiosRequestConfig, response => {
            if (response.success) {
              this.$success(this.$t('commons.create_success'));
              this.search();
              this.handleClose();
            } else {
              this.$error(response.message);
            }
          });
        } else {
          this.$error(this.$t('rule.full_param'));
          return false;
        }
      });
    },
    //编辑fs
    editAccount(form){
      this.$refs[form].validate(valid => {
        if (valid) {
          let formData = new FormData();
          if (this.tarFile) {
            formData.append("tarFile", this.tarFile);
          }
          formData.append("request", new Blob([JSON.stringify(this.form)], {type: "application/json"}));
          let axiosRequestConfig = {
            method: "POST",
            url: updateFsUrl,
            data: formData,
            headers: {
              "Content-Type": 'multipart/form-data'
            }
          };
          this.result = this.$request(axiosRequestConfig, (response) => {
            if (response.success) {
              this.$success(this.$t('commons.update_success'));
              this.search();
              this.handleClose();
            } else {
              this.$error(response.message);
            }
          });
        } else {
          this.$error(this.$t('rule.full_param'));
          return false;
        }
      });
    },
    handleScan(item) {
      this.$alert(this.$t('image.one_scan') + item.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$get(scanFsUrl + item.id,response => {
              if (response.success) {
                this.$success(this.$t('schedule.event_start'));
                this.search();
              } else {
                this.$error(response.message);
              }
            });
          }
        }
      });
    },
    handleDownload(item) {
      if (!item.resultId) {
        this.$warning(this.$t('resource.i18n_no_warn'));
        return;
      }
      this.$post(fsDownloadUrl, {
        id: item.resultId
      }, response => {
        if (response.success) {
          let blob = new Blob([response.data], { type: "application/json" });
          saveAs(blob, item.name + ".json");
        }
      }, error => {
        console.log("下载报错", error);
      });
    },
    copy(row) {
      let input = document.createElement("input");
      document.body.appendChild(input);
      input.value = row['command'];
      input.select();
      if (input.setSelectionRange) {
        input.setSelectionRange(0, input.value.length);
      }
      document.execCommand("copy");
      document.body.removeChild(input);
    },
    goResource (params) {
      if (params.returnSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      let p = '/fs/resultdetails/' + params.resultId;
      this.$router.push({
        path: p
      }).catch(error => error);
    },
    showResultLog (result) {
      this.result = this.$get(logFsUrl + result.resultId, response => {
        this.logData = response.data;
      });
      this.result = this.$get(getFsResultUrl + result.resultId, response => {
        this.logForm = response.data;
        this.logForm.returnJson = JSON.parse(this.logForm.returnJson);
      });
      this.logVisible = true;
    },
    noWarnLog(item) {
      this.$warning(item.name + this.$t('resource.i18n_no_warn'));
    },
    getStatus () {
      if (this.checkStatus(this.tableData)) {
        this.search();
        clearInterval(this.timer);
      } else {
        for (let data of this.tableData) {
          this.$get(getFsResultUrl + data.resultId, response => {
            let result = response.data;
            if (result && data.resultStatus !== result.resultStatus) {
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
        if (row.resultStatus && row.resultStatus != 'ERROR' && row.resultStatus != 'FINISHED' && row.resultStatus != 'WARNING') {
          sum++;
        }
      }
      return sum == 0;
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
/deep/ :focus{outline:0;}
.el-box-card {
  margin: 10px 0;
}
.sbom-icon{
  color: royalblue;
  font-size: 30px;
  vertical-align: middle;
}
.sbom-icon-2{
  color: red;
  font-size: 25px;
  vertical-align: middle;
}
.table-card >>> .search {
  width: 450px !important;
}
.table-card >>> .search .el-input {
  width: 140px !important;
}
.table-content {
  width: 100%;
}
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
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
.bg-purple-light {
  background: #f2f2f2;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.grid-content-log-span {
  width: 34%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0 6px 2px;
  color: #606266;
  padding: 0 1%;
}
.grid-content-status-span-approved{
  width: 20%;
  float: right;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
  padding: 0 1%;
  color: #579df8;
}
.grid-content-status-span-finished{
  width: 20%;
  float: right;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
  padding: 0 1%;
  color: #7ebf50
}
.grid-content-status-span-error{
  width: 20%;
  float: right;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
  padding: 0 1%;
  color: red;
}
.grid-content-status-span {
  width: 20%;float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
}
.txt-click {
  cursor:pointer;
}
.txt-click:hover {
  color: aliceblue;
  text-shadow: 1px 1px 1px #000;
}
* { touch-action: pan-y; }
/deep/ :focus{outline:0;}

.table-card >>> .search {
  width: 450px !important;
}
.table-card >>> .search .el-input {
  width: 140px !important;
}
</style>

