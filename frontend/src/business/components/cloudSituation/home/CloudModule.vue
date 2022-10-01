<template>
  <div>

    <slot name="header"></slot>

    <node-tree
      v-loading="result.loading"
      :tree-nodes="data"
      :type="isReadOnly ? 'view' : 'edit'"
      @add="add"
      @edit="edit"
      @drag="drag"
      @remove="remove"
      @nodeSelectEvent="nodeChange"
      ref="nodeTree">

      <template v-slot:header>
        <el-input class="module-input" :placeholder="$t('commons.search_by_name')" v-model="condition.filterText"
                  size="small">
          <template v-slot:append>
            <el-button icon="el-icon-folder"/>
          </template>
        </el-input>
      </template>

    </node-tree>

  </div>

</template>

<script>
import {getK8sID} from "@/common/js/utils";
import NodeTree from "./NodeTree";
import {buildNodePath} from "@/common/js/NodeTree";
import DialogFooter from "../../common/components/DialogFooter";
/* eslint-disable */
  export default {
    name: 'ScenarioModule',
    components: {
      NodeTree,
      DialogFooter,
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
        createVisible: false,
        form: {},
        tmpList: [],
        direction: 'rtl',
        plugins: [],
        rule: {
          name: [
            {required: true, message: this.$t('commons.input_name'), trigger: 'blur'},
            {min: 2, max: 100, message: this.$t('commons.input_limit', [2, 100]), trigger: 'blur'},
            {
              required: true,
              message: this.$t("workspace.special_characters_are_not_supported"),
              trigger: 'blur'
            }
          ]
        },
      }
    },
    created() {
      this.accountId = getK8sID();
      this.list();
      this.activePlugin();
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
      //查询插件
      activePlugin() {
        let url = "/plugin/cloud";
        this.result = this.$get(url, response => {
          let data = response.data;
          this.plugins =  data;
        });
      },
      list() {
        let url = "/account/allList";
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
      edit(param) {
      },
      add(param) {
      },
      remove(nodeIds) {
      },
      drag(param, list) {
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
      saveAsEdit(data) {
        this.$emit('saveAsEdit', data);
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
    width: 275px;
    margin: 6px;
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
  /deep/ :focus{outline:0;}
</style>
