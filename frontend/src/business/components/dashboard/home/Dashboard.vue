<template>
    <main-container v-loading="result.loading">
      <el-row :gutter="20">
        <el-col :span="12">
          <compliance></compliance>
        </el-col>
        <el-col :span="12">
          <rule-list></rule-list>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <account-list></account-list>
        </el-col>
        <el-col :span="12">
          <regions-list></regions-list>
        </el-col>
      </el-row>
    </main-container>
</template>
<script>
import MainContainer from "../../common/components/MainContainer";
import AccountList from "./AccountList";
import RegionsList from "./RegionsList";
import RuleList from "./RuleList";
import Compliance from "./Compliance";
/* eslint-disable */
  export default {
    name: "dashboard",
    components: {
      MainContainer,
      AccountList,
      RegionsList,
      RuleList,
      Compliance,
    },
    data() {
      return {
        values: [],
        result: {},
        data: [],
      }
    },
    methods: {
    },
    created() {
    },
  }

</script>

<style scoped>
</style>
