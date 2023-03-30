<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('image.result_list')"
                      :items="items" :columnNames="columnNames"
                      :checkedColumnNames="checkedColumnNames" :checkAll="checkAll" :isIndeterminate="isIndeterminate"
                      @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange"
                      @handleCheckAllChange="handleCheckAllChange"/>
      </template>

      <hide-table
        :table-data="tableData"
        @sort-change="sort"
        @filter-change="filter"
        @select-all="select"
        @select="select"
      >
        <el-table-column type="index" min-width="40"/>
        <el-table-column prop="name" v-if="checkedColumnNames.includes('name')" :label="$t('image.image_name')"
                         min-width="120" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)"
                     style="width: 30px; height: 25px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('imageUrl')"
                         :label="$t('image.image_url')" min-width="200" show-overflow-tooltip>
          <el-row v-if="scope.row.type==='repo'">{{ scope.row.imageUrl }}:{{ scope.row.imageTag }}</el-row>
          <el-row v-if="scope.row.type==='image'">{{ scope.row.imageUrl }}:{{ scope.row.imageTag }}</el-row>
          <el-row v-if="scope.row.type==='tar'">{{ scope.row.path }}</el-row>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('returnSum')"
                         :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip
                         min-width="200">
          <el-tooltip effect="dark"
                      :content="$t('history.result') + ' CRITICAL:' + scope.row.critical + ' HIGH:' +  scope.row.high + ' MEDIUM:' + scope.row.medium + ' LOW:' + scope.row.low + ' UNKNOWN:' + scope.row.unknown"
                      placement="top">
            <div class="txt-click" @click="goResource(scope.row)">
              <span style="background-color: #8B0000;color: white;padding: 3px;">{{ 'C:' + scope.row.critical }}</span>
              <span style="background-color: #FF4D4D;color: white;padding: 3px;">{{ 'H:' + scope.row.high }}</span>
              <span style="background-color: #FF8000;color: white;padding: 3px;">{{ 'M:' + scope.row.medium }}</span>
              <span style="background-color: #eeab80;color: white;padding: 3px;">{{ 'L:' + scope.row.low }}</span>
              <span style="background-color: #d5d0d0;color: white;padding: 3px;">{{ 'U:' + scope.row.unknown }}</span>
            </div>
          </el-tooltip>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('resultStatus')"
                         :label="$t('image.result_status')" min-width="130" prop="resultStatus" sortable
                         show-overflow-tooltip>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary"
                     v-if="scope.row.resultStatus === 'UNCHECKED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary"
                     v-else-if="scope.row.resultStatus === 'APPROVED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary"
                     v-else-if="scope.row.resultStatus === 'PROCESSING'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="success"
                     v-else-if="scope.row.resultStatus === 'FINISHED'">
            <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="danger"
                     v-else-if="scope.row.resultStatus === 'ERROR'">
            <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="warning"
                     v-else-if="scope.row.resultStatus === 'WARNING'">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
          </el-button>
        </el-table-column>
        <el-table-column prop="updateTime" min-width="150" v-if="checkedColumnNames.includes('updateTime')"
                         :label="$t('image.last_modified')" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="170" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Result log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="85%"
               :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-row class="el-form-item-dev" v-if="logData.length == 0">
        <span>{{ $t('resource.i18n_no_data') }}<br></span>
      </el-row>
      <el-row class="el-form-item-dev" v-if="logData.length > 0">
        <div>
          <el-row>
            <el-col :span="24">
              <div class="grid-content bg-purple-light">
                <span class="grid-content-log-span">
                  {{ logForm.name }}
                  <i class="el-icon-document-copy" @click="copy(logForm)" style="display: none;"></i>
                </span>
                <span class="grid-content-log-span">
                  <img :src="require(`@/assets/img/platform/docker.png`)"
                       style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ logForm.imageName }}
                </span>
                <span class="grid-content-status-span" v-if="logForm.resultStatus === 'APPROVED'"
                      style="color: #579df8">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'FINISHED'"
                      style="color: #7ebf50">
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
        <log-form :logForm="logForm"/>
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
import TableOperators from "@/business/components/common/components/TableOperators";
import MainContainer from "@/business/components/common/components/MainContainer";
import Container from "@/business/components/common/components/Container";
import TableHeader from "@/business/components/common/components/TableHeader";
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableOperator from "@/business/components/common/components/TableOperator";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {_filter, _sort} from "@/common/js/utils";
import {IMAGE_RESULT_CONFIGS} from "@/business/components/common/components/search/search-components";
import {saveAs} from "@/common/js/FileSaver";
import {severityOptions} from "@/common/js/constants";
import LogForm from "@/business/components/image/home/LogForm";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {
  deleteImageResultUrl,
  getImageResultUrl,
  getImageResultWithBLOBsUrl,
  imageDownloadUrl,
  imageLogUrl,
  imageReScanUrl,
  imageResultListUrl
} from "@/api/k8s/image/image";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'image.image_name',
    props: 'name',
    disabled: false
  },
  {
    label: 'image.image_url',
    props: 'imageUrl',
    disabled: false
  },
  {
    label: 'resource.i18n_not_compliance',
    props: 'returnSum',
    disabled: false
  },
  {
    label: 'image.result_status',
    props: 'resultStatus',
    disabled: false
  },
  {
    label: 'image.last_modified',
    props: 'updateTime',
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
    LogForm,
    HideTable,
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
          tip: this.$t('resource.scan'), icon: "el-icon-refresh-right", type: "success",
          exec: this.handleScans
        },
        {
          tip: this.$t('resource.download_report'), icon: "el-icon-bottom", type: "warning",
          exec: this.handleDownload
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
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'image.image_name',
          id: 'name'
        },
        {
          name: 'image.image_url',
          id: 'imageUrl'
        },
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
    handleVuln() {
      window.open('http://www.cnnvd.org.cn/', '_blank', '');
    },
    //查询列表
    search() {
      let url = imageResultListUrl + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    getStatus() {
      if (this.checkStatus(this.tableData)) {
        this.search();
        clearInterval(this.timer);
      } else {
        for (let data of this.tableData) {
          this.$get(getImageResultUrl + data.id, response => {
            let result = response.data;
            if (data.resultStatus !== result.resultStatus) {
              data.resultStatus = result.resultStatus;
              data.returnSum = result.returnSum;
              data.critical = result.critical ? result.critical : 0;
              data.high = result.high ? result.high : 0;
              data.medium = result.medium ? result.medium : 0;
              data.low = result.low ? result.low : 0;
              data.unknown = result.unknown ? result.unknown : 0;
            }
          });
        }
      }
    },
    //是否是结束状态，返回false代表都在运行中，true代表已结束
    checkStatus(tableData) {
      let sum = 0;
      for (let row of tableData) {
        if (row.resultStatus != 'ERROR' && row.resultStatus != 'FINISHED' && row.resultStatus != 'WARNING') {
          sum++;
        }
      }
      return sum == 0;
    },
    severityOptionsFnc() {
      this.severityOptions = severityOptions;
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
    showResultLog(result) {
      this.result = this.$get(imageLogUrl + result.id, response => {
        this.logData = response.data;
      });
      this.result = this.$get(getImageResultWithBLOBsUrl + result.id, response => {
        this.logForm = response.data;
        this.logForm.resultJson = JSON.parse(this.logForm.resultJson);
      });
      this.logVisible = true;
    },
    handleClose() {
      this.logVisible = false;
      this.detailVisible = false;
    },
    handleScans(item) {
      this.$alert(this.$t('resource.handle_scans'), '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$get(imageReScanUrl + item.id, response => {
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
            this.result = this.$get(deleteImageResultUrl + obj.id, res => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    goResource(params) {
      if (params.returnSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      let path = this.$route.path;
      if (path.indexOf("/image") >= 0) {
        let p = '/image/resultdetails/' + params.id;
        this.$router.push({
          path: p
        }).catch(error => error);
      } else if (path.indexOf("/resource") >= 0) {
        let p = '/resource/ImageResultdetails/' + params.id;
        this.$router.push({
          path: p
        }).catch(error => error);
      }
    },
    handleDownload(item) {
      this.$post(imageDownloadUrl, {
        id: item.id
      }, response => {
        if (response.success) {
          let blob = new Blob([response.data], {type: "application/json"});
          saveAs(blob, item.name + ".json");
        }
      }, error => {
        console.log("下载报错", error);
      });
    },
    copy(row) {
      let input = document.createElement("input");
      document.body.appendChild(input);
      input.value = row['command'];
      input.select();
      if (input.setSelectionRange) {
        input.setSelectionRange(0, input.value.length);
      }
      document.execCommand("copy");
      document.body.removeChild(input);
    },
  },
  computed: {
    codemirror() {
      return this.$refs.cmEditor.codemirror;
    }
  },
  activated() {
    this.init();
    this.location = window.location.href.split("#")[0];
    this.timer = setInterval(this.getStatus, 10000);
  },
  created() {
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

.el-form-item-dev >>> .el-form-item__content {
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

.tag-v {
  margin: 10px;
  cursor: pointer;
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

.el-form-item-dev >>> .el-form-item__content {
  margin-left: 0 !important;
}

.grid-content-log-span {
  width: 39%;
  float: left;
  vertical-align: middle;
  display: table-cell;
  margin: 6px 0 6px 2px;
  color: #606266;
}

.grid-content-status-span {
  width: 20%;
  float: left;
  vertical-align: middle;
  display: table-cell;
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

.txt-click {
  cursor: pointer;
}

.txt-click:hover {
  color: aliceblue;
  text-shadow: 1px 1px 1px #000;
}

* {
  touch-action: pan-y;
}

/deep/ :focus {
  outline: 0;
}
</style>
