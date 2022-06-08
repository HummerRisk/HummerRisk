<template>
  <div v-loading="result.loading">

    <el-card class="table-card">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search" @create="create"
                         :create-tip="$t('proxy.create')" :title="$t('commons.proxy')"/>
      </template>

      <el-table border class="adjust-table" :data="tableData" style="width: 100%" :row-class-name="tableRowClassName">
        <el-table-column type="index" min-width="5%"/>
        <el-table-column prop="proxyType" :label="$t('commons.proxy_type')" min-width="10%"/>
        <el-table-column prop="proxyIp" label="Proxy IP" min-width="15%"/>
        <el-table-column prop="proxyPort" :label="$t('commons.proxy_port')" min-width="10%"/>
        <el-table-column prop="proxyName" :label="$t('commons.proxy_name')" min-width="15%"/>
        <el-table-column prop="createTime" :label="$t('commons.create_time')" min-width="15%">
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" :label="$t('commons.update_time')" min-width="15%">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('commons.operating')" min-width="15%">
          <template v-slot:default="scope">
            <table-operator @editClick="edit(scope.row)" @deleteClick="del(scope.row)">
              <template v-slot:behind>
              </template>
            </table-operator>
          </template>
        </el-table-column>
      </el-table>

      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize"
                           :total="total"/>
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
import TableHeader from "../head/ProxyTableHeader";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../../common/components/DialogFooter";
import TableOperatorButton from "../../common/components/TableOperatorButton";
import {listenGoBack, removeGoBackListener} from "@/common/js/utils";
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
    },
    data() {
      return {
        queryPath: '/proxy/list',
        deletePath: '/proxy/delete/',
        createPath: '/proxy/add',
        updatePath: '/proxy/update',
        result: {},
        createVisible: false,
        updateVisible: false,
        multipleSelection: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        condition: {},
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
            {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
          ],
          proxyPassword: [
            {required: false, message: this.$t('proxy.proxy_password'), trigger: 'blur'},
            {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
          ],
        },
        proxyType: [
          {id: 'Http', value: "Http"},
          {id: 'Https', value: "Https"},
        ],
      }
    },
    activated() {
      this.search();
    },
    methods: {
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
          this.result = this.$get(this.deletePath + encodeURIComponent(row.id), () => {
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
            this.result = this.$post(this.createPath, this.form, () => {
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
            this.result = this.$post(this.updatePath, this.form, () => {
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
        if (!!this.condition.name) {
          this.condition.proxyIp = this.condition.name;
        } else {
          this.condition.proxyIp = null;
        }
        this.result = this.$post(this.buildPagePath(this.queryPath), this.condition, response => {
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
