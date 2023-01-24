<template>
  <el-card class="table-card" v-loading="result.loading">
    <template v-slot:header>
      <el-row>
        <el-col :span="20">
          <span class="title">{{ $t('vuln.regions_statistics') }}</span>
        </el-col>
      </el-row>
    </template>
    <regions-expand-chart ref="regions"/>
  </el-card>
</template>

<script>
import RegionsExpandChart from "../head/RegionsExpandChart";
/* eslint-disable */
  export default {
    name: "RegionsListExpand",
    components: {
      RegionsExpandChart
    },
    data() {
      return {
        result: {},
        accounts: [],
        data: this.$t('rule.all'),
        accountId: null,
      }
    },

    methods: {
      list() {
        let url = "/account/allList";
        this.result = this.$get(url, response => {
          if (response.data != undefined && response.data != null) {
            this.accounts = response.data;
          }
        });
      },
      handleCommand(command) {
        this.accountId = command.id;
        this.data = command.name;
        this.$refs.regions.focus(this.accountId);
      },
    },

    created() {
      this.list();
    },
    activated() {
    }
  }
</script>

<style scoped>
.table-card {
  margin-bottom: 2%;
  min-height: 26%;
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
  color: #16cfae;
}
</style>
