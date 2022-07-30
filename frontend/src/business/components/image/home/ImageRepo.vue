<template>
  <div v-loading="result.loading">

    <el-card class="table-card">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search" @create="create"
                      :create-tip="$t('image.repo_create')" :title="$t('image.image_repo_list')"/>
      </template>

      <el-table border class="adjust-table" :data="tableData" style="width: 100%" @sort-change="sort" @filter-change="filter"
                :row-class-name="tableRowClassName">
        <el-table-column type="index" min-width="3%"/>
        <el-table-column prop="name" :label="$t('image.image_repo_name')" min-width="15%"/>
        <el-table-column prop="repo" :label="$t('image.image_repo_url')" min-width="15%"/>
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
        <el-table-column min-width="15%" :label="$t('commons.operating')" fixed="right">
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
        <el-form-item :label="$t('image.image_repo_url')" ref="repo" prop="repo">
          <el-input v-model="form.repo" autocomplete="off" :placeholder="$t('image.image_repo_url')"/>
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
        <el-form-item :label="$t('image.image_repo_url')" ref="repo" prop="repo">
          <el-input v-model="form.repo" autocomplete="off" :placeholder="$t('image.image_repo_url')"/>
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

  </div>
</template>

<script>
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperators from "@/business/components/common/components/TableOperators";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import ImageStatus from "../head/ImageStatus";
import {_filter, _sort} from "@/common/js/utils";
/* eslint-disable */
export default {
  name: "ImageRepo",
  components: {
    TablePagination,
    TableHeader,
    DialogFooter,
    ImageStatus,
    TableOperators,
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
      editPasswordVisible: false,
      btnAddRole: false,
      multipleSelection: [],
      userRole: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      condition: {},
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
        console.log(this.tableData)
      });
    },
    handleClose() {
      this.form = {};
      this.createVisible =  false;
      this.updateVisible =  false;
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
