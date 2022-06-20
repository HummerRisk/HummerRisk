<template>
  <div>

    <slot name="header"></slot>

    <server-group-tree
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
        <el-input class="module-input" :placeholder="$t('server.server_search')" v-model="condition.filterText"
                  size="small">
          <template v-slot:append>
            <el-button icon="el-icon-folder-add" @click="addServer"/>
          </template>
        </el-input>
      </template>

    </server-group-tree>

    <!--Create group-->
    <el-drawer class="rtl" :title="$t('server.server_group_create')" :visible.sync="createVisible" size="50%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="small" :rules="rule" ref="createServerGroupCreateForm">
        <el-form-item :label="$t('server.server_group_name')" ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('server.server_group_name')"/>
        </el-form-item>
      </el-form>
      <dialog-footer
        @cancel="createVisible = false"
        @confirm="saveGroup(form, 'add')"/>
    </el-drawer>
    <!--Create group-->

  </div>

</template>

<script>
import ServerGroupTree from "@/business/components/common/components/ServerGroupTree";
import {buildNodePath} from "@/common/js/NodeTree";
import DialogFooter from "../../common/components/DialogFooter";
/* eslint-disable */
  export default {
    name: 'ServerGroup',
    components: {
      ServerGroupTree,
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
        data: [],
        currentModule: undefined,
        createVisible: false,
        form: {},
        tmpList: [],
        direction: 'rtl',
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
        let url = "/server/serverGroupList";
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
        this.$post("/server/editServerGroup", param, () => {
          this.$success(this.$t('commons.save_success'));
          this.list();
          this.refresh();
        }, (error) => {
          this.list();
        });
      },
      add(param) {
        this.$post("/server/addServerGroup", param, () => {
          this.$success(this.$t('commons.save_success'));
          this.list();
        }, (error) => {
          this.list();
        });
      },
      remove(data) {
        this.$post("/server/deleteServerGroup", data, () => {
          this.list();
          this.refresh();
        }, (error) => {
          this.list();
        });
      },
      drag(param, list) {
        this.$post("/server/drag", param, () => {
          this.list();
        }, (error) => {
          this.list();
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
      saveAsEdit(data) {
        this.$emit('saveAsEdit', data);
      },
      refresh() {
        this.$emit("refreshTable");
      },
      addServer() {
        this.form = {};
        this.createVisible = true;
      },
      enableTrash() {
        this.condition.trashEnable = true;
      },
      //保存虚拟机分组
      saveGroup(item, type){
        if (type === 'add') {
          this.$post("server/addServerGroup", item,response => {
            if (response.success) {
              this.$success(this.$t('server.i18n_hr_create_success'));
              this.handleClose();
              this.list();
            } else {
              this.$error(response.message);
            }
          });
        }
      },
      handleClose() {
        this.createVisible = false;
      },
    }
  }
</script>

<style scoped>
  .node-tree {
    margin-top: 15px;
    margin-bottom: 15px;
  }

  .ms-el-input {
    height: 25px;
    line-height: 25px;
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

  .ms-api-buttion {
    width: 30px;
  }

  .module-input {
    width: 275px;
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
