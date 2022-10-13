<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('fs.fs_settings_list')"
                      @create="create" :createTip="$t('fs.fs_create')" :show-create="true"/>

      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort"
                :row-class-name="tableRowClassName"
                @filter-change="filter" @select-all="select" @select="select">
        <el-table-column type="selection" min-width="5%">
        </el-table-column>
        <el-table-column type="index" min-width="5%"/>
        <el-table-column prop="name" :label="$t('code.name')" min-width="15%" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/code/${scope.row.pluginIcon}`)" style="width: 30px; height: 25px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" min-width="10%" :label="$t('code.status')"
                         column-key="status"
                         :filters="statusFilters"
                         :filter-method="filterStatus">
          <template v-slot:default="{row}">
            <code-status :row="row"/>
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
        <el-table-column min-width="17%" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create code-->
    <el-drawer class="rtl" :title="$t('code.code_create')" :visible.sync="createVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
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
                <img :src="require(`@/assets/img/code/${item.id}`)" style="width: 20px; height: 16px; vertical-align:middle" alt=""/>
                &nbsp;&nbsp; {{ $t(item.name) }}
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
        <div style="margin: 10px;">
          <span style="color: red;">{{ $t('code.code_note') }}</span>
        </div>
      </div>
      <dialog-create-footer
        @cancel="createVisible = false"
        @addAccount="addAccount(addAccountForm)"
        @confirm="saveAccount(addAccountForm, 'add')"/>
    </el-drawer>
    <!--Create code-->

    <!--Update code-->
    <el-drawer class="rtl" :title="$t('code.code_update')" :visible.sync="updateVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
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
          <el-select style="width: 100%;" disabled v-model="form.pluginId" :placeholder="$t('code.platform')" @change="changePlugin('edit')">
            <el-option
              v-for="item in plugins"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              <img :src="require(`@/assets/img/code/${item.id}`)" style="width: 20px; height: 16px; vertical-align:middle" alt=""/>
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
      <div style="margin: 10px;">
        <div style="margin: 10px;">
          <span style="color: red;">{{ $t('code.code_note') }}</span>
        </div>
      </div>
      <dialog-footer
        @cancel="updateVisible = false"
        @confirm="editAccount(form, 'update')"/>
    </el-drawer>
    <!--Update code-->

  </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperator from "../../common/components/TableOperator";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import FsStatus from "./FsStatus";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {CODE_CONFIGS} from "../../common/components/search/search-components";
import DialogFooter from "../head/DialogFooter";
import DialogCreateFooter from "../head/DialogCreateFooter";

/* eslint-disable */
export default {
  components: {
    TableOperators,
    FsStatus,
    MainContainer,
    Container,
    TableHeader,
    TablePagination,
    TableOperator,
    DialogFooter,
    DialogCreateFooter,
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
          tip: this.$t('k8s.execute_scan'), icon: "el-icon-s-promotion", type: "success",
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
      proxyType: [
        {id: 'Http', value: "Http"},
        {id: 'Https', value: "Https"},
      ],
      sboms: [],
      versions: [],
    }
  },
  watch: {
    '$route': 'init'
  },
  methods: {
    create() {
      this.addAccountForm = [ { "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] } ];
      this.createVisible = true;
      this.activeProxy();
    },
    initSboms() {
      this.result = this.$post("/sbom/allSbomList", {},response => {
        this.sboms = response.data;
      });
    },
    changeSbom(item) {
      let params = {
        sbomId: item.sbomId
      };
      this.result = this.$post("/sbom/allSbomVersionList", params,response => {
        this.versions = response.data;
      });
    },
    //查询代理
    activeProxy() {
      let url = "/proxy/list/all";
      this.result = this.$get(url, response => {
        this.proxys = response.data;
      });
    },
    //校验Git项目账号
    validate() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('code.please_choose_code'));
        return;
      }
      this.$alert(this.$t('account.one_validate') + this.$t('code.code_setting') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            let formData = new FormData();
            this.result = this.$request({
              method: 'POST',
              url: "/code/validate",
              data: Array.from(this.selectIds),
              headers: {
                'Content-Type': undefined
              }
            }, res => {
              if (res.data) {
                this.$success(this.$t('commons.success'));
              } else {
                this.$error(this.$t('commons.error'));
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
      let url = "/code/list/" + this.currentPage + "/" + this.pageSize;
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
    },
    handleDelete(obj) {
      this.$alert(this.$t('commons.delete_confirm') + obj.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/code/deleteCode/" + obj.id, () => {
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
      let url = "/code/plugin";
      this.result = await this.$get(url, response => {
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
      let url = "/code/plugin";
      this.result = await this.$get(url, response => {
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
          this.result = this.$post("/code/addCode", data,response => {
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
            this.result = this.$post("/code/addCode", data,response => {
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
            this.result = this.$post("/code/updateCode", data,response => {
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
    tableRowClassName({row, rowIndex}) {
      if (rowIndex % 4 === 0) {
        return 'success-row';
      } else if (rowIndex % 2 === 0) {
        return 'warning-row';
      } else {
        return '';
      }
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
    handleScan(item) {
      this.$alert(this.$t('image.one_scan') + item.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$get("/code/scan/" + item.id,response => {
              if (response.success) {
                this.$success(this.$t('schedule.event_start'));
                this.$router.push({
                  path: '/code/result',
                  query: {
                    date:new Date().getTime()
                  },
                }).catch(error => error);
              } else {
                this.$error(response.message);
              }
            });
          }
        }
      });
    },
  },
  activated() {
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
</style>

