<template>
  <div class="box-dev">

    <slot name="header"></slot>

    <favourite-tree
      v-loading="result.loading"
      :tree-nodes="favouriteData"
      :type="'view'"
      @nodeSelectEvent="nodeChange"
      ref="nodeTree">
      <template v-slot:header>
        <el-input class="module-input" :placeholder="$t('task.task_search')" v-model="condition.filterText"
                  size="small" :clearable="true">
          <template v-slot:append>
            <el-button icon="el-icon-search"/>
          </template>
        </el-input>
      </template>
    </favourite-tree>

    <account-tree
      v-loading="result.loading"
      :tree-nodes="data"
      :type="'view'"
      @nodeSelectEvent="nodeChange"
      ref="nodeTree">
    </account-tree>

  </div>
</template>

<script>
import {buildNodePath} from "@/common/js/NodeTree";
import FavouriteTree from "@/business/components/task/head/FavouriteTree";
import AccountTree from "@/business/components/task/head/AccountTree";

/* eslint-disable */
export default {
  components: {
    AccountTree,
    FavouriteTree,
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
  methods: {
    list() {
      //收藏夹
      this.result = this.$get("/task/favorite/list", response => {
        if (response.data != undefined && response.data != null) {
          this.favouriteData = response.data;
        }
      });

      let url = "/task/account/list";
      this.result = this.$get(url, response => {
        if (response.data != undefined && response.data != null) {
          this.data = response.data;
        }
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
.box-dev >>> .module-input {

}
/deep/ :focus{outline:0;}
</style>

