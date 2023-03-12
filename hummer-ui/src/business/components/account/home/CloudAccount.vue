<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <table-header :condition.sync="condition" @search="search"
                           :title="$t('account.cloud_account_list')"
                           @create="create" :createTip="$t('account.create')"
                           @validate="validate" :validateTip="$t('account.one_validate')"
                           :show-validate="true" :show-create="true"
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
          <el-table-column type="selection" id="selection"  prop="selection" min-width="50">
          </el-table-column>
          <el-table-column type="index" prop="index" min-width="50"/>
          <el-table-column prop="name" v-if="checkedColumnNames.includes('name')" :label="$t('account.name')" min-width="150" show-overflow-tooltip></el-table-column>
          <el-table-column prop="pluginName" v-if="checkedColumnNames.includes('pluginName')" :label="$t('account.cloud_platform')" min-width="150" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.pluginName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="status" v-if="checkedColumnNames.includes('status')" min-width="100" :label="$t('account.status')"
                           column-key="status"
                           :filters="statusFilters"
                           :filter-method="filterStatus">
            <template v-slot:default="{row}">
              <account-status @search="search" :row="row"/>
            </template>
          </el-table-column>
          <el-table-column prop="regions" v-if="checkedColumnNames.includes('regions')" :label="$t('account.regions')" min-width="100">
            <template v-slot:default="scope">
              <regions :row="scope.row"/>
            </template>
          </el-table-column>
          <el-table-column min-width="200" v-if="checkedColumnNames.includes('createTime')" :label="$t('account.create_time')" sortable prop="createTime">
            <template v-slot:default="scope">
              <span>{{ scope.row.createTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column min-width="200" v-if="checkedColumnNames.includes('updateTime')" :label="$t('account.update_time')" sortable prop="updateTime">
            <template v-slot:default="scope">
              <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="userName" v-if="checkedColumnNames.includes('userName')" :label="$t('account.creator')" min-width="100" show-overflow-tooltip/>
          <el-table-column min-width="170" id="fixed" :label="$t('commons.operating')" prop="operating" type="operating" fixed="right">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </hide-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
      </el-card>

      <!--Create account-->
      <el-drawer class="rtl" :title="$t('account.create')" :visible.sync="createVisible" size="50%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div v-loading="cloudResult.loading">
          <div v-for="(form, index) in addAccountForm" :key="index">
            <el-form :model="form" label-position="right" label-width="150px" size="medium" :rules="rule" :ref="'addAccountForm' + index">
              <el-form-item :label="$t('account.name')" ref="name" prop="name">
                <el-input v-model="form.name" autocomplete="off" :placeholder="$t('account.input_name')"/>
              </el-form-item>
              <el-form-item :label="$t('account.cloud_platform')" :rules="{required: true, message: $t('account.cloud_platform') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-select style="width: 100%;" filterable :clearable="true" v-model="form.pluginId" :placeholder="$t('account.please_choose_plugin')" @change="changePluginForAdd(form)">
                  <el-option
                    v-for="item in plugins"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                    <img :src="require(`@/assets/img/platform/${item.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                    &nbsp;&nbsp; {{ item.name }}
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
              <el-form-item v-if="form.isProxy && form.pluginId && iamStrategyNotSupport.indexOf(form.pluginId) === -1" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-select style="width: 100%;" filterable :clearable="true" v-model="form.proxyId" :placeholder="$t('commons.proxy')">
                  <el-option
                    v-for="item in proxys"
                    :key="item.id"
                    :label="item.proxyIp"
                    :value="item.id">
                    &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item v-if="form.pluginId && iamStrategyNotSupport.indexOf(form.pluginId) === -1" :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-switch v-model="form.isProxy"></el-switch>
              </el-form-item>
              <el-form-item v-if="form.script && iamStrategyNotSupport.indexOf(form.pluginId) === -1">
                <el-link type="danger" @click="addAccountIam(form)">{{ $t('account.iam_strategy') }}</el-link>
                <div>
                  <el-drawer
                    size="45%"
                    :title="$t('account.iam_strategy')"
                    :append-to-body="true"
                    :before-close="innerDrawerClose"
                    :visible.sync="innerDrawer">
                    <el-form-item>
                      <codemirror ref="cmEditor" v-model="script" class="code-mirror" :options="cmOptions" />
                    </el-form-item>
                  </el-drawer>
                </div>
              </el-form-item>
              <el-form-item v-if="index > 0" :label="$t('account.delete_this_cloud_account')">
                <el-button type="danger" icon="el-icon-delete" plain size="small" @click="deleteAccount(addAccountForm, form)">{{ $t('commons.delete') }}</el-button>
              </el-form-item>
            </el-form>
            <el-divider><i class="el-icon-cloudy"> {{ (index + 1) }}</i></el-divider>
          </div>
          <div>
            <el-drawer
              size="45%"
              :title="$t('proxy.add_proxy')"
              :append-to-body="true"
              :before-close="innerDrawerProxyClose"
              :visible.sync="innerDrawerProxy">
              <el-form :model="proxyForm" label-position="right" label-width="120px" size="small" :rules="rule" ref="createProxyForm">
                <el-form-item :label="$t('commons.proxy_type')" :rules="{required: true, message: $t('commons.proxy_type') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                  <el-select style="width: 100%;" filterable :clearable="true" v-model="proxyForm.proxyType" :placeholder="$t('commons.proxy_type')">
                    <el-option
                      v-for="item in proxyType"
                      :key="item.id"
                      :label="item.value"
                      :value="item.id">
                      &nbsp;&nbsp; {{ item.value }}
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="Proxy IP" prop="proxyIp">
                  <el-input v-model="proxyForm.proxyIp" autocomplete="off" :placeholder="$t('proxy.proxy_ip')"/>
                </el-form-item>
                <el-form-item :label="$t('commons.proxy_port')" prop="proxyPort">
                  <el-input type="number" v-model="proxyForm.proxyPort" autocomplete="off" :placeholder="$t('proxy.proxy_port')"/>
                </el-form-item>
                <el-form-item :label="$t('commons.proxy_name')" prop="proxyName">
                  <el-input v-model="proxyForm.proxyName" autocomplete="off" :placeholder="$t('proxy.proxy_name')"/>
                </el-form-item>
                <el-form-item :label="$t('commons.proxy_password')" prop="proxyPassword" style="margin-bottom: 29px">
                  <el-input v-model="proxyForm.proxyPassword" autocomplete="new-password" show-password
                            :placeholder="$t('proxy.proxy_password')"/>
                </el-form-item>
              </el-form>
              <dialog-footer
                @cancel="innerDrawerProxy = false"
                @confirm="createProxy('createProxyForm')"/>
            </el-drawer>
          </div>
          <proxy-dialog-create-footer
            @cancel="createVisible = false"
            @add="innerDrawerProxy = true"
            @addAccount="addAccount(addAccountForm)"
            @confirm="saveAccount(addAccountForm, 'add')"/>
        </div>
      </el-drawer>
      <!--Create account-->

      <!--Update account-->
      <el-drawer class="rtl" :title="$t('account.update')" :visible.sync="updateVisible" size="50%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div v-loading="cloudResult.loading">
          <el-form :model="form" label-position="right" label-width="150px" size="small" :rules="rule" ref="accountForm">
            <el-form-item :label="$t('account.name')"  ref="name" prop="name">
              <el-input v-model="form.name" autocomplete="off" :placeholder="$t('account.input_name')"/>
            </el-form-item>
            <el-form-item :label="$t('account.cloud_platform')" :rules="{required: true, message: $t('account.cloud_platform') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-select style="width: 100%;" disabled v-model="form.pluginId" :placeholder="$t('account.please_choose_plugin')" @change="changePlugin(form.pluginId)">
                <el-option
                  v-for="item in plugins"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                  <img :src="require(`@/assets/img/platform/${item.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
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
            <el-form-item v-if="form.isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
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
            <el-form-item v-if="iamStrategyNotSupport.indexOf(form.pluginId) === -1" :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-switch v-model="form.isProxy"></el-switch>
            </el-form-item>
            <el-form-item v-if="script">
              <el-link v-if="iamStrategyNotSupport.indexOf(form.pluginId) === -1" type="danger" @click="innerDrawer = true">{{ $t('account.iam_strategy') }}</el-link>
              <div v-if="iamStrategyNotSupport.indexOf(form.pluginId) === -1">
                <el-drawer
                  size="45%"
                  :title="$t('account.iam_strategy')"
                  :append-to-body="true"
                  :before-close="innerDrawerClose"
                  :visible.sync="innerDrawer">
                  <el-form-item>
                    <codemirror ref="cmEditor" v-model="script" class="code-mirror" :options="cmOptions" />
                  </el-form-item>
                </el-drawer>
              </div>
              <div>
                <el-drawer
                  size="45%"
                  :title="$t('proxy.add_proxy')"
                  :append-to-body="true"
                  :before-close="innerDrawerProxyClose"
                  :visible.sync="innerDrawerProxy">
                  <el-form :model="proxyForm" label-position="right" label-width="120px" size="small" :rules="rule" ref="updateProxyForm">
                    <el-form-item :label="$t('commons.proxy_type')" :rules="{required: true, message: $t('commons.proxy_type') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                      <el-select style="width: 100%;" v-model="proxyForm.proxyType" :placeholder="$t('commons.proxy_type')">
                        <el-option
                          v-for="item in proxyType"
                          :key="item.id"
                          :label="item.value"
                          :value="item.id">
                          &nbsp;&nbsp; {{ item.value }}
                        </el-option>
                      </el-select>
                    </el-form-item>
                    <el-form-item label="Proxy IP" prop="proxyIp">
                      <el-input v-model="proxyForm.proxyIp" autocomplete="off" :placeholder="$t('proxy.proxy_ip')"/>
                    </el-form-item>
                    <el-form-item :label="$t('commons.proxy_port')" prop="proxyPort">
                      <el-input type="number" v-model="proxyForm.proxyPort" autocomplete="off" :placeholder="$t('proxy.proxy_port')"/>
                    </el-form-item>
                    <el-form-item :label="$t('commons.proxy_name')" prop="proxyName">
                      <el-input v-model="proxyForm.proxyName" autocomplete="off" :placeholder="$t('proxy.proxy_name')"/>
                    </el-form-item>
                    <el-form-item :label="$t('commons.proxy_password')" prop="proxyPassword" style="margin-bottom: 29px">
                      <el-input v-model="proxyForm.proxyPassword" autocomplete="new-password" show-password
                                :placeholder="$t('proxy.proxy_password')"/>
                    </el-form-item>
                  </el-form>
                  <dialog-footer
                    @cancel="innerDrawerProxy = false"
                    @confirm="createProxy('updateProxyForm')"/>
                </el-drawer>
              </div>
            </el-form-item>
          </el-form>
          <proxy-dialog-footer
          @cancel="updateVisible = false"
          @add="innerDrawerProxy = true"
          @confirm="editAccount(form, 'update')"/>
        </div>
      </el-drawer>
      <!--Update account-->

      <!-- 一键检测选择规则组 -->
      <el-dialog :close-on-click-modal="false"
                 :modal-append-to-body="false"
                 :title="$t('account.scan_group_quick')"
                 :visible.sync="scanVisible"
                 class="" width="70%">
        <div v-loading="groupResult.loading">
          <el-card class="box-card el-box-card">
            <div slot="header" class="clearfix">
              <span>
                <img :src="require(`@/assets/img/platform/${accountWithGroup.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
             &nbsp;&nbsp; {{ accountWithGroup.pluginName }} {{ $t('rule.rule_set') }} | {{ accountWithGroup.name }}
              </span>
              <el-button style="float: right; padding: 3px 0" type="text"  @click="handleCheckAllByAccount">{{ $t('account.i18n_sync_all') }}</el-button>
            </div>
            <el-checkbox-group v-model="checkedGroups">
              <el-checkbox v-for="(group, index) in groups" :label="group.id" :value="group.id" :key="index" border >
                  {{ group.name }}
              </el-checkbox>
            </el-checkbox-group>
          </el-card>
          <dialog-footer
            @cancel="scanVisible = false"
            @confirm="scanGroup()"/>
        </div>
      </el-dialog>
      <!-- 一键检测选择检测组 -->

    </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import AccountStatus from "./AccountStatus";
import Regions from "./Regions";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {ACCOUNT_CONFIGS} from "../../common/components/search/search-components";
import ProxyDialogFooter from "@/business/components/common/components/ProxyDialogFooter";
import ProxyDialogCreateFooter from "@/business/components/common/components/ProxyDialogCreateFooter";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {ACCOUNT_ID, ACCOUNT_NAME} from "@/common/js/constants";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {accountListUrl, deleteAccountUrl, iamStrategyUrl, validateUrl} from "@/api/cloud/account/account";
import {addProxyUrl, cloudPluginUrl, proxyListAllUrl, updateProxyUrl} from "@/api/system/system";
import {groupsByAccountId, ruleScanUrl} from "@/api/cloud/rule/rule";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'account.name',
    props: 'name',
    disabled: false
  },
  {
    label: 'account.cloud_platform',
    props: 'pluginName',
    disabled: false
  },
  {
    label: 'account.status',
    props: 'status',
    disabled: false
  },
  {
    label: 'account.regions',
    props: 'regions',
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
    components: {
      TableOperators,
      AccountStatus,
      Regions,
      MainContainer,
      Container,
      TableHeader,
      TablePagination,
      DialogFooter,
      ProxyDialogFooter,
      ProxyDialogCreateFooter,
      HideTable,
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
        cloudResult: {},
        groupResult: {},
        condition: {
          components: ACCOUNT_CONFIGS
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
        regions: "",
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
            tip: this.$t('account.one_scan'), icon: "el-icon-s-promotion", type: "success",
            exec: this.openScanGroup
          },
          {
            tip: this.$t('account.tuning'), icon: "el-icon-setting", type: "warning",
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
        proxyType: [
          {id: 'Http', value: "Http"},
          {id: 'Https', value: "Https"},
        ],
        accountWithGroup: {pluginIcon: 'aliyun.png'},
        checkedGroups: [],
        groups: [],
        iamStrategyNotSupport: ['hummer-openstack-plugin', 'hummer-vsphere-plugin', 'hummer-nuclei-plugin', 'hummer-server-plugin', 'hummer-xray-plugin', 'hummer-tsunami-plugin'],
        checkedColumnNames: columnOptions.map((ele) => ele.props),
        columnNames: columnOptions,
        //名称搜索
        items: [
          {
            name: 'account.name',
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
      //校验云账号
      validate() {
        if (this.selectIds.size === 0) {
          this.$warning(this.$t('account.please_choose_account'));
          return;
        }
        this.$alert(this.$t('account.one_validate') + this.$t('account.cloud_account') + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$request({
                method: 'POST',
                url: validateUrl,
                data: Array.from(this.selectIds),
                headers: {
                  'Content-Type': undefined
                }
              }, res => {
                if (res.data.length == 0) {
                  this.$success(this.$t('commons.success'));
                } else {
                  let name = '';
                  for (let item of res.data) {
                    name = name + ' ' + item.name + ';';
                  }
                  this.$error(this.$t('account.failed_cloud') + name);
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
        let url = accountListUrl + this.currentPage + "/" + this.pageSize;
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
              this.result = this.$post(deleteAccountUrl + obj.id, {}, () => {
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
      addAccountIam(form) {
        //点击需要的IAM策略按钮
        this.$get(iamStrategyUrl + form.pluginId,res => {
          form.script = res.data;
          this.script = res.data;
          this.innerDrawer = true;
        });
      },
      showRegions (tmp) {
        this.regions = tmp.regions;
      },
      //调参云账号对应的规则
      handleScan(params) {
        this.$router.push({
          path: accountScanUrl + params.id,
        }).catch(error => error);
      },
      //查询插件
      activePlugin() {
        let url = cloudPluginUrl;
        this.result = this.$get(url, response => {
          let data = response.data;
          this.plugins =  data;
        });
      },
      //查询代理
      activeProxy() {
        this.result = this.$get(proxyListAllUrl, response => {
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
      //新增云账号选择插件查询云账号信息
      async changePluginForAdd (form){
        this.$get(iamStrategyUrl + form.pluginId,res => {
          form.script = res.data;
          this.script = res.data;
        });
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
      //编辑云账号选择插件查询云账号信息
      async changePlugin (pluginId, type){
        this.$get(iamStrategyUrl + pluginId,res => {
          this.script = res.data;
        });
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
      //保存云账号
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
            this.cloudResult = this.$post(addAccountUrl, data,response => {
              if (response.success) {
                this.$success(this.$t('account.i18n_hr_create_success'));
                this.search();
                this.handleClose();
              } else {
                this.$error(response.message);
              }
            });
          }
        }
      },
      //编辑云账号
      editAccount(item, type){
        if (!this.tmpList.length) {
          this.$warning(this.$t('account.i18n_account_cloud_plugin_param'));
          return;
        }
        this.$refs['accountForm'].validate(valid => {
          if (valid) {
            let data = {}, key = {};
            for (let tmp of this.tmpList) {
              if(!tmp.input) {
                this.$warning(this.$t('vuln.no_plugin_param') + tmp.label);
                return;
              }
              key[tmp.name] = tmp.input;
            }
            data["credential"] = JSON.stringify(key);
            data["name"] = item.name;
            data["pluginId"] = item.pluginId;
            if (item.isProxy) data["proxyId"] = item.proxyId;

            if (type === 'add') {
              this.cloudResult = this.$post(addAccountUrl, data,response => {
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
              this.cloudResult = this.$post(updateAccountUrl, data,response => {
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
      openScanGroup(account) {
        if (account.status === 'INVALID') {
          this.$warning(account.name + ':' + this.$t('account.failed_status'));
          return;
        }
        this.accountWithGroup = account;
        localStorage.setItem(ACCOUNT_ID, account.id);
        localStorage.setItem(ACCOUNT_NAME, account.name);
        this.initGroups(account.pluginId);
        this.scanVisible = true;
      },
      scanGroup () {
        let account = this.$t('account.one_scan') + this.$t('account.cloud_account');
        this.$alert( account + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              if (this.checkedGroups.length === 0) {
                this.$warning(this.$t('account.please_choose_rule_group'));
                return;
              }
              let params = {
                accountId: this.accountWithGroup.id,
                groups: this.checkedGroups
              }
              this.groupResult = this.$post(ruleScanUrl, params, () => {
                this.$success(this.$t('account.i18n_hr_create_success'));
                this.scanVisible = false;
                this.$router.push({
                  path: '/account/result',
                }).catch(error => error);
              });
            }
          }
        });
      },
      createProxy(createProxyForm) {
        this.$refs[createProxyForm].validate(valid => {
          if (valid) {
            this.result = this.$post(addProxyUrl, this.proxyForm, () => {
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
            this.result = this.$post(updateProxyUrl, this.proxyForm, () => {
              this.$success(this.$t('commons.modify_success'));
              this.innerDrawerProxy = false;
            });
          } else {
            return false;
          }
        })
      },
      initGroups(pluginId) {
        this.result = this.$get(groupsByAccountId + pluginId,response => {
          this.groups = response.data;
        });
      },
      addAccount (addAccountForm) {
        let newParam = { "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] };
        addAccountForm.push(newParam);
      },
      deleteAccount (parameter, p) {
        for (let i in parameter) {
          if (parameter[i].name === p.name) {
            parameter.splice(i, 1);
            return;
          }
        }
      },
      handleCheckAllByAccount() {
        if (this.checkedGroups.length === this.groups.length) {
          this.checkedGroups = [];
        } else {
          let arr = [];
          this.checkedGroups = [];
          for (let group of this.groups) {
            arr.push(group.id);
          }
          let concatArr = this.checkedGroups.concat(arr);
          this.checkedGroups = Array.from(concatArr);
        }
      },
    },
    computed: {
      codemirror() {
        return this.$refs.cmEditor.codemirror;
      }
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
  .el-box-card >>> .el-checkbox {
    margin: 5px 0;
  }
</style>
