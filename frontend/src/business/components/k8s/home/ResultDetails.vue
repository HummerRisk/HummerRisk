<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">

        <template v-slot:header>
          <table-header :condition.sync="condition"
                        @search="search"
                        :show-back="true"
                        @back="back" :backTip="$t('k8s.back_resource')"
                        :title="$t('k8s.result_details_list')"/>
        </template>

        <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" @filter-change="filter">
          <el-table-column type="index" min-width="2%"/>
          <el-table-column :label="'Title'" min-width="20%" prop="title" v-slot:default="scope">
            {{ scope.row.title?scope.row.title:'N/A' }}
          </el-table-column>
          <el-table-column :label="'VulnerabilityID'" min-width="13%" prop="vulnerabilityId" v-slot:default="scope">
            {{ scope.row.vulnerabilityId?scope.row.vulnerabilityId:'N/A' }}
          </el-table-column>
          <el-table-column min-width="8%" :label="'Severity'" prop="severity" v-slot:default="scope">
            {{ scope.row.severity?scope.row.severity:'N/A' }}
          </el-table-column>
          <el-table-column min-width="8%" :label="'Score'" prop="score" v-slot:default="scope">
            {{ scope.row.score?scope.row.score:'N/A' }}
          </el-table-column>
          <el-table-column min-width="20%" :label="'PrimaryLink'" prop="primaryLink" v-slot:default="scope">
            <el-link type="primary" style="color: #0000e4;" :href="scope.row.primaryLink" target="_blank">{{ scope.row.primaryLink?scope.row.primaryLink:'N/A' }}</el-link>
          </el-table-column>
          <el-table-column min-width="15%" :label="'InstalledVersion'" prop="installedVersion" v-slot:default="scope">
            {{ scope.row.installedVersion?scope.row.installedVersion:'N/A' }}
          </el-table-column>
          <el-table-column min-width="15%" :label="'FixedVersion'" prop="fixedVersion" v-slot:default="scope">
            {{ scope.row.fixedVersion?scope.row.fixedVersion:'N/A' }}
          </el-table-column>
          <el-table-column min-width="10%" :label="$t('commons.operating')" fixed="right">
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
      }
    },
    props: ["id"],
    methods: {
      handleVuln() {
        window.open('http://www.cnnvd.org.cn/web/vulnerability/queryLds.tag','_blank','');
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
        let url = "/k8s/resultItemList/" + this.currentPage + "/" + this.pageSize;
        this.condition.resultId = this.resultId;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
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
            path: '/resource/k8sResult',
          }).catch(error => error);
        }
      },
    },
    created() {
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
