<template>
  <main-container class="box-dev">
    <slot name="header"></slot>
    <favourite-tree
      v-loading="result.loading"
      :tree-nodes="favouriteData"
      :type="'view'"
      @nodeSelectEvent="nodeChange"
      @delFavourite="delFavourite"
      ref="favouriteTree">
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
      @favourite="favourite"
      ref="nodeTree">
    </account-tree>
  </main-container>
</template>

<script>
import FavouriteTree from "@/business/components/task/head/FavouriteTree";
import AccountTree from "@/business/components/task/head/AccountTree";
import MainContainer from "@/business/components/common/components/MainContainer";
import {taskAccountListUrl, taskAddFavoriteUrl, taskDelFavoriteUrl, taskFavoriteListUrl} from "@/api/system/task";

/* eslint-disable */
export default {
  components: {
    AccountTree,
    FavouriteTree,
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
      //收藏夹
      this.result = this.$get(taskFavoriteListUrl, response => {
        if (response.data != undefined && response.data != null) {
          this.favouriteData = response.data;
        }
      });
      //资源信息
      this.result = this.$get(taskAccountListUrl, response => {
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
    favourite(data) {
      let param = {};
      param = data;
      param.icon = param.pluginIcon;
      this.result = this.$post(taskAddFavoriteUrl, param, response => {
        this.list();
      });
    },
    delFavourite(data) {
      let url = taskDelFavoriteUrl + data.id;
      this.result = this.$get(url, response => {
        this.list();
      });
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

