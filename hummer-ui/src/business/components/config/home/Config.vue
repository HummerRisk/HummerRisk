<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('config.config_settings_list')"
                      @create="create" :createTip="$t('config.config_create')"
                      :show-create="true" @delete="deleteBatch" :show-delete="true"
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
        <el-table-column type="selection" id="selection" prop="selection" min-width="50">
        </el-table-column>
        <el-table-column type="index" min-width="40"/>
        <el-table-column :label="$t('config.name')" v-if="checkedColumnNames.includes('name')" min-width="130" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/config/yaml.png`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" min-width="90" v-if="checkedColumnNames.includes('status')" :label="$t('config.status')"
                         column-key="status"
                         :filters="statusFilters"
                         :filter-method="filterStatus">
          <template v-slot:default="{row}">
            <config-status :row="row"/>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" v-if="checkedColumnNames.includes('returnSum')" prop="returnSum" sortable show-overflow-tooltip min-width="200">
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
        <el-table-column v-slot:default="scope" :label="$t('image.result_status')" v-if="checkedColumnNames.includes('resultStatus')" min-width="140" prop="resultStatus" sortable show-overflow-tooltip>
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
          <el-button @click="noWarnLog(scope.row)" plain size="mini" type="info" v-else-if="scope.row.resultStatus === null">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_no_warn') }}
          </el-button>
        </el-table-column>
        <el-table-column prop="scanTime" min-width="200" v-if="checkedColumnNames.includes('scanTime')" :label="$t('commons.last_scan_time')" sortable>
          <template v-slot:default="scope">
            <span v-if="scope.row.resultStatus !== null"><i class="el-icon-time"/> {{ scope.row.scanTime | timestampFormatDate }}</span>
            <span v-if="scope.row.resultStatus === null">--</span>
          </template>
        </el-table-column>
        <el-table-column min-width="160" v-if="checkedColumnNames.includes('createTime')" :label="$t('account.create_time')" sortable
                         prop="createTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="160" v-if="checkedColumnNames.includes('updateTime')" :label="$t('account.update_time')" sortable
                         prop="updateTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" v-if="checkedColumnNames.includes('userName')" :label="$t('account.creator')" min-width="90" show-overflow-tooltip/>
        <el-table-column min-width="190" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create k8s config-->
    <el-drawer class="rtl" :title="$t('config.config_create')" :visible.sync="createVisible" size="75%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="viewResult.loading">
      <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form" :rules="rule">
        <el-form-item :label="$t('config.name')" ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('config.name')"/>
        </el-form-item>
        <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-switch v-model="isProxy"></el-switch>
        </el-form-item>
        <el-form-item v-if="isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
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
        <el-form-item :label="$t('config.config_type')" ref="type" prop="type">
          <el-radio v-model="configType" label="menu" @change="changeYaml">{{ $t('config.menu_config') }}</el-radio>
          <el-radio v-model="configType" label="k8s" @change="changeYaml">{{ $t('config.k8s_config') }}</el-radio>
          <el-radio v-model="configType" label="upload" @change="changeYaml">{{ $t('config.upload_config') }}</el-radio>
        </el-form-item>
        <el-form-item v-if="configType==='k8s'" :label="$t('k8s.k8s_setting')">
          <el-select style="width: 100%;" filterable v-model="sourceId" :placeholder="$t('k8s.k8s_setting')" @change="changeSearch">
            <el-option
              v-for="item in k8s"
              :key="item.id"
              :label="item.sourceName"
              :value="item.id">
              &nbsp;&nbsp; {{ '[K8s]' + item.cloudNativeName + '|{namespace}' +  item.sourceNamespace + '(type)' + item.sourceType + ':(source)' + item.sourceName }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="configType==='upload'" :label="$t('config.upload_yaml')">
          <yaml-upload v-on:appendYaml="appendYaml"/>
        </el-form-item>
        <el-form-item :label="$t('config.config_yaml')" ref="configYaml" prop="configYaml">
          <codemirror ref="cmEditor" v-model="form.configYaml" class="code-mirror" :options="cmOptions" />
        </el-form-item>
        <el-form-item>
          <span style="color: red">{{ $t('config.config_note') }}</span>
        </el-form-item>
      </el-form>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="createVisible = false"
          @confirm="save('form')"/>
      </div>
    </el-drawer>
    <!--Create k8s config-->

    <!--Update k8s config-->
    <el-drawer class="rtl" :title="$t('config.config_update')" :visible.sync="updateVisible" size="75%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="viewResult.loading">
      <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form" :rules="rule">
        <el-form-item :label="$t('config.name')" ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('config.name')"/>
        </el-form-item>
        <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-switch v-model="isProxy"></el-switch>
        </el-form-item>
        <el-form-item v-if="isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" filterable v-model="form.proxyId" :placeholder="$t('commons.proxy')">
            <el-option
              v-for="item in proxys"
              :key="item.id"
              :label="item.proxyIp"
              :value="item.id">
              &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('config.config_type')" ref="type" prop="type">
          <el-radio v-model="configType" label="menu" @change="changeYaml">{{ $t('config.menu_config') }}</el-radio>
          <el-radio v-model="configType" label="k8s" @change="changeYaml">{{ $t('config.k8s_config') }}</el-radio>
          <el-radio v-model="configType" label="upload" @change="changeYaml">{{ $t('config.upload_config') }}</el-radio>
        </el-form-item>
        <el-form-item v-if="configType==='k8s'" :label="$t('k8s.k8s_setting')">
          <el-select style="width: 100%;" filterable v-model="sourceId" :placeholder="$t('k8s.k8s_setting')" @change="changeSearch">
            <el-option
              v-for="item in k8s"
              :key="item.id"
              :label="item.sourceName"
              :value="item.id">
              &nbsp;&nbsp; {{ '[K8s]' + item.cloudNativeName + '|{namespace}' +  item.sourceNamespace + '(type)' + item.sourceType + ':(source)' + item.sourceName }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="configType==='upload'" :label="$t('config.upload_yaml')">
          <yaml-upload v-on:appendYaml="appendYaml"/>
        </el-form-item>
        <el-form-item :label="$t('config.config_yaml')" ref="configYaml" prop="configYaml">
          <codemirror ref="cmEditor" v-model="form.configYaml" class="code-mirror" :options="cmOptions" />
        </el-form-item>
        <el-form-item>
          <span style="color: red">{{ $t('config.config_note') }}</span>
        </el-form-item>
      </el-form>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="updateVisible = false"
          @confirm="update('form')"/>
      </div>
    </el-drawer>
    <!--Update k8s config-->

    <!--Result log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="85%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="viewResult.loading">
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
                  <img :src="require(`@/assets/img/config/yaml.png`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
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
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperator from "../../common/components/TableOperator";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import ConfigStatus from "./ConfigStatus";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {CONFIG_CONFIGS} from "../../common/components/search/search-components";
import ProxyDialogFooter from "@/business/components/common/components/ProxyDialogFooter";
import ProxyDialogCreateFooter from "@/business/components/common/components/ProxyDialogCreateFooter";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import YamlUpload from "@/business/components/config/home/YamlUpload";
import HideTable from "@/business/components/common/hideTable/HideTable";
import LogForm from "@/business/components/config/home/LogForm";
import {proxyListAllUrl} from "@/api/system/system";
import {allCloudNativeSource2YamlListUrl} from "@/api/k8s/k8s/k8s";
import {
  addConfigUrl,
  configDownloadUrl,
  configListUrl,
  deleteConfigsUrl,
  deleteConfigUrl,
  cloudNativeSource2YamlUrl,
  getCloudNativeConfigResultUrl,
  logConfigUrl,
  scanConfigUrl,
  updateConfigUrl
} from "@/api/k8s/config/config";
import {saveAs} from "@/common/js/FileSaver";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'config.name',
    props: 'name',
    disabled: false
  },
  {
    label: 'config.status',
    props: 'status',
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
    label: 'account.create_time',
    props: 'createTime',
    disabled: false
  },
  {
    label: 'account.update_time',
    props: 'updateTime',
    disabled: false
  },
  {
    label: 'account.creator',
    props: 'userName',
    disabled: false
  }
];

/* eslint-disable */
export default {
  name: "Event",
  components: {
    TableOperators,
    ConfigStatus,
    MainContainer,
    Container,
    TableHeader,
    TablePagination,
    TableOperator,
    DialogFooter,
    ProxyDialogFooter,
    ProxyDialogCreateFooter,
    YamlUpload,
    HideTable,
    LogForm,
  },
  data() {
    return {
      credential: {},
      result: {},
      viewResult: {},
      condition: {
        components: CONFIG_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      selectIds: new Set(),
      createVisible: false,
      updateVisible: false,
      innerDrawer: false,
      innerDrawerProxy: false,
      plugins: [],
      proxys: [],
      k8s: [],
      tmpList: [],
      item: {},
      form: {},
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
        configYaml: [
          {required: true, message: this.$t('config.config_yaml'), trigger: 'blur'},
          {
            required: true,
            message: this.$t('config.config_yaml') + this.$t('commons.cannot_be_empty'),
            trigger: 'blur'
          }
        ],
        proxyIp: [
          {required: true, message: this.$t('proxy.proxy_ip'), trigger: 'blur'},
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
        ],
        proxyPort: [
          {required: true, message: this.$t('proxy.proxy_port'), trigger: 'blur'},
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
        ],
        proxyName: [
          {required: false, message: this.$t('proxy.proxy_name'), trigger: 'blur'},
          {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
        ],
        proxyPassword: [
          {required: false, message: this.$t('proxy.proxy_password'), trigger: 'blur'},
          {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
        ],
      },
      buttons: [
        {
          tip: this.$t('k8s.execute_scan'), icon: "el-icon-s-promotion", type: "success",
          exec: this.handleScan
        }, {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        },  {
          tip: this.$t('resource.download_report'), icon: "el-icon-bottom", type: "warning",
          exec: this.handleDownload
        }, {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      statusFilters: [
        {text: this.$t('account.INVALID'), value: 'INVALID'},
        {text: this.$t('account.VALID'), value: 'VALID'},
        {text: this.$t('account.DELETE'), value: 'DELETE'}
      ],
      proxyType: [
        {id: 'Http', value: "Http"},
        {id: 'Https', value: "Https"},
      ],
      cmOptions: {
        tabSize: 4,
        mode: 'text/x-yaml',
        theme: 'bespin',
        lineNumbers: true,
        line: true,
        indentWithTabs: true,
      },
      configType: 'menu',
      isProxy: false,
      sourceId: '',
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'config.name',
          id: 'name'
        },
        {
          name: 'account.creator',
          id: 'userName'
        }
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
    create() {
      this.form = {};
      this.createVisible = true;
    },
    //查询代理
    activeProxy() {
      this.result = this.$get(proxyListAllUrl, response => {
        this.proxys = response.data;
      });
    },
    //查询k8s配置
    activeK8s() {
      this.result = this.$get(allCloudNativeSource2YamlListUrl, response => {
        this.k8s = response.data;
      });
    },
    select(selection) {
      this.selectIds.clear();
      selection.forEach(s => {
        this.selectIds.add(s.id)
      });
    },
    //查询列表
    search() {
      let url = configListUrl + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleClose() {
      this.createVisible =  false;
      this.updateVisible =  false;
      this.logVisible=false;
      this.detailVisible=false;
    },
    init() {
      this.selectIds.clear();
      this.activeProxy();
      this.activeK8s();
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
    filterStatus(value, row) {
      return row.status === value;
    },
    changeSearch(value){
      this.$get(cloudNativeSource2YamlUrl + value, res => {
        this.form.configYaml = res.data.sourceYaml;
      });
    },
    appendYaml(yaml) {
      this.form.configYaml = yaml;
    },
    save(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          this.viewResult = this.$post(addConfigUrl, this.form, () => {
            this.$success(this.$t('commons.save_success'));
            this.createVisible = false;
            this.search();
          });
        }
      });
    },
    update(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          this.viewResult = this.$post(updateConfigUrl, this.form, () => {
            this.$success(this.$t('commons.update_success'));
            this.updateVisible = false;
            this.search();
          });
        }
      });
    },
    handleDelete(obj) {
      this.$alert(this.$t('workspace.delete_confirm') + obj.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(deleteConfigUrl + obj.id, response => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    handleEdit(row) {
      this.form = row;
      this.updateVisible = true;
    },
    handleScan(item) {
      if (item.status !== 'VALID') {
        this.$warning(this.$t('commons.failed_status'));
        return;
      }
      this.$alert(this.$t('image.one_scan') + item.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(scanConfigUrl + item.id,response => {
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
    changeYaml() {
      this.form.configYaml = "";
    },
    goResource (params) {
      if (!params.resultId) {
        this.$warning(this.$t('resource.i18n_no_warn'));
        return;
      }
      if (params.returnSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      let p = '/config/result-details/' + params.resultId;
      this.$router.push({
        path: p
      }).catch(error => error);
    },
    handleDownload(item) {
      if (!item.resultId) {
        this.$warning(this.$t('resource.i18n_no_warn'));
        return;
      }
      this.result = this.$post(configDownloadUrl, {
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
    getStatus () {
      if (this.checkStatus(this.tableData)) {
        this.search();
        clearInterval(this.timer);
      } else {
        for (let data of this.tableData) {
          this.$get(getCloudNativeConfigResultUrl + data.resultId, response => {
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
    noWarnLog(item) {
      this.$warning(item.name + this.$t('resource.i18n_no_warn'));
    },
    showResultLog (result) {
      this.viewResult = this.$get(logConfigUrl + result.resultId, response => {
        this.logData = response.data;
      });
      this.viewResult = this.$get(getCloudNativeConfigResultUrl + result.resultId, response => {
        this.logForm = response.data;
        this.logForm.resultJson = JSON.parse(this.logForm.resultJson);
      });
      this.logVisible = true;
    },
    deleteBatch() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('commons.please_select') + this.$t('config.config_settings'));
        return;
      }
      this.$alert(this.$t('oss.delete_batch') + this.$t('config.config_settings') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$request({
              method: 'POST',
              url: deleteConfigsUrl,
              data: Array.from(this.selectIds),
              headers: {
                'Content-Type': undefined
              }
            }, res => {
              this.$success(this.$t('commons.success'));
              this.search();
            });
          }
        }
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
    this.timer = setInterval(this.getStatus, 10000);
  },
  beforeDestroy() {
    clearInterval(this.timer);
  }
}
</script>

<style scoped>
.rtl >>> .el-drawer__body {
  overflow-y: auto;
  padding: 20px;
}
.rtl >>> input {
  width: 100%;
}
.rtl >>> .el-select {
  width: 100%;
}
.rtl >>> .el-form-item__content {
  width: 80%;
}
/deep/ :focus{outline:0;}
.el-row-card {
  padding: 0 10px 0 10px;
  margin: 1%;
}
.split {
  height: 80px;
  border-left: 1px solid #D8DBE1;
}
.cl-ver-col {
  vertical-align: middle;
}
.cl-mid-row {
  margin: 0 0 1% 0;
}
.cl-btn-mid-row {
  margin: 1%;
}
.cl-span-col {
  margin: 1% 0;
}
.cl-btn-col {
  margin: 4% 0;
}
.cl-data-col {
  color: #888;
  font-size: 10px;
}
.cl-btn-data-col {
  color: #77aff9;
}

.input-inline-i{
  display:inline;
}

.input-inline-i >>> .el-input__inner {
  width: 68%;
}

.input-inline-t{
  display:inline;
}

.input-inline-t >>> .el-input__inner {
  width: 30%;
}

.co-el-img >>> .el-image {
  display: table-cell;
  left: 40%;
}
.cp-el-i {
  margin: 1%;
}
.co-el-i{
  width: 70px;
  height: 70px;
}
.code-mirror {
  height: 600px !important;
}
.code-mirror >>> .CodeMirror {
  /* Set height, width, borders, and global font properties here */
  height: 600px !important;
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
.txt-click {
  cursor:pointer;
}
.txt-click:hover {
  color: aliceblue;
  text-shadow: 1px 1px 1px #000;
}
.table-card >>> .search {
  width: 450px !important;
}
.table-card >>> .search .el-input {
  width: 140px !important;
}
* { touch-action: pan-y; }
/deep/ :focus{outline:0;}
</style>

