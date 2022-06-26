<template>
  <container v-loading="result.loading">
    <el-col :span="5">
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <span class="title">{{ $t('resource.i18n_not_compliance') }}</span>
        </template>
      </el-card>
    </el-col>
    <el-col :span="5">
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <span class="title">{{ $t('resource.i18n_not_compliance') }}</span>
        </template>
      </el-card>
    </el-col>
    <el-col :span="5">
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <span class="title">{{ $t('resource.i18n_not_compliance') }}</span>
        </template>
      </el-card>
    </el-col>
    <el-col :span="5">
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <span class="title">{{ $t('resource.i18n_not_compliance') }}</span>
        </template>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <span class="title">{{ $t('resource.i18n_not_compliance') }}</span>
        </template>
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
    }
  },
  methods: {
  },
}
</script>

<style scoped>
.table-card {
  min-height: 10%;
}
</style>

