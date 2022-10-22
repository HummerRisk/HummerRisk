<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search" @create="create"
                      :create-tip="$t('image.repo_create')" :title="$t('image.image_repo_list')" :show-create="true"/>
      </template>

      <el-table border class="adjust-table" :data="tableData" style="width: 100%" @sort-change="sort" @filter-change="filter"
                :row-class-name="tableRowClassName">
        <el-table-column type="index" min-width="3%"/>
        <el-table-column prop="name" :label="$t('image.image_repo_name')" min-width="18%">
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/repo/${scope.row.pluginIcon}`)" style="width: 30px; height: 25px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="repo" :label="$t('image.image_repo_url')" min-width="18%"/>
        <el-table-column prop="userName" :label="$t('image.image_repo_user_name')" min-width="12%"/>
        <el-table-column prop="status" min-width="10%" :label="$t('image.image_repo_status')"
                         column-key="status"
                         :filters="statusFilters"
                         :filter-method="filterStatus">
          <template v-slot:default="{row}">
            <image-status :row="row"/>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('commons.create_time')" min-width="15%" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="15%" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators v-if="scope.row.pluginIcon !== 'other.png'" :buttons="buttons" :row="scope.row"/>
            <table-operators v-if="scope.row.pluginIcon === 'other.png'" :buttons="buttons2" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>

      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create imageRepo-->
    <el-drawer class="rtl" :title="$t('image.repo_create')" :visible.sync="createVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form">
        <el-form-item :label="$t('image.image_repo_name')" ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('image.image_repo_name')"/>
        </el-form-item>
        <el-form-item :label="$t('image.image_repo_type')" :rules="{required: true, message: $t('image.image_repo_type') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" filterable :clearable="true" v-model="form.pluginIcon" :placeholder="$t('image.image_repo_type')">
            <el-option
              v-for="item in plugins"
              :key="item.value"
              :label="item.id"
              :value="item.value">
              <img :src="require(`@/assets/img/repo/${item.value}`)" style="width: 20px; height: 16px; vertical-align:middle" alt=""/>
              &nbsp;&nbsp; {{ item.id }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('image.image_repo_url')" ref="repo" prop="repo">
          <el-input v-model="form.repo" autocomplete="off" :placeholder="$t('image.image_repo_url_desc')"/>
        </el-form-item>
        <el-form-item :label="$t('image.image_repo_user_name')" ref="userName" prop="userName">
          <el-input v-model="form.userName" autocomplete="off" :placeholder="$t('image.image_repo_user_name')"/>
        </el-form-item>
        <el-form-item :label="$t('image.image_repo_password')" ref="password" prop="password">
          <el-input v-model="form.password" autocomplete="off" :placeholder="$t('image.image_repo_password')" show-password/>
        </el-form-item>
      </el-form>
      <span style="color: red;"><I>{{ $t('image.image_repo_note') }}</I></span>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="createVisible = false"
          @confirm="saveRepo('form')"/>
      </div>
    </el-drawer>
    <!--Create imageRepo-->

    <!--Update imageRepo-->
    <el-drawer class="rtl" :title="$t('image.repo_update')" :visible.sync="updateVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form">
        <el-form-item :label="$t('image.image_repo_name')" ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('image.image_repo_name')"/>
        </el-form-item>
        <el-form-item :label="$t('image.image_repo_type')" :rules="{required: true, message: $t('image.image_repo_type') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" v-model="form.pluginIcon" :placeholder="$t('image.image_repo_type')">
            <el-option
              v-for="item in plugins"
              :key="item.value"
              :label="item.id"
              :value="item.value">
              <img :src="require(`@/assets/img/repo/${item.value}`)" style="width: 20px; height: 16px; vertical-align:middle" alt=""/>
              &nbsp;&nbsp; {{ item.id }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('image.image_repo_url')" ref="repo" prop="repo">
          <el-input v-model="form.repo" autocomplete="off" :placeholder="$t('image.image_repo_url_desc')"/>
        </el-form-item>
        <el-form-item :label="$t('image.image_repo_user_name')" ref="userName" prop="userName">
          <el-input v-model="form.userName" autocomplete="off" :placeholder="$t('image.image_repo_user_name')"/>
        </el-form-item>
        <el-form-item :label="$t('image.image_repo_password')" ref="password" prop="password">
          <el-input v-model="form.password" autocomplete="off" :placeholder="$t('image.image_repo_password')" show-password/>
        </el-form-item>
      </el-form>
      <span style="color: red;"><I>{{ $t('image.image_repo_note') }}</I></span>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="updateVisible = false"
          @confirm="editRepo('form')"/>
      </div>
    </el-drawer>
    <!--Update imageRepo-->

    <!--Image list-->
    <el-drawer class="rtl" :title="$t('image.image_list')" :visible.sync="imageVisible" size="90%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <span style="color: red;"><I>{{ $t('image.image_repo_note') }}</I></span>
      <el-table border :data="imageData" class="adjust-table table-content">
        <el-table-column type="index" min-width="2%"/>
        <el-table-column prop="project" :label="'Project'" min-width="10%" v-slot:default="scope">
          {{ scope.row.project?scope.row.project:'N/A' }}
        </el-table-column>
        <el-table-column prop="repository" :label="'Repository'" min-width="15%">
        </el-table-column>
        <el-table-column prop="path" :label="'ImagePath'" min-width="25%">
        </el-table-column>
        <el-table-column min-width="9%" :label="'Size'" prop="size" v-slot:default="scope">
          {{ scope.row.size?scope.row.size:'--' }}
        </el-table-column>
        <el-table-column min-width="9%" :label="'Arch'" prop="arch" v-slot:default="scope">
          {{ scope.row.arch?scope.row.arch:'--' }}
        </el-table-column>
        <el-table-column min-width="15%" :label="'PushTime'" prop="pushTime" v-slot:default="scope">
          {{ scope.row.pushTime?scope.row.pushTime:'--' }}
        </el-table-column>
        <el-table-column min-width="10%" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons3" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>
      <div>
        <el-drawer
          class="rtl"
          size="60%"
          :title="$t('k8s.execute_scan')"
          :append-to-body="true"
          :before-close="innerClose"
          :visible.sync="innerAdd">
          <el-form :model="addForm" label-position="right" label-width="150px" size="small" ref="addForm" :rules="rule">
            <el-form-item :label="$t('sbom.sbom_project')" :rules="{required: true, message: $t('sbom.sbom_project') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="addForm.sbomId" :placeholder="$t('sbom.sbom_project')" @change="changeSbom(addForm)">
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
              <el-select style="width: 100%;" filterable :clearable="true" v-model="addForm.sbomVersionId" :placeholder="$t('sbom.sbom_project_version')">
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
            <el-form-item :label="$t('image.image_name')" ref="name" prop="name">
              <el-input v-model="addForm.name" autocomplete="off" :placeholder="$t('image.image_name')"/>
            </el-form-item>
            <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-switch v-model="addForm.isProxy"></el-switch>
            </el-form-item>
            <el-form-item v-if="addForm.isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="addForm.proxyId" :placeholder="$t('commons.proxy')">
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
            @cancel="innerAdd = false"
            @confirm="saveAdd()"/>
        </el-drawer>
      </div>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="imageVisible = false"
          @confirm="imageVisible = false"/>
      </div>
    </el-drawer>
    <!--Image list-->

    <!--Sync image-->
    <el-drawer class="rtl" :title="$t('image.image_sync_for_repo')" :visible.sync="syncVisible" size="90%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <span style="color: red;"><I>{{ $t('image.image_repo_note') }}</I></span>
      <sync-table-header @sync="sync" :sync-tip="$t('image.image_sync')" :title="$t('image.image_sync_log')" style="margin: 0 0 15px 0;"/>
      <el-table border :data="syncData" class="adjust-table table-content">
        <el-table-column type="index" min-width="2%"/>
        <el-table-column prop="operation" :label="$t('image.image_sync')" min-width="15%"/>
        <el-table-column prop="operator" :label="$t('resource.creator')" min-width="15%"/>
        <el-table-column prop="result" min-width="15%" :label="$t('image.image_repo_status')">
          <template v-slot:default="scope">
            <el-tooltip class="item" effect="dark" :content="scope.row.output" placement="top">
              <el-tag size="mini" type="success" v-if="scope.row.result">
                {{ $t('commons.success') }}
              </el-tag>
              <el-tag size="mini" type="danger" v-else-if="!scope.row.result">
                {{ $t('commons.error') }}
              </el-tag>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="sum" :label="$t('resource.i18n_not_compliance')" min-width="12%"/>
        <el-table-column prop="createTime" :label="$t('commons.create_time')" min-width="20%" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="syncVisible = false"
          @confirm="syncVisible = false"/>
      </div>
    </el-drawer>
    <!--Sync image-->
  </main-container>
</template>

<script>
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperators from "@/business/components/common/components/TableOperators";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import ImageStatus from "../head/ImageStatus";
import {_filter, _sort} from "@/common/js/utils";
import MainContainer from "../.././common/components/MainContainer";
import SyncTableHeader from "@/business/components/image/head/SyncTableHeader";
import {IMAGE_REPO_CONFIGS} from "@/business/components/common/components/search/search-components";
/* eslint-disable */
export default {
  name: "ImageRepo",
  components: {
    TablePagination,
    TableHeader,
    DialogFooter,
    ImageStatus,
    TableOperators,
    MainContainer,
    SyncTableHeader,
  },
  data() {
    return {
      queryPath: '/image/imageRepoList/',
      deletePath: '/image/deleteImageRepo/',
      createPath: '/image/addImageRepo/',
      updatePath: '/image/editImageRepo/',
      scanPath: '/image/scanImageRepo/',
      result: {},
      createVisible: false,
      updateVisible: false,
      imageVisible: false,
      editPasswordVisible: false,
      btnAddRole: false,
      multipleSelection: [],
      userRole: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      condition: {
        components: IMAGE_REPO_CONFIGS
      },
      tableData: [],
      form: {},
      direction: 'rtl',
      rule: {
        name: [
          {required: true, message: this.$t('commons.input_name'), trigger: 'blur'},
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
          {
            required: true,
            message: this.$t('workspace.special_characters_are_not_supported'),
            trigger: 'blur'
          }
        ],
      },
      buttons: [
        {
          tip: this.$t('image.image_sync'), icon: "el-icon-sort-down", type: "warning",
          exec: this.handleSync
        },
        {
          tip: this.$t('image.image_list'), icon: "el-icon-more", type: "success",
          exec: this.handleList
        },
        {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        }, {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      buttons2: [
        {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        }, {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      buttons3: [
        {
          tip: this.$t('k8s.execute_scan'), icon: "el-icon-s-promotion", type: "success",
          exec: this.handleScan
        }
      ],
      statusFilters: [
        {text: this.$t('server.INVALID'), value: 'INVALID'},
        {text: this.$t('server.VALID'), value: 'VALID'},
        {text: this.$t('server.DELETE'), value: 'DELETE'}
      ],
      plugins: [
        {value: 'harbor.png', id: "Harbor"},
        {value: 'dockerhub.png', id: "DockerHub"},
        {value: 'nexus.png', id: "Nexus"},
        {value: 'other.png', id: "Other"},
      ],
      imageData: [],
      syncData: [],
      syncVisible: false,
      repoId: "",
      innerAdd: false,
      addForm: {},
      sboms: [],
      versions: [],
      proxys: [],
    }
  },
  methods: {
    create() {
      this.form = {};
      this.createVisible = true;
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
    handleEdit(row) {
      this.updateVisible = true;
      this.form = row;
    },
    search() {
      this.result = this.$post(this.buildPagePath(this.queryPath), this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleClose() {
      this.form = {};
      this.createVisible =  false;
      this.updateVisible =  false;
      this.imageVisible = false;
      this.syncVisible = false;
    },
    buildPagePath(path) {
      return path + this.currentPage + "/" + this.pageSize;
    },
    tableRowClassName({row, rowIndex}) {
      if (rowIndex%4 === 0) {
        return 'success-row';
      } else if (rowIndex%2 === 0) {
        return 'warning-row';
      } else {
        return '';
      }
    },
    editRepo(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          this.result = this.$post(this.updatePath, this.form, () => {
            this.$success(this.$t('commons.save_success'));
            this.search();
            this.updateVisible = false;
          });
        } else {
          return false;
        }
      });
    },
    saveRepo(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          this.result = this.$post(this.createPath, this.form, () => {
            this.$success(this.$t('commons.save_success'));
            this.search();
            this.createVisible = false;
          });
        } else {
          return false;
        }
      });
    },
    handleDelete(obj) {
      this.$alert(this.$t('workspace.delete_confirm') + obj.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(this.deletePath + obj.id, response => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    handleList(item) {
      this.$get("/image/repoItemList/" + item.id, response => {
        this.imageData = response.data;
        this.imageVisible = true;
      });
    },
    handleSync(item) {
      this.repoId = item.id;
      this.$get("/image/repoSyncList/" + item.id, response => {
        this.syncData = response.data;
        this.syncVisible = true;
      });
    },
    sync() {
      this.$get("/image/syncImage/" + this.repoId, response => {
        this.$success(this.$t('commons.success'));
        this.$get("/image/repoSyncList/" + this.repoId, response => {
          this.syncData = response.data;
        });
      });
    },
    innerClose() {
      this.innerAdd = false;
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
    handleScan(item) {
      this.initSboms();
      this.activeProxy();
      this.addForm = item;
      this.addForm.name = item.path;
      this.innerAdd = true;
    },
    saveAdd() {
      this.$refs['addForm'].validate(valid => {
        if (valid) {
          this.result = this.$post(this.scanPath, this.addForm, response => {
            if (response.success) {
              this.$success(this.$t('schedule.event_start'));
              this.innerAdd = false;
              this.imageVisible = false;
              this.$router.push({
                path: '/image/result',
                query: {
                  date: new Date().getTime()
                },
              }).catch(error => error);
            } else {
              this.$error(this.$t('schedule.event_failed'));
            }
          });
        }
      });
    },
  },
  created() {
    this.search();
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
</style>
