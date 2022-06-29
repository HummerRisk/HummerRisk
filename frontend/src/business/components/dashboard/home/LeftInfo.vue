<template>
  <container v-loading="result.loading" class="container">
    <el-col :span="10" style="padding: 0 10px 0 0;">
      <el-row>
        <el-card class="table-card" v-loading="result.loading">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.cloud_account_statistics') }}</span>
          </template>
          <cloud-chart ref="cloudChart" :width="cloudChart"/>
        </el-card>
      </el-row>
      <el-row>
        <el-card class="table-card" v-loading="result.loading">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.vuln_statistics') }}</span>
          </template>
          <vuln-chart ref="vulnChart" :width="vulnChart"/>
        </el-card>
      </el-row>
      <el-row>
        <el-card class="table-card" v-loading="result.loading">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.server_statistics') }}</span>
          </template>
          <server-chart/>
        </el-card>
      </el-row>
    </el-col>
    <el-col :span="14" style="padding: 0;">
      <el-card class="table-card" v-loading="result.loading">
        <el-calendar>
          <!-- 这里使用的是 2.5 slot 语法，对于新项目请使用 2.6 slot 语法-->
          <template
            slot="dateCell"
            slot-scope="{date, data}">
            <p :class="data.isSelected ? 'is-selected' : ''" @click="handle(data.day)">
              {{ data.day.split('-').slice(1).join('-') }} {{ data.isSelected ? $t('dashboard.data_task') : ''}}
            </p>
          </template>
        </el-calendar>
      </el-card>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <span class="title">{{ $t('dashboard.package_statistics') }}</span>
        </template>
        <package-chart/>
      </el-card>
    </el-col>
  </container>
</template>

<script>
import Container from "../.././common/components/Container";
import CloudChart from "@/business/components/dashboard/head/CloudChart";
import VulnChart from "@/business/components/dashboard/head/VulnChart";
import PackageChart from "@/business/components/dashboard/head/PackageChart";
import ServerChart from "@/business/components/dashboard/head/ServerChart";

/* eslint-disable */
export default {
  components: {
    Container,
    CloudChart,
    VulnChart,
    PackageChart,
    ServerChart,
  },
  data() {
    return {
      result: {},
      cloudChart: 390,
      vulnChart: 390,
    }
  },
  methods: {
    /** 查询节假日管理 - 法定节假日列表 */
    getList() {
    },
    handle() {},
  },
  created() {
    this.getList();
    this.$nextTick(() => {
      this.cloudChart = this.$refs.cloudChart.$el.offsetWidth;
      this.vulnChart = this.$refs.vulnChart.$el.offsetWidth;
    });
  }
}
</script>

<style scoped>
.table-card {
  margin-bottom: 2%;
  min-height: 10%;
}
.is-selected {
  color: #1989FA;
}

.container {
  padding: 3px 5px 3px 15px;
}

.is-selected {
  color: white;
  background: green;
}
</style>

