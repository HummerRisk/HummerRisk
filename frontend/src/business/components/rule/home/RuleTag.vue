<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <table-header :condition.sync="condition" @search="search"
                        :title="$t('rule.rule_tag_list')"
                        @create="create"
                        :createTip="$t('rule.create_tag')"
                        :show-create="true"/>

        </template>

        <el-table :border="true" :stripe="true" :data="tableData" class="adjust-table table-content" @sort-change="sort"
                  @filter-change="filter" @select-all="select" @select="select">
          <el-table-column min-width="1%"></el-table-column>
          <el-table-column prop="tagKey" :label="$t('rule.tag_key')" min-width="20%" show-overflow-tooltip></el-table-column>
          <el-table-column prop="tagName" :label="$t('rule.tag_name')" min-width="20%" show-overflow-tooltip></el-table-column>
          <el-table-column :label="$t('rule.tag_flag')" min-width="20%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <el-tag size="mini" type="danger" v-if="scope.row.flag === true">
                {{ $t('rule.tag_flag_true') }}
              </el-tag>
              <el-tag size="mini" type="success" v-else-if="scope.row.flag === false">
                {{ $t('rule.tag_flag_false') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="index" :label="$t('rule._index')" min-width="20%" show-overflow-tooltip></el-table-column>
          <el-table-column min-width="20%" :label="$t('commons.operating')">
            <template v-slot:default="scope">
              <table-operators v-if="!!scope.row.flag" :buttons="buttonsN" :row="scope.row"/>
              <table-operators v-if="!scope.row.flag" :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
      </el-card>

      <!--Create ruleTag-->
      <el-drawer class="rtl" :title="$t('rule.create_tag')" :visible.sync="createVisible" size="45%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-form :model="createForm" label-position="right" label-width="120px" size="small" :rules="rule" ref="createForm">
          <el-form-item :label="$t('rule.tag_key')" prop="tagKey">
            <el-input v-model="createForm.tagKey" autocomplete="off" :placeholder="$t('commons.please_input')"/>
          </el-form-item>
          <el-form-item :label="$t('rule.tag_name')" prop="tagName">
            <el-input v-model="createForm.tagName" autocomplete="off" :placeholder="$t('commons.please_input')"/>
          </el-form-item>
          <el-form-item :label="$t('rule._index')" prop="index">
            <el-input type="number" v-model="createForm.index" autocomplete="off" :placeholder="$t('commons.please_input')"/>
          </el-form-item>
        </el-form>
        <dialog-footer
          @cancel="createVisible = false"
          @confirm="save(createForm, 'createForm')"/>
      </el-drawer>
      <!--Create ruleTag-->

      <!--Update ruleTag-->
      <el-drawer class="rtl" :title="$t('rule.update_tag')" :visible.sync="updateVisible" size="45%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-form :model="updateForm" label-position="right" label-width="120px" size="small" :rules="rule" ref="updateForm">
            <el-form-item :label="$t('rule.tag_key')" prop="tagKey">
              <el-input v-model="updateForm.tagKey" :disabled="true" autocomplete="off" :placeholder="$t('commons.please_input')"/>
            </el-form-item>
            <el-form-item :label="$t('rule.tag_name')" prop="tagName">
              <el-input v-model="updateForm.tagName" :disabled="updateForm.flag" autocomplete="off" :placeholder="$t('commons.please_input')"/>
            </el-form-item>
            <el-form-item :label="$t('rule._index')" prop="index">
              <el-input type="number" v-model="updateForm.index" :disabled="updateForm.flag" autocomplete="off" :placeholder="$t('commons.please_input')"/>
            </el-form-item>
          </el-form>
        <dialog-footer
          @cancel="updateVisible = false"
          @confirm="save(updateForm, 'updateForm')"/>
      </el-drawer>
      <!--Update ruleTag-->

      <!--Info ruleTag-->
      <el-drawer class="rtl" :title="$t('rule.update_tag')" :visible.sync="infoVisible" size="45%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-form :model="updateForm" label-position="right" label-width="120px" size="small" :rules="rule" ref="infoForm">
          <el-form-item :label="$t('rule.tag_key')" prop="tagKey">
            {{ updateForm.tagKey }}
          </el-form-item>
          <el-form-item :label="$t('rule.tag_name')" prop="tagName">
            {{ updateForm.tagName }}
          </el-form-item>
          <el-form-item :label="$t('rule._index')" prop="index">
            {{ updateForm.index }}
          </el-form-item>
        </el-form>
      </el-drawer>
      <!--Info ruleTag-->

      <!--rule list-->
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
      <!--rule list-->

    </main-container>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import TableHeader from "../../common/components/TableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../../common/components/DialogFooter";
import {_filter, _sort} from "@/common/js/utils";
import RuleType from "./RuleType";
import {RULE_CONFIGS, RULE_TAG_CONFIGS} from "../../common/components/search/search-components";
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
          components: RULE_TAG_CONFIGS
        },
        tableData: [],
        createForm: {},
        updateForm: {},
        ruleForm: [],
        createVisible: false,
        updateVisible: false,
        infoVisible: false,
        listVisible: false,
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        selectIds: new Set(),
        direction: 'rtl',
        rule: {
          tagKey: [
            {required: true, pattern: '[a-zA-Z]', message: this.$t('rule.tag_key_pattern'), trigger: 'blur'},
            {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
            {
              required: true,
              message: this.$t('rule.special_characters_are_not_supported'),
              trigger: 'blur'
            }
          ],
          tagName: [
            {required: true, message: this.$t('rule.tag_name') + this.$t('commons.cannot_be_empty'), trigger: 'blur'},
            {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
            {
              required: true,
              message: this.$t('rule.special_characters_are_not_supported'),
              trigger: 'blur'
            }
          ],
          index: [
            {
              required: true,
              message: this.$t('rule.number_format_is_incorrect'),
              trigger: 'blur'
            }
          ],
        },
        buttons: [
          {
            tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
            exec: this.handleEdit
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
        buttonsN: [
          {
            tip: this.$t('commons.detail'), icon: "el-icon-edit-outline", type: "primary",
            exec: this.handleInfo
          },
          {
            tip: this.$t('rule.rule_list'), icon: "el-icon-tickets", type: "success",
            exec: this.handleList
          },
        ],
        ruleListPage: 1,
        ruleListPageSize: 10,
        ruleListTotal: 0,
      }
    },

    watch: {
      '$route': 'init'
    },

    methods: {
      create() {
        this.createForm = {};
        this.createVisible = true;
      },
      handleList(item) {
        this.ruleListPage = 1;
        this.ruleListPageSize = 10;
        this.ruleListTotal = 0;
        this.ruleForm = [];
        this.handleListSearch(item);
        this.listVisible = true;
      },
      handleListSearch (item) {
        let condition = {
          components: RULE_CONFIGS
        };
        condition.combine = {ruleTag: {operator: 'in', value: item.tagKey }};
        let url = "/rule/list/" + this.ruleListPage + "/" + this.ruleListPageSize;
        this.result = this.$post(url, condition, response => {
          let data = response.data;
          this.ruleListTotal = data.itemCount;
          this.ruleForm = data.listObject;
        });
      },
      handleInfo(item) {
        this.updateForm = {};
        this.updateForm = item;
        this.infoVisible = true;
      },
      handleEdit(item) {
        this.updateForm = {};
        this.updateForm = item;
        this.updateVisible = true;
      },
      handleClose() {
        this.createVisible =  false;
        this.updateVisible =  false;
        this.infoVisible =  false;
        this.listVisible =  false;
        this.search();
      },
      handleDelete(item) {
        if (item.flag) {
          this.$warning(this.$t('rule.rule_tag_flag'));
          return;
        }
        this.$alert(this.$t('account.delete_confirm') + item.tagName + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$get("/tag/rule/delete/" + item.tagKey, () => {
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
        let url = "/rule/ruleTag/list/" + this.currentPage + "/" + this.pageSize;
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
      save(item, type) {
        this.$refs[type].validate(valid => {
            if (valid) {
              let params = item;
              params.flag = item.flag ? item.flag : false;
              let url = type == "createForm" ? "/tag/rule/save" : "/tag/rule/update";
              this.result = this.$post(url, params, response => {
                this.search();
                this.createVisible =  false;
                this.updateVisible =  false;
                this.$success(this.$t('commons.opt_success'));
              });
            }
        });
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
