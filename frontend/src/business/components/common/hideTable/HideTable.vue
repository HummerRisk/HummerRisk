<template>
  <div class="flex-table">
    <el-table
      ref="table"
      class="adjust-table table-content"
      border
      @sort-change="sort"
      @filter-change="filter"
      @select-all="select"
      @select="select"
      :row-class-name="tableRowClassName"
      v-bind="$attrs"
      v-on="tableEvent"
      min-height="2000"
      :data="tableData"
      :style="{ width: '100%' }">
      <table-body :columns="columns">
        <slot></slot>
      </table-body>
      <slot name="__operation"></slot>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-table>
  </div>
</template>

<script>
import TableBody from "./TableBody";
import TablePagination from "@/business/components/common/pagination/TablePagination";

export default {
  components: {
    TableBody,
    TablePagination,
  },
  props: {
    columns: {
      type: Array,
      default: () => [],
    },
    multipleSelection: {
      type: Array,
      default: () => [],
    },
    pagination: {
      type: Object,
      default: () => {
      },
    },
    isRememberSelected: {
      type: Boolean,
      default: false,
    },
    selectedFlags: {
      type: String,
      default: "id",
    },
    tableData: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      multipleSelectionCach: [],
      tableEvent: {},
      currentPage: 1,
      pageSize: 10,
      total: 0,
    };
  },
  created() {
    this.handleListeners();
  },
  computed: {
    multipleSelectionAll() {
      return [...this.multipleSelectionCach, ...this.multipleSelection];
    },
  },
  watch: {
    pagination: {
      handler() {
        this.paginationDefalut = {
          ...this.paginationDefalut,
          ...this.pagination,
        };
      },
      deep: true,
      immediate: true,
    },
    tableData: {
      handler() {
        this.$nextTick(() => {
          this.$refs.table.doLayout();
        });
        if (!this.isRememberSelected) return;
        // 先拷贝 重新加载数据会触发SelectionChange 导致this.multipleSelection为空
        const multipleSelection = [...this.multipleSelection];
        this.$nextTick(() => {
          this.handlerSelected(multipleSelection);
        });
      },
      deep: true,
    },
    columns: {
      handler() {
        this.$nextTick(() => {
          this.$refs.table.doLayout();
        });
      },
      deep: true,
    },
  },
  methods: {
    toggleRowSelection(row) {
      this.$refs.table.toggleRowSelection(row, true);
    },
    handlerSelected(multipleSelection) {
      this.multipleSelectionCach = [
        ...this.multipleSelectionCach,
        ...multipleSelection,
      ];
      const flags = this.multipleSelectionCach.map(
        (ele) => ele[this.selectedFlags]
      );
      // 当前页的选中项索引
      const notCurrenArr = [];
      this.tableData.forEach((ele) => {
        const resultIndex = flags.indexOf(ele[this.selectedFlags]);
        if (resultIndex !== -1) {
          this.$refs.table.toggleRowSelection(ele, true);
          notCurrenArr.push(resultIndex);
        }
      });
      notCurrenArr.sort().reduceRight((pre, next) => {
        this.multipleSelectionCach.splice(next, 1);
      }, 0);
    },
    handleListeners() {
      Object.keys(this.$listeners).forEach((key) => {
        this.tableEvent[key] = this.$listeners[key];
      });
    },
    search() {
      this.$emit('search');
    },
    sort() {
      this.$emit('sort');
    },
    filter() {
      this.$emit('filter');
    },
    select() {
      this.$emit('select');
    },
    tableRowClassName() {
      this.$emit('tableRowClassName');
    },
  },
};
</script>

<style lang="scss" scoped>
.flex-table {
  display: flex;
  height: 100%;
  flex-direction: column;
  justify-content: space-between;

  ::v-deep.el-table-column--selection .cell {
    padding: 0 14px;
  }

  .el-table::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    z-index: 10;
    border-top: 1px solid rgba(31, 35, 41, 0.15);;
  }

  ::v-deep .el-table__fixed-body-wrapper {
    tr {
      background-color: var(--TableBG, #ffffff) !important;
    }
  }

  .pagination-cont {
    text-align: right;
    margin-top: 10px;

    ::v-deep .el-pager li {
      background-color: #fff;
      border: 1px solid #bbbfc4;
      border-radius: 4px;
      color: #1f2329;
      box-sizing: border-box;
      line-height: 26px;
      font-family: SF Pro Text;
      font-size: 14px;
      font-weight: 400;
    }

    ::v-deep .btn-prev,
    ::v-deep .btn-next {
      background: #fff;
      background-color: #fff;
      border: 1px solid #bbbfc4;
      border-radius: 4px;
      color: #bbbfc4;
    }

    ::v-deep .el-pagination__total {
      font-family: "PingFang SC";
      font-style: normal;
      font-weight: 400;
      font-size: 14px;
      line-height: 22px;
      color: #1f2329;
      line-height: 28px;
    }

    ::v-deep .number.active,
    ::v-deep .el-input__inner:hover {
      border-color:  #df913c;
      color:  #df913c !important;
      background-color: #fff !important;
    }

    ::v-deep .el-icon-more {
      border: none !important;
    }
  }
}
</style>
