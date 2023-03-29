<template>
  <main-container v-loading="result.loading">

    <div id="pdfDom">

      <el-card class="table-card el-row-card">

        <account-change :project-name="currentAccount" @cloudAccountSwitch="cloudAccountSwitch" @selectAccount="selectAccount"/>

        <el-divider><i class="el-icon-tickets"></i></el-divider>

      </el-card>

      <el-card class="table-card">
        <template v-slot:header>
          <el-tabs type="card" v-model="activeName" @tab-click="handleClick">
            <el-tab-pane :label="$t('resource.result_list')" name="first"></el-tab-pane>
            <el-tab-pane :label="$t('resource.cloud_resource_detail_result')" name="second"></el-tab-pane>
          </el-tabs>
          <table-header :condition.sync="condition" @search="search"
                        :show-name="false" v-if="activeName === 'first'"
                        :items="items" :columnNames="columnNames"
                        :checkedColumnNames="checkedColumnNames" :checkAll="checkAll" :isIndeterminate="isIndeterminate"
                        @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange" @handleCheckAllChange="handleCheckAllChange"/>
        </template>

        <!-- result first -->
        <hide-table
          v-if="activeName === 'first'"
          :table-data="tableData"
          @sort-change="sort"
          @filter-change="filter"
          @select-all="select"
          @select="select"
        >
          <el-table-column type="index" min-width="40"/>
          <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('resourceTypes')" :label="$t('resource.i18n_task_type')" min-width="160" show-overflow-tooltip>
          <span>
            <template v-for="tag in tagSelect">
              <span :key="tag.value" v-if="scope.row.ruleTags">
                <span :key="tag.tagKey" v-if="scope.row.ruleTags.indexOf(tag.tagKey) > -1">
                  {{ tag.tagName }}
                </span>
              </span>
            </template>
              {{ scope.row.resourceTypes }}
          </span>
          </el-table-column>
          <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('taskName')" :label="$t('rule.rule_name')" min-width="200" show-overflow-tooltip>
            <el-link type="primary" :underline="false" class="md-primary text-click" @click="showTaskDetail(scope.row)">
              {{ scope.row.taskName }}
            </el-link>
          </el-table-column>
          <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('severity')" :label="$t('rule.severity')" min-width="110"
                           :sort-by="['CriticalRisk', 'HighRisk', 'MediumRisk', 'LowRisk']" prop="severity" :sortable="true" show-overflow-tooltip>
            <severity-type :row="scope.row"></severity-type>
          </el-table-column>
          <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('status')" :label="$t('resource.status')" min-width="130" prop="status" sortable show-overflow-tooltip>
            <el-button @click="showTaskLog(scope.row)" plain size="medium" type="primary" v-if="scope.row.status === 'UNCHECKED'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.status === 'APPROVED'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button @click="showTaskLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.status === 'PROCESSING'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
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
          </el-table-column>
          <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('returnSum')" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="80">
            <el-tooltip class="item" effect="dark" :content="$t('history.resource_result')" placement="top">
              <span v-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
              <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum == 0)">
              {{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}
            </span>
              <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)">
              <el-link type="primary" class="text-click" @click="goResource(scope.row)">
                {{scope.row.returnSum }}/{{ scope.row.resourcesSum }}
              </el-link>
            </span>
            </el-tooltip>
          </el-table-column>
          <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('resourcesSum')" :label="$t('resource.status_on_off')" prop="resourcesSum" sortable show-overflow-tooltip min-width="110">
            <span v-if="scope.row.returnSum == 0" style="color: #46ad59;">{{ $t('resource.i18n_compliance_true') }}</span>
            <span v-else-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)"
                  style="color: #f84846;">{{ $t('resource.i18n_compliance_false') }}</span>
            <span v-else-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
          </el-table-column>
          <el-table-column prop="createTime" min-width="160" v-if="checkedColumnNames.includes('createTime')" :label="$t('account.update_time')" sortable show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>{{ scope.row.createTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column min-width="120" :label="$t('commons.operating')" fixed="right" show-overflow-tooltip>
            <template v-slot:default="scope">
              <table-operators :buttons="rule_buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </hide-table>

        <table-pagination v-if="activeName === 'first'" :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
        <!-- result first -->

        <!-- result second -->
        <el-card class="table-card" v-if="activeName === 'second'">

          <template v-slot:header>
            <table-header :condition.sync="resourceCondition" @search="resourceSearch"
                          :show-name="false" v-if="activeName === 'second'"
                          :items="items2" :columnNames="columnNames2"
                          :checkedColumnNames="checkedColumnNames2" :checkAll="checkAll2" :isIndeterminate="isIndeterminate2"
                          @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange2" @handleCheckAllChange="handleCheckAllChange2"/>
          </template>

          <hide-table
            v-if="activeName === 'second'"
            :table-data="resourceTableData"
            @sort-change="resourceSort"
            @filter-change="resourceFilter"
            @select-all="select"
            @select="select"
          >
            <!-- 展开 start -->
            <el-table-column type="expand" min-width="50">
              <template v-slot:default="props">

                <el-divider><i class="el-icon-folder-opened"></i></el-divider>
                <el-form v-if="props.row.resource !== '[]'">
                  <result-read-only :row="typeof(props.row.resource) === 'string'?JSON.parse(props.row.resource):props.row.resource"></result-read-only>
                  <el-divider><i class="el-icon-document-checked"></i></el-divider>
                </el-form>
              </template>
            </el-table-column>
            <!-- 展开 end -->
            <el-table-column type="index" min-width="50"/>
            <el-table-column v-slot:default="scope" v-if="checkedColumnNames2.includes('hummerId')" :label="$t('resource.Hummer_ID')" min-width="140">
              {{ scope.row.hummerId }}
            </el-table-column>
            <el-table-column v-slot:default="scope" v-if="checkedColumnNames2.includes('resourceType')" :label="$t('rule.resource_type')" min-width="150">
              {{ scope.row.resourceType }}
            </el-table-column>
            <el-table-column prop="regionName" v-if="checkedColumnNames2.includes('regionName')" :label="$t('account.regions')" min-width="110">
              <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                {{ scope.row.regionName }}
              </span>
              </template>
            </el-table-column>
            <el-table-column v-slot:default="scope" v-if="checkedColumnNames2.includes('severity')" :label="$t('rule.severity')" min-width="120"
                             :sort-by="['CriticalRisk', 'HighRisk', 'MediumRisk', 'LowRisk']" prop="severity" :sortable="true"
                             show-overflow-tooltip>
              <severity-type :row="scope.row"></severity-type>
            </el-table-column>
            <el-table-column v-slot:default="scope" v-if="checkedColumnNames2.includes('ruleName')" :label="$t('rule.rule_name')" min-width="160" show-overflow-tooltip>
              {{ scope.row.ruleName }}
            </el-table-column>
            <el-table-column prop="createTime" min-width="160" v-if="checkedColumnNames2.includes('createTime')" :label="$t('account.update_time')" sortable show-overflow-tooltip>
              <template v-slot:default="scope">
                <span>{{ scope.row.createTime | timestampFormatDate }}</span>
              </template>
            </el-table-column>
            <el-table-column min-width="100" :label="$t('commons.operating')" show-overflow-tooltip>
              <template v-slot:default="scope">
                <table-operators v-if="!!scope.row.suggestion" :buttons="resource_buttons2" :row="scope.row"/>
                <table-operators v-if="!scope.row.suggestion" :buttons="resource_buttons" :row="scope.row"/>
              </template>
            </el-table-column>
          </hide-table>
          <table-pagination :change="resourceSearch" :current-page.sync="resourcePage" :page-size.sync="resourceSize" :total="resourceTotal"/>

          <!--file-->
          <el-drawer class="rtl" :title="string2Key" :visible.sync="visible"  size="80%" :before-close="handleClose" :direction="direction" :destroy-on-close="true">
            <codemirror ref="cmEditor" v-model="string2PrettyFormat" class="code-mirror" :options="cmOptions" />
          </el-drawer>
          <!--file-->

        </el-card>
        <!-- result second -->

      </el-card>

    </div>

    <!--Task log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="65%"
               :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <result-log :row="logForm"></result-log>
      <template v-slot:footer>
        <dialog-footer
          @cancel="logVisible = false"
          @confirm="logVisible = false"/>
      </template>
    </el-drawer>
    <!--Task log-->

    <!--Task detail-->
    <el-drawer v-if="detailVisible" :close-on-click-modal="false" class="rtl" :visible.sync="detailVisible" size="60%"
               :show-close="false" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <div slot="title" class="dialog-title">
        <span>{{ $t('resource.i18n_detail') }}</span>
        <i class="el-icon-close el-icon-close-detail" @click="detailVisible=false"></i>
      </div>
      <el-form :model="detailForm" label-position="right" label-width="120px" size="small" :rules="rule"
               ref="detailForm">
        <el-form-item class="el-form-item-dev">
          <el-tabs type="border-card" @tab-click="showCodemirror">
            <el-tab-pane>
              <span slot="label"><i class="el-icon-reading"></i> {{ $t('rule.rule') }}</span>
              <el-form label-position="left" inline class="demo-table-expand">
                <el-form-item :label="$t('account.cloud_platform')" v-if="detailForm.pluginIcon">
                        <span>
                          <img :src="require(`@/assets/img/platform/${detailForm.pluginIcon}`)"
                               style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                           &nbsp;&nbsp; {{ detailForm.pluginName }}
                        </span>
                </el-form-item>
                <el-form-item :label="$t('rule.rule_name')">
                  <el-tooltip class="item" effect="dark" :content="detailForm.taskName" placement="top-start">
                    <span v-if="detailForm.taskName" class="view-text">{{ detailForm.taskName }}</span>
                  </el-tooltip>
                </el-form-item>
                <el-form-item :label="$t('resource.i18n_task_type')" v-if="detailForm.ruleTags">
                      <span>
                        <template v-for="tag in tagSelect">
                          <span :key="tag.tagKey" v-if="detailForm.ruleTags.indexOf(tag.tagKey) > -1"> {{ tag.tagName }} </span>
                        </template>
                        <span>{{ detailForm.resourceTypes }}</span>
                      </span>
                </el-form-item>
                <el-form-item :label="$t('rule.severity')">
                  <severity-type :row="detailForm"></severity-type>
                </el-form-item>
                <el-form-item :label="$t('resource.status')">
                  <el-button plain size="mini" type="primary" v-if="detailForm.status === 'UNCHECKED'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                  </el-button>
                  <el-button plain size="mini" type="primary" v-else-if="detailForm.status === 'APPROVED'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                  </el-button>
                  <el-button plain size="mini" type="primary" v-else-if="detailForm.status === 'PROCESSING'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                  </el-button>
                  <el-button plain size="mini" type="success" v-else-if="detailForm.status === 'FINISHED'">
                    <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                  </el-button>
                  <el-button plain size="mini" type="danger" v-else-if="detailForm.status === 'ERROR'">
                    <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                  </el-button>
                  <el-button plain size="mini" type="warning" v-else-if="detailForm.status === 'WARNING'">
                    <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                  </el-button>
                </el-form-item>
                <el-form-item :label="$t('account.creator')">
                  <span>{{ detailForm.applyUser }}</span>
                </el-form-item>
                <el-form-item :label="$t('account.create_time')">
                  <span>{{ detailForm.createTime | timestampFormatDate }}</span>
                </el-form-item>
              </el-form>
              <div style="color: red;margin-left: 10px;">
                注: {{ detailForm.description }}
              </div>
            </el-tab-pane>
            <el-tab-pane>
              <span slot="label"><i class="el-icon-info"></i> {{ $t('rule.rule_detail') }}</span>
              <codemirror ref="cmEditor" v-model="detailForm.customData" class="code-mirror" :options="cmOptions"/>
            </el-tab-pane>
          </el-tabs>
        </el-form-item>
      </el-form>
    </el-drawer>
    <!--Task detail-->

    <!--regulation report-->
    <el-drawer class="rtl" :title="$t('resource.regulation')" :visible.sync="regulationVisible"  size="60%" :before-close="handleClose" :direction="direction" :destroy-on-close="true">
      <el-card class="table-card" :body-style="{ padding: '15px', margin: '15px' }" v-for="(data, index) in regulationData" :key="data.id">
        <el-row class="el-row-c">
          <el-col :span="8"><span style="color: #215d9a;">{{ '(' + (index + 1) +') ' + $t('resource.basic_requirements_for_grade_protection') }}</span></el-col>
          <el-col :span="16"><span>{{ data.project }}</span></el-col>
        </el-row>
        <el-row class="el-row-c">
          <el-col :span="8"><span style="color: #215d9a;">{{ $t('resource.security_level') }}</span></el-col>
          <el-col :span="16"><span>{{ data.itemSortFirstLevel }}</span></el-col>
        </el-row>
        <el-row class="el-row-c">
          <el-col :span="8"><span style="color: #215d9a;">{{ $t('resource.control_point') }}</span></el-col>
          <el-col :span="16"><span>{{ data.itemSortSecondLevel }}</span></el-col>
        </el-row>
        <el-row class="el-row-c">
          <el-col :span="8"><span style="color: #215d9a;">{{ $t('resource.suggestions_for_improvement') }} <i class="el-icon-question"></i></span></el-col>
          <el-col :span="16"><span>{{ data.improvement }}</span></el-col>
        </el-row>
      </el-card>
    </el-drawer>
    <!--regulation report-->

  </main-container>
</template>

<script>
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import Container from "@/business/components/common/components/Container";
import MainContainer from "@/business/components/common/components/MainContainer";
import TableOperators from "@/business/components/common/components/TableOperators";
import {_filter, _sort, getCurrentAccountID} from "@/common/js/utils";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {RESOURCE_CONFIGS, RESULT_CONFIGS} from "@/business/components/common/components/search/search-components";
import TableOperator from "@/business/components/common/components/TableOperator";
import CenterChart from "@/business/components/common/components/CenterChart";
import ResultLog from "@/business/components/resource/home/ResultLog";
import AccountChange from "@/business/components/oss/head/AccountSwitch";
import TableSearchBar from "@/business/components/common/components/TableSearchBar";
import ResultReadOnly from "@/business/components/common/components/ResultReadOnly";
import SeverityType from "@/business/components/common/components/SeverityType";
import {ACCOUNT_ID} from "@/common/js/constants";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {
  ossManualListUrl,
  resourceRegionDataUrl,
  resourceRegulationUrl,
  resourceRuleDataUrl,
  resourceSeverityDataUrl,
  resourceTypeDataUrl,
  string2PrettyFormatUrl
} from "@/api/cloud/resource/resource";
import {ossResourceListUrl} from "@/api/cloud/oss/oss";
import {tagRuleListUrl} from "@/api/cloud/rule/rule";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'resource.i18n_task_type',
    props: 'resourceTypes',
    disabled: false
  },
  {
    label: 'rule.rule_name',
    props: 'taskName',
    disabled: false
  },
  {
    label: 'rule.severity',
    props: 'severity',
    disabled: false
  },
  {
    label: 'resource.status',
    props: 'status',
    disabled: false
  },
  {
    label: 'resource.i18n_not_compliance',
    props: 'returnSum',
    disabled: false
  },
  {
    label: 'resource.status_on_off',
    props: 'resourcesSum',
    disabled: false
  },
  {
    label: 'account.update_time',
    props: 'createTime',
    disabled: false
  }
];
const columnOptions2 = [
  {
    label: 'resource.Hummer_ID',
    props: 'hummerId',
    disabled: false
  },
  {
    label: 'rule.resource_type',
    props: 'resourceType',
    disabled: false
  },
  {
    label: 'account.regions',
    props: 'regionName',
    disabled: false
  },
  {
    label: 'rule.rule_name',
    props: 'ruleName',
    disabled: false
  },
  {
    label: 'rule.severity',
    props: 'severity',
    disabled: false
  },
  {
    label: 'account.update_time',
    props: 'createTime',
    disabled: false
  }
];

