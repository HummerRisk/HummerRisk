<template>
  <container class="container" v-loading="result.loading">
    <el-col :span="4">
      <el-card shadow="always" class="hr-card-index-1">
        <span class="hr-card-data">
          <span class="hr-card-data-digital">{{ topInfo.codes }}</span>
          <span class="hr-card-data-unit"> {{ 'Codes' }}</span>
        </span>
        <span class="hr-card-desc">{{ $t('code.code_project') }}</span>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="always" class="hr-card-index-2">
        <span class="hr-card-data">
          <span class="hr-card-data-digital">{{ topInfo.critical }}</span>
          <span class="hr-card-data-unit"> {{ 'Critical' }}</span>
        </span>
        <span class="hr-card-desc">{{ $t('code.overview_critical') }}</span>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="always" class="hr-card-index-3">
        <span class="hr-card-data">
          <span class="hr-card-data-digital">{{ topInfo.high }}</span>
          <span class="hr-card-data-unit"> {{ 'High' }}</span>
        </span>
        <span class="hr-card-desc">{{ $t('code.overview_high') }}</span>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="always" class="hr-card-index-4">
        <span class="hr-card-data">
          <span class="hr-card-data-digital">{{ topInfo.medium }}</span>
          <span class="hr-card-data-unit"> {{ 'Medium' }}</span>
        </span>
        <span class="hr-card-desc">{{ $t('code.overview_medium') }}</span>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="always" class="hr-card-index-5">
        <span class="hr-card-data">
          <span class="hr-card-data-digital">{{ topInfo.low }}</span>
          <span class="hr-card-data-unit"> {{ 'Low' }}</span>
        </span>
        <span class="hr-card-desc">{{ $t('code.overview_low') }}</span>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="always" class="hr-card-index-6">
        <span class="hr-card-data">
          <span class="hr-card-data-digital">{{ topInfo.unknown }}</span>
          <span class="hr-card-data-unit"> {{ 'Unknown' }}</span>
        </span>
        <span class="hr-card-desc">{{ $t('code.overview_unknown') }}</span>
      </el-card>
    </el-col>
  </container>
</template>

<script>
import Container from "../.././common/components/Container";

/* eslint-disable */
export default {
  components: {
    Container,
  },
  data() {
    return {
      result: {},
      topInfo: {},
    }
  },
  methods: {
    init() {
      this.result = this.$post("/code/topInfo", {}, response => {
        let data = response.data;
        this.topInfo = data;
      });
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
  color: #893fdc;
}

.hr-card-index-1 {
  border-left-color: #893fdc;
  border-left-width: 3px;
}

.hr-card-index-2 .hr-card-data-digital {
  color: #8B0000;
}

.hr-card-index-2 {
  border-left-color: #8B0000;
  border-left-width: 3px;
}

.hr-card-index-3 .hr-card-data-digital {
  color: #FF4D4D;
}

.hr-card-index-3 {
  border-left-color: #FF4D4D;
  border-left-width: 3px;
}

.hr-card-index-4 .hr-card-data-digital {
  color: #FF8000;
}

.hr-card-index-4 {
  border-left-color: #FF8000;
  border-left-width: 3px;
}

.hr-card-index-5 .hr-card-data-digital {
  color: #336D9F;
}

.hr-card-index-5 {
  border-left-color: #336D9F;
  border-left-width: 3px;
}

.hr-card-index-6 .hr-card-data-digital {
  color: #67C23A;
}

.hr-card-index-6 {
  border-left-color: #67C23A;
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

