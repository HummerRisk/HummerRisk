<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <group-table-header :condition.sync="condition" @search="search"
                        :title="$t('resource.resource_result_list')" @more="more" @menu="menu"
                        :show-create="false" :show-list="true" :listStatus="listStatus"
                        :items="items" :columnNames="columnNames" @delete="deleteBatch" :show-delete="true"
                        :checkedColumnNames="checkedColumnNames" :checkAll="checkAll" :isIndeterminate="isIndeterminate"
                        @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange" @handleCheckAllChange="handleCheckAllChange"/>
        </template>

        <el-row :gutter="20" class="el-row-body pdfDom" v-show="listStatus === 2">
          <el-col :xs="24" :sm="12" :md="12" :lg="8" :xl="6" v-for="(data, index) in ftableData" :key="index" class="el-col el-col-su">
            <el-card :body-style="{ padding: '15px' }">
              <div style="height: 130px;">
                <el-row :gutter="20">
                  <el-col :span="24">
                    <el-row class="plugin" v-if="checkedColumnNames.includes('pluginName')">
                      <span class="plugin-name">
                        <el-image v-if="data.pluginIcon" style="border-radius: 50%;width: 25px; height: 25px; vertical-align:middle;" :src="require(`@/assets/img/platform/${data.pluginIcon}`)">
                          <div slot="error" class="image-slot">
                            <i class="el-icon-picture-outline"></i>
                          </div>
                        </el-image>
                          {{ data.pluginName }}
                      </span>
                      <span class="plugin-type">
                         <el-button plain size="mini" type="primary" v-if="data.status === 'UNCHECKED'">
                            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                         </el-button>
                         <el-button plain size="mini" type="primary" v-else-if="data.status === 'APPROVED'">
                            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                         </el-button>
                         <el-button plain size="mini" type="primary" v-else-if="data.status === 'PROCESSING'">
                            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                         </el-button>
                         <el-button plain size="mini" type="success" v-else-if="data.status === 'FINISHED'">
                            <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                         </el-button>
                         <el-button plain size="mini" type="danger" v-else-if="data.status === 'ERROR'">
                            <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                         </el-button>
                         <el-button plain size="mini" type="warning" v-else-if="data.status === 'WARNING'">
                            <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                         </el-button>
                      </span>
                    </el-row>
                    <el-row style="margin: 10px 0;">
                      <el-carousel :autoplay="false">
                        <el-carousel-item v-for="item in data.cloudGroupList" :key="item.id">
<!--                          <h3 class="medium">{{ item }}</h3>-->
                          <el-card class="medium medium-card" :body-style="{ padding: '15px', margin: '10px' }">
                            <div slot="header" class="clearfix">
                              <span class="table-card-header">{{ item.groupName }}</span>
                            </div>
                            <div class="text item">
                              <el-row>
                                <el-tooltip class="item" effect="dark" :content="item.groupDesc" placement="top">
                                  <div class="text-container">{{ item.groupDesc }}</div>
                                </el-tooltip>
                              </el-row>
                              <el-row>
                                <el-col :span="24">
                                  <span class="desc-rule">{{ $t('dashboard.rule_detail') }}</span>
                                  <span class="not_compliance_num">{{ "0" }}</span>
                                  <span class="compliance_num">&nbsp;&nbsp;/&nbsp;{{ "10" }}</span>
                                </el-col>
                              </el-row>
                              <el-row style="margin-top: 15px;">
                                <el-col :span="6">
                                  <div style="height: 12px;background-color: #8B0000;margin: 1px;border-radius: 3px;"></div>
                                </el-col>
                                <el-col :span="6">
                                  <div style="height: 12px;background-color: #FF4D4D;margin: 1px;border-radius: 3px;"></div>
                                </el-col>
                                <el-col :span="6">
                                  <div style="height: 12px;background-color: #FF8000;margin: 1px;border-radius: 3px;"></div>
                                </el-col>
                                <el-col :span="6">
                                  <div style="height: 12px;background-color: #336D9F;margin: 1px;border-radius: 3px;"></div>
                                </el-col>
                              </el-row>
                              <el-row style="margin-top: 15px;">
                                <el-col :span="6">
                                  <span class="label"> {{ $t('commons.critical') }} :</span>
                                  <span class="value critical"> {{ "0" }}</span>
                                </el-col>
                                <el-col :span="6">
                                  <span class="label"> {{ $t('commons.high') }} :</span>
                                  <span class="value high"> {{ "1" }}</span>
                                </el-col>
                                <el-col :span="6">
                                  <span class="label"> {{ $t('commons.medium') }} :</span>
                                  <span class="value middle"> {{ "3" }}</span>
                                </el-col>
                                <el-col :span="6">
                                  <span class="label"> {{ $t('commons.low') }} :</span>
                                  <span class="value low"> {{ "22" }}</span>
                                </el-col>
                              </el-row>
                            </div>
                          </el-card>
                        </el-carousel-item>
                      </el-carousel>
                    </el-row>
                  </el-col>
                </el-row>
              </div>
              <div style="padding: 0 14px 14px 14px;margin-top: 5px;">
                <el-row>
                  <el-col :span="19">
                    <span class="da-na" v-if="checkedColumnNames.includes('name')">
                      {{ data.accountName }}
