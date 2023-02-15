<template>
  <main-container>
    <el-card class="table-card" >
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('event.event_sync')" :show-create="true"
                      @create="create" :createTip="$t('event.sync')"
                      :items="items" :columnNames="columnNames"
                      :checkedColumnNames="checkedColumnNames" :checkAll="checkAll2" :isIndeterminate="isIndeterminate"
                      @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange" @handleCheckAllChange="handleCheckAllChange"/>
      </template>
      <hide-table
        :table-data="tableData"
        @sort-change="sort"
        @filter-change="filter"
        @select-all="select"
        @select="select"
      >
          <el-table-column type="index" min-width="50"/>
          <el-table-column min-width="150" v-if="checkedColumnNames.includes('accountName')" :label="$t('event.cloud_account_name')">
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${ getAccountIcon(scope.row.accountId)}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ getAccountName(scope.row.accountId) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="regionName" v-if="checkedColumnNames.includes('regionName')" :label="$t('event.region')" min-width="110">
            <template v-slot:default="scope">
              <regions :logId="scope.row.id" :accountId="scope.row.accountId"></regions>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" v-if="checkedColumnNames.includes('createTime')" :label="$t('event.sync_time')" min-width="160">
            <template v-slot:default="scope">
              <span>{{ scope.row.createTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('status')" :label="$t('event.sync_status')" min-width="120">
            <el-button @click="showTaskLog(scope.row)" plain size="mini" type="primary" v-if="scope.row.status === 0">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="mini" type="success" v-else-if="scope.row.status === 1">
              <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="mini" type="danger" v-else-if="scope.row.status === 2">
              <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="mini" type="warning" v-else-if="scope.row.status === 3">
              <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
            </el-button>
          </el-table-column>
          <el-table-column prop="dataCount" v-if="checkedColumnNames.includes('dataCount')" :label="$t('event.data_count')" min-width="90" v-slot:default="scope">
            <el-link type="primary" :underline="false" class="md-primary text-click" @click="showEvents(scope.row)">
              {{ scope.row.dataCount }}
            </el-link>
          </el-table-column>
          <el-table-column :label="$t('event.sync_time_section')" v-if="checkedColumnNames.includes('syncTime')" min-width="300">
            <template v-slot:default="scope">
              <span>{{ scope.row.requestStartTime | timestampFormatDate }} - {{ scope.row.requestEndTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('commons.operating')" fixed="right"  min-width="100">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </hide-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--sync log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="65%" :before-close="handleClose" direction="rtl"
               :destroy-on-close="true">
      <region-log :row="logForm"></region-log>
    </el-drawer>
    <!--sync log-->

    <!--sync-->
    <el-drawer class="rtl" :title="$t('event.event_sync')" :visible.sync="showSync" size="50%" :before-close="handleClose" direction="rtl"
               :destroy-on-close="true">
      <el-form :model="eventFrom" label-position="right" label-width="120px" size="small" >
        <el-form-item :label="$t('event.cloud_account')"  ref="accountId" prop="accountId">
          <el-select filterable :clearable="true"  style="width: 100%;" v-model="eventFrom.accountId" :placeholder="$t('event.cloud_account')" @change="changeFormRegion">
            <el-option
              v-for="item in accountList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              <img :src="require(`@/assets/img/platform/${item.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/> &nbsp; {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('event.region')" ref="regions" prop="regions" >
          <el-select filterable :clearable="true"  multiple style="width: 100%;" v-model="eventFrom.regions" :placeholder="$t('event.region')">
            <el-checkbox v-model="checkAll" @change="selectOnChangeAll(checkAll, null)">{{ $t('account.i18n_sync_all') }}</el-checkbox>
            <el-option
              v-for="item in regionList"
              :key="item.regionId"
              :label="item.regionName"
              :value="item.regionId">
               &nbsp; {{ item.regionName }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('event.sync_time_section')" ref="dateTime" prop="dateTime">
          <el-date-picker
            style="width: 100%"
            @change="changeDateTime"
            v-model="dateTime"
            type="datetimerange"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="pickerOptions"
            :range-separator="$t('event.to')"
            :start-placeholder="$t('event.start_time')"
            :end-placeholder="$t('event.end_time')"
            align="right">
          </el-date-picker>
        </el-form-item>
        <div style="color: red;font-style:italic;margin: 5px 0 10px 50px;">{{ $t('event.cloud_note') }}</div>
        <dialog-footer
          @cancel="cancel"
          @confirm="confirm"/>
       </el-form>

    </el-drawer>
    <!--sync-->

  </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import {CLOUD_EVENT_SYNC_CONFIGS} from "../../common/components/search/search-components";
import TableOperators from "../../common/components/TableOperators";
import RegionLog from "@/business/components/event/home/RegionLog";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import Regions from "@/business/components/event/home/Regions";
import {_filter, _sort} from "@/common/js/utils";
import HideTable from "@/business/components/common/hideTable/HideTable";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'event.cloud_account_name',
    props: 'accountName',
    disabled: false
  },
  {
    label: 'event.region',
    props: 'regionName',
    disabled: false
  },
  {
    label: 'event.sync_time',
    props: 'createTime',
    disabled: false
  },
  {
    label: 'event.sync_status',
    props: 'status',
    disabled: false
  },
  {
    label: 'event.data_count',
    props: 'dataCount',
    disabled: false
  },
  {
    label: 'event.sync_time_section',
    props: 'syncTime',
    disabled: false
  },
];

/* eslint-disable */
export default {
  name: "Event",
  components: {
    Container,
    TableHeader,
    TablePagination,
    MainContainer,
    TableOperators,
    RegionLog,
    DialogFooter,
    Regions,
    HideTable,
  },
  data() {
    return {
      pickerMinDate:"",
      eventFrom:{
        accountId: "",
        region: ""
      },
      supportCloud:["hummer-aliyun-plugin","hummer-huawei-plugin","hummer-qcloud-plugin","hummer-aws-plugin","hummer-baidu-plugin","hummer-huoshan-plugin","hummer-ksyun-plugin"],
      checkAll: false,
      showSync: false,
      syncLog: '',
      logVisible: false,
      dateTime: [],
      currentAccount: '',
      region:'',
      accountList:[],
      tableData: [],
      regionList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      condition: {
        components: CLOUD_EVENT_SYNC_CONFIGS
      },
      logForm: {regionLogs: []},
      buttons: [
        {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      pickerOptions: {
        shortcuts: [{
          text: this.$t('event.week'),
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
            text: this.$t('event.two_week'),
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 14);
              picker.$emit('pick', [start, end]);
            }
        }],
        onPick: obj => {
          this.pickerMinDate = new Date(obj.minDate).getTime();
        },
        disabledDate: time => {
          if (this.pickerMinDate) {
            const day1 = 14 * 24 * 3600 * 1000;
            let maxTime = this.pickerMinDate + day1;
            let minTime = this.pickerMinDate - day1;
            return time.getTime() > maxTime || time.getTime() < minTime;
          }
        }
      },
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'event.cloud_account_name',
          id: 'accountName'
        },
        {
          name: 'event.region',
          id: 'regionName'
        }
      ],
      checkAll2: true,
      isIndeterminate: false,
    }
  },
  created() {
    this.init()
  },
  activated() {
    this.timer = setInterval(this.getStatus,10000);
  },
  beforeDestroy() {
    clearInterval(this.timer);
  },
  methods: {
    handleCheckedColumnNamesChange(value) {
      const checkedCount = value.length;
      this.checkAll2 = checkedCount === this.columnNames.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.columnNames.length;
      this.checkedColumnNames = value;
    },
    handleCheckAllChange(val) {
      this.checkedColumnNames = val ? this.columnNames.map((ele) => ele.props) : [];
      this.isIndeterminate = false;
      this.checkAll2 = val;
    },
    select(selection) {
    },
    reset(){
      this.dateTime=[]
      this.eventFrom={
        accountId: "",
        region: "",
        startTime: "",
        endTime: "",
      }
    },
    cancel(){
      this.showSync = false
    },
    confirm(){
      if(!!this.dateTime){
        this.eventFrom.startTime = this.dateTime[0]
        this.eventFrom.endTime = this.dateTime[1]
      }else{
        this.$error(this.$t('event.error'))
        return
      }
      if(!!!this.eventFrom.accountId||!!!this.eventFrom.regions||this.eventFrom.regions.length===0||!!!this.eventFrom.startTime||!!!this.eventFrom.endTime){
        this.$error(this.$t('event.error'))
        return
      }
      let url = "/cloud/event/sync";
      this.result = this.$post(url, this.eventFrom, response => {
        this.showSync = false
        this.search()
      })
    },
    handleDelete(row){
      this.$alert(this.$t('account.delete_confirm') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$post("/cloud/event/sync/log/delete/"+row.id, {}, response => {
              this.$success(this.$t('commons.delete_success'));
              this.search()
            });
          }
        }
      });

    },
    handleClose() {
      this.logVisible=false;
      this.showSync=false;
      this.detailVisible=false;
    },
    showEvents(row){
      this.$router.push(
        {
          path:"/log/event",
          query:{
            accountId:row.accountId,
            region:row.region,
            startTime:row.requestStartTime,
            endTime:row.requestEndTime
          }
        }
      )
    },
    getStatus () {
      if(this.checkStatus(this.tableData)){
        this.search();
        clearInterval(this.timer);
        this.timer = setInterval(this.getStatus,60000);
        return
      }
      this.search()
    },
    //是否是结束状态，返回false代表都在运行中，true代表已结束
    checkStatus (tableData) {
      let sum = 0;
      for (let row of tableData) {
        if (row.status == 0) {
          sum++;
        }
      }
      return sum == 0;
    },
    create(){
      this.reset()
      this.checkAll = false
      this.showSync = true
    },
    search() {
      let url = "/cloud/event/sync/log/list/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    cloudAccountSwitch(accountId){
      this.currentAccount = accountId
      this.search()
    },
    changeRegion(value){
      this.region = value
      this.search()
    },
    changeFormRegion(){
      let account = this.accountList.filter(item =>{
        return item.id === this.eventFrom.accountId
      })
      if(account.length > 0){
        this.regionList = JSON.parse(account[0].regions)
        this.regionList = this.regionList.filter(item =>{
          return item.regionId !== 'default'
        })
      }
    },
    changeDateTime(value){
      this.dateTime = value
    },
    init(){
      this.$get("/account/allList", response => {
        let accountList = response.data
        this.accountList = accountList.filter(item=>{
          return this.supportCloud.includes(item.pluginId )
        })
        //that.dateTime = [this.formatDate(new Date().getTime()-1000*60*60*24),this.formatDate(new Date().getTime())]
        this.search()
      })
    },
    showTaskLog(row){
      let logId = row.id
      this.logForm.regionLogs = []
      this.logForm.showLogTaskId = logId
      let account = this.accountList.find(item=>{
        return item.id === row.accountId
      })
      this.logForm.account = account
      let url = "/cloud/event/sync/log/region/list/" + logId;
      this.result = this.$post(url, {}, response => {
        this.logForm.regionLogs = response.data;
        this.logVisible = true
      });
    },
    formatDate: function(value) {
      let dt = new Date(value);
      let year = dt.getFullYear();
      let month = (dt.getMonth() + 1).toString().padStart(2,'0');
      let date = dt.getDate().toString().padStart(2,'0');
      let hour = dt.getHours().toString().padStart(2,'0');
      let minute = dt.getMinutes().toString().padStart(2,'0');
      let second = dt.getSeconds().toString().padStart(2,'0');
      return `${year}-${month}-${date} ${hour}:${minute}:${second}`;
    },
    getAccountName(accountId){
      let result =  this.accountList.filter(item =>{
        return item.id === accountId
      })
      return result.length >0?result[0].name:""
    },
    getPluginName(accountId){
      let result =  this.accountList.filter(item =>{
        return item.id === accountId
      })
      return result.length >0?result[0].pluginName:""
    },
    getAccountIcon(accountId){
      let result =  this.accountList.filter(item =>{
        return item.id === accountId
      })
      return result.length >0?result[0].pluginIcon:""
    },
    selectOnChangeAll (checkAll, item) {
      if (!!item) {
        item.regions = [];
        if (item.checkAll) {
          for (let option of this.regions) {
            item.regions.push(option["regionId"]);
          }
        }
      } else {
        this.eventFrom.regions = [];
        if (!!checkAll) {
          for (let option of this.regionList) {
            this.eventFrom.regions.push(option["regionId"]);
          }
        }
      }
    },
    sort(column) {
      _sort(column, this.condition);
      this.search();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.search();
    },
  },
}
</script>

<style scoped>
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
.el-tag {
  margin: 2px;
  font-size: 5px;
}
</style>

