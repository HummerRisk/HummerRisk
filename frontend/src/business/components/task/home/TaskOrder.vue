<template>
    <main-container class="main-content-box">
      <el-card class="table-card" v-loading="result.loading">
          <template v-slot:header>
            <div slot="header" class="clearfix">
              <span>{{ $t('task.third_task') }}</span>
            </div>
            <el-row :gutter="20">
              <el-form ref="form" :model="form" label-width="80px">
                <el-col :span="8">
                  <el-form-item :label="$t('task.task_name')" :rules="{required: true, message: $t('task.task_name'), trigger: 'change'}">
                    <el-input
                      class="search"
                      type="text"
                      size="medium"
                      :placeholder="$t('task.task_name')"
                      @change="search"
                      maxlength="60"
                      v-model="form.name" clearable/>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item :label="$t('task.task_desc')" :rules="{required: true, message: $t('task.task_name'), trigger: 'change'}">
                    <el-input
                      class="search"
                      type="text"
                      size="medium"
                      :placeholder="$t('task.task_desc')"
                      @change="search"
                      maxlength="60"
                      v-model="form.desc" clearable/>
                  </el-form-item>
                </el-col>
              </el-form>
              <el-col :span="4"  style="height: 36px;">
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

          <el-table border :data="tableData" :key="itemKey" class="adjust-table table-content" stripe @filter-change="filter">
            <el-table-column type="index" min-width="3%"/>
            <el-table-column :label="$t('task.task_account_name')" min-width="15%" show-overflow-tooltip>
              <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ $t(scope.row.accountName) }}
              </span>
              </template>
            </el-table-column>
            <el-table-column prop="ruleName" :label="$t('task.task_rule_name')" min-width="20%" show-overflow-tooltip></el-table-column>
            <el-table-column min-width="10%" :label="$t('task.task_rule_type')" column-key="ruleType">
              <template v-slot:default="{row}">
                <rule-type :row="row"/>
              </template>
            </el-table-column>
            <el-table-column min-width="10%" :label="$t('task.task_rule_severity')" column-key="severity">
              <template v-slot:default="{row}">
                <severity-type :row="row"/>
              </template>
            </el-table-column>
            <el-table-column prop="ruleDesc" :label="$t('task.task_rule_desc')" min-width="30%" show-overflow-tooltip></el-table-column>
            <el-table-column :label="$t('task.task_order')" min-width="10%">
              <template slot-scope="scope">
                <el-input type="number" @input="change($event)" v-model="scope.row.taskOrder" :key="scope.$index"/>
              </template>
            </el-table-column>
            <el-table-column min-width="9%" :label="$t('commons.operating')" fixed="right">
              <template v-slot:default="scope">
                <el-button type="danger" plain size="mini" @click="deleteTask(scope.row, scope.$index)"><i class="el-icon-delete"/>{{ $t('commons.delete') }}</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-row type="flex" justify="end">
            <div class="table-page">
              {{ $t('task.page_total', [total]) }}
            </div>
          </el-row>
      </el-card>
      <div class="dialog-footer">
        <el-button @click="reset">{{ $t('task.reset') }}</el-button>
        <el-button type="primary" @click="confirm" @keydown.enter.native.prevent>{{ $t('task.save_task') }}</el-button>
      </div>
    </main-container>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import TableHeader from "../../common/components/TableHeader";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../../common/components/RuleDialogFooter";
import {_filter} from "@/common/js/utils";
import RuleType from "./RuleType";
import SeverityType from "./SeverityType";

/* eslint-disable */
  export default {
    components: {
      TableOperators,
      MainContainer,
      Container,
      TableHeader,
      TableOperator,
      DialogFooter,
      RuleType,
      SeverityType,
    },
    data() {
      return {
        result: {},
        condition: {
        },
        tableData: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        form: {},
        itemKey: Math.random(),
      }
    },

    watch: {
      '$route': 'init',
      taskOrder: function (val) {
        val.taskOrder = this.tableData.length + 1;
        this.init(val);
      }
    },
    props: {
      taskOrder: Object,
    },
    methods: {
      change(e) {
        //3种方法都没效果
        // 1. 在nextTick中使用this.$refs.table.doLayout()
        // 2. 在data赋值前清空tableData
        // 3. 强制刷新：this.$forceUpdate()
        //el-table表格数据变化，页面不更新问题。给table加个key，页面就能更新了
        this.itemKey = Math.random();
      },
      init(val) {
        this.tableData.unshift(val);
        this.total = this.tableData.length;

        // 按照order值排序数组
        this.tableData.sort(function (a, b) {
          let aVal = a.taskOrder;
          let bVal = b.taskOrder;
          if (aVal < bVal) {
            return -1;
          }
          if (aVal > bVal) {
            return 1;
          }
          return 0;
        });
      },
      //查询列表
      search() {

      },
      severityOptionsFnc () {
        this.severityOptions = [
          {key: '低风险', value: "LowRisk"},
          {key: '中风险', value: "MediumRisk"},
          {key: '高风险', value: "HighRisk"}
        ];
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.search();
      },
      deleteTask(item, index) {
        this.tableData.splice(item, index+1);
      },
      confirm() {},
      reset() {
        this.tableData = [];
      },
    },
    created() {
      this.search();
    }

  }
</script>

<style scoped>
  .table-card >>> .el-card__header {
    padding: 0;
  }
  .main-content-box{
    padding: 0;
    height: calc(100% - 680px);
  }
  .table-content {
    width: 100%;
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
  .clearfix {
    padding: 5px 20px;
    background-color: #b0abab;
    color: #fff;
    margin-bottom: 3px;
  }
  .dialog-footer {
    text-align: center;
  }
  .table-page {
    padding-top: 20px;
  }
  /deep/ :focus{outline:0;}
</style>
