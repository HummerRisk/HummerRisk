<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <el-menu class="header-menu" :unique-opened="true" mode="horizontal" default-active="1" router background-color="aliceblue" active-text-color="red">
        <!-- 不激活项目路由-->
        <el-menu-item index="1" v-show="false">Placeholder</el-menu-item>
        <el-submenu index="2" popper-class="submenu">
          <template v-slot:title>
            <span class="account-name" :title="currentAccount">
              {{ $t('k8s.name') }}: {{ currentAccount }}
            </span>
          </template>
          <search-list v-if="items.length>0" :items="items" @cloudAccountSwitch="cloudAccountSwitch"/>
        </el-submenu>

        <div style="float: right;margin: 10px 15px 10px 0;">
          <span class="title-account">{{ data }}</span>
          <el-divider direction="vertical"></el-divider>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ $t('k8s.k8s_resource_type') }}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-for="item in types"
                :key="item.id"
                :label="item.name"
                :value="item.id"
                :command="item">
                {{ item.name }}
              </el-dropdown-item>
              <el-dropdown-item divided :command="{id: 'all', name: $t('rule.all')}">{{ $t('rule.all') }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
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
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='NameSpace' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'NameSpace' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <name-space v-if="k8sTopology.k8sNameSpace" :key="timeRefusr" :k8sNameSpace="k8sTopology.k8sNameSpace" :edgesNameSpace="k8sTopology.edgesNameSpace"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='Node' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Node' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <node v-if="k8sTopology.k8sNode" :key="timeRefusr" :k8sLink="k8sTopology.k8sNode" :edgesBelong="k8sTopology.edgesNode"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='Pod' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Pod' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <pod v-if="k8sTopology.k8sPod" :key="timeRefusr" :k8sLink="k8sTopology.k8sPod" :edgesBelong="k8sTopology.edgesPod"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='Deployment' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Deployment' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <deployment v-if="k8sTopology.k8sDeployment" :key="timeRefusr" :k8sLink="k8sTopology.k8sDeployment" :edgesBelong="k8sTopology.edgesDeployment"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='Service' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Service' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <service v-if="k8sTopology.k8sService" :key="timeRefusr" :k8sLink="k8sTopology.k8sService" :edgesBelong="k8sTopology.edgesService"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='DaemonSet' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'DaemonSet' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <daemon-set v-if="k8sTopology.k8sDaemonSet" :key="timeRefusr" :k8sLink="k8sTopology.k8sDaemonSet" :edgesBelong="k8sTopology.edgesDaemonSet"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='Ingress' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Ingress' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <ingress v-if="k8sTopology.k8sIngress" :key="timeRefusr" :k8sLink="k8sTopology.k8sIngress" :edgesBelong="k8sTopology.edgesIngress"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='Role' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Role' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <role v-if="k8sTopology.k8sRole" :key="timeRefusr" :k8sLink="k8sTopology.k8sRole" :edgesBelong="k8sTopology.edgesRole"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='Secret' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Secret' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <secret v-if="k8sTopology.k8sSecret" :key="timeRefusr" :k8sLink="k8sTopology.k8sSecret" :edgesBelong="k8sTopology.edgesSecret"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='ConfigMap' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'ConfigMap' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <config-map v-if="k8sTopology.k8sConfigMap" :key="timeRefusr" :k8sLink="k8sTopology.k8sConfigMap" :edgesBelong="k8sTopology.edgesConfigMap"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='StatefulSet' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'StatefulSet' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <stateful-set v-if="k8sTopology.k8sStatefulSet" :key="timeRefusr" :k8sLink="k8sTopology.k8sStatefulSet" :edgesBelong="k8sTopology.edgesStatefulSet"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='CronJob' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'CronJob' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <cron-job v-if="k8sTopology.k8sCronJob" :key="timeRefusr" :k8sLink="k8sTopology.k8sCronJob" :edgesBelong="k8sTopology.edgesCronJob"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='Job' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Job' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <job v-if="k8sTopology.k8sJob" :key="timeRefusr" :k8sLink="k8sTopology.k8sJob" :edgesBelong="k8sTopology.edgesJob"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='PV' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'PV' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <p-v v-if="k8sTopology.k8sPV" :key="timeRefusr" :k8sLink="k8sTopology.k8sPV" :edgesBelong="k8sTopology.edgesPV"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='PVC' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'PVC' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <p-v-c v-if="k8sTopology.k8sPVC" :key="timeRefusr" :k8sLink="k8sTopology.k8sPVC" :edgesBelong="k8sTopology.edgesPVC"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='Lease' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Lease' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <lease v-if="k8sTopology.k8sLease" :key="timeRefusr" :k8sLink="k8sTopology.k8sLease" :edgesBelong="k8sTopology.edgesLease"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='EndpointSlice' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'EndpointSlice' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <endpoint-slice v-if="k8sTopology.k8sEndpointSlice" :key="timeRefusr" :k8sLink="k8sTopology.k8sEndpointSlice" :edgesBelong="k8sTopology.edgesEndpointSlice"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='Event' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Event' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <event v-if="k8sTopology.k8sEvent" :key="timeRefusr" :k8sLink="k8sTopology.k8sEvent" :edgesBelong="k8sTopology.edgesEvent"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='NetworkPolicy' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'NetworkPolicy' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <network-policy v-if="k8sTopology.k8sNetworkPolicy" :key="timeRefusr" :k8sLink="k8sTopology.k8sNetworkPolicy" :edgesBelong="k8sTopology.edgesNetworkPolicy"/>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12" class="el-col el-col-su" v-if="data==='Version' || data===$t('rule.all')">
          <el-card class="table-card">
            <template v-slot:header>
              <span class="title">{{ 'Version' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <version v-if="k8sTopology.k8sVersion" :key="timeRefusr" :k8sLink="k8sTopology.k8sVersion" :edgesBelong="k8sTopology.edgesVersion"/>
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
import DaemonSet from "@/business/components/k8sSituation/topology/DaemonSet";
import Ingress from "@/business/components/k8sSituation/topology/Ingress";
import Role from "@/business/components/k8sSituation/topology/Role";
import ConfigMap from "@/business/components/k8sSituation/topology/ConfigMap";
import StatefulSet from "@/business/components/k8sSituation/topology/StatefulSet";
import Secret from "@/business/components/k8sSituation/topology/Secret";
import CronJob from "@/business/components/k8sSituation/topology/CronJob";
import Job from "@/business/components/k8sSituation/topology/Job";
import PV from "@/business/components/k8sSituation/topology/PV";
import PVC from "@/business/components/k8sSituation/topology/PVC";
import Lease from "@/business/components/k8sSituation/topology/Lease";
import EndpointSlice from "@/business/components/k8sSituation/topology/EndpointSlice";
import Event from "@/business/components/k8sSituation/topology/Event";
import NetworkPolicy from "@/business/components/k8sSituation/topology/NetworkPolicy";
import Version from "@/business/components/k8sSituation/topology/Version";

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
    DaemonSet,
    Ingress,
    Role,
    ConfigMap,
    StatefulSet,
    Secret,
    CronJob,
    Job,
    PV,
    PVC,
    Lease,
    EndpointSlice,
    Event,
    NetworkPolicy,
    Version,
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
      types: [
        {id: 'NameSpace', name: 'NameSpace'},
        {id: 'Pod', name: 'Pod'},
        {id: 'Node', name: 'Node'},
        {id: 'Deployment', name: 'Deployment'},
        {id: 'Service', name: 'Service'},
        {id: 'DaemonSet', name: 'DaemonSet'},
        {id: 'Ingress', name: 'Ingress'},
        {id: 'Role', name: 'Role'},
        {id: 'Secret', name: 'Secret'},
        {id: 'ConfigMap', name: 'ConfigMap'},
        {id: 'StatefulSet', name: 'StatefulSet'},
        {id: 'CronJob', name: 'CronJob'},
        {id: 'Job', name: 'Job'},
        {id: 'PV', name: 'PV'},
        {id: 'PVC', name: 'PVC'},
        {id: 'Lease', name: 'Lease'},
        {id: 'EndpointSlice', name: 'EndpointSlice'},
        {id: 'Event', name: 'Event'},
        {id: 'NetworkPolicy', name: 'NetworkPolicy'},
        {id: 'Version', name: 'Version'},
      ],
      data: this.$t('rule.all'),
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
      this.k8sTopology = {};
      this.search();
    },
    async search() {
      let url = "/k8s/k8sTopology/" + this.accountId;
      await this.$get(url,response => {
        this.k8sTopology = response.data;
      });
    },
    handleCommand(command) {
      this.data = command.name;
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
  width: 250px;
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
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}
.el-icon-arrow-down {
  font-size: 12px;
}
.title-account{
  color: #e43235;
}
</style>

