<template>
  <el-row :gutter="24">
    <el-col :span="24">
      <history-list :select-node-ids="selectNodeIds"/>
    </el-col>
  </el-row>
</template>

<script>
import HistoryList from "./HistoryList";
/* eslint-disable */
  export default {
    name: "ScenarioHisList",
    components: {
      HistoryList
    },
    props: {
      selectNodeIds: Array,
    },
    data() {
      return {
      }
    },
    created() {
    },
    watch: {
      selectNodeIds() {
      },
      batchReportId() {
      }
    },
    computed: {
    },
    methods: {
      select(selection) {
        this.selection = selection.map(s => s.id);
        this.$emit('selection', selection);
      },
      isSelect(row) {
        return this.selection.includes(row.id)
      },
    }
  }
</script>

<style scoped>
  /deep/ .el-drawer__header {
    margin-bottom: 0;
  }
</style>
