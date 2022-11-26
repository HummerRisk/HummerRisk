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
                      type="primary" :content="createTip" @click="create"/>
        <table-button v-if="showScan" icon="el-icon-video-play"
                              type="success" :content="scanTip" @click="scan"/>
        <table-button v-if="showValidate" icon="el-icon-video-play"
                         type="warning" :content="validateTip" @click="validate"/>
        <table-button v-if="showDelete" icon="el-icon-remove-outline"
                      type="danger" :content="deleteTip" @click="deleteSelect"/>
        <table-search-bar :condition.sync="condition" @change="search" class="search-bar" :tip="tip" :items="items"/>
        <slot name="button"></slot>
      </span>
      <span>
        <table-adv-search-bar v-if="isCombine" :condition.sync="condition" :showOpen="showOpen" @search="search"
                              :columnNames="columnNames" :checkedColumnNames="checkedColumnNames" :checkAll="checkAll" :isIndeterminate="isIndeterminate"
                              @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange" @handleCheckAllChange="handleCheckAllChange"/>
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
      showValidate: {
        type: Boolean,
        default: false
      },
      showDelete: {
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
      validateTip: {
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
      deleteTip: {
        type: String,
        default() {
          return this.$t('commons.delete');
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
      items: {
        type: [Object,Array],
        default: () => [
          {'id' : 'name', 'name' : 'commons.name'},
        ],
      },
      columnNames: {
        type: [Object,Array],
      },
      checkedColumnNames: {
        type: [Object,Array],
      },
      checkAll: true,
      isIndeterminate: false,
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
        this.$emit('validate');
      },
      deleteSelect() {
        this.$emit('deleteSelect');
      },
      handleCheckAllChange(val) {
        console.log(111)
        this.$emit('handleCheckAllChange', val);
      },
      handleCheckedColumnNamesChange(value) {
        console.log(222)
        this.$emit('handleCheckedColumnNamesChange', value);
      },
    },
    computed: {
      isCombine() {
        return this.condition.components !== undefined && this.condition.components.length > 0;
      }
    },
    created() {
      console.log(this.columnNames)
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
