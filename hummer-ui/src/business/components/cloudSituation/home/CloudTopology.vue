<template>
  <main-container>
    <el-card class="table-card el-row-card">
      <topo-switch @selectAccount="selectAccount"/>
    </el-card>

    <el-card class="table-card" v-loading="result.loading">
      <svg id="cloud-topo"></svg>
    </el-card>
    <el-drawer class="rtl" :title="$t('resource.cloud_resource_detail')" :visible.sync="dialogVisible" size="75%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="viewResult.loading">
      <el-descriptions class="margin-top desc-top" title="" :column="2" border>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-paperclip"></i>
            {{ $t('resource.Hummer_ID') }}
          </template>
          {{ details.id }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-tickets"></i>
            {{ $t('dashboard.resource_name') }}
          </template>
          {{ details.name }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-cloudy"></i>
            {{ $t('account.cloud_platform') }}
          </template>
          {{ details.pluginName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-house"></i>
            {{ $t('account.cloud_account') }}
          </template>
          {{ details.accountName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-location-information"></i>
            {{ $t('account.regions') }}
          </template>
          {{ details.regionName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-location-information"></i>
            {{ $t('dashboard.resource_type') }}
          </template>
          {{ details.resourceType }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-collection-tag"></i>
            {{ $t('resource.risk') }}
          </template>
          <span v-if="details.riskType === 'risk'" style="color: red;">{{ $t('resource.have_risk') }}</span>
          <span v-if="details.riskType === 'normal'" style="color: green;">{{ $t('resource.n_risk') }}</span>
          <span v-if="details.riskType === 'uncheck'">{{ $t('resource.uncheck') }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <el-table v-if="details.riskType === 'risk'" :border="true" :stripe="true" :data="string2PrettyFormat" class="adjust-table table-content">
        <el-table-column v-slot:default="scope" :label="$t('rule.rule_name')" min-width="40%" show-overflow-tooltip>
          <el-link type="primary" :underline="false" class="md-primary text-click">
            {{ scope.row.taskName }}
          </el-link>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('rule.severity')" min-width="20%"
                         prop="severity" :sortable="true"
                         show-overflow-tooltip>
          <span v-if="scope.row.severity == 'HighRisk'" style="color: #f84846;"> {{ $t('rule.HighRisk') }}</span>
          <span v-else-if="scope.row.severity == 'MediumRisk'" style="color: #fe9636;">{{ $t('rule.MediumRisk') }}</span>
          <span v-else-if="scope.row.severity == 'LowRisk'" style="color: #4dabef;"> {{ $t('rule.LowRisk') }}</span>
          <span v-else> N/A</span>
        </el-table-column>

        <el-table-column v-slot:default="scope" :label="$t('resource.status_on_off')" prop="returnSum" sortable
                         show-overflow-tooltip min-width="20%">
          <span style="color: #f84846;">{{ $t('resource.i18n_compliance_false') }}</span>
        </el-table-column>
      </el-table>
      <cloud-detail-chart v-if="resources && supportPlugins.includes(details.pluginId)" :resourceItemId="details.resource"/>
      <div class="desc-top" v-if="resources && resources != '{}'">
        <el-divider><i class="el-icon-folder-opened"></i></el-divider>
        <result-read-only :row="typeof(resources) === 'string'?JSON.parse(resources):resources"></result-read-only>
        <el-divider><i class="el-icon-document-checked"></i></el-divider>
      </div>
    </el-drawer>
  </main-container>
</template>
<script>
import MainContainer from "../../common/components/MainContainer";
import CloudDetailChart from "@/business/components/cloudSituation/head/CloudDetailChart";
import ResultReadOnly from "@/business/components/cloudSituation/head/ResultReadOnly";
import TopoSwitch from "@/business/components/cloudSituation/head/TopoSwitch";
import * as d3 from 'd3';
import * as math from 'mathjs';
import {cloudTopologyByAccountIdUrl, cloudTopologyUrl} from "@/api/cloud/sync/sync";
import {cloudResourceByIdUrl, resourceRiskListUrl} from "@/api/cloud/account/account";
/* eslint-disable */
const width = 1600;
const height = 1100;
const cellSize = 30;
const iconSize = 12;
let cellCountX = 30;
let cellCountY = 30;
let clicked = false;
const iconData = [
  {"name": "hummer-aws-plugin", "file": "aws.svg"},
  {"name": "hummer-aliyun-plugin", "file": "aliyun.svg"},
  {"name": "hummer-huawei-plugin", "file": "huawei.svg"},
  {"name": "hummer-gcp-plugin", "file": "gcp.svg"},
  {"name": "hummer-qcloud-plugin", "file": "tengxun.svg"},
  {"name": "hummer-azure-plugin", "file": "azure.svg"},
  {"name": "hummer-baidu-plugin", "file": "baiduyun.png"},
  {"name": "hummer-huoshan-plugin", "file": "huoshan.svg"},
  {"name": "hummer-jdcloud-plugin", "file": "jdcloud2.png"},
  {"name": "hummer-ksyun-plugin", "file": "jinshanyun.png"},
  {"name": "hummer-openstack-plugin", "file": "openstack.png"},
  {"name": "hummer-qingcloud-plugin", "file": "qingcloud2.png"},
  {"name": "hummer-qiniu-plugin", "file": "qiniu2.png"},
  {"name": "hummer-ucloud-plugin", "file": "ucloud.png"},
  {"name": "hummer-vsphere-plugin", "file": "vsphere.png"},
]
export default {
  components: {
    MainContainer,
    d3,
    CloudDetailChart,
    ResultReadOnly,
    TopoSwitch,
  },
  data() {
    return {
      result: {},
      viewResult: {},
      cloudTopology: {},
      dialogVisible: false,
      direction: 'rtl',
      accountId: 'all',
      accountName: '',
      details: {
        id: '',
        name: '',
        pluginId: '',
        pluginName: '',
        accountId: '',
        accountName: '',
        regionId: '',
        regionName: '',
        resourceType: '',
        riskType: '',
        type: '',
        resource: '',
      },
      resources: '{}',//resource json : {"Logging":{},"CreationDate":"2023-02-02T02:25:28+00:00","Versioning":{"Status":"Enabled"},"Acl":{"Owner":{"ID":"06ef6af1f3cd38ee2235066e84f042c4c2651d1549a8b2e4cad047a3395a955c"},"Grants":[{"Grantee":{"Type":"CanonicalUser","ID":"06ef6af1f3cd38ee2235066e84f042c4c2651d1549a8b2e4cad047a3395a955c"},"Permission":"FULL_CONTROL"}]},"Tags":[],"Notification":{},"Name":"hummerrisk-package","Location":{"LocationConstraint":"ap-east-1"}},
      supportPlugins: ['hummer-aws-plugin', 'hummer-aliyun-plugin', 'hummer-huawei-plugin', 'hummer-qcloud-plugin'],
      string2PrettyFormat: [],
    };
  },
  methods: {
    //初始化
    init() {

      this.result = this.$get(cloudTopologyByAccountIdUrl + this.accountId, response => {

        let data = response.data;
        this.clearSVG()
        if (data.children.length == 0) {
          this.initSVG([]);
          return;
        }

        this.calculatePlatform(data.children);

        this.initSVG(data.children);

      });

    },
    clearSVG(){
      d3.selectAll("#cloud-topo > *").remove();
    },
    //初始化svg
    initSVG(cloudData) {

      const svg = d3.select("#cloud-topo").style("width", this.width + 'px')
        .style("height", this.height + 'px');
      const stage = this.createStage(d3, svg, cellSize, cellCountX, cellCountY);
      const eventCapture = stage.append('rect')
        .attr('width', width * 2)
        .attr('height', height * 2)
        .attr('x', -width)
        .attr('y', -height)
        .style('fill', 'none')
        .style('pointer-events', 'all');
      const container = stage.append('g').attr('class', 'container');

      // zooming
      const zoom = d3.zoom()
        .scaleExtent([1, 10])
        .on('zoom', (event, d) => {
          container.attr('transform', event.transform);
        });
      svg.call(zoom);


      const drawPolygon = this._drawPolygon();
      const drawSquare = this._drawSquare(drawPolygon);
      const drawGrid = this._drawGrid(drawSquare);
      const toRadians = this._toRadians();
      const toIsoTransform = this._toIsoTransform(toRadians, math)
      const fromIsoTransform = this._fromIsoTransform(toRadians, math)
      const isoTransform = toIsoTransform(30);
      const isoInverseTransform = fromIsoTransform(30);
      const makeCoordRoundTransform = this._makeCoordRoundTransform()
      const snapToHalf = makeCoordRoundTransform(cellSize / 2);
      const snapToQuarter = makeCoordRoundTransform(cellSize / 5);
      const isoGrid = drawGrid(container, cellSize, cellCountX, cellCountY, isoTransform);
      if(cloudData.length==0)
        return;
      const floorData = []
      const boxImageData = []
      const regionData = []
      const resourceTypeData = []
      const textData = []
      this.initData(cloudData, floorData, regionData, resourceTypeData, boxImageData, textData)

      const getShapeOrigin = (d) => [d.position[0] * cellSize, d.position[1] * cellSize];
      const getIsoShapeOrigin = (d) => isoTransform([d.position[0] * cellSize, d.position[1] * cellSize]);


      //plot moveables

      let replot = this._replot();
      replot(floorData, regionData, resourceTypeData, boxImageData, textData, isoGrid, isoTransform)

      const getIsoBoxOrigin = (d) => this.isoTransform([d.position[0] * cellSize, d.position[1] * cellSize]);

      // const getTextOrigin = (d) => [d.position[0] * cellSize, d.position[1] * cellSize];
      const getIsoTextOrigin = (d) => this.isoTransform([d.position[0] * cellSize, d.position[1] * cellSize]);


    },
    // 更新画布元素
    _replot() {
      return (floorData, regionData, resourceTypeData, boxImageData, textData, isoGrid, isoTransform) => {
        // boxes
        let isoBoxes;

        //floor
        let isoFloors;
        let isoDashedBox;

        // texts
        let texts;
        let isoTexts;

        let plotFloor = this._plotFloor(d3, cellSize)
        let plotImageBoxes = this._plotImageBoxes(d3, cellSize)
        let plotTexts = this._plotTexts(cellSize, d3)
        let plotDashedBox = this._plotDashedBox(d3, cellSize)
        let plotRcBox = this._plotRcBox(d3, cellSize)
        let plotIcons = this._plotIcon(cellSize, d3)

        isoFloors = plotFloor(floorData, isoGrid, isoTransform, plotIcons)

        isoDashedBox = plotDashedBox(regionData, isoGrid, isoTransform)
        isoDashedBox = plotRcBox(resourceTypeData, isoGrid, isoTransform)

        isoBoxes = plotImageBoxes(boxImageData, isoGrid, isoTransform);

        // text
        isoTexts = plotTexts(textData, isoGrid, isoTransform, 'matrix(0.707 0.409 -0.707 0.409 0 0)');
      }
    },
    //处理主svg的viewbox
    createStage(d3, svg, cellSize, cellCountX, cellCountY) {
      return svg.attr('viewBox', [
        -cellSize * cellCountX, 0,
        cellSize * cellCountY * 2, cellSize * cellCountX
      ].join(' '));
    },
    // 坐标取整处理，处理坐标移动时候的数值计算
    _makeCoordRoundTransform() {
      return (
        (roundBy) => {
          return (coords) => [
            Math.round(coords[0] / roundBy) * roundBy,
            Math.round(coords[1] / roundBy) * roundBy
          ];
        }
      )
    },
    // 从isometric转平面时的坐标转换
    _fromIsoTransform(toRadians, math) {
      return (
        (degrees) => {
          const rads = toRadians(degrees);
          const matrices = {
            inverse: [
              [math.tan(rads), 1, 0],
              [-math.tan(rads), 1, 0],
              [0, 0, 1]
            ]
          };
          return (coords) => {
            const product = math.multiply(matrices.inverse, [[coords[0]], [coords[1]], [1]]);
            return [product[0][0], product[1][0]];
          };
        }
      )
    },
    //在获取到的云账号数据上，计算出每个元素的相对坐标位置，并更新到对象中。
    calculatePlatform(cloudaccounts) {
      var totalNumber = 0;
      var position0 = [1, 1]

      for (var i = 0; i < cloudaccounts.length; i++) {
        totalNumber = cloudaccounts[i].total + totalNumber;
        let regionList = (!cloudaccounts[i].children)?[]:cloudaccounts[i].children;
        let regionListN = []
        regionList.forEach((d, rindex) => {
          var resourceTypeList = (!d.children)?[]:d.children;
          var resourceTypeListN = []
          var availableSquareList = []
          var basePlat = {}
          var regionNode = Object.assign({}, d);
          //更新resourceTypeList
          this.rebuildSublist(resourceTypeList);
          //初始化可用面积列表
          this.initSubAvailabList(availableSquareList, [0, 0], resourceTypeList[0], basePlat)

          resourceTypeList.forEach((item, idx) => {
            //把当前节点对比可用面积列表，确认节点如何处理，得到节点的最终相对位置
            var rnode = this.getAiliableSquareList(availableSquareList, resourceTypeList[0], item, basePlat, 1)
            rnode.regionId = item.regionId
            item.posNode = rnode;
            resourceTypeListN.push(rnode)
          })

          //根据处理好的结果，更新上级列表中对应节点面积和X,Y信息
          let maxAsis = Math.max(basePlat.xasis,basePlat.yasis)
          regionList[rindex].area = maxAsis * maxAsis
          regionList[rindex].sizeX = basePlat.xasis
          regionList[rindex].sizeY = basePlat.yasis
          regionList[rindex].resourceTypeList = resourceTypeListN
        })
        regionList.sort((a, b) => {
          return b.area - a.area
        })
        var availableRegionList = []
        // rebuildSublist(regionList);
        var baseRegionPlat = {}
        if(regionList.length>0){
          this.initSubAvailabList(availableRegionList, [0, 0], regionList[0], baseRegionPlat)

        regionList.forEach((item) => {
          var rnode = this.getAiliableSquareList(availableRegionList, regionList[0], item, baseRegionPlat)
          rnode.accountId = item.accountId
          item.posNode = rnode;
          regionListN.push(rnode)
        })
        let maxAsis = Math.max(baseRegionPlat.xasis,baseRegionPlat.yasis)
        cloudaccounts[i].area = maxAsis * maxAsis
        cloudaccounts[i].sizeX = baseRegionPlat.xasis
        cloudaccounts[i].sizeY = baseRegionPlat.yasis + 2
        cloudaccounts[i].regionList = regionListN;
        }else{
          cloudaccounts[i].area = 7 * 7
          cloudaccounts[i].sizeX = 7
          cloudaccounts[i].sizeY = 7
        }

      }
      cloudaccounts.sort((a, b) => {
        return b.area - a.area
      })
      var availableRegionList = []
      var baseCloudPlat = {}
      if(cloudaccounts.length>0){
        this.initSubAvailabList(availableRegionList, position0, cloudaccounts[0], baseCloudPlat)

        cloudaccounts.forEach((item, i) => {
          var rnode = this.getAiliableSquareList(availableRegionList, cloudaccounts[0], item, baseCloudPlat)
          item.basePosition = position0;
          item.posNode = rnode;

        })
        cellCountX = math.max(baseCloudPlat.xasis, baseCloudPlat.yasis) + 5
        cellCountY = cellCountX
        this.calActualPosition(cloudaccounts)
      }

      return cloudaccounts;
    },
    //多边形，背景的方格
    _drawPolygon() {
      return (
        (points, selection, pointTransform) => {
          pointTransform = pointTransform || (c => c);
          return selection.append('polygon')
            .attr('points', (d) => points.map(p => pointTransform(p).join()).join(' '))
            .attr('fill', 'none')
            .attr('stroke', 'black')
            .attr('stroke-width', 0.25);
        }
      )
    },
    //背景的方格
    _drawSquare(drawPolygon) {
      return (
        (origin, cellSize, selection, pointTransform) => {
          return drawPolygon([
            [origin[0], origin[1]],
            [origin[0] + cellSize, origin[1]],
            [origin[0] + cellSize, origin[1] + cellSize],
            [origin[0], origin[1] + cellSize]
          ], selection, pointTransform);
        }
      )
    },
    //整个方格背景
    _drawGrid(drawSquare) {
      return (
        (selection, cellSize, cellCountX, cellCountY, pointTransform) => {
          const grid = selection.append('g').attr('class', 'grid');
          for (let x = 0; x < cellCountX; x++) {
            for (let y = 0; y < cellCountY; y++) {
              drawSquare([x * cellSize, y * cellSize], cellSize, grid, pointTransform);
            }
          }
          return grid;
        }
      )
    },
    //画图中的所有图形元素，ecs,rds等的图形,节点点击事件
    _plotImageBoxes(d3, cellSize) {
      return (
        (data, selection, pointTransform) => {
          let size = cellSize;
          let boxes = selection.selectAll('g.imagebox').data(data);
          let imageType = ""
          boxes.exit().remove();
          boxes = boxes.enter()
            .append('g')
            .classed('imagebox', true)
            .each(function (d) {
              d3.select(this).append('image')
                .attr("xlink:href", () => {
                  var filePath = "`@/assets/img/cloudtopo/"
                  var filePrefix = ""
                  var filePosfix = ""
                  if (d.riskType == 'risk') {
                    filePrefix = "e-"
                  }
                  if (d.riskType == 'normal') {
                    filePrefix = "n-"
                  }
                  if (d.riskType == 'uncheck') {
                    filePrefix = "u-"
                  }
                  filePosfix = 'other.svg'
                  if (d.type == 'ecs')
                    filePosfix = 'server.svg'
                  if (d.type == 'rds')
                    filePosfix = 'db.svg'
                  if (d.type == 'oss')
                    filePosfix = 'oss.svg'
                  let imageFIle = require("@/assets/img/cloudtopo/" + filePrefix + filePosfix)
                  imageType = filePrefix + filePosfix
                  return imageFIle
                })
                .attr('height', size * 1.5 + 'px')
                .attr('weight', size * 1.2 + 'px')
                .attr('id', d.id)
                .attr('name', d.name)
                .attr('accountId', d.accountId)
                .attr('accountName', d.accountName)
                .attr('pluginId', d.pluginId)
                .attr('pluginName', d.pluginName)
                .attr('regionId', d.regionId)
                .attr('regionName', d.regionName)
                .attr('resourceType', d.resourceType)
                .attr('riskType', d.riskType)
                .attr('type', d.type)
                .attr('resource', d.resource)
                .attr('imageType', imageType)
              // .attr('transform', ' scale(0.5)')
            })
            .merge(boxes)
            .each(function (d) {
              //更具平面转isometric的左边转换，给图形确定x，y变换量
              let pos = pointTransform([(d.position[0] - 1) * cellSize - cellSize, (d.position[1]) * cellSize - cellSize])
              d3.select(this).select('image')
                .attr('x', pos[0]).attr('y', pos[1])
            }).on('click', function (event, d) {
              //在图形上增加点击事件
              let pos = pointTransform([(d.position[0] - 1) * cellSize - cellSize, (d.position[1]) * cellSize - cellSize]);
              if (clicked) {
                d3.selectAll('image.clicked-box').each(function (d) {
                  let imageType = d3.select(this).attr('imageType');
                  let itemid = d3.select(this).attr("id")

                  let p = pointTransform([(d.position[0] - 1) * cellSize - cellSize, (d.position[1]) * cellSize - cellSize])
                  d3.select(this).attr('x', p[0]).attr('y', p[1])
                    .attr("xlink:href", () => {

                      let imageFIle = require("@/assets/img/cloudtopo/" + imageType)
                      return imageFIle
                    })
                    .classed('clicked-box', false)
                  d3.select('#' + itemid + 'text')
                    .classed('clicked-text', false)
                    .style('transform', '')
                })
                clicked = false;
              } else {
                let imageType = d3.select(this).select('image').attr('imageType');
                let itemid = d3.select(this).select('image').attr("id")
                d3.select(this).select('image')
                  .classed('clicked-box', true)
                  .attr('x', pos[0]).attr('y', pos[1] - 10)
                  .attr("xlink:href", () => {
                    let filestr = imageType.split(".svg")
                    let imageFIle = require("@/assets/img/cloudtopo/" + filestr[0] + "-click.svg")
                    return imageFIle
                  })

                d3.select('#' + itemid + 'text')
                  .classed('clicked-text', true)
                  .style('transform', 'scale(3)')

                clicked = true;
              }
            });
          return selection.selectAll('g.imagebox');
        }
      )
    },
    //留用方法，根据不同类型图形，对图形位置进行微调
    modifyImageByType(iconType, pos) {
      if (iconType == 'type1') {
        return [pos[0], pos[1]]
      }
      if (iconType == 'type2') {
        return [pos[0] - 30, pos[1] - 3]
      }
      if (iconType == 'type3') {
        return [pos[0] - 15, pos[1] + 15]
      }
    },
    //画云账号一级的地板效果
    _plotFloor(d3, cellSize) {
      return (
        (data, selection, pointTransform, plotIcons) => {

          let floors = selection.selectAll('g.floor').data(data);
          floors.exit().remove();
          floors = floors.enter()
            .append('g')
            .classed('floor', true)
            .each(function (d) {
              d3.select(this).append('path')
                .classed('floor-profile', true)
                .attr('fill', 'none')
                .attr('stroke', 'gray')
                .attr('stroke-width', '3');
              d3.select(this).append('path')
                .classed('floor-top-face', true)
                .attr('fill', '#EEE')
                .attr('fill-opacity', '0.8')
                .attr('stroke', 'gray')
                .attr('stroke-width', '0.5');

              // point transform means iso, so we also need side faces
              if (pointTransform) {
                d3.select(this).append('path')
                  .classed('floor-left-face', true)
                  .attr('fill', '#725E37')
                  .attr('stroke', 'gray')
                  .attr('stroke-width', '0.5');
                d3.select(this).append('path')
                  .classed('floor-right-face', true)
                  .attr('fill', '#725E37')
                  .attr('stroke', 'gray')
                  .attr('stroke-width', '0.5');
              }
            })
            .merge(floors)
            .each(function (d) {
              d3.select(this).select('.floor-profile')
                .attr('d', () => {
                  if (!pointTransform) {
                    // no transform means this is standard 2d, so we only render
                    // a single side (the top)
                    let t = (c => c);
                    let path;
                    const origin = [d.position[0] * cellSize, d.position[1] * cellSize];
                    path = [
                      'M', t(origin).join(' '),
                      'l', t([cellSize * d.sizex, 0]).join(' '),
                      'l', t([0, cellSize * d.sizey]).join(' '),
                      'l', t([-cellSize * d.sizex, 0]).join(' '),
                      'Z'
                    ].join(' ');
                    return path;
                  } else {
                    // transform means this is iso, so we render three faces
                    let path;
                    const origin = [d.position[0] * cellSize - cellSize, d.position[1] * cellSize - cellSize];

                    path = [
                      'M', pointTransform(origin).join(' '),
                      'l', pointTransform([cellSize * d.sizex, 0]).join(' '),
                      'l', pointTransform([cellSize * d.thickness, (0 + cellSize) * d.thickness]).join(' '),
                      'l', pointTransform([0, cellSize * d.sizey]).join(' '),
                      'l', pointTransform([-cellSize * d.sizex, 0]).join(' '),
                      'l', pointTransform([-cellSize * d.thickness, (0 - cellSize) * d.thickness]).join(' '),
                      'Z'
                    ].join(' ');
                    return path;
                  }
                });
              d3.select(this).select('.floor-top-face')
                .attr('d', () => {
                  let origin = pointTransform ?
                    [d.position[0] * cellSize - cellSize, d.position[1] * cellSize - cellSize] :
                    [d.position[0] * cellSize, d.position[1] * cellSize];
                  let t = pointTransform || (c => c);
                  return [
                    'M', t(origin).join(' '),
                    'l', t([cellSize * d.sizex, 0]).join(' '),
                    'l', t([0, cellSize * d.sizey]).join(' '),
                    'l', t([-cellSize * d.sizex, 0]).join(' '),
                    'Z'
                  ].join(' ');
                });
              d3.select(this).select('.floor-left-face')
                .attr('d', () => {
                  let origin = [d.position[0] * cellSize - cellSize, (d.position[1] + d.sizey - 1) * cellSize];
                  let t = pointTransform || (c => c);
                  return [
                    'M', t(origin).join(' '),
                    'l', t([cellSize * d.sizex, 0]).join(' '),
                    'l', t([cellSize * d.thickness, cellSize * d.thickness]).join(' '),
                    'l', t([-cellSize * d.sizex, 0]).join(' '),
                    'Z'
                  ].join(' ');
                });
              d3.select(this).select('.floor-right-face')
                .attr('d', () => {
                  let origin = [(d.position[0] + d.sizex - 1) * cellSize, (d.position[1] + d.sizey - 1) * cellSize];
                  let t = pointTransform || (c => c);
                  return [
                    'M', t(origin).join(' '),
                    'l', t([0, -cellSize * d.sizey]).join(' '),
                    'l', t([cellSize * d.thickness, cellSize * d.thickness]).join(' '),
                    'l', t([0, cellSize * d.sizey]).join(' '),
                    'Z'
                  ].join(' ');
                });

            });

          plotIcons(data, selection, pointTransform, 'matrix(0.707 0.409 -0.707 0.409 0 0)', 12, iconData);
          return selection.selectAll('g.floor');
        }
      )
    },
    //画region级的边框
    _plotDashedBox(d3, cellSize) {
      return (
        (data, selection, pointTransform) => {
          let dashedBox = selection.selectAll('g.dashedBox').data(data);
          dashedBox.exit().remove();
          dashedBox = dashedBox.enter()
            .append('g')
            .classed('dashedBox', true)
            .each(function (d) {
              d3.select(this).append('path')
                .classed('dash-top-face', true)
                .attr('fill', 'none')
                .attr('stroke', '#4B4B71')
                .attr('stroke-width', '3')
              // .attr('stroke-dasharray', '5,5');
            })
            .merge(dashedBox)
            .each(function (d) {
              d3.select(this).select('.dash-top-face')
                .attr('d', () => {
                  let origin = pointTransform ?
                    [d.position[0] * cellSize - cellSize, d.position[1] * cellSize - cellSize] :
                    [d.position[0] * cellSize, d.position[1] * cellSize];
                  let t = pointTransform || (c => c);
                  return [
                    'M', t(origin).join(' '),
                    'l', t([cellSize * d.sizex, 0]).join(' '),
                    'l', t([0, cellSize * d.sizey]).join(' '),
                    'l', t([-cellSize * d.sizex, 0]).join(' '),
                    'Z'
                  ].join(' ');
                });
            });
          return selection.selectAll('g.dashedBox');
        }
      )
    },
    //画resourcestype级的地面
    _plotRcBox(d3, cellSize) {
      return (
        (data, selection, pointTransform) => {
          let dashedBox = selection.selectAll('g.resourcetype').data(data);
          dashedBox.exit().remove();
          dashedBox = dashedBox.enter()
            .append('g')
            .classed('resourcetype', true)
            .each(function (d) {
              d3.select(this).append('path')
                .classed('dash-top-face', true)
                .attr('fill', '#5CCCFF')
                .attr('fill-opacity', '0.3')
                .attr('stroke', '#5CCCFF')
                .attr('stroke-width', '0')
                .attr('stroke-dasharray', '3,3');
            })
            .merge(dashedBox)
            .each(function (d) {
              d3.select(this).select('.dash-top-face')
                .attr('d', () => {
                  let origin = pointTransform ?
                    [d.position[0] * cellSize - cellSize + 1, d.position[1] * cellSize - cellSize + 1] :
                    [d.position[0] * cellSize, d.position[1] * cellSize];
                  let t = pointTransform || (c => c);
                  return [
                    'M', t(origin).join(' '),
                    'l', t([cellSize * d.sizex - 1, 1]).join(' '),
                    'l', t([-1, cellSize * d.sizey - 1]).join(' '),
                    'l', t([-cellSize * d.sizex + 1, -1]).join(' '),
                    'Z'
                  ].join(' ');
                });
            });
          return selection.selectAll('g.resourcetype');
        }
      )
    },
    //画图标，目前用来给云账号一级的地板增加logo
    _plotIcon(cellSize, d3) {
      return (
        (data, selection, pointTransform, svgTransform = '', iconSize, iconData) => {
          pointTransform = pointTransform || (c => c);
          let viewboxsize = iconSize * 2;
          let icons = selection.selectAll('g.icon').data(data);
          icons.exit().remove();
          icons = icons.enter()
            .append('g')
            .classed('icon', true)
            .each(function (d) {
              d3.select(this).append('image')
                .attr("xlink:href", () => {
                  let filepath = './image/aliyun.svg'
                  for (var i = 0; i < iconData.length; i++) {
                    if (d.type == iconData[i].name) {
                      filepath = require("@/assets/img/cloudtopo/" + iconData[i].file)
                    }
                  }
                  return filepath;
                })
                .attr('height', cellSize)
                .attr('weight', cellSize)
            })
            .merge(icons)
            .each(function (d) {

              let pos = pointTransform([d.position[0] * cellSize - cellSize, (d.position[1] + d.sizey - 2) * cellSize])
              // let pos = pointTransform([d.position[0] * cellSize - cellSize, d.position[1] * cellSize - cellSize])
              d3.select(this).select('image')
                .attr('x', pos[0]).attr('y', pos[1])
                .attr('transform', svgTransform)
                .style('transform-origin', (d) => {
                  return `${pos[0]}px ${pos[1]}px`;
                })
            })
          return selection.selectAll('g.icon');

        }
      )
    },
    //处理图上所有的文字
    _plotTexts(cellSize, d3) {
      return (
        (data, selection, pointTransform, svgTransform = '') => {
          pointTransform = pointTransform || (c => c);
          let texts = selection.selectAll('text').data(data);
          const padding = cellSize / 10;
          texts.exit().remove();
          texts = texts
            .enter()
            .append('text')
            .attr('transform', svgTransform)
            .style('cursor', 'hand')
            .style('user-select', 'none')
            .each(function (d) {
              if (d.border) {
                d3.select(this)
                  .append('tspan')
                  .attr('dominant-baseline', 'hanging')
                  .style('font-family', 'helvetica')
                  .style('font-weight', 'bold')
                  .style('font-size', (d) => d.size ? `${d.size}rem` : '1.5rem')
                  .attr('stroke', "white")
                  .attr('stroke-width', '1');
              }
              d3.select(this)
                .attr('id', d.id)
                .append('tspan')
                .attr('dominant-baseline', 'hanging')
                .style('font-family', 'helvetica')
                .style('font-weight', 'bold')
                .style('font-size', (d) => d.size ? `${d.size}rem` : '1.5rem');
            })
            .merge(texts)
            .style('transform-origin', (d) => {
              const xy = pointTransform([d.position[0] * cellSize + padding, d.position[1] * cellSize + padding]);
              return `${xy[0]}px ${xy[1]}px`;
            })
            .each(function (d) {
              d3.select(this).selectAll('tspan')
                .text(d.text)
                .attr('fill', d.color)
                .attr('x', pointTransform([d.position[0] * cellSize + padding, d.position[1] * cellSize])[0])
                .attr('y', pointTransform([d.position[0] * cellSize, d.position[1] * cellSize + padding])[1]);
            });
          return selection.selectAll('text');
        }
      )
    },
    //计算弧度
    _toRadians() {
      return (
        (degrees) => degrees * (Math.PI / 180)
      )
    },
    //转换成isometric时的坐标变换计算
    _toIsoTransform(toRadians, math) {
      return (
        (degrees) => {
          const rads = toRadians(degrees);
          const matrices = {
            shear: [
              [1, math.tan(-rads), 0],
              [0, 1, 0],
              [0, 0, 1],
            ],
            scale: [
              [1, 0, 0],
              [0, math.cos(-rads), 0],
              [0, 0, 1]
            ],
            rotate: [
              [math.cos(rads), -math.sin(rads), 0],
              [math.sin(rads), math.cos(rads), 0],
              [0, 0, 1]
            ]
          };
          const matrix = math.multiply(
            matrices.rotate,
            math.multiply(matrices.shear, matrices.scale)
          );
          return (coords) => {
            const product = math.multiply(matrix, [[coords[0]], [coords[1]], [1]]);
            return [product[0][0], product[1][0]];
          };
        }
      )
    },
    //初始化 云账号，region，资源类型，资源的数据
    initData(allData, floorData, regionData, resourceTypeData, boxImageData, textData) {
      allData.forEach((item1, i1) => {
        let item = {thickness: 0.4}
        item.position = [item1.posX, item1.posY]
        item.sizex = item1.sizeX
        item.sizey = item1.sizeY
        item.type = item1.type
        floorData.push(item)
        let ftext = {}
        ftext.position = [item1.posX + 1, item1.posY + item1.sizeY - 2]
        ftext.text = item1.name
        ftext.color = "black"
        ftext.size = 0.8
        ftext.border = true
        textData.push(ftext)
        item1.children.forEach((item2, i2) => {
          let regionbox = {}
          regionbox.position = [item2.posX, item2.posY]
          regionbox.sizex = item2.sizeX
          regionbox.sizey = item2.sizeY
          regionData.push(regionbox)
          let rgtext = {}
          rgtext.position = [item2.posX - 1, item2.posY + item2.sizeY - 1.3]
          rgtext.text = item2.name
          rgtext.color = "black"
          rgtext.size = 0.6
          rgtext.border = true
          textData.push(rgtext)

          item2.children.forEach((item3, i3) => {
            let rcbox = {}
            rcbox.position = [item3.posX, item3.posY]
            rcbox.sizex = item3.sizeX
            rcbox.sizey = item3.sizeY
            resourceTypeData.push(rcbox)

            let rctext = {}
            rctext.position = [item3.posX - 1, item3.posY + item3.sizeY - 1.7]
            rctext.text = item3.name
            rctext.color = "black"
            rctext.size = 0.4
            rctext.border = true
            textData.push(rctext)

            item3.children.forEach((item4, i4) => {
              let box = item4?item4:{};
              box.position = [item3.posX + i4 % (item3.sizeX - 1), item3.posY + Math.floor(i4 / (item3.sizeY - 1))]
              box.iconType = 'type1'
              box.riskType = 'normal'
              if (item4.value > 1) {
                box.riskType = 'risk'
              }
              if (item4.value === 1) {
                box.riskType = 'normal'
              }
              if (item4.value < 1) {
                box.riskType = 'uncheck'
              }
              boxImageData.push(box)
              let nodetext = {}
              nodetext.position = [box.position[0] - 1.5, box.position[1] + 1 - 1.7]
              nodetext.text = item4.name
              nodetext.color = "black"
              nodetext.size = 0.2
              nodetext.border = true
              nodetext.id = box.id + 'text'
              textData.push(nodetext)
            })
          })

        })
      })

    },
    //初始化可选面积列表，在每次进入新循环，开始构建一个列表的位置前，需要构建初始列表
    initSubAvailabList(availableSquareList, starPosition, firstSquare, basePlat) {
      var squareNode = {}
      squareNode.starPosition = starPosition
      let xy = Math.max(firstSquare.sizeX,firstSquare.sizeY)
      squareNode.sizeX = xy;
      squareNode.sizeY = xy;
      squareNode.area = xy * xy

      basePlat.starPosition = starPosition;
      basePlat.xasis = xy;
      basePlat.yasis = xy;

      availableSquareList.push(squareNode)
    },
    //根据列表中元素包含的子元素数量，来确定一个正方形，用来放置所有的子元素
    rebuildSublist(sList) {
      sList.forEach((item, index) => {
        var totalItem = item.total;
        var rSizeX = Math.ceil(Math.sqrt(totalItem)) + 1
        var rSizeY = rSizeX
        sList[index].area = rSizeX * rSizeY
        sList[index].sizeX = rSizeX
        sList[index].sizeY = rSizeY
      })
      //对列表进行排序，确保面积最大的排在最前面
      sList.sort((a, b) => {
        return b.area - a.area
      })
    },
    //计算每个元素在画布上的实际位置，前面计算的记过存储的都是相对坐标，这里最后统一转化为绝对坐标
    calActualPosition(xList) {
      xList.forEach((item) => {
        item.posX = item.basePosition[0] + item.posNode.starPosition[0]
        item.posY = item.basePosition[1] + item.posNode.starPosition[1]
        var nextBasePos = [item.posX, item.posY]
        if(!item.children)
          item.children = []
        item.children.forEach((item1) => {
          item1.posX = nextBasePos[0] + item1.posNode.starPosition[0]
          item1.posY = nextBasePos[1] + item1.posNode.starPosition[1]
          var nextBasePos1 = [item1.posX, item1.posY]
          if(!item1.children)
            item1.children = []
          item1.children.forEach((item2) => {
            item2.posX = nextBasePos1[0] + item2.posNode.starPosition[0]
            item2.posY = nextBasePos1[1] + item2.posNode.starPosition[1]
          })

        })
      })
    },
    //根据现有的可用面积列表，来确定传入方块元素的位置
    getAiliableSquareList(availableSquareList, baseSquare, square, basePlat, padding) {
      //如果当前可用列表能放得下新元素，那么就用列表中元素，放置新元素后，重构可用列表。
      //如果当前可用列表放不下新元素，那么扩充可用列表，向短边扩充一个基础单元单元长度，扩充后重新计算
      var enough = undefined;
      var resultNode = {}
      for (var i = 0; i < availableSquareList.length; i++) {
        var d = availableSquareList[i]
        if ((d.area >= square.area) && (d.sizeX >= square.sizeX && d.sizeY >= square.sizeY)) {
          resultNode = Object.assign({}, d);
          resultNode.sizeX = square.sizeX.valueOf()
          resultNode.sizeY = square.sizeY.valueOf()
          resultNode.area = (square.sizeX * square.sizeY).valueOf();
          enough = true;
          let reSizex = d.sizeX - square.sizeX
          let reSizey = d.sizeY - square.sizeY
          availableSquareList.splice(i, 1)
          if (reSizex > 0) {
            var squareNode = {}
            squareNode.starPosition = [d.starPosition[0] + square.sizeX, d.starPosition[1].valueOf()]
            squareNode.sizeX = reSizex;
            squareNode.sizeY = square.sizeY.valueOf();
            squareNode.area = (squareNode.sizeX * squareNode.sizeY).valueOf();
            availableSquareList.push(squareNode)
          }
          if (reSizey > 0) {
            var squareNode = {}
            squareNode.starPosition = [d.starPosition[0], d.starPosition[1] + square.sizeY]
            squareNode.sizeX = d.sizeX.valueOf();
            squareNode.sizeY = reSizey;
            squareNode.area = (squareNode.sizeX * squareNode.sizeY).valueOf();
            availableSquareList.push(squareNode)
          }
          break;
        } else {
          enough = false;
        }

      }

      if (enough == undefined || enough == false) {
        let padding = 1;
        var squareNode = {}
        squareNode.sizeX = basePlat.xasis.valueOf()
        squareNode.sizeY = basePlat.yasis.valueOf()
        squareNode.area = basePlat.xasis * basePlat.yasis
        if (basePlat.xasis <= basePlat.yasis) {
          Object.assign(squareNode, {starPosition: [basePlat.xasis + basePlat.starPosition[0], basePlat.starPosition[1]]});
          basePlat.xasis = basePlat.xasis + basePlat.xasis;
        } else {
          Object.assign(squareNode, {starPosition: [basePlat.starPosition[0], basePlat.yasis + basePlat.starPosition[1]]});
          basePlat.yasis = basePlat.yasis + basePlat.yasis
        }

        availableSquareList.push(squareNode)
        availableSquareList.forEach((d, i) => {
          if ((d.area >= square.area) && (d.sizeX >= square.sizeX && d.sizeY >= square.sizeY)) {
            resultNode = Object.assign({}, d);
            resultNode.sizeX = square.sizeX.valueOf()
            resultNode.sizeY = square.sizeY.valueOf()
            resultNode.area = square.sizeX * square.sizeY;
            let reSizex = d.sizeX - square.sizeX
            let reSizey = d.sizeY - square.sizeY

            availableSquareList.splice(i, 1)
            if (reSizex != 0) {
              var squareNode = {}
              squareNode.starPosition = [d.starPosition[0] + square.sizeX, d.starPosition[1]]
              squareNode.sizeX = reSizex;
              squareNode.sizeY = square.sizeY.valueOf();
              squareNode.area = (squareNode.sizeX * squareNode.sizeY).valueOf();
              availableSquareList.push(squareNode)
            }
            if (reSizey != 0) {
              var squareNode = {}
              squareNode.starPosition = [d.starPosition[0], d.starPosition[1] + square.sizeY]
              squareNode.sizeX = d.sizeX.valueOf();
              squareNode.sizeY = reSizey;
              squareNode.area = (squareNode.sizeX * squareNode.sizeY).valueOf();

              availableSquareList.push(squareNode)
            }
            return;
          }

        })
      }
      availableSquareList.sort((a, b) => {
        return a.area - b.area
      })
      return resultNode;
    },
    //关闭弹框
    handleClose() {
      this.dialogVisible = false;
    },
    //选择云账号名称
    selectAccount(accountId, accountName) {
      this.accountId = accountId;
      this.accountName = accountName;
      this.init();
    },
  },

  mounted() {
    //弹框：全局点击事件监听，因为On click方法里获取的this是当前点击元素，获取不到this.dialogVisible，没办法做弹框
    document.addEventListener('click', async (e) => {
      let target = e.target;
      let thisClassName = target.className;
      if (thisClassName.baseVal === 'clicked-box') {
        this.details = {
          id: target.getAttribute("id"),
          name: target.getAttribute("name"),
          pluginId: target.getAttribute("pluginId"),
          pluginName: target.getAttribute("pluginName"),
          accountId: target.getAttribute("accountId"),
          accountName: target.getAttribute("accountName"),
          regionId: target.getAttribute("regionId"),
          regionName: target.getAttribute("regionName"),
          resourceType: target.getAttribute("resourceType"),
          riskType: target.getAttribute("riskType"),
          type: target.getAttribute("type"),
          resource: target.getAttribute("resource"),
        };
        if (this.details.resource) {
          await this.$get(cloudResourceByIdUrl + this.details.resource, response => {
            let data = response.data;
            this.resources = data;
          });
        }
        await this.$get(resourceRiskListUrl + this.details.regionId + "/" + this.details.id, response => {
          this.string2PrettyFormat = response.data;
        });
        this.dialogVisible = true;
      }
    })
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

.desc-top {
  margin: 15px;
}

.table-content {
  padding: 20px;
}
</style>
