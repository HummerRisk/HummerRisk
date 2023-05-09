<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('code.code_settings_list')"
                      @create="create" :createTip="$t('code.code_create')"
                      :show-create="true" :show-validate="false"
                      :items="items" :columnNames="columnNames" @delete="deleteBatch" :show-delete="true"
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
        <el-table-column type="selection" min-width="40">
        </el-table-column>
        <el-table-column type="index" min-width="40"/>
        <el-table-column prop="name" :label="$t('code.name')" v-if="checkedColumnNames.includes('name')"  min-width="220" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/code/${scope.row.pluginIcon}`)" style="width: 25px; height: 25px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" v-if="checkedColumnNames.includes('returnSum')" prop="returnSum" sortable show-overflow-tooltip min-width="220">
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
        <el-table-column v-slot:default="scope" :label="$t('code.result_status')" v-if="checkedColumnNames.includes('resultStatus')" min-width="140" prop="resultStatus" sortable show-overflow-tooltip>
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
        <el-table-column min-width="160" v-if="checkedColumnNames.includes('createTime')" :label="$t('commons.create_time')" sortable
                         prop="createTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
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

    <!--Create code-->
    <el-drawer class="rtl" :title="$t('code.code_create')" :visible.sync="createVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="viewResult.loading">
      <div v-for="(form, index) in addAccountForm" :key="index">
        <el-form :model="form" label-position="right" label-width="150px" size="medium" :rules="rule" :ref="'addAccountForm' + index">
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
          <el-form-item :label="$t('code.name')" ref="name" prop="name">
            <el-input v-model="form.name" autocomplete="off" :placeholder="$t('code.name')"/>
          </el-form-item>
          <el-form-item :label="$t('code.platform')" :rules="{required: true, message: $t('code.platform') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="form.pluginIcon" :placeholder="$t('code.platform')" @change="changePluginForAdd(form)">
              <el-option
                v-for="item in plugins"
                :key="item.id"
                :label="item.name"
                :value="item.id">
                <img :src="require(`@/assets/img/code/${item.id}`)" style="width: 24px; height: 24px; vertical-align:middle" alt=""/>
                &nbsp;&nbsp; {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item>
          <div v-for="(tmp, index) in form.tmpList" :key="index">
            <el-form-item v-if="tmp.inputType === 'password'" :label="tmp.label" style="margin-bottom: 29px">
              <el-input :type="tmp.inputType" v-model="tmp.input" autocomplete="new-password" show-password :placeholder="tmp.description"/>
            </el-form-item>
            <el-form-item v-if="tmp.inputType !== 'password' && tmp.inputType !== 'boolean'" :label="tmp.label">
              <el-input :type="tmp.inputType" v-model="tmp.input" @input="change($event)" autocomplete="off" :placeholder="tmp.description"/>
            </el-form-item>
          </div>
          <el-form-item v-if="form.isProxy && form.pluginId" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
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
          <el-form-item v-if="form.pluginId" :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-switch v-model="form.isProxy"></el-switch>
          </el-form-item>
          <el-form-item v-if="index > 0" :label="$t('code.delete_this_code')">
            <el-button type="danger" icon="el-icon-delete" plain size="small" @click="deleteAccount(addAccountForm, form)">{{ $t('commons.delete') }}</el-button>
          </el-form-item>
        </el-form>
        <el-divider><i class="el-icon-first-aid-kit"> {{ (index + 1) }}</i></el-divider>
        <div style="margin: 10px 10px 10px 50px;">
          <div style="color: red;">* {{ $t('commons.note') }}</div>
          <div style="color: red;">1. {{ $t('code.code_note') }}</div>
          <div style="color: red;">2. {{ $t('code.code_note2') }}</div>
          <div style="color: red;">3. {{ $t('code.code_note3') }}</div>
          <div style="color: red;">4. {{ $t('code.code_note4') }}</div>
          <div style="color: red;">5. {{ $t('code.code_note5') }}</div>
        </div>
      </div>
      <proxy-dialog-create-footer
        @cancel="createVisible = false"
        @addAccount="addAccount(addAccountForm)"
        @confirm="saveAccount(addAccountForm, 'add')"/>
    </el-drawer>
    <!--Create code-->

    <!--Update code-->
    <el-drawer class="rtl" :title="$t('code.code_update')" :visible.sync="updateVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="viewResult.loading">
      <el-form :model="form" label-position="right" label-width="150px" size="small" :rules="rule" ref="accountForm">
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
        <el-form-item :label="$t('code.name')"  ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('code.name')"/>
        </el-form-item>
        <el-form-item :label="$t('code.platform')" :rules="{required: true, message: $t('code.platform') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" disabled v-model="form.pluginIcon" :placeholder="$t('code.platform')">
            <el-option
              v-for="item in plugins"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              <img :src="require(`@/assets/img/code/${item.id}`)" style="width: 25px; height: 16px; vertical-align:middle" alt=""/>
              &nbsp;&nbsp; {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <div v-for="(tmp, index) in tmpList" :key="index">
          <el-form-item v-if="tmp.inputType === 'password'" :label="tmp.label" style="margin-bottom: 29px">
            <el-input :type="tmp.inputType" v-model="tmp.input" @input="change($event)" autocomplete="new-password" show-password :placeholder="tmp.description"/>
          </el-form-item>
          <el-form-item v-if="tmp.inputType !== 'password' && tmp.inputType !== 'boolean'" :label="tmp.label">
            <el-input :type="tmp.inputType" v-model="tmp.input" @input="change($event)" autocomplete="off" :placeholder="tmp.description"/>
          </el-form-item>
        </div>
      </el-form>
      <div style="margin: 10px;">
        <div style="margin: 10px 10px 10px 50px;">
          <div style="color: red;">* {{ $t('commons.note') }}</div>
          <div style="color: red;">1. {{ $t('code.code_note') }}</div>
          <div style="color: red;">2. {{ $t('code.code_note2') }}</div>
          <div style="color: red;">3. {{ $t('code.code_note3') }}</div>
          <div style="color: red;">4. {{ $t('code.code_note4') }}</div>
          <div style="color: red;">5. {{ $t('code.code_note5') }}</div>
        </div>
      </div>
      <proxy-dialog-footer
        @cancel="updateVisible = false"
        @confirm="editAccount(form, 'update')"/>
    </el-drawer>
    <!--Update code-->

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
                  <img :src="require(`@/assets/img/code/${logForm.pluginIcon}`)" style="width: 25px; height: 25px; vertical-align:middle" alt=""/>
                  {{ 'C:' + logForm.critical + ' H:' +  logForm.high + ' M:' + logForm.medium + ' L:' + logForm.low + ' U:' + logForm.unknown}}
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
      </el-row>
      <log-form :logForm="logForm"/>
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
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperator from "@/business/components/common/components/TableOperator";
import Container from "@/business/components/common/components/Container";
import MainContainer from "@/business/components/common/components/MainContainer";
import CodeStatus from "./CodeStatus";
import TableOperators from "@/business/components/common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {CODE_CONFIGS} from "@/business/components/common/components/search/search-components";
import ProxyDialogFooter from "@/business/components/common/components/ProxyDialogFooter";
import ProxyDialogCreateFooter from "@/business/components/common/components/ProxyDialogCreateFooter";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import HideTable from "@/business/components/common/hideTable/HideTable";
import LogForm from "@/business/components/code/home/LogForm";
import {
  addCodeUrl,
  codeDownloadUrl,
  codeListUrl,
  codePluginUrl,
  deleteCodesUrl,
  deleteCodeUrl,
  getCodeResultUrl,
  logCodeUrl,
  scanCodeUrl,
  updateCodeUrl
} from "@/api/k8s/code/code";
import {proxyListAllUrl} from "@/api/system/system";
import {allSbomListUrl, allSbomVersionListUrl} from "@/api/k8s/sbom/sbom";
import {saveAs} from "@/common/js/FileSaver";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'code.name',
    props: 'name',
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
    label: 'commons.create_time',
    props: 'createTime',
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
    CodeStatus,
    MainContainer,
    Container,
    TableHeader,
    TablePagination,
    TableOperator,
    DialogFooter,
    ProxyDialogFooter,
    ProxyDialogCreateFooter,
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
      viewResult: {},
      condition: {
        components: CODE_CONFIGS
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
      plugins: [
        {id: 'github.png', name: "Github"},
        {id: 'gitlab.png', name: "GitLab"},
      ],
      proxys: [],
      tmpList: [],
      item: {},
      form: {},
      addAccountForm: [ { "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] } ],
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
      sboms: [],
      versions: [],
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'code.name',
          id: 'name'
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
    noWarnLog(item) {
      this.$warning(item.name + this.$t('resource.i18n_no_warn'));
    },
    handleDownload(item) {
      if (!item.resultId) {
        this.$warning(this.$t('resource.i18n_no_warn'));
        return;
      }
      this.$post(codeDownloadUrl, {
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
    async create() {
      if(this.sboms && this.sboms.length > 0) {
        await this.$post(allSbomVersionListUrl, {sbomId: this.sboms[0].id},response => {
          this.versions = response.data;
          this.addAccountForm = [ { "sbomId": this.sboms[0].id, "sbomVersionId" : this.versions[0].id, "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] } ];
        });
      }
      this.createVisible = true;
      this.activeProxy();
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
    select(selection) {
      this.selectIds.clear();
      selection.forEach(s => {
        this.selectIds.add(s.id)
      });
    },
    //查询列表
    search() {
      let url = codeListUrl + this.currentPage + "/" + this.pageSize;
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
      this.changePlugin('edit');
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
            this.result = this.$get(deleteCodeUrl + obj.id, () => {
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
      this.selectIds.clear();
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
    filterStatus(value, row) {
      return row.status === value;
    },
    //新增Git项目账号信息/选择插件查询Git项目账号信息
    async changePluginForAdd (form){
      this.viewResult = await this.$get(codePluginUrl, response => {
        let fromJson = typeof(response.data) === 'string'?JSON.parse(response.data):response.data;
        form.tmpList = fromJson.data;
        for (let tmp of form.tmpList) {
          if (tmp.defaultValue !== undefined) {
            tmp.input = tmp.defaultValue;
          }
        }
      });
    },
    //编辑Git项目账号信息/选择插件查询Git项目账号信息
    async changePlugin (type){
      this.viewResult = await this.$get(codePluginUrl, response => {
        let fromJson = typeof(response.data) === 'string'?JSON.parse(response.data):response.data;
        this.tmpList = fromJson.data;
        if (type === 'edit') {
          let credentials = typeof(this.item.credential) === 'string'?JSON.parse(this.item.credential):this.item.credential;
          for (let tmp of this.tmpList) {
            if (credentials[tmp.name] === undefined) {
              tmp.input = tmp.defaultValue?tmp.defaultValue:"";
            } else {
              tmp.input = credentials[tmp.name];
            }
          }
        } else {
          for (let tmp of this.tmpList) {
            if (tmp.defaultValue !== undefined) {
              tmp.input = tmp.defaultValue;
            }
          }
        }
      });
    },
    //保存Git项目账号信息
    saveAccount(addAccountForm, type){
      for (let item of addAccountForm) {
        if (!item.tmpList.length) {
          this.$warning(this.$t('commons.no_plugin_param'));
          return;
        }
        let data = {}, key = {};
        for (let tmp of item.tmpList) {
          if(tmp.input) {
            key[tmp.name] = tmp.input.trim();
          }
        }
        data["credential"] = JSON.stringify(key);
        data["name"] = item.name;
        data["pluginIcon"] = item.pluginIcon;
        data["sbomId"] = item.sbomId;
        data["sbomVersionId"] = item.sbomVersionId;
        if (item.isProxy) data["proxyId"] = item.proxyId;
        if (type === 'add') {
          this.viewResult = this.$post(addCodeUrl, data,response => {
            if (response.success) {
              this.$success(this.$t('commons.create_success'));
              this.search();
              this.handleClose();
            } else {
              this.$error(response.message);
            }
          });
        }
      }
    },
    //编辑Git项目账号信息
    editAccount(item, type){
      if (!this.tmpList.length) {
        this.$error(this.$t('account.i18n_account_cloud_plugin_param'));
        return;
      }
      this.$refs['accountForm'].validate(valid => {
        if (valid) {
          let data = {}, key = {};
          for (let tmp of this.tmpList) {
            key[tmp.name] = tmp.input;
          }
          data["credential"] = JSON.stringify(key);
          data["name"] = item.name;
          data["pluginId"] = item.pluginId;
          data["sbomId"] = item.sbomId;
          data["sbomVersionId"] = item.sbomVersionId;
          if (item.isProxy) data["proxyId"] = item.proxyId;

          if (type === 'add') {
            this.viewResult = this.$post(addCodeUrl, data,response => {
              if (response.success) {
                this.$success(this.$t('commons.create_success'));
                this.search();
                this.handleClose();
              } else {
                this.$error(response.message);
              }
            });
          } else {
            data["id"] = item.id;
            this.viewResult = this.$post(updateCodeUrl, data,response => {
              if (response.success) {
                this.$success(this.$t('commons.update_success'));
                this.handleClose();
                this.search();
              } else {
                this.$error(response.message);
              }
            });
          }
        } else {
          this.$error(this.$t('rule.full_param'));
          return false;
        }
      });
    },
    async addAccount (addAccountForm) {
      if(this.sboms && this.sboms.length > 0) {
        this.viewResult = await this.$post(allSbomVersionListUrl, {sbomId: this.sboms[0].id},response => {
          this.versions = response.data;
          let newParam = { "sbomId": this.sboms[0].id, "sbomVersionId" : this.versions[0].id, "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] };
          addAccountForm.push(newParam);
        });
      }
    },
    deleteAccount (parameter, p) {
      for (let i in parameter) {
        if (parameter[i].name === p.name) {
          parameter.splice(i, 1);
          return;
        }
      }
    },
    handleScan(item) {
      this.$alert(this.$t('image.one_scan') + item.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(scanCodeUrl + item.id,response => {
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
    getStatus () {
      if (this.checkStatus(this.tableData)) {
        this.search();
        clearInterval(this.timer);
      } else {
        for (let data of this.tableData) {
          this.$get(getCodeResultUrl + data.resultId, response => {
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
    showResultLog (result) {
      if (!result.resultId) {
        this.$warning(this.$t('resource.i18n_no_warn'));
        return;
      }
      this.viewResult = this.$get(logCodeUrl + result.resultId, response => {
        this.logData = response.data;
      });
      this.viewResult = this.$get(getCodeResultUrl + result.resultId, response => {
        this.logForm = response.data;
        this.logForm.returnJson = JSON.parse(this.logForm.returnJson);
      });
      this.logVisible = true;
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
      if (!params.resultId) {
        this.$warning(this.$t('resource.i18n_no_warn'));
        return;
      }
      if (params.returnSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      let p = '/code/resultdetails/' + params.resultId;
      this.$router.push({
        path: p
      }).catch(error => error);
    },
    deleteBatch() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('commons.please_select') + this.$t('code.code_setting'));
        return;
      }
      this.$alert(this.$t('oss.delete_batch') + this.$t('code.code_setting') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$request({
              method: 'POST',
              url: deleteCodesUrl,
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
  width: 34%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0 6px 2px;
  color: #606266;
  padding: 0 1%;
}

.grid-content-status-span {
  width: 20%;float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
  padding: 0 1%;
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
* { touch-action: pan-y; }
/deep/ :focus{outline:0;}
</style>

