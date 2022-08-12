<template>
  <el-row :gutter="24">
    <el-col :span="24">
      <el-card class="box-card" shadow="always">
        <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                  @filter-change="filter" @select-all="select" @select="select">
          <el-table-column type="index" min-width="5%"/>
          <el-table-column :label="$t('vuln.vuln_setting')" min-width="20%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <el-row type="primary">
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                {{ scope.row.accountName }}
              </el-row>
            </template>
          </el-table-column>
          <el-table-column :label="$t('history.scan_score')" min-width="15%" show-overflow-tooltip>
            <template v-slot:default="scope">
              {{ scope.row.scanScore }}
            </template>
          </el-table-column>
          <el-table-column :label="$t('history.resource_result')" min-width="20%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span> {{ scope.row.returnSum?scope.row.returnSum:0 }}/{{ scope.row.resourcesSum?scope.row.resourcesSum:0 }}</span>
              <span> &nbsp;&nbsp;<i :class="scope.row.assets" ></i></span>
            </template>
          </el-table-column>
          <el-table-column min-width="20%" :label="$t('history.create_time')" sortable
                           prop="createTime">
            <template v-slot:default="scope">
              <span><i class="el-icon-time"></i> {{ scope.row.createTime | timestampFormatDayDate }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('resource.resource_result')" min-width="20%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>

      </el-card>

      <!--History output list-->
      <el-drawer class="rtl" :title="$t('vuln.history')" :visible.sync="visibleList" size="85%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div>
          <el-table border :data="outputListData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName">
            <el-table-column type="index" min-width="2%"/>
            <el-table-column prop="name" :label="$t('vuln.name')" min-width="15%" show-overflow-tooltip>
              <template v-slot:default="scope">
                <span>
                  <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                   &nbsp;&nbsp; {{ $t(scope.row.resourceName) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('rule.severity')" min-width="15%" :sort-by="['HighRisk', 'MediumRisk', 'LowRisk']" prop="severity" :sortable="true"  show-overflow-tooltip>
              <span v-if="scope.row.severity == 'HighRisk'" style="color: #f84846;"> {{ $t('rule.HighRisk') }}</span>
              <span v-else-if="scope.row.severity == 'MediumRisk'" style="color: #fe9636;"> {{ $t('rule.MediumRisk') }}</span>
              <span v-else-if="scope.row.severity == 'LowRisk'" style="color: #4dabef;"> {{ $t('rule.LowRisk') }}</span>
              <span v-else> N/A</span>
            </el-table-column>
            <el-table-column :label="$t('resource.status')" min-width="15%" prop="resourceStatus" show-overflow-tooltip>
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="10%">
              <el-tooltip class="item" effect="dark" :content="$t('history.resource_result')" placement="top">
                <span v-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
                <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum == 0)">
                  {{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}
                </span>
                <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)">
                  {{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}
                </span>
              </el-tooltip>
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('resource.status_on_off')" prop="returnSum" sortable show-overflow-tooltip min-width="10%">
              <span v-if="scope.row.returnSum == 0" style="color: #46ad59;">{{ $t('resource.i18n_compliance_true') }}</span>
              <span v-else-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)" style="color: #f84846;">{{ $t('resource.i18n_compliance_false') }}</span>
              <span v-else-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
            </el-table-column>
            <el-table-column prop="createTime" min-width="15%" :label="$t('account.create_time')" sortable show-overflow-tooltip>
              <template v-slot:default="scope">
                <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
              </template>
            </el-table-column>
            <el-table-column min-width="17%" :label="$t('commons.operating')" fixed="right" show-overflow-tooltip>
              <template v-slot:default="scope">
                <table-operators :buttons="listButtons" :row="scope.row"/>
              </template>
            </el-table-column>
          </el-table>
          <table-pagination :change="outputListDataSearch" :current-page.sync="outputListPage" :page-size.sync="outputListPageSize" :total="outputListTotal"/>
        </div>
        <!--History output-->
        <el-drawer class="rtl"
                   :title="$t('vuln.history')"
                   :visible.sync="visible"
                   size="60%"
                   :append-to-body="true"
                   :before-close="innerDrawerClose">
          <el-form label-position="right">
            <el-form-item style="margin: 1%;">
              <codemirror ref="cmEditor" v-model="script" class="code-mirror" :options="cmOptions" />
            </el-form-item>
          </el-form>
        </el-drawer>
        <!--History output-->
        <!--History result-->
        <el-drawer class="rtl"
                   :title="$t('vuln.history')"
                   :visible.sync="diffVisible"
                   size="80%"
                   :append-to-body="true"
                   :before-close="innerDrawerClose">
          <el-table border :data="historys" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName">
            <el-table-column type="index" min-width="2%"/>
            <el-table-column prop="name" :label="$t('vuln.name')" min-width="15%" show-overflow-tooltip>
              <template v-slot:default="scope">
                <span>
                  <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                   &nbsp;&nbsp; {{ $t(scope.row.resourceName) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('rule.severity')" min-width="15%" :sort-by="['HighRisk', 'MediumRisk', 'LowRisk']" prop="severity" :sortable="true"  show-overflow-tooltip>
              <span v-if="scope.row.severity == 'HighRisk'" style="color: #f84846;"> {{ $t('rule.HighRisk') }}</span>
              <span v-else-if="scope.row.severity == 'MediumRisk'" style="color: #fe9636;"> {{ $t('rule.MediumRisk') }}</span>
              <span v-else-if="scope.row.severity == 'LowRisk'" style="color: #4dabef;"> {{ $t('rule.LowRisk') }}</span>
              <span v-else> N/A</span>
            </el-table-column>
            <el-table-column :label="$t('resource.status')" min-width="15%" prop="resourceStatus" show-overflow-tooltip>
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="10%">
              <el-tooltip class="item" effect="dark" :content="$t('history.resource_result')" placement="top">
                <span v-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
                <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum == 0)">
                  {{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}
                </span>
                <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)">
                  {{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}
                </span>
              </el-tooltip>
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('resource.status_on_off')" prop="returnSum" sortable show-overflow-tooltip min-width="10%">
              <span v-if="scope.row.returnSum == 0" style="color: #46ad59;">{{ $t('resource.i18n_compliance_true') }}</span>
              <span v-else-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)" style="color: #f84846;">{{ $t('resource.i18n_compliance_false') }}</span>
              <span v-else-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
            </el-table-column>
            <el-table-column prop="createTime" min-width="15%" :label="$t('account.create_time')" sortable show-overflow-tooltip>
              <template v-slot:default="scope">
                <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
              </template>
            </el-table-column>
            <el-table-column min-width="17%" :label="$t('commons.operating')" fixed="right" show-overflow-tooltip>
              <template v-slot:default="scope">
                <table-operators :buttons="diffButtons" :row="scope.row"/>
              </template>
            </el-table-column>
          </el-table>
          <table-pagination :change="codeDiffListSearch" :current-page.sync="historyPage" :page-size.sync="historyPageSize" :total="historyTotal"/>
        </el-drawer>
        <!--History result-->
        <dialog-footer
          @cancel="visibleList = false"
          @confirm="handleClose"/>
      </el-drawer>
      <!--History output list-->

      <!--History Compared-->
      <el-dialog :title="$t('dashboard.online_comparison')" width="80%" :visible.sync="innerDrawer" :close-on-click-modal="false">
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

    </el-col>
  </el-row>
</template>

<script>

import {_filter, _sort} from "@/common/js/utils";
import TablePagination from "../../common/pagination/TablePagination";
import DialogFooter from "../../common/components/DialogFooter";
import TableOperators from "../../common/components/TableOperators";
import CodeDiff from 'vue-code-diff';
/* eslint-disable */
  const assets = [
    {key: "ec2", value: "el-icon-s-platform"},
    {key: "ecs", value: "el-icon-s-platform"},
    {key: "cvm", value: "el-icon-s-platform"},
    {key: "ami", value: "el-icon-picture"},
    {key: "s3", value: "el-icon-folder-opened"},
    {key: "oss", value: "el-icon-folder-opened"},
    {key: "obs", value: "el-icon-folder-opened"},
    {key: "cos", value: "el-icon-folder-opened"},
    {key: "security-group", value: "el-icon-lock"},
    {key: "network-addr", value: "el-icon-connection"},
    {key: "etc", value: "el-icon-s-platform"},
    {key: "asg", value: "el-icon-s-operation"},
    {key: "elb", value: "el-icon-s-operation"},
    {key: "slb", value: "el-icon-s-operation"},
    {key: "lambda", value: "el-icon-data-board"},
    {key: "autoscaling", value: "el-icon-s-operation"},
    {key: "account", value: "el-icon-s-custom"},
    {key: "eip", value: "el-icon-s-grid"},
    {key: "ip", value: "el-icon-s-grid"},
    {key: "ebs", value: "el-icon-wallet"},
    {key: "iam", value: "el-icon-s-tools"},
    {key: "rds", value: "el-icon-s-finance"},
    {key: "tag", value: "el-icon-s-flag"},
    {key: "vpc", value: "el-icon-menu"},
    {key: "vm", value: "el-icon-s-platform"},
    {key: "networksecuritygroup", value: "el-icon-lock"},
    {key: "disk", value: "el-icon-wallet"},
    {key: "storage", value: "el-icon-wallet"},
    {key: "others", value: "el-icon-s-unfold"},
  ];
  export default {
    name: "HistoryList",
    components: {
      TablePagination,
      DialogFooter,
      TableOperators,
      CodeDiff,
    },
    props: {
      selectNodeIds: Array,
    },
    watch: {
      selectNodeIds() {
        this.search();
      },
      batchReportId() {
      }
    },
    data() {
      return {
        tags: [],
        tableData: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        condition: {
          components: Object
        },
        direction: 'rtl',
        plugins: [],
        cmOptions: {
          tabSize: 4,
          mode: {
            name: 'shell',
            json: true
          },
          theme: 'bespin',
          lineNumbers: true,
          line: true,
          indentWithTabs: true,
        },
        visible: false,
        visibleList: false,
        diffVisible: false,
        radio: '',
        historys: [],
        historyPage: 1,
        historyPageSize: 10,
        historyTotal: 0,
        oldStr: 'old code',
        newStr: 'new code',
        innerDrawer: false,
        script: '',
        buttons: [
          {
            tip: this.$t('resource.resource_result'), icon: "el-icon-s-data", type: "success",
            exec: this.handleOpen
          }
        ],
        listButtons: [
          {
            tip: this.$t('resource.resource_result'), icon: "el-icon-data-board", type: "success",
            exec: this.handleOpenJson
          }, {
            tip: this.$t('vuln.history'), icon: "el-icon-guide", type: "primary",
            exec: this.codeDiffListOpen
          }
        ],
        diffButtons: [
         {
            tip: this.$t('commons.diff'), icon: "el-icon-sort", type: "primary",
            exec: this.codeDiffOpen
          }
        ],
        outputListData: [],
        outputListPage: 1,
        outputListPageSize: 10,
        outputListTotal: 0,
        outputListSearchData: {},
        codeDiffData: {},
      }
    },
    computed: {
    },
    methods: {
      init() {
        this.activePlugin();
        this.search();
      },
      //查询插件
      activePlugin() {
        let url = "/plugin/cloud";
        this.result = this.$get(url, response => {
          let data = response.data;
          this.plugins =  data;
        });
      },
      //查询列表
      async search() {
        let url = "/dashboard/vuln/history/" + this.currentPage + "/" + this.pageSize;
        if (!!this.selectNodeIds) {
          this.condition.accountId = this.selectNodeIds[0];
        } else {
          this.condition.accountId = null;
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
        if (rowIndex%4 === 0) {
          return 'success-row';
        } else if (rowIndex%2 === 0) {
          return 'warning-row';
        } else {
          return '';
        }
      },
      select(selection) {
        this.selection = selection.map(s => s.id);
        this.$emit('selection', selection);
      },
      isSelect(row) {
        return this.selection.includes(row.id)
      },
      edit(row) {
        this.$emit('edit', row);
      },
      handleOpen(item) {
        this.outputListSearchData = item;
        this.outputListDataSearch();
        this.visibleList =  true;
      },
      async outputListDataSearch() {
        let item = this.outputListSearchData;
        await this.$post("/vuln/historyList/" + this.outputListPage + "/" + this.outputListPageSize, item, response => {
          let data = response.data;
          this.outputListTotal = data.itemCount;
          this.outputListData = data.listObject;
        });
      },
      handleOpenJson(item) {
        this.script = item.resources;
        this.visible =  true;
      },
      async codeDiffListSearch() {
        let item = this.codeDiffData;
        let url = "/vuln/historyDiffList/" + this.historyPage + "/" + this.historyPageSize;
        await this.$post(url, item, response => {
          let data = response.data;
          this.historyTotal = data.itemCount;
          this.historys = data.listObject;
        });
      },
      codeDiffListOpen(item) {
        this.oldStr = item.resources;
        this.codeDiffData = item;
        this.codeDiffListSearch();
        this.diffVisible = true;
      },
      codeDiffOpen(item) {
        this.innerDrawerComparison(item);
      },
      handleClose() {
        this.visibleList = false;
        this.diffVisible =  false;
      },
      innerDrawerClose() {
        this.visible = false;
        this.diffVisible = false;
        this.innerDrawer = false;
      },
      innerDrawerComparison(item) {
        this.newStr = item.resources?item.resources:"[]";
        this.innerDrawer = true;
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
:focus{outline:0;}
.rtl >>> .el-drawer__body {
  overflow-y: auto;
  padding: 20px;
}
</style>
