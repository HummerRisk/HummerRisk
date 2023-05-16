<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('reportcenter.report_list')"
                      @create="create" :createTip="$t('reportcenter.report_create')"
                      :show-create="true"
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
        <el-table-column type="selection" id="selection" prop="selection" min-width="50">
        </el-table-column>
        <el-table-column type="index" min-width="40"/>
        <el-table-column prop="name" :label="$t('reportcenter.report_name')" v-if="checkedColumnNames.includes('name')" min-width="180" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <i class="el-icon-document"/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column min-width="160" v-if="checkedColumnNames.includes('downloadNumber')" :label="$t('reportcenter.download_number')" sortable prop="downloadNumber">
        </el-table-column>
        <el-table-column min-width="160" v-if="checkedColumnNames.includes('historyNumber')" :label="$t('reportcenter.history_number')" sortable prop="historyNumber">
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('status')" :label="$t('reportcenter.report_status')" min-width="140" prop="status" sortable show-overflow-tooltip>
          <el-button plain size="mini" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button plain size="mini" type="success" v-else-if="scope.row.resultStatus === 'FINISHED'">
            <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
          </el-button>
          <el-button plain size="mini" type="danger" v-else-if="scope.row.resultStatus === 'ERROR'">
            <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
          </el-button>
          <el-button plain size="mini" type="warning" v-else-if="scope.row.resultStatus === 'WARNING'">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
          </el-button>
          <el-button plain size="mini" type="info" v-else-if="scope.row.resultStatus === null">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_no_warn') }}
          </el-button>
        </el-table-column>
        <el-table-column min-width="160" v-if="checkedColumnNames.includes('createTime')" :label="$t('account.create_time')" sortable
                         prop="createTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="160" v-if="checkedColumnNames.includes('updateTime')" :label="$t('commons.update_time')" sortable
                         prop="updateTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="operator" :label="$t('account.creator')" v-if="checkedColumnNames.includes('operator')" min-width="100" show-overflow-tooltip/>
        <el-table-column min-width="230" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create report-->
    <el-drawer class="rtl" :title="$t('reportcenter.report_create')" :visible.sync="createVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="viewResult.loading">
      <el-card class="table-card" style="margin: 15px;">
        <div style="color: red;text-align: center;padding: 15px 15px 0 15px;">{{ $t('reportcenter.select_account') }}</div>
        <account @nodeSelectEvent="nodeChange" :selectAccounts="selectAccounts"/>
      </el-card>
      <dialog-footer
          @cancel="createVisible = false"
          @confirm="saveAccount()"/>
    </el-drawer>
    <!--Create report-->

  </main-container>
</template>

<script>
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperator from "@/business/components/common/components/TableOperator";
import Container from "@/business/components/common/components/Container";
import MainContainer from "@/business/components/common/components/MainContainer";
import TableOperators from "@/business/components/common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {REPORT_RESULT_CONFIGS} from "@/business/components/common/components/search/search-components";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {deleteFssUrl, deleteFsUrl, getFsResultUrl} from "@/api/k8s/fs/fs";
import {deleteReportsUrl, deleteReportUrl, getReportUrl, reportListUrl} from "@/api/xpack/report";
import Account from "@/business/components/reportcenter/home/Account";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'reportcenter.report_name',
    props: 'name',
    disabled: false
  },
  {
    label: 'reportcenter.download_number',
    props: 'downloadNumber',
    disabled: false
  },
  {
    label: 'reportcenter.history_number',
    props: 'historyNumber',
    disabled: false
  },
  {
    label: 'reportcenter.report_status',
    props: 'status',
    disabled: false
  },
  {
    label: 'commons.create_time',
    props: 'createTime',
    disabled: false
  },
  {
    label: 'commons.update_time',
    props: 'updateTime',
    disabled: false
  },
  {
    label: 'account.creator',
    props: 'operator',
    disabled: false
  },
];

/* eslint-disable */
export default {
  components: {
    MainContainer,
    TableOperators,
    Container,
    TableHeader,
    TablePagination,
    TableOperator,
    DialogFooter,
    HideTable,
    Account,
  },
  provide() {
    return {
      search: this.search,
    }
  },
  data() {
    return {
      result: {},
      viewResult: {},
      condition: {
        components: REPORT_RESULT_CONFIGS
      },
      selectIds: new Set(),
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      createVisible: false,
      updateVisible: false,
      item: {},
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
          tip: this.$t('reportcenter.report_gen'), icon: "el-icon-document", type: "success",
          exec: this.handleGen
        }, {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        }, {
          tip: this.$t('reportcenter.report_download'), icon: "el-icon-bottom", type: "warning",
          exec: this.handleDownload
        }, {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'reportcenter.report_name',
          id: 'name'
        },
        {
          name: 'account.creator',
          id: 'operator'
        },
      ],
      checkAll: true,
      isIndeterminate: false,
      logVisible: false,
      detailVisible: false,
      detailForm: {},
      selectAccounts: [],
      reportResultId: '',
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
      this.selectIds.clear();
      selection.forEach(s => {
        this.selectIds.add(s.id)
      });
    },
    create() {
      this.form = {};
      this.createVisible = true;
    },
    //查询列表
    search() {
      let url = reportListUrl + this.currentPage + "/" + this.pageSize;
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
    handleClose() {
      this.createVisible =  false;
      this.updateVisible =  false;
      this.logVisible=false;
      this.detailVisible=false;
    },
    handleDelete(obj) {
      this.$alert(this.$t('commons.delete_confirm') + obj.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(deleteReportUrl + obj.id, () => {
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
    init(){
      this.search();
    },
    sort(column) {
      _sort(column, this.condition);
      this.search();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.search();
    },
    getStatus () {
      if (this.checkStatus(this.tableData)) {
        this.search();
        clearInterval(this.timer);
      } else {
        for (let data of this.tableData) {
          this.$get(getReportUrl + data.id, response => {
            let result = response.data;
            if (data.status !== result.status) {
              data.status = result.status;
            }
          });
        }
      }
    },
    //是否是结束状态，返回false代表都在运行中，true代表已结束
    checkStatus (tableData) {
      let sum = 0;
      for (let row of tableData) {
        if (row.status && row.status != 'ERROR' && row.status != 'FINISHED' && row.status != 'WARNING') {
          sum++;
        }
      }
      return sum == 0;
    },
    deleteBatch() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('commons.please_select') + this.$t('reportcenter.report_name'));
        return;
      }
      this.$alert(this.$t('oss.delete_batch') + this.$t('reportcenter.report_name') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$request({
              method: 'POST',
              url: deleteReportsUrl,
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
    handleGen (item) {

    },
    nodeChange(node, nodeIds, pNodes) {
      if(node.data.id === "root" || !node.data.id) {
        this.$warning(this.$t('task.task_tree_child'));
        return;
      }
    },
    saveAccount() {
      console.log(1112222, this.selectAccounts);
    },
  },
  created() {
    this.init();
    this.timer = setInterval(this.getStatus, 10000);
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
</style>

