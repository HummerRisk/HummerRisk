<template>
  <el-row type="flex" justify="end">
    <div class="table-page">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="pageSizes"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
  </el-row>
</template>

<script>
/* eslint-disable */
  export default {
    name: "TablePagination",
    props: {
      page: Object,
      currentPage: {
        type: Number,
        default: 1
      },
      pageSize: {
        type: Number,
        default: 12
      },
      pageSizes: {
        type: Array,
        default: function () {
          return [12, 24, 48, 96, 192]
        }
      },
      total: {
        type: Number,
        default: 0
      },
      change: Function
    },
    methods: {
      handleSizeChange: function (size) {
        this.$emit('update:pageSize', size)
        this.change();
      },
      handleCurrentChange(current) {
        this.$emit('update:currentPage', current)
        this.change();
      }
    }
  }
</script>

<style scoped>
  .table-page {
    padding-top: 20px;
  }
</style>
