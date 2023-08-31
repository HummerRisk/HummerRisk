<template>

  <div>
    <el-row type="flex" justify="space-between" align="middle">
      <span>
        <project-switch class="account-change" :projectId="projectId" @projectSwitch="projectSwitch"
                        @openDownload="openDownload"/>
      </span>
      <span>
        <table-search-bar :condition.sync="condition" @change="search" @search="search" class="search-bar" :tip="tip" :items="items" ref="conditionSearch"/>
      </span>
    </el-row>
    <el-row v-show="normals && ((normals.length > 0) || (tags && Object.keys(tags).length > 0))" type="flex" justify="space-between" align="middle">
      <span>
        <I style="font-size: 12px;color: #888">{{ $t('commons.filter_condition') }} </I>
        <el-tag v-show="normals.length > 0" v-for="(normal, index) in normals" :key="index" closable type="success" size="mini" class="el-tag-con" @close="handleClose2(normal)">
          {{ $t(normal.i18nKey) }} : {{ normal.searchValue }}
        </el-tag>
        <el-tag v-show="tags && Object.keys(tags).length > 0" v-for="(value, key) in tags" :key="key" closable type="info" size="mini" class="el-tag-con" @close="handleClose(key)">
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
import ProjectSwitch from "@/business/components/report/head/ProjectSwitch";
import Vue from "vue";

/* eslint-disable */
  export default {
    name: "TableHeader",
    components: {
      TableAdvSearchBar,
      TableSearchBar,
      TableButton,
      ProjectSwitch
    },
    data(){
      return {
        tags: [],
        normals: [],
      }
    },
    props: {
      projectId: String,
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
        if (this.condition.normalSearch) {
          this.normals = this.condition.normalSearch;
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
      projectSwitch(projectId, accountName, createTime) {
        this.$emit("projectSwitch", projectId, accountName, createTime);
      },
      openDownload() {
        this.$emit('openDownload');
      },
      handleClose(key) {
        Vue.delete(this.condition.combine, key);
        this.search(null);
        this.$refs.conditionSearch.conditionSearch(this.condition.combine);
      },
      handleClose2(normal) {
        //普通搜索 对象数组删除元素 [{},{}] => [{}]
        let arr = this.condition.normalSearch.length;
        for (let i = 0; i < arr; i++) {
          if(normal === this.condition.normalSearch[i]) {
            this.condition.normalSearch.splice(i, 1);
            this.condition[normal.searchName] = '';
          }
        }
        this.search(null);
        this.$refs.conditionSearch.conditionSearch2(this.condition.normalSearch);
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
    width: 200px;
  }
  .el-tag-con {
    margin: 2px 5px 0 0;
  }
</style>
