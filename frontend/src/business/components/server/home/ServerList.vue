<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <server-table-header :condition.sync="condition" @search="search"
                           :title="$t('server.server_list')"
                           @create="create" :createTip="$t('server.server_create')"
                           @scan="scan" :scanTip="$t('server.one_scan')"
                           @validate="validate" :runTip="$t('server.one_validate')"
                           :show-run="true" :show-scan="true" :show-create="true"/>

        </template>

        <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort"
                  :row-class-name="tableRowClassName"
                  @filter-change="filter" @select-all="select" @select="select">
          <el-table-column type="selection" min-width="3%"/>
          <el-table-column type="index" min-width="3%"/>
          <el-table-column prop="name" :label="$t('server.server_name')" min-width="15%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ $t(scope.row.name) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="ip" :label="'IP'" min-width="15%" show-overflow-tooltip></el-table-column>
          <el-table-column prop="userName" :label="$t('server.server_user_name')" min-width="10%" show-overflow-tooltip></el-table-column>
          <el-table-column prop="status" min-width="12%" :label="$t('server.server_status')"
                           column-key="status"
                           :filters="statusFilters"
                           :filter-method="filterStatus">
            <template v-slot:default="{row}">
              <server-status :row="row"/>
            </template>
          </el-table-column>
          <el-table-column min-width="18%" :label="$t('account.update_time')" sortable
                           prop="updateTime">
            <template v-slot:default="scope">
              <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="user" :label="$t('account.creator')" min-width="10%" show-overflow-tooltip/>
          <el-table-column min-width="14%" :label="$t('commons.operating')" fixed="right">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
      </el-card>

      <!--Create server-->
      <el-drawer class="rtl" :title="$t('server.server_create')" :visible.sync="createVisible" size="70%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div style="margin: 10px;">
          <el-row>
            <el-col :span="10">
              <el-button icon="el-icon-circle-plus-outline" plain size="mini" @click="handleAddServerModel">
                {{ $t('server.server_add') }}
              </el-button>
            </el-col>
          </el-row>
        </div>
        <div>
          <el-row>
            <el-col :span="24">
              <el-table :data="servers" class="tb-edit" border :cell-style="rowClass" :header-cell-style="headClass">
                <el-table-column :label="$t('server.server_name')" min-width="20%" prop="serverName">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.name"></el-input>
                  </template>
                </el-table-column>
                <el-table-column :label="'IP'" prop="ip" min-width="20%">
                  <template v-slot:default="{row}">
                    <el-input v-model="row.ip"></el-input>
                  </template>
                </el-table-column>
                <el-table-column :label="$t('server.server_user_name')" min-width="20%" prop="userName">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.userName"></el-input>
                  </template>
                </el-table-column>
                <el-table-column :label="$t('commons.password')" min-width="20%" prop="password">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.password"></el-input>
                  </template>
                </el-table-column>
                <el-table-column :label="$t('commons.operating')" fixed="right" min-width="20%" prop="result">
                  <template v-slot:default="scope">
                    <el-button type="primary" size="mini" @click="handleAddServerModel(scope.$index, scope.row)">
                      {{ $t('commons.add') }}
                    </el-button>
                    <el-button type="danger" icon="el-icon-delete" size="mini" v-show="!scope.row.isSet"
                               @click.native.prevent="deleteRowServer(scope.$index, scope.row)"></el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-col>
          </el-row>
          <el-form :model="proxyForm" label-position="right" label-width="150px" size="small" ref="accountForm">
            <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-switch v-model="proxyForm.isProxy"></el-switch>
            </el-form-item>
            <el-form-item v-if="proxyForm.isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-select style="width: 100%;" v-model="proxyForm.proxyId" :placeholder="$t('commons.proxy')">
                <el-option
                  v-for="item in proxys"
                  :key="item.id"
                  :label="item.proxyIp"
                  :value="item.id">
                  &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}
                </el-option>
              </el-select>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin: 10px;">
          <dialog-footer
            @cancel="createVisible = false"
            @confirm="saveServer(servers)"/>
        </div>
      </el-drawer>
      <!--Create server-->

      <!--Update server-->
      <el-drawer class="rtl" :title="$t('server.server_update')" :visible.sync="updateVisible" size="50%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-form :model="form" label-position="right" label-width="150px" size="small" :rules="rule" ref="createServerForm">
          <el-form-item :label="$t('server.server_group_name')" ref="groupId" prop="groupId">
            <el-select style="width: 100%;" v-model="form.groupId" :placeholder="$t('server.server_group_name')">
              <el-option
                v-for="item in groups"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('server.server_name')" ref="name" prop="name">
            <el-input v-model="form.name" autocomplete="off" :placeholder="$t('server.server_name')"/>
          </el-form-item>
          <el-form-item :label="'IP'" ref="ip" prop="ip">
            <el-input v-model="form.ip" autocomplete="off" :placeholder="'IP'"/>
          </el-form-item>
          <el-form-item :label="$t('server.server_user_name')" ref="userName" prop="userName">
            <el-input v-model="form.userName" autocomplete="off" :placeholder="$t('server.server_user_name')"/>
          </el-form-item>
          <el-form-item :label="$t('commons.password')" ref="password" prop="password">
            <el-input type="password" v-model="form.password" autocomplete="off" :placeholder="$t('commons.password')" show-password/>
          </el-form-item>
          <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-switch v-model="form.isProxy"></el-switch>
          </el-form-item>
          <el-form-item v-if="form.isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="form.proxyId" :placeholder="$t('commons.proxy')">
              <el-option
                v-for="item in proxys"
                :key="item.id"
                :label="item.proxyIp"
                :value="item.id">
                &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <dialog-footer
          @cancel="updateVisible = false"
          @confirm="editServer(form)"/>
      </el-drawer>
      <!--Update server-->

    </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import ServerTableHeader from "../head/ServerTableHeader";
