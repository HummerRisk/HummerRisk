<template>
  <container v-loading="result.loading" style="padding-right: 5px !important;">
    <el-col :span="10" style="padding: 0 10px 0 0;">
      <el-row>
        <el-card class="table-card" v-loading="result.loading">
          <template v-slot:header>
            <span class="title">{{ $t('resource.i18n_not_compliance') }}</span>
          </template>
        </el-card>
      </el-row>
      <el-row>
        <el-card class="table-card" v-loading="result.loading">
          <template v-slot:header>
            <span class="title">{{ $t('resource.i18n_not_compliance') }}</span>
          </template>
        </el-card>
      </el-row>
      <el-row>
        <el-card class="table-card" v-loading="result.loading">
          <template v-slot:header>
            <span class="title">{{ $t('resource.i18n_not_compliance') }}</span>
          </template>
        </el-card>
      </el-row>
    </el-col>
    <el-col :span="14" style="padding: 0;">
      <el-card class="table-card" v-loading="result.loading">
        <el-calendar>
          <!-- 这里使用的是 2.5 slot 语法，对于新项目请使用 2.6 slot 语法-->
          <template
            slot="dateCell"
            slot-scope="{date, data}">
            <p :class="data.isSelected ? 'is-selected' : ''">
              {{ data.day.split('-').slice(1).join('-') }} {{ data.isSelected ? '✔️' : ''}}
            </p>
          </template>
        </el-calendar>
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
  margin-bottom: 2%;
  min-height: 10%;
}
.is-selected {
  color: #1989FA;
}
</style>

