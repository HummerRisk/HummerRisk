<template>

  <div>
    <el-row class="table-title" type="flex" justify="space-between" align="middle">
      <slot name="title">
        {{ title }}
      </slot>
    </el-row>
    <el-row type="flex" justify="space-between" align="middle">
      <span class="operate-button">
        <slot name="button"></slot>
      </span>
      <span>
        <table-search-bar :condition.sync="condition" @change="search" class="search-bar" :tip="tip"/>
        <table-adv-search-bar :condition.sync="condition" @search="search" v-if="isCombine"/>
      </span>
    </el-row>
  </div>

</template>

<script>
import TableSearchBar from '../../common/components/TableSearchBar';
import TableButton from '../../common/components/TableButton';
import TableAdvSearchBar from "../../common/components/search/TableAdvSearchBar";

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
      condition: {
        type: Object
      },
      tip: {
        String,
        default() {
          return this.$t('dashboard.search_by_name');
        }
      }
    },
    methods: {
      search(value) {
        this.$emit('update:condition', this.condition);
        this.$emit('search', value);
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