import TableOperator from "../../common/components/TableOperator";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import ServerStatus from "./ServerStatus";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {SERVER_CONFIGS} from "../../common/components/search/search-components";
import DialogFooter from "@/business/components/common/components/DialogFooter";

/* eslint-disable */
  export default {
    components: {
      TableOperators,
      ServerStatus,
      MainContainer,
      Container,
      ServerTableHeader,
      TablePagination,
      TableOperator,
      DialogFooter,
    },
    provide() {
      return {
        search: this.search,
      }
    },
    data() {
      return {
        result: {},
        servers: [],
        condition: {
          components: SERVER_CONFIGS
        },
        tableData: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        selectIds: new Set(),
        createVisible: false,
        updateVisible: false,
        item: {},
        form: {},
        script: '',
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
        buttons: [
          {
            tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
            exec: this.handleEdit
          }, {
            tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
            exec: this.handleDelete
          }
        ],
        statusFilters: [
          {text: this.$t('server.INVALID'), value: 'INVALID'},
          {text: this.$t('server.VALID'), value: 'VALID'},
          {text: this.$t('server.DELETE'), value: 'DELETE'}
        ],
        groupId: 'd691se79-2e8c-1s54-bbe6-491sd29e91fe',
        groups: [],
        proxyForm: {isProxy: false, proxyId: 0},
        proxys: [],
      }
    },
    props: {
      selectNodeIds: Array,
    },
    watch: {
      selectNodeIds() {
        this.search();
      },
      '$route': 'init',
    },

    methods: {
      create() {
        this.servers = [];
        this.createVisible = true;
      },
      //查询代理
      activeProxy() {
        let url = "/proxy/list/all";
        this.result = this.$get(url, response => {
          this.proxys = response.data;
        });
      },
      download() {},
      upload() {},
      //校验虚拟机ssh连接
      validate() {
        if (this.selectIds.size === 0) {
          this.$warning(this.$t('server.please_choose_server'));
          return;
        }
        this.$alert(this.$t('server.one_validate') + this.$t('server.server_setting') + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              let formData = new FormData();
              this.result = this.$request({
                method: 'POST',
                url: "/server/validate",
                data: Array.from(this.selectIds),
                headers: {
                  'Content-Type': undefined
                }
              }, res => {
                if (res.data) {
                  this.$success(this.$t('account.success'));
                } else {
                  this.$error(this.$t('account.error'));
                }
                this.search();
              });
            }
          }
        });
      },
      select(selection) {
        this.selectIds.clear();
        selection.forEach(s => {
          this.selectIds.add(s.id)
        });
      },
      //查询列表
      search() {
        if(this.selectNodeIds.length!=0) {
          this.condition.groupId = this.selectNodeIds[0];
          this.groupId = this.selectNodeIds[0];
        } else {
          this.condition.groupId = "";
        }
        let url = "/server/serverList/" + this.currentPage + "/" + this.pageSize;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
      },
      handleEdit(tmp) {
        let url = "/server/serverGroupList";
        this.result = this.$get(url, response => {
          if (response.data != undefined && response.data != null) {
            this.groups = response.data;
          }
        });
        this.form = tmp;
        this.updateVisible = true;
      },
      handleClose() {
        this.createVisible =  false;
        this.updateVisible =  false;
      },
      handleDelete(obj) {
        this.$alert(this.$t('server.delete_confirm') + obj.name + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$get("/server/deleteServer/" + obj.id, response => {
                this.$success(this.$t('commons.delete_success'));
                this.search();
              });
            }
          }
        });
      },
      change(e) {
        this.$forceUpdate();
      },
      init() {
        this.selectIds.clear();
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
      filterStatus(value, row) {
        return row.status === value;
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
      scan (){
        if (this.selectIds.size === 0) {
          this.$warning(this.$t('server.please_choose_server'));
          return;
        }
        this.$alert(this.$t('server.one_scan') + this.$t('server.server_rule') + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$request({
                method: 'POST',
                url: "/server/scan",
                data: Array.from(this.selectIds),
                headers: {
                  'Content-Type': undefined
                }
              }, res => {
                if (res.data) {
                  this.$success(this.$t('schedule.event_start'));
                } else {
                  this.$error(this.$t('schedule.event_failed'));
                }
                this.$router.push({
                  path: '/server/result',
                }).catch(error => error);
              });
            }
          }
        });
      },
      rowClass() {
        return "text-align:center"
      },
      headClass() {
        return "text-align:center;background:'#ededed'"
      },
      removeServer(index, data) { //移除
        if (!data[index].id) {
          data.splice(index, 1)
        } else {
          data[parseInt(index)].isSet = false;
        }
      },
      handleAddServerModel() {
        let server = {};
        server.name = '';
        server.ip = '';
        server.userName = '';
        server.password = '';
        server.groupId = this.groupId;
        this.servers.push(server);
        this.proxyForm = {isProxy: false, proxyId: 0};
      },
      deleteRowServer(index, data) { //删除
        this.servers.splice(index, 1);
      },
      saveServer(servers) {
        for (let server of servers) {
          if(!server.name || !server.ip || !server.userName || !server.password || !server.groupId) {
            this.$warning('value will not be null');
          } else {
            if (this.proxyForm.isProxy) {
              server.isProxy = true;
              server.proxyId = this.proxyForm.proxyId;
            } else {
              server.isProxy = false;
              server.proxyId = null;
            }
            this.result = this.$post('/server/addServer', server, response => {
              this.createVisible = false;
              this.search();
            });
          }
        }
      },
      editServer(server) {
        if (!server.isProxy) {
          server.proxyId = null;
        }
        this.result = this.$post('/server/editServer', server, response => {
          this.updateVisible = false;
          this.search();
        });
      },
    },
    created() {
      this.init();
      this.activeProxy();
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
    width: 75%;
  }
  .code-mirror {
    height: 600px !important;
  }
  .code-mirror >>> .CodeMirror {
    /* Set height, width, borders, and global font properties here */
    height: 600px !important;
  }
  /deep/ :focus{outline:0;}
  .el-box-card {
    margin: 10px 0;
  }
</style>
