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
                        @back="back" :backTip="$t('code.back_resource')"
                        :title="$t('code.result_details_list')"/>
        </template>

        <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" @filter-change="filter">
          <el-table-column type="index" min-width="2%"/>
          <el-table-column min-width="10%" :label="'PkgName'" prop="pkgName" v-slot:default="scope">
            <span style="font-weight:bold;color: #000000;">{{ scope.row.pkgName }}</span>
          </el-table-column>
          <el-table-column min-width="10%" :label="'VulnerabilityID'" prop="vulnerabilityId">
          </el-table-column>
          <el-table-column min-width="7%" :label="'Severity'" prop="severity" v-slot:default="scope">
            <span v-if="scope.row.severity === 'CRITICAL'" style="color: #8B0000;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'HIGH'" style="color: #FF4D4D;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'MEDIUM'" style="color: #FF8000;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'LOW'" style="color: #336D9F;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'UNKNOWN'" style="color: #67C23A;">{{ scope.row.severity }}</span>
          </el-table-column>
          <el-table-column :label="'InstalledVersion'" min-width="10%" prop="installedVersion">
          </el-table-column>
          <el-table-column min-width="10%" :label="'FixedVersion'" prop="fixedVersion">
          </el-table-column>
          <el-table-column min-width="25%" :label="'PrimaryURL'" prop="primaryUrl" v-slot:default="scope">
            <span>{{ scope.row.title }}</span>
            <br>
            <el-link type="primary" style="color: #0000e4;" :href="scope.row.primaryUrl" target="_blank">{{ scope.row.primaryUrl }}</el-link>
          </el-table-column>
          <el-table-column min-width="5%" :label="$t('commons.operating')" fixed="right">
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
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import TableHeader from "../head/DetailTableHeader";
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
        buttons: [
          {
            tip: this.$t('resource.scan_vuln_search'), icon: "el-icon-share", type: "primary",
            exec: this.handleVuln
          },
        ],
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
      handleVuln() {
        window.open('http://www.cnnvd.org.cn/web/vulnerability/querylist.tag','_blank','');
      },
      sort(column) {
        _sort(column, this.condition);
        this.init();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.init();
      },
      search () {
        let url = "/code/resultItemList/" + this.currentPage + "/" + this.pageSize;
        this.condition.resultId = this.resultId;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
        this.result = this.$get("/sbom/codeMetricChart/"+ this.resultId, response => {
          this.content = response.data;
        });
      },
      init() {
        this.resultId = this.$route.params.id;
        this.search();
      },
      back () {
        let path = this.$route.path;
        if (path.indexOf("/code") >= 0) {
          this.$router.push({
            path: '/code/result',
          }).catch(error => error);
        } else if (path.indexOf("/resource") >= 0) {
          this.$router.push({
            path: '/resource/codeResult',
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
