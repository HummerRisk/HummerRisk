<template>
  <main-container class="main-container" v-loading="result.loading">
    <el-row :gutter="20">
      <top-info/>
    </el-row>
    <el-row :gutter="20">
      <bottom-info/>
    </el-row>
  </main-container>
</template>

<script>
import MainContainer from "../../common/components/MainContainer";
import TopInfo from "@/business/components/event/home/TopInfo";
import BottomInfo from "@/business/components/event/home/BottomInfo";

/* eslint-disable */
export default {
  components: {
    MainContainer,
    TopInfo,
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

