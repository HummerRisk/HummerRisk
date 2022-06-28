<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <table-header :condition.sync="condition" @search="search"
                           :title="$t('account.quartz_task_list')"
                           @create="create" :createTip="$t('commons.create')"
                           :show-create="true"/>
        </template>

        <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName">
          <el-table-column type="index" min-width="3%"/>
          <el-table-column prop="name" :label="$t('account.task_input_name')" min-width="10%" show-overflow-tooltip></el-table-column>
          <el-table-column prop="qzType" :label="$t('account.choose_qztype')" min-width="9%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <el-link type="primary" @click="showAccount(scope.row)">{{ scope.row.qzType }}</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="cron" :label="$t('account.cron_expression')" min-width="8%" show-overflow-tooltip></el-table-column>
          <el-table-column prop="cronDesc" :label="$t('account.cron_expression_desc')" min-width="12%" show-overflow-tooltip></el-table-column>
          <el-table-column prop="prevFireTime" :label="$t('account.prev_fire_time')" min-width="12%" sortable>
            <template v-slot:default="scope">
              <span v-if="scope.row.prevFireTime"><i class="el-icon-time"></i> {{ scope.row.prevFireTime | timestampFormatDate }}</span>
              <span v-if="!scope.row.prevFireTime"><i class="el-icon-time"></i> {{ '--' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="lastFireTime" :label="$t('account.last_fire_time')" min-width="13%" sortable>
            <template v-slot:default="scope">
              <span><i class="el-icon-time"></i> {{ scope.row.lastFireTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" min-width="10%" :label="$t('account.task_status')"
                           column-key="status"
                           :filters="statusFilters"
                           :filter-method="filterStatus">
            <template v-slot:default="{row}">
              <el-button size="small" @click="showTaskLog(row)" type="success" v-if="row.status === 'RUNNING'">
                <i class="el-icon-timer"></i> {{ $t('account.RUNNING') }}
              </el-button>
              <el-button size="small" @click="showTaskLog(row)" type="warning" v-else-if="row.status === 'PAUSE'">
                <i class="el-icon-video-pause"></i> {{ $t('account.PAUSE') }}
              </el-button>
              <el-button size="small" @click="showTaskLog(row)" type="danger" v-else-if="row.status === 'ERROR'">
                <i class="el-icon-error"></i> {{ $t('account.ERROR') }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column min-width="12%" :label="$t('account.create_time')" sortable prop="createTime">
            <template v-slot:default="scope">
              <span><i class="el-icon-time"></i> {{ scope.row.createTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column min-width="11%" :label="$t('commons.operating')" fixed="right">
            <template v-slot:default="scope">
              <table-operators v-if="scope.row.status === 'PAUSE'" :buttons="buttons1" :row="scope.row"/>
              <table-operators v-if="scope.row.status != 'PAUSE'" :buttons="buttons2" :row="scope.row"/>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
      </el-card>

      <!--Create Quartz Task-->
      <el-drawer class="rtl" :title="$t('account.create_quartz_task')" :visible.sync="createVisible" size="50%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
          <el-form :model="form" label-position="right" label-width="150px" size="medium" :rules="rule" :ref="'createForm'">
            <el-steps :active="active"  finish-status="success">
              <el-step :title="$t('account.step1')">
              </el-step>
              <el-step :title="$t('account.step2')">
              </el-step>
              <el-step :title="$t('account.step3')">
              </el-step>
            </el-steps>
            <div v-if="active == 1">
              <span>
                  <h1>{{ $t('account.cron_expression') }}</h1>
              </span>
              <div class="app">
                <el-row>
                  <el-col :span="24">
                    <cron-input v-model="form.cron" @change="change" @reset="reset"/>
                  </el-col>
                </el-row>
              </div>
            </div>
            <div v-if="active == 2">
              <span>
                  <h1>{{ $t('account.choose_qztype') }}</h1>
              </span>
              <el-input :placeholder="$t('account.please_input_name')" v-model="form.name" clearable>
              </el-input>
              <el-select style="width: 100%;margin: 50px 0;" v-model="form.qzType" :placeholder="$t('account.please_choose_qztype')">
                <el-option
                  v-for="item in qzTypes"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                  &nbsp;&nbsp; {{ $t(item.name) }}
                </el-option>
              </el-select>
            </div>
            <div v-if="active == 3">
              <span>
                <h1>{{ $t('account.step3') }}</h1>
              </span>
              <div v-if="form.qzType == 'ACCOUNT'">
                <el-select v-model="form.accountIds" multiple filterable :collapse-tags="false" :placeholder="$t('account.please_choose_account')" :clearable="true" style="width: 100%;margin: 50px 0;">
                  <el-checkbox v-model="checkAll" @change="selectOnChangeAll(checkAll, form.qzType)">{{ $t('account.i18n_sync_all') }}</el-checkbox>
                  <el-option
                    v-for="item in accounts"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                    <img :src="require(`@/assets/img/platform/${item.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                    &nbsp;&nbsp; {{ $t(item.name) }}
                  </el-option>
                </el-select>
              </div>
              <div v-if="form.qzType == 'RULE'">
                <el-select v-model="form.accountId" filterable :collapse-tags="false" :placeholder="$t('account.please_choose_account')"
                           @change="changeAccount(form.accountId)" :clearable="true" style="width: 100%;margin: 50px 0;">
                  <el-option
                    v-for="item in accounts"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                    <img :src="require(`@/assets/img/platform/${item.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                    &nbsp;&nbsp; {{ $t(item.name) }}
                  </el-option>
                </el-select>
                <el-select v-if="form.accountId" v-model="form.ruleIds" multiple filterable :collapse-tags="false" :placeholder="$t('rule.please_choose_rule')" :clearable="true" style="width: 100%;margin: 20px 0;">
                  <el-checkbox v-model="checkAll" @change="selectOnChangeAll(checkAll, form.qzType)">{{ $t('account.i18n_sync_all') }}</el-checkbox>
                  <el-option
                    v-for="item in rules"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                    <img :src="require(`@/assets/img/platform/${item.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                    &nbsp;&nbsp; {{ $t(item.name) }}
                  </el-option>
                </el-select>
              </div>
            </div>
          </el-form>
        <dialog-footer
          @cancel="createVisible = false"
          :show-pre="preVisible" @preStep="pre()"
          :show-next="nextVisible" @nextStep="next()"
          :show-confirm="confirmVisible" @confirm="save()"/>
      </el-drawer>
      <!--Create Quartz Task-->

      <!--Quartz Task log-->
      <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="70%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <quartz-cloudTask-log :row="logForm"></quartz-cloudTask-log>
        <template v-slot:footer>
          <dialog-footer
            @cancel="logVisible = false"
            @confirm="logVisible = false"/>
        </template>
      </el-drawer>
      <!--Quartz Task log-->

      <!--Quartz Task detail-->
      <el-drawer v-if="detailVisible" :close-on-click-modal="false" class="rtl" :visible.sync="detailVisible" size="75%" :show-close="false" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div slot="title" class="dialog-title">
          <span>{{ $t('account.qztype_details') }}</span>
          <i class="el-icon-close el-icon-close-detail" @click="detailVisible=false"></i>
        </div>
        <el-form :model="detailForm" label-position="right"  size="small" ref="detailForm">
          <el-form-item class="el-form-item-dev">
            <el-tabs type="border-card">
              <el-tab-pane>
                <span slot="label"><i class="el-icon-reading"></i> {{ $t('account.quartz_task') }}</span>
                <el-form label-position="left" inline class="demo-table-expand" >
                  <el-form-item :label="$t('account.task_input_name')">
                    <el-tooltip class="item" effect="dark" :content="detailForm.name" placement="top-start">
                      <div v-if="detailForm.name" class="view-text">{{ detailForm.name }}</div>
                    </el-tooltip>
                  </el-form-item>
                  <el-form-item :label="$t('account.choose_qztype')">
                    <el-tooltip class="item" effect="dark" :content="detailForm.qzType" placement="top-start">
                      <div v-if="detailForm.qzType" class="view-text">{{ detailForm.qzType }}</div>
                    </el-tooltip>
                  </el-form-item>
                  <el-form-item :label="$t('account.cron_expression')">
                    <el-tooltip class="item" effect="dark" :content="detailForm.cron" placement="top-start">
                      <span v-if="detailForm.cron" class="view-text">{{ detailForm.cron }}</span>
                    </el-tooltip>
                  </el-form-item>
                  <el-form-item :label="$t('account.cron_expression_desc')">
                    <el-tooltip class="item" effect="dark" :content="detailForm.cronDesc" placement="top-start">
                      <span v-if="detailForm.cronDesc" class="view-text">{{ detailForm.cronDesc }}</span>
                    </el-tooltip>
                  </el-form-item>
                  <el-form-item :label="$t('account.prev_fire_time')">
                    <span v-if="detailForm.prevFireTime">{{ detailForm.prevFireTime | timestampFormatDate }}</span>
                    <span v-if="!detailForm.prevFireTime">{{ "--" }}</span>
                  </el-form-item>
                  <el-form-item :label="$t('account.last_fire_time')">
                    <span>{{ detailForm.lastFireTime | timestampFormatDate }}</span>
                  </el-form-item>
                  <el-form-item :label="$t('account.task_status')">
                    <el-button plain size="mini" type="success" v-if="detailForm.status === 'RUNNING'">
                      <i class="el-icon-timer"></i> {{ $t('account.RUNNING') }}
                    </el-button>
                    <el-button plain size="mini" type="warning" v-else-if="detailForm.status === 'PAUSE'">
                      <i class="el-icon-video-pause"></i> {{ $t('account.PAUSE') }}
                    </el-button>
                    <el-button plain size="mini" type="danger" v-else-if="detailForm.status === 'ERROR'">
                      <i class="el-icon-error"></i> {{ $t('account.ERROR') }}
                    </el-button>
                  </el-form-item>
                  <el-form-item :label="$t('account.creator')">
                    <span>{{ detailForm.applyUser }}</span>
                  </el-form-item>
                  <el-form-item :label="$t('account.create_time')">
                    <span>{{ detailForm.createTime | timestampFormatDate }}</span>
                  </el-form-item>
                  <el-form-item :label="$t('account.qztype_triggerId')">
                    <span>{{ detailForm.triggerId }}</span>
                  </el-form-item>
                </el-form>

                <el-divider></el-divider>

                <el-row class="el-form-item-dev" v-for="dto in detailForm.quartzTaskRelationDtos" :key="dto.id">
                  <el-table :show-header="true" :data="dto.cloudTaskList" class="adjust-table table-content" height="538">
                    <el-table-column v-slot:default="scope" :label="$t('rule.rule_name')" min-width="20%">
                        {{ scope.row.taskName }}
                    </el-table-column>
                    <el-table-column v-slot:default="scope" :label="$t('account.cloud_account')" min-width="15%">
                      <span class="grid-content-log-span">
                      <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                       &nbsp;&nbsp; {{ scope.row.accountName }}
                    </span>
                    </el-table-column>
                    <el-table-column v-slot:default="scope" :label="$t('account.creator')" min-width="7%">
                      {{ scope.row.applyUser }}
                    </el-table-column>
                    <el-table-column v-slot:default="scope" :label="$t('rule.severity')" min-width="8%" prop="severity">
                      <span v-if="scope.row.severity == 'HighRisk'" style="color: #f84846;"> {{ $t('rule.HighRisk') }}</span>
                      <span v-else-if="scope.row.severity == 'MediumRisk'" style="color: #fe9636;"> {{ $t('rule.MediumRisk') }}</span>
                      <span v-else-if="scope.row.severity == 'LowRisk'" style="color: #4dabef;"> {{ $t('rule.LowRisk') }}</span>
                      <span v-else> N/A</span>
                    </el-table-column>
                    <el-table-column v-slot:default="scope" :label="$t('resource.status')" min-width="12%" prop="status">
                      <el-button plain size="mini" type="primary" v-if="scope.row.status === 'UNCHECKED'">
                        <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                      </el-button>
                      <el-button plain size="mini" type="primary" v-else-if="scope.row.status === 'APPROVED'">
                        <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                      </el-button>
                      <el-button plain size="mini" type="primary" v-else-if="scope.row.status === 'PROCESSING'">
                        <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                      </el-button>
                      <el-button plain size="mini" type="success" v-else-if="scope.row.status === 'FINISHED'">
                        <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                      </el-button>
                      <el-button plain size="mini" type="danger" v-else-if="scope.row.status === 'ERROR'">
                        <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                      </el-button>
                      <el-button plain size="mini" type="warning" v-else-if="scope.row.status === 'WARNING'">
                        <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                      </el-button>
                    </el-table-column>
                    <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" min-width="8%">
                      <span v-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
                      <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum == 0)">
                        {{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}
                      </span>
                      <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)">
                        <el-link type="primary" class="text-click">{{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}</el-link>
                      </span>
                    </el-table-column>
                    <el-table-column v-slot:default="scope" :label="$t('resource.status_on_off')" prop="returnSum" min-width="10%">
                      <span v-if="scope.row.returnSum == 0" style="color: #46ad59;">{{ $t('resource.i18n_compliance_true') }}</span>
                      <span v-else-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)" style="color: #f84846;">{{ $t('resource.i18n_compliance_false') }}</span>
                      <span v-else-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
                    </el-table-column>
                    <el-table-column prop="createTime" min-width="20%" :label="$t('account.update_time')">
                      <template v-slot:default="scope">
                        <span><i class="el-icon-time"></i> {{ scope.row.createTime | timestampFormatDate }}</span>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-row>

              </el-tab-pane>
            </el-tabs>
          </el-form-item>
        </el-form>
      </el-drawer>
      <!--Quartz Task detail-->

    </main-container>
</template>

<script>
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperator from "../../common/components/TableOperator";
import TableOperators from "@/business/components/common/components/TableOperators";
import Container from "@/business/components/common/components/Container";
import MainContainer from "@/business/components/common/components/MainContainer";
import TablePagination from "@/business/components/common/pagination/TablePagination";
import {_filter, _sort} from "@/common/js/utils";
import RuleType from "@/business/components/rule/home/RuleType";
import DialogFooter from "@/business/components/account/head/DialogFooter";
import CronInput from 'vue-cron-generator/src/components/cron-input';
import {DEFAULT_CRON_EXPRESSION} from 'vue-cron-generator/src/constant/filed';
import QuartzTaskLog from "@/business/components/account/home/QuartzTaskLog";

/* eslint-disable */
  export default {
    components: {
      TableHeader,
      TableOperator,
      TableOperators,
      Container,
      MainContainer,
      TablePagination,
      RuleType,
      DialogFooter,
      CronInput,
      QuartzTaskLog,
    },
    provide() {
      return {
        search: this.search,
      }
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
        createVisible: false,
        updateVisible: false,
        buttons1: [
          {
            tip: this.$t('account.DETAIL'), icon: "el-icon-s-grid", type: "success",
            exec: this.handleDetail
          }, {
            tip: this.$t('account.RESUME'), icon: "el-icon-video-play", type: "primary",
            exec: this.handleResume
          }, {
            tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
            exec: this.handleDelete
          }
        ],
        buttons2: [
          {
            tip: this.$t('account.DETAIL'), icon: "el-icon-s-grid", type: "success",
            exec: this.handleDetail
          }, {
            tip: this.$t('account.PAUSE'), icon: "el-icon-video-pause", type: "primary",
            exec: this.handlePause
          }, {
            tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
            exec: this.handleDelete
          }
        ],
        statusFilters: [
          {text: this.$t('account.RUNNING'), value: 'RUNNING'},
          {text: this.$t('account.PAUSE'), value: 'PAUSE'},
          {text: this.$t('account.ERROR'), value: 'ERROR'}
        ],
        qzTypes: [
          {name: this.$t('account.qztype_account'), id: 'ACCOUNT'},
          {name: this.$t('account.qztype_rule'), id: 'RULE'},
        ],
        direction: 'rtl',
        rule: {
          name: [
            {required: true, message: this.$t('commons.input_name'), trigger: 'blur'},
            {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
            {
              required: true,
              message: this.$t("workspace.special_characters_are_not_supported"),
              trigger: 'blur'
            }
          ],
        },
        form: {cron: DEFAULT_CRON_EXPRESSION, name: '',accountId: "", qzType: 'ACCOUNT', accountIds: [], ruleIds: []},
        active: 1,
        preVisible: false,
        nextVisible: true,
        confirmVisible: false,
        accounts: [],
        rules: [],
        checkAll: false,
        logForm: {data: [], id: "", total: 0},
        logVisible: false,
        goLogPage: 1,
        pageLogSize: 10,
        logTotal: 0,
        detailVisible: false,
        detailForm: {},
      }
    },

    methods: {
      //查询列表
      search() {
        let url = "/cloud/task/quartz/list/" + this.currentPage + "/" + this.pageSize;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
        this.$post("/account/list/1/100", {}, response => {
          let data = response.data;
          this.accounts = data.listObject;
        });
      },
      init() {
        this.search();
      },
      sort(column) {
        _sort(column, this.condition);
        this.init();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.init();
      },
      tableRowClassName({row, rowIndex}) {
        if (rowIndex % 4 === 0) {
          return 'success-row';
        } else if (rowIndex % 2 === 0) {
          return 'warning-row';
        } else {
          return '';
        }
      },
      create() {
        this.active = 1;
        this.createVisible = true;
        this.preVisible = false;
        this.confirmVisible = false;
        this.nextVisible = true;
        this.form = {cron: DEFAULT_CRON_EXPRESSION, name: '', accountId: "", qzType: 'ACCOUNT', accountIds: [], ruleIds: []};
      },
      filterStatus(value, row) {
        return row.status === value;
      },
      handleClose() {
        this.createVisible =  false;
        this.logVisible=false;
        this.detailVisible=false;
      },
      save() {
        if (!this.form.cron) {
          this.$error(this.$t('account.cron_not_null'));
          return;
        }
        if (!this.form.qzType) {
          this.$error(this.$t('account.qztype_not_null'));
          return;
        }
        if (!this.form.name) {
          this.$error(this.$t('account.name_not_null'));
          return;
        }
        if (this.form.qzType==="ACCOUNT") {
          if(this.form.accountIds>0){
            this.$error(this.$t('account.accountIds_not_null'));
            return;
          }
        } else {
          if(this.form.ruleIds>0){
            this.$error(this.$t('account.ruleIds_not_null'));
            return;
          }
        }
        this.result = this.$post("/cloud/task/quartz/create", this.form, response => {
          this.createVisible =  false;
          this.search();
        });
      },
      change(cron) {
        this.form.cron = cron
      },
      reset(cron) {
        this.form.cron = DEFAULT_CRON_EXPRESSION
      },
      next() {
        this.preVisible = true;
        if (this.active === 1) {
          if (!this.form.cron) {
            this.$error(this.$t('account.cron_not_null'));
            return;
          }
          this.active = 2;
        } else if (this.active === 2) {
          if (!this.form.qzType) {
            this.$error(this.$t('account.qztype_not_null'));
            return;
          }
          if (!this.form.name) {
            this.$error(this.$t('account.name_not_null'));
            return;
          }
          this.active = 3;
          this.confirmVisible = true;
          this.nextVisible = false;
        }
      },
      pre() {
        if (this.active === 3) {
          this.active = 2;
          this.confirmVisible = false;
          this.nextVisible = true;
        } else if (this.active === 2) {
          this.active = 1;
          this.preVisible = false;
        }
      },
      selectOnChangeAll (checkAll, qzType) {
        if (!!checkAll) {
          if (qzType==='ACCOUNT') {
            if (this.form.accountIds.length != this.accounts.length) {
              for (let option of this.accounts) {
                this.form.accountIds.push(option["id"]);
              }
            }
          } else {
            if (this.form.ruleIds.length != this.rules.length) {
              for (let option of this.rules) {
                this.form.ruleIds.push(option["id"]);
              }
            }
          }
        } else {
          if (qzType==='ACCOUNT') {
            this.form.accountIds = [];
          } else {
            this.form.ruleIds = [];
          }
        }
      },
      changeAccount(accountId) {
        this.$get("/rule/listByAccountId/" + accountId, response => {
          let data = response.data;
          this.rules = data;
        });
      },
      handleDelete(obj) {
        this.$alert(this.$t('account.delete_confirm') + obj.name + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$get("/cloud/task/quartz/delete/" + obj.id, () => {
                this.$success(this.$t('commons.delete_success'));
                this.search();
              });
            }
          }
        });
      },
      handleDetail(obj){
        this.$router.push({
          path: '/resource/result',
        }).catch(error => error);
      },
      handleResume(obj){
        this.result = this.$get("/cloud/task/quartz/resume/" + obj.id, () => {
          this.$success(this.$t('account.resume_success'));
          this.search();
        });
      },
      handlePause(obj){
        this.result = this.$get("/cloud/task/quartz/pause/" + obj.id, () => {
          this.$success(this.$t('account.pause_success'));
          this.search();
        });
      },
      showTaskLog (obj) {
        let url = "/cloud/task/quartz/rela/log/";
        this.$post(url + obj.id + "/" + this.goLogPage + "/" + this.pageLogSize, {},response => {
          let data = response.data;
          this.logTotal = data.itemCount;
          this.logForm.data = data.listObject;
          this.logForm.id= obj.id;
          this.logForm.total = data.itemCount;
          this.logVisible =  true;
        });
      },
      showAccount(row) {
        let url = "/cloud/task/show/account/" + row.id;
        this.$get(url,response => {
          this.detailForm = response.data;
          this.detailVisible=true;
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

  .el-table {
    cursor: pointer;
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
    padding: 5px 1%;
    width: 47%;
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
    width: 100%;
  }
  /deep/ :focus{outline:0;}
  .el-box-card {
    margin: 10px 0;
  }
  .row {
    margin-right: -15px;
    margin-left: -15px
  }

  .el-icon-close-detail {
    float: right;
    cursor:pointer;
  }
</style>

<style lang="less" scoped>
  .app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin: 30px 0;
  }
</style>
