<template>
  <div v-loading="result.loading">

    <el-card class="table-card">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search" :title="$t('system.plugin')"
                      :items="items" :columnNames="columnNames" :show-open="false"
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
        <el-table-column prop="id" v-if="checkedColumnNames.includes('id')" :label="$t('system.plugin_id')" min-width="150"/>
        <el-table-column :label="$t('system.plugin_name')" v-if="checkedColumnNames.includes('name')" min-width="130" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="scanType" v-if="checkedColumnNames.includes('scanType')" :label="$t('system.plugin_scan_type')" min-width="90"/>
        <el-table-column prop="type" v-if="checkedColumnNames.includes('type')" v-slot:default="scope" :label="$t('system.plugin_type')" min-width="90">
          <span v-if="scope.row.type == 'cloud'" style="color: #f84846;"> {{ $t('system.cloud') }}</span>
          <span v-else-if="scope.row.type == 'vuln'" style="color: #fe9636;"> {{ $t('system.vuln') }}</span>
          <span v-else-if="scope.row.type == 'native'" style="color: #4dabef;"> {{ $t('system.native') }}</span>
          <span v-else> N/A</span>
        </el-table-column>
        <el-table-column prop="order" v-if="checkedColumnNames.includes('order')" :label="$t('system.plugin_order')" min-width="60"/>
        <el-table-column prop="updateTime" v-if="checkedColumnNames.includes('updateTime')" :label="$t('commons.update_time')" min-width="150">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
      </hide-table>

      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

  </div>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../../common/components/DialogFooter";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {PLUGIN_CONFIGS} from "../../common/components/search/search-components";
import TableHeader from "@/business/components/common/components/TableHeader";
import {_filter, _sort} from "@/common/js/utils";
import {pluginListUrl} from "@/api/system/system";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'system.plugin_id',
    props: 'id',
    disabled: false
  },
  {
    label: 'system.plugin_name',
    props: 'name',
    disabled: false
  },
  {
    label: 'system.plugin_scan_type',
    props: 'scanType',
    disabled: false
  },
  {
    label: 'system.plugin_type',
    props: 'type',
    disabled: false
  },
  {
    label: 'system.plugin_order',
    props: 'order',
    disabled: false
  },
  {
    label: 'commons.update_time',
    props: 'updateTime',
    disabled: false
  },
];

/* eslint-disable */
  export default {
    name: "Plugin",
    components: {
      TablePagination,
      TableHeader,
      TableOperator,
      DialogFooter,
      HideTable,
    },
    data() {
      return {
        queryPath: pluginListUrl,
        result: {},
        createVisible: false,
        updateVisible: false,
        multipleSelection: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        condition: {
          components: PLUGIN_CONFIGS
        },
        tableData: [],
        form: {
        },
        direction: 'rtl',
        //名称搜索
        items: [
          {
            name: 'system.plugin_id',
            id: 'id',
          },
          {
            name: 'system.plugin_name',
            id: 'name',
          },
          {
            name: 'system.plugin_scan_type',
            id: 'scanType',
          },
          {
            name: 'system.plugin_type',
            id: 'type',
          },
        ],
        checkedColumnNames: columnOptions.map((ele) => ele.props),
        columnNames: columnOptions,
        checkAll: true,
        isIndeterminate: false,
      }
    },
    activated() {
      this.search();
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
        this.search();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.search();
      },
      search() {
        this.result = this.$post(this.buildPagePath(this.queryPath), this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        })
      },
      buildPagePath(path) {
        return path + "/" + this.currentPage + "/" + this.pageSize;
      },
    }
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
    width: 100%;
  }
  .rtl >>> .el-form-item__content {
    width: 80%;
  }
  /deep/ :focus{outline:0;}
</style>
