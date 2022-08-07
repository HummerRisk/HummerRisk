<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">

        <template v-slot:header>
          <table-header :condition.sync="condition"
                        @search="search"
                        :show-back="true"
                        @back="back" :backTip="$t('resource.back_resource')"
                        :title="$t('resource.result_details_list')"/>
        </template>

        <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" @filter-change="filter" :row-class-name="tableRowClassName">
          <!-- 展开 start -->
          <el-table-column type="expand">
            <template v-slot:default="props">

              <el-divider><i class="el-icon-folder-opened"></i></el-divider>
              <el-form v-if="props.row.resource !== '[]'">
                  <result-read-only :row="typeof(props.row.resource) === 'string'?JSON.parse(props.row.resource):props.row.resource"></result-read-only>
                  <el-divider><i class="el-icon-document-checked"></i></el-divider>
              </el-form>
            </template>
          </el-table-column>
          <!-- 展开 end -->
          <el-table-column type="index" min-width="5%"/>
          <el-table-column v-slot:default="scope" :label="$t('resource.Hummer_ID')" min-width="28%">
              {{ scope.row.hummerId }}
          </el-table-column>
          <el-table-column v-slot:default="scope" :label="$t('rule.resource_type')" min-width="20%">
              {{ scope.row.resourceType }}
          </el-table-column>
          <el-table-column :label="$t('account.cloud_account')" min-width="15%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.pluginName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="regionName" :label="$t('account.regions')" min-width="15%"/>
          <el-table-column min-width="20%" :label="$t('account.update_time')" prop="updateTime">
            <template v-slot:default="scope">
              <span><i class="el-icon-time"></i> {{ scope.row.updateTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
      </el-card>

      <!--file-->
      <el-drawer class="rtl" :title="string2Key" :visible.sync="visible"  size="80%" :before-close="handleClose" :direction="direction" :destroy-on-close="true">
        <codemirror ref="cmEditor" v-model="string2PrettyFormat" class="code-mirror" :options="cmOptions" />
      </el-drawer>
      <!--file-->

    </main-container>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import TableHeader from "../head/DetailTableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../../common/components/RuleDialogFooter";
import CenterChart from "../../common/components/CenterChart";
import ResultReadOnly from "./ResultReadOnly";
import {_filter, _sort} from "@/common/js/utils";
/* eslint-disable */
  export default {
    name: "ResultDetails",
    components: {
      TableOperators,
      MainContainer,
      Container,
      TableHeader,
      TablePagination,
      TableOperator,
      DialogFooter,
      ResultReadOnly,
      CenterChart
    },
    data() {
      return {
        result: {},
        tableData: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        condition: {
        },
        tagSelect: [],
        resourceTypes: [],
        taskId: "",
        direction: 'rtl',
        buttons: [
          {
            tip: this.$t('resource.scan'), icon: "el-icon-refresh-right", type: "success",
            exec: this.scanAgain
          }
        ],
        platforms: [
          {text: this.$t('account.aliyun'), value: 'hummer-aliyun-plugin'},
          {text: this.$t('account.tencent'), value: 'hummer-qcloud-plugin'},
          {text: this.$t('account.huawei'), value: 'hummer-huawei-plugin'},
          {text: this.$t('account.aws'), value: 'hummer-aws-plugin'},
          {text: this.$t('account.azure'), value: 'hummer-azure-plugin'},
          {text: this.$t('account.vsphere'), value: 'hummer-vsphere-plugin'},
          {text: this.$t('account.openstack'), value: 'hummer-openstack-plugin'},
          {text: this.$t('account.huoshan'), value: 'hummer-huoshan-plugin'},
          {text: this.$t('account.baidu'), value: 'hummer-baidu-plugin'},
          {text: this.$t('account.qiniu'), value: 'hummer-qiniu-plugin'},
          {text: this.$t('account.qingcloud'), value: 'hummer-qingcloud-plugin'},
          {text: this.$t('account.ucloud'), value: 'hummer-ucloud-plugin'},
          {text: this.$t('account.k8s'), value: 'hummer-k8s-plugin'}
        ],
        string2Key: "",
        string2PrettyFormat: "",
        visible: false,
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
      }
    },
    props: ["id"],
    watch: {
      '$route': 'init'
    },
    methods: {
      scanAgain() {
      },
      sort(column) {
        _sort(column, this.condition);
        this.init();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.init();
      },
      showInformation (row, details, title) {
        this.string2Key = title;
        this.string2PrettyFormat = "";
        if (row) {
          this.$post("/resource/string2PrettyFormat", {json: details}, res => {
            this.string2PrettyFormat = res.data;
          });
        } else {
          this.string2PrettyFormat = details;
        }

        this.visible =  true;
      },
      handleClose() {
        this.visible =  false;
      },
      search () {
        let url = "/resource/list/" + this.currentPage + "/" + this.pageSize;
        this.condition.taskId = this.taskId;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
      },
      init() {
        this.taskId = this.$route.params.id;
        this.search();
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
      back () {
        let path = this.$route.path;
        if (path.indexOf("/account") >= 0) {
          this.$router.push({
            path: '/account/result',
          }).catch(error => error);
        } else if (path.indexOf("/resource") >= 0) {
          this.$router.push({
            path: '/resource/result',
          }).catch(error => error);
        }
      },
      goRule (ruleId) {
        this.$router.push({
          path: '/rule/rule',
        }).catch(error => error);
      },
    },
    computed: {
      codemirror() {
        return this.$refs.cmEditor.codemirror
      }
    },
    created() {
      this.init();
    }
  }
</script>

<style scoped>
  .el-form-tag-b {
    margin: 0 15px;
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
</style>
