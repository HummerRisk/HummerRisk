<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">

        <template v-slot:header>
          <table-header :condition.sync="condition"
                        @search="search"
                        :show-back="true"
                        @back="back" :backTip="$t('image.back_resource')"
                        :title="$t('code.result_details_list')"/>
        </template>

        <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" @filter-change="filter">
          <el-table-column type="index" min-width="5%"/>
          <el-table-column :label="'Name'" min-width="15%" prop="name">
          </el-table-column>
          <el-table-column :label="'Installed'" min-width="15%" prop="installed">
          </el-table-column>
          <el-table-column min-width="10%" :label="'FixedIn'" prop="fixedIn">
          </el-table-column>
          <el-table-column min-width="10%" :label="'Type'" prop="type">
          </el-table-column>
          <el-table-column min-width="15%" :label="'Vulnerability'" prop="vulnerability">
          </el-table-column>
          <el-table-column min-width="15%" :label="'Severity'" prop="severity">
          </el-table-column>
          <el-table-column min-width="15%" :label="$t('commons.operating')" fixed="right">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
      </el-card>

      <!--file-->
      <el-drawer class="rtl" :title="string2Key" :visible.sync="visible"  size="80%" :before-close="handleClose" :direction="direction" :destroy-on-close="true">
        <codemirror ref="cmEditor" v-model="string2PrettyFormat" class="code-mirror" :options="cmOptions" />
      </el-drawer>
      <!--file-->

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
import ResultReadOnly from "./ResultReadOnly";
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
      ResultReadOnly,
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
        string2Key: "",
        string2PrettyFormat: "",
        visible: false,
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
      showInformation (row, details, title) {
        this.string2Key = title;
        this.string2PrettyFormat = "";
        if (row) {
          this.$post("/resource/string2PrettyFormat", {json: details}, res => {
            this.string2PrettyFormat = res.data;
          });
        } else {
          this.string2PrettyFormat = details;
        }

        this.visible =  true;
      },
      handleClose() {
        this.visible =  false;
      },
      search () {
        let url = "/code/resultItemList/" + this.currentPage + "/" + this.pageSize;
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
    computed: {
      codemirror() {
        return this.$refs.cmEditor.codemirror
      }
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
