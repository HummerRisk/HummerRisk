<template>
  <div>
    <slot name="header">
      <el-input :placeholder="$t('server.search_by_name')" v-model="filterText" size="small" :clearable="true"/>
    </slot>

    <el-tree
      class="filter-tree node-tree"
      :data="extendTreeNodes"
      :default-expanded-keys="expandedNode"
      node-key="id"
      @node-expand="nodeExpand"
      @node-collapse="nodeCollapse"
      :filter-node-method="filterNode"
      :expand-on-click-node="false"
      highlight-current
      :draggable="!disabled"
      ref="tree">

      <template v-slot:default="{node,data}">
      <span class="custom-tree-node father" @click="handleNodeSelect(node)">
        <span class="node-icon">
          <i class="iconfont icon-fuwuqi"></i>
        </span>
        <span class="node-title" v-text="data.name"/>
      </span>
      </template>
    </el-tree>
  </div>
</template>

<script>
/* eslint-disable */
export default {
  name: "NodeTree",
  components: {},
  data() {
    return {
      result: {},
      filterText: "",
      expandedNode: [],
      defaultProps: {
        children: "children",
        label: "label"
      },
      extendTreeNodes: []
    };
  },
  props: {
    type: {
      type: String,
      default: "view"
    },
    treeNodes: {
      type: Array
    },
    allLabel: {
      type: String,
      default() {
        return this.$t("server.all_label");
      }
    },
  },
  watch: {
    treeNodes() {
      this.init();
    },
    filterText(val) {
      this.filter(val);
    }
  },
  computed: {
    disabled() {
      return this.type !== 'edit';
    }
  },
  methods: {
    init() {
      this.extendTreeNodes = [];
      this.extendTreeNodes.unshift({
        "id": "root",
        "name": this.allLabel,
        "level": 0,
        "children": this.treeNodes,
      });
      if (this.expandedNode.length === 0) {
        this.expandedNode.push("root");
      }
    },
    handleNodeSelect(node) {
      let nodeIds = [];
      let pNodes = [];
      this.getChildNodeId(node.data, nodeIds);
      this.getParentNodes(node, pNodes);
      this.$emit("nodeSelectEvent", node, nodeIds, pNodes);
    },
    filterNode(value, data) {
      if (!value) return true;
      if (data.name) {
        return data.name.indexOf(value) !== -1;
      }
      return false;
    },
    filter(val) {
      this.$refs.tree.filter(val);
    },
    nodeExpand(data) {
      if (data.id) {
        this.expandedNode.push(data.id);
      }
    },
    nodeCollapse(data) {
      if (data.id) {
        this.expandedNode.splice(this.expandedNode.indexOf(data.id), 1);
      }
    },
    getChildNodeId(rootNode, nodeIds) {
      //递归获取所有子节点ID
      nodeIds.push(rootNode.id);
      if (rootNode.children) {
        for (let i = 0; i < rootNode.children.length; i++) {
          this.getChildNodeId(rootNode.children[i], nodeIds);
        }
      }
    },
    getParentNodes(rootNode, pNodes) {
      if (rootNode.parent && rootNode.parent.id !== 0) {
        this.getParentNodes(rootNode.parent, pNodes);
      }
      if (rootNode.data.name && rootNode.data.name !== "") {
        pNodes.push(rootNode.data);
      }
    },
  }
};
</script>

<style scoped>
.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
}

.el-icon-arrow-down {
  font-size: 12px;
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

.node-tree {
  margin-top: 15px;
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

.name-input {
  height: 25px;
  line-height: 25px;
}

.name-input >>> .el-input__inner {
  height: 25px;
  line-height: 25px;
}

.el-input-group{
  margin: 6px;
}
</style>
