<template>

  <div>
    <el-row type="flex" justify="space-between" align="middle">
      <span>
        <account-change class="account-change" :accountName="currentAccount" @cloudAccountSwitch="cloudAccountSwitch"
                        @selectAccount="selectAccount" @openDownload="openDownload"/>
      </span>
      <span>
        <table-search-bar :condition.sync="condition" @change="search" class="search-bar" :tip="tip"/>
        <table-adv-search-bar :condition.sync="condition" :showOpen="showOpen" @search="search" v-if="isCombine"/>
      </span>
    </el-row>
  </div>

</template>

<script>
import TableSearchBar from '@/business/components/common/components/TableSearchBar';
import TableButton from '@/business/components/common/components/TableButton';
import TableAdvSearchBar from "@/business/components/common/components/search/TableAdvSearchBar";
import AccountChange from "@/business/components/oss/head/AccountSwitch";
import {ACCOUNT_NAME} from "@/common/js/constants";

/* eslint-disable */
  export default {
    name: "TableHeader",
    components: {
      TableAdvSearchBar,
      TableSearchBar,
      TableButton,
      AccountChange
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
      cloudAccountSwitch(accountId) {
        this.$emit('cloudAccountSwitch', accountId);
      },
      openDownload() {
        this.$emit('openDownload');
      },
      selectAccount(accountId, accountName) {
        this.$emit('selectAccount', accountId, accountName);
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
