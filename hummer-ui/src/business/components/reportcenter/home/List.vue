<template>
  <main-container>

  </main-container>
</template>

<script>
import MainContainer from "../../common/components/MainContainer";
//列表展示与隐藏

/* eslint-disable */
export default {
  components: {
    MainContainer,
  },
  provide() {
    return {
      search: this.search,
    }
  },
  data() {
    return {
    }
  },
  watch: {
    '$route': 'init'
  },
  methods: {
    init(){},
  },
  created() {
    this.init();
  },
}
</script>

<style scoped>
.table-content {
  width: 100%;
}

.el-table {
  cursor: pointer;
}
</style>

