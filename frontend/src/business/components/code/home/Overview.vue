<template>
  <main-container class="main-container" v-loading="result.loading">
    <el-row :gutter="20">
      <top-info/>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="18" style="padding-right: 0">
      </el-col>
      <el-col :span="6" style="padding-left: 0">
      </el-col>
    </el-row>
    <el-row :gutter="20">
    </el-row>
  </main-container>
</template>

<script>
import MainContainer from "../../common/components/MainContainer";
import TopInfo from "./TopInfo";

/* eslint-disable */
export default {
  components: {
    MainContainer,
    TopInfo,
  },
  data() {
    return {
      result: {},
    }
  },
  methods: {
  },
}
</script>

<style scoped>
.main-container {
  padding: 5px;
}
</style>

