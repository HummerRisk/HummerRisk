<template>
  <div class="container-lin">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="always" class="hr-card-index-1">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.quick_search') }}</span>
            <el-button icon="el-icon-refresh" style="float: right;" size="mini" @click="clean">{{ $t('commons.clear') }}</el-button>
          </template>
          <el-collapse v-model="activeName" accordion>
            <el-form ref="form" :model="condition" label-width="80px" size="mini">
              <el-collapse-item :title="$t('dashboard.types_1')" name="1">
                <el-radio-group v-model="condition.scanType" size="medium" @change="changeSearch">
                  <div class="_group"><el-radio class="radio_group" border :label="'clouAccount'" name="clouAccount">{{ $t('dashboard.cloud_scan') }}</el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="'vulnAccount'" name="vulnAccount">{{ $t('dashboard.vuln_scan') }}</el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="'serverAccount'" name="serverAccount">{{ $t('dashboard.server_scan') }}</el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="'packageAccount'" name="packageAccount">{{ $t('dashboard.package_scan') }}</el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="'imageAccount'" name="imageAccount">{{ $t('dashboard.image_scan') }}</el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="'codeAccount'" name="imageAccount">{{ $t('dashboard.code_scan') }}</el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="'k8sAccount'" name="imageAccount">{{ $t('dashboard.k8s_scan') }}</el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="'configAccount'" name="imageAccount">{{ $t('dashboard.config_scan') }}</el-radio></div>
                </el-radio-group>
              </el-collapse-item>
              <el-collapse-item :title="$t('dashboard.types_2')" name="2">
                <el-tree
                  :data="groupData"
                  :props="defaultProps"
                  accordion
                  @node-click="handleNodeClick">
                </el-tree>
              </el-collapse-item>
              <el-collapse-item :title="$t('dashboard.types_3')" name="3">
                <el-tree
                  class="filter-tree node-tree"
                  :data="extendTreeNodes"
                  default-expand-all
                  node-key="id"
                  @node-expand="nodeExpand"
                  @node-collapse="nodeCollapse"
                  :filter-node-method="filterNode"
                  :expand-on-click-node="false"
                  accordion
                  highlight-current
                  ref="tree">
                  <template v-slot:default="{node,data}">
                    <span class="node-title" v-text="data.name" @click="handleNodeClick(node,data)"/>
                  </template>
                </el-tree>
              </el-collapse-item>
              <el-collapse-item :title="$t('dashboard.types_4')" name="4">
                <el-radio-group v-model="condition.severityType" size="medium" @change="changeSearch">
                  <div class="_group"><el-radio class="radio_group" border :label="'HighRisk'" name="HighRisk">{{ $t('rule.HighRisk') }}</el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="'MediumRisk'" name="MediumRisk">{{ $t('rule.MediumRisk') }}</el-radio></div>
                  <div class="_group"><el-radio class="radio_group" border :label="'LowRisk'" name="LowRisk">{{ $t('rule.LowRisk') }}</el-radio></div>
                </el-radio-group>
              </el-collapse-item>
              <el-collapse-item :title="$t('dashboard.types_5')" name="5">
                <el-select v-model="condition.users" :placeholder="$t('dashboard.scan_users')" @change="changeSearch">
                  <el-option
                    v-for="item in users"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                    &nbsp;&nbsp; {{ $t(item.name) }}
                  </el-option>
                </el-select>
              </el-collapse-item>
              <el-collapse-item :title="$t('dashboard.types_6')" name="6">
                <div class="block">
                  <el-date-picker
                    v-model="condition.date"
                    size="mini"
                    type="daterange"
                    align="right"
                    class="date_picker"
                    unlink-panels
                    value-format="timestamp"
                    :range-separator="$t('commons.date.range_separator')"
                    :start-placeholder="$t('commons.date.start_date')"
                    :end-placeholder="$t('commons.date.end_date')"
                    @change="changeSearchByTime"
                    :picker-options="pickerOptions">
                  </el-date-picker>
                </div>
            </el-collapse-item>
            </el-form>
          </el-collapse>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card shadow="always" class="hr-card-index-1">
          <div>
            <el-table class="adjust-table table-content"
                      border max-height="710"
                      :data="tableData"
                      @sort-change="sort"
                      @filter-change="filter">
              <el-table-column type="index" min-width="2%"/>
              <el-table-column v-slot:default="scope" :label="$t('dashboard.accounts')" min-width="10%" show-overflow-tooltip>
                {{ scope.row.accountName }}
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('account.creator')" min-width="9%" show-overflow-tooltip>
                {{ scope.row.userName }}
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('dashboard.safe_score')" min-width="10%" show-overflow-tooltip>
                {{ scope.row.scanScore }}
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('resource.status')" min-width="13%" prop="status" sortable show-overflow-tooltip>
                <el-button plain size="mini" type="primary" v-if="scope.row.status === 'UNCHECKED'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                </el-button>
                <el-button plain size="mini" type="primary" v-else-if="scope.row.status === 'APPROVED'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                </el-button>
                <el-button plain size="mini" type="primary" v-else-if="scope.row.status === 'PROCESSING'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                </el-button>
                <el-button plain size="mini" type="success" v-else-if="scope.row.status === 'FINISHED'">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </el-button>
                <el-button plain size="mini" type="danger" v-else-if="scope.row.status === 'ERROR'">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </el-button>
                <el-button plain size="mini" type="warning" v-else-if="scope.row.status === 'WARNING'">
                  <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                </el-button>
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" show-overflow-tooltip min-width="8%">
                <el-tooltip class="item" effect="dark" :content="$t('history.resource_result')" placement="top">
                  <span v-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
                  <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum == 0)">
                  {{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}
                </span>
                  <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)">
                  <el-link type="primary" class="text-click" @click="goResource(scope.row)">{{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}</el-link>
                </span>
                </el-tooltip>
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('resource.status_on_off')" prop="returnSum" show-overflow-tooltip min-width="10%">
                <span v-if="scope.row.returnSum == 0" style="color: #46ad59;">{{ $t('resource.i18n_compliance_true') }}</span>
                <span v-else-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)" style="color: #f84846;">{{ $t('resource.i18n_compliance_false') }}</span>
                <span v-else-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
              </el-table-column>
              <el-table-column prop="createTime" min-width="14%" :label="$t('account.update_time')" sortable show-overflow-tooltip>
                <template v-slot:default="scope">
                  <span>{{ scope.row.createTime | timestampFormatDate }}</span>
                </template>
              </el-table-column>
            </el-table>

            <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {_filter, _sort} from "@/common/js/utils";
