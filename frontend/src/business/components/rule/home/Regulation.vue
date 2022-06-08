<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <table-header :condition.sync="condition"
                        @search="search"
                        :title="$t('resource.regulation_list')"/>
        </template>

        <el-table :border="true" :stripe="true" :data="tableData" class="adjust-table table-content" @sort-change="sort"
                  @filter-change="filter" @select-all="select" @select="select">
          <el-table-column type="index" min-width="5%"/>
          <el-table-column prop="itemSortFirstLevel" :label="$t('resource.security_level')" min-width="13%" show-overflow-tooltip></el-table-column>
          <el-table-column prop="itemSortSecondLevel" :label="$t('resource.control_point')" min-width="12%" show-overflow-tooltip></el-table-column>
          <el-table-column prop="project" :label="$t('resource.basic_requirements_for_grade_protection')" min-width="50%" show-overflow-tooltip></el-table-column>
          <el-table-column v-slot:default="scope" :label="$t('resource.suggestions_for_improvement')" min-width="10%">
            <el-tooltip class="item" effect="dark" :content="scope.row.improvement" placement="top">
              <span style="color: #0066ac;">
                {{ $t('resource.suggestions_for_improvement') }} <i class="el-icon-question"></i>
              </span>
            </el-tooltip>
          </el-table-column>
          <el-table-column min-width="10%" :label="$t('commons.operating')">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
      </el-card>

      <!--Info regulation-->
      <el-drawer class="rtl" :title="$t('resource.regulation')" :visible.sync="infoVisible" size="45%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-form :model="infoForm" label-position="right" label-width="120px" size="small" :rules="rule" ref="infoForm">
          <el-form-item :label="$t('resource.security_level')">
            {{ infoForm.itemSortFirstLevel }}
          </el-form-item>
          <el-form-item :label="$t('resource.control_point')">
            {{ infoForm.itemSortSecondLevel }}
          </el-form-item>
          <el-form-item :label="$t('resource.basic_requirements_for_grade_protection')">
            {{ infoForm.project }}
          </el-form-item>
          <el-form-item :label="$t('resource.suggestions_for_improvement')">
            {{ infoForm.improvement }}
          </el-form-item>
        </el-form>
      </el-drawer>
      <!--Info regulation-->

      <!--regulation list-->
      <el-drawer class="rtl" :title="$t('rule.rule_list')" :visible.sync="listVisible" size="80%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-table border :data="ruleForm" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                  @filter-change="filter" @select-all="select" @select="select">
          <el-table-column type="index" min-width="4%"/>
          <el-table-column prop="name" :label="$t('rule.rule_name')" min-width="18%" show-overflow-tooltip></el-table-column>
          <el-table-column :label="$t('rule.resource_type')" min-width="10%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span v-for="(resourceType, index) in scope.row.types" :key="index">[{{ resourceType }}] </span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('account.cloud_platform')" min-width="11%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.pluginName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column min-width="7%" :label="$t('rule.severity')" column-key="severity">
            <template v-slot:default="{row}">
              <rule-type :row="row"/>
            </template>
          </el-table-column>
          <el-table-column prop="description" :label="$t('rule.description')" min-width="28%" show-overflow-tooltip></el-table-column>
          <el-table-column :label="$t('rule.status')" min-width="7%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <el-switch @change="changeStatus(scope.row)" v-model="scope.row.status"/>
            </template>
          </el-table-column>
          <el-table-column prop="lastModified" min-width="15%" :label="$t('rule.last_modified')" sortable>
            <template v-slot:default="scope">
              <span><i class="el-icon-time"></i> {{ scope.row.lastModified | timestampFormatDate }}</span>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="handleListSearch" :current-page.sync="ruleListPage" :page-size.sync="ruleListPageSize" :total="ruleListTotal"/>
      </el-drawer>
      <!--regulation list-->

    </main-container>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import TableHeader from "@/business/components/resource/head/ResourceTableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../../common/components/DialogFooter";
import {_filter, _sort} from "@/common/js/utils";
import RuleType from "./RuleType";
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
      RuleType
    },
    data() {
      return {
        result: {},
        condition: {
        },
        tableData: [],
        ruleForm: [],
        infoForm: {},
        infoVisible: false,
        listVisible: false,
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        selectIds: new Set(),
        direction: 'rtl',
        rule: {
        },
        buttons: [
          {
            tip: this.$t('rule.info'), icon: "el-icon-edit-outline", type: "primary",
            exec: this.handleInfo
          },
          {
            tip: this.$t('rule.rule_list'), icon: "el-icon-tickets", type: "success",
            exec: this.handleList
          }
        ],
        ruleListPage: 1,
        ruleListPageSize: 10,
        ruleListTotal: 0,
        itemId: "",
      }
    },

    watch: {
      '$route': 'init'
    },

    methods: {
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
        let condition = {};
        condition.combine = {regulation: {operator: 'in', value: this.itemId }};
        let url = "/rule/list/" + this.ruleListPage + "/" + this.ruleListPageSize;
        this.result = this.$post(url, condition, response => {
          let data = response.data;
          this.ruleListTotal = data.itemCount;
          this.ruleForm = data.listObject;
        });
      },
      handleInfo(item) {
        this.infoForm = {};
        this.infoForm = item;
        this.infoVisible = true;
      },
      handleClose() {
        this.infoVisible =  false;
        this.listVisible =  false;
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
              this.result = this.$get("/rule/group/delete/" + item.id, () => {
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
      //查询列表
      search() {
        let url = "/rule/ruleInspectionReports/" + this.currentPage + "/" + this.pageSize;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
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
  /deep/ :focus{outline:0;}
</style>
