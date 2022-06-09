<template>
  <el-card class="table-card" v-loading="result.loading">
    <template v-slot:header>
      <el-row>
        <el-col :span="20">
          <span class="title">{{ $t('account.cloud_account') }}</span>
        </el-col>
        <el-col :span="4">
          <span class="title-unfold" @click="expand" style="max-height: 20px;">
            {{ $t('dashboard.expand_all') }}
            <i class="el-icon-full-screen"></i>
          </span>
        </el-col>
      </el-row>
    </template>
    <account-pie-chart/>

    <el-dialog
      title=""
      :visible.sync="dialogVisible"
      width="80%"
      :before-close="handleClose">
      <account-expand-chart/>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{ $t('commons.cancel') }}</el-button>
      </span>
    </el-dialog>
  </el-card>
</template>

<script>

import AccountPieChart from "@/business/components/common/chart/AccountPieChart";
import AccountExpandChart from "@/business/components/common/chart/AccountExpandChart";
/* eslint-disable */
  export default {
    name: "AccountList",
    components: {
      AccountPieChart,
      AccountExpandChart,
    },
    data() {
      return {
        result: {},
        dialogVisible: false,
      }
    },

    methods: {
      expand() {
        this.dialogVisible = true;
      },
      handleClose() {
        this.dialogVisible = false;
      }
    },

    created() {
    },
  }
</script>

<style scoped>
.table-card {
  margin-bottom: 2%;
  min-height: 26%;
}
.title-unfold{
  color: #409EFF;
  cursor:pointer;
}
</style>