/* eslint-disable */
export default {
  name: "Risk",
  components: {
    TableOperators,
    MainContainer,
    Container,
    TableHeader,
    TablePagination,
    TableOperator,
    DialogFooter,
    CenterChart,
    ResultLog,
    AccountChange,
    TableSearchBar,
    ResultReadOnly,
    SeverityType,
    HideTable,
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
        components: RESULT_CONFIGS
      },
      accountId: localStorage.getItem(ACCOUNT_ID),
      direction: 'rtl',
      tagSelect: [],
      timer: '',
      currentAccount: '',
      buttons: [
        {
          tip: this.$t('resource.delete_result'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      rule_buttons: [
        {
          tip: this.$t('resource.regulation'), icon: "el-icon-document", type: "warning",
          exec: this.showSeverityDetail
        },
      ],
      resource_buttons: [
        {
          tip: this.$t('resource.regulation'), icon: "el-icon-document", type: "warning",
          exec: this.showSeverityDetail
        },
      ],
      resource_buttons2: [
        {
          tip: this.$t('rule.suggestion'), icon: "el-icon-share", type: "primary",
          exec: this.handleSuggestion
        },
        {
          tip: this.$t('resource.regulation'), icon: "el-icon-document", type: "warning",
          exec: this.showSeverityDetail
        },
      ],
      logVisible: false,
      detailVisible: false,
      logForm: {cloudTaskItemLogDTOs: []},
      detailForm: {},
      rule: {
        pluginId: [
          {required: true, message: this.$t('user.input_id'), trigger: 'blur'},
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
          {
            required: true,
            message: this.$t('user.special_characters_are_not_supported'),
            trigger: 'blur'
          }
        ],
      },
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
      activeName: 'first',
      regionData: [],
      severityData: [],
      resourceTypeData: [],
      ruleData: [],
      regionCondition: {},
      severityCondition: {},
      resourceTypeCondition: {},
      ruleCondition: {},
      resourceTableData: [],
      resourcePage: 1,
      resourceSize: 10,
      resourceTotal: 0,
      string2Key: "",
      string2PrettyFormat: "",
      visible: false,
      resourceCondition: {
        components: RESOURCE_CONFIGS
      },
      highRegionRow: true,
      highSeverityRow: true,
      highResourceTypeRow: true,
      highRuleRow: true,
      regulationData: [],
      regulationVisible: false,
      rowIndex: '',
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'rule.rule_name',
          id: 'taskName'
        },
        {
          name: 'resource.i18n_task_type',
          id: 'resourceTypes'
        }
      ],
      checkAll: true,
      isIndeterminate: false,
      checkedColumnNames2: columnOptions2.map((ele) => ele.props),
      columnNames2: columnOptions2,
      //名称搜索
      items2: [
        {
          name: 'rule.rule_name',
          id: 'ruleName'
        },
        {
          name: 'rule.resource_type',
          id: 'resourceType'
        },
        {
          name: 'resource.Hummer_ID',
          id: 'hummerId',
        },
        {
          name: 'account.regions',
          id: 'regionName',
        },
      ],
      checkAll2: true,
      isIndeterminate2: false,
    }
  },
  methods: {
    handleCheckedColumnNamesChange(value) {
      const checkedCount = value.length;
      this.checkAll = checkedCount === this.columnNames.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.columnNames.length;
      this.checkedColumnNames = value;
    },
    handleCheckAllChange(val) {
      this.checkedColumnNames = val ? this.columnNames.map((ele) => ele.props) : [];
      this.isIndeterminate = false;
      this.checkAll = val;
    },
    handleCheckedColumnNamesChange2(value) {
      const checkedCount = value.length;
      this.checkAll2 = checkedCount === this.columnNames2.length;
      this.isIndeterminate2 = checkedCount > 0 && checkedCount < this.columnNames2.length;
      this.checkedColumnNames2 = value;
    },
    handleCheckAllChange2(val) {
      this.checkedColumnNames2 = val ? this.columnNames2.map((ele) => ele.props) : [];
      this.isIndeterminate2 = false;
      this.checkAll2 = val;
    },
    select(selection) {
    },
    sort(column) {
      _sort(column, this.condition);
      this.init();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.init();
    },
    resourceSort(column) {
      _sort(column, this.resourceCondition);
      this.init();
    },
    resourceFilter(filters) {
      _filter(filters, this.resourceCondition);
      this.init();
    },
    handleSuggestion(item) {
      window.open(item.suggestion,'_blank','');
    },
    handleDelete(obj) {
      this.$alert(this.$t('account.delete_confirm') + obj.name + this.$t('resource.resource_result') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(resourceAccountDeleteUrl + obj.id, res => {
              setTimeout(function () {
                window.location.reload()
              }, 2000);
              this.$success(this.$t('commons.delete_success'));
            });
          }
        }
      });
    },
    cloudAccountSwitch(accountId) {
      this.accountId = accountId;
      this.search();
      this.regionDataSearch();
      this.ruleDataSearch();
      this.resourceTypeDataSearch();
      this.severityDataSearch();
    },
    async search() {
      let url = ossManualListUrl + this.currentPage + "/" + this.pageSize;
      //在这里实现事件
      this.condition.accountId = this.accountId;
      this.result = await this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    resourceSearch() {
      let url = ossResourceListUrl + this.resourcePage + "/" + this.resourceSize;
      this.resourceCondition.accountId = this.accountId;
      this.result = this.$post(url, this.resourceCondition, response => {
        let data = response.data;
        this.resourceTotal = data.itemCount;
        this.resourceTableData = data.listObject;
      });
    },
    async initSelect() {
      this.tagSelect = [];
      await this.$get(tagRuleListUrl, response => {
        this.tagSelect = response.data;
      });
      if (!!getCurrentAccountID()) {
        this.accountId = getCurrentAccountID();
      }
    },
    goResource(params) {
      if (params.returnSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      this.activeName = 'second';
      this.highRuleRow = true;
      this.resourceCondition.taskId = params.id;
      for (let i=0; i < this.ruleData.length; i++) {
        if (params.id === this.ruleData[i].id) {
          this.rowIndex = i;
          break;
        } else {
          this.rowIndex = '';
        }
      }
      this.resourceSearch();
    },
    init() {
      this.initSelect();
      this.search();
      this.regionDataSearch();
      this.severityDataSearch();
      this.resourceTypeDataSearch();
      this.ruleDataSearch();
      this.resourceSearch();
    },
    ruleTableRowClassName({row, rowIndex}) {
      if (this.rowIndex) {
        if(this.rowIndex === rowIndex) {
          return 'current-row';
        }
        return '';
      } else {
        return '';
      }
    },
    showTaskLog(cloudTask) {
      let showLogTaskId = cloudTask.id;
      let url = "";
      if (showLogTaskId) {
        url = cloudTaskLogByIdUrl;
      }
      this.logForm.cloudTaskItemLogDTOs = [];
      this.logForm.showLogTaskId = showLogTaskId;
      this.$get(url + showLogTaskId, response => {
        this.logForm.cloudTaskItemLogDTOs = response.data;
        this.logVisible = true;
      });
    },
    showTaskDetail(item) {
      this.detailForm = {};
      this.$get(cloudTaskDetailUrl + item.id, response => {
        if (response.success) {
          this.detailForm = response.data;
          this.detailVisible = true;
        }
      });
    },
    handleClose() {
      this.logVisible = false;
      this.detailVisible = false;
      this.visible =  false;
      this.regulationVisible = false;
    },
    showCodemirror() {
      setTimeout(() => {
        this.$refs.cmEditor.codemirror.refresh();
      }, 50);
    },
    handleClick(tab, event) {
      this.resourceCondition.taskId = null;
      this.regionDataSearch();
      this.severityDataSearch();
      this.resourceTypeDataSearch();
      this.ruleDataSearch();
      this.resourceSearch();
      this.activeName = tab.name;
      this.rowIndex = '';
    },
    regionFilter() {
      this.regionDataSearch();
    },
    regionDataSearch() {
      this.regionCondition.id = this.accountId;
      this.$post(resourceRegionDataUrl, this.regionCondition, response => {
        let data = response.data;
        this.regionData = data;
      });
    },
    severityFilter() {
      this.severityDataSearch();
    },
    severityDataSearch() {
      this.severityCondition.id = this.accountId;
      this.$post(resourceSeverityDataUrl, this.severityCondition, response => {
        let data = response.data;
        this.severityData = data;
      });
    },
    resourceTypeFilter() {
      this.resourceTypeDataSearch();
    },
    resourceTypeDataSearch() {
      this.resourceTypeCondition.id = this.accountId;
      this.$post(resourceTypeDataUrl, this.resourceTypeCondition, response => {
        let data = response.data;
        this.resourceTypeData = data;
      });
    },
    ruleFilter() {
      this.ruleDataSearch();
    },
    ruleDataSearch() {
      this.ruleCondition.id = this.accountId;
      this.$post(resourceRuleDataUrl, this.ruleCondition, response => {
        let data = response.data;
        this.ruleData = data;
      });
    },
    showInformation (row, details, title) {
      this.string2Key = title;
      this.string2PrettyFormat = "";
      if (row) {
        this.$post(string2PrettyFormatUrl, {json: details}, res => {
          this.string2PrettyFormat = res.data;
        });
      } else {
        this.string2PrettyFormat = details;
      }
      this.visible =  true;
    },
    handleRegionRow(row) {
      if (this.resourceCondition.regionId) {
        this.highRegionRow = false;
        this.resourceCondition.regionId = null;
      } else {
        this.highRegionRow = true;
        this.resourceCondition.regionId = row.id;
      }
      this.resourceSearch();
    },
    handleSeverityRow(row) {
      if (this.resourceCondition.severity) {
        this.highSeverityRow = false;
        this.resourceCondition.severity = null;
      } else {
        this.highSeverityRow = true;
        this.resourceCondition.severity = row.id;
      }
      this.resourceSearch();
    },
    handleResourceTypeRow(row) {
      if (this.resourceCondition.resourceType) {
        this.highResourceTypeRow = false;
        this.resourceCondition.resourceType = null;
      } else {
        this.highResourceTypeRow = true;
        this.resourceCondition.resourceType = row.id;
      }
      this.resourceSearch();
    },
    handleRuleRow(row) {
      this.rowIndex = '';
      if (this.resourceCondition.taskId) {
        this.highRuleRow = false;
        this.resourceCondition.taskId = null;
      } else {
        this.highRuleRow = true;
        this.resourceCondition.taskId = row.id;
      }
      this.resourceSearch();
    },
    showSeverityDetail(item) {
      this.$get(resourceRegulationUrl + item.ruleId, response => {
        if (response.success) {
          this.regulationData = response.data;
          this.regulationVisible = true;
        }
      });
    },
    selectAccount(accountId, accountName) {
      this.accountId = accountId;
      this.currentAccount = accountName;
    },
  },
  computed: {
    codemirror() {
      return this.$refs.cmEditor.codemirror;
    }
  },
  created() {
    this.init();
  },
}
</script>

