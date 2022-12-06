<template>
  <div v-loading="result.loading">
    <container class="container" >
      <el-col :span="12">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('image.by_image_repo') }}</span>
          </template>
          <left-chart/>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('commons.by_severity') }}</span>
          </template>
          <right-chart/>
        </el-card>
      </el-col>
    </container>
    <container class="container">
      <el-col :span="24">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('image.by_image') }}</span>
          </template>
          <image-chart/>
        </el-card>
      </el-col>
    </container>
  </div>
</template>

<script>
import Container from "../.././common/components/Container";
import LeftChart from "@/business/components/image/head/LeftChart";
import RightChart from "@/business/components/image/head/RightChart";
import ImageChart from "@/business/components/dashboard/head/ImageChart";

/* eslint-disable */
export default {
  components: {
    Container,
    LeftChart,
    RightChart,
    ImageChart,
  },
  data() {
    return {
      result: {},
    }
  },
  methods: {
    init() {
    },
  },
  created() {
    this.init();
  }
}
</script>

<style scoped>
.table-card {
  min-height: 10%;
}

.container {
  padding: 3px 15px;
}
</style>

