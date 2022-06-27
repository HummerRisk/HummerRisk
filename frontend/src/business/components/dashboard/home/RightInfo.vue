<template>
  <container v-loading="result.loading" class="container">
    <el-col :span="24" style="padding: 0;">
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <span class="title">{{ $t('dashboard.safe_score') }}</span>
        </template>
        <score-pie-chart/>
      </el-card>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <span class="title">{{ $t('dashboard.system_his') }}</span>
        </template>
        <el-table
          :data="tableData"
          stripe max-height="528"
          style="width: 100%">
          <el-table-column
            prop="resourceUserId"
            :label="$t('dashboard.resource_user_id')"
            min-width="25%">
          </el-table-column>
          <el-table-column
            prop="operation"
            :label="$t('commons.operating')"
            min-width="23%">
            <template v-slot:default="scope">
              {{ $t(scope.row.operation) }}
            </template>
          </el-table-column>
          <el-table-column
            prop="time"
            :label="$t('dashboard.time')"
            min-width="52%">
            <template v-slot:default="scope">
              <span>{{ scope.row.time | timestampFormatMinutesDate }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-col>
  </container>
</template>

<script>
import Container from "../.././common/components/Container";
import ScorePieChart from "@/business/components/common/chart/ScorePieChart";

/* eslint-disable */
export default {
  components: {
    Container,
    ScorePieChart,
  },
  data() {
    return {
      result: {},
      tableData: [],
      condition: {
      },
    }
  },
  methods: {
    init() {
      let url = "/log/operation/query/resource/1/10";
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.tableData = data.listObject;
      });
    },
  },
  created() {
    this.init();
  }
}
</script>

<style scoped>
.table-card {
  margin-bottom: 2%;
  min-height: 10%;
}
.container {
  padding: 3px 15px 3px 5px;
}
</style>