<style scoped>
.el-cloud-row >>> .el-row {
  margin-bottom: 20px;
}

.el-col {
  border-radius: 4px;
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

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}

.el-form-item-dev >>> .el-form-item__content {
  margin-left: 0 !important;
}

.grid-content-log-span {
  width: 40%;
  float: left;
  vertical-align: middle;
  display: table-cell;
  margin: 6px 0;
}

.grid-content-status-span {
  width: 20%;
  float: left;
  vertical-align: middle;
  display: table-cell;
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
  cursor: pointer;
}

.view-text {
  display: inline-block;
  white-space: nowrap;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
}

.text-click {
  color: #0066ac;
  text-decoration: none;
}

.rtl >>> .el-drawer__body {
  overflow-y: auto;
}

.el-row-card {
  padding: 0 20px 0 20px;
  margin: 0 0 20px 0;
}

.el-row-card >>> .el-card__body {
  margin: 30px 0 0 0;
}

.el-row-body >>> .el-card__header {
  padding: 15px;
}

.el-row-body >>> .el-row {
  margin-bottom: 0;
}

.split {
  height: 120px;
  border-left: 1px solid #D8DBE1;
}

.icon-loading {
  font-size: 100px;
}

.el-row-body {
  line-height: 1.15;
}

.el-col-su >>> .el-card {
  margin: 10px 0;
}

.round {
  font-size: 13px;
  margin: 0 0 0 5px;
  padding: 1px 3px 1px 3px;
  float: right;
}

.el-row-c {
  margin: 10px;
}

/deep/ :focus {
  outline: 0;
}
</style>

