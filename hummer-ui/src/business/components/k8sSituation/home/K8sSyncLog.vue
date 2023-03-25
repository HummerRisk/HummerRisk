<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('k8s.sync_log_list')"
                      @create="create" :createTip="$t('k8s.sync_log_create')"
                      :show-create="true"
                      :items="items" :columnNames="columnNames"
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
        <el-table-column type="index" min-width="40"/>
        <el-table-column :label="$t('k8s.platform')" v-if="checkedColumnNames.includes('k8sName')" min-width="150" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.k8sName }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="operation" v-if="checkedColumnNames.includes('operation')" :label="$t('k8s.sync_log')" min-width="150"/>
        <el-table-column prop="operator" v-if="checkedColumnNames.includes('operator')" :label="$t('resource.creator')" min-width="100"/>
        <el-table-column prop="status" v-if="checkedColumnNames.includes('status')" min-width="100" :label="$t('code.status')">
          <template v-slot:default="scope">
            <el-tooltip class="item" effect="dark" :content="scope.row.output" placement="top">
              <el-tag size="mini" type="success" v-if="scope.row.result !== null">
                {{ $t('commons.success') }}
              </el-tag>
              <el-tag size="mini" type="primary" v-else-if="scope.row.result === null">
                {{ $t('resource.i18n_in_process') }}<i class="el-icon-loading"/>
              </el-tag>
              <el-tag size="mini" type="danger" v-else>
                {{ $t('commons.error') }}
              </el-tag>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="sum" v-if="checkedColumnNames.includes('sum')" :label="$t('resource.i18n_not_compliance')" min-width="100"/>
        <el-table-column prop="createTime" v-if="checkedColumnNames.includes('createTime')" :label="$t('k8s.sync_time')" min-width="160" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="50" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create sync-->
    <el-drawer class="rtl" :title="$t('k8s.sync_log_create')" :visible.sync="createVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form">
        <el-form-item :label="$t('k8s.platform')" :rules="{required: true, message: $t('k8s.platform') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" filterable :clearable="true" v-model="form.id" :placeholder="$t('k8s.please_choose_k8s')">
            <el-option
              v-for="item in k8s"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              <img :src="require(`@/assets/img/platform/${item.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
              &nbsp;&nbsp; {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="createVisible = false"
          @confirm="saveSync"/>
      </div>
    </el-drawer>
    <!--Create sync-->

  </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperator from "../../common/components/TableOperator";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {K8S_SITUATION_LOG_CONFIGS} from "../../common/components/search/search-components";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {allCloudNativeListUrl, deleteK8sSyncLogUrl, syncK8sListUrl, syncK8sSourceUrl} from "@/api/k8s/k8s/k8s";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'k8s.platform',
    props: 'k8sName',
    disabled: false
  },
  {
    label: 'k8s.sync_log',
    props: 'operation',
    disabled: false
  },
  {
    label: 'resource.creator',
    props: 'operator',
    disabled: false
  },
  {
    label: 'code.status',
    props: 'status',
    disabled: false
  },
  {
    label: 'resource.i18n_not_compliance',
    props: 'sum',
    disabled: false
  },
  {
    label: 'k8s.sync_time',
    props: 'createTime',
    disabled: false
  },
];

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
    HideTable,
  },
  data() {
    return {
      credential: {},
      result: {},
      condition: {
        components: K8S_SITUATION_LOG_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      createVisible: false,
      form: {},
      direction: 'rtl',
      rule: {
        name: [
          {required: true, message: this.$t('commons.input_name'), trigger: 'blur'},
          {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
          {
            required: true,
            message: this.$t("workspace.special_characters_are_not_supported"),
            trigger: 'blur'
          }
        ],
      },
      buttons: [
       {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      k8s: [],
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'k8s.platform',
          id: 'k8sName'
        },
        {
          name: 'resource.creator',
          id: 'operator'
        }
      ],
      checkAll: true,
      isIndeterminate: false,
    }
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
    },
    create() {
      this.form = {};
      this.createVisible = true;
    },
    initK8s() {
      this.result = this.$get(allCloudNativeListUrl,response => {
        this.k8s = response.data;
      });
    },
    saveSync() {
      this.result = this.$get(syncK8sSourceUrl + this.form.id,response => {
        this.$success(this.$t('k8s.notes'));
        this.search();
        this.handleClose();
      });
    },
    //查询列表
    search() {
      let url = syncK8sListUrl + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleClose() {
      this.createVisible =  false;
    },
    handleDelete(obj) {
      this.$alert(this.$t('commons.delete_confirm') + this.$t('k8s.sync_log') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(deleteK8sSyncLogUrl + obj.id, () => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    init() {
      this.search();
      this.initK8s();
    },
    sort(column) {
      _sort(column, this.condition);
      this.search();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.search();
    },
  },
  created() {
    this.init();
    this.timer = setInterval(this.search,60000);
  },
  beforeDestroy() {
    clearInterval(this.timer);
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
/deep/ :focus{outline:0;}
.el-box-card {
  margin: 10px 0;
}
.sbom-icon{
  color: royalblue;
  font-size: 30px;
  vertical-align: middle;
}
.sbom-icon-2{
  color: red;
  font-size: 25px;
  vertical-align: middle;
}
</style>

