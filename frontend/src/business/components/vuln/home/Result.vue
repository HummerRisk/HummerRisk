<template>
  <main-container v-loading="result.loading">

    <el-card class="table-card">
      <template v-slot:header>
        <table-header :condition.sync="condition"
                      @search="search"
                      :title="$t('vuln.vuln_result_list')"/>
      </template>

      <el-table class="adjust-table table-content"
                border
                :data="tableData"
                :row-class-name="tableRowClassName"
                @sort-change="sort"
                @filter-change="filter">
        <el-table-column type="index" min-width="2%"/>
        <el-table-column prop="name" :label="$t('vuln.name')" min-width="10%" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ $t(scope.row.accountName) }}
              </span>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('resource.i18n_task_type')" min-width="10%" show-overflow-tooltip>
          <span>
            <template v-for="tag in tagSelect">
              <span :key="tag.value" v-if="scope.row.ruleTags">
                <span :key="tag.tagKey" v-if="scope.row.ruleTags.indexOf(tag.tagKey) > -1"> {{ tag.tagName }}</span>
              </span>
            </template>
            <span v-if="!!scope.row.resourceTypes && scope.row.resourceTypes.indexOf('.')===-1"> {{ scope.row.resourceTypes }}</span>
            <span v-if="!!scope.row.resourceTypes && scope.row.resourceTypes.indexOf('.')>-1">
              <template v-for="type in resourceTypes" >
                <span :key="type.value" v-if="scope.row.resourceTypes">
                  <span :key="type.value" v-if="scope.row.resourceTypes.indexOf(type.value) > -1"> [{{ type.value }}]</span>
                </span>
              </template>
            </span>
          </span>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('rule.rule_name')" min-width="13%" show-overflow-tooltip>
          <el-link type="primary" class="md-primary text-click" @click="showTaskDetail(scope.row)">
            {{ scope.row.taskName }}
          </el-link>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('account.creator')" min-width="6%" show-overflow-tooltip>
          {{ scope.row.applyUser }}
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('rule.severity')" min-width="8%" :sort-by="['HighRisk', 'MediumRisk', 'LowRisk']" prop="severity" :sortable="true"  show-overflow-tooltip>
          <span v-if="scope.row.severity == 'HighRisk'" style="color: #f84846;"> {{ $t('rule.HighRisk') }}</span>
          <span v-else-if="scope.row.severity == 'MediumRisk'" style="color: #fe9636;"> {{ $t('rule.MediumRisk') }}</span>
          <span v-else-if="scope.row.severity == 'LowRisk'" style="color: #4dabef;"> {{ $t('rule.LowRisk') }}</span>
          <span v-else> N/A</span>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('resource.status')" min-width="12%" prop="status" sortable show-overflow-tooltip>
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
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="6%">
          <el-tooltip class="item" effect="dark" :content="$t('history.resource_result')" placement="top">
            <span v-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
            <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum == 0)">
              {{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}
            </span>
            <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)">
              <el-link type="primary" class="text-click" @click="goResource(scope.row)">{{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}</el-link>
            </span>
          </el-tooltip>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('resource.status_on_off')" prop="returnSum" sortable show-overflow-tooltip min-width="8%">
          <span v-if="scope.row.returnSum == 0" style="color: #46ad59;">{{ $t('resource.i18n_compliance_true') }}</span>
          <span v-else-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)" style="color: #f84846;">{{ $t('resource.i18n_compliance_false') }}</span>
          <span v-else-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
        </el-table-column>
        <el-table-column prop="createTime" min-width="14%" :label="$t('account.update_time')" sortable show-overflow-tooltip>
          <template v-slot:default="scope">
            <span><i class="el-icon-time"></i> {{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="15%" :label="$t('commons.operating')" fixed="right" show-overflow-tooltip>
          <template v-slot:default="scope">
            <table-operators :buttons="rule_buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>

      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Task log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="65%" :before-close="handleClose" :direction="direction"
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
    <el-drawer v-if="detailVisible" :close-on-click-modal="false" class="rtl" :visible.sync="detailVisible" size="60%" :show-close="false" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <div slot="title" class="dialog-title">
        <span>{{ $t('resource.i18n_detail') }}</span>
        <i class="el-icon-close el-icon-close-detail" @click="detailVisible=false"></i>
      </div>
      <el-form :model="detailForm" label-position="right" label-width="120px" size="small" :rules="rule" ref="detailForm">
        <el-form-item class="el-form-item-dev">
          <el-tabs type="border-card" @tab-click="showCodemirror">
            <el-tab-pane>
              <span slot="label"><i class="el-icon-reading"></i> {{ $t('rule.rule') }}</span>
              <el-form label-position="left" inline class="demo-table-expand" >
                <el-form-item :label="$t('vuln.platform')" v-if="detailForm.pluginIcon">
                        <span>
                          <img :src="require(`@/assets/img/platform/${detailForm.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
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
                          <span :key="tag.tagKey" v-if="detailForm.ruleTags.indexOf(tag.tagKey) > -1"> {{ tag.tagName }}</span>
                        </template>
                        <template v-for="type in resourceTypes" >
                          <span :key="type.value" v-if="detailForm.resourceTypes.indexOf(type.value) > -1"> [{{ type.value }}]</span>
                        </template>
                      </span>
                </el-form-item>
                <el-form-item :label="$t('rule.severity')">
                  <span v-if="detailForm.severity == 'HighRisk'" style="color: #f84846;"> {{ $t('rule.HighRisk') }}</span>
                  <span v-else-if="detailForm.severity == 'MediumRisk'" style="color: #fe9636;"> {{ $t('rule.MediumRisk') }}</span>
                  <span v-else-if="detailForm.severity == 'LowRisk'" style="color: #4dabef;"> {{ $t('rule.LowRisk') }}</span>
                  <span v-else> N/A</span>
                </el-form-item>
                <el-form-item :label="$t('resource.status')">
                  <el-button plain size="mini" type="primary" v-if="detailForm.status === 'UNCHECKED'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                  </el-button>
                  <el-button plain size="mini" type="primary" v-else-if="detailForm.status === 'APPROVED'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                  </el-button>
                  <el-button plain size="mini" type="primary" v-else-if="detailForm.status === 'PROCESSING'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
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
                注: {{detailForm.description}}
              </div>
            </el-tab-pane>
            <el-tab-pane>
              <span slot="label"><i class="el-icon-info"></i> {{ $t('rule.rule_detail') }}</span>
              <codemirror ref="cmEditor" v-model="detailForm.customData" class="code-mirror" :options="cmOptions" />
            </el-tab-pane>
          </el-tabs>
        </el-form-item>
      </el-form>
    </el-drawer>
    <!--Task detail-->

  </main-container>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import TableHeader from "../head/ResourceTableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../../common/components/DialogFooter";
import CenterChart from "../../common/components/CenterChart";
import ResultLog from "./ResultLog";

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
    CenterChart,
    ResultLog
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
      },
      direction: 'rtl',
      tagSelect: [],
      resourceTypes: [],
      timer: '',
      rule_buttons: [
        {
          tip: this.$t('resource.scan_vuln_search'), icon: "el-icon-share", type: "primary",
          exec: this.handleVuln
        },
        {
          tip: this.$t('resource.i18n_detail'), icon: "el-icon-document", type: "warning",
          exec: this.showTaskDetail
        },
        {
          tip: this.$t('resource.result_details_list'), icon: "el-icon-edit-outline", type: "success",
          exec: this.goResource
        },
        {
          tip: this.$t('resource.scan'), icon: "el-icon-zoom-in", type: "danger",
          exec: this.handleScan
        }
      ],
      platforms: [
        {text: this.$t('account.aliyun'), value: 'hummer-aliyun-plugin'},
        {text: this.$t('account.tencent'), value: 'hummer-qcloud-plugin'},
        {text: this.$t('account.huawei'), value: 'hummer-huawei-plugin'},
        {text: this.$t('account.aws'), value: 'hummer-aws-plugin'},
        {text: this.$t('account.azure'), value: 'hummer-azure-plugin'},
        {text: this.$t('account.vsphere'), value: 'hummer-vsphere-plugin'},
        {text: this.$t('account.openstack'), value: 'hummer-openstack-plugin'},
        {text: this.$t('account.huoshan'), value: 'hummer-huoshan-plugin'},
        {text: this.$t('account.baidu'), value: 'hummer-baidu-plugin'},
        {text: this.$t('account.qiniu'), value: 'hummer-qiniu-plugin'},
        {text: this.$t('account.qingcloud'), value: 'hummer-qingcloud-plugin'},
        {text: this.$t('account.ucloud'), value: 'hummer-ucloud-plugin'},
        {text: this.$t('account.k8s'), value: 'hummer-k8s-plugin'}
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
        name: [
          {required: true, message: this.$t('commons.input_name'), trigger: 'blur'},
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
          {
            required: true,
            message: this.$t("workspace.special_characters_are_not_supported"),
            trigger: 'blur'
          }
        ]
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
    }
  },
  methods: {
    handleVuln() {
      window.open('http://www.cnnvd.org.cn/web/vulnerability/queryLds.tag','_blank','');
    },
    sort(column) {
      _sort(column, this.condition);
      this.init();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.init();
    },
    async search () {
      let url = "/vuln/manual/list/" + this.currentPage + "/" + this.pageSize;
      this.result = await this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    async initSelect () {
      this.tagSelect = [];
      await this.$get("/tag/rule/list", response => {
        this.tagSelect = response.data;
      });
      this.resourceTypes = [];
      await this.$get("/rule/all/resourceTypes", response => {
        for (let item of response.data) {
          let typeItem = {};
          typeItem.value = item.name;
          typeItem.label = item.name;
          this.resourceTypes.push(typeItem);
        }
      });
    },
    goResource (params) {
      if (params.returnSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      let p = '/vuln/resultdetails/' + params.id;
      this.$router.push({
        path: p
      }).catch(error => error);
    },
    init() {
      this.initSelect();
      this.search();
    },
    getStatus () {
      if (this.checkStatus(this.tableData)) {
        this.search();
        clearInterval(this.timer);
        this.timer = setInterval(this.getStatus,30000);
      } else {
        let url = "/cloud/task/manual/list/" + this.currentPage + "/" + this.pageSize;
        this.condition.accountId = this.accountId;
        //在这里实现事件
        this.$post(url, this.condition, response => {
          for (let data of response.data.listObject) {
            for (let item of this.tableData) {
              if (data.id == item.id) {
                item.status = data.status;
                item.resourceTypes = data.resourceTypes;
                item.returnSum = data.returnSum;
                item.resourcesSum = data.resourcesSum;
              }
            }
          }
        });
      }
    },
    //是否是结束状态，返回false代表都在运行中，true代表已结束
    checkStatus (tableData) {
      let sum = 0;
      for (let row of tableData) {
        if (row.status != 'ERROR' && row.status != 'FINISHED' && row.status != 'WARNING') {
          sum++;
        }
      }
      return sum == 0;
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
    showTaskLog (cloudTask) {
      let showLogTaskId = cloudTask.id;
      let url = "";
      if (showLogTaskId) {
        url = "/cloud/task/log/taskId/";
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
      this.$get("/cloud/task/detail/" + item.id, response => {
        if (response.success) {
          this.detailForm = response.data;
          this.detailVisible = true;
        }
      });
    },
    handleClose(done) {
      this.logVisible=false;
      this.detailVisible=false;
    },
    handleScan (item) {
      this.$alert(this.$t('resource.handle_scans'), '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action)  => {
          if (action === 'confirm') {
            this.$get("/rule/reScan/" + item.id + "/" + item.accountId, response => {
              if (response.success) {
                this.search();
              }
            });
          }
        }
      });
    },
    showCodemirror () {
      setTimeout(() => {
        this.$refs.cmEditor.codemirror.refresh();
      },50);
    }
  },
  computed: {
    codemirror() {
      return this.$refs.cmEditor.codemirror;
    }
  },
  created() {
  },
  mounted() {
    this.timer = setInterval(this.getStatus,5000);
    this.init();
  },
  beforeDestroy() {
    clearInterval(this.timer);
  }
}
</script>

<style scoped>
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
  background: #d3dce6;
}
.bg-purple-light {
  background: #f2f2f2;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
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

.view-text{
  display: inline-block;
  white-space: nowrap;
  width: 100%;
  overflow: hidden;
  text-overflow:ellipsis;
}
.text-click {
  color: #0066ac;
}
.rtl >>> .el-drawer__body {
  overflow-y: auto;
}
.el-row-card {
  padding: 0 20px 0 20px;
  margin: 0 0 20px 0;
}
.el-row-card >>> .el-card__body {
  margin: 30px 0 0 30px;
}
.split {
  height: 120px;
  border-left: 1px solid #D8DBE1;
}
.icon-loading {
  font-size: 100px;
}
/deep/ :focus{outline:0;}
</style>
