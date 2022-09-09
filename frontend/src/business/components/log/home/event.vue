<template>
  <main-container>
    <el-card class="table-card" >
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      ref="tableHeader"
                      title="事件列表"
                      :currentAccount="currentAccount"
                      searchTip="查询"
                      :dateTime = "dateTime"
                      :initRegion="region"
                      @cloudAccountSwitch="cloudAccountSwitch"
                      @changeRegion="changeRegion"
                      @changeDateTime="changeDateTime"
                      @syncData="syncData" :createTip="$t('common.sync')"
                      :showSync = "false"
                      />
        <el-table border :data="tableData" class="adjust-table table-content">
          <el-table-column
            label="云账号名称"
          >
            <template v-slot:default="scope">
              <span>{{ getAccountName(scope.row.cloudAccountId) }}</span>
            </template>
          </el-table-column>
          <el-table-column

            label="云平台"
          >
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${ getAccountIcon(scope.row.cloudAccountId)}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ $t(getPluginName(scope.row.cloudAccountId)) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column
            prop="syncRegion"
            label="区域"
          >
          </el-table-column>
          <el-table-column
            prop="eventTime"
            label="事件时间"
            >
            <template v-slot:default="scope">
              <span>{{ scope.row.eventTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="userName"
            label="用户名"
           >
          </el-table-column>
          <el-table-column
            prop="eventName"
            label="事件名称"
          >
          </el-table-column>
          <el-table-column
            prop="resourceType"
            label="资源类型"
           >
          </el-table-column>
          <el-table-column
            prop="resourceName"
            label="资源名称"
            >
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
      region:[],
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
    let accountId = this.$route.query.accountId;
    if(!!accountId){
      let region =  this.$route.query.region;
      let startTime = this.$route.query.startTime;
      let endTime = this.$route.query.endTime;
      this.currentAccount = accountId
      this.region = region.split(",")
      this.dateTime = [this.formatDate(startTime*1),this.formatDate(endTime*1)]
    }else{
      this.currentAccount = localStorage.getItem(ACCOUNT_ID)
     // this.dateTime = [this.formatDate(new Date().getTime()-1000*60*60*24),this.formatDate(new Date().getTime())]
    }
    this.init()
  },

  methods: {
    handleDelete(row){
      this.$alert(this.$t('account.delete_confirm') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$post("/cloud/event/delete/"+row.eventId, {}, response => {
              this.$success(this.$t('commons.delete_success'));
              this.search()
            });
          }
        }
      });

    },
    syncData(){
      let url = "/cloud/event/sync";
      this.result = this.$post(url, {accountId:this.currentAccount,region:this.region,startTime:this.dateTime[0],endTime:this.dateTime[1]}, response => {
        alert("同步中")
      });
    },
    search() {
      let url = "/cloud/event/list/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, {accountId:this.currentAccount,regions:this.region,startTime:this.dateTime[0],endTime:this.dateTime[1]}, response => {
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
    init(){
      this.$get("/account/allList", response => {
        this.accountList = response.data
        this.search()
      })
    },
    changeDateTime(value){
      this.dateTime = value
      this.search()
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
  watch: {
    $route(to, from){
      let accountId = this.$route.query.accountId;
      if(!!accountId){
        let region =  this.$route.query.region;
        let startTime = this.$route.query.startTime;
        let endTime = this.$route.query.endTime;
        this.currentAccount = accountId
        this.region = region.split(",")
        this.dateTime = [this.formatDate(startTime*1),this.formatDate(endTime*1)]
        this.$refs.tableHeader.setDateTime(this.dateTime)
        this.$refs.tableHeader.setRegion(this.region)
        this.search()
      }
    },
  }
}
</script>

<style scoped>

</style>

