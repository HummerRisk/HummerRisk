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
                v-model="condition.ruleName" clearable/>
            </el-col>
          </el-row>
        </template>

        <el-table border :data="tableData" class="adjust-table table-content" stripe @filter-change="filter" height="317">
          <el-table-column type="index" min-width="3%"/>
          <el-table-column :label="$t('task.task_rule_name')" min-width="27%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <el-link type="primary" :underline="false" class="md-primary text-click"  @click="showTaskDetail(scope.row)">
                <span>
                  <img v-if="scope.row.icon" :src="require(`@/assets/img/platform/${scope.row.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                   &nbsp;&nbsp; {{ scope.row.ruleName }}
                </span>
              </el-link>
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

      <!--Task rule detail-->
      <el-drawer v-if="detailVisible" class="rtl" :visible.sync="detailVisible" size="60%"
                 :append-to-body="true">
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
                    <severity-type :row="detailForm"></severity-type>
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
      <el-drawer class="rtl" :title="$t('resource.i18n_detail')" :visible.sync="tagDetailVisible" size="85%"
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
      <el-drawer class="rtl" :title="$t('resource.i18n_detail')" :visible.sync="groupDetailVisible" size="80%"
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

    </main-container>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import TableHeader from "../../common/components/TableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {_filter} from "@/common/js/utils";
import SeverityType from "@/business/components/common/components/SeverityType";

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
        key: 'all',
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
        let url = "/task/allList/" + this.currentPage + "/" + this.pageSize;
        if(this.key === 'all') {
          url = "/task/allList/" + this.currentPage + "/" + this.pageSize;
        } else if (this.key === 'rule') {
          url = "/task/ruleList/" + this.currentPage + "/" + this.pageSize;
        } else if (this.key === 'tag') {
          url = "/task/ruleTagList/" + this.currentPage + "/" + this.pageSize;
        } else if (this.key === 'group') {
          url = "/task/ruleGroupList/" + this.currentPage + "/" + this.pageSize;
        }
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
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
      filterRules (tag) {
        for (let obj of this.tags) {
          if (tag.label == obj.name) {
            this.key = obj.id;
            break;
          } else {
            this.key = 'all';
          }
        }
        this.search();
      },
      init() {
        this.search();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.init();
      },
      addTask(item) {
        this.$emit('addTask', item);
      },
      showTaskDetail(item) {
        if (item.ruleType === 'rule') {
          this.detailForm = {};
          this.result = this.$post("/task/detailRule",item, response => {
            if (response.success) {
              let data = response.data;
              if (item.accountType === 'cloudAccount') {
                this.detailForm = data.ruleDTO;
              } else if(item.accountType === 'serverAccount') {
                this.detailForm = data.serverRuleDTO;
              } else if(item.accountType === 'imageAccount') {
                this.detailForm = data.imageRuleDTO;
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
    },
    computed: {
      codemirror() {
        return this.$refs.cmEditor.codemirror;
      }
    },
    activated() {
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
  /deep/ :focus{outline:0;}
</style>
