<template>
  <main-container class="main-container" v-loading="result.loading">
    <el-row :gutter="20">
      <top-info/>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="18" style="padding-right: 0">
        <left-info/>
      </el-col>
      <el-col :span="6" style="padding-left: 0">
        <right-info/>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <bottom-info/>
    </el-row>
  </main-container>
</template>

<script>
import MainContainer from "../../common/components/MainContainer";
import TopInfo from "./TopInfo";
import LeftInfo from "./LeftInfo";
import RightInfo from "./RightInfo";
import BottomInfo from "@/business/components/dashboard/home/BottomInfo";

/* eslint-disable */
export default {
  components: {
    MainContainer,
    TopInfo,
    LeftInfo,
    RightInfo,
    BottomInfo,
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

