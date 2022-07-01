<template>
  <div class="container-lin">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="always" class="hr-card-index-1">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.quick_search') }}</span>
          </template>
          <el-collapse v-model="activeName" accordion>
            <el-form ref="form" :model="searchForm" label-width="80px" size="mini">
              <el-collapse-item :title="$t('dashboard.types_1')" name="1">
                <el-radio-group v-model="searchForm.scanType" size="medium">
                  <div class="_group"><el-radio class="radio_group" border :label="$t('dashboard.cloud_scan')" name="cloud_scan"></el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="$t('dashboard.vuln_scan')" name="vuln_scan"></el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="$t('dashboard.server_scan')" name="server_scan"></el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="$t('dashboard.package_scan')" name="package_scan"></el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="$t('dashboard.image_scan')" name="image_scan"></el-radio></div>
                </el-radio-group>
              </el-collapse-item>
              <el-collapse-item :title="$t('dashboard.types_2')" name="2">
                <el-tree
                  :data="groupData"
                  :props="defaultProps"
                  accordion
                  @node-click="handleNodeClick">
                </el-tree>
              </el-collapse-item>
              <el-collapse-item :title="$t('dashboard.types_3')" name="3">
                <el-tree
                  :data="accountData"
                  :props="defaultProps"
                  accordion
                  @node-click="handleNodeClick">
                </el-tree>
              </el-collapse-item>
              <el-collapse-item :title="$t('dashboard.types_4')" name="4">
                <el-radio-group v-model="searchForm.severityType" size="medium">
                  <div class="_group"><el-radio class="radio_group" border :label="$t('rule.HighRisk')" name="HighRisk"></el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="$t('rule.MediumRisk')" name="MediumRisk"></el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="$t('rule.LowRisk')" name="LowRisk"></el-radio></div>
                </el-radio-group>
              </el-collapse-item>
              <el-collapse-item :title="$t('dashboard.types_5')" name="5">
                <div class="block">
                  <el-date-picker
                    v-model="searchForm.date"
                    size="mini"
                    type="daterange"
                    align="right"
                    class="date_picker"
                    unlink-panels
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    :picker-options="pickerOptions">
                  </el-date-picker>
                </div>
            </el-collapse-item>
            </el-form>
          </el-collapse>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card shadow="always" class="hr-card-index-1">
          <div>
            <el-table class="adjust-table table-content"
                      border max-height="710"
                      :data="tableData"
                      @sort-change="sort"
                      @filter-change="filter">
              <el-table-column type="index" min-width="2%"/>
              <el-table-column v-slot:default="scope" :label="$t('rule.rule_name')" min-width="20%" show-overflow-tooltip>
                {{ scope.row.taskName }}
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('account.creator')" min-width="6%" show-overflow-tooltip>
                {{ scope.row.applyUser }}
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('rule.severity')" min-width="8%" :sort-by="['HighRisk', 'MediumRisk', 'LowRisk']" prop="severity" :sortable="true"  show-overflow-tooltip>
                <span v-if="scope.row.severity == 'HighRisk'" style="color: #f84846;"> {{ $t('rule.HighRisk') }}</span>
                <span v-else-if="scope.row.severity == 'MediumRisk'" style="color: #fe9636;"> {{ $t('rule.MediumRisk') }}</span>
                <span v-else-if="scope.row.severity == 'LowRisk'" style="color: #4dabef;"> {{ $t('rule.LowRisk') }}</span>
                <span v-else> N/A</span>
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('resource.status')" min-width="10%" prop="status" sortable show-overflow-tooltip>
                <el-button @click="showTaskLog(scope.row)" plain size="medium" type="primary" v-if="scope.row.status === 'UNCHECKED'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                </el-button>
                <el-button @click="showTaskLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.status === 'APPROVED'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                </el-button>
                <el-button @click="showTaskLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.status === 'PROCESSING'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                </el-button>
                <el-button @click="showTaskLog(scope.row)" plain size="medium" type="success" v-else-if="scope.row.status === 'FINISHED'">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </el-button>
                <el-button @click="showTaskLog(scope.row)" plain size="medium" type="danger" v-else-if="scope.row.status === 'ERROR'">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </el-button>
                <el-button @click="showTaskLog(scope.row)" plain size="medium" type="warning" v-else-if="scope.row.status === 'WARNING'">
                  <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                </el-button>
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="6%">
                <el-tooltip class="item" effect="dark" :content="$t('history.resource_result')" placement="top">
                  <span v-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
                  <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum == 0)">
                  {{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}
                </span>
                  <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)">
                  <el-link type="primary" class="text-click" @click="goResource(scope.row)">{{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}</el-link>
                </span>
                </el-tooltip>
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('resource.status_on_off')" prop="returnSum" sortable show-overflow-tooltip min-width="8%">
                <span v-if="scope.row.returnSum == 0" style="color: #46ad59;">{{ $t('resource.i18n_compliance_true') }}</span>
                <span v-else-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)" style="color: #f84846;">{{ $t('resource.i18n_compliance_false') }}</span>
                <span v-else-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
              </el-table-column>
              <el-table-column prop="createTime" min-width="14%" :label="$t('account.update_time')" sortable show-overflow-tooltip>
                <template v-slot:default="scope">
                  <span><i class="el-icon-time"></i> {{ scope.row.createTime | timestampFormatDate }}</span>
                </template>
              </el-table-column>
            </el-table>

            <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {_filter, _sort} from "@/common/js/utils";
import TablePagination from "@/business/components/common/pagination/TablePagination";

/* eslint-disable */
export default {
  name: "AnalysisLeftInfo",
  components: {
    TablePagination,
  },
  props: {
    data: {},
  },
  data() {
    return {
      activeName: '1',
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      condition: {
      },
      searchForm: {},
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
      groupData: [{
        label: this.$t('dashboard.rule_tag'),
        children: []
      }, {
        label: this.$t('dashboard.rule_set'),
        children: []
      }, {
        label: this.$t('dashboard.rule_report'),
        children: []
      }],
      accountData: [{
        label: this.$t('dashboard.cloud_scan'),
        children: []
      }, {
        label: this.$t('dashboard.vuln_scan'),
        children: []
      }, {
        label: this.$t('dashboard.server_scan'),
        children: []
      }, {
        label: this.$t('dashboard.package_scan'),
        children: []
      }, {
        label: this.$t('dashboard.image_scan'),
        children: []
      }],
      defaultProps: {
        children: 'children',
        label: 'label'
      },

    }
  },
  methods: {
    sort(column) {
      _sort(column, this.condition);
      this.search();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.search();
    },
    search () {
      let url = "/cloud/task/manual/list/" + this.currentPage + "/" + this.pageSize;
      //在这里实现事件
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleNodeClick(data) {
      console.log(data);
    }
  },
  created() {
    this.search();
  },
}

</script>

<style scoped>
.container-lin {
  padding: 0;
}
.hr-card-index-1{
  border-left-color: #e8a97e;
  border-left-width: 3px;
  height: 787px;
}
.descriptions__title {
  font-size: 16px;
  font-weight: 700;
}
.block {
  margin: 10px 10px 0 30px;
}
.title {
  font-size: 16px;
  font-weight: 500;
  margin-top: 0;
  text-overflow: ellipsis;
  overflow: hidden;
  word-wrap: break-word;
  white-space: nowrap;
}
._group {
  margin: 10px 0 10px 20px;
  width: 200px;
}
.radio_group {
  width: 130px;
}
.block {
  width: 100%;
}
.date_picker {
  width: 80%;
}
</style>
