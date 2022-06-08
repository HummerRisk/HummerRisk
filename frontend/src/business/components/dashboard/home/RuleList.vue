<template>
  <el-card class="table-card" v-loading="result.loading">
    <template v-slot:header>
      <el-row>
        <el-col :span="20">
          <span class="title">{{ $t('rule.rule') }}</span>
        </el-col>
        <el-col :span="4">
          <span class="title-unfold" @click="expand" style="max-height: 20px;">
            {{ $t('dashboard.expand_all') }}
            <i class="el-icon-full-screen"></i>
          </span>
        </el-col>
      </el-row>
    </template>
    <div style="text-align: center;">
      <h3 style="margin: 0;">{{ this.$t('dashboard.rule_detail_top') }}</h3>
      <h5 style="margin: 0;color: #ababab;">{{ this.$t('history.resource_result') }}</h5>
    </div>
    <rule-list-pie-chart/>

    <el-dialog
      title=""
      :visible.sync="dialogVisible"
      width="80%"
      :before-close="handleClose">
      <rule-list-expand-chart/>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{ $t('commons.cancel') }}</el-button>
      </span>
    </el-dialog>
  </el-card>
</template>

<script>
import RuleListPieChart from "@/business/components/common/chart/RuleListPieChart";
import RuleListExpandChart from "@/business/components/common/chart/RuleListExpandChart";

/* eslint-disable */
  export default {
    name: "RuleList",
    components: {
      RuleListPieChart,
      RuleListExpandChart,
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

    mounted() {
    },
  }
</script>

<style scoped>
.table-card {
  margin-bottom: 2%;
  min-height: 46%;
}
.title-unfold{
  color: #409EFF;
  cursor:pointer;
}
</style>
