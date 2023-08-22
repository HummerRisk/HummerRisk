<template>
  <div v-loading="result.loading">
    <container class="container" >
      <el-col :span="12">
        <el-card shadow="hover" class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.cloud_account_statistics') }}</span>
          </template>
          <left-chart/>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.regions_statistics') }}</span>
          </template>
          <right-chart/>
        </el-card>
      </el-col>
    </container>
    <container class="container">
      <el-col :span="24">
        <el-card shadow="hover" class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('rule.severity') }}</span>
          </template>
          <buttom-chart/>
        </el-card>
      </el-col>
    </container>
  </div>
</template>

<script>
import Container from "../.././common/components/Container";
import LeftChart from "@/business/components/event/head/LeftChart";
import RightChart from "@/business/components/event/head/RightChart";
import ButtomChart from "@/business/components/event/head/ButtomChart";

/* eslint-disable */
export default {
  components: {
    Container,
    LeftChart,
    RightChart,
    ButtomChart,
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
  padding: 3px 15px;
}
</style>

