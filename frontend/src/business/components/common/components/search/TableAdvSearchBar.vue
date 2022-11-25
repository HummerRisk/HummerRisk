<template>
  <span class="adv-search-bar">
    <el-button icon="el-icon-refresh" size="small" @click="refresh">{{ $t('commons.refresh') }}</el-button>
    <el-button icon="el-icon-download" size="small" @click="download">{{ $t('server.download') }}</el-button>
    <el-button icon="el-icon-setting" size="small" @click="list">{{ $t('commons.list') }}</el-button>
  </span>
</template>

<script>
/* eslint-disable */
  export default {
    name: "TableAdvSearchBar",
    props: {
      condition: Object,
    },
    data() {
      return {
      }
    },
    methods: {
      refresh() {
        this.$emit('search');
      },
      list() {

      },
      download() {

      },
    }
  }
</script>

<style>
  @media only screen and (min-width: 1870px) {
    .el-dialog.adv-dialog {
      width: 70%;
    }
  }

  @media only screen and (min-width: 1650px) and (max-width: 1869px) {
    .el-dialog.adv-dialog {
      width: 80%;
    }
  }

  @media only screen and (min-width: 1470px) and (max-width: 1649px) {
    .el-dialog.adv-dialog {
      width: 90%;
    }
  }

  @media only screen and (max-width: 1469px) {
    .el-dialog.adv-dialog {
      width: 70%;
      min-width: 695px;
    }
  }
</style>

<style scoped>
</style>