import TablePagination from "@/business/components/common/pagination/TablePagination";

/* eslint-disable */
export default {
  name: "AnalysisLeftInfo",
  components: {
    TablePagination,
  },
  props: {
    data: {},
  },
  data() {
    return {
      activeName: '1',
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      condition: {
      },
      searchForm: {},
      pickerOptions: {
        shortcuts: [{
          text: this.$t('dashboard.last_week'),
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: this.$t('dashboard.last_month'),
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: this.$t('dashboard.last_three_month'),
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
      groupData: [{
        label: this.$t('dashboard.rule_tag'),
        children: []
      }, {
        label: this.$t('dashboard.rule_set'),
        children: []
      }, {
        label: this.$t('dashboard.rule_report'),
        children: []
      }],
      accountData: [{
        label: this.$t('dashboard.cloud_scan'),
        children: []
      }, {
        label: this.$t('dashboard.vuln_scan'),
        children: []
      }, {
        label: this.$t('dashboard.server_scan'),
        children: []
      }, {
        label: this.$t('dashboard.package_scan'),
        children: []
      }, {
        label: this.$t('dashboard.image_scan'),
        children: []
      }, {
        label: this.$t('dashboard.code_scan'),
        children: []
      }, {
        label: this.$t('dashboard.k8s_scan'),
        children: []
      }, {
        label: this.$t('dashboard.config_scan'),
        children: []
      }],
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      users: [],
      extendTreeNodes: [],
      allLabel: this.$t("task.all_account"),
      cloudAccount: this.$t("task.task_cloud"),
      vulnAccount: this.$t("task.task_vuln"),
      serverAccount: this.$t("task.task_server"),
      imageAccount: this.$t("task.task_image"),
      packageAccount: this.$t("task.task_package"),
      codeAccount: this.$t("task.task_code"),
      k8sAccount: this.$t("task.task_k8s"),
      configAccount: this.$t("task.task_config"),
      expandedNode: [],
    }
  },
  methods: {
    clean() {
      this.condition = {};
      this.search();
    },
    initAccountTree() {
      let url = "/task/account/list";
      this.result = this.$get(url, response => {
        if (response.data != undefined && response.data != null) {
          let treeNodes = response.data;
          //资源信息树
          this.extendTreeNodes = [];
          this.extendTreeNodes.unshift({
            "id": "root",
            "name": this.allLabel,
            "level": 0,
            "children": [
              {name: this.cloudAccount, level: 1, type: 'cloudAccount', children: treeNodes.cloudAccount},
              {name: this.vulnAccount, level: 1, type: 'vulnAccount', children: treeNodes.vulnAccount},
              {name: this.serverAccount, level: 1, type: 'serverAccount', children: treeNodes.serverAccount},
              {name: this.imageAccount, level: 1, type: 'imageAccount', children: treeNodes.imageAccount},
              {name: this.packageAccount, level: 1, type: 'packageAccount', children: treeNodes.packageAccount},
              {name: this.codeAccount, level: 1, type: 'codeAccount', children: treeNodes.codeAccount},
              {name: this.k8sAccount, level: 1, type: 'k8sAccount', children: treeNodes.k8sAccount},
              {name: this.configAccount, level: 1, type: 'configAccount', children: treeNodes.configAccount},
            ],
          });
        }
      });
    },
    sort(column) {
      _sort(column, this.condition);
      this.search();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.search();
    },
    search () {
      this.result = this.$get("/user/list/all", response => {
        let data = response.data;
        this.users =  data;
      });
      let url = "/dashboard/historyScanVo/" + this.currentPage + "/" + this.pageSize;
      //在这里实现事件
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    changeSearch(){
      this.search();
    },
    changeSearchByTime() {
      if (this.condition.date) {
        this.condition.startTime = this.condition.date[0];
        this.condition.endTime = this.condition.date[1];
      } else {
        this.condition.startTime = null;
        this.condition.endTime = null;
      }
      this.search();
    },
    handleNodeClick(node, data) {
      if(node.level !== 3) return;
      this.condition.accountId = data.id;
      this.search();
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
    getNodeTree(nodes, id, list) {
      if (!nodes) {
        return;
      }
      for (let i = 0; i < nodes.length; i++) {
        if (nodes[i].id === id) {
          i - 1 >= 0 ? list[0] = nodes[i-1].id : list[0] = "";
          list[1] = nodes[i].id;
          i + 1 < nodes.length ? list[2] = nodes[i+1].id : list[2] = "";
          return;
        }
        if (nodes[i].children) {
          this.getNodeTree(nodes[i].children, id, list);
        }
      }
    },
    findTreeByNodeId(rootNode, nodeId) {
      if (rootNode.id === nodeId) {
        return rootNode;
      }
      if (rootNode.children) {
        for (let i = 0; i < rootNode.children.length; i++) {
          if (this.findTreeByNodeId(rootNode.children[i], nodeId)) {
            return rootNode;
          }
        }
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
    filterNode(value, data) {
      if (!value) return true;
      if (data.name) {
        return data.name.indexOf(value) !== -1;
      }
      return false;
    },
  },
  created() {
    this.initAccountTree();
    this.search();
  },
}

</script>

<style scoped>
.container-lin {
  padding: 0;
}
.hr-card-index-1{
  border-left-color: #e8a97e;
  border-left-width: 3px;
  height: 787px;
}
.descriptions__title {
  font-size: 16px;
  font-weight: 700;
}
.block {
  margin: 10px 10px 0 30px;
}
.title {
  font-size: 16px;
  font-weight: 500;
  margin-top: 0;
  text-overflow: ellipsis;
  overflow: hidden;
  word-wrap: break-word;
  white-space: nowrap;
}
._group {
  margin: 10px 0 10px 20px;
  width: 200px;
}
.radio_group {
  width: 130px;
}
.block {
  width: 100%;
}
.date_picker {
  width: 80%;
}
</style>
