<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search" :showOpen="false"
                      :items="items" :columnNames="columnNames" :showName="false" @delete="deleteBatch" :show-delete="true"
                      :checkedColumnNames="checkedColumnNames" :checkAll="checkAll" :isIndeterminate="isIndeterminate"
                      @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange"
                      @handleCheckAllChange="handleCheckAllChange"/>
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
        <el-table-column prop="name" :label="$t('config.name')" v-if="checkedColumnNames.includes('name')"
                         min-width="150" show-overflow-tooltip>
          <template v-slot:default="scope">
                <span>
                  <img :src="require(`@/assets/img/config/yaml.png`)"
                       style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                   &nbsp;&nbsp; {{ scope.row.name }}
                </span>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')"
                         v-if="checkedColumnNames.includes('returnSum')" prop="returnSum" sortable show-overflow-tooltip
                         min-width="200">
          <el-tooltip effect="dark"
                      :content="$t('history.result') + ' CRITICAL:' + scope.row.critical + ' HIGH:' +  scope.row.high + ' MEDIUM:' + scope.row.medium + ' LOW:' + scope.row.low + ' UNKNOWN:' + scope.row.unknown"
                      placement="top">
            <div class="txt-click" @click="goResource(scope.row)">
              <span style="background-color: #8B0000;color: white;padding: 3px;">{{ 'C:' + scope.row.critical }}</span>
              <span style="background-color: #FF4D4D;color: white;padding: 3px;">{{ 'H:' + scope.row.high }}</span>
              <span style="background-color: #FF8000;color: white;padding: 3px;">{{ 'M:' + scope.row.medium }}</span>
              <span style="background-color: #eeab80;color: white;padding: 3px;">{{ 'L:' + scope.row.low }}</span>
              <span style="background-color: #d5d0d0;color: white;padding: 3px;">{{ 'U:' + scope.row.unknown }}</span>
            </div>
          </el-tooltip>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('image.result_status')"
                         v-if="checkedColumnNames.includes('resultStatus')" min-width="120" prop="resultStatus" sortable
                         show-overflow-tooltip>
          <el-button @click="showResultLog(scope.row)" plain size="mini" type="primary"
                     v-if="scope.row.resultStatus === 'UNCHECKED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="mini" type="primary"
                     v-else-if="scope.row.resultStatus === 'APPROVED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="mini" type="primary"
                     v-else-if="scope.row.resultStatus === 'PROCESSING'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="mini" type="success"
                     v-else-if="scope.row.resultStatus === 'FINISHED'">
            <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="mini" type="danger"
                     v-else-if="scope.row.resultStatus === 'ERROR'">
            <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="mini" type="warning"
                     v-else-if="scope.row.resultStatus === 'WARNING'">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
          </el-button>
        </el-table-column>
        <el-table-column prop="updateTime" min-width="160" v-if="checkedColumnNames.includes('updateTime')"
                         :label="$t('image.last_modified')" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" :label="$t('account.creator')" v-if="checkedColumnNames.includes('userName')"
                         min-width="80" show-overflow-tooltip/>
        <el-table-column :label="$t('commons.operating')" min-width="50" show-overflow-tooltip fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>

    </el-card>

    <!--History statistics-->
    <el-drawer class="rtl" :title="$t('resource.i18n_not_compliance')" :visible.sync="statisticsList" size="85%"
               :before-close="handleClose" :direction="direction" v-loading="viewResult.loading"
               :destroy-on-close="true">
      <div>
        <el-table border :data="statisticsData" class="adjust-table table-content" @sort-change="sort"
                  :row-class-name="tableRowClassName">
          <el-table-column type="index" min-width="2%"/>
          <el-table-column :label="'ID'" min-width="5%" prop="itemId" v-slot:default="scope">
            <span style="font-weight:bold;color: #000000;">{{ scope.row.itemId ? scope.row.itemId : 'N/A' }}</span>
          </el-table-column>
          <el-table-column :label="'Type'" min-width="11%" prop="type" v-slot:default="scope">
            {{ scope.row.type ? scope.row.type : 'N/A' }}
          </el-table-column>
          <el-table-column min-width="7%" :label="'Severity'" prop="severity" v-slot:default="scope">
            <span v-if="scope.row.severity === 'CRITICAL'" style="color: #8B0000;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'HIGH'" style="color: #FF4D4D;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'MEDIUM'" style="color: #FF8000;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'LOW'" style="color: #336D9F;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'UNKNOWN'" style="color: #67C23A;">{{ scope.row.severity }}</span>
          </el-table-column>
          <el-table-column min-width="5%" :label="'Status'" prop="status" v-slot:default="scope">
            {{ scope.row.status ? scope.row.status : 'N/A' }}
          </el-table-column>
          <el-table-column :label="'Title'" min-width="15%" prop="title" v-slot:default="scope">
            {{ scope.row.title ? scope.row.title : 'N/A' }}
          </el-table-column>
          <el-table-column min-width="25%" :label="'PrimaryURL'" prop="primaryUrl" v-slot:default="scope">
            <span>{{ scope.row.resolution }}</span>
            <br>
            <el-link type="primary" style="color: #0000e4;" :href="scope.row.primaryUrl" target="_blank">
              {{ scope.row.primaryUrl }}
            </el-link>
          </el-table-column>
        </el-table>
      </div>
    </el-drawer>
    <!--History statistics-->

    <!--History status-->
    <el-drawer class="rtl" :title="$t('resource.i18n_resource_scanning_log')" :visible.sync="logVisible" size="85%"
               :before-close="handleClose" :direction="direction" v-loading="viewResult.loading"
               :destroy-on-close="true">
      <el-row class="el-form-item-dev" v-if="logData.length == 0">
        <span>{{ $t('resource.i18n_no_data') }}<br></span>
      </el-row>
      <el-row class="el-form-item-dev" v-if="logData.length > 0">
        <div>
          <el-row>
            <el-col :span="24">
              <div class="grid-content bg-purple-light">
                <span class="grid-content-log-span"> {{ logForm.name }}</span>
                <span class="grid-content-log-span">
                  <img :src="require(`@/assets/img/config/yaml.png`)"
                       style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                </span>
                <span class="grid-content-status-span" v-if="logForm.resultStatus === 'APPROVED'"
                      style="color: #579df8">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'FINISHED'"
                      style="color: #7ebf50">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'ERROR'" style="color: red;">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </span>
              </div>
            </el-col>
          </el-row>
        </div>
        <el-table :show-header="false" :data="logData" class="adjust-table table-content">
          <el-table-column>
            <template v-slot:default="scope">
              <div class="bg-purple-div">
                <span
                    v-bind:class="{true: 'color-red', false: ''}[scope.row.result == false]">
                      {{ scope.row.createTime | timestampFormatDate }}
                      {{ scope.row.operator }}
                      {{ scope.row.operation }}
                      {{ scope.row.output }}<br>
                </span>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <log-form :logForm="logForm"/>
      </el-row>
      <template v-slot:footer>
        <dialog-footer
            @cancel="logVisible = false"
            @confirm="logVisible = false"/>
      </template>
    </el-drawer>
    <!--History status-->

    <!--History output list-->
    <el-drawer class="rtl" :title="$t('commons.history')" :visible.sync="visibleList" size="85%"
               :before-close="handleClose" :direction="direction" v-loading="viewResult.loading"
               :destroy-on-close="true">
      <div>
        <el-table border :data="outputListData" class="adjust-table table-content" @sort-change="sort"
                  :row-class-name="tableRowClassName">
          <el-table-column type="index" min-width="40"/>
          <el-table-column prop="name" :label="$t('config.name')" min-width="150" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/config/yaml.png`)"
                     style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="userName" :label="$t('account.creator')" min-width="90" show-overflow-tooltip/>
          <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable
                           show-overflow-tooltip min-width="16%">
            {{ 'C:' + scope.row.critical + ' H:' + scope.row.high + ' M:' + scope.row.medium + ' L:' + scope.row.low + ' U:' + scope.row.unknown }}
          </el-table-column>
          <el-table-column v-slot:default="scope" :label="$t('image.result_status')" min-width="120" prop="resultStatus"
                           sortable show-overflow-tooltip>
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
          </el-table-column>
          <el-table-column prop="updateTime" min-width="160" :label="$t('image.last_modified')" sortable>
            <template v-slot:default="scope">
              <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column min-width="100" :label="$t('commons.operating')" fixed="right" show-overflow-tooltip>
            <template v-slot:default="scope">
              <table-operators :buttons="diffButtons" :row="scope.row"/>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="outputListDataSearch" :current-page.sync="outputListPage"
                          :page-size.sync="outputListPageSize" :total="outputListTotal"/>
      </div>
    </el-drawer>
    <!--History output list-->

    <!--History Compared-->
    <el-dialog :title="$t('dashboard.online_comparison')" width="90%" :visible.sync="innerDrawer"
               :close-on-click-modal="false" v-loading="viewResult.loading">
      <el-form>
        <code-diff
            :old-string="oldStr"
            :new-string="newStr"
            outputFormat="side-by-side"
            :isShowNoChange="true"
            :drawFileList="true"
            :context="10"/>
      </el-form>
    </el-dialog>
    <!--History Compared-->

  </main-container>
</template>

<script>

import {_filter, _sort} from "@/common/js/utils";
import MainContainer from "@/business/components/common/components/MainContainer";
import TableHeader from "@/business/components/common/components/TableHeader";
import TablePagination from "@/business/components/common/pagination/TablePagination";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import TableOperators from "@/business/components/common/components/TableOperators";
import LogForm from "@/business/components/config/home/LogForm";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {CONFIG_RESULT_CONFIGS} from "@/business/components/common/components/search/search-components";
import CodeDiff from 'vue-code-diff';
import {configHistoryUrl, configDeleteHistoryResultUrl, deleteConfigHistoryResultsUrl} from "@/api/system/history";
import {
  configMetricChartUrl,
  getCloudNativeConfigResultUrl,
  historyResultItemListUrl,
  logConfigUrl
} from "@/api/k8s/config/config";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'config.name',
    props: 'name',
    disabled: false
  },
  {
    label: 'resource.i18n_not_compliance',
    props: 'returnSum',
    disabled: false
  },
  {
    label: 'image.result_status',
    props: 'resultStatus',
    disabled: false
  },
  {
    label: 'account.update_time',
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
  name: "HistoryList",
  components: {
    TablePagination,
    DialogFooter,
    TableOperators,
    CodeDiff,
    LogForm,
    MainContainer,
    HideTable,
    TableHeader,
  },
  props: {
    selectNodeIds: Array,
  },
  watch: {
    selectNodeIds() {
      this.search();
    },
  },
  data() {
    return {
      result: {},
      viewResult: {},
      tags: [],
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      condition: {
        components: CONFIG_RESULT_CONFIGS
      },
      direction: 'rtl',
      visibleList: false,
      oldStr: 'old code',
      newStr: 'new code',
      innerDrawer: false,
      script: '',
      buttons: [
        // {
        //   tip: this.$t('resource.resource_result'), icon: "el-icon-s-data", type: "success",
        //   exec: this.handleOpen
        // },
        {
          tip: this.$t('resource.delete_result'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      diffButtons: [
        {
          tip: this.$t('commons.diff'), icon: "el-icon-sort", type: "primary",
          exec: this.imageDiffOpen
        }
      ],
      outputListData: [],
      outputListPage: 1,
      outputListPageSize: 10,
      outputListTotal: 0,
      outputListSearchData: {},
      statisticsList: false,
      logVisible: false,
      statisticsData: [],
      logForm: {},
      logData: [],
      selectIds: new Set(),
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'config.name',
          id: 'name'
        },
        {
          name: 'account.creator',
          id: 'userName'
        }
      ],
      checkAll: true,
      isIndeterminate: false,
    }
  },
  computed: {},
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
    init() {
      this.search();
    },
    //查询列表
    async search() {
      let url = configHistoryUrl + this.currentPage + "/" + this.pageSize;
      if (!!this.selectNodeIds) {
        this.condition.configId = this.selectNodeIds[0];
      } else {
        this.condition.configId = null;
      }
      this.result = await this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    sort(column) {
      _sort(column, this.condition);
      this.init();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.init();
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
    handleOpen(item) {
      this.outputListSearchData = item;
      this.outputListDataSearch();
      this.oldStr = item.resultJson;
      this.visibleList = true;
    },
    handleDelete(obj) {
      this.$alert(this.$t('config.delete_confirm') + this.$t('config.result') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(configDeleteHistoryResultUrl + obj.id, res => {
              setTimeout(function () {
                window.location.reload()
              }, 2000);
              this.$success(this.$t('commons.delete_success'));
            });
          }
        }
      });
    },
    async outputListDataSearch() {
      let item = this.outputListSearchData;
      this.viewResult = await this.$post(configHistoryUrl + this.outputListPage + "/" + this.outputListPageSize, {cloudNativeId: item.cloudNativeId}, response => {
        let data = response.data;
        this.outputListTotal = data.itemCount;
        this.outputListData = data.listObject;
      });
    },
    imageDiffOpen(item) {
      this.innerDrawerComparison(item);
    },
    handleClose() {
      this.visibleList = false;
      this.statisticsList = false;
      this.logVisible = false;
    },
    innerDrawerClose() {
      this.innerDrawer = false;
    },
    innerDrawerComparison(item) {
      this.newStr = item.resultJson ? item.resultJson : "[]";
      this.innerDrawer = true;
    },
    goResource(params) {
      if (params.returnSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      this.viewResult = this.$post(historyResultItemListUrl, {resultId: params.id}, response => {
        let data = response.data;
        this.statisticsData = data;
        this.statisticsList = true;
      });
      this.viewResult = this.$get(configMetricChartUrl + this.resultId, response => {
        this.content = response.data;
      });
    },
    showResultLog(result) {
      this.viewResult = this.$get(logConfigUrl + result.id, response => {
        this.logData = response.data;
      });
      this.viewResult = this.$get(getCloudNativeConfigResultUrl + result.id, response => {
        this.logForm = response.data;
        this.logForm.resultJson = JSON.parse(this.logForm.resultJson);
      });
      this.logVisible = true;
    },
    deleteBatch() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('commons.please_select') + this.$t('config.history'));
        return;
      }
      this.$alert(this.$t('oss.delete_batch') + this.$t('config.history') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$request({
              method: 'POST',
              url: deleteConfigHistoryResultsUrl,
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
  },
}
</script>

<style scoped>
.code-mirror {
  height: 800px !important;
}

.code-mirror >>> .CodeMirror {
  /* Set height, width, borders, and global font properties here */
  height: 800px !important;
}

.el-drawer__header {
  margin-bottom: 0;
}

:focus {
  outline: 0;
}

.rtl >>> .el-drawer__body {
  overflow-y: auto;
  padding: 20px;
}

.bg-purple-dark {
  background: #99a9bf;
}

.bg-purple {
  background: #d3dce6;
}

.bg-purple-light {
  background: #f2f2f2;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}

.el-form-item-dev >>> .el-form-item__content {
  margin-left: 0 !important;
}

.grid-content-log-span {
  width: 39%;
  float: left;
  vertical-align: middle;
  display: table-cell;
  margin: 6px 0 6px 2px;
  color: #606266;
}

.grid-content-status-span {
  width: 20%;
  float: left;
  vertical-align: middle;
  display: table-cell;
  margin: 6px 0;
}

.pure-span {
  color: #606266;
  margin: 10px 0;
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
  padding: 10px 2%;
  width: 46%;
}

.txt-click {
  cursor: pointer;
}

.txt-click:hover {
  color: aliceblue;
  text-shadow: 1px 1px 1px #000;
}
</style>
