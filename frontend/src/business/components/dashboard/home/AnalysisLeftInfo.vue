<template>
  <div class="container-lin">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="always" class="hr-card-index-1">
          <div class="block">
            <span class="descriptions__title">{{ $t('dashboard.quick_search') }}</span>
          </div>
          <el-divider></el-divider>
          <el-collapse v-model="activeName" accordion>
            <el-collapse-item title="一致性 Consistency" name="1">
              <div>与现实生活一致：与现实生活的流程、逻辑保持一致，遵循用户习惯的语言和概念；</div>
              <div>在界面中一致：所有的元素和结构需保持一致，比如：设计样式、图标和文本、元素的位置等。</div>
            </el-collapse-item>
            <el-collapse-item title="反馈 Feedback" name="2">
              <div>控制反馈：通过界面样式和交互动效让用户可以清晰的感知自己的操作；</div>
              <div>页面反馈：操作后，通过页面元素的变化清晰地展现当前状态。</div>
            </el-collapse-item>
            <el-collapse-item title="效率 Efficiency" name="3">
              <div>简化流程：设计简洁直观的操作流程；</div>
              <div>清晰明确：语言表达清晰且表意明确，让用户快速理解进而作出决策；</div>
              <div>帮助用户识别：界面简单直白，让用户快速识别而非回忆，减少用户记忆负担。</div>
            </el-collapse-item>
            <el-collapse-item title="可控 Controllability" name="4">
              <div>用户决策：根据场景可给予用户操作建议或安全提示，但不能代替用户进行决策；</div>
              <div>结果可控：用户可以自由的进行操作，包括撤销、回退和终止当前操作等。</div>
            </el-collapse-item>
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
    }
  },
  methods: {
    sort(column) {
      _sort(column, this.condition);
      this.init();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.init();
    },
    search () {
      let url = "/cloud/task/manual/list/" + this.currentPage + "/" + this.pageSize;
      //在这里实现事件
      this.result = this.$post(url, this.condition, response => {
        console.log(response)
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
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
</style>
