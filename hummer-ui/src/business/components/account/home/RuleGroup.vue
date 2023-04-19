<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <group-table-header :condition.sync="condition" @search="search"
                        :title="$t('rule.rule_set_list')" @more="more" @menu="menu"
                        @create="create" :createTip="$t('rule.create_rule_set')"
                        :show-create="true" :show-list="true" :listStatus="listStatus"
                        :items="items" :columnNames="columnNames" @delete="deleteBatch" :show-delete="true"
                        :checkedColumnNames="checkedColumnNames" :checkAll="checkAll" :isIndeterminate="isIndeterminate"
                        @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange" @handleCheckAllChange="handleCheckAllChange"/>
        </template>

        <el-row :gutter="20" class="el-row-body pdfDom" v-show="listStatus === 2">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-for="(data, index) in ftableData"
                  :key="index" class="el-col el-col-su">
            <el-card :body-style="{ padding: '15px' }">
              <div style="height: 130px;">
                <el-row :gutter="20">
                  <el-col :span="3">
                    <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/platform/${data.pluginIcon}`)">
                      <div slot="error" class="image-slot">
                        <i class="el-icon-picture-outline"></i>
                      </div>
                    </el-image>
                  </el-col>
                  <el-col :span="21">
                    <el-row class="plugin" v-if="checkedColumnNames.includes('pluginName')">
                      <span class="plugin-name">{{ data.pluginName }}</span>
                      <span class="plugin-type">
                        <el-tag type="success" size="mini">{{ data.type }}</el-tag>
                      </span>
                    </el-row>
                    <el-row class="desc" v-if="checkedColumnNames.includes('description')">{{ data.description }}</el-row>
                  </el-col>
                </el-row>
              </div>
              <el-divider></el-divider>
              <div style="padding: 0 14px 14px 14px;">
                <el-row>
                  <el-col :span="19">
                    <span class="da-na" v-if="checkedColumnNames.includes('name')">{{ data.name }}</span>
                  </el-col>
                  <el-col :span="5" v-if="checkedColumnNames.includes('flag')">
                    <el-button size="medium" type="danger" class="round" round v-if="data.flag === true">
                      {{ $t('rule.tag_flag_true') }}
                    </el-button>
                    <el-button size="medium" type="success" class="round" round v-else-if="data.flag === false">
                      {{ $t('rule.tag_flag_false') }}
                    </el-button>
                  </el-col>
                </el-row>
                <span class="button time pa-na">
              </span>
                <div class="bottom clearfix">
                  <div class="time time2">
                    <span class="pa-time">{{ data.level }}&nbsp;<span class="pa-time2">{{ $t('rule.rule_sum', [data.ruleSum]) }}</span></span>
                  </div>
                  <el-dropdown class="button button-drop" @command="(command)=>{handleCommand(command, data)}">
                    <span class="el-dropdown-link">
                      {{ $t('package.operate') }}
                      <i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown" v-if="!!data.flag">
                      <el-dropdown-item command="handleScan">{{ $t('account.scan') }}</el-dropdown-item>
                      <el-dropdown-item command="handleInfo">{{ $t('commons.detail') }}</el-dropdown-item>
                      <el-dropdown-item command="handleBind">{{ $t('rule.bind') }}</el-dropdown-item>
                      <el-dropdown-item command="handleList">{{ $t('dashboard.rules') }}</el-dropdown-item>
                    </el-dropdown-menu>
                    <el-dropdown-menu slot="dropdown" v-if="!data.flag">
                      <el-dropdown-item command="handleScan">{{ $t('account.scan') }}</el-dropdown-item>
                      <el-dropdown-item command="handleEdit">{{ $t('commons.edit') }}</el-dropdown-item>
                      <el-dropdown-item command="handleBind">{{ $t('rule.bind') }}</el-dropdown-item>
                      <el-dropdown-item command="handleList">{{ $t('dashboard.rules') }}</el-dropdown-item>
                      <el-dropdown-item command="handleDelete">{{ $t('commons.delete') }}</el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <f-table-pagination v-show="listStatus === 2" :change="search" :current-page.sync="fcurrentPage" :page-size.sync="fpageSize" :total="ftotal"/>

        <hide-table v-show="listStatus === 1"
          :table-data="tableData"
          @sort-change="sort"
          @filter-change="filter"
          @select-all="select"
          @select="select"
        >
          <el-table-column type="selection" id="selection"  prop="selection" min-width="50">
          </el-table-column>
          <el-table-column type="index" min-width="40"/>
          <el-table-column prop="name" v-if="checkedColumnNames.includes('name')" :label="$t('rule.rule_set_name')" min-width="180" show-overflow-tooltip></el-table-column>
          <el-table-column prop="description" v-if="checkedColumnNames.includes('description')" :label="$t('commons.description')" min-width="600" show-overflow-tooltip></el-table-column>
          <el-table-column :label="$t('account.cloud_platform')" v-if="checkedColumnNames.includes('pluginName')" min-width="180" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.pluginName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('rule.tag_flag')" v-if="checkedColumnNames.includes('flag')" min-width="140" show-overflow-tooltip>
            <template v-slot:default="scope">
              <el-tag size="mini" type="danger" v-if="scope.row.flag === true">
                {{ $t('rule.tag_flag_true') }}
              </el-tag>
              <el-tag size="mini" type="success" v-else-if="scope.row.flag === false">
                {{ $t('rule.tag_flag_false') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="level" v-if="checkedColumnNames.includes('level')" :label="$t('resource.equal_guarantee_level')" min-width="140" show-overflow-tooltip></el-table-column>
          <el-table-column min-width="210" :label="$t('commons.operating')" fixed="right">
            <template v-slot:default="scope">
              <table-operators v-if="!!scope.row.flag" :buttons="buttonsN" :row="scope.row"/>
              <table-operators v-if="!scope.row.flag" :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </hide-table>
        <table-pagination v-show="listStatus === 1" :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>

      </el-card>

      <!--Create group-->
      <el-drawer class="rtl" :title="$t('rule.create_group')" :visible.sync="createVisible" size="45%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-form v-loading="groupResult.loading" :model="createForm" label-position="right" label-width="120px" size="small" :rules="rule" ref="createForm">
          <el-form-item :label="$t('rule.rule_set')" prop="name">
            <el-input v-model="createForm.name" autocomplete="off" :placeholder="$t('rule.rule_set')"/>
          </el-form-item>
          <el-form-item :label="$t('commons.description')" prop="description">
            <el-input type="textarea" :rows="5" v-model="createForm.description" autocomplete="off" :placeholder="$t('commons.description')"/>
          </el-form-item>
          <el-form-item :label="$t('resource.equal_guarantee_level')" prop="level">
            <el-input v-model="createForm.level" autocomplete="off" :placeholder="$t('resource.equal_guarantee_level')"/>
          </el-form-item>
          <el-form-item :label="$t('account.cloud_platform')" prop="pluginId" :rules="{required: true, message: $t('account.cloud_platform') + this.$t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="createForm.pluginId" :placeholder="$t('account.please_choose_plugin')">
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
        </el-form>
        <dialog-footer
          @cancel="createVisible = false"
          @confirm="save(createForm, 'createForm')"/>
      </el-drawer>
      <!--Create group-->

      <!--Update group-->
      <el-drawer class="rtl" :title="$t('rule.update_group')" :visible.sync="updateVisible" size="45%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-form v-loading="groupResult.loading" :model="infoForm" label-position="right" label-width="120px" size="small" :rules="rule" ref="infoForm">
          <el-form-item :label="$t('rule.rule_set')" prop="name">
            <el-input v-model="infoForm.name" :disabled="infoForm.flag" autocomplete="off" :placeholder="$t('commons.please_input')"/>
          </el-form-item>
          <el-form-item :label="$t('commons.description')" prop="description">
            <el-input type="textarea" :rows="5" v-model="infoForm.description" :disabled="infoForm.flag" autocomplete="off" :placeholder="$t('commons.please_input')"/>
          </el-form-item>
          <el-form-item :label="$t('resource.equal_guarantee_level')" prop="level">
            <el-input v-model="infoForm.level" autocomplete="off" :placeholder="$t('resource.equal_guarantee_level')"/>
          </el-form-item>
          <el-form-item :label="$t('account.cloud_platform')" prop="pluginId" :rules="{required: true, message: $t('account.cloud_platform') + this.$t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="infoForm.pluginId" :disabled="infoForm.flag" :placeholder="$t('account.please_choose_plugin')">
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
        </el-form>
        <dialog-footer
          @cancel="updateVisible = false"
          @confirm="save(infoForm, 'infoForm')"/>
      </el-drawer>
      <!--Update group-->

      <!--Info group-->
      <el-drawer class="rtl" :title="$t('rule.update_group')" :visible.sync="infoVisible" size="45%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-form v-loading="groupResult.loading" :model="infoForm" label-position="right" label-width="120px" size="small" :rules="rule" ref="infoForm">
          <el-form-item :label="$t('rule.rule_set')" prop="name">
            {{ infoForm.name }}
          </el-form-item>
          <el-form-item :label="$t('commons.description')" prop="description">
            {{ infoForm.description }}
          </el-form-item>
          <el-form-item :label="$t('resource.equal_guarantee_level')" prop="level">
            {{ infoForm.level }}
          </el-form-item>
          <el-form-item :label="$t('account.cloud_platform')">
         &nbsp;&nbsp; {{ infoForm.pluginName }}
          </el-form-item>
        </el-form>
      </el-drawer>
      <!--Info group-->

      <!--rule list-->
      <el-drawer class="rtl" :visible.sync="listVisible" size="85%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div v-loading="groupResult.loading">
          <table-header :condition.sync="ruleCondition" @search="handleListSearch"
                        :title="$t('rule.rule_list')"
                        :items="items2" :columnNames="columnNames2"
                        :checkedColumnNames="checkedColumnNames2" :checkAll="checkAll2" :isIndeterminate="isIndeterminate2"
                        @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange2" @handleCheckAllChange="handleCheckAllChange2"/>
          <hide-table
            :table-data="ruleForm"
            @sort-change="sort"
            @filter-change="filter"
            @select-all="select"
            @select="select"
          >
            <el-table-column type="index" min-width="40"/>
            <el-table-column prop="name" v-if="checkedColumnNames2.includes('name')" :label="$t('rule.rule_name')" min-width="150" show-overflow-tooltip></el-table-column>
            <el-table-column :label="$t('rule.resource_type')" v-if="checkedColumnNames2.includes('resourceType')" min-width="80" show-overflow-tooltip>
              <template v-slot:default="scope">
                <span v-for="(resourceType, index) in scope.row.types" :key="index">[{{ resourceType }}] </span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('account.cloud_platform')" v-if="checkedColumnNames2.includes('pluginName')" min-width="110" show-overflow-tooltip>
              <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.pluginName }}
              </span>
              </template>
            </el-table-column>
            <el-table-column min-width="80" :label="$t('rule.severity')" v-if="checkedColumnNames2.includes('severity')" column-key="severity">
              <template v-slot:default="{row}">
                <severity-type :row="row"></severity-type>
              </template>
            </el-table-column>
            <el-table-column prop="description" :label="$t('rule.description')" v-if="checkedColumnNames2.includes('description')" min-width="220" show-overflow-tooltip></el-table-column>
            <el-table-column :label="$t('rule.status')" min-width="70" show-overflow-tooltip>
              <template v-slot:default="scope">
                <el-switch @change="changeStatus(scope.row)" v-model="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column prop="lastModified" min-width="150" v-if="checkedColumnNames2.includes('lastModified')" :label="$t('rule.last_modified')" sortable>
              <template v-slot:default="scope">
                <span><i class="el-icon-time"></i> {{ scope.row.lastModified | timestampFormatDate }}</span>
              </template>
            </el-table-column>
          </hide-table>
          <table-pagination :change="handleListSearch" :current-page.sync="ruleListPage" :page-size.sync="ruleListPageSize" :total="ruleListTotal"/>
        </div>
      </el-drawer>
      <!--rule list-->

      <!--rule bind-->
      <el-drawer class="rtl edit-dev-drawer" :title="$t('rule.rule_list_bind')" :visible.sync="bindVisible" size="85%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-card v-loading="groupResult.loading" class="table-card edit_dev" style="">
          <div style="text-align: center; margin: 25px;">
            <p style="text-align: center; padding: 10px;margin: 25px;color: red;background-color: aliceblue;">{{ $t('rule.rule_list_bind') }}</p>
            <el-transfer :titles="[$t('rule.source_rule'), $t('rule.target_rule')]" :filter-method="filterMethod" class="el-trans"
                         :filter-placeholder="$t('commons.search_by_name')" filterable v-model="cloudValue" :data="cloudData">
            </el-transfer>
          </div>
        </el-card>
        <dialog-footer
          @cancel="bindVisible = false"
          @confirm="bind()"/>
      </el-drawer>
      <!--rule bind-->

      <!--Create sync-->
      <el-drawer class="rtl" :title="$t('account.scan_group_quick')" :visible.sync="scanVisible" size="60%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-form v-loading="groupResult.loading" :model="scanForm" label-position="right" label-width="150px" size="small" ref="form" style="margin-top: 20px;">
          <el-form-item :label="$t('account.cloud_account')" :rules="{required: true, message: $t('account.cloud_account') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" filterable :clearable="true" v-model="scanForm.id" :placeholder="$t('account.please_choose_account')">
              <el-option
                v-for="item in accounts"
                :key="item.id"
                :label="item.name"
                :value="item.id">
                <img :src="require(`@/assets/img/platform/${item.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                &nbsp;&nbsp; {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div style="margin: 10px;">
          <dialog-footer
            @cancel="scanVisible = false"
            @confirm="saveScan"/>
        </div>
      </el-drawer>
      <!--Create sync-->

    </main-container>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import GroupTableHeader from "@/business/components/rule/head/GroupTableHeader";
import TableHeader from "@/business/components/common/components/TableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import FTablePagination from "../../common/pagination/FTablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../../common/components/DialogFooter";
import {_filter, _sort} from "@/common/js/utils";
import SeverityType from "@/business/components/common/components/SeverityType";
import {RULE_CONFIGS, RULE_GROUP_CONFIGS} from "../../common/components/search/search-components";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {
  bindRuleUrl,
  deleteGroupsUrl,
  ruleAllBindListUrl,
  ruleGroupDeleteUrl,
  ruleGroupListUrl,
  ruleGroupSaveUrl,
  ruleGroupUpdateUrl,
  ruleListUrl,
  ruleUnBindListUrl,
  scanByGroupUrl
} from "@/api/cloud/rule/rule";
import {cloudPluginUrl} from "@/api/system/system";
import {cloudListByGroupUrl} from "@/api/cloud/account/account";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'rule.rule_set_name',
    props: 'name',
    disabled: false
  },
  {
    label: 'commons.description',
    props: 'description',
    disabled: false
  },
  {
    label: 'account.cloud_platform',
    props: 'pluginName',
    disabled: false
  },
  {
    label: 'rule.tag_flag',
    props: 'flag',
    disabled: false
  },
  {
    label: 'resource.equal_guarantee_level',
    props: 'level',
    disabled: false
  },
];

