<template>
  <div v-loading="result.loading">

    <el-card class="table-card">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search" @create="create"
                      :create-tip="$t('proxy.create')" :title="$t('commons.proxy')" :show-create="true" :show-delete-name="false"
                      :items="items" :columnNames="columnNames" :show-open="false" @delete="deleteBatch" :show-delete="true"
                      :checkedColumnNames="checkedColumnNames" :checkAll="checkAll" :isIndeterminate="isIndeterminate"
                      @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange" @handleCheckAllChange="handleCheckAllChange"/>
      </template>

      <hide-table
        :table-data="tableData"
        @sort-change="sort"
        @filter-change="filter"
        @select-all="select"
        @select="select"
      >
        <el-table-column type="selection" id="selection" prop="selection" min-width="50">
        </el-table-column>
        <el-table-column type="index" min-width="40"/>
        <el-table-column prop="proxyType" v-if="checkedColumnNames.includes('proxyType')" :label="$t('commons.proxy_type')" min-width="100"/>
        <el-table-column prop="proxyIp" v-if="checkedColumnNames.includes('proxyIp')" label="Proxy IP" min-width="100"/>
        <el-table-column prop="proxyPort" v-if="checkedColumnNames.includes('proxyPort')" :label="$t('commons.proxy_port')" min-width="100"/>
        <el-table-column prop="proxyName" v-if="checkedColumnNames.includes('proxyName')" :label="$t('commons.proxy_name')" min-width="120"/>
        <el-table-column prop="createTime" v-if="checkedColumnNames.includes('createTime')" :label="$t('commons.create_time')" min-width="160">
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" v-if="checkedColumnNames.includes('updateTime')" :label="$t('commons.update_time')" min-width="160">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('commons.operating')" fixed="right" min-width="100">
          <template v-slot:default="scope">
            <table-operator @editClick="edit(scope.row)" @deleteClick="del(scope.row)">
              <template v-slot:behind>
              </template>
            </table-operator>
          </template>
        </el-table-column>
      </hide-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create proxy-->
    <el-drawer class="rtl" :title="$t('proxy.create')" :visible.sync="createVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="120px" size="small" :rules="rule" ref="createProxyForm">
        <el-form-item :label="$t('commons.proxy_type')" :rules="{required: true, message: $t('commons.proxy_type') + this.$t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" v-model="form.proxyType" :placeholder="$t('commons.proxy_type')">
            <el-option
              v-for="item in proxyType"
              :key="item.id"
              :label="item.value"
              :value="item.id">
              &nbsp;&nbsp; {{ item.value }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Proxy IP" prop="proxyIp">
          <el-input v-model="form.proxyIp" autocomplete="off" :placeholder="$t('proxy.proxy_ip')"/>
        </el-form-item>
        <el-form-item :label="$t('commons.proxy_port')" prop="proxyPort">
          <el-input type="number" v-model="form.proxyPort" autocomplete="off" :placeholder="$t('proxy.proxy_port')"/>
        </el-form-item>
        <el-form-item :label="$t('commons.proxy_name')" prop="proxyName">
          <el-input v-model="form.proxyName" autocomplete="off" :placeholder="$t('proxy.proxy_name')"/>
        </el-form-item>
        <el-form-item :label="$t('commons.proxy_password')" prop="proxyPassword" style="margin-bottom: 29px">
          <el-input v-model="form.proxyPassword" autocomplete="new-password" show-password
                    :placeholder="$t('proxy.proxy_password')"/>
        </el-form-item>
      </el-form>
      <dialog-footer
        @cancel="handleClose()"
        @confirm="createProxy('createProxyForm')"/>
    </el-drawer>

    <!--Modify proxy information in system settings-->
    <el-drawer class="rtl" :title="$t('proxy.modify')" :visible.sync="updateVisible" size="60%" :before-close="handleClose" :direction="direction" :destroy-on-close="true"
               :validate-on-rule-change="true" v-loading="result.loading">
      <el-form :model="form" label-position="right" label-width="120px" size="small" :rules="rule" ref="updateProxyForm">
        <el-form-item :label="$t('commons.proxy_type')" :rules="{required: true, message: $t('commons.proxy_type') + this.$t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" v-model="form.proxyType" :placeholder="$t('commons.proxy_type')">
            <el-option
              v-for="item in proxyType"
              :key="item.id"
              :label="item.value"
              :value="item.id">
              &nbsp;&nbsp; {{ item.value }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Proxy IP" prop="proxyIp">
          <el-input v-model="form.proxyIp" autocomplete="off" :placeholder="$t('proxy.proxy_ip')"/>
        </el-form-item>
        <el-form-item :label="$t('commons.proxy_port')" prop="proxyPort">
          <el-input type="number" v-model="form.proxyPort" autocomplete="off" :placeholder="$t('proxy.proxy_port')"/>
        </el-form-item>
        <el-form-item :label="$t('commons.proxy_name')" prop="proxyName">
          <el-input v-model="form.proxyName" autocomplete="off" :placeholder="$t('proxy.proxy_name')"/>
        </el-form-item>
        <el-form-item :label="$t('commons.proxy_password')" prop="proxyPassword" style="margin-bottom: 29px">
          <el-input v-model="form.proxyPassword" autocomplete="new-password" show-password
                    :placeholder="$t('proxy.proxy_password')"/>
        </el-form-item>
      </el-form>
      <dialog-footer
        @cancel="handleClose()"
        @confirm="updateProxy('updateProxyForm')"/>
    </el-drawer>

  </div>
</template>

<script>
import CreateBox from "../CreateBox";
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../../common/components/DialogFooter";
import TableOperatorButton from "../../common/components/TableOperatorButton";
import {_filter, _sort, listenGoBack, removeGoBackListener} from "@/common/js/utils";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {PROXY_CONFIGS} from "../../common/components/search/search-components";
import {
  addProxyUrl,
  deleteProxysUrl,
  deleteProxyUrl,
  proxyListUrl,
  updateProxyUrl
} from "@/api/system/system";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'commons.proxy_type',
    props: 'proxyType',
    disabled: false
  },
  {
    label: 'Proxy IP',
    props: 'proxyIp',
    disabled: false
  },
  {
    label: 'commons.proxy_port',
    props: 'proxyPort',
    disabled: false
  },
  {
    label: 'commons.proxy_name',
    props: 'proxyName',
    disabled: false
  },
  {
    label: 'commons.create_time',
    props: 'createTime',
    disabled: false
  },
  {
    label: 'commons.update_time',
    props: 'updateTime',
    disabled: false
  },
];

