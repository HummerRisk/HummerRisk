<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('k8s.sync_log_list')"
                      @create="create" :createTip="$t('commons.sync')"
                      :show-create="true"/>

      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort"
                :row-class-name="tableRowClassName" @filter-change="filter">
        <el-table-column type="index" min-width="2%"/>
        <el-table-column prop="operation" :label="$t('k8s.sync_log')" min-width="15%"/>
        <el-table-column prop="operator" :label="$t('resource.creator')" min-width="15%"/>
        <el-table-column prop="status" min-width="10%" :label="$t('code.status')">
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
        <el-table-column prop="createTime" :label="$t('k8s.sync_time')" min-width="20%" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="12%" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create sync-->
    <el-drawer class="rtl" :title="$t('k8s.sync_log_create')" :visible.sync="createVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form">
        <el-form-item :label="$t('k8s.platform')" :rules="{required: true, message: $t('k8s.platform') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" filterable :clearable="true" v-model="form.id" :placeholder="$t('k8s.please_choose_k8s')">
            <el-option
              v-for="item in k8s"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              <img :src="require(`@/assets/img/platform/${item.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
              &nbsp;&nbsp; {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="createVisible = false"
          @confirm="saveSync"/>
      </div>
    </el-drawer>
    <!--Create sync-->

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
import {SITUATION_LOG_CONFIGS} from "../../common/components/search/search-components";
import DialogFooter from "@/business/components/common/components/DialogFooter";

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
  },
  data() {
    return {
      credential: {},
      result: {},
      condition: {
        components: SITUATION_LOG_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      createVisible: false,
      form: {},
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
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      k8s: [],
    }
  },
  methods: {
    create() {
      this.form = {};
      this.createVisible = true;
    },
    initK8s() {
      this.result = this.$get("/k8s/allCloudNativeList",response => {
        this.k8s = response.data;
      });
    },
    saveSync() {
      this.result = this.$get("/k8s/syncSource/" + this.form.id,response => {
        this.$success(this.$t('k8s.notes'));
        this.search();
        this.handleClose();
      });
    },
    //查询列表
    search() {
      let url = "/k8s/syncList/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleClose() {
      this.createVisible =  false;
    },
    handleDelete(obj) {
      this.$alert(this.$t('commons.delete_confirm') + this.$t('k8s.sync_log') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/k8s/deleteSyncLog/" + obj.id, () => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    init() {
      this.search();
      this.initK8s();
    },
    sort(column) {
      _sort(column, this.condition);
      this.search();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.search();
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
  },
  created() {
    this.init();
    this.timer = setInterval(this.search,60000);
  },
  beforeDestroy() {
    clearInterval(this.timer);
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

