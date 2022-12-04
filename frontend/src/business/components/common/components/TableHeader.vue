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
        <table-search-bar v-if="isCombine" :condition.sync="condition" @change="search" @search="search" class="search-bar" :tip="tip" :items="items"/>
        <slot name="button"></slot>
      </span>
      <span class="operate-button">
        <table-adv-search-bar v-if="isCombine" :showOpen="showOpen" :showList="showList" @search="search" @download="download" @more="more" @menu="menu"
                              :columnNames="columnNames" :checkedColumnNames="checkedColumnNames" :checkAll="checkAll" :isIndeterminate="isIndeterminate"
                              @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange" @handleCheckAllChange="handleCheckAllChange"/>
      </span>
    </el-row>
  </div>

</template>

<script>
import TableSearchBar from '@/business/components/common/components/TableSearchBar';
import TableButton from '@/business/components/common/components/TableButton';
import TableAdvSearchBar from "@/business/components/common/components/search/TableAdvSearchBar";
// 引入导出Excel表格依赖
import FileSaver from "file-saver";
import XLSX from "xlsx";

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
      showList: {
        type: Boolean,
        default: false
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
      download() {
        /* 从表生成工作簿对象 */
        // 判断要导出的节点中是否有fixed的表格，如果有，转换excel时先将该dom移除，然后append回去，
        let fix = document.querySelector("#out-table .el-table__fixed");
        let wb;
        if (fix) {
          wb = XLSX.utils.table_to_book(document.querySelector("#out-table").removeChild(fix));
          document.querySelector("#out-table").appendChild(fix);
        } else {
          wb = XLSX.utils.table_to_book(document.querySelector("#out-table"),{raw:true});
        }
        /* 获取二进制字符串作为输出 */
        var wbout = XLSX.write(wb, {
          bookType: "xlsx",
          bookSST: true,
          type: "array"
        });
        try {
          FileSaver.saveAs(
            //Blob 对象表示一个不可变、原始数据的类文件对象。
            //Blob 表示的不一定是JavaScript原生格式的数据。
            //File 接口基于Blob，继承了 blob 的功能并将其扩展使其支持用户系统上的文件。
            //返回一个新创建的 Blob 对象，其内容由参数中给定的数组串联组成。
            new Blob([wbout], { type: "application/octet-stream" }),
            //设置导出文件名称
            "table.xlsx"
          );
        } catch (e) {
          if (typeof console !== "undefined") console.log(e, wbout);
        }
        return wbout;
      },
      deleteSelect() {
        this.$emit('deleteSelect');
      },
      handleCheckAllChange(val) {
        this.$emit('handleCheckAllChange', val);
      },
      handleCheckedColumnNamesChange(value) {
        this.$emit('handleCheckedColumnNamesChange', value);
      },
      more() {
        this.$emit('more');
      },
      menu() {
        this.$emit('menu');
      },
    },
    computed: {
      isCombine() {
        return this.condition.components !== undefined && this.condition.components.length > 0;
      }
    },
    created() {
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
