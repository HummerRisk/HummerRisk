<template>
  <main-container v-loading="result.loading" class="container">
    <el-row :gutter="20">
      <el-col :span="18">
        <el-row :gutter="20">
          <el-card class="table-card" shadow="always">
            <template v-slot:header>
              <span class="title">{{ $t('dashboard.scan_analysis') }}</span>
            </template>
            <analysis-top-chart/>
          </el-card>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="6">
            <aside-container>
              <h1>{{ $t('commons.setting') }}</h1>
            </aside-container>
          </el-col>
          <el-col :span="18">
            <main-container>
              <h1>{{ $t('commons.setting') }}</h1>
            </main-container>
          </el-col>
        </el-row>
      </el-col>
      <el-col :span="6" style="padding: 10px 0 10px 0;">
        <analysis-right-container/>
      </el-col>
    </el-row>
  </main-container>
</template>

<script>
import AnalysisTopChart from "@/business/components/dashboard/head/AnalysisTopChart";
import AsideContainer from "@/business/components/common/components/AsideContainer";
import AnalysisRightContainer from "@/business/components/dashboard/home/AnalysisRightContainer";
import MainContainer from "@/business/components/common/components/MainContainer";

/* eslint-disable */
export default {
  components: {
    AnalysisTopChart,
    AsideContainer,
    MainContainer,
    AnalysisRightContainer,
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
.container {
  padding: 3px 5px 3px 15px;
}
.table-card {
  margin: 10px;
  min-height: 10%;
}
.title {

}
</style>

