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
                        :title="$t('k8s.result_kubench_details_list')"
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
          <el-table-column min-width="120" v-if="checkedColumnNames.includes('number')" :label="'Number'" prop="number" v-slot:default="scope">
            <span style="font-weight:bold;color: #000000;">{{ scope.row.number }}</span>
          </el-table-column>
          <el-table-column min-width="70" v-if="checkedColumnNames.includes('checkId')" :label="'CheckID'" prop="checkId">
          </el-table-column>
          <el-table-column min-width="70" v-if="checkedColumnNames.includes('severity')" :label="'Severity'" prop="severity" v-slot:default="scope">
            <span v-if="scope.row.severity === 'CRITICAL'" style="color: #8B0000;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'HIGH'" style="color: #FF4D4D;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'MEDIUM'" style="color: #FF8000;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'LOW'" style="color: #336D9F;">{{ scope.row.severity }}</span>
            <span v-if="scope.row.severity === 'UNKNOWN'" style="color: #67C23A;">{{ scope.row.severity }}</span>
          </el-table-column>
          <el-table-column min-width="120" v-if="checkedColumnNames.includes('category')" :label="'Category'" prop="category" v-slot:default="scope">
            {{ scope.row.category?scope.row.category:'N/A' }}
          </el-table-column>
          <el-table-column min-width="250" v-if="checkedColumnNames.includes('description')" :label="'Description'" prop="description" v-slot:default="scope">
            <span>{{ scope.row.description }}</span>
          </el-table-column>
          <el-table-column min-width="70" :label="'Success'" prop="success" v-slot:default="scope" fixed="right">
            {{ scope.row.success?scope.row.success:'N/A' }}
          </el-table-column>
        </hide-table>
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
import {_filter, _sort} from "@/common/js/utils";
import MetricChart from "@/business/components/common/chart/MetricChart";
import { DETAIL_RESULT_CONFIGS } from "@/business/components/common/components/search/search-components";
import HideTable from "@/business/components/common/hideTable/HideTable";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'Number',
    props: 'number',
    disabled: false
  },
  {
    label: 'CheckID',
    props: 'checkId',
    disabled: false
  },
  {
    label: 'Severity',
    props: 'severity',
    disabled: false
  },
  {
    label: 'Category',
    props: 'category',
    disabled: false
  },
  {
    label: 'Description',
    props: 'description',
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
            name: 'Number',
            id: 'number',
          },
          {
            name: 'CheckID',
            id: 'checkId',
          },
          {
            name: 'Severity',
            id: 'severity',
          },
          {
            name: 'Category',
            id: 'category',
          },
          {
            name: 'Description',
            id: 'description',
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
      sort(column) {
        _sort(column, this.condition);
        this.init();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.init();
      },
      search () {
        let url = "/k8s/resultConfigItemListBySearch/" + this.currentPage + "/" + this.pageSize;
        this.condition.resultId = this.resultId;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
        this.result = this.$get("/k8s/metricConfigChart/"+ this.resultId, response => {
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