const columnOptions2 = [
  {
    label: 'rule.rule_name',
    props: 'name',
    disabled: false
  },
  {
    label: 'rule.resource_type',
    props: 'resourceType',
    disabled: false
  },
  {
    label: 'account.cloud_platform',
    props: 'pluginName',
    disabled: false
  },
  {
    label: 'rule.severity',
    props: 'severity',
    disabled: false
  },
  {
    label: 'commons.description',
    props: 'description',
    disabled: false
  },
  {
    label: 'rule.last_modified',
    props: 'lastModified',
    disabled: false
  },
];

/* eslint-disable */
  export default {
    components: {
      TableOperators,
      MainContainer,
      Container,
      GroupTableHeader,
      TablePagination,
      FTablePagination,
      TableOperator,
      DialogFooter,
      SeverityType,
      TableHeader,
      HideTable,
    },
    data() {
      return {
        result: {},
        groupResult: {},
        condition: {
          components: RULE_GROUP_CONFIGS
        },
        ruleCondition: {
          components: RULE_CONFIGS
        },
        plugins: [],
        tableData: [],
        ftableData: [],
        createForm: {},
        infoForm: {},
        ruleForm: [],
        createVisible: false,
        updateVisible: false,
        infoVisible: false,
        listVisible: false,
        bindVisible: false,
        currentPage: 1,
        pageSize: 10,
        total: 0,
        fcurrentPage: 1,
        fpageSize: 12,
        ftotal: 0,
        loading: false,
        selectIds: new Set(),
        direction: 'rtl',
        rule: {
          name: [
            {required: true, message: this.$t('rule.tag_name') + this.$t('commons.cannot_be_empty'), trigger: 'blur'},
            {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
            {
              required: true,
              message: this.$t('rule.special_characters_are_not_supported'),
              trigger: 'blur'
            }
          ],
          description: [
            {required: true, message: this.$t('rule.description') + this.$t('commons.cannot_be_empty'), trigger: 'blur'},
            {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
            {
              required: true,
              message: this.$t('rule.special_characters_are_not_supported'),
              trigger: 'blur'
            }
          ]
        },
        buttonsN: [
          {
            tip: this.$t('account.scan'), icon: "el-icon-s-promotion", type: "danger",
            exec: this.handleScan
          },
          {
            tip: this.$t('commons.detail'), icon: "el-icon-edit-outline", type: "primary",
            exec: this.handleInfo
          },
          {
            tip: this.$t('rule.bind'), icon: "el-icon-plus", type: "warning",
            exec: this.handleBind
          },
          {
            tip: this.$t('rule.rule_list'), icon: "el-icon-tickets", type: "success",
            exec: this.handleList
          },
        ],
        buttons: [
          {
            tip: this.$t('account.scan'), icon: "el-icon-s-promotion", type: "danger",
            exec: this.handleScan
          },
          {
            tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
            exec: this.handleEdit
          },
          {
            tip: this.$t('rule.bind'), icon: "el-icon-plus", type: "warning",
            exec: this.handleBind
          },
          {
            tip: this.$t('rule.rule_list'), icon: "el-icon-tickets", type: "success",
            exec: this.handleList
          },
          {
            tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
            exec: this.handleDelete
          }
        ],
        ruleListPage: 1,
        ruleListPageSize: 10,
        ruleListTotal: 0,
        itemId: "",
        listStatus: 2,
        cloudValue: [],
        cloudData: [],
        groupId: '',
        scanVisible: false,
        scanForm: {},
        accounts: [],
        checkedColumnNames: columnOptions.map((ele) => ele.props),
        columnNames: columnOptions,
        //名称搜索
        items: [
          {
            name: 'rule.rule_set_name',
            id: 'name'
          },
          {
            name: 'commons.description',
            id: 'description'
          },
          {
            name: 'resource.equal_guarantee_level',
            id: 'level',
          },
        ],
        checkAll: true,
        isIndeterminate: false,
        checkedColumnNames2: columnOptions2.map((ele) => ele.props),
        columnNames2: columnOptions2,
        //名称搜索
        items2: [
          {
            name: 'rule.rule_name',
            id: 'name'
          },
          {
            name: 'commons.description',
            id: 'description'
          }
        ],
        checkAll2: true,
        isIndeterminate2: false,
      }
    },

    watch: {
      '$route': 'init'
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
      create() {
        this.createForm = { level: '最佳实践' };
        this.createVisible = true;

      },
      handleEdit(item) {
        this.infoForm = item;
        this.updateVisible = true;
      },
      handleList(item) {
        this.ruleListPage = 1;
        this.ruleListPageSize = 10;
        this.ruleListTotal = 0;
        this.ruleForm = [];
        this.itemId = item.id;
        this.handleListSearch();
        this.listVisible = true;
      },
      handleListSearch () {
        this.ruleCondition.combine = {group: {operator: 'in', value: this.itemId }};
        let url = ruleListUrl + this.ruleListPage + "/" + this.ruleListPageSize;
        this.groupResult = this.$post(url, this.ruleCondition, response => {
          let data = response.data;
          this.ruleListTotal = data.itemCount;
          this.ruleForm = data.listObject;
        });
      },
      handleInfo(item) {
        this.infoForm = item;
        this.infoVisible = true;
      },
      handleClose() {
        this.createVisible = false;
        this.updateVisible = false;
        this.infoVisible = false;
        this.listVisible = false;
        this.bindVisible = false;
        this.scanVisible = false;
        this.search();
      },
      handleDelete(item) {
        if (item.flag) {
          this.$warning(this.$t('rule.rule_group_flag'));
          return;
        }
        this.$alert(this.$t('account.delete_confirm') + item.name + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$get(ruleGroupDeleteUrl + item.id, () => {
                this.$success(this.$t('commons.delete_success'));
                this.search();
              });
            }
          }
        });
      },
      select(selection) {
        this.selectIds.clear()
        selection.forEach(s => {
          this.selectIds.add(s.id)
        })
      },
      getPlugins () {
        this.result = this.$get(cloudPluginUrl, response => {
          this.plugins = response.data;
        });
      },
      //查询列表
      search() {
        this.condition.type = "cloud";
        if (this.listStatus === 1) {
          let url = ruleGroupListUrl + this.currentPage + "/" + this.pageSize;
          this.result = this.$post(url, this.condition, response => {
            let data = response.data;
            this.total = data.itemCount;
            this.tableData = data.listObject;
          });
        } else {
          let url = ruleGroupListUrl + this.fcurrentPage + "/" + this.fpageSize;
          this.result = this.$post(url, this.condition, response => {
            let data = response.data;
            this.ftotal = data.itemCount;
            this.ftableData = data.listObject;
          });
        }
      },
      init() {
        this.search();
        this.getPlugins();
      },
      sort(column) {
        _sort(column, this.condition);
        this.init();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.init();
      },
      save(item, type) {
        this.$refs[type].validate(valid => {
            if (valid) {
              let params = item;
              params.flag = item.flag ? item.flag : false;
              params.type = "cloud";
              let url = type == "createForm" ? ruleGroupSaveUrl : ruleGroupUpdateUrl;
              this.groupResult = this.$post(url, params, response => {
                this.search();
                this.createVisible =  false;
                this.updateVisible =  false;
                this.$success(this.$t('commons.opt_success'));
              });
            }
        });
      },
      more() {
        this.listStatus = 1;
        this.search();
      },
      menu() {
        this.listStatus = 2;
        this.search();
      },
      handleCommand(command, data) {
        switch (command) {
          case "handleScan":
            this.handleScan(data);
            break;
          case "handleInfo":
            this.handleInfo(data);
            break;
          case "handleEdit":
            this.handleEdit(data);
            break;
          case "handleBind":
            this.handleBind(data);
            break;
          case "handleList":
            this.handleList(data);
            break;
          case "handleDelete":
            this.handleDelete(data);
            break;
          default:
            break;
        }
      },
      handleBind(item) {
        this.groupId = item.id;
        this.$get(ruleUnBindListUrl + item.id,response => {
          this.cloudData = [];
          for(let data of response.data) {
            this.cloudData.push({
              key: data.id,
              label: data.name
            });
          }
          this.bindVisible = true;
        });
        this.groupResult = this.$get(ruleAllBindListUrl + item.id,response => {
          this.cloudValue = [];
          for(let data of response.data) {
            this.cloudValue.push(data.id);
          }
        });
      },
      bind() {
        let params = {
          cloudValue: this.cloudValue,
          groupId: this.groupId,
        };
        this.groupResult = this.$post(bindRuleUrl, params,response => {
          this.$success(this.$t('organization.integration.successful_operation'));
          this.bindVisible = false;
          this.search();
        });
      },
      filterMethod(query, item) {
        return item.label.indexOf(query) > -1;
      },
      handleScan(item) {
        let url = cloudListByGroupUrl + item.pluginId;
        this.groupResult = this.$get(url, response => {
          if (response.data != undefined && response.data != null) {
            this.accounts = response.data;
            this.groupId = item.id;
            this.scanVisible = true;
          }
        });
      },
      saveScan() {
        if (!this.scanForm.id){
          this.$warning(this.$t('account.please_choose_account'));
          return;
        }
        let url = scanByGroupUrl + this.groupId + "/" + this.scanForm.id;
        this.groupResult = this.$get(url, response => {
          this.scanVisible = false;
          this.$success(this.$t('account.i18n_hr_create_success'));
          this.$router.push({
            path: '/account/result',
          }).catch(error => error);
        });
      },
      deleteBatch() {
        if (this.selectIds.size === 0) {
          this.$warning(this.$t('commons.please_select') + this.$t('rule.rule_set'));
          return;
        }
        this.$alert(this.$t('oss.delete_batch') + this.$t('rule.rule_set') + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$request({
                method: 'POST',
                url: deleteGroupsUrl,
                data: Array.from(this.selectIds),
                headers: {
                  'Content-Type': undefined
                }
              }, res => {
                this.$success(this.$t('commons.success'));
                this.search();
              });
            }
          }
        });
      },
    },
    created() {
      this.init();
    }

  }
