<template>
    <main-container class="main-content-box">
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <el-row :gutter="20">
            <el-col :span="20">
              <el-tabs type="card" @tab-click="filterRules">
                <el-tab-pane :label="$t('rule.all')"></el-tab-pane>
                <el-tab-pane
                  :key="tag.tagKey"
                  v-for="tag in tags"
                  :label="$t(tag.tagName)">
                </el-tab-pane>
              </el-tabs>
            </el-col>
            <el-col :span="4">
              <el-input
                class="search"
                type="text"
                size="medium"
                :placeholder="$t('task.search_rule')"
                prefix-icon="el-icon-search"
                @change="search"
                maxlength="60"
                v-model="condition.name" clearable/>
            </el-col>
          </el-row>
        </template>

        <el-table border :data="tableData" class="adjust-table table-content" stripe @filter-change="filter">
          <el-table-column type="index" min-width="3%"/>
          <el-table-column :label="$t('account.cloud_platform')" min-width="15%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.pluginName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="name" :label="$t('rule.rule_name')" min-width="20%" show-overflow-tooltip></el-table-column>
          <el-table-column min-width="8%" :label="$t('rule.severity')" column-key="severity">
            <template v-slot:default="{row}">
              <rule-type :row="row"/>
            </template>
          </el-table-column>
          <el-table-column prop="description" :label="$t('rule.description')" min-width="45%" show-overflow-tooltip></el-table-column>
          <el-table-column min-width="9%" :label="$t('commons.operating')" fixed="right">
            <template v-slot:default="scope">
              <el-button type="primary" plain size="mini" @click="addTask(scope.row)"><i class="el-icon-plus"/>{{ $t('commons.add') }}</el-button>
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
import TableHeader from "../../common/components/TableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../../common/components/RuleDialogFooter";
import {_filter} from "@/common/js/utils";
import RuleType from "./RuleType";

/* eslint-disable */
  export default {
    components: {
      TableOperators,
      MainContainer,
      Container,
      TableHeader,
      TablePagination,
      TableOperator,
      DialogFooter,
      RuleType
    },
    data() {
      return {
        result: {},
        condition: {
        },
        tableData: [],
        currentPage: 1,
        pageSize: 5,
        total: 0,
        loading: false,
        plugins: [],
        tags: [],
      }
    },

    watch: {
      '$route': 'init'
    },

    methods: {
      //查询列表
      search() {
        let url = "/rule/list/" + this.currentPage + "/" + this.pageSize;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
      },
      tagLists() {
        let url = "/rule/ruleTags";
        this.result = this.$get(url, response => {
          this.tags = response.data;
        });
      },
      filterRules (tag) {
        let key = "";
        for (let obj of this.tags) {
          if (tag.label == obj.tagName) {
            key = obj.tagKey;
            break;
          } else {
            key = 'all';
          }
        }
        if (this.condition.combine) {
          this.condition.combine.ruleTag = {operator: 'in', value: key};
        } else {
          this.condition.combine = {ruleTag: {operator: 'in', value: key }};
        }
        this.search();
      },
      severityOptionsFnc () {
        this.severityOptions = [
          {key: '低风险', value: "LowRisk"},
          {key: '中风险', value: "MediumRisk"},
          {key: '高风险', value: "HighRisk"}
        ];
      },
      ruleSetOptionsFnc (pluginId) {
        this.$get("/rule/ruleGroups/" + pluginId, res => {
          this.ruleSetOptions = res.data;
        });
      },
      inspectionSeportOptionsFnc () {
        this.$get("/rule/all/ruleInspectionReport", res => {
          this.inspectionSeportOptions = res.data;
        });
      },
      init() {
        this.tagLists();
        this.severityOptionsFnc();
        this.inspectionSeportOptionsFnc();
        this.search();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.init();
      },
      addTask(item) {
        this.$emit('addTask', item);
      }
    },
    created() {
      this.init();
    }

  }
</script>

<style scoped>
  .table-card >>> .el-card__header {
    padding: 10px 5px 0 5px;
  }
  .main-content-box{
    padding: 10px 0 0 0;
    max-height: 480px;
  }
  .table-content {
    width: 100%;
  }
  .table-card {
    max-height: 480px;
  }

  /deep/ :focus{outline:0;}
</style>
