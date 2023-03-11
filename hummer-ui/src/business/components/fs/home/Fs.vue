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
        <el-table-column prop="name" :label="$t('fs.name')" v-if="checkedColumnNames.includes('name')" min-width="150" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/fs/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="fileName" v-if="checkedColumnNames.includes('fileName')" :label="$t('fs.file_name')" min-width="200" show-overflow-tooltip/>
        <el-table-column prop="size" v-if="checkedColumnNames.includes('size')" :label="$t('fs.size')" min-width="100" show-overflow-tooltip/>
        <el-table-column min-width="160" v-if="checkedColumnNames.includes('updateTime')" :label="$t('commons.update_time')" sortable
                         prop="updateTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" :label="$t('account.creator')" v-if="checkedColumnNames.includes('userName')" min-width="90" show-overflow-tooltip/>
        <el-table-column min-width="140" :label="$t('commons.operating')" fixed="right">
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
import {allSbomListUrl, allSbomVersionListUrl} from "@/api/k8s/sbom/sbom";
import {proxyListAllUrl} from "@/api/system/system";
import {deleteFsUrl, fsListUrl, scanFsUrl, updateFsUrl} from "@/api/k8s/fs/fs";

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
            url: "/fs/addFs",
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
                this.$router.push({
                  path: '/fs/result',
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
.table-card >>> .search {
  width: 450px !important;
}
.table-card >>> .search .el-input {
  width: 140px !important;
}
</style>

