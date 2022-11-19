<template>
  <el-row :gutter="24">
    <el-col :span="24">
      <el-card class="content" shadow="always">
        <el-descriptions :title="$t('k8s.source_sum')" :column="8" border direction="vertical">
          <el-descriptions-item v-for="item in resourceSummary" :label="item.resourceType" :key="item.resourceType" label-class-name="my-label" content-class-name="my-content" ><a href="#" @click="resourceTypeClick(item.resourceType)">{{ item.count }}</a></el-descriptions-item>
        </el-descriptions>

        <el-card class="table-card" v-loading="result.loading" style="margin-top: 10px;">
          <template v-slot:header>
            <table-header :condition.sync="condition" @search="search"
                          :title="$t('k8s.source_list')"/>
          </template>
          <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort"
                    :row-class-name="tableRowClassName"
                    @filter-change="filter">
            <el-table-column type="expand"  min-width="40">
              <template v-slot:default="scope">
                <el-divider><i class="el-icon-folder-opened"></i></el-divider>
                <el-form>
                  <result-read-only :row="typeof(scope.row.resource) === 'string'?JSON.parse(scope.row.resource):scope.row.resource"></result-read-only>
                  <el-divider><i class="el-icon-document-checked"></i></el-divider>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column type="index" min-width="40"/>
            <el-table-column :label="$t('event.cloud_account_name')" min-width="130" show-overflow-tooltip>
              <template v-slot:default="scope">
              <span><img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                {{ getAccountName(scope.row.accountId) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="hummerId" :label="$t('resource.resource_id')" min-width="130" show-overflow-tooltip sortable></el-table-column>
            <el-table-column prop="regionName" :label="$t('event.region')" min-width="150"  show-overflow-tooltip sortable></el-table-column>
            <el-table-column prop="resourceType" :label="$t('dashboard.resource_type')" min-width="130" show-overflow-tooltip sortable></el-table-column>
            <el-table-column min-width="160" :label="$t('account.update_time')" sortable
                             prop="updateTime">
              <template v-slot:default="scope">
                <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="ruleCount"
              :label="$t('resource.risk')"
              min-width="120"
            >
              <template v-slot:default="scope">
                <resource-rule  :hummer-id="scope.row.hummerId" :risk-count="scope.row.riskCount" :account-id="scope.row.accountId"
                               :region-id="scope.row.regionId" :resource-type="scope.row.resourceType"
                               :account-name="getAccountName(scope.row.accountId)"
                ></resource-rule>
              </template>
            </el-table-column>
          </el-table>
          <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
        </el-card>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import Container from "../.././common/components/Container";
import TableHeader from "@/business/components/common/components/TableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {SITUATION_CONFIGS} from "../../common/components/search/search-components";
import ResultReadOnly from "@/business/components/common/components/ResultReadOnly";
import ResourceRule from "@/business/components/cloudSituation/home/ResourceRule";
/* eslint-disable */
export default {
  components: {
    Container,
    TableHeader,
    TablePagination,
    TableOperators,
    ResultReadOnly,
    ResourceRule
  },
  data() {
    return {
      result: {},
      loading: false,
      situationInfo: {},
      resourceSummary:[],
      condition: {
        components: SITUATION_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
    }
  },
  props: {
    selectNodeIds: Array,
  },
  watch: {
    selectNodeIds() {
      this.searchByAccount();
    },
    batchReportId() {
    }
  },
  methods: {
    init() {
      this.initAccount()
    },

    searchByAccount() {
      let accountId = ""
      if (!!this.selectNodeIds[0]) {
       accountId = this.selectNodeIds[0];
      }
      this.result = this.$get("/cloud/resource/summary?accountId="+accountId, response => {
        let data = response.data;
        this.resourceSummary = data;
      });
      if(!!accountId){
        this.condition["combine"] = {"accountId":{"value":accountId}}
      }else {
        this.condition["combine"] = {}
      }
      this.search()
    },


    resourceTypeClick(resourceType){
      let accountId = ""
      if (!!this.selectNodeIds[0]) {
        accountId = this.selectNodeIds[0];
      }
      if(!!accountId){
        this.condition["combine"]["accountId"]= {"value":accountId}
      }
      this.condition["combine"]["resourceType"]={"value":resourceType}
      this.condition.resourceType = resourceType;
      this.search()
    },

    search() {
      let url = "/cloud/resource/list/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
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
    getAccountName(accountId){
      let result =  this.accountList.filter(item =>{
        return item.id === accountId
      })
      return result.length >0?result[0].name:""
    },
    initAccount() {
      this.$get("/account/allList", response => {
        this.accountList = response.data
        this.searchByAccount()
      })
    },
    tableRowClassName({row, rowIndex}) {
      if (rowIndex % 4 === 0) {
        return 'success-row';
      } else if (rowIndex % 2 === 0) {
        return 'warning-row';
      } else {
        return '';
      }
    },
  },
  activated() {
    this.init();
  }
}
</script>

<style scoped>
.table-card {
  min-height: 10%;
}
.hr-card-index-1 .hr-card-data-digital {
  color: #0051a4;
}

.hr-card-index-1 {
  border-left-color: #0051a4;
  border-left-width: 3px;
}

.hr-card-index-2 .hr-card-data-digital {
  color: #65A2FF;
}

.hr-card-index-2 {
  border-left-color: #65A2FF;
  border-left-width: 3px;
}

.hr-card-index-3 .hr-card-data-digital {
  color: #E69147;
}

.hr-card-index-3 {
  border-left-color: #E69147;
  border-left-width: 3px;
}

.hr-card-index-4 .hr-card-data-digital {
  color: #E6113C;
}

.hr-card-index-4 {
  border-left-color: #E6113C;
  border-left-width: 3px;
}

.hr-card-index-5 .hr-card-data-digital {
  color: #44B349;
}

.hr-card-index-5 {
  border-left-color: #44B349;
  border-left-width: 3px;
}

.hr-card-data {
  text-align: left;
  display: block;
  margin-bottom: 5px;
}

.hr-card-desc {
  display: block;
  text-align: left;
}

.hr-card-data-digital {
  font-size: 21px;
}

.hr-card-data-unit {
  color: #8492a6;
  font-size: 10px;
}

.margin-top {
  padding: 15px 25px 0 25px;
}

.content >>> .my-label {
  font-size: 14px;
  background: #ebf5ff;
  text-align: center;
}

.content >>> .my-content {
  color: red;
  font-size: 10px;
  text-align: center;
}

</style>
