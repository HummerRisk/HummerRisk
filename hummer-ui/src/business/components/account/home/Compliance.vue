<template>
  <el-card class="table-card" v-loading="result.loading">
    <template v-slot:header>
      <span class="title">{{ $t('resource.i18n_not_compliance') }}</span>
    </template>
    <md-card-content-overall/>
  </el-card>
</template>

<script>
import MdCardContentOverall from "./MdCardContentOverall";
/* eslint-disable */
  export default {
    name: "Compliance",
    components: {
      MdCardContentOverall,
    },
    data() {
      return {
        result: {},
        tableData: []
      }
    },

    methods: {
      search() {
      },
      link(row) {
      }
    },

    created() {
      this.search();
    },
    activated() {
      this.search();
    }
  }
</script>

<style scoped>
.table-card {
  margin-bottom: 2%;
  min-height: 46%;
}
</style>
