<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition"
                      @search="search"  @create="create" :createTip="$t('k8s.sync_log_create')"
                      :title="$t('account.sync_log_list')" :show-create="true"
                      :items="items" :columnNames="columnNames" @delete="deleteBatch" :show-delete="true"
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
        <el-table-column type="selection" id="selection"  prop="selection" min-width="50">
        </el-table-column>
        <el-table-column type="index" min-width="50"/>
        <el-table-column prop="accountName" v-if="checkedColumnNames.includes('accountName')" :label="$t('event.cloud_account_name')" width="200">
          <template v-slot:default="scope">
              <span><img :src="require(`@/assets/img/platform/${ scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                {{ getAccountName(scope.row.accountId) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="resourceTypes" v-if="checkedColumnNames.includes('resourceTypes')" :label="$t('dashboard.resource_type')" min-width="150">
          <template v-slot:default="scope">
            <ResourceType :sync-id="scope.row.id" ></ResourceType>
          </template>
        </el-table-column>
        <el-table-column prop="status" min-width="150" v-if="checkedColumnNames.includes('status')" :label="$t('code.status')">
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
        <el-table-column prop="resourcesSum" v-if="checkedColumnNames.includes('resourcesSum')" :label="$t('event.data_count')" min-width="120"/>
        <el-table-column prop="createTime" v-if="checkedColumnNames.includes('createTime')" :label="$t('k8s.sync_time')" min-width="200" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="150" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create sync-->
    <el-drawer class="rtl" :title="$t('k8s.sync_log_create')" :visible.sync="createVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="viewResult.loading">
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
import HideTable from "@/business/components/common/hideTable/HideTable";
import {
  cloudSyncDeleteUrl,
  cloudSyncLogItemListUrl,
  cloudSyncLogListUrl,
  cloudSyncUrl, syncCloudTasksUrl,
  syncDeleteLogsUrl, syncOssTasksUrl, syncResourceUrl
} from "@/api/cloud/sync/sync";
import {allListUrl} from "@/api/cloud/account/account";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'event.cloud_account_name',
    props: 'accountName',
    disabled: false
  },
  {
    label: 'dashboard.resource_type',
    props: 'resourceTypes',
    disabled: false
  },
  {
    label: 'code.status',
    props: 'status',
    disabled: false
  },
  {
    label: 'event.data_count',
    props: 'resourcesSum',
    disabled: false
  },
  {
    label: 'k8s.sync_time',
    props: 'createTime',
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
    ResultLog,
    ResourceType,
    HideTable,
  },
  data() {
    return {
      logVisible: false,
      credential: {},
      logForm: {cloudTaskItemLogDTOs: []},
      result: {},
      viewResult: {},
      condition: {
        components: SITUATION_LOG_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      selectIds: new Set(),
      createVisible: false,
      form: {},
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
          tip: this.$t('resource.resync'), icon: "el-icon-refresh-right", type: "primary",
          exec: this.reSync
        },
        {
          tip: this.$t('commons.finish'), icon: "el-icon-check", type: "success",
          exec: this.syncResource
        },
        {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      accountList: [],
      //名称搜索
      items: [
        {
          name: 'event.cloud_account_name',
          id: 'accountName'
        },
        {
          name: 'dashboard.resource_type',
          id: 'resourceTypes'
        },
      ],
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      checkAll: true,
      isIndeterminate: false,
    }
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
      this.selectIds.clear();
      selection.forEach(s => {
        this.selectIds.add(s.id)
      });
    },
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
        url = cloudSyncLogItemListUrl;
      }
      this.logForm.cloudTaskItemLogDTOs = [];
      this.logForm.showLogTaskId = showLogTaskId;
      this.$get(url + showLogTaskId, response => {
        this.logForm.cloudTaskItemLogDTOs = response.data;
        this.logVisible = true;
      });
    },
    initAccount() {
      this.$get(allListUrl, response => {
        this.accountList = response.data
        this.search()
      })
    },
    reSync(obj){
      this.$alert( this.$t('resource.resync'), '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(cloudSyncUrl +  obj.accountId,response => {
              this.search();
            });
          }
        }
      });
    },
    syncResource() {
      this.result = this.$get(syncResourceUrl,response => {
        this.search();
      });
      this.$get(syncOssTasksUrl,response => {
      });
      this.$get(syncCloudTasksUrl,response => {
      });
    },
    saveSync() {
      this.viewResult = this.$get(cloudSyncUrl + this.form.id,response => {
        this.search();
        this.handleClose();
      });
    },
    //查询列表
    search() {
      let url = cloudSyncLogListUrl + this.currentPage + "/" + this.pageSize;
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
      this.$alert(this.$t('workspace.delete_confirm'), '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(cloudSyncDeleteUrl + obj.id, () => {
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
    deleteBatch() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('commons.please_select') + this.$t('account.sync_log'));
        return;
      }
      this.$alert(this.$t('oss.delete_batch') + this.$t('account.sync_log') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$request({
              method: 'POST',
              url: syncDeleteLogsUrl,
              data: Array.from(this.selectIds),
              headers: {
                'Content-Type': undefined
              }
            }, res => {
              this.$success(this.$t('commons.success'));
              this.search();
            });
          }
        }
      });
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

