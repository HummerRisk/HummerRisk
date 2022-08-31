<template>
  <div v-loading="result.loading">
    <container class="container">
      <el-col :span="24">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.k8s_statistics') }}</span>
          </template>
          <cloud-native-chart/>
        </el-card>
      </el-col>
    </container>
    <container class="container" >
      <el-col :span="24">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.image_statistics') }}</span>
          </template>
          <image-chart/>
        </el-card>
      </el-col>
    </container>
    <container class="container">
      <el-col :span="24">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.code_statistics') }}</span>
          </template>
          <code-chart/>
        </el-card>
      </el-col>
    </container>
  </div>
</template>

<script>
import ImageChart from "@/business/components/dashboard/head/ImageChart";
import CodeChart from "@/business/components/dashboard/head/CodeChart";
import CloudNativeChart from "@/business/components/dashboard/head/CloudNativeChart";
import Container from "../.././common/components/Container";

/* eslint-disable */
export default {
  components: {
    ImageChart,
    CodeChart,
    CloudNativeChart,
    Container,
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

.hr-card-index-1 .hr-card-data-digital {
  color: #0051a4;
}

.hr-card-index-1 {
  border-left-color: #0051a4;
  border-left-width: 3px;
}

.hr-card-index-2 .hr-card-data-digital {
  color: #65A2FF;
}

.hr-card-index-2 {
  border-left-color: #65A2FF;
  border-left-width: 3px;
}

.hr-card-index-3 .hr-card-data-digital {
  color: #E69147;
}

.hr-card-index-3 {
  border-left-color: #E69147;
  border-left-width: 3px;
}

.hr-card-index-4 .hr-card-data-digital {
  color: #E6113C;
}

.hr-card-index-4 {
  border-left-color: #E6113C;
  border-left-width: 3px;
}

.hr-card-index-5 .hr-card-data-digital {
  color: #44B349;
}

.hr-card-index-5 {
  border-left-color: #44B349;
  border-left-width: 3px;
}

.hr-card-index-6 .hr-card-data-digital {
  color: #893fdc;
}

.hr-card-index-6 {
  border-left-color: #893fdc;
  border-left-width: 3px;
}

.hr-card-data {
  text-align: left;
  display: block;
  margin-bottom: 5px;
}

.hr-card-desc {
  display: block;
  text-align: left;
}

.hr-card-data-digital {
  font-size: 21px;
}

.hr-card-data-unit {
  color: #8492a6;
  font-size: 10px;
}

.container {
  padding: 3px 15px;
}
</style>