/* eslint-disable */
  export default {
    name: "Proxy",
    components: {
      CreateBox,
      TablePagination,
      TableHeader,
      TableOperator,
      DialogFooter,
      TableOperatorButton,
      HideTable,
    },
    data() {
      return {
        result: {},
        viewResult: {},
        selectIds: new Set(),
        createVisible: false,
        updateVisible: false,
        multipleSelection: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        condition: {
          components: PROXY_CONFIGS
        },
        tableData: [],
        form: {
        },
        direction: 'rtl',
        rule: {
          proxyIp: [
            {required: true, message: this.$t('proxy.proxy_ip'), trigger: 'blur'},
            {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
          ],
          proxyPort: [
            {required: true, message: this.$t('proxy.proxy_port'), trigger: 'blur'},
            {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
          ],
          proxyName: [
            {required: false, message: this.$t('proxy.proxy_name'), trigger: 'blur'},
            {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
          ],
          proxyPassword: [
            {required: false, message: this.$t('proxy.proxy_password'), trigger: 'blur'},
            {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
          ],
        },
        proxyType: [
          {id: 'Http', value: "Http"},
          {id: 'Https', value: "Https"},
        ],
        //名称搜索
        items: [
          {
            name: 'commons.proxy_type',
            id: 'proxyType',
          },
          {
            name: 'Proxy IP',
            id: 'proxyIp',
          },
          {
            name: 'commons.proxy_port',
            id: 'proxyPort',
          },
          {
            name: 'commons.proxy_name',
            id: 'proxyName',
          },
        ],
        checkedColumnNames: columnOptions.map((ele) => ele.props),
        columnNames: columnOptions,
        checkAll: true,
        isIndeterminate: false,
      }
    },
    activated() {
      this.search();
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
      select(selection) {
        this.selectIds.clear();
        selection.forEach(s => {
          this.selectIds.add(s.id)
        });
      },
      sort(column) {
        _sort(column, this.condition);
        this.search();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.search();
      },
      create() {
        this.createVisible = true;
        this.form= {};
        listenGoBack(this.handleClose);
      },
      edit(row) {
        this.updateVisible = true;
        this.form = Object.assign({}, row);
        listenGoBack(this.handleClose);
      },
      del(row) {
        this.$confirm(this.$t('proxy.delete_confirm'), '', {
          confirmButtonText: this.$t('commons.confirm'),
          cancelButtonText: this.$t('commons.cancel'),
          type: 'warning'
        }).then(() => {
          this.result = this.$get(deleteProxyUrl + encodeURIComponent(row.id), () => {
            this.$success(this.$t('commons.delete_success'));
            this.search();
          });
        }).catch(() => {
          this.$info(this.$t('commons.delete_cancel'));
        });
      },
      createProxy(createProxyForm) {
        this.$refs[createProxyForm].validate(valid => {
          if (valid) {
            this.viewResult = this.$post(addProxyUrl, this.form, () => {
              this.$success(this.$t('commons.save_success'));
              this.search();
              this.createVisible = false;
            });
          } else {
            return false;
          }
        })
      },
      updateProxy(updateProxyForm) {
        this.$refs[updateProxyForm].validate(valid => {
          if (valid) {
            this.viewResult = this.$post(updateProxyUrl, this.form, () => {
              this.$success(this.$t('commons.modify_success'));
              this.updateVisible = false;
              this.search();
            });
          } else {
            return false;
          }
        })
      },
      search() {
        this.result = this.$post(this.buildPagePath(proxyListUrl), this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        })
      },
      handleClose() {
        this.form = {};
        removeGoBackListener(this.handleClose);
        this.createVisible =  false;
        this.updateVisible =  false;
      },
      buildPagePath(path) {
        return path + "/" + this.currentPage + "/" + this.pageSize;
      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
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
      deleteBatch() {
        if (this.selectIds.size === 0) {
          this.$warning(this.$t('commons.please_select') + this.$t('commons.proxy'));
          return;
        }
        this.$alert(this.$t('oss.delete_batch') + this.$t('commons.proxy') + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$request({
                method: 'POST',
                url: deleteProxysUrl,
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
    }
  }
</script>

<style scoped>
  .rtl >>> .el-drawer__body {
    overflow-y: auto;
    padding: 20px;
  }
  .rtl >>> input {
    width: 100%;
  }
  .rtl >>> .el-select {
    width: 100%;
  }
  .rtl >>> .el-form-item__content {
    width: 80%;
  }
  /deep/ :focus{outline:0;}
</style>
