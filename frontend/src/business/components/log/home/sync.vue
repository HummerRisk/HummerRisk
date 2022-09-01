<template>
  <main-container>
    <el-card class="table-card" >
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      title="事件同步"
                      :currentAccount="currentAccount"
                      searchTip="查询"
                      :dateTime = "dateTime"
                      @cloudAccountSwitch="cloudAccountSwitch"
                      @changeRegion="changeRegion"
                      @changeDateTime="changeDateTime"
                      @syncData="syncData" :createTip="$t('common.sync')"
                      :showSearch = "true" :showDate = "true"
        />
        <el-table border :data="tableData" class="adjust-table table-content">
          <el-table-column
            label="云账号名称"
          >
            <template v-slot:default="scope">
              <span>{{ getAccountName(scope.row.accountId) }}</span>
            </template>
          </el-table-column>
          <el-table-column

              label="云平台"
          >
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${ getAccountIcon(scope.row.accountId)}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ $t(getPluginName(scope.row.accountId)) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column
            prop="region"
            label="区域"
          >
          </el-table-column>
          <el-table-column
            prop="createTime"
            label="同步时间"
          >
            <template v-slot:default="scope">
              <span>{{ scope.row.createTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>

          <el-table-column v-slot:default="scope"
            label="同步状态"
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
          </el-table-column>
          <el-table-column
            prop="dataCount"
            label="数量"
            v-slot:default="scope"
          >
            <el-link type="primary" :underline="false" class="md-primary text-click" @click="showEvents(scope.row)">
              {{ scope.row.dataCount }}
            </el-link>
          </el-table-column>
          <el-table-column
            label="时间段"
          >
            <template v-slot:default="scope">
              <span>{{ scope.row.requestStartTime | timestampFormatDate }}---{{ scope.row.requestEndTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('commons.operating')" fixed="right">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </el-table>
      </template>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>
  </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "../head/TableHeader";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import {CLOUD_EVENT_CONFIGS} from "../../common/components/search/search-components";
import {ACCOUNT_ID} from "@/common/js/constants";
import TableOperators from "../../common/components/TableOperators";

/* eslint-disable */
export default {
  name: "Event",
  components: {
    Container,
    TableHeader,
    TablePagination,
    MainContainer,
    TableOperators
  },
  data() {
    return {
      dateTime: [],
      currentAccount: '',
      region:'',
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
    }
  },
  created() {
    this.currentAccount = localStorage.getItem(ACCOUNT_ID)
    this.init()
  },
  activated() {
    this.timer = setInterval(this.getStatus,10000);
  },
  beforeDestroy() {
    clearInterval(this.timer);
  },
  methods: {
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
    showTaskLog(row){
      console.log(row)
    },
    syncData(){
      if(!!!this.currentAccount || !!!this.region){
        this.$error("云账号和区域不能为空")
        return
      }
      let url = "/cloud/event/sync";
      this.result = this.$post(url, {accountId:this.currentAccount,region:this.region,startTime:this.dateTime[0],endTime:this.dateTime[1]}, response => {
        this.search()
      });
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
    },
    changeRegion(value){
      this.region = value
      this.search()
    },
    changeDateTime(value){
      this.dateTime = value
    },
    init(){
      let that = this
      this.$get("/account/allList", response => {
        that.accountList = response.data

        //that.dateTime = [this.formatDate(new Date().getTime()-1000*60*60*24),this.formatDate(new Date().getTime())]
        that.search()
      })
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
    }
  },
}
</script>

<style scoped>

</style>

