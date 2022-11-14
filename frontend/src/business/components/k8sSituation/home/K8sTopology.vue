<template>
  <main-container class="card">
    <el-card class="table-card" v-loading="result.loading">
      <el-tabs type="border-card">
        <el-tab-pane v-if="k8sImage" :label="$t('k8s.k8s_perspective')">
          <el-row :gutter="24">
            <el-col :span="6">
              <el-card class="box-card">
                <div slot="header" class="clearfix">
                  <span style="color:red;">{{ k8sImage.images }} {{ 'Images' }}</span>
<!--                  <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>-->
                </div>
                <div v-for="o in 4" :key="o" class="text item">
                  {{'列表内容 ' + o }}
                </div>
              </el-card>
            </el-col>
            <el-col :span="15">
              <el-row :gutter="24">
                <el-card class="box-card-top">
                    <div style="float: left;color: white;margin: 11px 1%;min-width: 18%;">NameSpace<I style="color: turquoise;margin-left: 20px;">{{ k8sImage.nameSpaces }}</I></div>
                    <div style="float: left;min-width: 57%;vertical-align: middle;height: 100%;background-color: #364f6c;">
                      <el-menu class="header-menu" :unique-opened="true" mode="horizontal" default-active="1" router background-color="#364f6c;" active-text-color="red">
                        <!-- 不激活项目路由-->
                        <el-menu-item index="1" v-show="false">Placeholder</el-menu-item>
                        <el-submenu index="2" popper-class="submenu">
                          <template v-slot:title>
                          <span class="account-name" :title="currentAccount">
                            {{ $t('k8s.name') }}:  {{ currentAccount }}
                          </span>
                          </template>
                          <search-list v-if="items.length>0" :items="items" @cloudAccountSwitch="cloudAccountSwitch"/>
                        </el-submenu>
                      </el-menu>
                    </div>
                    <div style="float: right;color: white;margin: 11px 1%;min-width: 18%;">Controller<I style="color: turquoise;margin-left: 20px;">{{ k8sImage.riskController }} / {{ k8sImage.controllers }} (Reset)</I></div>
                </el-card>
              </el-row>
              <el-row :gutter="24">
                <svg id="risk-topo"></svg>
              </el-row>
            </el-col>
            <el-col :span="3">
              <el-card class="box-card-right">
                <div class="text item-left">
                  {{ $t('k8s.image_risk') }}
                </div>
              </el-card>
              <el-card class="box-card-right2 hr-card-index-1">
                <div class="text item-left">
                  {{ 'Critical: ' }} {{ k8sImage.critical }}
                </div>
              </el-card>
              <el-card class="box-card-right2 hr-card-index-2">
                <div class="text item-left">
                  {{ 'High: ' }} {{ k8sImage.high }}
                </div>
              </el-card>
              <el-card class="box-card-right2 hr-card-index-3">
                <div class="text item-left">
                  {{ 'Medium: ' }} {{ k8sImage.medium }}
                </div>
              </el-card>
              <el-card class="box-card-right2 hr-card-index-4">
                <div class="text item-left">
                  {{ 'Low: ' }} {{ k8sImage.low }}
                </div>
              </el-card>
              <el-card class="box-card-right2 hr-card-index-5">
                <div class="text item-left">
                  {{ 'Ok: ' }} {{ k8sImage.unknown }}
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>
        <el-tab-pane :label="$t('k8s.node_perspective')">
          <el-col :span="18">
            <svg id="node-topo"></svg>
          </el-col>
        </el-tab-pane>
        <el-tab-pane :label="$t('k8s.namespace_perspective')">
          <svg id="namespace-topo"></svg>
        </el-tab-pane>
        <el-tab-pane :label="$t('k8s.resource_perspective')">
          <svg id="k8s-topo"></svg>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </main-container>
