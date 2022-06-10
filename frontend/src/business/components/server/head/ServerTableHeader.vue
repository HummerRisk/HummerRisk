<template>

  <div>
    <el-row class="table-title" type="flex" justify="space-between" align="middle">
      <slot name="title">
        {{ title }}
      </slot>
    </el-row>
    <el-row type="flex" justify="space-between" align="middle">
      <span class="operate-button">
        <el-upload
          class="upload"
          ref="upload"
          :show-file-list="false"
          action=""
          :multiple="false"
          accept="application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          :on-change="importExcel"
          :limit="1">
        <table-button  v-if="showCreate" icon="el-icon-circle-plus-outline"
                       :content="createTip" @click="create"/>
        <table-button v-if="showScan" icon="el-icon-video-play"
                      type="success" :content="scanTip" @click="scan"/>
        <table-button v-if="showRun" icon="el-icon-check"
                      type="primary" :content="runTip" @click="validate"/>
<!--        <table-button v-if="showDownload" icon="el-icon-download"-->
<!--                      type="danger" :content="downloadTip" @click="download"/>-->
<!--        <table-button v-if="showUpload" slot="trigger" icon="el-icon-upload2"-->
<!--                      type="info" :content="uploadTip" @click="upload"/>-->
        </el-upload>
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
import XLSX from 'xlsx';

export default {
  name: "ServerTableHeader",
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
    showDownload: {
      type: Boolean,
      default: false
    },
    showUpload: {
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
    downloadTip: {
      type: String,
      default() {
        return this.$t('account.download');
      }
    },
    uploadTip: {
      type: String,
      default() {
        return this.$t('account.upload');
      }
    },
    tip: {
      String,
      default() {
        return this.$t('commons.search_by_name');
      }
    }
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
    download() {
      this.$emit('download')
    },
    upload() {
      this.$emit('upload')
    },
    // 导入表格
    importExcel(param) {
      let _this = this;
      _this.file2Xce(param).then(item => {
        if (item && item.length > 0) {
          // xlsxJson就是解析出来的json数据,数据格式如下
          // [{sheetName: sheet1, sheet: sheetData }]
          if (item[0] && item[0].sheet && item[0].sheet.length) {
            _this.tableData = item[0].sheet  //把数据塞到表格预览
          }
        }
      }).catch(error => {
          // loading.close();
        });
    },
    /**
     * 解析文件
     * @param {Object} file
     */
    file2Xce(file) {
      return new Promise(function(resolve, reject) {
        const reader = new FileReader();
        reader.onload = function(e) {
          const data = e.target.result;
          this.wb = XLSX.read(data, {
            type: "binary"
          });
          const result = [];
          this.wb.SheetNames.forEach(sheetName => {
            result.push({
              sheetName: sheetName,
              sheet: XLSX.utils.sheet_to_json(this.wb.Sheets[sheetName])
            });
          });
          resolve(result);
        };
        reader.readAsBinaryString(file.raw);
      });
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
