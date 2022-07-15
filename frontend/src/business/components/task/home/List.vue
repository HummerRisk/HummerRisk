<template>
  <div v-loading="result.loading">

    <el-card class="table-card">
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


  </div>
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
  },
  data() {
    return {
      result: {},
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      condition: {},
      tableData: [],
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
          tip: this.$t('task.task_quartz'), icon: "el-icon-time", type: "warning",
          exec: this.handleQuartz
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
      this.detailVisible =  false;
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
    handleExecute() {},
    handleEdit(item) {
      this.updateVisible = true;
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
    handleQuartz(item) {},
    handleReExecute(item) {},
    showCodemirror () {
      setTimeout(() => {
        this.$refs.cmEditor.codemirror.refresh();
      },50);
    },
  },
  activated() {
    this.search();
  },
}
</script>

<style scoped>
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
</style>

