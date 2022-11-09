<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('oss.oss_account_list')"
                      @create="create" :createTip="$t('oss.create')"
                      :show-validate="false" :show-create="true"/>

      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort"
                :row-class-name="tableRowClassName"
                @filter-change="filter">
        <el-table-column type="index" min-width="2%"/>
        <el-table-column prop="name" :label="$t('oss.oss_account')" min-width="12%" show-overflow-tooltip></el-table-column>
        <el-table-column :label="$t('account.cloud_platform')" min-width="10%" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.pluginName }}
              </span>
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

    <!--oss account-->
    <el-drawer class="rtl" :title="$t('account.update')" :visible.sync="visible" size="50%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <div v-loading="cloudResult.loading">
        <el-form :model="form" label-position="right" label-width="150px" size="small" :rules="rule" ref="accountForm">
          <el-form-item :label="$t('account.name')"  ref="name" prop="name">
            <el-input v-model="form.name" autocomplete="off" :placeholder="$t('account.input_name')"/>
          </el-form-item>
          <el-form-item :label="$t('account.cloud_platform')" :rules="{required: true, message: $t('account.cloud_platform') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="form.pluginId" :placeholder="$t('account.please_choose_plugin')" @change="changePlugin(form.pluginId)">
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
          @cancel="visible = false"
          @add="innerDrawerProxy = true"
          @confirm="saveOss(form, ossType)"/>
      </div>
    </el-drawer>
    <!--oss account-->

  </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import Regions from "@/business/components/account/home/Regions";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import ProxyDialogFooter from "@/business/components/common/components/ProxyDialogFooter";
import ProxyDialogCreateFooter from "@/business/components/common/components/ProxyDialogCreateFooter";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {ACCOUNT_CONFIGS} from "@/business/components/common/components/search/search-components";

/* eslint-disable */
export default {
  components: {
    TableOperators,
    Regions,
    MainContainer,
    Container,
    TableHeader,
    TablePagination,
    DialogFooter,
    ProxyDialogFooter,
    ProxyDialogCreateFooter
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
      script: '',
      direction: 'rtl',
      form: [ { "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] } ],
      visible: false,
      innerDrawer: false,
      innerDrawerProxy: false,
      plugins: [],
      accounts: [],
      proxys: [],
      tmpList: [],
      item: {},
      iamStrategyNotSupport: ['hummer-openstack-plugin', 'hummer-vsphere-plugin', 'hummer-nuclei-plugin', 'hummer-server-plugin', 'hummer-xray-plugin', 'hummer-tsunami-plugin'],
      proxyForm: {},
      proxyType: [
        {id: 'Http', value: "Http"},
        {id: 'Https', value: "Https"},
      ],
      ossType: 'add',
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
    }
  },
  methods: {
    //查询列表
    search() {
      let url = "/oss/list/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleClose() {
      this.visible =  false;
    },
    showRegions (tmp) {
      this.regions = tmp.regions;
    },
    //查询插件
    activePlugin() {
      let url = "/plugin/cloud";
      this.result = this.$get(url, response => {
        let data = response.data;
        this.plugins =  data;
      });
    },
    //查询可以添加的云账号到对象存储账号列表中
    activeAccount() {
      let url = "/oss/accounts";
      this.result = this.$get(url, response => {
        let data = response.data;
        this.accounts =  data;
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
    tableRowClassName({row, rowIndex}) {
      if (rowIndex % 4 === 0) {
        return 'success-row';
      } else if (rowIndex % 2 === 0) {
        return 'warning-row';
      } else {
        return '';
      }
    },
    create() {
      this.form = [ { "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] } ];
      this.visible = true;
      this.activePlugin();
      this.activeProxy();
    },
    //选择插件查询云账号信息
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
    change(e) {
      this.$forceUpdate();
    },
    innerDrawerClose() {
      this.innerDrawer = false;
    },
    innerDrawerProxyClose() {
      this.innerDrawerProxy = false;
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
    //编辑oss账号
    saveOss(item, type){
      if (!this.tmpList.length) {
        this.$warning(this.$t('account.i18n_account_cloud_plugin_param'));
        return;
      }
      this.$refs['form'].validate(valid => {
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
            this.cloudResult = this.$post("/account/add", data,response => {
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
            this.cloudResult = this.$post("/account/update", data,response => {
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
</style>

