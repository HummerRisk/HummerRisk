<template>
  <main-container class="box-dev">
    <slot name="header"></slot>
    <account-tree
      v-loading="result.loading"
      :tree-nodes="data"
      :type="'view'"
      @nodeSelectEvent="nodeChange"
      :selectAccounts="selectAccounts"
      :checkedKeys="checkedKeys"
      ref="nodeTree">
    </account-tree>
  </main-container>
</template>

<script>
import AccountTree from "@/business/components/reportcenter/head/AccountTree";
import MainContainer from "@/business/components/common/components/MainContainer";
import {reportProjectListUrl} from "@/api/cloud/report/report";

/* eslint-disable */
export default {
  components: {
    AccountTree,
    MainContainer,
  },
  data() {
    return {
      result: {},
      condition: {
        filterText: "",
        trashEnable: false
      },
      data: [],
      favouriteData: [],
      currentModule: undefined,
    }
  },
  props: {
    selectAccounts: [],
    checkedKeys: [],
  },
  watch: {
    'condition.filterText'(val) {
      this.$refs.nodeTree.filter(val);
      this.$refs.favouriteTree.filter(val);
    },
    'condition.trashEnable'() {
      this.$emit('enableTrash', this.condition.trashEnable);
    },
  },
  methods: {
    list() {
      //账号信息
      this.result = this.$get(reportProjectListUrl, response => {
          this.data = response.data;
      });
    },
    nodeChange(node, nodeIds, pNodes) {
      this.currentModule = node.data;
      this.condition.trashEnable = false;
      if (node.data.id === 'root') {
        this.$emit("nodeSelectEvent", node, [], pNodes);
      } else {
        this.$emit("nodeSelectEvent", node, nodeIds, pNodes);
      }
    },
  },
  created() {
    this.list();
  }
}
</script>

<style scoped>
.node-tree {
  margin-top: 15px;
  margin-bottom: 15px;
}

.custom-tree-node {
  flex: 1 1 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
  width: 100%;
}

.father .child {
  display: none;
}

.father:hover .child {
  display: block;
}

.node-title {
  width: 0;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1 1 auto;
  padding: 0 5px;
  overflow: hidden;
}

.node-operate > i {
  color: #409eff;
  margin: 0 5px;
}

/deep/ .el-tree-node__content {
  height: 33px;
}

.module-input {
  width: 100%;
}

.rtl >>> .el-drawer__body {
  overflow-y: auto;
  padding: 20px;
}
.rtl >>> input {
  width: 100%;
}
.rtl >>> .el-select {
  width: 80%;
}
.rtl >>> .el-form-item__content {
  width: 60%;
}
.grid-content {
  min-height: 36px;
}
.bg-purple {
  background: #d3dce6;
  width: 100%;
}
.box-dev {
  max-height: 426px;
}
/deep/ :focus{outline:0;}
/deep/ .scrollbar {
  white-space: nowrap;
}
.el-scrollbar {
  display: flex;
  justify-content: space-around;
  padding: 0 10px;
}
/deep/ .el-scrollbar__wrap {
  overflow: scroll;
  width: 110%;
  height: 100%;
}

</style>

