<template>
  <div>

    <slot name="header"></slot>

    <node-tree
      v-loading="result.loading"
      :tree-nodes="data"
      @nodeSelectEvent="nodeChange"
      ref="nodeTree">

      <template v-slot:header>
        <el-input class="module-input" :placeholder="$t('image.search_by_name')" v-model="condition.filterText"
                  size="small">
          <template v-slot:append>
            <el-button><i class="iconfont icon-wendang"/></el-button>
          </template>
        </el-input>
      </template>

    </node-tree>

  </div>

</template>

<script>
import NodeTree from "@/business/components/fs/head/NodeTree";
import {buildNodePath} from "@/common/js/NodeTree";

/* eslint-disable */
  export default {
    name: 'ScenarioModule',
    components: {
      NodeTree,
    },
    props: {
      isReadOnly: {
        type: Boolean,
        default() {
          return false
        }
      },
      relevanceProjectId: String,
      planId: String
    },
    computed: {
    },
    data() {
      return {
        result: {},
        condition: {
          filterText: "",
          trashEnable: false
        },
        accountId: "",
        data: [],
        currentModule: undefined,
      }
    },
    mounted() {
      this.list();
    },
    watch: {
      'condition.filterText'(val) {
        this.$refs.nodeTree.filter(val);
      },
      'condition.trashEnable'() {
        this.$emit('enableTrash', this.condition.trashEnable);
      },
      planId() {
        this.list();
      },
      relevanceProjectId() {
        this.list();
      }
    },
    methods: {
      list() {
        let url = "/image/allList";
        this.result = this.$get(url, response => {
          if (response.data != undefined && response.data != null) {
            this.data = response.data;
            let moduleOptions = [];
            this.data.forEach(node => {
              buildNodePath(node, {path: ''}, moduleOptions);
            });
            this.$emit('setModuleOptions', moduleOptions);
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
      refresh() {
        this.$emit("refreshTable");
      },
      enableTrash() {
        this.condition.trashEnable = true;
      },
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
    width: 95%;
    margin: 6px;
  }

  .iconfont {
    color: #409eff;
  }

  /deep/ :focus{outline:0;}
</style>
