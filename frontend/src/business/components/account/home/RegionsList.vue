<template>
  <el-card class="table-card" v-loading="result.loading">
    <template v-slot:header>
      <el-row>
        <el-col :span="20">
          <span class="title">{{ $t('account.regions') }}</span>
        </el-col>
        <el-col :span="4">
          <span class="title-unfold" @click="expand" style="max-height: 20px;">
            {{ $t('dashboard.expand_all') }}
            <i class="el-icon-full-screen"></i>
          </span>
        </el-col>
      </el-row>
    </template>
    <regions-pie-chart ref="regions"/>

    <el-dialog
      title=""
      :visible.sync="dialogVisible"
      width="80%"
      :before-close="handleClose">
      <regions-list-expand/>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{ $t('commons.cancel') }}</el-button>
      </span>
    </el-dialog>
  </el-card>

</template>

<script>
import RegionsPieChart from "@/business/components/common/chart/RegionsPieChart";
import RegionsListExpand from "./RegionsListExpand";
/* eslint-disable */
  export default {
    name: "RegionsList",
    components: {
      RegionsPieChart,
      RegionsListExpand,
    },
    data() {
      return {
        result: {},
        data: this.$t('rule.all'),
        accountId: null,
        dialogVisible: false,
      }
    },

    methods: {
      handleCommand(command) {
        this.accountId = command.id;
        this.data = command.name;
        this.$refs.regions.focus(this.accountId);
      },
      expand() {
        this.dialogVisible = true;
      },
      handleClose() {
        this.dialogVisible = false;
      }
    },
    created() {
    },
    activated() {
    }
  }
</script>

<style scoped>
.table-card {
  margin-bottom: 2%;
  min-height: 46%;
}
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}
.el-icon-arrow-down {
  font-size: 12px;
}
.title-account{
  color: #e43235;
}
.title-unfold{
  color: #409EFF;
  cursor:pointer;
}
</style>