<!--                      <span v-if="data.jobType === 'once'" class="pa-time2">-->
<!--                        {{ $t('resource.result_once') }}-->
<!--                      </span>-->
                      <span v-if="data.jobType === 'cron'" class="pa-time2">
                        （{{ $t('resource.result_cron') }})
                      </span>
                    </span>
                  </el-col>
                  <el-col :span="5" style="text-align: right;" v-if="checkedColumnNames.includes('flag')">
                    <span>{{ data.creator }}</span>
                  </el-col>
                </el-row>
                <span class="button time pa-na">
              </span>
                <div class="bottom clearfix">
                  <div class="time time2">
                    <span class="pa-time">{{ data.createTime | timestampFormatDate }}&nbsp;
                    </span>
                  </div>
                  <el-dropdown class="button button-drop" @command="(command)=>{handleCommand(command, data)}">
                    <span class="el-dropdown-link">
                      {{ $t('package.operate') }}
                      <i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown" v-if="!data.flag">
                      <el-dropdown-item command="handleDetail">{{ $t('commons.detail') }}</el-dropdown-item>
                      <el-dropdown-item command="handleLog">{{ $t('commons.log') }}</el-dropdown-item>
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
                <img v-if="scope.row.pluginIcon" :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
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
          <el-table-column min-width="170" :label="$t('commons.operating')" fixed="right">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </hide-table>
        <table-pagination v-show="listStatus === 1" :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>

      </el-card>

      <!--Project log-->
      <!--Project log-->

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
import {cloudPluginUrl} from "@/api/system/system";
import {RULE_GROUP_IMG} from "@/common/js/constants";
import {projectDeleteByIdUrl, projectDeletesUrl, projectListUrl} from "@/api/cloud/project/project";

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
        viewResult: {},
        condition: {
          components: RULE_GROUP_CONFIGS
        },
        ruleCondition: {
          components: RULE_CONFIGS
        },
        plugins: [],
        tableData: [],
        ftableData: [],
        infoForm: {},
        ruleForm: [],
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
        buttons: [
          {
            tip: this.$t('commons.detail'), icon: "el-icon-edit-outline", type: "primary",
            exec: this.handleDetail
          },
          {
            tip: this.$t('commons.log'), icon: "el-icon-tickets", type: "success",
            exec: this.handleLog
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
        groupTypes: [
          {id: 'cloud', name: 'Cloud'},
          {id: 'k8s', name: 'K8s'},
          {id: 'server', name: 'Server'},
        ],
        checkPlugins: [],
        serverTypes: [
          {id: 'linux', name: 'Linux'},
          {id: 'windows', name: 'Windows'}
        ],
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
      handleList(item) {
        this.ruleListPage = 1;
        this.ruleListPageSize = 10;
        this.ruleListTotal = 0;
        this.ruleForm = [];
        this.itemId = item.id;
        this.infoForm = item;
        this.handleListSearch(item);
        this.listVisible = true;
      },
      handleInfo(item) {
        this.infoForm = item;
        this.infoVisible = true;
      },
      handleClose() {
        this.updateVisible = false;
        this.infoVisible = false;
        this.listVisible = false;
        this.bindVisible = false;
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
              this.result = this.$get(projectDeleteByIdUrl + item.id, () => {
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
        this.viewResult = this.$get(cloudPluginUrl, response => {
          this.plugins = response.data;
        });
      },
      //查询列表
      search() {
        if (this.listStatus === 1) {
          let url = projectListUrl + this.currentPage + "/" + this.pageSize;
          this.result = this.$post(url, this.condition, response => {
            let data = response.data;
            this.total = data.itemCount;
            this.tableData = data.listObject;
          });
        } else {
          let url = projectListUrl + this.fcurrentPage + "/" + this.fpageSize;
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
          case "handleDetail":
            this.handleDetail(data);
            break;
          case "handleLog":
            this.handleLog(data);
            break;
          case "handleDelete":
            this.handleDelete(data);
            break;
          default:
            break;
        }
      },
      handleLog(item) {

      },
      handleDetail(item) {
        this.$router.push({
          path: '/resource/resultdetails/' + item.id,
        }).catch(error => error);
      },
      filterMethod(query, item) {
        return item.label.indexOf(query) > -1;
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
                url: projectDeletesUrl,
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
      changePlugin (pluginId){
        let plugins = RULE_GROUP_IMG;
        this.checkPlugins = [];
        for (let p of plugins) {
          if (p.id === pluginId) {
            this.checkPlugins.push(p);
          }
        }
      },
      changeServerPlugin (pluginId, serverType){
        let plugins = RULE_GROUP_IMG;
        this.checkPlugins = [];
        for (let p of plugins) {
          if (p.id === pluginId && (p.value.indexOf(serverType) > -1)) {
            this.checkPlugins.push(p);
          }
        }
      },
      changeImage (form) {
        let plugins = RULE_GROUP_IMG;
        for (let p of plugins) {
          if (p.value === form.imageUrl) {
            form.level = p.name;
            break;
          }
        }
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
    width: 70%;
    overflow:hidden;
    text-overflow:ellipsis;
    white-space:nowrap;
  }
  .plugin-type {
    float: right;
    width: 30%;
    overflow:hidden;
    text-overflow:ellipsis;
    white-space:nowrap;
    text-align: right;
  }
  .desc {
    color: #888888;
    font-size: 13px;
    margin: 10px 0;
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
  .el-carousel__item h3 {
    font-size: 14px;
    opacity: 0.75;
    line-height: 200px;
    margin: 0;
  }
  .el-carousel__item:nth-child(2n) {
    //background-color: #99a9bf;
  }
  .el-carousel__item:nth-child(2n+1) {
    //background-color: #d3dce6;
  }
  .medium-card {
    margin: 0 !important;
    height: 90%;
  }
  .el-carousel >>> .el-carousel__indicator--horizontal .el-carousel__button {
    background-color: #99a9bf;
  }
  .el-carousel >>> .el-carousel__indicators--horizontal {
    max-width: 200px;/* 设置按钮容器的最大宽度 */
    max-height: 26px;
    overflow: hidden;/* 控制溢出部分隐藏 */
  }
  .el-carousel >>> .is-active .el-carousel__button {
    background: #31e5f5;
  }
  .desc-rule {
    color: #646a73;
    height: 22px;
    font-weight: 400;
    font-size: 14px;
    line-height: 22px;
  }

  .not_compliance_num {
    margin-left: 12px;
    color: #1f2329;
    font-size: 20px;
    line-height: 28px;
    font-weight: 500;
  }

  .compliance_num {
    color: #646a73;
  }
  .label {
    height: 22px;
    font-size: 14px;
    color: #646a73;
    font-weight: 400;
    display: inline-block;
  }

  .value {
    font-weight: 400;
    font-size: 14px;
    line-height: 22px;
    height: 22px;
  }

  .critical {
    color: #8B0000;
  }

  .high {
    color: #FF4D4D;
  }

  .middle {
    color: #FF8000;
  }

  .low {
    color: #336D9F;
  }
  .text-container {
    display: -webkit-box; /* 设置为弹性盒子布局 */
    -webkit-box-orient: vertical; /* 设置盒子内元素垂直排列 */
    overflow: hidden; /* 控制溢出部分隐藏 */
    text-overflow: ellipsis; /* 超出部分显示省略号 */
    -webkit-line-clamp: 3; /* 设置最大显示行数为3行 */
    margin: 5px 0;
    color: #193349;
  }
  .button-drop {
    float: right;
    width: 28%;
    overflow:hidden;
    text-overflow:ellipsis;
    white-space:nowrap;
    text-align: right;
  }
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
  }
  .el-icon-arrow-down {
    font-size: 12px;
  }
  /deep/ :focus{outline:0;}
</style>
