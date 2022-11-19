<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('account.cloud_account_list')"
                      @create="create" :createTip="$t('account.create')"
                      :show-validate="false" :show-create="true"/>

      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort"
                :row-class-name="tableRowClassName"
                @filter-change="filter" @select-all="select" @select="select">
        <el-table-column type="selection" min-width="2%">
        </el-table-column>
        <el-table-column type="index" min-width="2%"/>
        <el-table-column prop="name" :label="$t('account.name')" min-width="12%" show-overflow-tooltip></el-table-column>
        <el-table-column :label="$t('account.cloud_platform')" min-width="10%" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.pluginName }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" min-width="8%" :label="$t('account.status')"
                         column-key="status"
                         :filters="statusFilters"
                         :filter-method="filterStatus">
          <template v-slot:default="{row}">
            <account-status @search="search" :row="row"/>
          </template>
        </el-table-column>
        <el-table-column min-width="15%" :label="$t('account.create_time')" sortable
                         prop="createTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="15%" :label="$t('account.update_time')" sortable
                         prop="updateTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" :label="$t('account.creator')" min-width="8%" show-overflow-tooltip/>
        <el-table-column :label="$t('account.regions')" min-width="7%">
          <template v-slot:default="scope">
            <regions :row="scope.row"/>
          </template>
        </el-table-column>
        <el-table-column min-width="15%" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
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
import Regions from "@/business/components/account/home/Regions";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import ProxyDialogFooter from "@/business/components/common/components/ProxyDialogFooter";
import ProxyDialogCreateFooter from "@/business/components/common/components/ProxyDialogCreateFooter";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {ACCOUNT_CONFIGS} from "@/business/components/common/components/search/search-components";

/* eslint-disable */
export default {
  components: {
    TableOperators,
    Regions,
    MainContainer,
    Container,
    TableHeader,
    TablePagination,
    DialogFooter,
    ProxyDialogFooter,
    ProxyDialogCreateFooter
  },
  data() {
    return {
      credential: {},
      result: {},
      cloudResult: {},
      groupResult: {},
      condition: {
        components: ACCOUNT_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
    }
  },
  methods: {
    //查询列表
    search() {
      let url = "/oss/list/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleClose() {
      this.createVisible =  false;
      this.updateVisible =  false;
      this.handleScan = false;
    },
    showRegions (tmp) {
      this.regions = tmp.regions;
    },
    //查询插件
    activePlugin() {
      let url = "/plugin/cloud";
      this.result = this.$get(url, response => {
        let data = response.data;
        this.plugins =  data;
      });
    },
    //查询代理
    activeProxy() {
      let url = "/proxy/list/all";
      this.result = this.$get(url, response => {
        this.proxys = response.data;
      });
    },
    init() {
      this.search();
    },
    sort(column) {
      _sort(column, this.condition);
      this.init();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.init();
    },
    filterStatus(value, row) {
      return row.status === value;
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
    create() {
      this.addAccountForm = [ { "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] } ];
      this.createVisible = true;
      this.activePlugin();
      this.activeProxy();
    },
  },
  created() {
    this.init();
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
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  padding: 10px 10%;
  width: 47%;
}

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

