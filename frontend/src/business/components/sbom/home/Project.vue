<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('sbom.project_list')"
                      @create="create" :createTip="$t('sbom.sbom_create')" :show-create="true"/>

      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort"
                :row-class-name="tableRowClassName" @filter-change="filter">
        <el-table-column type="index" min-width="3%"/>
        <el-table-column prop="name" :label="$t('sbom.name')" min-width="15%" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <i class="iconfont icon-SBOM sbom-icon"></i>
                <span slot="title">{{ scope.row.name }}</span>
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" :label="$t('sbom.desc')" min-width="20%" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span slot="title">{{ scope.row.description }}</span>
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
        <el-table-column min-width="15%" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create sbom-->
    <el-drawer class="rtl" :title="$t('sbom.sbom_create')" :visible.sync="createVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="addForm" label-position="right" label-width="150px" size="small" ref="addForm" :rules="rule">
        <el-form-item :label="$t('sbom.name')" ref="name" prop="name">
          <el-input v-model="addForm.name" autocomplete="off" :placeholder="$t('sbom.name')"/>
        </el-form-item>
        <el-form-item :label="$t('sbom.desc')" ref="name" prop="description">
          <el-input v-model="addForm.description" autocomplete="off" :placeholder="$t('sbom.desc')"/>
        </el-form-item>
        <el-form-item>
          <span style="color: red">{{ $t('sbom.sbom_note') }}</span>
        </el-form-item>
      </el-form>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="createVisible = false"
          @confirm="save()"/>
      </div>
    </el-drawer>
    <!--Create sbom-->

    <!--Update sbom-->
    <el-drawer class="rtl" :title="$t('sbom.sbom_update')" :visible.sync="updateVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form" :rules="rule">
        <el-form-item :label="$t('sbom.name')" ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('sbom.name')"/>
        </el-form-item>
        <el-form-item :label="$t('sbom.desc')" ref="name" prop="description">
          <el-input v-model="form.description" autocomplete="off" :placeholder="$t('sbom.desc')"/>
        </el-form-item>
        <el-form-item>
          <span style="color: red">{{ $t('sbom.sbom_note') }}</span>
        </el-form-item>
      </el-form>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="updateVisible = false"
          @confirm="edit()"/>
      </div>
    </el-drawer>
    <!--Update sbom-->

    <!--Sbom version-->
    <el-drawer class="rtl" :title="$t('sbom.project_version')" :visible.sync="versionVisible" size="90%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-card class="table-card">
        <template v-slot:header>
          <version-table-header :title="$t('sbom.project_version_list')" @search="searchVersion"
                        @create="createVersion" :createTip="$t('sbom.version_create')" :show-create="true"/>
        </template>

        <el-table border :data="versionTableData" class="adjust-table table-content" @sort-change="sort"
                  :row-class-name="tableRowClassName" @filter-change="filter">
          <el-table-column type="index" min-width="3%"/>
          <el-table-column prop="name" :label="$t('sbom.version_name')" min-width="15%" show-overflow-tooltip>
            <template v-slot:default="scope">
                <span>
                  <i class="iconfont icon-lianmenglian sbom-icon-2"></i>
                  <span slot="title">{{ $t(scope.row.name) }}</span>
                </span>
            </template>
          </el-table-column>
          <el-table-column prop="description" :label="$t('sbom.version_desc')" min-width="20%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span slot="title">{{ $t(scope.row.description) }}</span>
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
          <el-table-column min-width="20%" :label="$t('commons.operating')" fixed="right">
            <template v-slot:default="scope">
              <table-operators :buttons="versionButtons" :row="scope.row"/>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="searchVersion" :current-page.sync="versionPage" :page-size.sync="versionPageSize" :total="versionTotal"/>
      </el-card>
      <!--Add Sbom version-->
      <div>
        <el-drawer
          size="80%"
          :title="$t('sbom.version_create')"
          :append-to-body="true"
          :before-close="innerVersionClose"
          :visible.sync="innerAddVersion">
          <el-form :model="addVersionForm" label-position="right" label-width="150px" size="small" ref="addVersionForm" :rules="rule">
            <el-form-item :label="$t('sbom.version_name')" ref="name" prop="name">
              <el-input v-model="addVersionForm.name" autocomplete="off" :placeholder="$t('sbom.version_name')"/>
            </el-form-item>
            <el-form-item :label="$t('sbom.version_desc')" ref="name" prop="description">
              <el-input v-model="addVersionForm.description" autocomplete="off" :placeholder="$t('sbom.version_desc')"/>
            </el-form-item>
            <el-form-item>
              <span style="color: red">{{ $t('sbom.sbom_note2') }}</span>
            </el-form-item>
          </el-form>
          <dialog-footer
            @cancel="innerAddVersion = false"
            @confirm="saveVersion()"/>
        </el-drawer>
      </div>
      <!--Add Sbom version-->
      <!--Edit Sbom version-->
      <div>
        <el-drawer
          size="80%"
          :title="$t('sbom.version_update')"
          :append-to-body="true"
          :before-close="innerVersionClose"
          :visible.sync="innerEditVersion">
          <el-form :model="editVersionForm" label-position="right" label-width="150px" size="small" ref="editVersionForm" :rules="rule">
            <el-form-item :label="$t('sbom.version_name')" ref="name" prop="name">
              <el-input v-model="editVersionForm.name" autocomplete="off" :placeholder="$t('sbom.version_name')"/>
            </el-form-item>
            <el-form-item :label="$t('sbom.version_desc')" ref="name" prop="description">
              <el-input v-model="editVersionForm.description" autocomplete="off" :placeholder="$t('sbom.version_desc')"/>
            </el-form-item>
            <el-form-item>
              <span style="color: red">{{ $t('sbom.sbom_note2') }}</span>
            </el-form-item>
          </el-form>
          <dialog-footer
            @cancel="innerEditVersion = false"
            @confirm="editVersion()"/>
        </el-drawer>
      </div>
      <!--Edit Sbom version-->
      <!--Setting Sbom version-->
      <div>
        <el-drawer
          size="80%"
          :title="$t('sbom.sbom_version_setting')"
          :append-to-body="true"
          :before-close="innerVersionClose"
          :visible.sync="innerSettingVersion">
          <el-card class="table-card edit_dev" style="padding: 25px;margin: 25px;">
            <div style="text-align: center; margin: 25px;">
              <p style="text-align: center; padding: 10px;margin: 25px;color: red;background-color: aliceblue;">{{ $t('code.code_scan') }}</p>
              <el-transfer :titles="[$t('sbom.source_code'), $t('sbom.target_code')]" :filter-method="filterMethod" style="text-align: left; display: inline-block"
                           :filter-placeholder="$t('commons.search_by_name')" filterable v-model="codeValue" :data="codeData">
              </el-transfer>
            </div>
            <div style="text-align: center; margin: 25px;">
              <p style="text-align: center; padding: 10px;margin: 25px;color: #893fdc;background-color: aliceblue;">{{ $t('image.image_scan') }}</p>
              <el-transfer :titles="[$t('sbom.source_image'), $t('sbom.target_image')]" :filter-method="filterMethod" style="text-align: left; display: inline-block"
                           :filter-placeholder="$t('commons.search_by_name')" filterable v-model="imageValue" :data="imageData">
              </el-transfer>
            </div>
            <div style="text-align: center; margin: 25px;">
              <p style="text-align: center; padding: 10px;margin: 25px;color: royalblue;background-color: aliceblue;">{{ $t('package.package_scan') }}</p>
              <el-transfer :titles="[$t('sbom.source_package'), $t('sbom.target_package')]" :filter-method="filterMethod" style="text-align: left; display: inline-block"
                           :filter-placeholder="$t('commons.search_by_name')" filterable v-model="packageValue" :data="packageData">
              </el-transfer>
            </div>
          </el-card>
          <dialog-footer
          @cancel="innerSettingVersion = false"
          @confirm="settingVersion()"/>
        </el-drawer>
      </div>
      <!--Setting Sbom version-->
    </el-drawer>
    <!--Sbom version-->

  </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "../head/TableHeader";
