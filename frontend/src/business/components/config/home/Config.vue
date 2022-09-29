<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('config.config_settings_list')"
                      @create="create" :createTip="$t('config.config_create')"
                      @validate="validate" :runTip="$t('account.one_validate')"
                      :show-run="true" :show-create="true"/>

      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort"
                :row-class-name="tableRowClassName"
                @filter-change="filter" @select-all="select" @select="select">
        <el-table-column type="selection" min-width="5%">
        </el-table-column>
        <el-table-column type="index" min-width="5%"/>
        <el-table-column :label="$t('config.name')" min-width="10%" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/config/yaml.png`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" min-width="10%" :label="$t('config.status')"
                         column-key="status"
                         :filters="statusFilters"
                         :filter-method="filterStatus">
          <template v-slot:default="{row}">
            <config-status :row="row"/>
          </template>
        </el-table-column>
        <el-table-column min-width="15%" :label="$t('account.create_time')" sortable
                         prop="createTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="15%" :label="$t('account.update_time')" sortable
                         prop="updateTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" :label="$t('account.creator')" min-width="8%" show-overflow-tooltip/>
        <el-table-column min-width="17%" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create k8s config-->
    <el-drawer class="rtl" :title="$t('config.config_create')" :visible.sync="createVisible" size="75%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form" :rules="rule">
        <el-form-item :label="$t('config.name')" ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('config.name')"/>
        </el-form-item>
        <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-switch v-model="isProxy"></el-switch>
        </el-form-item>
        <el-form-item v-if="isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
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
        <el-form-item :label="$t('config.config_type')" ref="type" prop="type">
          <el-radio v-model="configType" label="menu" @change="changeYaml">{{ $t('config.menu_config') }}</el-radio>
          <el-radio v-model="configType" label="k8s" @change="changeYaml">{{ $t('config.k8s_config') }}</el-radio>
          <el-radio v-model="configType" label="upload" @change="changeYaml">{{ $t('config.upload_config') }}</el-radio>
        </el-form-item>
        <el-form-item v-if="configType==='k8s'" :label="$t('k8s.k8s_setting')">
          <el-select style="width: 100%;" v-model="sourceId" :placeholder="$t('k8s.k8s_setting')" @change="changeSearch">
            <el-option
              v-for="item in k8s"
              :key="item.id"
              :label="item.sourceName"
              :value="item.sourceYaml">
              &nbsp;&nbsp; {{ '(namespace)' +  item.sourceNamespace + ':(source)' + item.sourceName }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="configType==='upload'" :label="$t('config.upload_yaml')">
          <yaml-upload v-on:appendYaml="appendYaml"/>
        </el-form-item>
        <el-form-item :label="$t('config.config_yaml')" ref="configYaml" prop="configYaml">
          <codemirror ref="cmEditor" v-model="form.configYaml" class="code-mirror" :options="cmOptions" />
        </el-form-item>
        <el-form-item>
          <span style="color: red">{{ $t('config.config_note') }}</span>
        </el-form-item>
      </el-form>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="createVisible = false"
          @confirm="save('form')"/>
      </div>
    </el-drawer>
    <!--Create k8s config-->

    <!--Update k8s config-->
    <el-drawer class="rtl" :title="$t('config.config_update')" :visible.sync="updateVisible" size="75%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form" :rules="rule">
        <el-form-item :label="$t('config.name')" ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('config.name')"/>
        </el-form-item>
        <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-switch v-model="isProxy"></el-switch>
        </el-form-item>
        <el-form-item v-if="isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
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
        <el-form-item :label="$t('config.config_type')" ref="type" prop="type">
          <el-radio v-model="configType" label="menu" @change="changeYaml">{{ $t('config.menu_config') }}</el-radio>
          <el-radio v-model="configType" label="k8s" @change="changeYaml">{{ $t('config.k8s_config') }}</el-radio>
          <el-radio v-model="configType" label="upload" @change="changeYaml">{{ $t('config.upload_config') }}</el-radio>
        </el-form-item>
        <el-form-item v-if="configType==='k8s'" :label="$t('k8s.k8s_setting')">
          <el-select style="width: 100%;" v-model="sourceId" :placeholder="$t('k8s.k8s_setting')" @change="changeSearch">
            <el-option
              v-for="item in k8s"
              :key="item.id"
              :label="item.sourceName"
              :value="item.sourceYaml">
              &nbsp;&nbsp; {{ '(namespace)' +  item.sourceNamespace + ':(source)' + item.sourceName }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="configType==='upload'" :label="$t('config.upload_yaml')">
          <yaml-upload v-on:appendYaml="appendYaml"/>
        </el-form-item>
        <el-form-item :label="$t('config.config_yaml')" ref="configYaml" prop="configYaml">
          <codemirror ref="cmEditor" v-model="form.configYaml" class="code-mirror" :options="cmOptions" />
        </el-form-item>
        <el-form-item>
          <span style="color: red">{{ $t('config.config_note') }}</span>
        </el-form-item>
      </el-form>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="updateVisible = false"
          @confirm="update('form')"/>
      </div>
    </el-drawer>
    <!--Update k8s config-->

  </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "../head/TableHeader";
import TableOperator from "../../common/components/TableOperator";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import ConfigStatus from "./ConfigStatus";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {CONFIG_CONFIGS} from "../../common/components/search/search-components";
import ProxyDialogFooter from "../head/ProxyDialogFooter";
import ProxyDialogCreateFooter from "../head/ProxyDialogCreateFooter";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import YamlUpload from "@/business/components/config/home/YamlUpload";

/* eslint-disable */
export default {
  name: "Event",
  components: {
    TableOperators,
    ConfigStatus,
    MainContainer,
    Container,
    TableHeader,
    TablePagination,
    TableOperator,
    DialogFooter,
    ProxyDialogFooter,
    ProxyDialogCreateFooter,
    YamlUpload,
  },
  data() {
    return {
      credential: {},
      result: {},
      condition: {
        components: CONFIG_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      selectIds: new Set(),
      createVisible: false,
      updateVisible: false,
      innerDrawer: false,
      innerDrawerProxy: false,
      plugins: [],
      proxys: [],
      k8s: [],
      tmpList: [],
      item: {},
      form: {},
      proxyForm: {},
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
        configYaml: [
          {required: true, message: this.$t('config.config_yaml'), trigger: 'blur'},
          {
            required: true,
            message: this.$t('config.config_yaml') + this.$t('commons.cannot_be_empty'),
            trigger: 'blur'
          }
        ],
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
      buttons: [
        {
          tip: this.$t('k8s.execute_scan'), icon: "el-icon-s-promotion", type: "success",
          exec: this.handleScan
        }, {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        }, {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      statusFilters: [
        {text: this.$t('account.INVALID'), value: 'INVALID'},
        {text: this.$t('account.VALID'), value: 'VALID'},
        {text: this.$t('account.DELETE'), value: 'DELETE'}
      ],
      proxyType: [
        {id: 'Http', value: "Http"},
        {id: 'Https', value: "Https"},
      ],
      cmOptions: {
        tabSize: 4,
        mode: 'text/x-yaml',
        theme: 'bespin',
        lineNumbers: true,
        line: true,
        indentWithTabs: true,
      },
      configType: 'menu',
      isProxy: false,
      sourceId: '',
    }
  },
  methods: {
    create() {
      this.form = {};
      this.createVisible = true;
    },
    //查询代理
    activeProxy() {
      let url = "/proxy/list/all";
      this.result = this.$get(url, response => {
        this.proxys = response.data;
      });
    },
    //查询k8s配置
    activeK8s() {
      let url = "/k8s/allCloudNativeSource2YamlList";
      this.result = this.$get(url, response => {
        this.k8s = response.data;
      });
    },
    //校验云原生账号
    validate() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('k8s.please_choose_k8s'));
        return;
      }
      this.$alert(this.$t('account.one_validate') + this.$t('k8s.k8s_setting') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            let formData = new FormData();
            this.result = this.$request({
              method: 'POST',
              url: "/k8s/validate",
              data: Array.from(this.selectIds),
              headers: {
                'Content-Type': undefined
              }
            }, res => {
              if (res.data) {
                this.$success(this.$t('commons.success'));
              } else {
                this.$error(this.$t('commons.error'));
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
      let url = "/config/list/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleClose() {
      this.createVisible =  false;
      this.updateVisible =  false;
    },
    init() {
      this.selectIds.clear();
      this.activeProxy();
      this.activeK8s();
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
    changeSearch(value){
      this.form.configYaml = value;
    },
    appendYaml(yaml) {
      this.form.configYaml = yaml;
    },
    save(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          this.result = this.$post("/config/add", this.form, () => {
            this.$success(this.$t('commons.save_success'));
            this.createVisible = false;
            this.search();
          });
        }
      });
    },
    update(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          this.result = this.$post("/config/update", this.form, () => {
            this.$success(this.$t('commons.update_success'));
            this.updateVisible = false;
            this.search();
          });
        }
      });
    },
    handleDelete(obj) {
      this.$alert(this.$t('workspace.delete_confirm') + obj.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/config/delete/" + obj.id, response => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    handleEdit(row) {
      this.form = row;
      this.updateVisible = true;
    },
    handleScan(item) {
      this.$alert(this.$t('image.one_scan') + item.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$get("/config/scan/" + item.id,response => {
              if (response.success) {
                this.$success(this.$t('schedule.event_start'));
                this.$router.push({
                  path: '/config/result',
                  query: {
                    date:new Date().getTime()
                  },
                }).catch(error => error);
              } else {
                this.$error(response.message);
              }
            });
          }
        }
      });
    },
    changeYaml() {
      this.form.configYaml = "";
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
.el-row-card {
  padding: 0 10px 0 10px;
  margin: 1%;
}
.split {
  height: 80px;
  border-left: 1px solid #D8DBE1;
}
.cl-ver-col {
  vertical-align: middle;
}
.cl-mid-row {
  margin: 0 0 1% 0;
}
.cl-btn-mid-row {
  margin: 1%;
}
.cl-span-col {
  margin: 1% 0;
}
.cl-btn-col {
  margin: 4% 0;
}
.cl-data-col {
  color: #888;
  font-size: 10px;
}
.cl-btn-data-col {
  color: #77aff9;
}

.input-inline-i{
  display:inline;
}

.input-inline-i >>> .el-input__inner {
  width: 68%;
}

.input-inline-t{
  display:inline;
}

.input-inline-t >>> .el-input__inner {
  width: 30%;
}

.co-el-img >>> .el-image {
  display: table-cell;
  left: 40%;
}
.cp-el-i {
  margin: 1%;
}
.co-el-i{
  width: 70px;
  height: 70px;
}
.code-mirror {
  height: 600px !important;
}
.code-mirror >>> .CodeMirror {
  /* Set height, width, borders, and global font properties here */
  height: 600px !important;
}
</style>

