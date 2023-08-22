<template>
  <container v-loading="result.loading" class="container">
    <el-col :span="8" style="padding-right: 0;">
      <el-card shadow="hover" class="table-card">
        <template v-slot:header>
          <span class="title">{{ $t('dashboard.non_compliant_assets') }}</span>
        </template>
        <cloud-resource-chart/>
      </el-card>
    </el-col>
    <el-col :span="8">
      <el-card shadow="hover" class="table-card">
        <template v-slot:header>
          <span class="title">{{ $t('dashboard.non_compliant_risk') }}</span>
        </template>
        <cloud-risk-chart/>
      </el-card>
    </el-col>
    <el-col :span="8" style="padding-left: 0;">
      <el-card shadow="hover" class="table-card">
        <template v-slot:header>
          <span class="title">{{ $t('rule.rule') + $t('resource.i18n_not_compliance') }}</span>
        </template>
        <rule-risk-chart/>
      </el-card>
    </el-col>
  </container>
</template>

<script>
import CloudResourceChart from "@/business/components/dashboard/head/CloudResourceChart";
import CloudRiskChart from "@/business/components/dashboard/head/CloudRiskChart";
import Container from "@/business/components/common/components/Container";
import RuleRiskChart from "@/business/components/dashboard/head/RuleRiskChart.vue";

/* eslint-disable */
export default {
  components: {
    CloudResourceChart,
    RuleRiskChart,
    Container,
    CloudRiskChart,
  },
  data() {
    return {
      result: {},
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
  min-height: 10%;
}

.container {
  padding: 0 15px 3px 15px;
}

</style>

