<template>

  <div>
    <el-row type="flex" justify="space-between" align="middle">
      <span>
        <account-change class="account-change" :accountName="currentAccount" @cloudAccountSwitch="cloudAccountSwitch"
                        @selectAccount="selectAccount" @openDownload="openDownload"/>
      </span>
      <span>
        <table-search-bar :condition.sync="condition" @change="search" @search="search" class="search-bar" :tip="tip" :items="items" ref="conditionSearch"/>
      </span>
    </el-row>
    <el-row v-show="tags && Object.keys(tags).length > 0" type="flex" justify="space-between" align="middle">
      <span>
        <I style="font-size: 12px;color: #888">{{ $t('commons.filter_condition') }} </I>
        <el-tag v-for="(value, key) in tags" :key="key" closable type="info" size="mini" class="el-tag-con" @close="handleClose(key)">
          {{ $t(value.label) }} : {{ value.valueArray }}
        </el-tag>
      </span>
    </el-row>
  </div>

</template>

<script>
import TableSearchBar from '@/business/components/report/head/TableSearchBar';
import TableButton from '@/business/components/common/components/TableButton';
import TableAdvSearchBar from "@/business/components/common/components/search/TableAdvSearchBar";
import AccountChange from "@/business/components/report/head/AccountSwitch";
import {ACCOUNT_NAME} from "@/common/js/constants";
import Vue from "vue";

/* eslint-disable */
  export default {
    name: "TableHeader",
    components: {
      TableAdvSearchBar,
      TableSearchBar,
      TableButton,
      AccountChange
    },
    data(){
      return {
        tags: [],
      }
    },
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
      currentAccount: {
        type: String,
        default: localStorage.getItem(ACCOUNT_NAME)
      },
      items: {
        type: [Object,Array],
        default: () => [
          {'id' : 'name', 'name' : 'commons.name'},
        ],
      },
    },
    methods: {
      search(value) {
        if (this.condition.combine) {
          this.tags = this.condition.combine;
        }
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
      cloudAccountSwitch(accountId) {
        this.$emit('cloudAccountSwitch', accountId);
      },
      openDownload() {
        this.$emit('openDownload');
      },
      selectAccount(accountId, accountName) {
        this.$emit('selectAccount', accountId, accountName);
      },
      handleClose(key) {
        Vue.delete(this.condition.combine, key);
        this.search(null);
        this.$refs.conditionSearch.conditionSearch(this.condition.combine);
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
  .search-bar {
    width: 200px
  }
</style>
