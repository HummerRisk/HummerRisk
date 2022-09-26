<template>
  <el-row :gutter="24">
    <el-col :span="24">
      <el-card class="content" shadow="always">
        <el-descriptions :title="$t('k8s.source_sum')" :column="8" border direction="vertical">
          <el-descriptions-item label="DaemonSet" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.daemonsets }}</el-descriptions-item>
          <el-descriptions-item label="Ingress" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.ingress }}</el-descriptions-item>
          <el-descriptions-item label="Role" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.roles }}</el-descriptions-item>
          <el-descriptions-item label="Secret" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.secrets }}</el-descriptions-item>
          <el-descriptions-item label="ConfigMap" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.configmaps }}</el-descriptions-item>
          <el-descriptions-item label="StatefulSet" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.statefulSets }}</el-descriptions-item>
          <el-descriptions-item label="CronJob" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.cronJobs }}</el-descriptions-item>
          <el-descriptions-item label="Job" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.jobs }}</el-descriptions-item>
          <el-descriptions-item label="PV" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.pvs }}</el-descriptions-item>
          <el-descriptions-item label="PVC" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.pvcs }}</el-descriptions-item>
          <el-descriptions-item label="Lease" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.leases }}</el-descriptions-item>
          <el-descriptions-item label="EndpointSlice" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.endpointSlices }}</el-descriptions-item>
          <el-descriptions-item label="Event" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.events }}</el-descriptions-item>
          <el-descriptions-item label="NetworkPolicy" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.networkPolicies }}</el-descriptions-item>
          <el-descriptions-item label="Version" label-class-name="my-label" content-class-name="my-content">{{ situationInfo.versions }}</el-descriptions-item>
        </el-descriptions>

        <container v-loading="result.loading">
          <el-col :span="5">
            <el-card shadow="always" class="hr-card-index-1">
              <span class="hr-card-data">
                <span class="hr-card-data-digital">{{ situationInfo.namespaces }}</span>
                <span class="hr-card-data-unit"> {{ 'namespaces' }}</span>
              </span>
              <span class="hr-card-desc">{{ 'Namespace' }}</span>
            </el-card>
          </el-col>
          <el-col :span="5">
            <el-card shadow="always" class="hr-card-index-2">
              <span class="hr-card-data">
                <span class="hr-card-data-digital">{{ situationInfo.pods }}</span>
                <span class="hr-card-data-unit"> {{ 'pods' }}</span>
              </span>
              <span class="hr-card-desc">{{ 'Pod' }}</span>
            </el-card>
          </el-col>
          <el-col :span="5">
            <el-card shadow="always" class="hr-card-index-3">
              <span class="hr-card-data">
                <span class="hr-card-data-digital">{{ situationInfo.nodes }}</span>
                <span class="hr-card-data-unit"> {{ 'nodes' }}</span>
              </span>
              <span class="hr-card-desc">{{ 'Node' }}</span>
            </el-card>
          </el-col>
          <el-col :span="5">
            <el-card shadow="always" class="hr-card-index-4">
              <span class="hr-card-data">
                <span class="hr-card-data-digital">{{ situationInfo.deployments }}</span>
                <span class="hr-card-data-unit"> {{ 'deployments' }}</span>
              </span>
              <span class="hr-card-desc">{{ $t('Deployment') }}</span>
            </el-card>
          </el-col>
          <el-col :span="5">
            <el-card shadow="always" class="hr-card-index-5">
              <span class="hr-card-data">
                <span class="hr-card-data-digital">{{ situationInfo.services }}</span>
                <span class="hr-card-data-unit"> {{ 'services' }}</span>
              </span>
              <span class="hr-card-desc">{{ 'Service' }}</span>
            </el-card>
          </el-col>
        </container>

        <el-card class="table-card" v-loading="result.loading">
          <template v-slot:header>
            <table-header :condition.sync="condition" @search="search"
                          :title="$t('k8s.source_list')"/>
          </template>
          <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort"
                    :row-class-name="tableRowClassName"
                    @filter-change="filter">
            <el-table-column type="index" min-width="3%"/>
            <el-table-column :label="$t('k8s.platform')" min-width="15%" show-overflow-tooltip>
              <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.cloudNativeName }}
              </span>
              </template>
            </el-table-column>
            <el-table-column prop="sourceName" :label="$t('k8s.source_name')" min-width="23%" show-overflow-tooltip></el-table-column>
            <el-table-column prop="sourceNamespace" :label="$t('k8s.source_namespace')" min-width="16%" show-overflow-tooltip sortable></el-table-column>
            <el-table-column prop="sourceType" :label="$t('k8s.source_type')" min-width="13%" show-overflow-tooltip sortable></el-table-column>
            <el-table-column min-width="18%" :label="$t('k8s.sync_time')" sortable
                             prop="createTime">
              <template v-slot:default="scope">
                <span>{{ scope.row.createTime | timestampFormatDate }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="userName" :label="$t('account.creator')" min-width="12%" show-overflow-tooltip/>
          </el-table>
          <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
        </el-card>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import Container from "../.././common/components/Container";
import TableHeader from "../head/TableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {SITUATION_CONFIGS} from "../../common/components/search/search-components";
/* eslint-disable */
export default {
  components: {
    Container,
    TableHeader,
    TablePagination,
    TableOperators
  },
  data() {
    return {
      result: {},
      loading: false,
      situationInfo: {},
      condition: {
        components: SITUATION_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
    }
  },
  props: {
    selectNodeIds: Array,
  },
  watch: {
    selectNodeIds() {
      this.search();
    },
    batchReportId() {
    }
  },
  methods: {
    init() {
      this.search();
    },
    search() {
      let param = {};
      if (!!this.selectNodeIds) {
        param.cloudNativeId = this.selectNodeIds[0];
      }
      this.result = this.$post("/cloud/native/situation", param, response => {
        let data = response.data;
        this.situationInfo = data;
      });
      let url = "/cloud/native/cloudNativeSourceList/" + this.currentPage + "/" + this.pageSize;
      if (!!this.selectNodeIds) {
        this.condition.cloudNativeId = this.selectNodeIds[0];
      } else {
        this.condition.cloudNativeId = null;
      }
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
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
      if (rowIndex % 4 === 0) {
        return 'success-row';
      } else if (rowIndex % 2 === 0) {
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
.table-card {
  min-height: 10%;
}
.hr-card-index-1 .hr-card-data-digital {
  color: #0051a4;
}

.hr-card-index-1 {
  border-left-color: #0051a4;
  border-left-width: 3px;
}

.hr-card-index-2 .hr-card-data-digital {
  color: #65A2FF;
}

.hr-card-index-2 {
  border-left-color: #65A2FF;
  border-left-width: 3px;
}

.hr-card-index-3 .hr-card-data-digital {
  color: #E69147;
}

.hr-card-index-3 {
  border-left-color: #E69147;
  border-left-width: 3px;
}

.hr-card-index-4 .hr-card-data-digital {
  color: #E6113C;
}

.hr-card-index-4 {
  border-left-color: #E6113C;
  border-left-width: 3px;
}

.hr-card-index-5 .hr-card-data-digital {
  color: #44B349;
}

.hr-card-index-5 {
  border-left-color: #44B349;
  border-left-width: 3px;
}

.hr-card-data {
  text-align: left;
  display: block;
  margin-bottom: 5px;
}

.hr-card-desc {
  display: block;
  text-align: left;
}

.hr-card-data-digital {
  font-size: 21px;
}

.hr-card-data-unit {
  color: #8492a6;
  font-size: 10px;
}

.margin-top {
  padding: 15px 25px 0 25px;
}

.content >>> .my-label {
  font-size: 14px;
  background: #ebf5ff;
  text-align: center;
}

.content >>> .my-content {
  color: red;
  font-size: 10px;
  text-align: center;
}

</style>
