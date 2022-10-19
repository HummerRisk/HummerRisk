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
                @filter-change="filter">
        <el-table-column type="index" min-width="2%"/>
        <el-table-column prop="name" :label="$t('fs.name')" min-width="15%" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/fs/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="fileName" :label="$t('fs.file_name')" min-width="20%" show-overflow-tooltip/>
        <el-table-column prop="size" :label="$t('fs.size')" min-width="8%" show-overflow-tooltip/>
        <el-table-column min-width="13%" :label="$t('account.update_time')" sortable
                         prop="updateTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" :label="$t('account.creator')" min-width="8%" show-overflow-tooltip/>
        <el-table-column min-width="12%" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>
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
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
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
    }
  },
  watch: {
    '$route': 'init'
  },
  methods: {
    create() {
      this.addAccountForm = {};
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
    appendTar(file) {
      this.tarFile = file;
    },
    //查询列表
    search() {
      let url = "/fs/list/" + this.currentPage + "/" + this.pageSize;
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
            this.result = this.$get("/fs/deleteFs/" + obj.id, () => {
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
            url: "/fs/updateFs",
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
    tableRowClassName({row, rowIndex}) {
      if (rowIndex % 4 === 0) {
        return 'success-row';
      } else if (rowIndex % 2 === 0) {
        return 'warning-row';
      } else {
        return '';
      }
    },
    handleScan(item) {
      this.$alert(this.$t('image.one_scan') + item.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$get("/fs/scan/" + item.id,response => {
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
</style>

