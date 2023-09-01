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
                        @back="back" :backTip="$t('fs.back_resource')"
                        :title="$t('fs.result_details_list')"
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
          <el-table-column min-width="120" v-if="checkedColumnNames.includes('pkgName')" :label="'PkgName'" prop="pkgName" v-slot:default="scope">
            <span style="font-weight:bold;color: #000000;">{{ scope.row.pkgName }}</span>
          </el-table-column>
          <el-table-column min-width="120" v-if="checkedColumnNames.includes('vulnerabilityId')" :label="'VulnerabilityID'" prop="vulnerabilityId">
          </el-table-column>
          <el-table-column min-width="70" v-if="checkedColumnNames.includes('severity')" :label="'Severity'" prop="severity" v-slot:default="scope">
            <span v-if="scope.row.severity === 'CRITICAL'" style="color: #8B0000;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'HIGH'" style="color: #FF4D4D;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'MEDIUM'" style="color: #FF8000;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'LOW'" style="color: #336D9F;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'UNKNOWN'" style="color: #67C23A;">{{ scope.row.severity }}</span>
          </el-table-column>
          <el-table-column :label="'InstalledVersion'" v-if="checkedColumnNames.includes('installedVersion')" min-width="110" prop="installedVersion">
          </el-table-column>
          <el-table-column min-width="110" v-if="checkedColumnNames.includes('fixedVersion')" :label="'FixedVersion'" prop="fixedVersion">
          </el-table-column>
          <el-table-column min-width="250" v-if="checkedColumnNames.includes('primaryUrl')" :label="'PrimaryURL'" prop="primaryUrl" v-slot:default="scope">
            <span>{{ scope.row.title }}</span>
            <br>
            <el-link type="primary" style="color: #0000e4;" :href="scope.row.primaryUrl" target="_blank">{{ scope.row.primaryUrl }}</el-link>
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
import {fsMetricChartUrl, fsResultItemListBySearchUrl} from "@/api/k8s/fs/fs";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'PkgName',
    props: 'pkgName',
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
    label: 'PrimaryURL',
    props: 'primaryUrl',
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
        checkedColumnNames: columnOptions.map((ele) => ele.props),
        columnNames: columnOptions,
        //名称搜索
        items: [
          {
            name: 'PkgName',
            id: 'pkgName',
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
            name: 'InstalledVersion',
            id: 'installedVersion',
          },
          {
            name: 'FixedVersion',
            id: 'fixedVersion',
          },
          {
            name: 'PrimaryURL',
            id: 'primaryUrl',
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
        let url = fsResultItemListBySearchUrl + this.currentPage + "/" + this.pageSize;
        this.condition.resultId = this.resultId;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
        this.result = this.$get(fsMetricChartUrl + this.resultId, response => {
          this.content = response.data;
        });
      },
      init() {
        this.resultId = this.$route.params.id;
        this.search();
      },
      back () {
        let path = this.$route.path;
        if (path.indexOf("/fs") >= 0) {
          this.$router.push({
            path: '/fs/fs',
          }).catch(error => error);
        } else if (path.indexOf("/resource") >= 0) {
          this.$router.push({
            path: '/resource/fs-result',
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
