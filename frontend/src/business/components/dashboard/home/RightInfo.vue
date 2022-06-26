<template>
  <container v-loading="result.loading" style="padding-left: 5px !important;">
    <el-col :span="24" style="padding: 0;">
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <span class="title">{{ $t('resource.i18n_not_compliance') }}</span>
        </template>
        <score-pie-chart/>
      </el-card>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <span class="title">{{ $t('resource.i18n_not_compliance') }}</span>
        </template>
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
    }
  },
  methods: {
  },
}
</script>

<style scoped>
.table-card {
  margin-bottom: 2%;
  min-height: 10%;
}
</style>

