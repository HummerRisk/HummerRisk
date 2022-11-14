<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('oss.oss_bucket')"
                      :show-validate="false" :show-create="false"/>

      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort"
                :row-class-name="tableRowClassName"
                @filter-change="filter">
        <el-table-column type="index" min-width="1%"/>
        <el-table-column prop="bucketName" :label="$t('oss.bucket')" min-width="12%" show-overflow-tooltip v-slot:default="scope">
          <el-link type="primary" @click="showObject(scope.row)">
            {{ scope.row.bucketName }}
          </el-link>
        </el-table-column>
        <el-table-column :label="$t('oss.name')" min-width="10%" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="location" :label="$t('oss.location')" min-width="10%" show-overflow-tooltip></el-table-column>
        <el-table-column prop="canned_acl" :label="$t('oss.acl')" min-width="10%" show-overflow-tooltip v-slot:default="scope">
          {{ scope.row.cannedAcl?scope.row.cannedAcl:'-' }}
        </el-table-column>
        <el-table-column prop="storageClass" :label="$t('oss.storage_class')" min-width="10%" show-overflow-tooltip></el-table-column>
        <el-table-column prop="size" :label="$t('oss.oss_size')" min-width="10%" show-overflow-tooltip></el-table-column>
        <el-table-column prop="objectNumber" :label="$t('oss.object_number')" min-width="10%" show-overflow-tooltip></el-table-column>
        <!--        <el-table-column min-width="10%" :label="$t('commons.operating')" fixed="right">-->
        <!--          <template v-slot:default="scope">-->
        <!--            <table-operators :buttons="bucketButtons" :row="scope.row"/>-->
        <!--          </template>-->
        <!--        </el-table-column>-->
      </el-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--oss bucket-->
    <el-drawer class="rtl" :title="$t('oss.oss_bucket')" :visible.sync="bucketVisible" size="70%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-table border :data="objectData" class="adjust-table table-content" @sort-change="sort" stripe>
        <el-table-column type="index" min-width="1%"></el-table-column>
        <el-table-column prop="objectName" :label="$t('oss.object_name')" min-width="20%" show-overflow-tooltip v-slot:default="scope">
          <el-link v-if="scope.row.objectType==='BACK'" type="primary" style="color: red;" @click="backObject(scope.row)">
            <i class="el-icon-back"></i>  {{ scope.row.objectName }}
          </el-link>
          <el-link v-if="scope.row.objectType==='DIR'" type="primary" @click="selectObject(scope.row)">
            <i class="el-icon-folder-opened"></i>  {{ scope.row.objectName }}
          </el-link>
          <span v-if="scope.row.objectType==='FILE'" style="color: #336d9f">
            <i class="el-icon-document"></i> {{ scope.row.objectName }}
          </span>
        </el-table-column>
        <el-table-column prop="objectType" :label="$t('oss.object_type')" min-width="8%" show-overflow-tooltip v-slot:default="scope">
          <span v-if="scope.row.objectType==='DIR'">{{ $t('oss.object_dir') }}</span>
          <span v-if="scope.row.objectType==='FILE'">{{ $t('oss.object_file') }}</span>
          <span v-if="scope.row.objectType==='BACK'">{{ $t('vis.back') }}</span>
        </el-table-column>
        <el-table-column prop="objectSize" :label="$t('oss.oss_size')" min-width="10%" show-overflow-tooltip v-slot:default="scope">
          {{ scope.row.objectSize?scope.row.objectSize:'-' }}
        </el-table-column>
        <el-table-column prop="storageClass" :label="$t('oss.storage_class')" min-width="10%" show-overflow-tooltip v-slot:default="scope">
          {{ scope.row.storageClass?scope.row.storageClass:'-' }}
        </el-table-column>
        <el-table-column min-width="15%" :label="$t('account.update_time')" sortable prop="lastModified">
          <template v-slot:default="scope">
            <span v-if="scope.row.lastModified">{{ scope.row.lastModified | timestampFormatDate }}</span>
            <span v-if="!scope.row.lastModified">{{ '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
    <!--oss bucket-->

  </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {OSS_CONFIGS} from "@/business/components/common/components/search/search-components";
import {saveAs} from "@/common/js/FileSaver";

/* eslint-disable */
export default {
  name: "Result",
  components: {
    TableOperators,
    MainContainer,
    Container,
    TableHeader,
    TablePagination,
    DialogFooter,
  },
  data() {
    return {
      result: {},
      condition: {
        components: OSS_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      path: '/',
      thisObject: {},
      bucketVisible: false,
      visible: false,
      direction: 'rtl',
      objectData: [],
    }
  },
  methods: {
    //查询列表
    search() {
      let url = "/oss/bucketList/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleClose() {
      this.visible =  false;
      this.bucketVisible = false;
    },
    sort(column) {
      _sort(column, this.condition);
      this.init();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.init();
    },
    init() {
      this.search();
    },
    tableRowClassName({row, rowIndex}) {
      if (rowIndex % 4 === 0) {
        return 'success-row';
      } else if (rowIndex % 2 === 0) {
        return 'warning-row';
      } else {
        return '';
      }
    },
    showObject(bucket) {
      this.path = '/';
      this.result = this.$get("/oss/objects/" + bucket.id, response => {
        this.objectData = response.data;
        this.bucketVisible = true;
      });
    },
    getObjects(path) {
      if (path !== '' && path !== 'none') {
        this.path = path;
        this.result = this.$post("/oss/objects/" + this.thisObject.bucketId, { "path" : path=="/"?"":path}, response => {
          this.objectData = response.data;
          this.bucketVisible = true;
        });
      }
    },
    backObject(item) {
      if (this.path === '/') {
        this.showObject(item);
      } else {
        this.thisObject = item;
        this.getObjects(item.id);
      }
    },
    selectObject(item) {
      this.thisObject = item;
      this.getObjects(item.id);
    },
    download(item) {
      this.result = this.$download("/downloadObject/" + item.bucketId, {
        objectId: item.id,
      }, response => {
        if (response.status === 201) {
          let blob = new Blob([response.data], {type: "'application/octet-stream'"});
          saveAs(blob, item.objectName);
        }
      }, error => {
        console.log("下载报错", error);
      });
    },
  },
  created() {
    this.init();
  },
}
</script>

<style scoped>

</style>

