<template>
  <div class="flex-table">
    <el-table
      id="out-table"
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
    tableRowClassName({row, rowIndex}) {
      if (rowIndex % 4 === 0) {
        return 'success-row';
      } else if (rowIndex % 2 === 0) {
        return 'warning-row';
      } else {
        return '';
      }
    },
  },
};
</script>

<style scoped>
</style>
