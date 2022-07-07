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
        <el-input class="module-input" :placeholder="$t('account.search')" v-model="condition.filterText"
                  size="small">
          <template v-slot:append>
            <el-button icon="el-icon-folder-add" @click="addScenario"/>
          </template>
        </el-input>
      </template>

    </node-tree>

    <!--Create account-->
    <el-drawer class="rtl" :title="$t('account.create')" :visible.sync="createVisible" size="50%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="small" :rules="rule" ref="createAccountForm">
        <el-form-item :label="$t('account.name')" ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('account.input_name')"/>
        </el-form-item>
        <el-form-item :label="$t('account.cloud_platform')" :rules="{required: true, message: $t('account.cloud_platform'), trigger: 'change'}">
          <el-select style="width: 100%;" v-model="form.pluginId" :placeholder="$t('account.please_choose_plugin')" @change="changePlugin(form.pluginId)">
            <el-option
              v-for="item in plugins"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              <img :src="require(`@/assets/img/platform/${item.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
              &nbsp;&nbsp; {{ $t(item.name) }}
            </el-option>
          </el-select>
        </el-form-item>
        <div v-for="(tmp, index) in tmpList" :key="index">
          <el-form-item v-if="tmp.inputType === 'password'" :label="tmp.label" style="margin-bottom: 29px">
            <el-input :type="tmp.inputType" v-model="tmp.input" autocomplete="new-password" show-password :placeholder="tmp.description"/>
          </el-form-item>
          <el-form-item v-if="tmp.inputType != 'password' && tmp.inputType != 'boolean'" :label="tmp.label">
            <el-input :type="tmp.inputType" v-model="tmp.input" autocomplete="off" :placeholder="tmp.description"/>
          </el-form-item>
        </div>
      </el-form>
      <dialog-footer
        @cancel="createVisible = false"
        @confirm="saveAccount(form, 'add')"/>
    </el-drawer>
    <!--Create account-->

  </div>

</template>

<script>
import {getCurrentAccountID} from "@/common/js/utils";
import NodeTree from "@/business/components/common/components/NodeTree";
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
    mounted() {
      this.accountId = getCurrentAccountID();
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
        let url = "/plugin/all";
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
      addScenario() {
        this.form = {};
        this.createVisible = true;
      },
      enableTrash() {
        this.condition.trashEnable = true;
      },
      //选择插件查询云账号信息
      async changePlugin (pluginId, type){
        let url = "/plugin/";
        this.result = await this.$get(url + pluginId, response => {
          let fromJson = typeof(response.data) === 'string'?JSON.parse(response.data):response.data;
          this.tmpList = fromJson.data;
          if (type === 'edit') {
            let credentials = typeof(this.item.credential) === 'string'?JSON.parse(this.item.credential):this.item.credential;
            for (let tmp of this.tmpList) {
              if (credentials[tmp.name] === undefined) {
                tmp.input = tmp.defaultValue?tmp.defaultValue:"";
              } else {
                tmp.input = credentials[tmp.name];
              }
            }
          } else {
            for (let tmp of this.tmpList) {
              if (tmp.defaultValue !== undefined) {
                tmp.input = tmp.defaultValue;
              }
            }
          }
        });
      },
      //保存云账号
      saveAccount(item, type){
        if (!this.tmpList.length) {
          this.$error(this.$t('account.i18n_account_cloud_plugin_param'));
          return;
        }
        let data = {}, key = {};
        for (let tmp of this.tmpList) {
          key[tmp.name] = tmp.input;
        }
        data["credential"] = JSON.stringify(key);
        data["name"] = item.name;
        data["pluginId"] = item.pluginId;

        if (type === 'add') {
          this.$post("account/add", data,response => {
            if (response.success) {
              this.$success(this.$t('account.i18n_hr_create_success'));
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
