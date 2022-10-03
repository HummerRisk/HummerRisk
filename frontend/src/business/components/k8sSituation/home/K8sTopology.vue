<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <el-menu class="header-menu" :unique-opened="true" mode="horizontal" default-active="1" router background-color="aliceblue" active-text-color="red">
        <!-- 不激活项目路由-->
        <el-menu-item index="1" v-show="false">Placeholder</el-menu-item>
        <el-submenu index="2" popper-class="submenu">
          <template v-slot:title>
        <span class="account-name" :title="currentAccount" style="width: 250px;">
          {{ $t('k8s.name') }}: {{ currentAccount }}
        </span>
          </template>
          <search-list v-if="items.length>0" :items="items" @cloudAccountSwitch="cloudAccountSwitch"/>
        </el-submenu>

      </el-menu>

      <el-row :gutter="20" class="el-row-body">
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ $t('k8s.k8s_resource_type') }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <k8s v-if="accountId" :key="timeRefusr" :accountId="accountId" :currentAccount="currentAccount"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'NameSpace' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <name-space v-if="k8sTopology.k8sNameSpace" :key="timeRefusr" :k8sNameSpace="k8sTopology.k8sNameSpace" :edgesNameSpace="k8sTopology.edgesNameSpace"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Node' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <node v-if="k8sTopology.k8sNode" :key="timeRefusr" :k8sLink="k8sTopology.k8sNode" :edgesBelong="k8sTopology.edgesNode"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Pod' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <pod v-if="k8sTopology.k8sPod" :key="timeRefusr" :k8sLink="k8sTopology.k8sPod" :edgesBelong="k8sTopology.edgesPod"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Deployment' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <deployment v-if="k8sTopology.k8sDeployment" :key="timeRefusr" :k8sLink="k8sTopology.k8sDeployment" :edgesBelong="k8sTopology.edgesDeployment"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Service' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <service v-if="k8sTopology.k8sService" :key="timeRefusr" :k8sLink="k8sTopology.k8sService" :edgesBelong="k8sTopology.edgesService"/>
          </el-card>
        </el-col>
      </el-row>

    </el-card>
  </main-container>
</template>

<script>
import MainContainer from "../../common/components/MainContainer";
import SearchList from "@/business/components/k8sSituation/home/SearchList";
import K8s from "@/business/components/k8sSituation/topology/K8s";
import NameSpace from "@/business/components/k8sSituation/topology/NameSpace";
import Node from "@/business/components/k8sSituation/topology/Node";
import Pod from "@/business/components/k8sSituation/topology/Pod";
import Deployment from "@/business/components/k8sSituation/topology/Deployment";
import Service from "@/business/components/k8sSituation/topology/Service";

/* eslint-disable */
export default {
  components: {
    MainContainer,
    SearchList,
    K8s,
    NameSpace,
    Node,
    Pod,
    Deployment,
    Service,
  },
  data() {
    return {
      result: {},
      dialogVisible: false,
      currentAccount: "",
      items: [],
      accountId: "",
      timeRefusr: new Date().getTime(),
      k8sTopology: {},
    };
  },
  methods: {
    init() {
      this.$get("/k8s/allList", response => {
        this.items = response.data;
        this.accountId = this.items[0].id;
        this.currentAccount = this.items[0].name;
        this.search();
      })
    },
    resetAllNodes() {
      let _this = this;
      _this.nodes.clear();
      _this.edges.clear();
      _this.nodes.add(_this.nodesArray);
      _this.edges.add(_this.edgesArray);
      _this.data = {
        nodes: _this.nodes,
        edges: _this.edges
      };
      //   network是一种用于将包含点和线的网络和网络之间的可视化展示
      _this.network = new Vis.Network(
        _this.container,
        _this.data,
        _this.options
      );
    },
    resetAllNodesStabilize() {
      let _this = this;
      _this.resetAllNodes();
      _this.network.stabilize();
    },
    cloudAccountSwitch(accountId, accountName) {
      this.accountId = accountId;
      this.currentAccount = accountName;
    },
    async search() {
      let url = "/k8s/k8sTopology/" + this.accountId;
      await this.$get(url,response => {
        this.k8sTopology = response.data;
      });
    },
  },
  mounted() {
    this.init();
  },
  created() {
  }
}
</script>

<style scoped>
.table-card >>> .vis-edit-mode {
  width: 60px;
}
.account-name {
  display: inline-block;
  width: 130px;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
}
.header-menu {
  margin: 15px 0 15px 0;
}
.el-divider--horizontal {
  margin: 0;
}
.el-row-body {
  line-height: 1.15;
}
.el-col-su >>> .el-card {
  margin: 5px 0;
}
</style>

