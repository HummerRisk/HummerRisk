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

      <el-divider><i class="el-icon-tickets"></i></el-divider>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="table-card" v-loading="result.loading">
            <template v-slot:header>
              <span class="title">{{ $t('k8s.k8s_resource_type') }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
            <k8s v-if="accountId" :key="timeRefusr" :accountId="accountId" :currentAccount="currentAccount"/>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="table-card" v-loading="result.loading">
            <template v-slot:header>
              <span class="title">{{ 'NameSpace' }}</span>
            </template>
            <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
<!--            <div id="network_id1" class="network" style="height:80vh"></div>-->
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

/* eslint-disable */
export default {
  components: {
    MainContainer,
    SearchList,
    K8s,
  },
  data() {
    return {
      result: {},
      dialogVisible: false,
      currentAccount: "",
      items: [],
      nodes: [],
      edges: [],
      network:null,
      container: null,
      //   节点数组
      nodesArray: [
        {
          id: 1,
          label: "Namespace",
          color: { background: "pink" },
        },
        {
          id: 2,
          label: "Pod",
          color: { background: "pink" },
        },
        {
          id: 3,
          label: "Node",
          color: { background: "pink" },
        },
        {
          id: 4,
          label: "Deployment",
          color: { background: "pink" },
        },
        {
          id: 5,
          label: "Service",
          color: { background: "pink" },
        },
      ],
      //   关系线数组
      edgesArray: [],
      options: {},
      data: {},
      accountId: "",
      timeRefusr: new Date().getTime(),
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
        // let k8sTopology = response.data.k8sTopology;
        // let edgesTopology = response.data.edgesTopology;
        // for(let obj of k8sTopology){
        //   if (obj.type === 'Namespace') {
        //     obj = Object.assign(obj, {shape: "image", image: require(`@/assets/img/vis/k8s/namespace.png`)});
        //   } else if (obj.type === 'Pod') {
        //     obj = Object.assign(obj, {shape: "image", image: require(`@/assets/img/vis/k8s/pod.png`)});
        //   } else if (obj.type === 'Node') {
        //     obj = Object.assign(obj, {shape: "image", image: require(`@/assets/img/vis/k8s/node.png`)});
        //   } else if (obj.type === 'Deployment') {
        //     obj = Object.assign(obj, {shape: "image", image: require(`@/assets/img/vis/k8s/deployment.png`)});
        //   } else if (obj.type === 'Service') {
        //     obj = Object.assign(obj, {shape: "image", image: require(`@/assets/img/vis/k8s/service.png`)});
        //   }
        // }
        // this.nodesArray = this.nodesArray.concat(k8sTopology);
        // let k8sNode = [
        //   {
        //     id: this.accountId,
        //     label: this.currentAccount,
        //     shape: "image",
        //     image: require(`@/assets/img/platform/k8s.png`)
        //   }
        // ];
        // this.nodesArray = this.nodesArray.concat(k8sNode);

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
</style>