import TableOperator from "../../common/components/TableOperator";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {SBOM_CONFIGS} from "../../common/components/search/search-components";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import VersionTableHeader from "../head/VersionTableHeader";

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
    VersionTableHeader,
  },
  data() {
    return {
      result: {},
      condition: {
        components: SBOM_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      versionTableData: [],
      versionPage: 1,
      versionPageSize: 10,
      versionTotal: 0,
      loading: false,
      createVisible: false,
      updateVisible: false,
      versionVisible: false,
      innerAddVersion: false,
      innerEditVersion: false,
      innerSettingVersion: false,
      tmpList: [],
      item: {},
      form: {},
      addForm: {},
      addVersionForm: {},
      editVersionForm: {},
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
        description: [
          {required: true, message: this.$t('commons.input_name'), trigger: 'blur'},
          {min: 2, max: 100, message: this.$t('commons.input_limit', [2, 100]), trigger: 'blur'},
          {
            required: true,
            message: this.$t("workspace.special_characters_are_not_supported"),
            trigger: 'blur'
          }
        ],
      },
      buttons: [
        {
          tip: this.$t('sbom.project_version'), icon: "el-icon-s-grid", type: "success",
          exec: this.handleVersion
        }, {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        }, {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      versionButtons: [
        {
          tip: this.$t('sbom.sbom_scan'), icon: "el-icon-s-promotion", type: "success",
          exec: this.handleScan
        }, {
          tip: this.$t('sbom.sbom_version_setting'), icon: "el-icon-s-flag", type: "warning",
          exec: this.handleSetting
        }, {
          tip: this.$t('sbom.version_update'), icon: "el-icon-edit", type: "primary",
          exec: this.handleVersionEdit
        }, {
          tip: this.$t('sbom.version_delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDeleteVersion
        }
      ],
      sbomId: '',
      sbomVersionId: '',
      codeValue: [],
      codeData: [],
      imageValue: [],
      imageData: [],
      packageValue: [],
      packageData: [],
    }
  },
  watch: {
    '$route': 'init'
  },
  methods: {
    create() {
      this.addForm = {};
      this.createVisible = true;
    },
    createVersion() {
      this.addVersionForm = {};
      this.innerAddVersion = true;
    },
    //查询列表
    search() {
      let url = "/sbom/list/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleEdit(tmp) {
      this.form = tmp;
      this.updateVisible = true;
    },
    handleVersionEdit(tmp) {
      this.editVersionForm = tmp;
      this.innerEditVersion = true;
    },
    handleClose() {
      this.createVisible =  false;
      this.updateVisible =  false;
      this.versionVisible = false;
    },
    handleDelete(obj) {
      this.$alert(this.$t('commons.delete_confirm') + obj.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/sbom/deleteSbom/" + obj.id, () => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    handleDeleteVersion(obj) {
      this.$alert(this.$t('commons.delete_confirm') + obj.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/sbom/deleteSbomVersion/" + obj.id, () => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
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
    //保存项目信息
    save(){
      this.$refs['addForm'].validate(valid => {
        if (valid) {
          this.result = this.$post("/sbom/addSbom", this.addForm, response => {
            if (response.success) {
              this.$success(this.$t('commons.create_success'));
              this.search();
              this.handleClose();
            } else {
              this.$error(response.message);
            }
          });
        }
      });
    },
    //编辑项目信息
    edit(){
      this.$refs['form'].validate(valid => {
        if (valid) {
          this.result = this.$post("/sbom/updateSbom", this.form, response => {
            if (response.success) {
              this.$success(this.$t('commons.update_success'));
              this.search();
              this.handleClose();
            } else {
              this.$error(response.message);
            }
          });
        }
      });
    },
    //保存项目版本信息
    saveVersion(){
      this.$refs['addVersionForm'].validate(valid => {
        if (valid) {
          this.addVersionForm.sbomId = this.sbomId;
          this.result = this.$post("/sbom/addSbomVersion", this.addVersionForm, response => {
            if (response.success) {
              this.$success(this.$t('commons.create_success'));
              this.searchVersion();
              this.innerVersionClose();
            } else {
              this.$error(response.message);
            }
          });
        }
      });
    },
    //编辑项目版本信息
    editVersion(){
      this.$refs['editVersionForm'].validate(valid => {
        if (valid) {
          this.result = this.$post("/sbom/updateSbomVersion", this.editVersionForm, response => {
            if (response.success) {
              this.$success(this.$t('commons.update_success'));
              this.searchVersion();
              this.innerVersionClose();
            } else {
              this.$error(response.message);
            }
          });
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
    searchVersion() {
      let url = "/sbom/sbomVersionList/" + this.versionPage + "/" + this.versionPageSize;
      let params = {sbomId: this.sbomId};
      this.result = this.$post(url, params, response => {
        let data = response.data;
        this.versionTotal = data.itemCount;
        this.versionTableData = data.listObject;
      });
    },
    handleVersion(item) {
      this.sbomId = item.id;
      this.searchVersion();
      this.versionVisible = true;
    },
    handleScan(item) {
      this.$alert(this.$t('image.one_scan') + item.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$get("/sbom/scan/" + item.id,response => {
              if (response.success) {
                this.$success(this.$t('schedule.event_start'));
                this.$router.push({
                  path: '/sbom/sbom',
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
    innerVersionClose() {
      this.innerAddVersion = false;
      this.innerEditVersion = false;
      this.innerSettingVersion = false;
    },
    handleSetting(item) {
      this.sbomVersionId = item.id;
      this.$get("/code/unBindList",response => {
        this.codeData = [];
        for(let data of response.data) {
          this.codeData.push({
            key: data.id,
            label: data.name
          });
        }
      });
      this.$get("/image/unBindList",response => {
        this.imageData = [];
        for(let data of response.data) {
          this.imageData.push({
            key: data.id,
            label: data.name
          });
        }
      });
      this.$get("/package/unBindList",response => {
        this.packageData = [];
        for(let data of response.data) {
          this.packageData.push({
            key: data.id,
            label: data.name
          });
        }
      });
      this.$get("/code/allBindList/" + item.id,response => {
        this.codeValue = [];
        for(let data of response.data) {
          this.codeValue.push(data.id);
        }
      });
      this.$get("/image/allBindList/" + item.id,response => {
        this.imageValue = [];
        for(let data of response.data) {
          this.imageValue.push(data.id);
        }
      });
      this.$get("/package/allBindList/" + item.id,response => {
        this.packageValue = [];
        for(let data of response.data) {
          this.packageValue.push(data.id);
        }
      });
      this.innerSettingVersion = true;
    },
    settingVersion() {
      let params = {
        codeValue: this.codeValue,
        imageValue: this.imageValue,
        packageValue: this.packageValue,
        sbomId: this.sbomId,
        sbomVersionId: this.sbomVersionId,
      };
      this.$post("/sbom/settingVersion", params,response => {
        this.$success(this.$t('organization.integration.successful_operation'));
        this.innerSettingVersion = false;
        this.searchVersion();
      });
    },
    filterMethod(query, item) {
      return item.label.indexOf(query) > -1;
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
.edit_dev >>> .el-transfer-panel {
  width:350px;
}
</style>

