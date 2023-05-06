<template>
  <div v-loading="result.loading">
    <el-col :span="8" style="padding-right: 0;">
      <container class="container1" >
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.non_compliant_assets') }}</span>
          </template>
          <cloud-resource-chart/>
        </el-card>
      </container>
    </el-col>
    <el-col :span="8">
      <container class="container2" >
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.non_compliant_risk') }}</span>
          </template>
          <cloud-risk-chart/>
        </el-card>
      </container>
    </el-col>
    <el-col :span="8" style="padding-left: 0;">
      <container class="container3">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.server_top10_risk') }}</span>
          </template>
          <server-risk-chart/>
        </el-card>
      </container>
    </el-col>
  </div>
</template>

<script>
import CloudResourceChart from "@/business/components/dashboard/head/CloudResourceChart";
import CloudRiskChart from "@/business/components/dashboard/head/CloudRiskChart";
import Container from "@/business/components/common/components/Container";
import ServerRiskChart from "@/business/components/dashboard/head/ServerRiskChart";

/* eslint-disable */
export default {
  components: {
    CloudResourceChart,
    ServerRiskChart,
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

.container1 {
  padding: 3px 0 3px 15px;
}

.container2 {
  padding: 3px 0;
}

.container3 {
  padding: 3px 15px 3px 0;
}
</style>

