<template>
  <container v-loading="result.loading" class="container">
    <el-col :span="24" style="padding: 0;">
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <span class="title">{{ $t('dashboard.safe_score') }}</span>
        </template>
        <score-pie-chart/>
      </el-card>
    </el-col>
  </container>
</template>

<script>
import Container from "../.././common/components/Container";
import ScorePieChart from "@/business/components/common/chart/ScorePieChart";
import {logOperationUrl} from "@/api/system/system";

/* eslint-disable */
export default {
  components: {
    Container,
    ScorePieChart,
  },
  data() {
    return {
      result: {},
      condition: {
      },
    }
  },
  methods: {
    init() {
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

