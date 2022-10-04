<template>
  <div>
    <!--width,height 画布的宽度，高度。 可以是百分比或像素，一般在dom元素上设置 -->
    <div id="network_id9" class="network" style="height:80vh"></div>
  </div>
</template>

<script>
import Vis from "vis";
/* eslint-disable */
export default {
  name: "K8s",
  components: {
    Vis,
  },
  props: {
    k8sLink: [],
    edgesBelong: []
  },
  watch:{
    //监听，父页面值变化，子页面重新刷新init
    k8sNameSpace(){
      this.init();
    }
  },
  data() {
    return {
      nodes: [],
      edges: [],
      network:null,
      container: null,
      //   节点数组
      nodesArray: [],
      //   关系线数组
      edgesArray: [],
      options: {},
      data: {},
    }
  },
  methods: {
    init() {
      this.nodesArray = [
        {
          id: 1,
          label: "Secret",
          shape: "image",
          image: require(`@/assets/img/vis/k8s/secret.png`)
        },
      ];
      this.nodesArray = this.nodesArray.concat(this.k8sLink);
      this.edgesArray = this.edgesBelong;
      let _this = this;
      //1.创建一个nodes数组
      _this.nodes = new Vis.DataSet(_this.nodesArray);
      //2.创建一个edges数组
      _this.edges = new Vis.DataSet(_this.edgesArray);
      _this.container = document.getElementById("network_id9");
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
          size: 16,
          font: {
            //字体配置
            size: 10
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
          enabled: false, //该属性表示可以编辑，出现编辑操作按钮
          addNode: false,
          addEdge: false,
          // editNode: undefined,
          editEdge: false,
          deleteNode: false,
          deleteEdge: false
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
  },
  mounted() {
    this.init();
  },
}
</script>

<style scoped>

</style>

