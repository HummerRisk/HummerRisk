<template>
  <main-container>
    <el-card class="table-card" >
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      title="事件列表"
                      :currentAccount="currentAccount"
                      searchTip="查询"
                      :dateTime = "dateTime"
                      @cloudAccountSwitch="cloudAccountSwitch"
                      @changeRegion="changeRegion"
                      @changeDateTime="changeDateTime"
                      @syncData="syncData" :createTip="$t('common.sync')"
                      />
        <el-table border :data="tableData" class="adjust-table table-content">
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

/* eslint-disable */
export default {
  name: "Event",
  components: {
    Container,
    TableHeader,
    TablePagination,
    MainContainer,
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
    }
  },
  created() {
    this.currentAccount = localStorage.getItem(ACCOUNT_ID)
    this.dateTime = [this.formatDate(new Date().getTime()-1000*60*60*24),this.formatDate(new Date().getTime())]
  },
  methods: {
    syncData(){
      let url = "/cloud/event/sync";
      this.result = this.$post(url, {accountId:this.currentAccount,region:this.region,startTime:this.dateTime[0],endTime:this.dateTime[1]}, response => {
        alert("同步中")
      });
    },
    search() {
      let url = "/cloud/event/list/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, {accountId:this.currentAccount,region:this.region,startTime:this.dateTime[0],endTime:this.dateTime[1]}, response => {
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
    },
    changeDateTime(value){
      this.dateTime = value
      console.log(this.dateTime)
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
    }
  },
}
</script>

<style scoped>

</style>

