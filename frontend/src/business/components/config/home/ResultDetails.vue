<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">

        <section class="report-container">
          <main>
            <metric-chart :content="content"/>
          </main>
        </section>

        <template v-slot:header>
          <table-header :condition.sync="condition"
                        @search="search"
                        :show-back="true"
                        @back="back" :backTip="$t('config.back_resource')"
                        :title="$t('config.config_result_details_list')"/>
        </template>

        <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" @filter-change="filter">
          <el-table-column type="index" min-width="2%"/>
          <el-table-column :label="'ID'" min-width="5%" prop="itemId" v-slot:default="scope">
            <span style="font-weight:bold;color: #000000;">{{ scope.row.itemId?scope.row.itemId:'N/A' }}</span>
          </el-table-column>
          <el-table-column :label="'Type'" min-width="11%" prop="type" v-slot:default="scope">
            {{ scope.row.type?scope.row.type:'N/A' }}
          </el-table-column>
          <el-table-column min-width="7%" :label="'Severity'" prop="severity" v-slot:default="scope">
            <span v-if="scope.row.severity === 'CRITICAL'" style="color: #8B0000;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'HIGH'" style="color: #FF4D4D;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'MEDIUM'" style="color: #FF8000;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'LOW'" style="color: #336D9F;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'UNKNOWN'" style="color: #67C23A;">{{ scope.row.severity }}</span>
          </el-table-column>
          <el-table-column min-width="5%" :label="'Status'" prop="status" v-slot:default="scope">
            {{ scope.row.status?scope.row.status:'N/A' }}
          </el-table-column>
          <el-table-column :label="'Title'" min-width="15%" prop="title" v-slot:default="scope">
            {{ scope.row.title?scope.row.title:'N/A' }}
          </el-table-column>
          <el-table-column min-width="25%" :label="'PrimaryURL'" prop="primaryUrl" v-slot:default="scope">
            <span>{{ scope.row.resolution }}</span>
            <br>
            <el-link type="primary" style="color: #0000e4;" :href="scope.row.primaryUrl" target="_blank">{{ scope.row.primaryUrl }}</el-link>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
      </el-card>
    </main-container>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import TableHeader from "@/business/components/common/components/DetailTableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../../common/components/RuleDialogFooter";
import CenterChart from "../../common/components/CenterChart";
import {_filter, _sort} from "@/common/js/utils";
import RuleType from "@/business/components/image/home/RuleType";
import MetricChart from "@/business/components/common/chart/MetricChart";

/* eslint-disable */
  export default {
    name: "ResultDetails",
    components: {
      TableOperators,
      MainContainer,
      Container,
      TableHeader,
      TablePagination,
      TableOperator,
      DialogFooter,
      CenterChart,
      RuleType,
      MetricChart,
    },
    data() {
      return {
        result: {},
        tableData: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        condition: {
        },
        tagSelect: [],
        resourceTypes: [],
        direction: 'rtl',
        buttons: [
        ],
        cmOptions: {
          tabSize: 4,
          mode: {
            name: 'shell',
            json: true
          },
          theme: 'bespin',
          lineNumbers: true,
          line: true,
          indentWithTabs: true,
        },
        resultId: "",
        content: {
          critical: 0,
          high: 0,
          medium: 0,
          low: 0,
          unknown: 0,
          total: 0,
        },
      }
    },
    props: ["id"],
    methods: {
      sort(column) {
        _sort(column, this.condition);
        this.init();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.init();
      },
      search () {
        let url = "/config/resultItemList/" + this.currentPage + "/" + this.pageSize;
        this.condition.resultId = this.resultId;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
        this.result = this.$get("/config/metricChart/"+ this.resultId, response => {
          this.content = response.data;
        });
      },
      init() {
        this.resultId = this.$route.params.id;
        this.search();
      },
      back () {
        let path = this.$route.path;
        if (path.indexOf("/config") >= 0) {
          this.$router.push({
            path: '/config/result',
          }).catch(error => error);
        } else if (path.indexOf("/resource") >= 0) {
          this.$router.push({
            path: '/resource/ConfigResult',
          }).catch(error => error);
        }
      },
    },
    activated() {
      this.init();
    }
  }
</script>

<style scoped>
  .el-form-tag-b {
    margin: 0 15px;
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
  /deep/ :focus{outline:0;}
</style>
