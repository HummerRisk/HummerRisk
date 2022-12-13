<template>

  <div>
    <el-row class="table-title" type="flex" justify="space-between" align="middle">
      <slot name="title">
        {{ title }}
      </slot>
    </el-row>
    <el-row type="flex" justify="space-between" align="middle">
      <span class="operate-button">
        <table-button  v-if="showBack" icon="el-icon-back"
                         :content="backTip" @click="back"/>
        <table-search-bar v-if="isCombine" :condition.sync="condition" @change="search" @search="search" class="search-bar" @upload="upload"
                          :showFilter="showFilter" :showUpload="showUpload" :showUploadName="showUploadName" :tip="tip" :items="items"/>
      </span>
      <span>
        <table-adv-search-bar v-if="isCombine" :showOpen="showOpen" :showList="showList" @search="search" @pdfDown="pdfDown" @excelDown="excelDown" @more="more" @menu="menu"
                              :columnNames="columnNames" :checkedColumnNames="checkedColumnNames" :checkAll="checkAll" :isIndeterminate="isIndeterminate"
                              @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange" @handleCheckAllChange="handleCheckAllChange"/>
      </span>
    </el-row>
  </div>

</template>

<script>
import TableSearchBar from '../../common/components/TableSearchBar';
import TableButton from '../../common/components/TableButton';
import TableAdvSearchBar from "../../common/components/search/TableAdvSearchBar";
// 引入导出Excel表格依赖
import FileSaver from "file-saver";
import XLSX from "xlsx";
import htmlToPdf from "@/common/js/htmlToPdf";

export default {
    name: "TableHeader",
    components: {TableAdvSearchBar, TableSearchBar, TableButton},
    data(){
      return {
        htmlTitle: this.$t('pdf.html_title'),
      }
    },
    props: {
      title: {
        type: String,
        default() {
          return this.$t('resource.search_by_hummerid');
        }
      },
      showBack: {
        type: Boolean,
        default: false
      },
      condition: {
        type: Object
      },
      backTip: {
        type: String
      },
      tip: {
        String,
        default() {
          return this.$t('resource.search_by_hummerid');
        }
      },
      showOpen: {
        type: Boolean,
        default: true
      },
      showFilter: {
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
      showUpload: {
        type: Boolean,
        default: false
      },
      showUploadName: {
        type: Boolean,
        default: false
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
      checkAll: {
        type: Boolean,
        default: true
      },
      isIndeterminate: {
        type: Boolean,
        default: false
      },
    },
    methods: {
      search(value) {
        this.$emit('update:condition', this.condition);
        this.$emit('search', value);
      },
      back() {
        this.$emit('back');
      },
      //下载excel
      excelDown() {
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
      //下载pdf
      pdfDown() {
        htmlToPdf.getPdf(this.htmlTitle);
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
      upload() {
        this.$emit('upload');
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
