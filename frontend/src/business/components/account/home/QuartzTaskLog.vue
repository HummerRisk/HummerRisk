<template>
  <div>
    <el-row class="el-form-item-dev" v-if="row.data.length == 0">
      <span>{{ $t('resource.i18n_no_data') }}<br></span>
    </el-row>
    <el-row class="el-form-item-dev" v-if="row.data.length > 0">
      <el-table :show-header="true" :data="row.data" class="adjust-table table-content" :row-class-name="tableRowClassName">
        <el-table-column type="index" min-width="3%"/>
        <el-table-column prop="qzType" :label="$t('account.choose_qztype')" min-width="7%" show-overflow-tooltip></el-table-column>
        <el-table-column prop="operator" :label="$t('account.operator')" min-width="10%" show-overflow-tooltip></el-table-column>
        <el-table-column prop="operation" :label="$t('account.operation')" min-width="10%" show-overflow-tooltip></el-table-column>
        <el-table-column min-width="12%" :label="$t('account.create_time')" sortable prop="createTime">
          <template v-slot:default="scope">
            <span><i class="el-icon-time"></i> {{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
      </el-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="row.total"/>
    </el-row>
  </div>
</template>

<script>
import TablePagination from "@/business/components/common/pagination/TablePagination";
/* eslint-disable */
  export default {
    name: "QuartzTaskLog",
    components: {
      TablePagination,
    },
    data() {
      return {
        currentPage: 1,
        pageSize: 10,
        loading: false,
      }
    },
    activated() {
    },
    props: {
      row: {data: [], id: "", total: 0},
    },
    methods: {
      init() {
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
      search(){
        let url = "/task/quartz/rela/log/";
        this.$post(url + this.row.id + "/" + this.currentPage + "/" + this.pageSize, {},response => {
          let data = response.data;
          this.row.total = data.itemCount;
          this.row.data = data.listObject;
        });
      }
    },

    created() {
      this.init();
    },
    mounted() {
    },
    beforeDestroy() {
    }
  }
</script>

<style scoped>
  .bg-purple-light {
    background: #f2f2f2;
  }
  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  }

  .grid-content-log-span {
    width: 38%;
    float: left;
    vertical-align: middle;
    display:table-cell;
    margin: 6px 1%;
  }

</style>
