<template>
  <main-container v-loading="result.loading">

    <el-card class="table-card">
      <template v-slot:header>
        <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
          <el-tab-pane :label="$t('server.rule_dimension')" name="first"></el-tab-pane>
          <el-tab-pane :label="$t('server.server_dimension')" name="second"></el-tab-pane>
        </el-tabs>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('server.result_list')" v-if="activeName === 'first'"
                      :items="items" :columnNames="columnNames"
                      :checkedColumnNames="checkedColumnNames" :checkAll="checkAll" :isIndeterminate="isIndeterminate"
                      @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange" @handleCheckAllChange="handleCheckAllChange"/>
        <table-header :condition.sync="serverCondition" @search="search"
                      :title="$t('server.result_list')" v-if="activeName === 'second'"
                      :items="items2" :columnNames="columnNames2"
                      :checkedColumnNames="checkedColumnNames2" :checkAll="checkAll2" :isIndeterminate="isIndeterminate2"
                      @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange2" @handleCheckAllChange="handleCheckAllChange2"/>
      </template>

      <hide-table
        v-if="activeName === 'first'"
        :table-data="tableData"
        @sort-change="sort"
        @filter-change="filter"
        @select-all="select"
        @select="select"
      >
        <!-- 展开 start -->
        <el-table-column type="expand" min-width="40">
          <template slot-scope="props">
            <el-form>
              <codemirror ref="cmEditor" v-model="props.row.returnLog" class="code-mirror" :options="cmOptions" />
            </el-form>
          </template>
        </el-table-column >
        <!-- 展开 end -->
        <el-table-column type="index" min-width="40"/>
        <el-table-column prop="serverName" v-if="checkedColumnNames.includes('serverName')" :label="$t('server.server_name')" min-width="140" show-overflow-tooltip></el-table-column>
        <el-table-column prop="ip" v-if="checkedColumnNames.includes('ip')" :label="'IP'" min-width="130" show-overflow-tooltip></el-table-column>
        <el-table-column prop="ruleName" v-if="checkedColumnNames.includes('ruleName')" :label="$t('server.rule_name')" min-width="180" show-overflow-tooltip></el-table-column>
        <el-table-column min-width="100" :label="$t('server.severity')" column-key="severity">
          <template v-slot:default="{row}">
            <rule-type :row="row"/>
          </template>
        </el-table-column>
        <el-table-column prop="type" v-if="checkedColumnNames.includes('type')" :label="$t('commons.type')" min-width="70" show-overflow-tooltip>
          <template v-slot:default="scope">
            <span v-if="scope.row.type === 'linux'">Linux</span>
            <span v-if="scope.row.type === 'windows'">Windows</span>
            <span v-if="!scope.row.type">N/A</span>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('resultStatus')" :label="$t('server.result_status')" min-width="130" prop="resultStatus" sortable show-overflow-tooltip>
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
        <el-table-column prop="isSeverity" v-if="checkedColumnNames.includes('isSeverity')" :label="$t('server.is_severity')" min-width="110" show-overflow-tooltip v-slot:default="scope" sortable>
          <el-tooltip class="item" effect="dark" :content="scope.row.returnLog" placement="top">
            <span v-if="scope.row.isSeverity" style="color: #46ad59">{{ $t('resource.risk_free') }}</span>
            <span v-if="!scope.row.isSeverity" style="color: #f84846">{{ $t('resource.risky') }}</span>
          </el-tooltip>
        </el-table-column>
        <el-table-column prop="updateTime" v-if="checkedColumnNames.includes('updateTime')" min-width="160" :label="$t('server.last_modified')" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="100" :label="$t('commons.operating')">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>
      <table-pagination v-if="activeName === 'first'" :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>


      <table-pagination v-if="activeName === 'second'" :change="search" :current-page.sync="serverPage" :page-size.sync="serverSize" :total="serverTotal"/>
    </el-card>

    <!--Result log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="65%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-row class="el-form-item-dev" v-if="logData.length == 0">
        <span>{{ $t('resource.i18n_no_data') }}<br></span>
      </el-row>
      <el-row class="el-form-item-dev" v-if="logData.length > 0">
        <div>
          <el-row>
            <el-col :span="24">
              <div class="grid-content bg-purple-light">
                <span class="grid-content-log-span">
                  {{ logForm.ruleName }}
                  <i class="el-icon-document-copy" @click="copy(logForm)" style="display: none;"></i>
                </span>
                <span class="grid-content-log-span">
                      <img :src="require(`@/assets/img/platform/${logForm.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                       &nbsp;&nbsp; {{ logForm.serverGroupName }} : {{ logForm.serverName }}
                      </span>
                <span class="grid-content-status-span" v-if="logForm.resultStatus === 'APPROVED'" style="color: #579df8">
                        <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                      </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'FINISHED'" style="color: #7ebf50">
                        <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                      </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'ERROR'" style="color: red;">
                        <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                      </span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="grid-content bg-purple-light">
                <span class="grid-content-log-span"> {{ logForm.ruleDesc }}</span>
                <span class="grid-content-log-span">
                  {{ logForm.ip }}
                  <span v-if="logForm.isSeverity" style="color: #46ad59">({{ $t('resource.risk_free') }})</span>
                  <span v-if="!logForm.isSeverity" style="color: #f84846">({{ $t('resource.risky') }})</span>
                </span>
                <span class="grid-content-status-span">
                  <rule-type :row="logForm"/>
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
        <el-form style="margin: 15px 0 0 0">
          <el-form-item :label="$t('server.server_rule')">
            <codemirror ref="cmEditor" v-model="logForm.rule" class="code-mirror" :options="cmOptions" />
          </el-form-item>
          <el-form-item :label="$t('server.server_result')">
            <codemirror ref="cmEditor" v-model="logForm.returnLog" class="code-mirror" :options="cmOptions" />
          </el-form-item>
        </el-form>
      </el-row>
      <template v-slot:footer>
        <dialog-footer
          @cancel="logVisible = false"
          @confirm="logVisible = false"/>
      </template>
    </el-drawer>
    <!--Result log-->

  </main-container>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import TableHeader from "@/business/components/common/components/TableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {_filter, _sort} from "@/common/js/utils";
