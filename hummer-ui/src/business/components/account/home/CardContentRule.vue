<template>
  <el-card class="box-card" shadow="always">
    <el-tabs type="border-card" @tab-click="changeTab">
      <!-- 检测规则 start-->
      <el-tab-pane :label="$t('history.rule')">
        <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                  @filter-change="filter" @select-all="select" @select="select">
          <el-table-column type="index" min-width="40"/>
          <el-table-column :label="$t('rule.rule_name')" min-width="300" show-overflow-tooltip>
            <template v-slot:default="scope">
              <el-link type="primary" @click="viewRule(scope.row)">
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                {{ scope.row.name }}
              </el-link>
            </template>
          </el-table-column>
          <el-table-column :label="$t('dashboard.i18n_compliance_ratio')" min-width="160" show-overflow-tooltip>
            <template v-slot:default="scope">
              {{ !!scope.row.ratio?scope.row.ratio:'0.00%' }}
            </template>
          </el-table-column>
          <el-table-column :label="$t('rule.rule_tag')" min-width="110" show-overflow-tooltip>
            <template v-slot:default="scope">
              {{ scope.row.tagName }}
            </template>
          </el-table-column>
          <el-table-column prop="severity" :label="$t('dashboard.i18n_severity_resource_number')" min-width="300" show-overflow-tooltip sortable fixed="right">
            <template v-slot:default="scope">
              <severity-type :row="scope.row"></severity-type>
              <span> | ({{ scope.row.ruSum?scope.row.ruSum:0 }}/{{ scope.row.reSum?scope.row.reSum:0 }})</span>
              <span> &nbsp;&nbsp;<i :class="scope.row.assets"></i></span>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
      </el-tab-pane>
      <!-- 检测规则 end-->
      <!-- 规则组别 start-->
      <el-tab-pane :label="$t('history.rule_set')">
        <el-table border :data="groupData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                  @filter-change="filter" @select-all="select" @select="select" >
          <el-table-column type="index" min-width="40"/>
          <el-table-column :label="$t('rule.rule_set')" min-width="200" show-overflow-tooltip>
            <template v-slot:default="scope">
                {{ scope.row.name }}
            </template>
          </el-table-column>
          <el-table-column :label="$t('commons.description')" min-width="400" show-overflow-tooltip>
            <template v-slot:default="scope">
              {{ scope.row.description }}
            </template>
          </el-table-column>
          <el-table-column prop="returnSum" :label="$t('history.resource_result')" min-width="250" show-overflow-tooltip sortable fixed="right">
            <template v-slot:default="scope">
              <span> {{ scope.row.returnSum?scope.row.returnSum:0 }}/{{ scope.row.resourcesSum?scope.row.resourcesSum:0 }}</span>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="groupTotal"/>
      </el-tab-pane>
      <!-- 规则组别 end-->
      <!-- 等保条例 start-->
      <el-tab-pane :label="$t('history.inspection_report')">
        <el-table border :data="reportData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                  @filter-change="filter" @select-all="select" @select="select" >
          <el-table-column type="index" min-width="50"/>
          <el-table-column prop="item_sort_first_level" :label="$t('resource.security_level')" min-width="120" show-overflow-tooltip></el-table-column>
          <el-table-column prop="item_sort_second_level" :label="$t('resource.control_point')" min-width="120" show-overflow-tooltip></el-table-column>
          <el-table-column prop="project" :label="$t('resource.basic_requirements_for_grade_protection')" min-width="400" show-overflow-tooltip></el-table-column>
          <el-table-column :label="$t('resource.pre_check_results')" min-width="120" show-overflow-tooltip>
            <template v-slot:default="scope">
              <el-tooltip class="item" effect="dark" :content="$t('resource.risk_of_non_compliance')" placement="top">
                <span v-if="scope.row.status === 'risky'" style="color: red;">
                    {{ $t('resource.' + scope.row.status) }} <i class="el-icon-warning"></i>
                </span>
              </el-tooltip>
              <el-tooltip class="item" effect="dark" :content="$t('resource.requirements_of_the_regulations')" placement="top">
                <span v-if="scope.row.status === 'risk_free'" style="color: #00bb00;">
                    {{ $t('resource.' + scope.row.status) }} <i class="el-icon-warning"></i>
                </span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column v-slot:default="scope" :label="$t('resource.suggestions_for_improvement')" min-width="120">
            <el-tooltip v-if="scope.row.status === 'risky'" class="item" effect="dark" :content="scope.row.improvement" placement="top">
              <el-link type="primary">
                {{ $t('resource.suggestions_for_improvement') }} <i class="el-icon-question"></i>
              </el-link>
            </el-tooltip>
            <span v-if="scope.row.status === 'risk_free'">
              <i class="el-icon-minus"></i>
            </span>
          </el-table-column>
          <el-table-column prop="returnSum" :label="$t('history.resource_result')" min-width="180" show-overflow-tooltip sortable fixed="right">
            <template v-slot:default="scope">
              <span> {{ scope.row.returnSum?scope.row.returnSum:0 }}/{{ scope.row.resourcesSum?scope.row.resourcesSum:0 }}</span>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="reportTotal"/>
      </el-tab-pane>
      <!-- 等保条例 end-->
      <!-- 规则标签 start-->
      <el-tab-pane :label="$t('history.rule_tag')">
        <el-table border :data="tagData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                  @filter-change="filter" @select-all="select" @select="select" >
          <el-table-column type="index" min-width="50"/>
          <el-table-column :label="$t('rule.tag_key')" min-width="150" show-overflow-tooltip>
            <template v-slot:default="scope">
                {{ scope.row.tagKey }}
            </template>
          </el-table-column>
          <el-table-column :label="$t('rule.tag_name')" min-width="150" show-overflow-tooltip>
            <template v-slot:default="scope">
              {{ scope.row.tagName }}
            </template>
          </el-table-column>
          <el-table-column :label="$t('rule._index')" min-width="150" show-overflow-tooltip>
            <template v-slot:default="scope">
              {{ scope.row.index }}
            </template>
          </el-table-column>
          <el-table-column prop="returnSum" :label="$t('history.resource_result')" min-width="300" show-overflow-tooltip sortable fixed="right">
            <template v-slot:default="scope">
              <span> {{ scope.row.returnSum?scope.row.returnSum:0 }}/{{ scope.row.resourcesSum?scope.row.resourcesSum:0 }}</span>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="tagTotal"/>
      </el-tab-pane>
      <!-- 规则标签 end-->
      <!-- 检测区域 start-->
      <el-tab-pane :label="$t('history.regions')">
        <el-table border :data="regionsData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                  @filter-change="filter" @select-all="select" @select="select" >
          <el-table-column type="index" min-width="50"/>
          <el-table-column :label="$t('account.region_name')" min-width="160" show-overflow-tooltip>
            <template v-slot:default="scope">
                {{ scope.row.regionName }}
            </template>
          </el-table-column>
          <el-table-column :label="$t('account.region_id')" min-width="160" show-overflow-tooltip>
            <template v-slot:default="scope">
              {{ scope.row.regionId }}
            </template>
          </el-table-column>
          <el-table-column :label="$t('account.cloud_account')" min-width="180" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.accountUrl}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.accountName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="returnSum" :label="$t('history.resource_result')" min-width="250" show-overflow-tooltip sortable fixed="right">
            <template v-slot:default="scope">
              <span> {{ scope.row.returnSum?scope.row.returnSum:0 }}/{{ scope.row.resourcesSum?scope.row.resourcesSum:0 }}</span>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="regionsTotal"/>
      </el-tab-pane>
      <!-- 检测区域 end-->
      <!-- 检测资源 start-->
      <el-tab-pane :label="$t('history.scan_resources')">
        <el-table border :data="resourceData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                  @filter-change="filter" @select-all="select" @select="select" >
          <el-table-column type="index" min-width="50"/>
          <el-table-column v-slot:default="scope" :label="$t('resource.Hummer_ID')" min-width="150">
            {{ scope.row.hummerId }}
          </el-table-column>
          <el-table-column v-slot:default="scope" :label="$t('rule.resource_type')" min-width="150">
            {{ scope.row.resourceType }}
          </el-table-column>
          <el-table-column prop="taskName" :label="$t('rule.rule_name')" min-width="200"/>
          <el-table-column :label="$t('account.cloud_account')" min-width="150" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.pluginName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="regionName" :label="$t('account.region_name')" min-width="140"/>
          <el-table-column min-width="200" :label="$t('account.update_time')" sortable fixed="right"
                           prop="updateTime">
            <template v-slot:default="scope">
              <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="resourceTotal"/>
      </el-tab-pane>
      <!-- 检测资源 end-->
    </el-tabs>
    <!--View rule-->
    <el-drawer class="rtl" :title="$t('rule.rule_detail')" :visible.sync="ruleVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="ruleForm" label-position="right" label-width="120px" size="small" ref="ruleForm">
        <el-form-item :label="$t('rule.rule_name')">
          <el-input v-model="ruleForm.name" autocomplete="off" :placeholder="$t('rule.rule_name')"/>
        </el-form-item>
        <el-form-item :label="$t('rule.rule_description')">
          <el-input v-model="ruleForm.description" autocomplete="off" :placeholder="$t('rule.rule_description')"/>
        </el-form-item>
        <el-form-item :label="$t('account.cloud_platform')">
          <el-select style="width: 100%;" v-model="ruleForm.pluginId" :placeholder="$t('account.please_choose_plugin')">
            <el-option
              v-for="item in plugins"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              <img :src="require(`@/assets/img/platform/${item.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
              &nbsp;&nbsp; {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('rule.rule_tag')">
          <el-select style="width: 100%;" multiple v-model="ruleForm.tags" :placeholder="$t('rule.please_choose_tag')">
            <el-option
              v-for="item in tags"
              :key="item.tagKey"
              :label="item.tagName"
              :value="item.tagKey">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('rule.severity')">
          <el-select style="width: 100%;" v-model="ruleForm.severity" :placeholder="$t('rule.please_choose_severity')">
            <el-option
              v-for="item in severityOptions"
              :key="item.value"
              :label="item.key"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('rule.rule_set')">
          <el-select style="width: 100%;" multiple filterable v-model="ruleForm.ruleSets">
            <el-option
              v-for="item in ruleSetOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('rule.inspection_report')">
          <el-select style="width: 100%;" multiple filterable collapse-tags v-model="ruleForm.inspectionSeports">
            <el-option
              v-for="item in inspectionSeportOptions"
              :key="item.id"
              :label="item.id + '. ' + item.project.substring(0, 50) + '...'"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('rule.rule_yml')">
          <codemirror ref="cmEditor" v-model="ruleForm.script" class="code-mirror" :options="cmOptions" />
        </el-form-item>
        <el-form-item>
          <el-table
            :data="ruleForm.parameter"
            style="width: 100%">
            <el-table-column v-slot:default="scope" label="Key" min-width="20%">
              <el-input v-model="scope.row.key" :placeholder="$t('commons.input_content')"></el-input>
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('rule.middleware_name')" min-width="30%">
              <el-input v-model="scope.row.name" :placeholder="$t('commons.input_content')"></el-input>
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('rule.middleware_parameter_default')" min-width="30%">
              <el-input v-model="scope.row.defaultValue" :placeholder="$t('commons.input_content')"></el-input>
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('rule.required')" min-width="20%">
              <el-switch v-model="scope.row.required" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <dialog-footer
        @cancel="ruleVisible = false"
        @confirm="ruleVisible = false"
        />
    </el-drawer>
    <!--View rule-->

  </el-card>
</template>
<script>
import {_filter, _sort} from "@/common/js/utils";
import TablePagination from "../../common/pagination/TablePagination";
import DialogFooter from "../../common/components/DialogFooter";
import SeverityType from "@/business/components/common/components/SeverityType";
import {severityOptions} from "@/common/js/constants";
import {groupListUrl, regionsListUrl, reportListUrl, resourceListUrl, tagListUrl} from "@/api/cloud/account/account";
import {
  getResourceTypesById,
  getRuleUrl,
  ruleGroupsUrl,
  ruleInspectionReport,
  ruleTagsUrl
} from "@/api/cloud/rule/rule";
import {cloudPluginUrl} from "@/api/system/system";
import {dashboardPointUrl} from "@/api/cloud/dashboard/dashboard";

/* eslint-disable */
  const assets = [
    {key: "ec2", value: "el-icon-s-platform"},
    {key: "ecs", value: "el-icon-s-platform"},
    {key: "cvm", value: "el-icon-s-platform"},
    {key: "ami", value: "el-icon-picture"},
    {key: "s3", value: "el-icon-folder-opened"},
    {key: "oss", value: "el-icon-folder-opened"},
    {key: "obs", value: "el-icon-folder-opened"},
    {key: "cos", value: "el-icon-folder-opened"},
    {key: "security-group", value: "el-icon-lock"},
    {key: "network-addr", value: "el-icon-connection"},
    {key: "etc", value: "el-icon-s-platform"},
    {key: "asg", value: "el-icon-s-operation"},
    {key: "elb", value: "el-icon-s-operation"},
    {key: "slb", value: "el-icon-s-operation"},
    {key: "lambda", value: "el-icon-data-board"},
    {key: "autoscaling", value: "el-icon-s-operation"},
    {key: "account", value: "el-icon-s-custom"},
    {key: "eip", value: "el-icon-s-grid"},
    {key: "ip", value: "el-icon-s-grid"},
    {key: "ebs", value: "el-icon-wallet"},
    {key: "iam", value: "el-icon-s-tools"},
    {key: "rds", value: "el-icon-s-finance"},
    {key: "tag", value: "el-icon-s-flag"},
    {key: "vpc", value: "el-icon-menu"},
    {key: "vm", value: "el-icon-s-platform"},
    {key: "networksecuritygroup", value: "el-icon-lock"},
    {key: "disk", value: "el-icon-wallet"},
    {key: "storage", value: "el-icon-wallet"},
    {key: "others", value: "el-icon-s-unfold"},
  ];
  export default {
    name: "CardContentRule",
    components: {
      TablePagination,
      DialogFooter,
      SeverityType,
    },
    props: {
      selectNodeIds: Array,
    },
    watch: {
      selectNodeIds() {
        this.search();
      },
      batchReportId() {
      }
    },
    data() {
      return {
        tags: [],
        currentPage: 1,
        pageSize: 10,
        tableData: [],
        total: 0,
        groupData: [],
        groupTotal: 0,
        reportData: [],
        reportTotal: 0,
        tagData: [],
        tagTotal: 0,
        regionsData: [],
        regionsTotal: 0,
        resourceData: [],
        resourceTotal: 0,
        loading: false,
        condition: {
          components: Object
        },
        direction: 'rtl',
        ruleVisible: false,
        ruleForm: {},
        plugins: [],
        severityOptions: [],
        ruleSetOptions: [],
        inspectionSeportOptions: [],
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
      }
    },
    methods: {
      changeTab () {
        this.currentPage = 1;
        this.pageSize = 10;
      },
      init() {
        this.tagLists();
        this.activePlugin();
        this.severityOptionsFnc();
        this.ruleSetOptionsFnc();
        this.inspectionSeportOptionsFnc();
        this.search();
      },
      tagLists() {
        this.result = this.$get(ruleTagsUrl, response => {
          this.tags = response.data;
        });
      },
      //查询插件
      activePlugin() {
        this.result = this.$get(cloudPluginUrl, response => {
          let data = response.data;
          this.plugins =  data;
        });
      },
      async viewRule (item) {
        await this.$get(getRuleUrl + item.id, response => {
          this.ruleForm = response.data;
          if (typeof(this.ruleForm.parameter) == 'string') this.ruleForm.parameter = JSON.parse(this.ruleForm.parameter);
          this.ruleForm.tagKey = this.ruleForm.tags[0];
          this.ruleVisible = true;
        });
      },
      //查询列表
      async search() {
        if (!!this.selectNodeIds) {
          this.condition.accountId = this.selectNodeIds[0];
        } else {
          this.condition.accountId = null;
        }
        this.result = await this.$post(dashboardPointUrl + this.currentPage + "/" + this.pageSize, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
          for (let data of this.tableData) {
            this.getAssets(data);
          }
        });
        this.result = await this.$post(groupListUrl + this.currentPage + "/" + this.pageSize, {accountId: this.selectNodeIds[0]}, response => {
          let data = response.data;
          this.groupTotal = data.itemCount;
          this.groupData = data.listObject;
        });
        this.result = await this.$post(reportListUrl + this.currentPage + "/" + this.pageSize, {accountId: this.selectNodeIds[0]}, response => {
          let data = response.data;
          this.reportTotal = data.itemCount;
          this.reportData = data.listObject;
        });
        this.result = await this.$post(tagListUrl + this.currentPage + "/" + this.pageSize, {accountId: this.selectNodeIds[0]}, response => {
          let data = response.data;
          this.tagTotal = data.itemCount;
          this.tagData = data.listObject;
        });
        this.result = await this.$post(regionsListUrl + this.currentPage + "/" + this.pageSize, {accountId: this.selectNodeIds[0]}, response => {
          let data = response.data;
          this.regionsTotal = data.itemCount;
          this.regionsData = data.listObject;
        });
        this.result = await this.$post(resourceListUrl + this.currentPage + "/" + this.pageSize, {accountId: this.selectNodeIds[0]}, response => {
          let data = response.data;
          this.resourceTotal = data.itemCount;
          this.resourceData = data.listObject;
        });
      },
      sort(column) {
        _sort(column, this.condition);
        this.init();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.init();
      },
      select(selection) {
        this.selectIds.clear()
        selection.forEach(s => {
          this.selectIds.add(s.id)
        })
      },
      handleClose() {
        this.ruleVisible =  false;
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
      async getAssets (item) {
        await this.$get(getResourceTypesById + item.id, response => {
          item.assets = this.checkoutAssets(response.data);
        });
      },
      checkoutAssets (resource) {
        for(let item of assets){
          if (resource.indexOf(item.key) > -1) {
            return item.value;
          }
        }
        return "el-icon-s-platform";
      },
      severityOptionsFnc () {
        this.severityOptions = severityOptions;
      },
      ruleSetOptionsFnc () {
        this.$get(ruleGroupsUrl + null, res => {
          this.ruleSetOptions = res.data;
        });
      },
      inspectionSeportOptionsFnc () {
        this.$get(ruleInspectionReport, res => {
          this.inspectionSeportOptions = res.data;
        });
      },
    },
    mounted() {
      this.init();
    },
  }

</script>

<style scoped>
  .box-card {
    width: 100%;
  }
  .el-row {
    margin-bottom: 20px;
  }
  .el-col {
    border-radius: 4px;
  }
  .bg-purple-dark {
    background: #99a9bf;
  }
  .bg-purple {
    background: #ffffff;
  }
  .bg-purple-light {
    background: #e5e9f2;
  }
  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  }
  .row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
  }
  .dashboard-head-title {
    text-align: center;
    font-size: initial;
    vertical-align:middle;
    margin-top: 8px;
  }
  .dashboard-head-value {
    text-align: center;
    font-weight: 500;
    font-size: larger;
    vertical-align:middle;
  }
  .md-primary {
    font-size: larger;
    color: rgb(0,33,51);
    text-decoration:underline;
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
    width: 60%;
  }
  .md-primary {
    font-size: larger;
    text-decoration:underline;
  }
  .text-click {
    color: #0066ac;
  }
  /deep/ :focus{outline:0;}
</style>
