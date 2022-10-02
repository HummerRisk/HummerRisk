<template>
  <div v-loading="result.loading">
    <container class="container" >
      <el-col :span="12">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('server.server_risk') }}</span>
          </template>
          <left-chart/>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('server.severity') }}</span>
          </template>
          <right-chart/>
        </el-card>
      </el-col>
    </container>
    <container class="container">
      <el-col :span="24">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.server_statistics') }}</span>
          </template>
          <server-chart/>
        </el-card>
      </el-col>
    </container>
  </div>
</template>

<script>
import Container from "../.././common/components/Container";
import LeftChart from "@/business/components/server/head/LeftChart";
import RightChart from "@/business/components/server/head/RightChart";
import ServerChart from "@/business/components/server/head/ServerChart";
import Server from "@/business/components/server/base";

/* eslint-disable */
export default {
  components: {
    Server,
    Container,
    LeftChart,
    RightChart,
    ServerChart,
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

