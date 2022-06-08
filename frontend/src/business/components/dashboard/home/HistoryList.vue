<template>
  <el-row :gutter="24" class="el-row-se">
    <el-col :span="24">
      <el-card class="box-card" shadow="always">
        <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                  @filter-change="filter" @select-all="select" @select="select">
          <el-table-column type="index" min-width="5%"/>
          <el-table-column :label="$t('history.cloud_account')" min-width="20%" show-overflow-tooltip>
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

      <!--History output-->
      <el-drawer class="rtl" :title="$t('dashboard.history')" :visible.sync="visible" size="60%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-form label-position="right">
          <el-form-item style="margin: 1%;">
            <codemirror ref="cmEditor" v-model="script" class="code-mirror" :options="cmOptions" />
          </el-form-item>
        </el-form>
        <dialog-footer
          @cancel="visible = false"
          @confirm="handleClose"/>
      </el-drawer>
      <!--History output-->

      <!--History result-->
      <el-drawer class="rtl" :title="$t('dashboard.history')" :visible.sync="diffVisible" size="85%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-table border :data="historys" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName" style="margin: 2%;">
          <el-table-column type="index" min-width="5%"/>
          <el-table-column :label="$t('history.cloud_account')" min-width="20%" show-overflow-tooltip>
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
          <el-table-column :label="$t('history.resource_result')" min-width="15%" show-overflow-tooltip>
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
              <el-link type="primary" @click="innerDrawerComparison(scope.row)">
                {{ $t('dashboard.online_comparison') }} <i class="el-icon-s-data"></i>
              </el-link>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="historyList" :current-page.sync="historyPage" :page-size.sync="historyPageSize" :total="historyTotal"/>
        <el-drawer
          size="80%"
          :title="$t('dashboard.online_comparison')"
          :append-to-body="true"
          :before-close="innerDrawerClose"
          :visible.sync="innerDrawer">
          <el-form>
            <code-diff
              :old-string="oldStr"
              :new-string="newStr"
              outputFormat="side-by-side"
              :isShowNoChange="true"
              :drawFileList="true"
              :context="10"/>
          </el-form>
        </el-drawer>
        <dialog-footer
          @cancel="diffVisible = false"
          @confirm="handleClose"/>
      </el-drawer>
      <!--History result-->

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
          }, {
            tip: this.$t('commons.edit'), icon: "el-icon-document-copy", type: "primary",
            exec: this.codeDiffOpen
          }
        ],
      }
    },
    created() {
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
        let url = "/plugin/all";
        this.result = this.$get(url, response => {
          let data = response.data;
          this.plugins =  data;
        });
      },
      //查询列表
      async search() {
        let url = "/dashboard/history/" + this.currentPage + "/" + this.pageSize;
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
        this.visible =  true;
        this.$post("/resource/string2PrettyFormat", {json: item.output?item.output:"[]"}, res => {
          this.script = res.data;
        });
      },
      codeDiffOpen(item) {
        this.condition.accountId = item.accountId;
        this.historyList();
        this.diffVisible =  true;
        this.$post("/resource/string2PrettyFormat", {json: item.output?item.output:"[]"}, res => {
          this.oldStr = res.data;
        });
      },
      handleClose() {
        this.visible =  false;
        this.diffVisible =  false;
      },
      historyList() {
        let url = "/dashboard/history/" + this.historyPage + "/" + this.historyPageSize;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.historyTotal = data.itemCount;
          this.historys = data.listObject;
        });
      },
      innerDrawerClose() {
        this.innerDrawer = false;
      },
      innerDrawerComparison(item) {
        this.newStr = item.output?item.output:"[]";
        this.innerDrawer = true;
      },
    },
    mounted() {
      this.init();
    },
  }
</script>

<style scoped>
/deep/  .code-mirror {
    height: 600px !important;
  }
/deep/ .code-mirror >>> .CodeMirror {
    /* Set height, width, borders, and global font properties here */
    height: 600px !important;
  }
/deep/ .el-drawer__header {
    margin-bottom: 0;
  }
/deep/ :focus{outline:0;}
</style>
