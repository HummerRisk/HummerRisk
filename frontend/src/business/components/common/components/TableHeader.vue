<template>

  <div>
    <el-row class="table-title" type="flex" justify="space-between" align="middle" v-if="showName">
      <slot name="title">
        {{ title }}
      </slot>
    </el-row>
    <el-row type="flex" justify="space-between" align="middle">
      <span class="operate-button">
        <table-button v-if="showCreate" icon="el-icon-circle-plus-outline"
                         :content="createTip" @click="create"/>
        <table-button v-if="showScan" icon="el-icon-video-play"
                              type="success" :content="scanTip" @click="scan"/>
        <table-button v-if="showRun" icon="el-icon-video-play"
                         type="primary" :content="runTip" @click="validate"/>

        <slot name="button"></slot>
      </span>
      <span>
        <table-search-bar :condition.sync="condition" @change="search" class="search-bar" :tip="tip"/>
        <table-adv-search-bar :condition.sync="condition" :showOpen="showOpen" @search="search" v-if="isCombine"/>
      </span>
    </el-row>
  </div>

</template>

<script>
import TableSearchBar from './TableSearchBar';
import TableButton from './TableButton';
import TableAdvSearchBar from "./search/TableAdvSearchBar";
/* eslint-disable */
  export default {
    name: "TableHeader",
    components: {TableAdvSearchBar, TableSearchBar, TableButton},
    props: {
      title: {
        type: String,
        default() {
          return this.$t('commons.name');
        }
      },
      showCreate: {
        type: Boolean,
        default: false
      },
      showScan: {
        type: Boolean,
        default: false
      },
      showRun: {
        type: Boolean,
        default: false
      },
      condition: {
        type: Object
      },
      createTip: {
        type: String,
        default() {
          return this.$t('commons.create');
        }
      },
      scanTip: {
        type: String,
        default() {
          return this.$t('account.scan');
        }
      },
      runTip: {
        type: String,
        default() {
          return this.$t('account.validate');
        }
      },
      tip: {
        String,
        default() {
          return this.$t('commons.search_by_name');
        }
      },
      showOpen: {
        type: Boolean,
        default: true
      },
      showName: {
        type: Boolean,
        default: true
      },
    },
    methods: {
      search(value) {
        this.$emit('update:condition', this.condition);
        this.$emit('search', value);
      },
      create() {
        this.$emit('create');
      },
      scan() {
        this.$emit('scan');
      },
      validate() {
        this.$emit('validate')
      },
    },
    computed: {
      isCombine() {
        return this.condition.components !== undefined && this.condition.components.length > 0;
      }
    }
  }
</script>

<style>

  .table-title {
    height: 40px;
    font-weight: bold;
    font-size: 18px;
  }

</style>

<style scoped>

  .operate-button {
    margin-bottom: -5px;
  }

  .search-bar {
    width: 200px
  }

</style>
