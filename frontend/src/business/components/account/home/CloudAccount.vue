<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <table-header :condition.sync="condition" @search="search"
                           :title="$t('account.cloud_account_list')"
                           @create="create" :createTip="$t('account.create')"
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
          <el-table-column prop="name" :label="$t('account.name')" min-width="12%" show-overflow-tooltip></el-table-column>
          <el-table-column :label="$t('account.cloud_platform')" min-width="10%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ $t(scope.row.pluginName) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="status" min-width="8%" :label="$t('account.status')"
                           column-key="status"
                           :filters="statusFilters"
                           :filter-method="filterStatus">
            <template v-slot:default="{row}">
              <account-status @search="search" :row="row"/>
            </template>
          </el-table-column>
          <el-table-column min-width="15%" :label="$t('account.create_time')" sortable
                           prop="createTime">
            <template v-slot:default="scope">
              <span>{{ scope.row.createTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column min-width="15%" :label="$t('account.update_time')" sortable
                           prop="updateTime">
            <template v-slot:default="scope">
              <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="userName" :label="$t('account.creator')" min-width="8%" show-overflow-tooltip/>
          <el-table-column :label="$t('account.regions')" min-width="7%">
            <template v-slot:default="scope">
              <regions :row="scope.row"/>
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

      <!--Create account-->
      <el-drawer class="rtl" :title="$t('account.create')" :visible.sync="createVisible" size="50%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div v-for="(form, index) in addAccountForm" :key="index">
          <el-form :model="form" label-position="right" label-width="150px" size="medium" :rules="rule" :ref="'addAccountForm' + index">
              <el-form-item :label="$t('account.name')" ref="name" prop="name">
                <el-input v-model="form.name" autocomplete="off" :placeholder="$t('account.input_name')"/>
              </el-form-item>
              <el-form-item :label="$t('account.cloud_platform')" :rules="{required: true, message: $t('account.cloud_platform') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-select style="width: 100%;" v-model="form.pluginId" :placeholder="$t('account.please_choose_plugin')" @change="changePluginForAdd(form)">
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
              <el-form-item v-if="form.isProxy && form.pluginId && iamStrategyNotSupport.indexOf(form.pluginId) === -1" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
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
              @confirm="createProxy('createProxyForm')"/>
          </el-drawer>
        </div>
        <proxy-dialog-create-footer
          @cancel="createVisible = false"
          @add="innerDrawerProxy = true"
          @addAccount="addAccount(addAccountForm)"
          @confirm="saveAccount(addAccountForm, 'add')"/>
      </el-drawer>
      <!--Create account-->

      <!--Update account-->
      <el-drawer class="rtl" :title="$t('account.update')" :visible.sync="updateVisible" size="50%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
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
      </el-drawer>
      <!--Update account-->

      <!-- 一键检测选择规则组 -->
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
      <!-- 一键检测选择检测组 -->

    </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "../head/TableHeader";
import TableOperator from "../../common/components/TableOperator";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import AccountStatus from "./AccountStatus";
import Regions from "./Regions";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {ACCOUNT_CONFIGS} from "../../common/components/search/search-components";
import ProxyDialogFooter from "../head/ProxyDialogFooter";
import ProxyDialogCreateFooter from "../head/ProxyDialogCreateFooter";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {ACCOUNT_ID, ACCOUNT_NAME} from "@/common/js/constants";

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
        //云账号规则组list分组
        accountGroups: [],
        isIndeterminate: true,
        //云账号规则组拼接可用类型： [acccount/id]
        groupsSelect: [],
        proxyType: [
          {id: 'Http', value: "Http"},
          {id: 'Https', value: "Https"},
        ],
        iamStrategyNotSupport: ['hummer-openstack-plugin', 'hummer-vsphere-plugin', 'hummer-nuclei-plugin', 'hummer-server-plugin', 'hummer-xray-plugin', 'hummer-tsunami-plugin'],
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
        let url = "/account/list/" + this.currentPage + "/" + this.pageSize;
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
      addAccountIam(form) {
        //点击需要的IAM策略按钮
        this.$get("/account/iam/strategy/" + form.pluginId,res => {
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
          path: '/account/accountscan/' + params.id,
        }).catch(error => error);
      },
      //查询插件
      activePlugin() {
        let url = "/plugin/cloud";
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
      //新增云账号选择插件查询云账号信息
      async changePluginForAdd (form){
        this.$get("/account/iam/strategy/" + form.pluginId,res => {
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
        this.$get("/account/iam/strategy/" + pluginId,res => {
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
          this.$warning(this.$t('account.please_choose_account'));
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
        let account = this.$t('account.one_scan') + this.$t('account.cloud_account');
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
            return;
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
