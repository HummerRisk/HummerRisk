<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <el-menu class="header-menu" :unique-opened="true" mode="horizontal" default-active="1" router background-color="aliceblue" active-text-color="red">
        <!-- 不激活项目路由-->
        <el-menu-item index="1" v-show="false">Placeholder</el-menu-item>
        <el-submenu index="2" popper-class="submenu">
          <template v-slot:title>
        <span class="account-name" :title="currentAccount" style="width: 250px;">
          {{ $t('account.name') }}: {{ currentAccount }}
        </span>
          </template>
          <search-list @cloudAccountSwitch="cloudAccountSwitch"/>
        </el-submenu>

      </el-menu>

      <el-divider><i class="el-icon-tickets"></i></el-divider>

      <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
      <div id="network_id" class="network" style="height:80vh"></div>
      <el-dialog :title="$t('vis.edit')" :visible.sync="dialogVisible" width="width">
        <span style="color: red;">{{ $t('vis.unedit') }}</span>
        <div slot="footer">
          <el-button @click="dialogVisible = false">{{ $t('commons.cancel') }}</el-button>
          <el-button type="primary" @click="dialogVisible = false">{{ $t('commons.confirm') }}</el-button>
        </div>
      </el-dialog>
    </el-card>
  </main-container>
</template>

<script>
import MainContainer from "../../common/components/MainContainer";
import Vis from "vis";
import SearchList from "@/business/components/common/head/SearchList";

