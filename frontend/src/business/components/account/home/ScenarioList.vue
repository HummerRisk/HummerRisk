<template>
  <el-row :gutter="24" class="el-row-se">
    <el-col :span="24">
      <card-content-rule :select-node-ids="selectNodeIds"/>
    </el-col>
  </el-row>
</template>

<script>
import CardContentRule from "./CardContentRule";
/* eslint-disable */
  export default {
    name: "ScenarioList",
    components: {
      CardContentRule
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
      edit(row) {
        this.$emit('edit', row);
      },
    }
  }
</script>

<style scoped>
  /deep/ .el-drawer__header {
    margin-bottom: 0;
  }
</style>
