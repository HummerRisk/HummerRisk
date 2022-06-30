<template>
  <main-container v-loading="result.loading" class="container">
    <el-row :gutter="20">
      <el-col :span="18">
        <el-row :gutter="20">
          <el-card class="table-card" shadow="always" >
            <template v-slot:header>
              <span class="title">{{ $t('dashboard.scan_analysis') }}</span>
            </template>
            <analysis-top-chart/>
          </el-card>
        </el-row>
        <analysis-left-info/>
      </el-col>
      <el-col :span="6" style="padding: 10px 0 10px 0;">
        <analysis-right-container/>
      </el-col>
    </el-row>
  </main-container>
</template>

<script>
import AnalysisTopChart from "@/business/components/dashboard/head/AnalysisTopChart";
import AnalysisLeftInfo from "@/business/components/dashboard/home/AnalysisLeftInfo";
import AnalysisRightContainer from "@/business/components/dashboard/home/AnalysisRightContainer";
import MainContainer from "@/business/components/common/components/MainContainer";

/* eslint-disable */
export default {
  components: {
    AnalysisTopChart,
    MainContainer,
    AnalysisLeftInfo,
    AnalysisRightContainer,
  },
  data() {
    return {
      result: {},
      tableData1: [{name: this.$t('dashboard.select_type')}],
    }
  },
  methods: {
  },
}
</script>

<style scoped>
.container {
  padding: 3px 5px 3px 15px;
}
.table-card {
  margin: 10px;
  min-height: 10%;
  border-left-color: #e8a97e;
  border-left-width: 3px;
}
.title {
}
</style>