</template>
<script>
import MainContainer from "../../common/components/MainContainer";
import SearchList from "@/business/components/k8sSituation/home/SearchList";
import * as d3 from 'd3';
/* eslint-disable */
export default {
  components: {
    MainContainer,
    SearchList,
    d3,
  },
  data() {
    return {
      result: {},
      cloudTopology: {},
      currentAccount: "",
      accountId: "",
      items: [],
      k8sImage: {},
    };
  },
  methods: {
    init() {
      // let data = {"name":"cloud","children":[{"name":"analytics","children":[{"name":"cluster","children":[{"name":"AgglomerativeCluster","value":3938},{"name":"CommunityStructure","value":3812},{"name":"HierarchicalCluster","value":6714},{"name":"MergeEdge","value":743}]},{"name":"graph","children":[{"name":"BetweennessCentrality","value":3534},{"name":"LinkDistance","value":5731},{"name":"MaxFlowMinCut","value":7840},{"name":"ShortestPaths","value":5914},{"name":"SpanningTree","value":3416}]},{"name":"optimization","children":[{"name":"AspectRatioBanker","value":7074}]}]},{"name":"animate","children":[{"name":"Easing","value":17010},{"name":"FunctionSequence","value":5842},{"name":"interpolate","children":[{"name":"ArrayInterpolator","value":1983},{"name":"ColorInterpolator","value":2047},{"name":"DateInterpolator","value":1375},{"name":"Interpolator","value":8746},{"name":"MatrixInterpolator","value":2202},{"name":"NumberInterpolator","value":1382},{"name":"ObjectInterpolator","value":1629},{"name":"PointInterpolator","value":1675},{"name":"RectangleInterpolator","value":2042}]},{"name":"ISchedulable","value":1041},{"name":"Parallel","value":5176},{"name":"Pause","value":449},{"name":"Scheduler","value":5593},{"name":"Sequence","value":5534},{"name":"Transition","value":9201},{"name":"Transitioner","value":19975},{"name":"TransitionEvent","value":1116},{"name":"Tween","value":6006}]},{"name":"data","children":[{"name":"converters","children":[{"name":"Converters","value":721},{"name":"DelimitedTextConverter","value":4294},{"name":"GraphMLConverter","value":9800},{"name":"IDataConverter","value":1314},{"name":"JSONConverter","value":2220}]},{"name":"DataField","value":1759},{"name":"DataSchema","value":2165},{"name":"DataSet","value":586},{"name":"DataSource","value":3331},{"name":"DataTable","value":772},{"name":"DataUtil","value":3322}]},{"name":"display","children":[{"name":"DirtySprite","value":8833},{"name":"LineSprite","value":1732},{"name":"RectSprite","value":3623},{"name":"TextSprite","value":10066}]},{"name":"flex","children":[{"name":"FlareVis","value":4116}]},{"name":"physics","children":[{"name":"DragForce","value":1082},{"name":"GravityForce","value":1336},{"name":"IForce","value":319},{"name":"NBodyForce","value":10498},{"name":"Particle","value":2822},{"name":"Simulation","value":9983},{"name":"Spring","value":2213},{"name":"SpringForce","value":1681}]},{"name":"query","children":[{"name":"AggregateExpression","value":1616},{"name":"And","value":1027},{"name":"Arithmetic","value":3891},{"name":"Average","value":891},{"name":"BinaryExpression","value":2893},{"name":"Comparison","value":5103},{"name":"CompositeExpression","value":3677},{"name":"Count","value":781},{"name":"DateUtil","value":4141},{"name":"Distinct","value":933},{"name":"Expression","value":5130},{"name":"ExpressionIterator","value":3617},{"name":"Fn","value":3240},{"name":"If","value":2732},{"name":"IsA","value":2039},{"name":"Literal","value":1214},{"name":"Match","value":3748},{"name":"Maximum","value":843},{"name":"methods","children":[{"name":"add","value":593},{"name":"and","value":330},{"name":"average","value":287},{"name":"count","value":277},{"name":"distinct","value":292},{"name":"div","value":595},{"name":"eq","value":594},{"name":"fn","value":460},{"name":"gt","value":603},{"name":"gte","value":625},{"name":"iff","value":748},{"name":"isa","value":461},{"name":"lt","value":597},{"name":"lte","value":619},{"name":"max","value":283},{"name":"min","value":283},{"name":"mod","value":591},{"name":"mul","value":603},{"name":"neq","value":599},{"name":"not","value":386},{"name":"or","value":323},{"name":"orderby","value":307},{"name":"range","value":772},{"name":"select","value":296},{"name":"stddev","value":363},{"name":"sub","value":600},{"name":"sum","value":280},{"name":"update","value":307},{"name":"variance","value":335},{"name":"where","value":299},{"name":"xor","value":354},{"name":"_","value":264}]},{"name":"Minimum","value":843},{"name":"Not","value":1554},{"name":"Or","value":970},{"name":"Query","value":13896},{"name":"Range","value":1594},{"name":"StringUtil","value":4130},{"name":"Sum","value":791},{"name":"Variable","value":1124},{"name":"Variance","value":1876},{"name":"Xor","value":1101}]},{"name":"scale","children":[{"name":"IScaleMap","value":2105},{"name":"LinearScale","value":1316},{"name":"LogScale","value":3151},{"name":"OrdinalScale","value":3770},{"name":"QuantileScale","value":2435},{"name":"QuantitativeScale","value":4839},{"name":"RootScale","value":1756},{"name":"Scale","value":4268},{"name":"ScaleType","value":1821},{"name":"TimeScale","value":5833}]},{"name":"util","children":[{"name":"Arrays","value":8258},{"name":"Colors","value":10001},{"name":"Dates","value":8217},{"name":"Displays","value":12555},{"name":"Filter","value":2324},{"name":"Geometry","value":10993},{"name":"heap","children":[{"name":"FibonacciHeap","value":9354},{"name":"HeapNode","value":1233}]},{"name":"IEvaluable","value":335},{"name":"IPredicate","value":383},{"name":"IValueProxy","value":874},{"name":"math","children":[{"name":"DenseMatrix","value":3165},{"name":"IMatrix","value":2815},{"name":"SparseMatrix","value":3366}]},{"name":"Maths","value":17705},{"name":"Orientation","value":1486},{"name":"palette","children":[{"name":"ColorPalette","value":6367},{"name":"Palette","value":1229},{"name":"ShapePalette","value":2059},{"name":"SizePalette","value":2291}]},{"name":"Property","value":5559},{"name":"Shapes","value":19118},{"name":"Sort","value":6887},{"name":"Stats","value":6557},{"name":"Strings","value":22026}]},{"name":"vis","children":[{"name":"axis","children":[{"name":"Axes","value":1302},{"name":"Axis","value":24593},{"name":"AxisGridLine","value":652},{"name":"AxisLabel","value":636},{"name":"CartesianAxes","value":6703}]},{"name":"controls","children":[{"name":"AnchorControl","value":2138},{"name":"ClickControl","value":3824},{"name":"Control","value":1353},{"name":"ControlList","value":4665},{"name":"DragControl","value":2649},{"name":"ExpandControl","value":2832},{"name":"HoverControl","value":4896},{"name":"IControl","value":763},{"name":"PanZoomControl","value":5222},{"name":"SelectionControl","value":7862},{"name":"TooltipControl","value":8435}]},{"name":"data","children":[{"name":"Data","value":20544},{"name":"DataList","value":19788},{"name":"DataSprite","value":10349},{"name":"EdgeSprite","value":3301},{"name":"NodeSprite","value":19382},{"name":"render","children":[{"name":"ArrowType","value":698},{"name":"EdgeRenderer","value":5569},{"name":"IRenderer","value":353},{"name":"ShapeRenderer","value":2247}]},{"name":"ScaleBinding","value":11275},{"name":"Tree","value":7147},{"name":"TreeBuilder","value":9930}]},{"name":"events","children":[{"name":"DataEvent","value":2313},{"name":"SelectionEvent","value":1880},{"name":"TooltipEvent","value":1701},{"name":"VisualizationEvent","value":1117}]},{"name":"legend","children":[{"name":"Legend","value":20859},{"name":"LegendItem","value":4614},{"name":"LegendRange","value":10530}]},{"name":"operator","children":[{"name":"distortion","children":[{"name":"BifocalDistortion","value":4461},{"name":"Distortion","value":6314},{"name":"FisheyeDistortion","value":3444}]},{"name":"encoder","children":[{"name":"ColorEncoder","value":3179},{"name":"Encoder","value":4060},{"name":"PropertyEncoder","value":4138},{"name":"ShapeEncoder","value":1690},{"name":"SizeEncoder","value":1830}]},{"name":"filter","children":[{"name":"FisheyeTreeFilter","value":5219},{"name":"GraphDistanceFilter","value":3165},{"name":"VisibilityFilter","value":3509}]},{"name":"IOperator","value":1286},{"name":"label","children":[{"name":"Labeler","value":9956},{"name":"RadialLabeler","value":3899},{"name":"StackedAreaLabeler","value":3202}]},{"name":"layout","children":[{"name":"AxisLayout","value":6725},{"name":"BundledEdgeRouter","value":3727},{"name":"CircleLayout","value":9317},{"name":"CirclePackingLayout","value":12003},{"name":"DendrogramLayout","value":4853},{"name":"ForceDirectedLayout","value":8411},{"name":"IcicleTreeLayout","value":4864},{"name":"IndentedTreeLayout","value":3174},{"name":"Layout","value":7881},{"name":"NodeLinkTreeLayout","value":12870},{"name":"PieLayout","value":2728},{"name":"RadialTreeLayout","value":12348},{"name":"RandomLayout","value":870},{"name":"StackedAreaLayout","value":9121},{"name":"TreeMapLayout","value":9191}]},{"name":"Operator","value":2490},{"name":"OperatorList","value":5248},{"name":"OperatorSequence","value":4190},{"name":"OperatorSwitch","value":2581},{"name":"SortOperator","value":2023}]},{"name":"Visualization","value":16540}]}]};
      this.result = this.$get("/k8s/nodeTopology", response => {
        let data = response.data;

        let width = 932, height = width;

        let color = d3.scaleLinear()
          .domain([0, 5])
          .range(["hsl(152,80%,80%)", "hsl(228,30%,40%)"])
          .interpolate(d3.interpolateHcl);

        let pack = data => d3.pack()
          .size([width, height])
          .padding(3)
          (d3.hierarchy(data)
            .sum(d => d.value)
            .sort((a, b) => b.value - a.value));

        const root = pack(data);
        let focus = root;
        let view;

        const svg = d3.select("#node-topo")
          .attr("viewBox", `-${width / 2} -${height / 2} ${width} ${height}`)
          .style("display", "block")
          .style("margin", "0 -14px")
          .style("background", color(0))
          .style("cursor", "pointer")
          .on("click", (event) => zoom(event, root));

        const node = svg.append("g")
          .selectAll("circle")
          .data(root.descendants().slice(1))
          .join("circle")
          .attr("fill", d => d.children ? color(d.depth) : (d.value!=1?"AliceBlue":"Aquamarine"))
          .attr("pointer-events", d => !d.children ? "none" : null)
          .on("mouseover", function() { d3.select(this).attr("stroke", "#000"); })
          .on("mouseout", function() { d3.select(this).attr("stroke", null); })
          .on("click", (event, d) => {
            if(focus !== d && d.parent !== focus && d.depth === 2) {
              this.open(d);
            }
            focus !== d && (zoom(event, d), event.stopPropagation());
          });

        const label = svg.append("g")
          .style("font", "10px sans-serif")
          .attr("pointer-events", "none")
          .attr("text-anchor", "middle")
          .selectAll("text")
          .data(root.descendants())
          .join("text")
          .style("fill-opacity", d => d.parent === root ? 1 : 0)
          .style("display", d => d.parent === root ? "inline-block" : "none")
          .text(d => d.data.name);

        zoomTo([root.x, root.y, root.r * 2]);

        function zoomTo(v) {
          const k = width / v[2];

          view = v;

          label.attr("transform", d => `translate(${(d.x - v[0]) * k},${(d.y - v[1]) * k})`);
          node.attr("transform", d => `translate(${(d.x - v[0]) * k},${(d.y - v[1]) * k})`);
          node.attr("r", d => d.r * k);
        }

        function zoom(event, d) {
          const focus0 = focus;

          focus = d;

          const transition = svg.transition()
            .duration(event.altKey ? 7500 : 750)
            .tween("zoom", d => {
              const i = d3.interpolateZoom(view, [focus.x, focus.y, focus.r * 2]);
              return t => zoomTo(i(t));
            });

          label
            .filter(function(d) { return d.parent === focus || this.style.display === "inline-block"; })
            .transition(transition)
            .style("fill-opacity", d => d.parent === focus ? 1 : 0)
            .on("start", function(d) { if (d.parent === focus) this.style.display = "inline-block"; })
            .on("end", function(d) { if (d.parent !== focus) this.style.display = "none"; });
        }

        return svg.node();
      });
      this.result = this.$get("/k8s/namespaceTopology", response => {
        let data = response.data;

        let width = 932, height = width;

        let color = d3.scaleLinear()
          .domain([0, 5])
          .range(["hsl(152,80%,80%)", "hsl(228,30%,40%)"])
          .interpolate(d3.interpolateHcl);

        let pack = data => d3.pack()
          .size([width, height])
          .padding(3)
          (d3.hierarchy(data)
            .sum(d => d.value)
            .sort((a, b) => b.value - a.value));

        const root = pack(data);
        let focus = root;
        let view;

        const svg = d3.select("#namespace-topo")
          .attr("viewBox", `-${width / 2} -${height / 2} ${width} ${height}`)
          .style("display", "block")
          .style("margin", "0 -14px")
          .style("background", color(0))
          .style("cursor", "pointer")
          .on("click", (event) => zoom(event, root));

        const node = svg.append("g")
          .selectAll("circle")
          .data(root.descendants().slice(1))
          .join("circle")
          .attr("fill", d => d.children ? color(d.depth) : (d.value!=1?"AliceBlue":"Aquamarine"))
          .attr("pointer-events", d => !d.children ? "none" : null)
          .on("mouseover", function() { d3.select(this).attr("stroke", "#000"); })
          .on("mouseout", function() { d3.select(this).attr("stroke", null); })
          .on("click", (event, d) => {
            if(focus !== d && d.parent !== focus && d.depth === 2) {
              this.open(d);
            }
            focus !== d && (zoom(event, d), event.stopPropagation());
          });

        const label = svg.append("g")
          .style("font", "10px sans-serif")
          .attr("pointer-events", "none")
          .attr("text-anchor", "middle")
          .selectAll("text")
          .data(root.descendants())
          .join("text")
          .style("fill-opacity", d => d.parent === root ? 1 : 0)
          .style("display", d => d.parent === root ? "inline-block" : "none")
          .text(d => d.data.name);

        zoomTo([root.x, root.y, root.r * 2]);

        function zoomTo(v) {
          const k = width / v[2];

          view = v;

          label.attr("transform", d => `translate(${(d.x - v[0]) * k},${(d.y - v[1]) * k})`);
          node.attr("transform", d => `translate(${(d.x - v[0]) * k},${(d.y - v[1]) * k})`);
          node.attr("r", d => d.r * k);
        }

        function zoom(event, d) {
          const focus0 = focus;

          focus = d;

          const transition = svg.transition()
            .duration(event.altKey ? 7500 : 750)
            .tween("zoom", d => {
              const i = d3.interpolateZoom(view, [focus.x, focus.y, focus.r * 2]);
              return t => zoomTo(i(t));
            });

          label
            .filter(function(d) { return d.parent === focus || this.style.display === "inline-block"; })
            .transition(transition)
            .style("fill-opacity", d => d.parent === focus ? 1 : 0)
            .on("start", function(d) { if (d.parent === focus) this.style.display = "inline-block"; })
            .on("end", function(d) { if (d.parent !== focus) this.style.display = "none"; });
        }

        return svg.node();
      });
      this.result = this.$get("/k8s/k8sTopology", response => {
        let data = response.data;

        let width = 932, height = width;

        let color = d3.scaleLinear()
          .domain([0, 5])
          .range(["hsl(152,80%,80%)", "hsl(228,30%,40%)"])
          .interpolate(d3.interpolateHcl);

        let pack = data => d3.pack()
          .size([width, height])
          .padding(3)
          (d3.hierarchy(data)
            .sum(d => d.value)
            .sort((a, b) => b.value - a.value));

        const root = pack(data);
        let focus = root;
        let view;

        const svg = d3.select("#k8s-topo")
          .attr("viewBox", `-${width / 2} -${height / 2} ${width} ${height}`)
          .style("display", "block")
          .style("margin", "0 -14px")
          .style("background", color(0))
          .style("cursor", "pointer")
          .on("click", (event) => zoom(event, root));

        const node = svg.append("g")
          .selectAll("circle")
          .data(root.descendants().slice(1))
          .join("circle")
          .attr("fill", d => d.children ? color(d.depth) : (d.value!=1?"AliceBlue":"Aquamarine"))
          .attr("pointer-events", d => !d.children ? "none" : null)
          .on("mouseover", function() { d3.select(this).attr("stroke", "#000"); })
          .on("mouseout", function() { d3.select(this).attr("stroke", null); })
          .on("click", (event, d) => {
            if(focus !== d && d.parent !== focus && d.depth === 2) {
              this.open(d);
            }
            focus !== d && (zoom(event, d), event.stopPropagation());
          });

        const label = svg.append("g")
          .style("font", "10px sans-serif")
          .attr("pointer-events", "none")
          .attr("text-anchor", "middle")
          .selectAll("text")
          .data(root.descendants())
          .join("text")
          .style("fill-opacity", d => d.parent === root ? 1 : 0)
          .style("display", d => d.parent === root ? "inline-block" : "none")
          .text(d => d.data.name);

        zoomTo([root.x, root.y, root.r * 2]);

        function zoomTo(v) {
          const k = width / v[2];

          view = v;

          label.attr("transform", d => `translate(${(d.x - v[0]) * k},${(d.y - v[1]) * k})`);
          node.attr("transform", d => `translate(${(d.x - v[0]) * k},${(d.y - v[1]) * k})`);
          node.attr("r", d => d.r * k);
        }

        function zoom(event, d) {
          const focus0 = focus;

          focus = d;

          const transition = svg.transition()
            .duration(event.altKey ? 7500 : 750)
            .tween("zoom", d => {
              const i = d3.interpolateZoom(view, [focus.x, focus.y, focus.r * 2]);
              return t => zoomTo(i(t));
            });

          label
            .filter(function(d) { return d.parent === focus || this.style.display === "inline-block"; })
            .transition(transition)
            .style("fill-opacity", d => d.parent === focus ? 1 : 0)
            .on("start", function(d) { if (d.parent === focus) this.style.display = "inline-block"; })
            .on("end", function(d) { if (d.parent !== focus) this.style.display = "none"; });
        }

        return svg.node();
      });
      this.result = this.$get("/k8s/riskTopology/" + this.accountId, response => {
        let data = response.data;

        let width = 932, height = width;

        let color = d3.scaleLinear()
          .domain([0, 5])
          .range(["hsl(152,80%,80%)", "hsl(228,30%,40%)"])
          .interpolate(d3.interpolateHcl);

        let pack = data => d3.pack()
          .size([width, height])
          .padding(3)
          (d3.hierarchy(data)
            .sum(d => d.value)
            .sort((a, b) => b.value - a.value));

        const root = pack(data);
        let focus = root;
        let view;

        const svg = d3.select("#risk-topo")
          .attr("viewBox", `-${width / 2} -${height / 2} ${width} ${height}`)
          .style("display", "block")
          .style("margin", "0 -14px")
          .style("background", color(0))
          .style("cursor", "pointer")
          .on("click", (event) => zoom(event, root));

        const node = svg.append("g")
          .selectAll("circle")
          .data(root.descendants().slice(1))
          .join("circle")
          .attr("fill", d => d.children ? color(d.depth) : (d.risk>0?"#e69147":"#FFFFFF"))
          .attr("pointer-events", d => !d.children ? "none" : null)
          .on("mouseover", function() { d3.select(this).attr("stroke", "#000"); })
          .on("mouseout", function() { d3.select(this).attr("stroke", null); })
          .on("click", (event, d) => {
            if(focus !== d && d.parent !== focus && d.depth === 2) {
              this.open(d);
            }
            focus !== d && (zoom(event, d), event.stopPropagation());
          });

        const label = svg.append("g")
          .style("font", "10px sans-serif")
          .attr("pointer-events", "none")
          .attr("text-anchor", "middle")
          .selectAll("text")
          .data(root.descendants())
          .join("text")
          .style("fill-opacity", d => d.parent === root ? 1 : 0)
          .style("display", d => d.parent === root ? "inline-block" : "none")
          .text(d => d.data.name);

        zoomTo([root.x, root.y, root.r * 2]);

        function zoomTo(v) {
          const k = width / v[2];

          view = v;

          label.attr("transform", d => `translate(${(d.x - v[0]) * k},${(d.y - v[1]) * k})`);
          node.attr("transform", d => `translate(${(d.x - v[0]) * k},${(d.y - v[1]) * k})`);
          node.attr("r", d => d.r * k);
        }

        function zoom(event, d) {
          const focus0 = focus;

          focus = d;

          const transition = svg.transition()
            .duration(event.altKey ? 7500 : 750)
            .tween("zoom", d => {
              const i = d3.interpolateZoom(view, [focus.x, focus.y, focus.r * 2]);
              return t => zoomTo(i(t));
            });

          label
            .filter(function(d) { return d.parent === focus || this.style.display === "inline-block"; })
            .transition(transition)
            .style("fill-opacity", d => d.parent === focus ? 1 : 0)
            .on("start", function(d) { if (d.parent === focus) this.style.display = "inline-block"; })
            .on("end", function(d) { if (d.parent !== focus) this.style.display = "none"; });
        }

        return svg.node();
      });

      this.result = this.$get("/k8s/getImage/" + this.accountId, response => {
        this.k8sImage = response.data;
      });

    },
    open(node) {
      let childrens = node.children;
      let message = '<table><tr style="background-color: thistle;width: 120px;"><th>命名空间</th><th>资源详情</th></tr>';
      for (let children of childrens) {
        let tr = '<tr style="background-color: #9ec1e5;width: 120px;"><td>' + (children.data.namespace?children.data.namespace:'N/A') + '</td><td>' + children.data.name + '</td></tr>';
        message += tr;
      }
      message+='</table>';
      this.$notify.success({
        title: node.data.name,
        message: message,
        dangerouslyUseHTMLString: true,
      });
    },
    initK8s() {
      this.$get("/k8s/allList", response => {
        this.items = response.data;
        this.accountId = this.items[0].id;
        this.currentAccount = this.items[0].name;
        this.init();
      })
    },
    cloudAccountSwitch(accountId, accountName) {
      this.accountId = accountId;
      this.currentAccount = accountName;
      this.init();
    },
  },

  mounted() {
    this.initK8s();
  }

}
</script>

