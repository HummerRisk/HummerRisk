<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <scan-header  :condition.sync="condition"
                        :title="$t('rule.rule_list')"
                        @save="save" :saveTip="$t('account.save_settings')"
                        @create="create" :createTip="$t('account.start_batch')"
                        @setting="setting" :settingTip="$t('account.quick_settings')"
                        @clean="clean" :cleanTip="$t('account.clean_settings')"
                        @back="back" :backTip="$t('account.back_account')"
                        :show-save="true" :show-create="true" :show-setting="true" :show-clean="true"/>

        </template>

        <el-table :border="true" :stripe="true" :data="tableData" class="adjust-table table-content" @sort-change="sort"
                   @select-all="select" @select="select">
          <el-table-column type="selection" min-width="5%"/>
          <el-table-column :label="$t('rule.rule_name')" min-width="20%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('account.regions')" min-width="15%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <el-select v-model="scope.row.regions" multiple collapse-tags filterable :placeholder="$t('account.please_choose_region')" :clearable="true" style="width: 100%;">
                <el-checkbox v-model="scope.row.checkAll" @change="selectOnChangeAll(scope.row.checkAll, scope.row)">{{ $t('account.i18n_sync_all') }}</el-checkbox>
                <el-option
                  v-for="item in regions"
                  :key="item.regionId"
                  :label="item.regionName"
                  :value="item.regionId">
                  &nbsp;&nbsp; {{ item.regionName }}
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column min-width="40%" :label="$t('rule.middleware_parameter')" show-overflow-tooltip>
            <template v-slot:default="scope">
              <div v-if="scope.row.parameter.length > 0">
                <span layout-gt-sm="row" class="hve-sp" :key="index" v-for="(p, index) in scope.row.parameter">
                  <el-input class="tp-el-inp" v-model="p.name" autocomplete="off" disabled/>
                  <el-input class="tp-el-inp" v-model="p.defaultValue" autocomplete="off"/>
                </span>
              </div>
              <div v-else-if="scope.row.parameter.length == 0" class="tp-el-inp"> N/A </div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('rule.resource_type')" min-width="10%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span v-for="(resourceType, index) in scope.row.types" :key="index">[{{ resourceType }}] </span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('account.save_param')" min-width="10%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <el-button plain size="mini" type="primary" v-if="scope.row.saveParam">
                {{ $t('account.is_save_param_yes') }}
              </el-button>
              <el-button plain size="mini" type="info" v-if="!scope.row.saveParam">
                {{ $t('account.is_save_param_not') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
      </el-card>

      <!--quick settings-->
      <el-drawer class="rtl" :title="$t('account.quick_settings')" :visible.sync="regionsVisible" size="45%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-form :model="form" label-position="right" label-width="60px" size="small" :rules="rule" ref="form">
          <el-form-item :label="$t('account.regions')">
            <el-select v-model="form.quickSettingRegions" multiple filterable :collapse-tags="false" :placeholder="$t('account.please_choose_region')" :clearable="true" style="width: 100%;">
              <el-checkbox v-model="checkAll" @change="selectOnChangeAll(checkAll, null)">{{ $t('account.i18n_sync_all') }}</el-checkbox>
              <el-option
                v-for="item in regions"
                :key="item.regionId"
                :label="item.regionName"
                :value="item.regionId">
                &nbsp;&nbsp; {{ $t(item.regionName) }}
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
          <dialog-footer
            @cancel="regionsVisible = false"
            @confirm="saveQuickSettings(form)"/>
      </el-drawer>
      <!--quick settings-->

    </main-container>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import ScanHeader from "../head/ScanHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../../common/components/DialogFooter";
import {ACCOUNT_ID, ACCOUNT_NAME} from "../../../../common/js/constants";
import {_sort} from "@/common/js/utils";

/* eslint-disable */
  export default {
    name: "AccountScan",
    components: {
      TableOperators,
      MainContainer,
      Container,
      ScanHeader,
      TablePagination,
      TableOperator,
      DialogFooter
    },
    props: ["id"],
    watch: {
      '$route': 'init'
    },
    data() {
      return {
        result: {},
        form: {},
        regions: {},
        tableData: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        selectIds: new Set(),
        regionsVisible: false,
        direction: 'rtl',
        rule: {
        },
        accountId: '',
        accountName: '',
        condition: {
          components: []
        },
        checkAll: false,
        ruleList: [],
        params: [],
      }
    },
    created() {
      this.init();
    },
    methods: {
      init() {
        this.accountId = this.$route.params.id;
        this.search();
      },
      clean() {
        if (!this.batch()) return;
        this.$alert(this.$t('account.clean_settings'), '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              let params = [];
              for (let item of this.ruleList) {
                let param = {};
                param.accountId = this.accountId;
                param.ruleId = item.id;
                params.push(param);
              }
              this.$post('/account/clean/parameter', params, response => {
                this.search();
                this.$success(this.$t('commons.delete_success'));
              });
            }
          }
        });
      },
      save() {
        if (!this.batch()) return;
        let flag = 0, count = 0;
        for (let item of this.tableData) {
          if (!item.regions) {
            count++;
            this.$warning(item.name + ': ' + this.$t('account.i18n_please_select_area_information'));
          }
        }
        if (count > 0) {
          return;
        }
        let params = [];
        for (let item of this.ruleList) {
          let param = Object.assign({}, item);
          param.accountId = this.accountId;
          param.regions = JSON.stringify(param.regions);
          param.taskName = item.name;
          if (param.parameter.length == 0) {
            param.parameter = '[]';
          } else {
            param.parameter = JSON.stringify(param.parameter);
          }
          params.push(param);
        }
        this.$post('/account/save/parameter', params, response => {
          this.search();
          this.$success(this.$t('commons.save_success'));
        });
      },
      create() {
        if (!this.batch()) return;
        let flag = 0, count = 0;
        for (let item of this.tableData) {
          if (!item.regions) {
            count++;
            this.$warning(item.name + ': ' + this.$t('account.i18n_please_select_area_information'));
          }
        }
        if (count > 0) {
          return;
        }
        for (let item of this.ruleList) {
          item.type = "manual";
          let param = Object.assign({}, item);
          param.selectTags = [{accountId: this.accountId, regions: param.regions}];
          param.accountId = this.accountId;
          param.taskName = param.name;
          param.regions = JSON.stringify(param.regions);
          if (param.parameter.length == 0) {
            param.parameter = '[]';
          } else {
            param.parameter = JSON.stringify(param.parameter);
          }
          this.$post('/task/manual/create', param, response => {
            flag++;
            if (flag == this.ruleList.length) {
              this.$success(this.$t("resource.i18n_create_manual_task_success"));
              this.$alert(this.$t('resource.i18n_comfirm_resource'), '', {
                confirmButtonText: this.$t('commons.confirm'),
                callback: (action) => {
                  if (action === 'confirm') {
                    localStorage.setItem(ACCOUNT_ID, this.accountId);
                    localStorage.setItem(ACCOUNT_NAME, this.accountName);
                    this.$router.push({
                      path: '/resource/result',
                    }).catch(error => error);
                  }
                }
              });
            }
          });
        }
      },
      batch () {
        const selectIds = this.selectIds;
        this.ruleList = this.tableData.filter( value => {
          if (selectIds.has(value.id)) {
            return value;
          }
        });
        if (this.selectIds.size == 0) {
          this.$error(this.$t('rule.please_choose_rule'));
          return false;
        }
        return true;
      },
      saveQuickSettings(form) {
        if (this.selectIds.size == 0) {
          this.$warning(this.$t('account.quick_settings_all'));
          for (let obj of this.tableData) {
            obj.regions = form.quickSettingRegions;
          }
        } else {
          this.$warning(this.$t('account.quick_settings_select'));
          for (let id of this.selectIds) {
            for (let obj of this.tableData) {
              if (id == obj.id) {
                obj.regions = form.quickSettingRegions;
              }
            }
          }
        }

        this.regionsVisible = false;
      },
      setting () {
        this.form = {};
        this.regionsVisible = true;
        this.checkAll = false;
      },
      handleClose() {
        this.checkAll = false;
        this.regionsVisible =  false;
      },
      sort(column) {
        _sort(column, this.condition);
        this.init();
      },
      select(selection) {
        this.selectIds.clear()
        selection.forEach(s => {
          this.selectIds.add(s.id)
        })
      },
      //查询列表
      async search() {
        if (!this.accountId) {
          return;
        }
        this.result = await this.$get("/account/getAccount/" + this.accountId, res => {
          this.accountName = res.data.name;
          this.regions = typeof(res.data.regions) == 'string'?JSON.parse(res.data.regions):res.data.regions;
          this.condition.pluginId = res.data.pluginId;
          this.condition.accountId = this.accountId;
          this.condition.status = true;
          let url = "/account/rule/list/" + this.currentPage + "/" + this.pageSize;
          this.$post(url, this.condition, response => {
            let data = response.data;
            this.total = data.itemCount;
            this.tableData = data.listObject;
            for (let obj of this.tableData) {
              obj.parameter = typeof(obj.parameter) == 'string'?JSON.parse(obj.parameter):obj.parameter;
              if (!!obj.regions && obj.regions.length > 0) {
                obj.regions = typeof(obj.regions) == 'string'?JSON.parse(obj.regions):obj.regions;
              } else {
                obj.regions = [];
                if (!!this.regions) {
                  for (let option of this.regions) {
                    obj.regions.push(option["regionId"]);
                  }
                }
              }
            }
          });
        });
      },
      back () {
        this.$router.push({
          path: '/account/cloudaccount',
        }).catch(error => error);
      },
      selectOnChangeAll (checkAll, item) {
        if (!!item) {
          item.regions = [];
          if (item.checkAll) {
            for (let option of this.regions) {
              item.regions.push(option["regionId"]);
            }
          }
        } else {
          this.form.quickSettingRegions = [];
          if (!!checkAll) {
            for (let option of this.regions) {
              this.form.quickSettingRegions.push(option["regionId"]);
            }
          }
        }
      },
    },
  }
</script>

<style scoped>
  .hve-sp {
    word-break:normal;
    width:auto;
    display:block;
    white-space:pre-wrap;
    word-wrap : break-word ;
    overflow: hidden ;
  }
  .tp-el-inp {
    width: 44%;
    margin: 2% 1%;
  }
  .el-checkbox {
    text-align: right;
    width: 100%;
    padding-right: 10px;
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
    width: 75%;
  }
  .el-checkbox >>> .el-checkbox__input{
    margin-left: -20px;
  }
  .el-checkbox >>> .el-checkbox__label{
    margin-right: 20px;
  }
  /deep/ :focus{outline:0;}
</style>
