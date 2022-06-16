<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('vuln.vuln_settings_list')"
                      @create="create" :createTip="$t('vuln.create')"
                      @scan="scan" :scanTip="$t('account.one_scan')"
                      @validate="validate" :runTip="$t('account.one_validate')"
                      :show-run="true" :show-scan="true" :show-create="true"/>

      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort"
                :row-class-name="tableRowClassName"
                @filter-change="filter" @select-all="select" @select="select">
        <el-table-column type="selection" min-width="5%">
        </el-table-column>
        <el-table-column type="index" min-width="5%"/>
        <el-table-column prop="name" :label="$t('vuln.name')" min-width="15%" show-overflow-tooltip></el-table-column>
        <el-table-column :label="$t('vuln.platform')" min-width="10%" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ $t(scope.row.pluginName) }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" min-width="10%" :label="$t('vuln.status')"
                         column-key="status"
                         :filters="statusFilters"
                         :filter-method="filterStatus">
          <template v-slot:default="{row}">
            <vuln-status :row="row"/>
          </template>
        </el-table-column>
        <el-table-column min-width="15%" :label="$t('account.create_time')" sortable
                         prop="createTime">
          <template v-slot:default="scope">
            <span><i class="el-icon-time"></i> {{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="15%" :label="$t('account.update_time')" sortable
                         prop="updateTime">
          <template v-slot:default="scope">
            <span><i class="el-icon-time"></i> {{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" :label="$t('account.creator')" min-width="8%" show-overflow-tooltip/>
        <el-table-column min-width="17%" :label="$t('commons.operating')">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create vuln-->
    <el-drawer class="rtl" :title="$t('vuln.create')" :visible.sync="createVisible" size="50%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <div v-for="(form, index) in addAccountForm" :key="index">
        <el-form :model="form" label-position="right" label-width="150px" size="medium" :rules="rule" :ref="'addAccountForm' + index">
          <el-form-item :label="$t('vuln.name')" ref="name" prop="name">
            <el-input v-model="form.name" autocomplete="off" :placeholder="$t('vuln.name')"/>
          </el-form-item>
          <el-form-item :label="$t('vuln.platform')" :rules="{required: true, message: $t('vuln.platform') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="form.pluginId" :placeholder="$t('vuln.platform')" @change="changePluginForAdd(form)">
              <el-option
                v-for="item in plugins"
                :key="item.id"
                :label="item.name"
                :value="item.id">
                <img :src="require(`@/assets/img/platform/${item.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                &nbsp;&nbsp; {{ $t(item.name) }}
              </el-option>
            </el-select>
          </el-form-item>
          <div v-for="(tmp, index) in form.tmpList" :key="index">
            <el-form-item v-if="tmp.inputType === 'password'" :label="tmp.label" style="margin-bottom: 29px">
              <el-input :type="tmp.inputType" v-model="tmp.input" autocomplete="new-password" show-password :placeholder="tmp.description"/>
            </el-form-item>
            <el-form-item v-if="tmp.inputType !== 'password' && tmp.inputType !== 'boolean'" :label="tmp.label">
              <el-input :type="tmp.inputType" v-model="tmp.input" autocomplete="off" :placeholder="tmp.description"/>
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
          <el-form-item v-if="index > 0" :label="$t('vuln.delete_this_vuln')">
            <el-button type="danger" icon="el-icon-delete" plain size="small" @click="deleteAccount(addAccountForm, form)">{{ $t('commons.delete') }}</el-button>
          </el-form-item>
        </el-form>
        <el-divider><i class="el-icon-first-aid-kit"> {{ (index + 1) }}</i></el-divider>
      </div>
      <proxy-dialog-create-footer
        @cancel="createVisible = false"
        @addAccount="addAccount(addAccountForm)"
        @confirm="saveAccount(addAccountForm, 'add')"/>
    </el-drawer>
    <!--Create vuln-->

    <!--Update vuln-->
    <el-drawer class="rtl" :title="$t('vuln.update')" :visible.sync="updateVisible" size="50%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="small" :rules="rule" ref="accountForm">
        <el-form-item :label="$t('vuln.name')"  ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('vuln.name')"/>
        </el-form-item>
        <el-form-item :label="$t('vuln.platform')" :rules="{required: true, message: $t('vuln.platform') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" disabled v-model="form.pluginId" :placeholder="$t('vuln.platform')" @change="changePlugin(form.pluginId)">
            <el-option
              v-for="item in plugins"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              <img :src="require(`@/assets/img/platform/${item.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
              &nbsp;&nbsp; {{ $t(item.name) }}
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
      <proxy-dialog-footer
        @cancel="updateVisible = false"
        @confirm="editAccount(form, 'update')"/>
    </el-drawer>
    <!--Update vuln-->

    <!-- 一键扫描选择规则组 -->
    <el-dialog :close-on-click-modal="false"
               :modal-append-to-body="false"
               :title="$t('account.scan_group_quick')"
               :visible.sync="scanVisible"
               class="" width="70%">
      <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAll">{{ $t('account.i18n_sync_all') }}</el-checkbox>
      <el-card class="box-card el-box-card" v-for="(accountGroup, index) in accountGroups" :key="index">
        <div slot="header" class="clearfix">
            <span>
              <img :src="require(`@/assets/img/platform/${accountGroup.accountWithBLOBs.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
               &nbsp;&nbsp; {{ accountGroup.accountWithBLOBs.pluginName }} {{ $t('rule.rule_set') }} | {{accountGroup.accountWithBLOBs.name}}
            </span>
          <el-button style="float: right; padding: 3px 0" type="text"  @click="handleCheckAllByAccount(accountGroup, index)">{{ $t('account.i18n_sync_all') }}</el-button>
        </div>
        <el-checkbox-group v-model="checkedGroups" @change="handleCheckedGroupsChange(accountGroup)">
          <el-checkbox v-for="(group,index) in accountGroup.groups" :label="accountGroup.accountWithBLOBs.id + '/' + group.id" :value="accountGroup.accountWithBLOBs.id + '/' + group.id" :key="index" border >
            {{ group.name }}
          </el-checkbox>
        </el-checkbox-group>
      </el-card>
      <dialog-footer
        @cancel="scanVisible = false"
        @confirm="scanGroup()"/>
    </el-dialog>
    <!-- 一键扫描选择扫描组 -->

  </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "../head/TableHeader";
import TableOperator from "../../common/components/TableOperator";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import VulnStatus from "./VulnStatus";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {VULN_CONFIGS} from "../../common/components/search/search-components";
import ProxyDialogFooter from "../head/ProxyDialogFooter";
import ProxyDialogCreateFooter from "../head/ProxyDialogCreateFooter";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {ACCOUNT_ID, ACCOUNT_NAME} from "@/common/js/constants";

/* eslint-disable */
export default {
  components: {
    TableOperators,
    VulnStatus,
    MainContainer,
    Container,
    TableHeader,
    TablePagination,
    TableOperator,
    DialogFooter,
    ProxyDialogFooter,
    ProxyDialogCreateFooter
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
        components: VULN_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      selectIds: new Set(),
      createVisible: false,
      updateVisible: false,
      scanVisible: false,
      innerDrawer: false,
      innerDrawerProxy: false,
      plugins: [],
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
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
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
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
        ],
        proxyPassword: [
          {required: false, message: this.$t('proxy.proxy_password'), trigger: 'blur'},
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
        ],
      },
      buttons: [
        {
          tip: this.$t('account.tuning'), icon: "el-icon-setting", type: "success",
          exec: this.handleScan
        }, {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
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
      cmOptions: {
        tabSize: 4,
        mode: {
          name: 'json',
          json: true
        },
        theme: 'bespin',
        lineNumbers: true,
        line: true,
        indentWithTabs: true,
      },
      checkAll: false,
      //选中的规则组的acccount/id集合
      checkedGroups: [],
      //漏扫信息规则组list分组
      accountGroups: [],
      isIndeterminate: true,
      //漏扫信息规则组拼接可用类型： [acccount/id]
      groupsSelect: [],
      proxyType: [
        {id: 'Http', value: "Http"},
        {id: 'Https', value: "Https"},
      ],
    }
  },

  watch: {
    '$route': 'init'
  },

  methods: {
    create() {
      this.addAccountForm = [ { "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] } ];
      this.createVisible = true;
      this.activePlugin();
      this.activeProxy();
    },
    innerDrawerClose() {
      this.innerDrawer = false;
    },
    innerDrawerProxyClose() {
      this.innerDrawerProxy = false;
    },
    //校验漏洞扫描账号
    validate() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('account.please_choose_account'));
        return;
      }
      this.$alert(this.$t('account.one_validate') + this.$t('vuln.vuln_setting') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            let formData = new FormData();
            this.result = this.$request({
              method: 'POST',
              url: "/account/validate",
              data: Array.from(this.selectIds),
              headers: {
                'Content-Type': undefined
              }
            }, res => {
              if (res.data) {
                this.$success(this.$t('account.success'));
              } else {
                this.$error(this.$t('account.error'));
              }
              this.search();
            });
          }
        }
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
      let url = "/vuln/vulnList/" + this.currentPage + "/" + this.pageSize;
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
      this.updateVisible = true;
      this.activePlugin();
      this.activeProxy();
      this.changePlugin(tmp.pluginId, 'edit');
    },
    handleClose() {
      this.createVisible =  false;
      this.updateVisible =  false;
      this.handleScan = false;
    },
    handleDelete(obj) {
      this.$alert(this.$t('account.delete_confirm') + obj.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$post("/account/delete/" + obj.id, {}, () => {
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
    handleCopy(test) {
      this.$refs.apiCopy.open(test);
    },
    //调参漏扫信息对应的规则
    handleScan(params) {
      this.$router.push({
        path: '/account/accountscan/' + params.id,
      }).catch(error => error);
    },
    //查询插件
    activePlugin() {
      let url = "/plugin/vuln";
      this.result = this.$get(url, response => {
        let data = response.data;
        this.plugins =  data;
      });
    },
    //查询代理
    activeProxy() {
      let url = "/proxy/list/all";
      this.result = this.$get(url, response => {
        this.proxys = response.data;
      });
    },
    init() {
      this.selectIds.clear();
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
    //新增漏扫信息选择插件查询漏扫信息信息
    async changePluginForAdd (form){
      let url = "/plugin/";
      this.result = await this.$get(url + form.pluginId, response => {
        let fromJson = typeof(response.data) === 'string'?JSON.parse(response.data):response.data;
        form.tmpList = fromJson.data;
        for (let tmp of form.tmpList) {
          if (tmp.defaultValue !== undefined) {
            tmp.input = tmp.defaultValue;
          }
        }
      });
    },
    //编辑漏扫信息选择插件查询漏扫信息
    async changePlugin (pluginId, type){
      let url = "/plugin/";
      this.result = await this.$get(url + pluginId, response => {
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
    //保存漏扫信息
    saveAccount(addAccountForm, type){
      for (let item of addAccountForm) {
        if (!item.tmpList.length) {
          this.$error(this.$t('account.i18n_account_cloud_plugin_param'));
          return;
        }
        let data = {}, key = {};
        for (let tmp of item.tmpList) {
          key[tmp.name] = tmp.input.trim();
        }
        data["credential"] = JSON.stringify(key);
        data["name"] = item.name;
        data["pluginId"] = item.pluginId;
        if (item.isProxy) data["proxyId"] = item.proxyId;

        if (type === 'add') {
          this.result = this.$post("/account/add", data,response => {
            if (response.success) {
              this.$success(this.$t('account.i18n_hr_create_success'));
              this.search();
              this.handleClose();
              location.reload();
            } else {
              this.$error(response.message);
            }
          });
        }
      }
    },
    //编辑漏扫信息
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
          if (item.isProxy) data["proxyId"] = item.proxyId;

          if (type === 'add') {
            this.result = this.$post("/account/add", data,response => {
              if (response.success) {
                this.$success(this.$t('account.i18n_hr_create_success'));
                this.search();
                this.handleClose();
              } else {
                this.$error(response.message);
              }
            });
          } else {
            data["id"] = item.id;
            this.result = this.$post("/account/update", data,response => {
              if (response.success) {
                this.$success(this.$t('account.i18n_hr_update_success'));
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
    tableRowClassName({row, rowIndex}) {
      if (rowIndex % 4 === 0) {
        return 'success-row';
      } else if (rowIndex % 2 === 0) {
        return 'warning-row';
      } else {
        return '';
      }
    },
    scan (){
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('vuln.please_choose_vuln'));
        return;
      }
      for (let accountId of this.selectIds) {
        for (let item of this.tableData) {
          if (accountId === item.id) {
            if (item.status === "INVALID") {
              this.$warning(this.$t('account.invalid_cloud_account'));
              return;
            }
          }
        }
      }
      this.openScanGroup();
    },
    openScanGroup() {
      this.scanVisible = true;
      this.initGroups();
    },
    scanGroup () {
      let account = this.$t('account.one_scan') + this.$t('vuln.vuln_rule');
      this.$alert( account + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            let formData = new FormData();
            if (this.checkedGroups.length === 0) {
              this.$warning(this.$t('account.please_choose_rule_group'));
              return;
            }
            formData.append('scanCheckedGroups', new Blob([JSON.stringify(Array.from(this.checkedGroups))], {
              type: "application/json"
            }));
            this.result = this.$request({
              method: 'POST',
              url: "/rule/scan",
              data: formData,
              headers: {
                'Content-Type': undefined
              }
            }, () => {
              for (let item of this.tableData) {
                for (let id of this.selectIds) {
                  if (id===item.id) {
                    localStorage.setItem(ACCOUNT_ID, item.id);
                    localStorage.setItem(ACCOUNT_NAME, item.name);
                    break;
                  }
                }
              }
              this.$success(this.$t('account.i18n_hr_create_success'));
            });
            this.$router.push({
              path: '/vuln/result',
            }).catch(error => error);
          }
        }
      });
    },
    createProxy(createProxyForm) {
      this.$refs[createProxyForm].validate(valid => {
        if (valid) {
          this.result = this.$post('/proxy/add', this.proxyForm, () => {
            this.$success(this.$t('commons.save_success'));
            this.innerDrawerProxy = false;
            this.activeProxy();
          });
        } else {
          return false;
        }
      })
    },
    updateProxy(updateProxyForm) {
      this.$refs[updateProxyForm].validate(valid => {
        if (valid) {
          this.result = this.$post('/proxy/update', this.proxyForm, () => {
            this.$success(this.$t('commons.modify_success'));
            this.innerDrawerProxy = false;
          });
        } else {
          return false;
        }
      })
    },
    handleCheckAllByAccount(val, index) {
      let arr = [];
      if (val) {
        for (let obj of val.groups) {
          arr.push(val.accountWithBLOBs.id + "/" + obj.id);
        }
      }
      let concatArr = this.checkedGroups.concat(arr);
      this.checkedGroups = !this.isContain(this.checkedGroups, arr) ? Array.from(concatArr) : this.checkedGroups.filter(n => !arr.toString().includes(n));
      this.checkAll = this.checkedGroups.length === this.groupsSelect.length;
      this.isIndeterminate = this.checkedGroups.length > 0 && this.checkedGroups.length < this.groupsSelect.length;
    },
    handleCheckAll() {
      this.checkedGroups = this.checkedGroups.length === 0 ? this.groupsSelect : [];
      this.checkAll = this.checkedGroups.length === this.groupsSelect.length;
      this.isIndeterminate = this.checkedGroups.length > 0 && this.checkedGroups.length < this.groupsSelect.length;
    },
    handleCheckedGroupsChange(value) {
      let checkedCount = value.checkedGroups.length;
      this.checkAll = checkedCount === this.groupsSelect.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.groupsSelect.length;
    },
    initGroups() {
      let formData = new FormData();
      formData.append('selectIds', new Blob([JSON.stringify(Array.from(this.selectIds))], {
        type: "application/json"
      }));
      this.result = this.$request({
        method: 'POST',
        url: "/rule/groups",
        data: formData,
        headers: {
          'Content-Type': undefined
        }
      }, (res) => {
        this.accountGroups = res.data;
        for (let item of this.accountGroups) {
          let accountGroup = {accountId: item.accountWithBLOBs.id, checkedGroups: []};
          let checkedGroups = [];
          for(let group of item.groups) {
            checkedGroups.push(item.accountWithBLOBs.id + "/" + group.id);
            this.checkedGroups.push(item.accountWithBLOBs.id + "/" + group.id);
            this.groupsSelect.push(item.accountWithBLOBs.id + "/" + group.id);
          }
          accountGroup.checkedGroups = checkedGroups;
          item.checkedGroups = checkedGroups;
        }
      });
    },
    isContain (arr1, arr2) {
      for (var i = arr2.length - 1; i >= 0; i--) {
        for (let obj of arr1) {
          if(obj === arr2[i]){
            return true;
          }
        }
      }
      return false;
    },
    addAccount (addAccountForm) {
      let newParam = { "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] };
      addAccountForm.push(newParam);
    },
    deleteAccount (parameter, p) {
      for (let i in parameter) {
        if (parameter[i].name === p.name) {
          parameter.splice(i, 1);
        }
      }
    },
  },
  created() {
    this.init();
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
</style>
