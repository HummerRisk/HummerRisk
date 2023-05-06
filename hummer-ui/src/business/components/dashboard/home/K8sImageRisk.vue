<template>
  <div v-loading="result.loading">
    <el-col :span="16" style="padding-right: 6px;">
      <container class="container1" >
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.k8s_risk') }}</span>
          </template>
          <k8s-risk-chart/>
        </el-card>
      </container>
    </el-col>
    <el-col :span="8" style="padding-left: 0;">
      <container class="container3">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.image_risk') }}</span>
          </template>
          <image-risk-chart/>
        </el-card>
      </container>
    </el-col>
  </div>
</template>

<script>
import K8sRiskChart from "@/business/components/dashboard/head/K8sRiskChart";
import Container from "@/business/components/common/components/Container";
import ImageRiskChart from "@/business/components/dashboard/head/ImageRiskChart";

/* eslint-disable */
export default {
  components: {
    K8sRiskChart,
    ImageRiskChart,
    Container,
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
  padding: 3px 3px 3px 15px;
}

.container3 {
  padding: 3px 15px 3px 0;
}
</style>

