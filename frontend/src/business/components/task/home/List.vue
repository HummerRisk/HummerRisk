<template>
  <main-container>
    <el-card v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search" @create="create" :createTip="$t('task.task_add')" :title="$t('task.task_list')"/>
      </template>

      <el-table border class="adjust-table" :data="tableData" style="width: 100%" @sort-change="sort" @filter-change="filter"
                :row-class-name="tableRowClassName">
        <el-table-column type="index" min-width="3%"/>
        <el-table-column :label="$t('task.task_name')" min-width="12%">
          <template v-slot:default="scope">
            <el-link type="primary" :underline="false" class="md-primary text-click"  @click="showTaskDetail(scope.row)">
              <span>
             &nbsp;&nbsp; {{ $t(scope.row.taskName) }}
              </span>
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="description" :label="$t('task.task_desc')" min-width="20%"/>
        <el-table-column min-width="10%" :label="$t('task.task_type')" column-key="type">
          <template v-slot:default="{row}">
            <task-type :row="row"/>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('account.creator')" min-width="8%" show-overflow-tooltip>
          {{ scope.row.userName }}
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('resource.status')" min-width="13%" prop="status" sortable show-overflow-tooltip>
          <el-button @click="showTaskLog(scope.row)" plain size="medium" type="primary" v-if="scope.row.status === 'UNCHECKED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
          </el-button>
          <el-button @click="showTaskLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.status === 'APPROVED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
          </el-button>
          <el-button @click="showTaskLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.status === 'PROCESSING'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
          </el-button>
          <el-button @click="showTaskLog(scope.row)" plain size="medium" type="success" v-else-if="scope.row.status === 'FINISHED'">
            <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
          </el-button>
          <el-button @click="showTaskLog(scope.row)" plain size="medium" type="danger" v-else-if="scope.row.status === 'ERROR'">
            <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
          </el-button>
          <el-button @click="showTaskLog(scope.row)" plain size="medium" type="warning" v-else-if="scope.row.status === 'WARNING'">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
          </el-button>
          <el-button @click="showTaskLog(scope.row)" plain size="medium" type="warning" v-else-if="scope.row.status === 'WAITING'">
            <i class="el-icon-refresh-right"></i> {{ $t('task.waiting') }}
          </el-button>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('commons.create_time')" min-width="15%" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="18%" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>

      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Task detail-->
    <el-drawer class="rtl" :title="$t('task.task_detail')" :visible.sync="detailVisible" size="85%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-table border :data="detailTable" class="adjust-table table-content" :row-class-name="tableRowClassName" @filter-change="filter">
        <el-table-column type="index" min-width="3%"/>
        <el-table-column :label="$t('task.task_account_name')" min-width="15%" show-overflow-tooltip>
          <template v-slot:default="scope">
            <el-link type="primary" :underline="false" class="md-primary text-click"  @click="showTaskRuleDetail(scope.row)">
              <span>
                <img v-if="scope.row.icon" :src="require(`@/assets/img/platform/${scope.row.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ $t(scope.row.accountName) }}
              </span>
            </el-link>
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
            <span type="number">{{ scope.row.taskOrder }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-row type="flex" justify="end">
        <div class="table-page">
          {{ $t('task.page_total', [detailTable.length]) }}
        </div>
      </el-row>
      <div>
        <!--Task rule detail-->
        <el-drawer v-if="ruleDetailVisible" :close-on-click-modal="false" class="rtl" :visible.sync="ruleDetailVisible" size="60%"
                   :append-to-body="true">
          <div slot="title" class="dialog-title">
            <span>{{ $t('resource.i18n_detail') }}</span>
          </div>
          <el-form :model="ruleDetailForm" label-position="right" label-width="120px" size="small" ref="detailForm">
            <el-form-item class="el-form-item-dev">
              <el-tabs type="border-card" @tab-click="showCodemirror">
                <el-tab-pane>
                  <span slot="label"><i class="el-icon-reading"></i> {{ $t('rule.rule') }}</span>
                  <el-form label-position="left" inline class="demo-table-expand" >
                    <el-form-item :label="$t('task.task_rule_type')" v-if="ruleDetailForm.pluginIcon">
                          <span>
                            <img :src="require(`@/assets/img/platform/${ruleDetailForm.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                          </span>
                    </el-form-item>
                    <el-form-item :label="$t('rule.rule_name')">
                      <el-tooltip class="item" effect="dark" :content="ruleDetailForm.name" placement="top-start">
                        <span v-if="ruleDetailForm.name" class="view-text">{{ ruleDetailForm.name }}</span>
                      </el-tooltip>
                    </el-form-item>
                    <el-form-item :label="$t('task.task_tag')">
                      <span> {{ ruleDetailForm.tagName }}</span>
                    </el-form-item>
                    <el-form-item :label="$t('rule.severity')">
                      <span v-if="ruleDetailForm.severity == 'HighRisk'" style="color: #f84846;"> {{ $t('rule.HighRisk') }}</span>
                      <span v-else-if="ruleDetailForm.severity == 'MediumRisk'" style="color: #fe9636;"> {{ $t('rule.MediumRisk') }}</span>
                      <span v-else-if="ruleDetailForm.severity == 'LowRisk'" style="color: #4dabef;"> {{ $t('rule.LowRisk') }}</span>
                      <span v-else> N/A</span>
                    </el-form-item>
                    <el-form-item :label="$t('account.create_time')">
                      <span>{{ ruleDetailForm.lastModified | timestampFormatDate }}</span>
                    </el-form-item>
                  </el-form>
                  <div style="color: red;margin-left: 10px;">
                    注: {{ruleDetailForm.description}}
                  </div>
                </el-tab-pane>
                <el-tab-pane>
                  <span slot="label"><i class="el-icon-info"></i> {{ $t('rule.rule_detail') }}</span>
                  <codemirror ref="cmEditor" v-model="ruleDetailForm.script" class="code-mirror" :options="cmOptions" />
                </el-tab-pane>
              </el-tabs>
            </el-form-item>
          </el-form>
        </el-drawer>
        <!--Task rule detail-->

        <!--Task tag detail-->
        <el-drawer class="rtl" :title="$t('resource.i18n_detail')" :visible.sync="tagDetailVisible" size="75%"
                   :append-to-body="true">
          <el-table border :data="tagDetailTable" class="adjust-table table-content" style="margin: 2%;">
            <el-table-column type="index" min-width="5%"/>
            <el-table-column prop="name" :label="$t('task.task_rule_name')" min-width="20%" show-overflow-tooltip></el-table-column>
            <el-table-column min-width="10%" :label="$t('package.severity')" column-key="severity">
              <template v-slot:default="{row}">
                <severity-type :row="row"/>
              </template>
            </el-table-column>
            <el-table-column prop="description" :label="$t('package.description')" min-width="35%" show-overflow-tooltip></el-table-column>
            <el-table-column :label="$t('package.status')" min-width="10%" show-overflow-tooltip>
              <template v-slot:default="scope">
                <el-switch v-model="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column :label="$t('rule.tag_flag')" min-width="10%" show-overflow-tooltip>
              <template v-slot:default="scope">
                <el-switch v-model="scope.row.flag"/>
              </template>
            </el-table-column>
            <el-table-column prop="lastModified" min-width="20%" :label="$t('package.last_modified')" sortable>
              <template v-slot:default="scope">
                <span><i class="el-icon-time"></i> {{ scope.row.lastModified | timestampFormatDate }}</span>
              </template>
            </el-table-column>
          </el-table>
          <dialog-footer
            @cancel="tagDetailVisible = false"
            @confirm="handleClose"/>
        </el-drawer>
        <!--Task tag detail-->

        <!--Task group detail-->
        <el-drawer class="rtl" :title="$t('resource.i18n_detail')" :visible.sync="groupDetailVisible" size="75%"
                   :append-to-body="true">
          <el-table border :data="groupDetailTable" class="adjust-table table-content" style="margin: 2%;">
            <el-table-column type="index" min-width="5%"/>
            <el-table-column prop="name" :label="$t('task.task_rule_name')" min-width="20%" show-overflow-tooltip></el-table-column>
            <el-table-column min-width="10%" :label="$t('package.severity')" column-key="severity">
              <template v-slot:default="{row}">
                <severity-type :row="row"/>
              </template>
            </el-table-column>
            <el-table-column prop="description" :label="$t('package.description')" min-width="25%" show-overflow-tooltip></el-table-column>
            <el-table-column :label="$t('package.status')" min-width="10%" show-overflow-tooltip>
              <template v-slot:default="scope">
                <el-switch v-model="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column :label="$t('rule.tag_flag')" min-width="10%" show-overflow-tooltip>
              <template v-slot:default="scope">
                <el-switch v-model="scope.row.flag"/>
              </template>
            </el-table-column>
            <el-table-column prop="lastModified" min-width="20%" :label="$t('package.last_modified')" sortable>
              <template v-slot:default="scope">
                <span><i class="el-icon-time"></i> {{ scope.row.lastModified | timestampFormatDate }}</span>
              </template>
            </el-table-column>
          </el-table>
          <dialog-footer
            @cancel="groupDetailVisible = false"
            @confirm="handleClose"/>
        </el-drawer>
        <!--Task group detail-->

      </div>
      <dialog-footer
        @cancel="detailVisible = false"
        @confirm="handleClose"/>
    </el-drawer>
    <!--Task detail-->

    <!--Update Task-->
    <el-drawer class="rtl" :title="$t('task.task_update')" :visible.sync="updateVisible" size="90%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-card class="table-card" v-loading="result.loading">
        <el-row :gutter="20">
          <el-col :span="5" style="max-height: 468px;">
            <el-card class="box-card" style="max-height: 489px;">
              <div slot="header" class="clearfix">
                <span>{{ $t('task.first_task') }}</span>
              </div>
              <account @nodeSelectEvent="nodeChange"/>
            </el-card>
          </el-col>
          <el-col :span="19" style="max-height: 513px;">
            <update-rule :account="account" @addTask="addTask"/>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
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
                          v-model="form.taskName" clearable/>
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
                          v-model="form.description" clearable/>
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
                      v-model="condition.accountName" clearable/>
                  </el-col>
                </el-row>
              </template>

              <el-table border :data="updateTableData" :key="itemKey" class="adjust-table table-content" :row-class-name="tableRowClassName" @filter-change="updateFilter">
                <el-table-column type="index" min-width="3%"/>
                <el-table-column :label="$t('task.task_account_name')" min-width="15%" show-overflow-tooltip>
                  <template v-slot:default="scope">
              <span>
                <img v-if="scope.row.icon" :src="require(`@/assets/img/platform/${scope.row.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
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
                    <el-button type="danger" plain size="mini" @click="deleteUpdateTask(scope.row, scope.$index)"><i class="el-icon-delete"/>{{ $t('commons.delete') }}</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-row type="flex" justify="end">
                <div class="table-page">
                  {{ $t('task.page_total', [updateTotal]) }}
                </div>
              </el-row>
            </el-card>
            <div class="dialog-footer">
              <el-button @click="reset">{{ $t('task.reset') }}</el-button>
              <el-button type="primary" @click="confirm" @keydown.enter.native.prevent>{{ $t('task.save_task') }}</el-button>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </el-drawer>
    <!--Update Task-->

    <!--Task status detail-->
    <el-drawer class="rtl" :title="$t('task.task_detail')" :visible.sync="taskLogListVisible" size="80%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <div v-for="taskLogTable in taskLogListTable" :key="taskLogTable.id" class="el-form-item-dev">
        <div class="el-icon-detail" @click="goResource(taskLogTable)">
          <el-row v-if="taskLogTable.taskResourceVo">
            <el-col :span="24">
              <div class="grid-content bg-purple-light">
                <span class="grid-content-log-span"> {{ taskLogTable.taskResourceVo.name }}</span>
                <span class="grid-content-log-span">
                  <img :src="require(`@/assets/img/platform/${taskLogTable.taskResourceVo.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                  {{ taskLogTable.accountName }}
                </span>
                <span class="grid-content-status-span" v-if="taskLogTable.taskResourceVo.status === 'APPROVED'" style="color: #579df8">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                </span>
                <span class="grid-content-status-span" v-else-if="taskLogTable.taskResourceVo.status === 'FINISHED'" style="color: #7ebf50">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </span>
                <span class="grid-content-status-span" v-else-if="taskLogTable.taskResourceVo.status === 'WARNING'" style="color: #e6a23c;">
                  <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                </span>
                <span class="grid-content-status-span" v-else-if="taskLogTable.taskResourceVo.status === 'ERROR'" style="color: red;">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="grid-content bg-purple-light">
                <span class="grid-content-log-span">
                  <i class="el-icon-time"></i> {{ taskLogTable.createTime | timestampFormatDate }}
                </span>
                <span class="grid-content-log-span">
                  <account-type :row="taskLogTable"/>
                </span>
                <span class="grid-content-status-span">
                   <severity-type :row="taskLogTable.taskResourceVo"/>
                </span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="grid-content bg-purple-light">
                <span class="grid-content-log-span">
                  <rule-type :row="taskLogTable"/>
                </span>
                <span class="grid-content-desc-span">
                  {{ taskLogTable.taskResourceVo.desc }}
                </span>
              </div>
            </el-col>
          </el-row>
        </div>
        <el-table :show-header="false" :data="taskLogTable.taskItemResourceLogList" class="adjust-table table-content">
          <el-table-column>
            <template v-slot:default="scope">
              <div class="bg-purple-div">
                <span
                  v-bind:class="{true: 'color-red', false: ''}[scope.row.result == false]">
                      {{ scope.row.createTime | timestampFormatDate }}
                      {{ scope.row.operator }}
                      {{ scope.row.operation }}
                      {{ scope.row.output }}<br>
                </span>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-divider><i class="el-icon-circle-check"></i></el-divider>
      </div>
    </el-drawer>
    <!--Task status detail-->

  </main-container>
</template>

<script>
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperators from "@/business/components/common/components/TableOperators";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import TaskType from "./TaskType";
import {_filter, _sort} from "@/common/js/utils";
import RuleType from "@/business/components/task/home/RuleType";
import SeverityType from "@/business/components/task/home/SeverityType";
import Account from "@/business/components/task/home/Account";
import UpdateRule from "@/business/components/task/home/UpdateRule";
import AccountType from "@/business/components/task/home/AccountType";
import MainContainer from "../.././common/components/MainContainer";
import {TASK_CONFIGS} from "@/business/components/common/components/search/search-components";

/* eslint-disable */
export default {
  components: {
    TablePagination,
    TableHeader,
    DialogFooter,
    TableOperators,
    TaskType,
    RuleType,
    SeverityType,
    Account,
    UpdateRule,
    AccountType,
    MainContainer,
  },
  data() {
    return {
      result: {},
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      updateTotal: 0,
      condition: {
        components: TASK_CONFIGS
      },
      updateCondition: {},
      tableData: [],
      updateTableData: [],
      form: {},
      direction: 'rtl',
      buttons: [
        {
          tip: this.$t('task.execute'), icon: "el-icon-s-promotion", type: "success",
          exec: this.handleExecute
        },
        {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        },
        {
          tip: this.$t('task.re_execute'), icon: "el-icon-refresh-right", type: "info",
          exec: this.handleReExecute
        },
        {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      detailVisible: false,
      ruleDetailVisible: false,
      ruleDetailForm: {},
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
      tagDetailVisible: false,
      tagDetailTable: [],
      groupDetailVisible: false,
      groupDetailTable: [],
      detailTable: [],
      updateVisible: false,
      taskLogListTable: [],
      taskLogListVisible: false,
      itemKey: Math.random(),
      account: {},
      taskOrder: {},
    }
  },
  methods: {
    sort(column) {
      _sort(column, this.condition);
      this.init();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.init();
    },
    updateFilter(filters) {
      _filter(filters, this.updateCondition);
      this.init();
    },
    filterStatus(value, row) {
      return row.status === value;
    },
    search() {
      let url = "/task/taskList/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    searchRule() {

    },
    searchTag(item) {
      this.result = this.$post("/task/detailTag",item, response => {
        if (response.success) {
          this.tagDetailTable = response.data;
        }
      });
    },
    searchGroup(item) {
      this.result = this.$post("/task/detailGroup",item, response => {
        if (response.success) {
          this.groupDetailTable = response.data;
        }
      });
    },
    handleClose() {
      this.form = {};
      this.updateVisible =  false;
      this.detailVisible =  false;
      this.taskLogListVisible = false;
    },
    tableRowClassName({row, rowIndex}) {
      if (rowIndex%4 === 0) {
        return 'success-row';
      } else if (rowIndex%2 === 0) {
        return 'warning-row';
      } else {
        return '';
      }
    },
    create() {
      this.$router.push({
        path: '/task/task',
      }).catch(error => error);
    },
    showTaskLog (task) {
      if (task.status === 'WAITING') {
        this.$warning(this.$t('task.task_waiting'));
        return;
      }
      this.result = this.$post("/task/taskLogList", task, response => {
        let data = response.data;
        this.taskLogListTable = data;
        this.taskLogListVisible = true;
      });
    },
    showTaskDetail(item) {
      this.result = this.$post("/task/taskItemList", item, response => {
        let data = response.data;
        this.detailTable = data;
        this.detailVisible = true;
      });
    },
    showTaskRuleDetail(item) {
      if (item.ruleType === 'rule') {
        this.ruleDetailForm = {};
        this.result = this.$post("/task/detailRule",item, response => {
          if (response.success) {
            let data = response.data;
            if (item.accountType === 'cloudAccount') {
              this.ruleDetailForm = data.ruleDTO;
            } else if(item.accountType === 'vulnAccount') {
              this.ruleDetailForm = data.ruleDTO;
            } else if(item.accountType === 'serverAccount') {
              this.ruleDetailForm = data.serverRuleDTO;
            } else if(item.accountType === 'imageAccount') {
              this.ruleDetailForm = data.imageRuleDTO;
            } else if(item.accountType === 'packageAccount') {
              this.ruleDetailForm = data.packageRuleDTO;
            }
            this.ruleDetailVisible = true;
          }
        });
      } else if(item.ruleType === 'tag') {
        this.searchTag(item);
        this.tagDetailVisible = true;
      } else if(item.ruleType === 'group') {
        this.searchGroup(item);
        this.groupDetailVisible = true;
      }
    },
    handleExecute(item) {
      this.result = this.$get("/task/execute/" + item.id, response => {
        this.$success(this.$t('task.task_start'));
        this.search();
      });
    },
    handleEdit(item) {
      this.form = item;
      this.result = this.$post("/task/taskItemList", item, response => {
        let data = response.data;
        this.updateTableData = data;
        this.updateVisible = true;
      });
    },
    handleDelete(item) {
      this.$alert(this.$t('account.delete_confirm') + item.taskName + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/task/deleteTask/" + item.id, () => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    handleReExecute(item) {
      this.result = this.$get("/task/reExecute/" + item.id, response => {
        this.$success(this.$t('task.task_restart'));
      });
    },
    showCodemirror () {
      setTimeout(() => {
        this.$refs.cmEditor.codemirror.refresh();
      },50);
    },
    change(e) {
      //3种方法都没效果
      // 1. 在nextTick中使用this.$refs.table.doLayout()
      // 2. 在data赋值前清空tableData
      // 3. 强制刷新：this.$forceUpdate()
      //el-table表格数据变化，页面不更新问题。给table加个key，页面就能更新了
      this.itemKey = Math.random();
    },
    deleteUpdateTask(item, index) {
      this.updateTableData.splice(index, 1);
    },
    confirm() {
      if (!this.form.taskName) {
        this.$warning(this.$t('vuln.no_plugin_param') + this.$t('task.task_name'));
        return;
      }
      if (!this.form.description) {
        this.$warning(this.$t('vuln.no_plugin_param') + this.$t('task.task_desc'));
        return;
      }
      if (this.updateTableData.length === 0) {
        this.$warning(this.$t('task.second_task'));
        return;
      }
      this.form.taskItemList = this.updateTableData;
      this.result = this.$post("/task/updateTask", this.form, response => {
        if (response.success) {
          this.$success(this.$t('commons.save_success'));
          this.handleClose();
        }
      });
    },
    reset() {
      this.updateTableData = [];
    },
    nodeChange(node, nodeIds, pNodes) {
      if(node.data.id === "root" || !node.data.id) {
        this.$warning(this.$t('task.task_tree_child'));
        return;
      }
      this.account = node.data;
    },
    addTask(val) {
      val.taskOrder = this.updateTableData.length + 1;
      this.updateTableData.unshift(val);
      this.updateTotal = this.updateTableData.length;

      // 按照order值排序数组
      this.updateTableData.sort(function (a, b) {
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
    getStatus () {
      if (this.checkStatus(this.tableData)) {
        this.search();
        clearInterval(this.timer);
        this.timer = setInterval(this.getStatus,60000);
      } else {
        for (let data of this.tableData) {
          let url = "/task/getTask/";
          this.$get(url + data.id, response => {
            let result = response.data;
            if (data.status !== result.status) {
              data.status = result.status;
            }
          });
        }
      }
    },
    //是否是结束状态，返回false代表都在运行中，true代表已结束
    checkStatus (tableData) {
      let sum = 0;
      for (let row of tableData) {
        if (row.status != 'ERROR' && row.status != 'FINISHED' && row.status != 'WARNING' && row.status != 'WAITING') {
          sum++;
        }
      }
      return sum == 0;
    },
    goResource(item) {
      this.$alert(this.$t('resource.i18n_comfirm_resource') + item.taskResourceVo.name, '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            if (item.accountType === 'cloudAccount') {
              this.$router.push({
                path: '/resource/result',
              }).catch(error => error);
            } else if(item.accountType === 'vulnAccount') {
              this.$router.push({
                path: '/resource/vulnResult',
              }).catch(error => error);
            } else if(item.accountType === 'serverAccount') {
              this.$router.push({
                path: '/resource/serverResult',
              }).catch(error => error);
            } else if(item.accountType === 'imageAccount') {
              this.$router.push({
                path: '/resource/imageResult',
              }).catch(error => error);
            } else if(item.accountType === 'packageAccount') {
              this.$router.push({
                path: '/resource/packageResult',
              }).catch(error => error);
            }
          }
        }
      });
    }
  },
  activated() {
    this.search();
    this.timer = setInterval(this.getStatus,10000);
  },
  beforeDestroy() {
    clearInterval(this.timer);
  }
}
</script>

<style scoped>
.table-card >>> .el-card__header {
  padding: 0;
}
.text-click {
  text-decoration: none;
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
  padding: 20px 20px 0 0;
}
.text-click {
  color: #0066ac;
  text-decoration: none;
}
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
}
.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  padding: 10px 2%;
  width: 46%;
}

.el-icon-detail {
  cursor:pointer;
  width: 100%;
}

.table-content .success-row {
  background: #0066ac !important;
}
.box-card {
  margin: 10px 0 0 0;
}
.box-card >>> .el-card__header {
  padding: 5px 20px;
  background-color: #b0abab;
  color: #fff;
}
.box-card >>> .el-card__body {
  padding: 10px;
  min-height: 405px;
}
.bg-purple-dark {
  background: #99a9bf;
}
.bg-purple {
  background: #d3dce6;
}
.bg-purple-light {
  background: #f2f2f2;
}
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
}
.rtl >>> .el-drawer__body {
  overflow-y: auto;
  padding: 20px;
}
.grid-content-log-span {
  width: 38%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0 6px 10px;
  color: #606266;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}
.grid-content-status-span {
  width: 20%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}
.grid-content-desc-span {
  width: 60%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0 6px 10px;
  color: #606266;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

</style>