<style scoped>
svg {
  margin: 25px;
}
.table-card >>> .el-card__body {
  padding: 0;
}
.table-card >>> .el-tabs__content{
  padding: 0;
}
.text {
  font-size: 14px;
}
.item {
  margin-bottom: 18px;
}
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}
.box-card {
  width: 80%;
  margin: 10%;
  background-color: #364f6c;
  color: #FFFFFF;
  height: calc(100vh - 200px);
  border-radius: 10px;
}
.box-card-top {
  width: 100%;
  background-color: #364f6c;
  color: #FFFFFF;
  border-radius: 10px;
  margin-top: 3%;
}
.box-card-right {
  width: 80%;
  background-color: #9ec1e5;
  color: #000000;
  margin: 20% 5% 1px 5%;
}
.box-card-right2 {
  width: 80%;
  background-color: #364f6c;
  color: #FFFFFF;
  border-radius: 3px;
  margin: 0 5% 0 5%;
}
.header-menu {
  background-color: #364f6c;
  color: #FFFFFF;
}
.account-name {
  color: #FFFFFF;
}
.item-left {
  padding-left: 5px;
}
.table-card >>> .el-tabs__content {
  background-color: hsl(152,80%,80%);
}
.el-col {
  border-radius: 4px;
}
.bg-purple-dark {
  background: hsl(152,80%,80%);
}
.bg-purple {
  background: hsl(152,80%,80%);
}
.bg-purple-light {
  background: #364f6c;
  margin: 1%;
  display: table-cell;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
}

.hr-card-index-1 {
  border-left-color: #8B0000;
  border-left-width: 5px;
}

.hr-card-index-2 {
  border-left-color: #FF4D4D;
  border-left-width: 5px;
}
.hr-card-index-3 {
  border-left-color: #FF8000;
  border-left-width: 5px;
}
.hr-card-index-4 {
  border-left-color: #eeab80;
  border-left-width: 5px;
}
.hr-card-index-5 {
  border-left-color: #67C23A;
  border-left-width: 5px;
}
</style>
