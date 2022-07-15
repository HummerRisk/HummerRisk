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

          <el-table border :data="tableData" :key="itemKey" class="adjust-table table-content" :row-class-name="tableRowClassName" @filter-change="filter">
            <el-table-column type="index" min-width="3%"/>
            <el-table-column :label="$t('task.task_account_name')" min-width="15%" show-overflow-tooltip>
              <template v-slot:default="scope">
                <el-link type="primary" :underline="false" class="md-primary text-click"  @click="showTaskDetail(scope.row)">
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

      <!--Task rule detail-->
      <el-drawer v-if="detailVisible" :close-on-click-modal="false" class="rtl" :visible.sync="detailVisible" size="60%" :show-close="false" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div slot="title" class="dialog-title">
          <span>{{ $t('resource.i18n_detail') }}</span>
          <i class="el-icon-close el-icon-close-detail" @click="detailVisible=false"></i>
        </div>
        <el-form :model="detailForm" label-position="right" label-width="120px" size="small" ref="detailForm">
          <el-form-item class="el-form-item-dev">
            <el-tabs type="border-card" @tab-click="showCodemirror">
              <el-tab-pane>
                <span slot="label"><i class="el-icon-reading"></i> {{ $t('rule.rule') }}</span>
                <el-form label-position="left" inline class="demo-table-expand" >
                  <el-form-item :label="$t('task.task_rule_type')" v-if="detailForm.pluginIcon">
                        <span>
                          <img :src="require(`@/assets/img/platform/${detailForm.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                        </span>
                  </el-form-item>
                  <el-form-item :label="$t('rule.rule_name')">
                    <el-tooltip class="item" effect="dark" :content="detailForm.name" placement="top-start">
                      <span v-if="detailForm.name" class="view-text">{{ detailForm.name }}</span>
                    </el-tooltip>
                  </el-form-item>
                  <el-form-item :label="$t('task.task_tag')">
                    <span> {{ detailForm.tagName }}</span>
                  </el-form-item>
                  <el-form-item :label="$t('rule.severity')">
                    <span v-if="detailForm.severity == 'HighRisk'" style="color: #f84846;"> {{ $t('rule.HighRisk') }}</span>
                    <span v-else-if="detailForm.severity == 'MediumRisk'" style="color: #fe9636;"> {{ $t('rule.MediumRisk') }}</span>
                    <span v-else-if="detailForm.severity == 'LowRisk'" style="color: #4dabef;"> {{ $t('rule.LowRisk') }}</span>
                    <span v-else> N/A</span>
                  </el-form-item>
                  <el-form-item :label="$t('account.create_time')">
                    <span>{{ detailForm.lastModified | timestampFormatDate }}</span>
                  </el-form-item>
                </el-form>
                <div style="color: red;margin-left: 10px;">
                  注: {{detailForm.description}}
                </div>
              </el-tab-pane>
              <el-tab-pane>
                <span slot="label"><i class="el-icon-info"></i> {{ $t('rule.rule_detail') }}</span>
                <codemirror ref="cmEditor" v-model="detailForm.script" class="code-mirror" :options="cmOptions" />
              </el-tab-pane>
            </el-tabs>
          </el-form-item>
        </el-form>
      </el-drawer>
      <!--Task rule detail-->

      <!--Task tag detail-->
      <el-drawer class="rtl" :title="$t('resource.i18n_detail')" :visible.sync="tagDetailVisible" size="85%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
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
              <span>{{ scope.row.lastModified | timestampFormatDate }}</span>
            </template>
          </el-table-column>
        </el-table>
        <dialog-footer
          @cancel="tagDetailVisible = false"
          @confirm="handleClose"/>
      </el-drawer>
      <!--Task tag detail-->

      <!--Task group detail-->
      <el-drawer class="rtl" :title="$t('resource.i18n_detail')" :visible.sync="groupDetailVisible" size="85%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
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
              <span>{{ scope.row.lastModified | timestampFormatDate }}</span>
            </template>
          </el-table-column>
        </el-table>
        <dialog-footer
          @cancel="groupDetailVisible = false"
          @confirm="handleClose"/>
      </el-drawer>
      <!--Task group detail-->

    </main-container>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import TableHeader from "../../common/components/TableHeader";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../head/DialogFooter";
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
        detailVisible: false,
        detailForm: {},
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
        tagDetailVisible: false,
        tagDetailTable: [],
        groupDetailVisible: false,
        groupDetailTable: [],
        curSentenceRowIndex: -1,
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
        if(this.condition.accountName) {
          for (let i = 0; i < this.tableData.length; i++) {
            if(this.tableData[i].accountName.indexOf(this.condition.accountName) > -1) {
              this.curSentenceRowIndex = i;
              this.$success(this.$t('task.warn_background'));
              return;
            }
          }
        }
        this.curSentenceRowIndex = -1;
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
        this.tableData.splice(index, 1);
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
        if (this.tableData.length === 0) {
          this.$warning(this.$t('task.second_task'));
          return;
        }
        this.form.taskItemList = this.tableData;
        this.result = this.$post("/task/addTask",this.form, response => {
          if (response.success) {
            this.$router.push({
              path: '/task/list',
            }).catch(error => error);
          }
        });
      },
      reset() {
        this.tableData = [];
      },
      showTaskDetail(item) {
        if (item.ruleType === 'rule') {
          this.detailForm = {};
          this.result = this.$post("/task/detailRule",item, response => {
            if (response.success) {
              let data = response.data;
              if (item.accountType === 'cloudAccount') {
                this.detailForm = data.ruleDTO;
              } else if(item.accountType === 'vulnAccount') {
                this.detailForm = data.ruleDTO;
              } else if(item.accountType === 'serverAccount') {
                this.detailForm = data.serverRuleDTO;
              } else if(item.accountType === 'imageAccount') {
                this.detailForm = data.imageRuleDTO;
              } else if(item.accountType === 'packageAccount') {
                this.detailForm = data.packageRuleDTO;
              }
              this.detailVisible = true;
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
      handleClose() {
        this.detailVisible=false;
        this.tagDetailVisible=false;
        this.groupDetailVisible=false;
      },
      showCodemirror () {
        setTimeout(() => {
          this.$refs.cmEditor.codemirror.refresh();
        },50);
      },
      tableRowClassName({row, rowIndex}) {
        if (rowIndex === this.curSentenceRowIndex) {
          return 'success-row';
        }
        return '';
      }
    },
    computed: {
      codemirror() {
        return this.$refs.cmEditor.codemirror;
      }
    },
    activated() {
      this.search();
    },
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
  .text-click {
    color: #0066ac;
    text-decoration: none;
  }
  .el-form-item-dev  >>> .el-form-item__content {
    margin-left: 0 !important;
  }

  .grid-content-log-span {
    width: 40%;float: left;
    vertical-align: middle;
    display:table-cell;
    margin: 6px 0;
  }

  .grid-content-status-span {
    width: 20%;float: left;
    vertical-align: middle;
    display:table-cell;
    margin: 6px 0;
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

  .el-icon-close-detail {
    float: right;
    cursor:pointer;
  }

  .table-content .success-row {
    background: #0066ac !important;
  }
  /deep/ :focus{outline:0;}
</style>
