<template>
  <main-container>
    <el-card class="table-card" >
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('log.event_sync')"
                      @syncData="syncData" :syncTip="$t('log.sync')"
                      :show-sync="true"/>
      </template>
        <el-table border :data="tableData" class="adjust-table table-content">
          <el-table-column  min-width="10%" :label="$t('log.cloud_account_name')">
            <template v-slot:default="scope">
              <span> <img :src="require(`@/assets/img/platform/${ getAccountIcon(scope.row.accountId)}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ getAccountName(scope.row.accountId) }}</span>
            </template>
          </el-table-column>

          <el-table-column
            prop="region"
            :label="$t('log.region')"
            min-width="15%"
          >
          </el-table-column>
          <el-table-column
            prop="createTime"
            :label="$t('log.sync_time')"
            min-width="10%"
          >
            <template v-slot:default="scope">
              <span>{{ scope.row.createTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>

          <el-table-column v-slot:default="scope" :label="$t('log.sync_status')" min-width="10%"
          >
            <el-button @click="showTaskLog(scope.row)" plain size="medium" type="primary" v-if="scope.row.status === 0">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="medium" type="success" v-else-if="scope.row.status === 1">
              <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="medium" type="danger" v-else-if="scope.row.status === 2">
              <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="medium" type="warning" v-else-if="scope.row.status === 3">
              <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
            </el-button>
          </el-table-column>
          <el-table-column
            prop="dataCount"
            :label="$t('log.data_count')"
            min-width="5%"
            v-slot:default="scope"
          >
            <el-link type="primary" :underline="false" class="md-primary text-click" @click="showEvents(scope.row)">
              {{ scope.row.dataCount }}
            </el-link>
          </el-table-column>
          <el-table-column
            :label="$t('log.sync_time_section')" min-width="20%"
          >
            <template v-slot:default="scope">
              <span>{{ scope.row.requestStartTime | timestampFormatDate }}-{{ scope.row.requestEndTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('commons.operating')" fixed="right"  min-width="5%">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </el-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>
    <!--Task log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="65%" :before-close="handleClose" direction="rtl"
               :destroy-on-close="true">
      <region-log :row="logForm"></region-log>
    </el-drawer>
    <!--Task log-->

    <!--sync-->
    <el-drawer class="rtl" :title="$t('log.event_sync')" :visible.sync="showSync" size="50%" :before-close="handleClose" direction="rtl"
               :destroy-on-close="true">
      <el-form :model="eventFrom" label-position="right" label-width="120px" size="small" >
        <el-form-item :label="$t('log.cloud_account')"  ref="accountId" prop="accountId">
          <el-select filterable :clearable="true"  style="width: 100%;" v-model="eventFrom.accountId" :placeholder="$t('log.cloud_account')" @change="changeFormRegion">
            <el-option
              v-for="item in accountList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              <img :src="require(`@/assets/img/platform/${item.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/> &nbsp; {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('log.region')" ref="regions" prop="regions" >
          <el-select filterable :clearable="true"  multiple style="width: 100%;" v-model="eventFrom.regions" :placeholder="$t('log.region')">
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
        <el-form-item :label="$t('log.sync_time_section')" ref="dateTime" prop="dateTime">
          <el-date-picker
            @change="changeDateTime"
            v-model="dateTime"
            type="datetimerange"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="pickerOptions"
            :range-separator="$t('log.to')"
            :start-placeholder="$t('log.start_time')"
            :end-placeholder="$t('log.end_time')"
            align="right">
          </el-date-picker>
        </el-form-item>
        <proxy-dialog-footer
          @cancel="cancel"
          @confirm="confirm"/>
       </el-form>

    </el-drawer>
    <!--sync-->

  </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "../head/TableHeader";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import {CLOUD_EVENT_CONFIGS} from "../../common/components/search/search-components";
import TableOperators from "../../common/components/TableOperators";
import RegionLog from "@/business/components/log/home/RegionLog";
import ProxyDialogFooter from "@/business/components/log/head/ProxyDialogFooter";
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
    ProxyDialogFooter
  },
  data() {
    return {
      eventFrom:{
        accountId: "",
        region: ""
      },
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
        components: CLOUD_EVENT_CONFIGS
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
          text: this.$t('log.week'),
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: this.$t('log.month'),
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
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
    cancel(){
      this.showSync = false
    },
    confirm(){
      if(!!this.dateTime){
        this.eventFrom.startTime = this.dateTime[0]
        this.eventFrom.endTime = this.dateTime[1]
      }else{
        this.$error(this.$t('log.error'))
        return
      }
      if(!!!this.eventFrom.accountId||!!!this.eventFrom.regions||this.eventFrom.regions.length===0||!!!this.eventFrom.startTime||!!!this.eventFrom.endTime){
        this.$error(this.$t('log.error'))
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
      this.$router.push({path:"/log/event",query:{
          accountId:row.accountId,
          region:row.region,
          startTime:row.requestStartTime,
          endTime:row.requestEndTime,
        }})
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
    syncData(){
      this.showSync = true
    /*  if(!!!this.currentAccount || !!!this.region){
        this.$error("云账号和区域不能为空")
        return
      }
      let url = "/cloud/event/sync";
      this.result = this.$post(url, {accountId:this.currentAccount,region:this.region,startTime:this.dateTime[0],endTime:this.dateTime[1]}, response => {
        this.search()
      });*/
    },
    search() {
      let url = "/cloud/event/sync/log/list/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, {accountId:this.currentAccount,region:this.region}, response => {
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
      }
    },
    changeDateTime(value){
      this.dateTime = value
    },
    init(){
      this.$get("/account/allList", response => {
        let accountList = response.data
        this.accountList = accountList.filter(item=>{
          return item.pluginId === "hummer-aliyun-plugin"
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
        console.log(response.data)
        this.logVisible = true
      });
    },
    formatDate: function(value) {
      let dt = new Date(value)
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
</style>

