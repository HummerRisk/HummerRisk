<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition"
                      @search="search"
                      :title="$t('dashboard.active_list')"
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
        <el-table-column type="index" min-width="50"/>
        <el-table-column prop="resourceUserName" v-if="checkedColumnNames.includes('resourceUserName')" :label="$t('dashboard.resource_user_name')" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="resourceUserId" v-if="checkedColumnNames.includes('resourceUserId')" :label="$t('dashboard.resource_user_id')" min-width="80" show-overflow-tooltip></el-table-column>
        <el-table-column :label="$t('commons.description')" v-if="checkedColumnNames.includes('message')"  min-width="120" show-overflow-tooltip>
          <template v-slot:default="scope">
            {{ scope.row.message }}
          </template>
        </el-table-column>
        <el-table-column prop="resourceName" v-if="checkedColumnNames.includes('resourceName')" :label="$t('dashboard.resource_name')" min-width="120" show-overflow-tooltip></el-table-column>
        <el-table-column :label="$t('dashboard.resource_type')" v-if="checkedColumnNames.includes('resourceType')" min-width="100" show-overflow-tooltip>
          <template v-slot:default="scope">
            {{ $t(scope.row.resourceType) }}
          </template>
        </el-table-column>
        <el-table-column prop="sourceIp" v-if="checkedColumnNames.includes('sourceIp')" :label="$t('dashboard.source_ip')" min-width="100" show-overflow-tooltip>
          <template v-slot:default="scope">
            <span> {{ scope.row.sourceIp?scope.row.sourceIp:'--' }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="170" v-if="checkedColumnNames.includes('time')" :label="$t('dashboard.time')" sortable prop="time">
          <template v-slot:default="scope">
            <span><i class="el-icon-time"></i> {{ scope.row.time | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="checkedColumnNames.includes('operation')" :label="$t('commons.operating')" fixed="right" min-width="100" show-overflow-tooltip>
          <template v-slot:default="scope">
            {{ $t(scope.row.operation) }}
          </template>
        </el-table-column>
      </hide-table>
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
import TableHeader from "@/business/components/common/components/TableHeader";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {ACTIVE_CONFIGS} from "../../common/components/search/search-components";

const columnOptions = [
  {
    label: 'dashboard.resource_user_name',
    props: 'resourceUserName',
    disabled: false
  },
  {
    label: 'dashboard.resource_user_id',
    props: 'resourceUserId',
    disabled: false
  },
  {
    label: 'commons.description',
    props: 'message',
    disabled: false
  },
  {
    label: 'dashboard.resource_name',
    props: 'resourceName',
    disabled: false
  },
  {
    label: 'dashboard.resource_type',
    props: 'resourceType',
    disabled: false
  },
  {
    label: 'dashboard.source_ip',
    props: 'sourceIp',
    disabled: false
  },
  {
    label: 'dashboard.time',
    props: 'time',
    disabled: false
  },
  {
    label: 'commons.operating',
    props: 'operation',
    disabled: false
  },
];

/* eslint-disable */
  export default {
    name: "active",
    components: {
      MainContainer,
      TablePagination,
      TableOperator,
      TableHeader,
      Container,
      HideTable,
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
          components: ACTIVE_CONFIGS
        },
        items: [
          {
            name: 'dashboard.resource_user_name',
            id: 'resourceUserName'
          },
          {
            name: 'dashboard.resource_user_id',
            id: 'resourceUserId'
          },
          {
            name: 'commons.description',
            id: 'message'
          },
          {
            name: 'dashboard.resource_name',
            id: 'resourceName'
          },
          {
            name: 'dashboard.source_ip',
            id: 'sourceIp'
          }
        ],
        checkedColumnNames: columnOptions.map((ele) => ele.props),
        columnNames: columnOptions,
        checkAll: true,
        isIndeterminate: false,
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
      select(selection) {
      },
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
  .hr-container >>> span.title {
    font-size: 16px;
    font-weight: 500;
    margin-top: 0;
    text-overflow: ellipsis;
    overflow: hidden;
    word-wrap: break-word;
    white-space: nowrap;
  }
  .hr-main-container {
    padding: 15px;
    height: calc(100vh - 80px);
  }
</style>
