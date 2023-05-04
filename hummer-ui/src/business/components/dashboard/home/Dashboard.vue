<template>
  <main-container class="main-container" v-loading="result.loading">
    <el-row :gutter="20">
      <el-col :span="18" style="padding-right: 0">
        <top-info/>
      </el-col>
      <el-col :span="6" style="padding-left: 0">
        <top-info-r/>
      </el-col>
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
import MainContainer from "@/business/components/common/components/MainContainer";
import TopInfo from "@/business/components/dashboard/home/TopInfo";
import LeftInfo from "@/business/components/dashboard/home/LeftInfo";
import RightInfo from "@/business/components/dashboard/home/RightInfo";
import BottomInfo from "@/business/components/dashboard/home/BottomInfo";
import TopInfoR from "@/business/components/dashboard/home/TopInfoR.vue";

/* eslint-disable */
export default {
  components: {
    TopInfoR,
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