import RuleType from "./RuleType";
import {SERVER_RESULT_CONFIGS, SERVER_RESULT_CONFIGS2} from "../../common/components/search/search-components";
import {severityOptions} from "@/common/js/constants";
import HideTable from "@/business/components/common/hideTable/HideTable";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'server.server_name',
    props: 'serverName',
    disabled: false
  },
  {
    label: 'IP',
    props: 'ip',
    disabled: false
  },
  {
    label: 'server.rule_name',
    props: 'ruleName',
    disabled: false
  },
  {
    label: 'server.severity',
    props: 'severity',
    disabled: false
  },
  {
    label: 'commons.type',
    props: 'type',
    disabled: false
  },
  {
    label: 'server.result_status',
    props: 'resultStatus',
    disabled: false
  },
  {
    label: 'server.is_severity',
    props: 'isSeverity',
    disabled: false
  },
  {
    label: 'server.last_modified',
    props: 'updateTime',
    disabled: false
  },
];

const columnOptions2 = [
  {
    label: 'server.server_name',
    props: 'name',
    disabled: false
  },
  {
    label: 'IP:Port',
    props: 'ip',
    disabled: false
  },
  {
    label: 'server.server_status',
    props: 'status',
    disabled: false
  },
  {
    label: 'server.server_type',
    props: 'type',
    disabled: false
  },
  {
    label: 'server.server_user_name',
    props: 'userName',
    disabled: false
  },
  {
    label: 'account.update_time',
    props: 'updateTime',
    disabled: false
  },
  {
    label: 'server.server_group',
    props: 'groupName',
    disabled: false
  },
  {
    label: 'account.creator',
    props: 'user',
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
    RuleType,
    HideTable,
  },
  data() {
    return {
      result: {},
      condition: {
        components: SERVER_RESULT_CONFIGS
      },
      serverCondition: {
        components: SERVER_RESULT_CONFIGS2
      },
      tableData: [],
      serverData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      serverPage: 1,
      serverSize: 10,
      serverTotal: 0,
      loading: false,
      severityOptions: [],
      direction: 'rtl',
      logVisible: false,
      detailVisible: false,
      logForm: {},
      logData: [],
      detailForm: {},
      buttons: [
        {
          tip: this.$t('resource.scan'), icon: "el-icon-refresh-right", type: "success",
          exec: this.handleScans
        },
        {
          tip: this.$t('resource.delete_result'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
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
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'server.server_name',
          id: 'serverName'
        },
        {
          name: 'IP',
          id: 'ip',
        },
        {
          name: 'server.rule_name',
          id: 'ruleName',
        },
      ],
      checkAll: true,
      isIndeterminate: false,
      activeName: 'first',
      checkedColumnNames2: columnOptions2.map((ele) => ele.props),
      columnNames2: columnOptions2,
      //名称搜索
      items2: [
        {
          name: 'server.server_name',
          id: 'name'
        },
        {
          name: 'IP',
          id: 'ip'
        },
        {
          name: 'Port',
          id: 'port',
        },
        {
          name: 'server.server_user_name',
          id: 'userName',
        },
      ],
      checkAll2: true,
      isIndeterminate2: false,
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
    handleCheckedColumnNamesChange2(value) {
      const checkedCount = value.length;
      this.checkAll2 = checkedCount === this.columnNames2.length;
      this.isIndeterminate2 = checkedCount > 0 && checkedCount < this.columnNames2.length;
      this.checkedColumnNames2 = value;
    },
    handleCheckAllChange2(val) {
      this.checkedColumnNames2 = val ? this.columnNames2.map((ele) => ele.props) : [];
      this.isIndeterminate2 = false;
      this.checkAll2 = val;
    },
    select(selection) {
    },
    //查询列表
    search() {
      let url = "/server/resultList/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
      this.result = this.$post("/server/resultServerList/" + this.currentPage + "/" + this.pageSize, this.serverCondition, response => {
        let data = response.data;
        this.serverTotal = data.itemCount;
        this.serverData = data.listObject;
      });
    },
    getStatus () {
      if (this.checkStatus(this.tableData)) {
        this.search();
        clearInterval(this.timer);
        this.timer = setInterval(this.getStatus,60000);
      } else {
        for (let data of this.tableData) {
          let url = "/server/getServerResult/";
          this.$get(url + data.id, response => {
            let result = response.data;
            if (data.resultStatus !== result.resultStatus) {
              data.resultStatus = result.resultStatus;
              data.returnLog = result.returnLog;
            }
          });
        }
      }
    },
    //是否是结束状态，返回false代表都在运行中，true代表已结束
    checkStatus (tableData) {
      let sum = 0;
      for (let row of tableData) {
        if (row.status != 'ERROR' && row.status != 'FINISHED' && row.status != 'WARNING') {
          sum++;
        }
      }
      return sum == 0;
    },
    severityOptionsFnc () {
      this.severityOptions = severityOptions;
    },
    init() {
      this.severityOptionsFnc();
      this.search();
    },
    sort(column) {
      _sort(column, this.condition);
      this.init();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.init();
    },
    showResultLog (result) {
      let url = "/server/log/";
      this.logForm = result;
      this.$get(url + result.id, response => {
        this.logData = response.data;
        this.logVisible = true;
      });
      this.$get("/server/getServerResult/" + result.id, response => {
        this.logForm = response.data;
      });
    },
    handleClose() {
      this.logVisible=false;
      this.detailVisible=false;
    },
    handleScans (item) {
      this.$alert(this.$t('resource.handle_scans'), '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$get("/server/rescan/" + item.id, response => {
              if (response.success) {
                this.search();
              }
            });
          }
        }
      });
    },
    handleDelete(obj) {
      this.$alert(this.$t('server.delete_confirm') + this.$t('server.result') + " ？", obj.ruleName, {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/server/deleteServerResult/" + obj.id,  res => {
              this.search();
              this.$success(this.$t('commons.delete_success'));
            });
          }
        }
      });
    },
    copy(row) {
      let input = document.createElement("input");
      document.body.appendChild(input);
      input.value = row['command'];
      input.select();
      if (input.setSelectionRange) {
        input.setSelectionRange(0, input.value.length);
      }
      document.execCommand("copy");
      document.body.removeChild(input);
    },
    handleClick(tag) {
      this.activeName = tag.name;
    },
  },
  computed: {
    codemirror() {
      return this.$refs.cmEditor.codemirror;
    }
  },
  created() {
  },
  activated() {
    this.init();
    this.timer = setInterval(this.getStatus,10000);
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
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
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
.tag-v{
  margin: 10px;
  cursor:pointer;
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
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
}

.grid-content-log-span {
  width: 39%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0 6px 2px;
  color: #606266;
}

.grid-content-status-span {
  width: 20%;float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
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
.code-mirror {
  width: 100%;
}
/deep/ :focus{outline:0;}
</style>
