<template>

  <div>
    <el-row class="table-title" type="flex" justify="space-between" align="middle">
      <slot name="title">
        {{ title }}
      </slot>
    </el-row>
    <el-row type="flex" justify="space-between" align="middle">
      <account-change :project-name="currentAccount" @cloudAccountSwitch="cloudAccountSwitch"/>
      <el-select v-model="region" clearable placeholder="请选择区域" @change = "changeRegion">
        <el-option
          v-for="item in regions"
          :key="item['regionId']"
          :label="item['regionName']"
          :value="item['regionId']">
        </el-option>
      </el-select>

      <span>
         <el-date-picker
           v-if="showDate"
           @change="changeDateTime"
           v-model="dateTime2"
           type="datetimerange"
           value-format="yyyy-MM-dd HH:mm:ss"
           :picker-options="pickerOptions"
           range-separator="至"
           start-placeholder="开始日期"
           end-placeholder="结束日期"
           align="right">
    </el-date-picker>
      </span>
      <span class="operate-button">
        <table-button   icon="el-icon-video-play" v-if="showSync"
                        :content="syncTip" @click="syncData"/>
        <table-button   icon="el-icon-video-play" v-if="showSearch"
                        :content="searchTip" @click="search"/>
        <slot name="button"></slot>
      </span>
    </el-row>
  </div>

</template>

<script>
import TableSearchBar from '../../common/components/TableSearchBar';
import TableButton from '../../common/components/TableButton';
import TableAdvSearchBar from "../../common/components/search/TableAdvSearchBar";
import AccountChange from "@/business/components/common/head/AccountSwitch";
import {ACCOUNT, ACCOUNT_NAME} from "@/common/js/constants";
export default {
    name: "TableHeader",
    components: {TableAdvSearchBar, TableSearchBar, TableButton,AccountChange},
    props: {
      title: {
        type: String,
        default() {
          return this.$t('commons.name');
        }
      },
      showDate: {
        type: Boolean,
        default() {
          return true;
        }
      },
      showSync: {
        type: Boolean,
        default() {
          return true;
        }
      },
      showSearch: {
        type: Boolean,
        default() {
          return true;
        }
      },
      dateTime: {
        type: Array,
      },
      condition: {
        type: Object
      },
      syncTip: {
        type: String,
        default() {
          return this.$t('commons.sync');
        }
      },
      searchTip: {
        type: String,
      },
      currentAccount: {
        type: String,
      },
      tip: {
        String,
        default() {
          return this.$t('commons.search_by_name');
        }
      },
      initRegion:{
        String
      }
    },
    data(){
      return {
        dateTime2:'',
        regions: [],
        region: '',
        pickerOptions: {
          shortcuts: [{
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }]
        },
      }
    },
    created() {
      let account = localStorage.getItem(ACCOUNT)
      account = JSON.parse(account)
      this.regions = JSON.parse(account.regions)
      this.dateTime2 = this.dateTime
      if(this.initRegion){
        this.region = this.initRegion
      }
    },
    methods: {
      setDateTime(dateTime){
        this.dateTime2 = dateTime
      },
      setRegion(region){
        this.region = region
      },
      syncData() {
        this.$emit('syncData');
      },
      search(value) {
        this.$emit('update:condition', this.condition);
        this.$emit('search', value);
      },
      cloudAccountSwitch(value){
        let account = localStorage.getItem(ACCOUNT)
        account = JSON.parse(account)
        this.regions = JSON.parse(account.regions)
        this.$emit('cloudAccountSwitch',value)
      },
      changeRegion(){
        this.$emit('changeRegion', this.region);
      },
      changeDateTime(){
        this.$emit('changeDateTime', this.dateTime2);
      }
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
