<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('event.ip')"
                      :items="items" :columnNames="columnNames"
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
        <el-table-column type="index" min-width="40"/>
        <el-table-column min-width="100" show-overflow-tooltip v-if="checkedColumnNames.includes('ip')" :label="'IP'">
          <template v-slot:default="scope">
            <el-link type="primary" :underline="false" class="md-primary text-click" @click="showDetail(scope.row)">
              {{ scope.row.sourceIpAddress }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="region" v-if="checkedColumnNames.includes('regionName')" :label="$t('event.region')" min-width="170"  show-overflow-tooltip ></el-table-column>
        <el-table-column prop="earliestEventTime" v-if="checkedColumnNames.includes('earliestEventTime')" :label="$t('event.earliest_event_time')" min-width="120">
          <template v-slot:default="scope">
            <span>{{ scope.row.earliestEventTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="lastEventTime" v-if="checkedColumnNames.includes('lastEventTime')" :show-overflow-tooltip="true" :label="$t('event.last_event_time')" min-width="120">
          <template v-slot:default="scope">
            <span>{{ scope.row.lastEventTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="eventName" v-if="checkedColumnNames.includes('eventName')" :label="$t('event.event_name')" min-width="150" show-overflow-tooltip></el-table-column>
        <el-table-column prop="eventSum" v-if="checkedColumnNames.includes('eventSum')" :label="$t('event.event_sum')" min-width="80" sortable fixed="right"></el-table-column>
      </hide-table>

      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--ip detail-->
    <el-drawer class="rtl ip-list" :title="$t('event.ip_detail')" :visible.sync="detailVisible" size="75%" :before-close="handleClose" direction="rtl"
               :destroy-on-close="true">
      <div>
        <ip-chart/>
      </div>
      <el-tabs v-model="activeName" @tab-click="showCodemirror" style="margin: 20px;">
        <el-tab-pane :label="$t('event.event_audit')" name="first">
          <el-descriptions class="margin-top" v-for="detail in details" :key = "detail.id" :title="detail.eventName"  style="margin-top: 20px;" :column="2" border>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-s-grid"></i>
                {{$t('event.cloud_account')}}
              </template>
              <img :src="require(`@/assets/img/platform/${ getAccountIcon(detail.cloudAccountId)}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
              {{getAccountName(detail.cloudAccountId)}}
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-location-outline"></i>
                {{$t('event.region')}}
              </template>
              {{detail.regionName}}
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-tickets"></i>
                {{$t('event.event_name')}}
              </template>
              {{detail.eventName}}
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-time"></i>
                {{$t('event.event_time')}}
              </template>
              {{ detail.eventTime | timestampFormatDate }}
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-s-flag"></i>
                {{$t('event.event_id')}}
              </template>
              {{detail.eventId}}
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-link"></i>
                {{$t('event.event_source')}}
              </template>
              {{detail.eventSource}}
            </el-descriptions-item>
            <el-descriptions-item>
              <template slot="label">
                <i class="el-icon-position"></i>
                {{$t('event.source_ip')}}
              </template>
              {{detail.sourceIpAddress}}
            </el-descriptions-item>
            <el-descriptions-item >
              <template slot="label">
                <i class="el-icon-user"></i>
                {{$t('event.user_name')}}
              </template>
              {{detail.userName}}
            </el-descriptions-item>
          </el-descriptions>
          <table-pagination :change="searchDetail" :current-page.sync="detailCurrentPage" :page-size.sync="detailPageSize" :total="detailTotal"/>
        </el-tab-pane>
        <el-tab-pane :label="$t('event.event_insight')" name="second">
          <codemirror ref="cmEditor" v-model="script" class="code-mirror" :options="cmOptions" />
        </el-tab-pane>
      </el-tabs>
    </el-drawer>
    <!--ip detail-->

  </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import {CLOUD_EVENT_CONFIGS} from "../../common/components/search/search-components";
import {ACCOUNT_ID} from "@/common/js/constants";
import TableOperators from "../../common/components/TableOperators";
import ResultReadOnly from "@/business/components/common/components/ResultReadOnly";
import {_filter, _sort} from "@/common/js/utils";
import HideTable from "@/business/components/common/hideTable/HideTable";
import IpChart from "@/business/components/event/head/IpChart";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'IP',
    props: 'ip',
    disabled: false
  },
  {
    label: 'event.region',
    props: 'regionName',
    disabled: false
  },
  {
    label: 'event.event_name',
    props: 'eventName',
    disabled: false
  },
  {
    label: 'event.event_sum',
    props: 'eventSum',
    disabled: false
  },
  {
    label: 'event.earliest_event_time',
    props: 'earliestEventTime',
    disabled: false
  },
  {
    label: 'event.last_event_time',
    props: 'lastEventTime',
    disabled: false
  },
];

/* eslint-disable */
export default {
  name: "Ip",
  components: {
    ResultReadOnly,
    Container,
    TableHeader,
    TablePagination,
    MainContainer,
    TableOperators,
    HideTable,
    IpChart,
  },
  data() {
    return {
      details:[],
      detailCurrentPage: 1,
      detailPageSize: 10,
      detailTotal: 0,
      result: {},
      dateTime: [],
      currentAccount: '',
      region: [],
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      condition: {
        components: CLOUD_EVENT_CONFIGS
      },
      buttons: [
        {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'event.source_ip',
          id: 'sourceIpAddress',
        },
        {
          name: 'event.region',
          id: 'regionName'
        },
        {
          name: 'event.event_name',
          id: 'eventName',
        },

      ],
      checkAll: true,
      isIndeterminate: false,
      detailVisible: false,
      cmOptions: {
        tabSize: 4,
        mode: {
          name: 'json',
          json: true
        },
        theme: 'bespin',
        lineNumbers: true,
        line: true,
        indentWithTabs: true,
      },
      script: '',
      activeName: 'first',
    }
  },
  created() {
    let accountId = this.$route.query.accountId;
    if (!!accountId) {
      let region = this.$route.query.region;
      let startTime = this.$route.query.startTime;
      let endTime = this.$route.query.endTime;
      this.condition["combine"] = {
        accountId: {operator: "in", value: [accountId]}
        , region: {operator: "in", value: [...region.split(",")]}
        , eventTime: {operator: "between", value: [startTime, endTime]}
      }
      this.currentAccount = accountId
      this.region = region.split(",")
      this.dateTime = [this.formatDate(startTime * 1), this.formatDate(endTime * 1)]
    } else {
      this.currentAccount = localStorage.getItem(ACCOUNT_ID)
    }
    this.init()
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
    },
    resourceTypeFormat(row, column) {
      if (!!row.resourceType) {
        return row.resourceType
      } else {
        return "N/A"
      }
    },
    resourceNameFormat(row, column) {
      if (!!row.resourceName) {
        return row.resourceName
      } else {
        return "N/A"
      }
    },
    serviceNameFormat(row, column) {
      if (!!row.serviceName) {
        return row.serviceName
      } else {
        return "N/A"
      }
    },
    search() {
      let url = "/cloud/event/insight/list/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    cloudAccountSwitch(accountId) {
      this.currentAccount = accountId
      this.search()
    },
    changeRegion(value) {
      this.region = value
      this.search()
    },
    init() {
      this.$get("/account/allList", response => {
        this.accountList = response.data
        this.search()
      })
    },
    changeDateTime(value) {
      this.dateTime = value
      this.search()
    },
    formatDate: function (value) {
      let dt = new Date(value)
      let year = dt.getFullYear();
      let month = (dt.getMonth() + 1).toString().padStart(2, '0');
      let date = dt.getDate().toString().padStart(2, '0');
      let hour = dt.getHours().toString().padStart(2, '0');
      let minute = dt.getMinutes().toString().padStart(2, '0');
      let second = dt.getSeconds().toString().padStart(2, '0');
      return `${year}-${month}-${date} ${hour}:${minute}:${second}`;
    },
    getAccountName(accountId) {
      let result = this.accountList.filter(item => {
        return item.id === accountId
      })
      return result.length > 0 ? result[0].name : ""
    },
    getPluginName(accountId) {
      let result = this.accountList.filter(item => {
        return item.id === accountId
      })
      return result.length > 0 ? result[0].pluginName : ""
    },
    getAccountIcon(accountId) {
      let result = this.accountList.filter(item => {
        return item.id === accountId
      })
      return result.length > 0 ? result[0].pluginIcon : ""
    },
    sort(column) {
      _sort(column, this.condition);
      this.search();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.search();
    },
    //显示详情
    showDetail(item) {
      this.currentIp = item["sourceIpAddress"]
      this.detailCurrentPage = 1
      this.searchDetail()
      this.detailVisible = true;
    },
    searchDetail(){
      let url = "/cloud/event/list/" + this.detailCurrentPage + "/" +this.detailPageSize;
      let condition = this.condition
      condition["sourceIpAddress"]=this.currentIp
      this.result = this.$post(url, condition, response => {
        let data = response.data;
        this.detailTotal = data.itemCount;
        this.details = data.listObject
        this.script = JSON.stringify(data.listObject, null, "\t")
      });
    },
    handleClose() {
      this.detailVisible = false;
    },
    //切换tab时刷新代码块，不然不显示值
    showCodemirror() {
      setTimeout(() => {
        this.$refs.cmEditor.codemirror.refresh();
      }, 50);
    },
  },
  watch: {
    $route(to, from) {
      let accountId = this.$route.query.accountId;
      if (!!accountId) {
        let region = this.$route.query.region;
        let startTime = this.$route.query.startTime;
        let endTime = this.$route.query.endTime;
        this.condition["combine"] = {
          accountId: {operator: "in", value: [accountId]}
          , region: {operator: "in", value: [...region.split(",")]}
          , eventTime: {operator: "between", value: [startTime, endTime]}
        }
        this.currentAccount = accountId
        this.region = region.split(",")
        this.dateTime = [this.formatDate(startTime * 1), this.formatDate(endTime * 1)]
        this.search()
      }
    },
  },
  computed: {
    codemirror() {
      return this.$refs.cmEditor.codemirror;
    }
  },
}
</script>

<style scoped>
.table-content {
  width: 100%;
}

.el-table {
  cursor: pointer;
}
.code-mirror >>> .CodeMirror {
  /* Set height, width, borders, and global font properties here */
  height: 600px !important;
}
.ip-list >>> .el-drawer__header {
  margin: 0;
}
</style>

