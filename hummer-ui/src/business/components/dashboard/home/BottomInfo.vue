<template>
  <div v-loading="result.loading">
    <container class="container" >
      <el-col :span="24">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.image_statistics') }}</span>
          </template>
          <image-chart/>
        </el-card>
      </el-col>
    </container>
    <container class="container">
      <el-col :span="24">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.config_statistics') }}</span>
          </template>
          <config-chart/>
        </el-card>
      </el-col>
    </container>
    <container class="container">
      <el-col :span="24">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.fs_statistics') }}</span>
          </template>
          <fs-chart/>
        </el-card>
      </el-col>
    </container>
  </div>
</template>

<script>
import ImageChart from "@/business/components/dashboard/head/ImageChart";
import ConfigChart from "@/business/components/dashboard/head/ConfigChart";
import FsChart from "@/business/components/fs/head/FsChart";
import Container from "../.././common/components/Container";

/* eslint-disable */
export default {
  components: {
    ImageChart,
    ConfigChart,
    Container,
    FsChart,
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

