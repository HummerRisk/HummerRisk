<template>
    <div>

        <table-header :condition.sync="condition"
                      @search="search"
                      :title="$t('package.result_details_list')"/>

        <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" @filter-change="filter">
          <el-table-column type="index" min-width="3%"/>
          <el-table-column prop="name" :label="'Name'" min-width="10%">
          </el-table-column>
          <el-table-column prop="description" :label="'Description'" min-width="60%">
          </el-table-column>
          <el-table-column min-width="9%" :label="'Severity'" prop="severity">
          </el-table-column>
          <el-table-column min-width="9%" :label="'Source'" prop="source">
          </el-table-column>
          <el-table-column min-width="9%" :label="$t('commons.operating')" fixed="right">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>

    </div>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import TableHeader from "../head/DetailTableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import {_filter, _sort} from "@/common/js/utils";
/* eslint-disable */
  export default {
    name: "ResultDetails",
    components: {
      TableOperators,
      TableHeader,
      TablePagination,
      TableOperator,
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
      }
    },
    props: {
      id: String,
    },
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
      handleClose() {
        this.visible =  false;
      },
      async search () {
        let url = "/package/resultVulnItemList/" + this.currentPage + "/" + this.pageSize;
        this.condition.resultId = this.id;
        await this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
      },
      init() {
        this.search();
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