</script>

<style scoped>
  .table-content {
    width: 100%;
  }
  .el-row-body {
    line-height: 1.15;
  }
  .time {
    font-size: 13px;
    color: #999;
  }
  .time2 {
    width: 70%;
    overflow:hidden;
    text-overflow:ellipsis;
    white-space:nowrap;
    float: left;
  }
  .round {
    font-size: 13px;
    margin: 0 0 0 5px;
    padding: 1px 3px 1px 3px;
    float: right;
  }
  .bottom {
    margin-top: 13px;
    line-height: 13px;
  }
  .button {
    padding: 0;
    float: right;
    white-space:nowrap;
    text-overflow:ellipsis;
    -o-text-overflow:ellipsis;
    overflow:hidden;
  }
  .da-na {
    width: 100%;
    white-space:nowrap;
    text-overflow:ellipsis;
    overflow:hidden;
    float: left;
  }
  .pa-na {
    max-width: 60%;
    white-space:nowrap;
    text-overflow:ellipsis;
    -o-text-overflow:ellipsis;
    overflow:hidden;
  }
  .pa-time {
    color: #1e6427;
  }
  .pa-time2 {
    color: red;
  }
  .button-drop {
    float: right;
    width: 28%;
    overflow:hidden;
    text-overflow:ellipsis;
    white-space:nowrap;
  }
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
  }
  .el-icon-arrow-down {
    font-size: 12px;
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
    padding: 10px 10%;
    width: 47%;
  }
  .rtl >>> .el-drawer__body {
    overflow-y: auto;
    padding: 0 20px;
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
  .rtl >>> #el-drawer__title {
    margin: 0;
  }
  .el-col-su >>> .el-card {
    margin: 10px 0;
  }
  .vue-select-image >>> .vue-select-image__img {
    width: 120px;
    height: 100px;
  }
  .el-row-body >>> .el-card__body >>> .el-divider {
    margin: 5px 0;
  }
  .plugin {
    color: #215d9a;
    font-size: 16px;
  }
  .plugin-name {
    float: left;
    width: 75%;
    overflow:hidden;
    text-overflow:ellipsis;
    white-space:nowrap;
  }
  .plugin-type {
    float: right;
    width: 25%;
    overflow:hidden;
    text-overflow:ellipsis;
    white-space:nowrap;
  }
  .desc {
    color: #888888;
    font-size: 13px;
    margin-top: 10px;
    line-height: 20px;
    overflow: hidden;
    text-overflow: ellipsis;
    display:-webkit-box;
    -webkit-box-orient:vertical;
    -webkit-line-clamp:6;
  }
  .edit_dev >>> .el-transfer-panel {
    width: 40%;
    text-align: left;
  }
  .edit-dev-drawer >>> .el-drawer__header {
    margin-bottom: 0;
  }
  .edit-dev-drawer >>> .el-transfer-panel__body{
    height: 500px;
  }
  .edit-dev-drawer >>> .el-transfer-panel__list{
    height: 500px;
    padding-bottom: 50px;
  }
  .el-trans {
    width: 100%;
    text-align: center;
    display: inline-block;
  }
  /deep/ :focus{outline:0;}
</style>
