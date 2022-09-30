<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <el-menu class="header-menu" :unique-opened="true" mode="horizontal" default-active="1" router background-color="aliceblue" active-text-color="red">
        <!-- 不激活项目路由-->
        <el-menu-item index="1" v-show="false">Placeholder</el-menu-item>
        <el-submenu index="2" popper-class="submenu">
          <template v-slot:title>
        <span class="account-name" :title="currentAccount" style="width: 250px;">
          {{ $t('account.cloud_account') }}: {{ currentAccount }}
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
import {ACCOUNT_NAME} from "../../../../common/js/constants";

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
      nodes: [],
      edges: [],
      // network:null,
      container: null,
      //   节点数组
      nodesArray: [
        {
          id: 0,
          label: "大前端",
          color: { background: "yellow" }
        },
        {
          id: 1,
          label: "HTML",
          color: { background: "pink" }
        },
        {
          id: 2,
          label: "JavaScript",
          color: { background: "pink" }
        },
        {
          id: 3,
          label: "CSS",
          color: { background: "pink" }
        },
        {
          id: 4,
          label: "三大主流框架",
          color: { background: "pink" }
        },
        {
          id: 5,
          label: "vue.js",
          color: { background: "pink" }
        },
        {
          id: 6,
          label: "react.js",
          color: { background: "pink" }
        },
        {
          id: 7,
          label: "angular.js",
          color: { background: "pink" }
        }
      ],
      //   关系线数组
      edgesArray: [
        { from: 0, to: 1, label: "ddd" },
        { from: 1, to: 0, label: "aaa" },
        { from: 0, to: 2, label: "step1" },
        { from: 0, to: 3, label: "step1" },
        { from: 0, to: 4, label: "step1" },
        { from: 4, to: 5, label: "step2" },
        { from: 4, to: 6, label: "step2" },
        { from: 4, to: 7, label: "step2" }
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
    cloudAccountSwitch(accountId) {
      this.accountId = accountId;
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

