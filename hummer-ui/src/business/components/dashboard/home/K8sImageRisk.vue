<template>
  <container v-loading="result.loading" class="container">
    <el-col :span="16" style="padding-right: 10px;">
      <el-card shadow="hover" class="table-card">
        <el-tabs v-model="activeName" type="card" class="active-card">
          <el-tab-pane :label="$t('k8s.vuln_compliance')" name="first">
            <el-col :span="12">
              <k8s-vuln-chart/>
            </el-col>
            <el-col :span="12">
              <k8s-vuln-bar-chart/>
            </el-col>
          </el-tab-pane>
          <el-tab-pane :label="$t('k8s.config_compliance')" name="second">
            <el-col :span="12">
              <k8s-config-chart/>
            </el-col>
            <el-col :span="12">
              <k8s-config-bar-chart/>
            </el-col>
          </el-tab-pane>
          <el-tab-pane :label="$t('k8s.kubench_compliance')" name="third">
            <el-col :span="12">
              <k8s-kubench-chart/>
            </el-col>
            <el-col :span="12">
              <k8s-kubench-bar-chart/>
            </el-col>
          </el-tab-pane>
          <el-tab-pane :label="$t('dashboard.k8s_compliance_scan_statistics')" name="fourth">
            <el-col :span="12">
              <k8s-scan-chart/>
            </el-col>
            <el-col :span="12">
              <k8s-scan-bar-chart/>
            </el-col>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </el-col>
    <el-col :span="8" style="padding-left: 0;">
      <el-card shadow="hover" class="table-card">
        <template v-slot:header>
          <span class="title">{{ $t('dashboard.image_risk') }}</span>
        </template>
        <image-risk-chart/>
      </el-card>
    </el-col>
  </container>
</template>

<script>
import K8sVulnChart from "@/business/components/dashboard/head/K8sVulnChart";
import K8sVulnBarChart from "@/business/components/dashboard/head/K8sVulnBarChart";
import K8sConfigChart from "@/business/components/dashboard/head/K8sConfigChart";
import K8sConfigBarChart from "@/business/components/dashboard/head/K8sConfigBarChart";
import K8sKubenchChart from "@/business/components/dashboard/head/K8sKubenchChart";
import K8sKubenchBarChart from "@/business/components/dashboard/head/K8sKubenchBarChart";
import K8sScanChart from "@/business/components/dashboard/head/K8sScanChart";
import K8sScanBarChart from "@/business/components/dashboard/head/K8sScanBarChart";
import Container from "@/business/components/common/components/Container";
import ImageRiskChart from "@/business/components/dashboard/head/ImageRiskChart";

/* eslint-disable */
export default {
  components: {
    K8sVulnChart,
    K8sVulnBarChart,
    K8sConfigChart,
    K8sConfigBarChart,
    K8sKubenchChart,
    K8sKubenchBarChart,
    K8sScanChart,
    K8sScanBarChart,
    ImageRiskChart,
    Container,
  },
  data() {
    return {
      result: {},
      activeName: 'first'
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
  padding: 3px 15px 3px 15px;
}

.active-card {
  margin-top: 5px;
}
</style>

