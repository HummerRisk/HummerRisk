<template>
  <main-container class="main-container" v-loading="result.loading">
    <el-row :gutter="20">
      <el-col :span="18" style="padding-right: 0">
        <entry-info/>
      </el-col>
      <el-col :span="6" style="padding-left: 0">
        <user-info/>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="18" style="padding-right: 0">
        <assets-info/>
      </el-col>
      <el-col :span="6" style="padding-left: 0">
        <score-info/>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <bottom-info/>
    </el-row>
  </main-container>
</template>

<script>
import MainContainer from "@/business/components/common/components/MainContainer";
import EntryInfo from "@/business/components/dashboard/home/EntryInfo";
import AssetsInfo from "@/business/components/dashboard/home/AssetsInfo";
import ScoreInfo from "@/business/components/dashboard/home/ScoreInfo";
import BottomInfo from "@/business/components/dashboard/home/BottomInfo";
import UserInfo from "@/business/components/dashboard/home/UserInfo";

/* eslint-disable */
export default {
  components: {
    UserInfo,
    MainContainer,
    EntryInfo,
    AssetsInfo,
    ScoreInfo,
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

