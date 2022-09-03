<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search" @create="create"
                      :create-tip="$t('image.repo_create')" :title="$t('image.image_repo_list')"/>
      </template>

      <el-table border class="adjust-table" :data="tableData" style="width: 100%" @sort-change="sort" @filter-change="filter"
                :row-class-name="tableRowClassName">
        <el-table-column type="index" min-width="3%"/>
        <el-table-column prop="name" :label="$t('image.image_repo_name')" min-width="18%"/>
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
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" :label="$t('commons.update_time')" min-width="15%" sortable>
          <template v-slot:default="scope">
            <span><i class="el-icon-time"></i> {{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="13%" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
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
              &nbsp;&nbsp; {{ $t(item.id) }}
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
          <el-select style="width: 100%;" disabled v-model="form.pluginIcon" :placeholder="$t('image.image_repo_type')">
            <el-option
              v-for="item in plugins"
              :key="item.id"
              :label="item.id"
              :value="item.id">
              <img :src="require(`@/assets/img/repo/${item.value}`)" style="width: 20px; height: 16px; vertical-align:middle" alt=""/>
              &nbsp;&nbsp; {{ $t(item.id) }}
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
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="updateVisible = false"
          @confirm="editRepo('form')"/>
      </div>
    </el-drawer>
    <!--Update imageRepo-->

    <!--Create imageRepo-->
    <el-drawer class="rtl" :title="$t('image.image_list')" :visible.sync="imageVisible" size="80%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-table border :data="imageData" class="adjust-table table-content">
        <el-table-column type="index" min-width="2%"/>
        <el-table-column prop="project" :label="'Project'" min-width="7%">
        </el-table-column>
        <el-table-column prop="repository" :label="'Repository'" min-width="15%">
        </el-table-column>
        <el-table-column prop="path" :label="'ImagePath'" min-width="30%">
        </el-table-column>
        <el-table-column min-width="9%" :label="'Size'" prop="size">
        </el-table-column>
        <el-table-column min-width="9%" :label="'Arch'" prop="arch">
        </el-table-column>
        <el-table-column min-width="15%" :label="'PushTime'" prop="pushTime">
        </el-table-column>
      </el-table>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="imageVisible = false"
          @confirm="imageVisible = false"/>
      </div>
    </el-drawer>
    <!--Create imageRepo-->

    <!--Sync image-->
    <el-drawer class="rtl" :title="$t('image.image_sync_for_repo')" :visible.sync="syncVisible" size="80%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
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
import SyncTableHeader from "../head/SyncTableHeader";
import TableOperators from "@/business/components/common/components/TableOperators";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import ImageStatus from "../head/ImageStatus";
import {_filter, _sort} from "@/common/js/utils";
import MainContainer from "../.././common/components/MainContainer";
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
          tip: this.$t('image.image_sync'), icon: "el-icon-refresh", type: "warning",
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
      statusFilters: [
        {text: this.$t('server.INVALID'), value: 'INVALID'},
        {text: this.$t('server.VALID'), value: 'VALID'},
        {text: this.$t('server.DELETE'), value: 'DELETE'}
      ],
      plugins: [
        {value: 'harbor.png', id: "Harbor"},
        // {value: 'dockerhub.png', id: "DockerHub"},
        // {value: 'nexus.png', id: "Nexus"},
        {value: 'other.png', id: "Other"},
      ],
      imageData: [],
      syncData: [],
      syncVisible: false,
      repoId: "",
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
    }
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
