<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition"
                      @search="search"
                      :title="$t('dashboard.active_list')"/>
      </template>

      <el-table border :data="tableData"
                class="adjust-table table-content"
                @sort-change="sort"
                :row-class-name="tableRowClassName"
                @filter-change="filter">
        <el-table-column type="index" min-width="5%"/>
        <el-table-column prop="resourceUserName" :label="$t('dashboard.resource_user_name')" min-width="15%" show-overflow-tooltip></el-table-column>
        <el-table-column prop="resourceUserId" :label="$t('dashboard.resource_user_id')" min-width="10%" show-overflow-tooltip></el-table-column>
        <el-table-column :label="$t('commons.operating')" min-width="10%" show-overflow-tooltip>
          <template v-slot:default="scope">
            {{ $t(scope.row.operation) }}
          </template>
        </el-table-column>
        <el-table-column :label="$t('commons.description')" min-width="15%" show-overflow-tooltip>
          <template v-slot:default="scope">
            {{ scope.row.message }}
          </template>
        </el-table-column>
        <el-table-column prop="resourceName" :label="$t('dashboard.resource_name')" min-width="10%" show-overflow-tooltip></el-table-column>
        <el-table-column :label="$t('dashboard.resource_type')" min-width="10%" show-overflow-tooltip>
          <template v-slot:default="scope">
            {{ $t(scope.row.resourceType) }}
          </template>
        </el-table-column>
        <el-table-column prop="sourceIp" :label="$t('dashboard.source_ip')" min-width="10%" show-overflow-tooltip>
          <template v-slot:default="scope">
            <span> {{ scope.row.sourceIp?scope.row.sourceIp:'--' }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="15%" :label="$t('dashboard.time')" sortable prop="time">
          <template v-slot:default="scope">
            <span><i class="el-icon-time"></i> {{ scope.row.time | timestampFormatDate }}</span>
          </template>
        </el-table-column>
      </el-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>
  </main-container>
</template>
<script>

import MainContainer from "../../common/components/MainContainer";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import Container from "../../common/components/Container";
import {_filter, _sort} from "@/common/js/utils";
import TableHeader from "../head/TableHeader";
/* eslint-disable */
  export default {
    name: "active",
    components: {
      MainContainer,
      TablePagination,
      TableOperator,
      TableHeader,
      Container,
    },
    data() {
      return {
        result: {},
        loading: false,
        tableData: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        condition: {
        },
      }
    },
    methods: {
      sort(column) {
        _sort(column, this.condition);
        this.init();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.init();
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
      //查询列表
      search() {
        let url = "/log/operation/query/resource/" + this.currentPage + "/" + this.pageSize;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
      },
    },
    created() {
      this.search();
    }
  }

</script>

<style scoped>
  .rs-container >>> span.title {
    font-size: 16px;
    font-weight: 500;
    margin-top: 0;
    text-overflow: ellipsis;
    overflow: hidden;
    word-wrap: break-word;
    white-space: nowrap;
  }
  .rs-main-container {
    padding: 15px;
    height: calc(100vh - 80px);
  }
</style>
