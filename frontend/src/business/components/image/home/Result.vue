<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <image-result-header :condition.sync="condition"
                               @search="search"
                               :title="$t('image.result_list')"/>
      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                @filter-change="filter">
        <el-table-column type="index" min-width="3%"/>
        <el-table-column prop="name" :label="$t('image.image_name')" min-width="10%" show-overflow-tooltip></el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('image.image_url')" min-width="24%" show-overflow-tooltip>
          <el-row v-if="scope.row.type==='image'">{{ scope.row.imageUrl }} : {{ scope.row.imageTag }}</el-row>
          <el-row v-if="scope.row.type==='tar'">{{ scope.row.path }}</el-row>
        </el-table-column>
        <el-table-column prop="ruleName" :label="$t('image.rule_name')" min-width="10%" show-overflow-tooltip></el-table-column>
        <el-table-column min-width="10%" :label="$t('image.severity')" column-key="severity">
          <template v-slot:default="{row}">
            <rule-type :row="row"/>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="6%">
          <el-tooltip class="item" effect="dark" :content="$t('history.resource_result')" placement="top">
            <el-link type="primary" class="text-click" @click="goResource(scope.row)">{{ scope.row.returnSum }}</el-link>
          </el-tooltip>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('image.result_status')" min-width="12%" prop="resultStatus" sortable show-overflow-tooltip>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="success" v-else-if="scope.row.resultStatus === 'FINISHED'">
            <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="danger" v-else-if="scope.row.resultStatus === 'ERROR'">
            <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="warning" v-else-if="scope.row.resultStatus === 'WARNING'">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
          </el-button>
        </el-table-column>
        <el-table-column prop="updateTime" min-width="15%" :label="$t('image.last_modified')" sortable>
          <template v-slot:default="scope">
            <span><i class="el-icon-time"></i> {{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="11%" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Result log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="65%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-row class="el-form-item-dev" v-if="logData.length == 0">
        <span>{{ $t('resource.i18n_no_data') }}<br></span>
      </el-row>
      <el-row class="el-form-item-dev" v-if="logData.length > 0">
        <div>
          <el-row>
            <el-col :span="24">
              <div class="grid-content bg-purple-light">
                <span class="grid-content-log-span"> {{ logForm.ruleName }} | {{ logForm.name }}</span>
                <span class="grid-content-log-span">
                  <img :src="require(`@/assets/img/platform/docker.png`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ logForm.imageName }}
                </span>
                <span class="grid-content-status-span" v-if="logForm.resultStatus === 'APPROVED'" style="color: #579df8">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'FINISHED'" style="color: #7ebf50">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'ERROR'" style="color: red;">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </span>
              </div>
            </el-col>
          </el-row>
        </div>
        <el-table :show-header="false" :data="logData" class="adjust-table table-content">
          <el-table-column>
            <template v-slot:default="scope">
              <div class="bg-purple-div">
                <span
                  v-bind:class="{true: 'color-red', false: ''}[scope.row.result == false]">
                      {{ scope.row.createTime | timestampFormatDate }}
                      {{ scope.row.operator }}
                      {{ scope.row.operation }}
                      {{ scope.row.output }}<br>
                </span>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-form>
          <div class="pure-span">{{ $t('image.grype_table') }}</div>
          <codemirror ref="cmEditor" v-model="logForm.grypeTable" class="code-mirror" :options="cmOptions" />
        </el-form>
        <el-form>
          <div class="pure-span">{{ $t('image.grype_json') }}</div>
          <codemirror ref="cmEditor" v-model="logForm.grypeJson" class="code-mirror" :options="cmOptions" />
        </el-form>
        <el-form>
          <div class="pure-span">{{ $t('image.syft_table') }}</div>
          <codemirror ref="cmEditor" v-model="logForm.syftTable" class="code-mirror" :options="cmOptions" />
        </el-form>
        <el-form>
          <div class="pure-span">{{ $t('image.syft_json') }}</div>
          <codemirror ref="cmEditor" v-model="logForm.syftJson" class="code-mirror" :options="cmOptions" />
        </el-form>
      </el-row>
      <template v-slot:footer>
        <dialog-footer
          @cancel="logVisible = false"
          @confirm="logVisible = false"/>
      </template>
    </el-drawer>
    <!--Result log-->

  </main-container>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import ImageResultHeader from "../head/ImageResultHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../head/DialogFooter";
import {_filter, _sort} from "@/common/js/utils";
import RuleType from "./RuleType";
import {IMAGE_RESULT_CONFIGS} from "../../common/components/search/search-components";

/* eslint-disable */
export default {
  components: {
    TableOperators,
    MainContainer,
    Container,
    ImageResultHeader,
    TablePagination,
    TableOperator,
    DialogFooter,
    RuleType
  },
  data() {
    return {
      result: {},
      condition: {
        components: IMAGE_RESULT_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      severityOptions: [],
      direction: 'rtl',
      logVisible: false,
      detailVisible: false,
      logForm: {},
      logData: [],
      detailForm: {},
      buttons: [
        {
          tip: this.$t('resource.scan_vuln_search'), icon: "el-icon-share", type: "primary",
          exec: this.handleVuln
        },
        {
          tip: this.$t('resource.scan'), icon: "el-icon-zoom-in", type: "success",
          exec: this.handleScans
        },
        {
          tip: this.$t('resource.delete_result'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
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
        location: "",
      },
    }
  },

  methods: {
    handleVuln() {
      window.open('http://www.cnnvd.org.cn/web/vulnerability/queryLds.tag','_blank','');
    },
    //查询列表
    search() {
      let url = "/image/resultList/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    getStatus () {
      if (this.checkStatus(this.tableData)) {
        this.search();
        clearInterval(this.timer);
        this.timer = setInterval(this.getStatus,30000);
      } else {
        for (let data of this.tableData) {
          let url = "/image/getImageResult/";
          this.$get(url + data.id, response => {
            let result = response.data;
            if (data.resultStatus !== result.resultStatus) {
              data.resultStatus = result.resultStatus;
              data.grypeTable = result.grypeTable;
              data.grypeJson = result.grypeJson;
              data.syftTable = result.syftTable;
              data.syftJson = result.syftJson;
            }
          });
        }
      }
    },
    //是否是结束状态，返回false代表都在运行中，true代表已结束
    checkStatus (tableData) {
      let sum = 0;
      for (let row of tableData) {
        if (row.resultStatus != 'ERROR' && row.resultStatus != 'FINISHED' && row.resultStatus != 'WARNING') {
          sum++;
        }
      }
      return sum == 0;
    },
    severityOptionsFnc () {
      this.severityOptions = [
        {key: '低风险', value: "LowRisk"},
        {key: '中风险', value: "MediumRisk"},
        {key: '高风险', value: "HighRisk"}
      ];
    },
    init() {
      this.severityOptionsFnc();
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
      if (rowIndex%4 === 0) {
        return 'success-row';
      } else if (rowIndex%2 === 0) {
        return 'warning-row';
      } else {
        return '';
      }
    },
    showResultLog (result) {
      let url = "/image/log/";
      this.$get(url + result.id, response => {
        this.logData = response.data;
        this.logVisible = true;
      });
      if(!result.grypeJson) {
        let url = "/image/getImageResult/";
        this.$get(url + result.id, response => {
          this.logForm = response.data;
        });
      } else {
        this.logForm = result;
      }
    },
    handleClose() {
      this.logVisible=false;
      this.detailVisible=false;
    },
    handleScans (item) {
      this.$alert(this.$t('resource.handle_scans'), '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$get("/image/reScan/" + item.id, response => {
              if (response.success) {
                this.search();
              }
            });
          }
        }
      });
    },
    handleDelete(obj) {
      this.$alert(this.$t('image.delete_confirm') + this.$t('image.result') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/image/deleteImageResult/" + obj.id,  res => {
              setTimeout(function () {window.location.reload()}, 2000);
              this.$success(this.$t('commons.delete_success'));
            });
          }
        }
      });
    },
    goResource (params) {
      if (params.returnSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      let p = '/image/resultdetails/' + params.id;
      this.$router.push({
        path: p
      }).catch(error => error);
    },
  },
  computed: {
    codemirror() {
      return this.$refs.cmEditor.codemirror;
    }
  },
  created() {
    this.init();
    this.location = window.location.href.split("#")[0];
    this.timer = setInterval(this.getStatus,5000);
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
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
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
.tag-v{
  margin: 10px;
  cursor:pointer;
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
.bg-purple-dark {
  background: #99a9bf;
}
.bg-purple {
  background: #d3dce6;
}
.bg-purple-light {
  background: #f2f2f2;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
}

.grid-content-log-span {
  width: 39%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0 6px 2px;
  color: #606266;
}

.grid-content-status-span {
  width: 20%;float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
}
.pure-span {
  color: #606266;
  margin: 10px 0;
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
* { touch-action: pan-y; }
/deep/ :focus{outline:0;}
</style>
