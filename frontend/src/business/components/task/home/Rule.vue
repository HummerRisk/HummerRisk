<template>
    <main-container class="main-content-box">
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <div class="clearfix">
            <el-col :span="6"><span>{{ $t('task.second_task') }}</span></el-col>
            <el-col :span="18" v-if="account.icon || account.pluginIcon">
              <span>
                <span v-if="account.type==='cloudAccount'">
                  <span style="color: red;">{{ $t('task.task_cloud') }} : </span>
                  <img :src="require(`@/assets/img/platform/${account.icon?account.icon:account.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                  <span> {{ account.name }} <i class="el-icon-time"></i> {{ account.createTime | timestampFormatDate }}</span>
                </span>
                <span v-if="account.type==='vulnAccount'">
                  <span style="color: red;">{{ $t('task.task_vuln') }} : </span>
                  <img :src="require(`@/assets/img/platform/${account.icon?account.icon:account.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                  <span>{{ account.name }} <i class="el-icon-time"></i> {{ account.createTime | timestampFormatDate }}</span>
                </span>
                <span v-if="account.type==='serverAccount'">
                  <span style="color: red;">{{ $t('task.task_server') }} : </span>
                  <img :src="require(`@/assets/img/platform/server.png`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                  {{ account.name }} {{ account.ip }} : {{ account.port }}
                </span>
                <span v-if="account.type==='imageAccount'">
                  <span style="color: red;">{{ $t('task.task_image') }} : </span>
                  <img :src="require(`@/assets/img/platform/docker.png`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                  {{ account.name }}
                  <span v-if="!account.path">{{ account.imageUrl }} : {{ account.imageTag }} </span>
                  <span v-if="account.path">{{ account.path }} </span>
                </span>
                <span v-if="account.type==='packageAccount'">
                  <span style="color: red;">{{ $t('task.task_package') }} : </span>
                  <img :src="require(`@/assets/img/platform/package.png`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                  {{ account.name }} {{ account.packageName }}
                </span>
              </span>
            </el-col>
          </div>
          <el-row :gutter="20">
            <el-col :span="20">
              <el-tabs type="card" @tab-click="filterRules">
                <el-tab-pane :label="$t('rule.all')"></el-tab-pane>
                <el-tab-pane
                  :key="tag.id"
                  v-for="tag in tags"
                  :label="tag.name">
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

        <el-table border :data="tableData" class="adjust-table table-content" stripe @filter-change="filter" height="317">
          <el-table-column type="index" min-width="3%"/>
          <el-table-column :label="$t('task.task_rule_name')" min-width="27%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.ruleName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column min-width="10%" :label="$t('task.task_rule_severity')" column-key="severity">
            <template v-slot:default="{row}">
              <severity-type :row="row"/>
            </template>
          </el-table-column>
          <el-table-column prop="ruleDesc" :label="$t('task.task_rule_desc')" min-width="50%" show-overflow-tooltip></el-table-column>
          <el-table-column min-width="10%" :label="$t('commons.operating')" fixed="right">
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
import SeverityType from "./SeverityType";

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
      SeverityType
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
        tags: [
          {id: 'rule', name: this.$t('task.task_rule')},
          {id: 'tag', name: this.$t('task.task_tag')},
          {id: 'group', name: this.$t('task.task_group')},
        ],
      }
    },

    watch: {
      '$route': 'init',
      account: function (val) {
        this.condition.accountId = val.sourceId?val.sourceId:val.id;
        this.condition.accountName = val.name;
        this.condition.accountType = val.type;
        this.search();
      }
    },
    props: {
      account: Object,
    },

    methods: {
      //查询列表
      search() {
        let url = "/task/ruleList/" + this.currentPage + "/" + this.pageSize;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
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
    padding: 0;
  }
  .main-content-box{
    padding: 10px 0 0 0;
    max-height: 513px;
  }
  .table-content {
    width: 100%;
  }
  .table-card {
    max-height: 513px;
  }
  .clearfix {
    padding: 5px 20px;
    background-color: #b0abab;
    color: #fff;
    margin-bottom: 3px;
  }
  /deep/ :focus{outline:0;}
</style>
