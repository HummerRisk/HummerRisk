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
                        @back="back" :backTip="$t('k8s.back_resource')"
                        :title="$t('k8s.result_details_list')"
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
          :tableRow="false"
        >
          <el-table-column type="index" min-width="40"/>
          <el-table-column min-width="110" v-if="checkedColumnNames.includes('resource')" :label="'Resource'" prop="resource" v-slot:default="scope">
            <span style="font-weight:bold;color: #000000;">{{ scope.row.resource }}</span>
          </el-table-column>
          <el-table-column min-width="120" v-if="checkedColumnNames.includes('vulnerabilityId')" :label="'VulnerabilityID'" prop="vulnerabilityId">
          </el-table-column>
          <el-table-column min-width="80" v-if="checkedColumnNames.includes('severity')" :label="'Severity'" prop="severity" v-slot:default="scope">
            <span v-if="scope.row.severity === 'CRITICAL'" style="color: #8B0000;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'HIGH'" style="color: #FF4D4D;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'MEDIUM'" style="color: #FF8000;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'LOW'" style="color: #336D9F;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'UNKNOWN'" style="color: #67C23A;">{{ scope.row.severity }}</span>
          </el-table-column>
          <el-table-column min-width="60" v-if="checkedColumnNames.includes('score')" :label="'Score'" prop="score" v-slot:default="scope">
            {{ scope.row.score?scope.row.score:'N/A' }}
          </el-table-column>
          <el-table-column :label="'InstalledVersion'" v-if="checkedColumnNames.includes('installedVersion')" min-width="120" prop="installedVersion">
          </el-table-column>
          <el-table-column min-width="110" v-if="checkedColumnNames.includes('fixedVersion')" :label="'FixedVersion'" prop="fixedVersion">
          </el-table-column>
          <el-table-column min-width="250" v-if="checkedColumnNames.includes('primaryLink')" :label="'PrimaryLink'" prop="primaryLink" v-slot:default="scope">
            <span>{{ scope.row.title }}</span>
            <br>
            <el-link type="primary" style="color: #0000e4;" :href="scope.row.primaryLink" target="_blank">{{ scope.row.primaryLink }}</el-link>
          </el-table-column>
          <el-table-column min-width="50" :label="$t('commons.operating')" fixed="right">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </hide-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
      </el-card>
    </main-container>
</template>

<script>
import TableOperators from "@/business/components/common/components/TableOperators";
import MainContainer from "@/business/components/common/components/MainContainer";
import Container from "@/business/components/common/components/Container";
import TableHeader from "@/business/components/common/components/DetailTableHeader";
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableOperator from "@/business/components/common/components/TableOperator";
import DialogFooter from "@/business/components/common/components/RuleDialogFooter";
import CenterChart from "@/business/components/common/components/CenterChart";
import {_filter, _sort} from "@/common/js/utils";
import MetricChart from "@/business/components/common/chart/MetricChart";
import {DETAIL_RESULT_CONFIGS} from "@/business/components/common/components/search/search-components";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {k8sMetricChartUrl, resultItemListBySearchUrl} from "@/api/k8s/k8s/k8s";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'Resource',
    props: 'resource',
    disabled: false
  },
  {
    label: 'VulnerabilityID',
    props: 'vulnerabilityId',
    disabled: false
  },
  {
    label: 'Severity',
    props: 'severity',
    disabled: false
  },
  {
    label: 'Score',
    props: 'score',
    disabled: false
  },
  {
    label: 'InstalledVersion',
    props: 'installedVersion',
    disabled: false
  },
  {
    label: 'FixedVersion',
    props: 'fixedVersion',
    disabled: false
  },
  {
    label: 'PrimaryLink',
    props: 'primaryLink',
    disabled: false
  },
];

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
      MetricChart,
      HideTable,
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
          components: DETAIL_RESULT_CONFIGS
        },
        tagSelect: [],
        resourceTypes: [],
        direction: 'rtl',
        buttons: [
          {
            tip: this.$t('resource.scan_vuln_search'), icon: "el-icon-share", type: "primary",
            exec: this.handleVuln
          },
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
        checkedColumnNames: columnOptions.map((ele) => ele.props),
        columnNames: columnOptions,
        //名称搜索
        items: [
          {
            name: 'Resource',
            id: 'resource',
          },
          {
            name: 'VulnerabilityID',
            id: 'vulnerabilityId',
          },
          {
            name: 'Severity',
            id: 'severity',
          },
          {
            name: 'Score',
            id: 'score',
          },
          {
            name: 'InstalledVersion',
            id: 'installedVersion',
          },
          {
            name: 'FixedVersion',
            id: 'fixedVersion',
          },
          {
            name: 'PrimaryLink',
            id: 'primaryLink',
          },
        ],
        checkAll: true,
        isIndeterminate: false,
      }
    },
    props: ["id"],
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
      handleVuln() {
        window.open('http://www.cnnvd.org.cn/','_blank','');
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
        let url = resultItemListBySearchUrl + this.currentPage + "/" + this.pageSize;
        this.condition.resultId = this.resultId;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
        this.result = this.$get(k8sMetricChartUrl + this.resultId, response => {
          this.content = response.data;
        });
      },
      init() {
        this.resultId = this.$route.params.id;
        this.search();
      },
      back () {
        let path = this.$route.path;
        if (path.indexOf("/k8s") >= 0) {
          this.$router.push({
            path: '/k8s/result',
          }).catch(error => error);
        } else if (path.indexOf("/resource") >= 0) {
          this.$router.push({
            path: '/resource/K8sResult',
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
