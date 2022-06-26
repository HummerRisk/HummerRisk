<template>
  <main-container v-loading="result.loading">
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
  </main-container>
</template>

<script>
import MainContainer from "../../common/components/MainContainer";
import TopInfo from "./TopInfo";
import LeftInfo from "./LeftInfo";
import RightInfo from "./RightInfo";

/* eslint-disable */
export default {
  components: {
    MainContainer,
    TopInfo,
    LeftInfo,
    RightInfo,
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

</style>

