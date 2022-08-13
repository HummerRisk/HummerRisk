<template>
  <div v-loading="result.loading">

    <el-card class="table-card">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search" :title="$t('system.plugin')"/>
      </template>

      <el-table border class="adjust-table" :data="tableData" style="width: 100%" :row-class-name="tableRowClassName">
        <el-table-column type="index" min-width="2%"/>
        <el-table-column prop="id" :label="$t('system.plugin_id')" min-width="20%"/>
        <el-table-column :label="$t('system.plugin_name')" min-width="15%" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ $t(scope.row.name) }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="scanType" :label="$t('system.plugin_scan_type')" min-width="13%"/>
        <el-table-column prop="type" v-slot:default="scope" :label="$t('system.plugin_type')" min-width="12%">
          <span v-if="scope.row.type == 'cloud'" style="color: #f84846;"> {{ $t('system.cloud') }}</span>
          <span v-else-if="scope.row.type == 'vuln'" style="color: #fe9636;"> {{ $t('system.vuln') }}</span>
          <span v-else-if="scope.row.type == 'native'" style="color: #4dabef;"> {{ $t('system.native') }}</span>
          <span v-else> N/A</span>
        </el-table-column>
        <el-table-column prop="order" :label="$t('system.plugin_order')" min-width="8%"/>
        <el-table-column prop="updateTime" :label="$t('commons.update_time')" min-width="20%">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
      </el-table>

      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize"
                           :total="total"/>
    </el-card>

  </div>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "../head/PluginTableHeader";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../../common/components/DialogFooter";
/* eslint-disable */
  export default {
    name: "Plugin",
    components: {
      TablePagination,
      TableHeader,
      TableOperator,
      DialogFooter,
    },
    data() {
      return {
        queryPath: '/plugin/list',
        result: {},
        createVisible: false,
        updateVisible: false,
        multipleSelection: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        condition: {},
        tableData: [],
        form: {
        },
        direction: 'rtl',
      }
    },
    activated() {
      this.search();
    },
    methods: {
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
      tableRowClassName({row, rowIndex}) {
        if (rowIndex%4 === 0) {
          return 'success-row';
        } else if (rowIndex%2 === 0) {
          return 'warning-row';
        } else {
          return '';
        }
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
