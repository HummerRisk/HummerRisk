<template>
  <el-row :gutter="24">
    <el-col :span="24">
      <el-card class="box-card" shadow="always">
        <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                  @filter-change="filter" @select-all="select" @select="select">
          <el-table-column type="index" min-width="5%"/>
          <el-table-column prop="name" :label="$t('code.name')" min-width="15%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/code/${scope.row.pluginIcon}`)" style="width: 30px; height: 25px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
            </template>
          </el-table-column>
          <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="16%">
            <el-tooltip effect="dark" :content="$t('history.result') + ' CRITICAL:' + scope.row.critical + ' HIGH:' +  scope.row.high + ' MEDIUM:' + scope.row.medium + ' LOW:' + scope.row.low + ' UNKNOWN:' + scope.row.unknown" placement="top">
              <el-link type="primary" class="text-click" @click="goResource(scope.row)">
                {{ 'C:' + scope.row.critical + ' H:' +  scope.row.high + ' M:' + scope.row.medium + ' L:' + scope.row.low + ' U:' + scope.row.unknown}}
              </el-link>
            </el-tooltip>
          </el-table-column>
          <el-table-column v-slot:default="scope" :label="$t('image.result_status')" min-width="11%" prop="resultStatus" sortable show-overflow-tooltip>
            <el-button @click="showResultLog(scope.row)" plain size="mini" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button @click="showResultLog(scope.row)" plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button @click="showResultLog(scope.row)" plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button @click="showResultLog(scope.row)" plain size="mini" type="success" v-else-if="scope.row.resultStatus === 'FINISHED'">
              <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
            </el-button>
            <el-button @click="showResultLog(scope.row)" plain size="mini" type="danger" v-else-if="scope.row.resultStatus === 'ERROR'">
              <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
            </el-button>
            <el-button @click="showResultLog(scope.row)" plain size="mini" type="warning" v-else-if="scope.row.resultStatus === 'WARNING'">
              <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
            </el-button>
          </el-table-column>
          <el-table-column prop="updateTime" min-width="14%" :label="$t('image.last_modified')" sortable>
            <template v-slot:default="scope">
              <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('resource.resource_result')" min-width="10%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>

      </el-card>

      <!--History output list-->
      <el-drawer class="rtl" :title="$t('dashboard.history')" :visible.sync="visibleList" size="85%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div>
          <el-table border :data="outputListData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName">
            <el-table-column type="index" min-width="2%"/>
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
                   :title="$t('dashboard.history')"
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
                   :title="$t('dashboard.history')"
                   :visible.sync="diffVisible"
                   size="80%"
                   :append-to-body="true"
                   :before-close="innerDrawerClose">
          <el-table border :data="historys" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName">
            <el-table-column type="index" min-width="2%"/>
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
          },
          {
            tip: this.$t('resource.delete_result'), icon: "el-icon-delete", type: "danger",
            exec: this.handleDelete
          }
        ],
        listButtons: [
          {
            tip: this.$t('resource.resource_result'), icon: "el-icon-data-board", type: "success",
            exec: this.handleOpenJson
          }, {
            tip: this.$t('dashboard.history'), icon: "el-icon-guide", type: "primary",
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
        let url = "/code/history/" + this.currentPage + "/" + this.pageSize;
        if (!!this.selectNodeIds) {
          this.condition.codeId = this.selectNodeIds[0];
        } else {
          this.condition.codeId = null;
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
      handleDelete(obj) {
        this.$alert(this.$t('code.delete_confirm') + this.$t('code.result') + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$get("/code/deleteHistoryCodeResult/" + obj.id,  res => {
                setTimeout(function () {window.location.reload()}, 2000);
                this.$success(this.$t('commons.delete_success'));
              });
            }
          }
        });
      },
      async outputListDataSearch() {
        let item = this.outputListSearchData;
        await this.$post("/account/historyList/" + this.outputListPage + "/" + this.outputListPageSize, item, response => {
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
        let url = "/account/historyDiffList/" + this.historyPage + "/" + this.historyPageSize;
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
