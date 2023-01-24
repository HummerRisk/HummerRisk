<template>
  <div v-loading="result.loading">
    <container class="container" >
      <el-col :span="12">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('code.by_code_project') }}</span>
          </template>
          <left-chart/>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('commons.by_severity') }}</span>
          </template>
          <right-chart/>
        </el-card>
      </el-col>
    </container>
    <container class="container">
      <el-col :span="24">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('code.by_code') }}</span>
          </template>
          <code-chart/>
        </el-card>
      </el-col>
    </container>
  </div>
</template>

<script>
import Container from "../.././common/components/Container";
import LeftChart from "@/business/components/code/head/LeftChart";
import RightChart from "@/business/components/code/head/RightChart";
import CodeChart from "@/business/components/dashboard/head/CodeChart";

/* eslint-disable */
export default {
  components: {
    Container,
    LeftChart,
    RightChart,
    CodeChart,
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

