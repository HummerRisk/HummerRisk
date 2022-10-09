<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('account.sync_log_list')"
                      @create="create" :createTip="$t('k8s.sync_log_create')"
                      :show-create="true"/>

      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort"
                :row-class-name="tableRowClassName" @filter-change="filter">
        <el-table-column type="index" min-width="2%"/>
        <el-table-column prop="accountId" :label="$t('event.cloud_account_name')" min-width="15%">
          <template v-slot:default="scope">
              <span><img :src="require(`@/assets/img/platform/${ scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                {{ getAccountName(scope.row.accountId) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="resourceTypes" :label="$t('dashboard.resource_type')" min-width="10%">
          <template v-slot:default="scope">
            <ResourceType :resourceTypes="scope.row.resourceTypes" ></ResourceType>
          </template>
        </el-table-column>
        <el-table-column prop="status" min-width="10%" :label="$t('code.status')">
          <template v-slot:default="scope">
            <el-button @click="showTaskLog(scope.row)" plain size="mini" type="primary"
                       v-if="scope.row.status === 'UNCHECKED'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="mini" type="primary"
                       v-else-if="scope.row.status === 'APPROVED'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="mini" type="primary"
                       v-else-if="scope.row.status === 'RUNNING'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="mini" type="primary"
                       v-else-if="scope.row.status === 'PROCESSING'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="mini" type="success"
                       v-else-if="scope.row.status === 'FINISHED'">
              <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="mini" type="danger"
                       v-else-if="scope.row.status === 'ERROR'">
              <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="mini" type="warning"
                       v-else-if="scope.row.status === 'WARNING'">
              <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="resourcesSum" :label="$t('event.data_count')" min-width="10%"/>
        <el-table-column prop="createTime" :label="$t('k8s.sync_time')" min-width="15%" sortable>
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
        <el-form-item :label="$t('event.cloud_account')" :rules="{required: true, message: $t('event.cloud_account') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" filterable :clearable="true" v-model="form.id" :placeholder="$t('event.cloud_account')">
            <el-option
              v-for="item in accountList"
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

    <!--Task log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="65%"
               :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <result-log :row="logForm"></result-log>
      <template v-slot:footer>
        <dialog-footer
          @cancel="logVisible = false"
          @confirm="logVisible = false"/>
      </template>
    </el-drawer>
    <!--Task log-->

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
import ResultLog from "./ResultLog";
import ResourceType from "./ResourceType";
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
    ResultLog,
    ResourceType
  },
  data() {
    return {
      logVisible: false,
      credential: {},
      logForm: {cloudTaskItemLogDTOs: []},
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
          tip: this.$t('resource.resync'), icon: "el-icon-s-promotion", type: "success",
          exec: this.reSync
        },
        {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      k8s: [],
      accountList: []
    }
  },
  methods: {
    create() {
      this.form = {};
      this.createVisible = true;
    },
    getAccountName(accountId){
      let result =  this.accountList.filter(item =>{
        return item.id === accountId
      })
      return result.length >0?result[0].name:""
    },
    showTaskLog(task) {
      let showLogTaskId = task.id;
      let url = "";
      if (showLogTaskId) {
        url = "/cloud/sync/log/item/list/";
      }
      this.logForm.cloudTaskItemLogDTOs = [];
      this.logForm.showLogTaskId = showLogTaskId;
      this.$get(url + showLogTaskId, response => {
        this.logForm.cloudTaskItemLogDTOs = response.data;
        this.logVisible = true;
      });
    },
    initAccount() {
      this.$get("/account/allList", response => {
        this.accountList = response.data
        this.search()
      })
    },
    reSync(obj){
      this.$alert( this.$t('resource.resync'), '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/cloud/sync/sync/" +  obj.accountId,response => {
              this.search();
            });
          }
        }
      });
    },
    saveSync() {
      this.result = this.$get("/cloud/sync/sync/" + this.form.id,response => {
        this.search();
        this.handleClose();
      });
    },
    //查询列表
    search() {
      let url = "/cloud/sync/log/list/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleClose() {
      this.logVisible =  false;
      this.createVisible = false;
    },
    handleDelete(obj) {
      this.$alert(this.$t('workspace.delete_confirm') + this.$t('resource.sync_log') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/cloud/sync/delete/" + obj.id, () => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    init() {
      this.search();
      this.initAccount();
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