/* eslint-disable */
export default {
  components: {
    MainContainer,
    SearchList,
    Vis,
  },
  data() {
    return {
      result: {},
      dialogVisible: false,
      currentAccount: "",
      nodes: [],
      edges: [],
      // network:null,
      container: null,
      //   节点数组
      nodesArray: [
        {
          id: 111,
          label: "aliyun",
          color: { background: "yellow" },
          shape: "image",
          image: require(`@/assets/img/platform/aliyun2.png`)
        },
        {
          id: 0,
          label: "ECS",
          color: { background: "yellow" },
          shape: "image",
          image: require(`@/assets/img/vis/aliyun.ecs.png`)
        },
        {
          id: 1,
          label: "CDN",
          color: { background: "pink" },
          shape: "image",
          image: require(`@/assets/img/vis/aliyun.cdn.png`)
        },
        {
          id: 2,
          label: "Disk",
          color: { background: "pink" },
          shape: "image",
          image: require(`@/assets/img/vis/aliyun.disk.png`)
        },
        {
          id: 3,
          label: "EIP",
          color: { background: "pink" },
          shape: "image",
          image: require(`@/assets/img/vis/aliyun.eip.png`)
        },
        {
          id: 4,
          label: "ELB",
          color: { background: "pink" },
          shape: "image",
          image: require(`@/assets/img/vis/aliyun.elb.png`)
        },
        {
          id: 5,
          label: "MongoDB",
          color: { background: "pink" },
          shape: "image",
          image: require(`@/assets/img/vis/aliyun.mongodb.png`)
        },
        {
          id: 6,
          label: "OSS",
          color: { background: "pink" },
          shape: "image",
          image: require(`@/assets/img/vis/aliyun.oss.png`)
        },
        {
          id: 7,
          label: "Polardb",
          color: { background: "pink" },
          shape: "image",
          image: require(`@/assets/img/vis/aliyun.polardb.png`)
        },
        {
          id: 8,
          label: "RAM",
          color: { background: "pink" },
          shape: "image",
          image: require(`@/assets/img/vis/aliyun.ram.png`)
        },
        {
          id: 9,
          label: "RDS",
          color: { background: "pink" },
          shape: "image",
          image: require(`@/assets/img/vis/aliyun.rds.png`)
        },
        {
          id: 10,
          label: "Redis",
          color: { background: "pink" },
          shape: "image",
          image: require(`@/assets/img/vis/aliyun.redis.png`)
        },
        {
          id: 11,
          label: "Security-group",
          color: { background: "pink" },
          shape: "image",
          image: require(`@/assets/img/vis/aliyun.security-group.png`)
        },
        {
          id: 12,
          label: "Slb",
          color: { background: "pink" },
          shape: "image",
          image: require(`@/assets/img/vis/aliyun.slb.png`)
        },
        {
          id: 13,
          label: "maguohao",
          color: { background: "pink" },
        },
        {
          id: 14,
          label: "test",
          color: { background: "pink" },
        },
        {
          id: 15,
          label: "ma",
          color: { background: "pink" },
        },
        {
          id: 16,
          label: "qiu",
          color: { background: "pink" },
        },
        {
          id: 17,
          label: "hu",
          color: { background: "pink" },
        },
        {
          id: 18,
          label: "maguohao7",
          color: { background: "pink" },
        },
        {
          id: 19,
          label: "test6",
          color: { background: "pink" },
        },
        {
          id: 20,
          label: "ma5",
          color: { background: "pink" },
        },
        {
          id: 21,
          label: "hu33",
          color: { background: "pink" },
        },
        {
          id: 22,
          label: "hu22",
          color: { background: "pink" },
        },
        {
          id: 23,
          label: "test6",
          color: { background: "pink" },
        },
        {
          id: 24,
          label: "ma5",
          color: { background: "pink" },
        },
        {
          id: 25,
          label: "qiu4",
          color: { background: "pink" },
        },
        {
          id: 26,
          label: "hu33",
          color: { background: "pink" },
        },
        {
          id: 27,
          label: "hu22",
          color: { background: "pink" },
        },
        {
          id: 28,
          label: "qiu41111",
          color: { background: "pink" },
        },
      ],
      //   关系线数组
      edgesArray: [
        { from: 111, to: 0, label: "类型" },
        { from: 111, to: 1, label: "类型" },
        { from: 111, to: 2, label: "类型" },
        { from: 111, to: 3, label: "类型" },
        { from: 111, to: 4, label: "类型" },
        { from: 111, to: 5, label: "类型" },
        { from: 111, to: 6, label: "类型" },
        { from: 111, to: 7, label: "类型" },
        { from: 111, to: 8, label: "类型" },
        { from: 111, to: 9, label: "类型" },
        { from: 111, to: 10, label: "类型" },
        { from: 111, to: 11, label: "类型" },
        { from: 111, to: 12, label: "类型" },
        { from: 0, to: 13, label: "资源" },
        { from: 1, to: 14, label: "资源" },
        { from: 2, to: 15, label: "资源" },
        { from: 3, to: 16, label: "资源" },
        { from: 4, to: 17, label: "资源" },
        { from: 5, to: 18, label: "资源" },
        { from: 6, to: 19, label: "资源" },
        { from: 7, to: 20, label: "资源" },
        { from: 8, to: 21, label: "资源" },
        { from: 9, to: 22, label: "资源" },
        { from: 10, to: 23, label: "资源" },
        { from: 11, to: 24, label: "资源" },
        { from: 12, to: 25, label: "资源" },
        { from: 0, to: 26, label: "资源" },
        { from: 1, to: 27, label: "资源" },
        { from: 2, to: 28, label: "资源" },
      ],
      options: {},
      data: {}
    };
  },
  methods: {
    init() {
      let _this = this;
      //1.创建一个nodes数组
      _this.nodes = new Vis.DataSet(_this.nodesArray);
      //2.创建一个edges数组
      _this.edges = new Vis.DataSet(_this.edgesArray);
      _this.container = document.getElementById("network_id");
      _this.data = {
        nodes: _this.nodes,
        edges: _this.edges
      };
      _this.options = {
        autoResize: true, //网络将自动检测其容器的大小调整，并相应地重绘自身
        locale: "cn", //语言设置：工具栏显示中文
        //设置语言
        locales: {
          cn: {
            //工具栏中文翻译
            edit: this.$t('vis.edit'),
            del: this.$t('vis.del'),
            back: this.$t('vis.back'),
            addNode: this.$t('vis.addNode'),
            addEdge: this.$t('vis.addEdge'),
            editNode: this.$t('vis.editNode'),
            editEdge: this.$t('vis.editEdge'),
            addDescription: this.$t('vis.addDescription'),
            edgeDescription: this.$t('vis.edgeDescription'),
            editEdgeDescription: this.$t('vis.editEdgeDescription'),
            createEdgeError: this.$t('vis.createEdgeError'),
            deleteClusterError: this.$t('vis.deleteClusterError'),
            editClusterError: this.$t('vis.editClusterError'),
          }
        },

        // 设置节点样式
        nodes: {
          shape: "circle",
          size: 50,
          font: {
            //字体配置
            size: 32
          },
          color: {
            // border: "#2B7CE9", //节点边框颜色
            background: "#97C2FC", //节点背景颜色
            highlight: {
              //节点选中时状态颜色
              border: "#2B7CE9",
              background: "#D2E5FF"
            },
            hover: {
              //节点鼠标滑过时状态颜色
              border: "#2B7CE9",
              background: "#D2E5FF"
            }
          },
          borderWidth: 0, //节点边框宽度，单位为px
          borderWidthSelected: 2 //节点被选中时边框的宽度，单位为px
        },
        // 边线配置
        edges: {
          width: 1,
          length: 260,
          color: {
            color: "#848484",
            highlight: "#848484",
            hover: "#848484",
            inherit: "from",
            opacity: 1.0
          },
          shadow: true,
          smooth: {
            //设置两个节点之前的连线的状态
            enabled: true //默认是true，设置为false之后，两个节点之前的连线始终为直线，不会出现贝塞尔曲线
          },
          arrows: { to: true } //箭头指向to
        },
        //计算节点之前斥力，进行自动排列的属性
        physics: {
          enabled: true, //默认是true，设置为false后，节点将不会自动改变，拖动谁谁动。不影响其他的节点
          barnesHut: {
            gravitationalConstant: -4000,
            centralGravity: 0.3,
            springLength: 120,
            springConstant: 0.04,
            damping: 0.09,
            avoidOverlap: 0
          }
        },
        //用于所有用户与网络的交互。处理鼠标和触摸事件以及导航按钮和弹出窗口
        interaction: {
          dragNodes: true, //是否能拖动节点
          dragView: true, //是否能拖动画布
          hover: true, //鼠标移过后加粗该节点和连接线
          multiselect: true, //按 ctrl 多选
          selectable: true, //是否可以点击选择
          selectConnectedEdges: true, //选择节点后是否显示连接线
          hoverConnectedEdges: true, //鼠标滑动节点后是否显示连接线
          zoomView: true //是否能缩放画布
        },
        //操作模块:包括 添加、删除、获取选中点、设置选中点、拖拽系列、点击等等
        manipulation: {
          enabled: true, //该属性表示可以编辑，出现编辑操作按钮
          addNode: true,
          addEdge: true,
          // editNode: undefined,
          editEdge: true,
          deleteNode: true,
          deleteEdge: true
        }
      };

      _this.network = new Vis.Network(
        _this.container,
        _this.data,
        _this.options
      );
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
      this.search();
    },
    search() {

    },
  },

  mounted() {
    this.init();
    // 点击事件
    this.network.on("click", params => {
      this.network.addEdgeMode();
    });
    // 点击鼠标右键事件
    this.network.on("oncontext", params => {
      this.dialogVisible = true;
    });
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

