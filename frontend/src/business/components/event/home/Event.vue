<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('event.event_analysis')"/>
      </template>
      <el-table border :data="tableData" class="adjust-table table-content">
        <el-table-column type="expand">
          <template v-slot:default="props">
            <el-divider><i class="el-icon-folder-opened"></i></el-divider>
            <el-form>
              <result-read-only
                :row="typeof(props.row) === 'string'?JSON.parse(props.row):props.row"></result-read-only>
              <el-divider><i class="el-icon-document-checked"></i></el-divider>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column
          min-width="10%" :label="$t('event.cloud_account_name')"
        >
          <template v-slot:default="scope">
              <span><img :src="require(`@/assets/img/platform/${ getAccountIcon(scope.row.cloudAccountId)}`)"
                         style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                {{ getAccountName(scope.row.cloudAccountId) }}</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="regionName"
          :label="$t('event.region')"
          min-width="10%"
        >
        </el-table-column>
        <el-table-column
          prop="eventTime"
          :label="$t('event.event_time')"
          min-width="10%"
        >
          <template v-slot:default="scope">
            <span>{{ scope.row.eventTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="userName"
          :show-overflow-tooltip="true"
          :label="$t('event.user_name')"
          min-width="10%"
        >
        </el-table-column>
        <el-table-column
          prop="sourceIpAddress"
          :show-overflow-tooltip="true"
          :label="$t('event.source_ip')"
          min-width="10%"
        >
        </el-table-column>
        <el-table-column
          prop="eventName"
          :label="$t('event.event_name')"
          min-width="10%"
        >
        </el-table-column>
        <el-table-column
          prop="resourceType"
          :show-overflow-tooltip="true"
          :label="$t('event.resource_type')"
          min-width="10%"
          :formatter="resourceTypeFormat"
        >
        </el-table-column>
        <el-table-column
          prop="resourceName"
          :show-overflow-tooltip="true"
          :label="$t('event.resource_name')"
          min-width="15%"
          :formatter="resourceNameFormat"
        >
        </el-table-column>
        <el-table-column
          :label="$t('event.risk_level')"
          min-width="10%"
        >
          <template v-slot:default="scope">
            <el-tag v-if="!!!scope.row.eventRating || scope.row.eventRating === 0 " type="success">{{ $t('rule.LowRisk') }}</el-tag>
            <el-tag v-if="scope.row.eventRating=== 1" type="warning">{{ $t('rule.MediumRisk') }}</el-tag>
            <el-tag v-if="scope.row.eventRating === 2" type="danger">{{ $t('rule.HighRisk') }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>
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
import ResultReadOnly from "@/business/components/event/home/ResultReadOnly";
/* eslint-disable */
export default {
  name: "Event",
  components: {
    ResultReadOnly,
    Container,
    TableHeader,
    TablePagination,
    MainContainer,
    TableOperators
  },
  data() {
    return {
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
      // this.dateTime = [this.formatDate(new Date().getTime()-1000*60*60*24),this.formatDate(new Date().getTime())]
    }
    this.init()
  },

  methods: {
    handleDelete(row) {
      this.$alert(this.$t('account.delete_confirm') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$post("/cloud/event/delete/" + row.eventId, {}, response => {
              this.$success(this.$t('commons.delete_success'));
              this.search()
            });
          }
        }
      });

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


    search() {
      let url = "/cloud/event/list/" + this.currentPage + "/" + this.pageSize;
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
    }
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
  }
}
</script>

<style scoped>
.table-content {
  width: 100%;
}

.el-table {
  cursor: pointer;
}
</style>

