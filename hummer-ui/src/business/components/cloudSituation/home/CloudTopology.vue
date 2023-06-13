<template>
  <main-container>
    <el-card class="table-card el-row-card">
      <topo-switch @topoSwitch="topoSwitch" @selectAccount="selectAccount" :accountName="accountName"/>
    </el-card>

    <el-card class="table-card" v-loading="result.loading">
      <svg id="cloud-topo"></svg>
    </el-card>
    <el-drawer class="rtl" :title="$t('resource.cloud_resource_detail')" :visible.sync="dialogVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="viewResult.loading">
      <el-descriptions class="margin-top" title="" :column="2" border>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-paperclip"></i>
            {{ $t('resource.Hummer_ID') }}
          </template>
          {{ 'hummerrisk-0001' }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-tickets"></i>
            {{ $t('dashboard.resource_name') }}
          </template>
          {{ 'hummerrisk-dev' }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-cloudy"></i>
            {{ $t('account.cloud_platform') }}
          </template>
          {{ '阿里云' }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-house"></i>
            {{ $t('account.cloud_account') }}
          </template>
          {{ 'aliyun' }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-location-information"></i>
            {{ $t('account.regions') }}
          </template>
          {{ '北京1' }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-collection-tag"></i>
            {{ $t('resource.risk') }}
          </template>
          <span style="color: red;">{{ '有风险' }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <el-divider><i class="el-icon-folder-opened"></i></el-divider>
      <result-read-only :row="typeof(resource) === 'string'?JSON.parse(resource):resource"></result-read-only>
      <el-divider><i class="el-icon-document-checked"></i></el-divider>
      <cloud-detail-chart/>
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
import {cloudTopologyUrl} from "@/api/cloud/sync/sync";
/* eslint-disable */
const width = 1600;
const height = 1100;
const cellSize = 30;
const iconSize = 12;
let cellCountX = 30;
let cellCountY = 30;
let clicked = false;
const iconData = [
  {"name": "aws", "file": "aws.svg"},
  {"name": "aliyun", "file": "aliyun.svg"},
  {"name": "huawei", "file": "huawei.svg"},
  {"name": "gcp", "file": "gcp.svg"},
  {"name": "tengxun", "file": "tengxun.svg"},
  {"name": "azure", "file": "azure.svg"},
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
      accountId: '',
      accountName: '',
      resource: {"Logging":{},"CreationDate":"2023-02-02T02:25:28+00:00","Versioning":{"Status":"Enabled"},"Acl":{"Owner":{"ID":"06ef6af1f3cd38ee2235066e84f042c4c2651d1549a8b2e4cad047a3395a955c"},"Grants":[{"Grantee":{"Type":"CanonicalUser","ID":"06ef6af1f3cd38ee2235066e84f042c4c2651d1549a8b2e4cad047a3395a955c"},"Permission":"FULL_CONTROL"}]},"Tags":[],"Notification":{},"Name":"hummerrisk-package","Location":{"LocationConstraint":"ap-east-1"}},
    };
  },
  methods: {
    //初始化
    init() {

      let testData = {
        "children": [{
          "children": [{
            "children": [{
              "children": [{
                "value": 10,
                "name": "(有安全合规风险)资源标识:sg-0ffb37ab7ada5c256",
                "namespace": null,
                "type": "other"
              }, {
                "value": 10,
                "name": "(有安全合规风险)资源标识:sg-07c37c89c1e5feea4",
                "namespace": null,
                "type": "other"
              }, {
                "value": 10,
                "name": "(有安全合规风险)资源标识:sg-019900da7df6437f2",
                "namespace": null,
                "type": "other"
              }, {
                "value": 10,
                "name": "(有安全合规风险)资源标识:sg-0b90f930fab86227e",
                "namespace": null,
                "type": "other"
              }, {
                "value": 10,
                "name": "(有安全合规风险)资源标识:sg-001fb0e8ad2976649",
                "namespace": null,
                "type": "other"
              }],
              "name": "资源类型: aws.security-group(共5 个资源)",
              "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
              "regionId": "cn-north-1",
              "resourceType": "aws.security-group",
              "total": 5
            }, {
              "children": [{
                "value": 12,
                "name": "(有安全合规风险)资源标识:vol-0871c4a9e3662875e",
                "namespace": null,
                "type": "other"
              }, {
                "value": 12,
                "name": "(有安全合规风险)资源标识:vol-0cd75443ca20a70fb",
                "namespace": null,
                "type": "other"
              }, {
                "value": 12,
                "name": "(有安全合规风险)资源标识:vol-0df905d1ba33c4420",
                "namespace": null,
                "type": "other"
              }, {
                "value": 12,
                "name": "(有安全合规风险)资源标识:vol-00a383641043a764c",
                "namespace": null,
                "type": "other"
              }, {
                "value": 12,
                "name": "(有安全合规风险)资源标识:vol-04a2dcb840b6e1ca4",
                "namespace": null,
                "type": "other"
              }],
              "name": "资源类型: aws.ebs(共5 个资源)",
              "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
              "regionId": "cn-north-1",
              "resourceType": "aws.ebs",
              "total": 5
            }, {
              "children": [{
                "value": 8,
                "name": "(有安全合规风险)资源标识:i-0d9b2118a5f4b223e",
                "namespace": null,
                "type": "ecs"
              }, {"value": 1, "name": "资源标识:i-0ad808a8c1e077ce7", "namespace": null, "type": "ecs"}, {
                "value": 1,
                "name": "资源标识:i-04d32e6dd7adf2868",
                "namespace": null,
                "type": "ecs"
              }, {"value": 1, "name": "资源标识:i-0119c27e6b27d86f1", "namespace": null, "type": "ecs"}],
              "name": "资源类型: aws.ec2(共4 个资源)",
              "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
              "regionId": "cn-north-1",
              "resourceType": "aws.ec2",
              "total": 4
            }, {
              "children": [{
                "value": 6,
                "name": "(有安全合规风险)资源标识:eipalloc-0c3f6b39801154ce8",
                "namespace": null,
                "type": "other"
              }, {
                "value": 6,
                "name": "(有安全合规风险)资源标识:eipalloc-0d4aa85f89a41acad",
                "namespace": null,
                "type": "other"
              }, {
                "value": 6,
                "name": "(有安全合规风险)资源标识:eipalloc-0ca933c006f2c7400",
                "namespace": null,
                "type": "other"
              }],
              "name": "资源类型: aws.network-addr(共3 个资源)",
              "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
              "regionId": "cn-north-1",
              "resourceType": "aws.network-addr",
              "total": 3
            }],
            "name": "区域: 中国（北京）(共17 个资源)",
            "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
            "regionId": "cn-north-1",
            "total": 17
          }, {
            "children": [{
              "children": [{
                "value": 1,
                "name": "资源标识:sg-017c527b52bf0c658",
                "namespace": null,
                "type": "other"
              }],
              "name": "资源类型: aws.security-group(共1 个资源)",
              "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
              "regionId": "cn-northwest-1",
              "resourceType": "aws.security-group",
              "total": 1
            }],
            "name": "区域: 中国（宁夏）(共1 个资源)",
            "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
            "regionId": "cn-northwest-1",
            "total": 1
          }],
          "name": "云账号: aws(共18 个资源)",
          "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
          "type": "tengxun",
          "total": 18
        }, {
          "children": [{
            "children": [{
              "children": [{
                "value": 10,
                "name": "(有安全合规风险)资源标识:sg-0ffb37ab7ada5c256",
                "namespace": null,
                "type": "other"
              }, {
                "value": 10,
                "name": "(有安全合规风险)资源标识:sg-07c37c89c1e5feea4",
                "namespace": null,
                "type": "other"
              }, {
                "value": 10,
                "name": "(有安全合规风险)资源标识:sg-019900da7df6437f2",
                "namespace": null,
                "type": "other"
              }, {
                "value": 10,
                "name": "(有安全合规风险)资源标识:sg-0b90f930fab86227e",
                "namespace": null,
                "type": "other"
              }, {
                "value": 10,
                "name": "(有安全合规风险)资源标识:sg-001fb0e8ad2976649",
                "namespace": null,
                "type": "other"
              }],
              "name": "资源类型: aws.security-group(共5 个资源)",
              "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
              "regionId": "cn-north-1",
              "resourceType": "aws.security-group",
              "total": 5
            }, {
              "children": [{
                "value": 12,
                "name": "(有安全合规风险)资源标识:vol-0871c4a9e3662875e",
                "namespace": null,
                "type": "other"
              }, {
                "value": 12,
                "name": "(有安全合规风险)资源标识:vol-0cd75443ca20a70fb",
                "namespace": null,
                "type": "other"
              }, {
                "value": 12,
                "name": "(有安全合规风险)资源标识:vol-0df905d1ba33c4420",
                "namespace": null,
                "type": "other"
              }, {
                "value": 12,
                "name": "(有安全合规风险)资源标识:vol-00a383641043a764c",
                "namespace": null,
                "type": "other"
              }, {
                "value": 12,
                "name": "(有安全合规风险)资源标识:vol-04a2dcb840b6e1ca4",
                "namespace": null,
                "type": "other"
              }],
              "name": "资源类型: aws.ebs(共5 个资源)",
              "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
              "regionId": "cn-north-1",
              "resourceType": "aws.ebs",
              "total": 5
            }, {
              "children": [{
                "value": 8,
                "name": "(有安全合规风险)资源标识:i-0d9b2118a5f4b223e",
                "namespace": null,
                "type": "ecs"
              }, {"value": 1, "name": "资源标识:i-0ad808a8c1e077ce7", "namespace": null, "type": "ecs"}, {
                "value": 1,
                "name": "资源标识:i-04d32e6dd7adf2868",
                "namespace": null,
                "type": "ecs"
              }, {"value": 1, "name": "资源标识:i-0119c27e6b27d86f1", "namespace": null, "type": "ecs"}],
              "name": "资源类型: aws.ec2(共4 个资源)",
              "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
              "regionId": "cn-north-1",
              "resourceType": "aws.ec2",
              "total": 4
            }, {
              "children": [{
                "value": 6,
                "name": "(有安全合规风险)资源标识:eipalloc-0c3f6b39801154ce8",
                "namespace": null,
                "type": "other"
              }, {
                "value": 6,
                "name": "(有安全合规风险)资源标识:eipalloc-0d4aa85f89a41acad",
                "namespace": null,
                "type": "other"
              }, {
                "value": 6,
                "name": "(有安全合规风险)资源标识:eipalloc-0ca933c006f2c7400",
                "namespace": null,
                "type": "other"
              }],
              "name": "资源类型: aws.network-addr(共3 个资源)",
              "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
              "regionId": "cn-north-1",
              "resourceType": "aws.network-addr",
              "total": 3
            }],
            "name": "区域: 中国（北京）(共17 个资源)",
            "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
            "regionId": "cn-north-1",
            "total": 17
          }, {
            "children": [{
              "children": [{
                "value": 1,
                "name": "资源标识:sg-017c527b52bf0c658",
                "namespace": null,
                "type": "other"
              }],
              "name": "资源类型: aws.security-group(共1 个资源)",
              "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
              "regionId": "cn-northwest-1",
              "resourceType": "aws.security-group",
              "total": 1
            }],
            "name": "区域: 中国（宁夏）(共1 个资源)",
            "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
            "regionId": "cn-northwest-1",
            "total": 1
          }],
          "name": "云账号: aws(共18 个资源)",
          "accountId": "fbd20422-9c1a-4120-83a9-2ce4f36f9b93",
          "type": "aws",
          "total": 18
        }, {
          "children": [{
            "children": [{
              "children": [{
                "value": 10,
                "name": "(有安全合规风险)资源标识:oss-123",
                "namespace": null,
                "type": "oss"
              }],
              "name": "资源类型: aliyun.oss(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.oss",
              "total": 1
            }, {
              "children": [{
                "value": 12,
                "name": "(有安全合规风险)资源标识:disk-0871c4a9e3662835e",
                "namespace": null,
                "type": "other"
              }],
              "name": "资源类型: aliyun.disk(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.disk",
              "total": 1
            }, {
              "children": [{
                "value": 8,
                "name": "(有安全合规风险)资源标识:i-0d9b2118a5f4b223e",
                "namespace": null,
                "type": "rds"
              }],
              "name": "资源类型: aliyun.mongodb(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.mongodb",
              "total": 1
            }, {
              "children": [{
                "value": 6,
                "name": "(有安全合规风险)资源标识:rds-0c3f6b39801154ce8",
                "namespace": null,
                "type": "rds"
              }],
              "name": "资源类型: aliyun.rds(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.rds",
              "total": 1
            }],
            "name": "区域: 华东 2（上海）(共4 个资源)",
            "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
            "regionId": "cn-shanghai",
            "total": 4
          }, {
            "children": [{
              "children": [{
                "value": 1,
                "name": "资源标识:ecs-017c527b52bf0c656",
                "namespace": null,
                "type": "ecs"
              }],
              "name": "资源类型: aliyun.ecs(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-northwest-1",
              "resourceType": "aliyun.ecs",
              "total": 1
            }],
            "name": "区域: 华北 2（北京）(共1 个资源)",
            "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
            "regionId": "cn-beijing",
            "total": 1
          }],
          "name": "云账号: aliyun(共5 个资源)",
          "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
          "type": "aws",
          "total": 5
        }, {
          "children": [{
            "children": [{
              "children": [{
                "value": 10,
                "name": "(有安全合规风险)资源标识:oss-123",
                "namespace": null,
                "type": "oss"
              }],
              "name": "资源类型: aliyun.oss(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.oss",
              "total": 1
            }, {
              "children": [{
                "value": 12,
                "name": "(有安全合规风险)资源标识:disk-0871c4a9e3662835e",
                "namespace": null,
                "type": "other"
              }],
              "name": "资源类型: aliyun.disk(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.disk",
              "total": 1
            }, {
              "children": [{
                "value": 8,
                "name": "(有安全合规风险)资源标识:i-0d9b2118a5f4b223e",
                "namespace": null,
                "type": "rds"
              }],
              "name": "资源类型: aliyun.mongodb(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.mongodb",
              "total": 1
            }, {
              "children": [{
                "value": 6,
                "name": "(有安全合规风险)资源标识:rds-0c3f6b39801154ce8",
                "namespace": null,
                "type": "rds"
              }],
              "name": "资源类型: aliyun.rds(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.rds",
              "total": 1
            }],
            "name": "区域: 华东 2（上海）(共4 个资源)",
            "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
            "regionId": "cn-shanghai",
            "total": 4
          }, {
            "children": [{
              "children": [{
                "value": 1,
                "name": "资源标识:ecs-017c527b52bf0c656",
                "namespace": null,
                "type": "ecs"
              }],
              "name": "资源类型: aliyun.ecs(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-northwest-1",
              "resourceType": "aliyun.ecs",
              "total": 1
            }],
            "name": "区域: 华北 2（北京）(共1 个资源)",
            "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
            "regionId": "cn-beijing",
            "total": 1
          }],
          "name": "云账号: aliyun(共5 个资源)",
          "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
          "type": "huawei",
          "total": 5
        }, {
          "children": [{
            "children": [{
              "children": [{"value": 10, "name": "(有安全合规风险)资源标识:oss-123", "namespace": null}],
              "name": "资源类型: aliyun.oss(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.oss",
              "total": 1
            }, {
              "children": [{
                "value": 12,
                "name": "(有安全合规风险)资源标识:disk-0871c4a9e3662835e",
                "namespace": null,
                "type": "other"
              }],
              "name": "资源类型: aliyun.disk(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.disk",
              "total": 1
            }, {
              "children": [{
                "value": 8,
                "name": "(有安全合规风险)资源标识:i-0d9b2118a5f4b223e",
                "namespace": null,
                "type": "ecs"
              }],
              "name": "资源类型: aliyun.mongodb(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.mongodb",
              "total": 1
            }, {
              "children": [{
                "value": 6,
                "name": "(有安全合规风险)资源标识:rds-0c3f6b39801154ce8",
                "namespace": null,
                "type": "rds"
              }],
              "name": "资源类型: aliyun.rds(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.rds",
              "total": 1
            }],
            "name": "区域: 华东 2（上海）(共4 个资源)",
            "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
            "regionId": "cn-shanghai",
            "total": 4
          }, {
            "children": [{
              "children": [{
                "value": 1,
                "name": "资源标识:ecs-017c527b52bf0c656",
                "namespace": null,
                "type": "ecs"
              }],
              "name": "资源类型: aliyun.ecs(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-northwest-1",
              "resourceType": "aliyun.ecs",
              "total": 1
            }],
            "name": "区域: 华北 2（北京）(共1 个资源)",
            "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
            "regionId": "cn-beijing",
            "total": 1
          }],
          "name": "云账号: aliyun(共5 个资源)",
          "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
          "type": "aliyun",
          "total": 5
        }, {
          "children": [{
            "children": [{
              "children": [{
                "value": 10,
                "name": "(有安全合规风险)资源标识:oss-123",
                "namespace": null,
                "type": "oss"
              }],
              "name": "资源类型: aliyun.oss(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.oss",
              "total": 1
            }, {
              "children": [{
                "value": 12,
                "name": "(有安全合规风险)资源标识:disk-0871c4a9e3662835e",
                "namespace": null,
                "type": "other"
              }],
              "name": "资源类型: aliyun.disk(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.disk",
              "total": 1
            }, {
              "children": [{
                "value": 8,
                "name": "(有安全合规风险)资源标识:i-0d9b2118a5f4b223e",
                "namespace": null,
                "type": "rds"
              }],
              "name": "资源类型: aliyun.mongodb(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.mongodb",
              "total": 1
            }, {
              "children": [{
                "value": 6,
                "name": "(有安全合规风险)资源标识:rds-0c3f6b39801154ce8",
                "namespace": null,
                "type": "rds"
              }],
              "name": "资源类型: aliyun.rds(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-shanghai",
              "resourceType": "aliyun.rds",
              "total": 1
            }],
            "name": "区域: 华东 2（上海）(共4 个资源)",
            "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
            "regionId": "cn-shanghai",
            "total": 4
          }, {
            "children": [{
              "children": [{
                "value": 1,
                "name": "资源标识:ecs-017c527b52bf0c656",
                "namespace": null,
                "type": "ecs"
              }],
              "name": "资源类型: aliyun.ecs(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-northwest-1",
              "resourceType": "aliyun.ecs",
              "total": 1
            }],
            "name": "区域: 华北 2（北京）(共1 个资源)",
            "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
            "regionId": "cn-beijing",
            "total": 1
          }, {
            "children": [{
              "children": [{
                "value": 1,
                "name": "资源标识:ecs-017c527b52bf0c656",
                "namespace": null,
                "type": "ecs"
              }],
              "name": "资源类型: aliyun.ecs(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-northwest-1",
              "resourceType": "aliyun.ecs",
              "total": 1
            }],
            "name": "区域: 华北 2（北京）(共1 个资源)",
            "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
            "regionId": "cn-beijing",
            "total": 1
          }, {
            "children": [{
              "children": [{
                "value": 1,
                "name": "资源标识:ecs-017c527b52bf0c656",
                "namespace": null,
                "type": "ecs"
              }],
              "name": "资源类型: aliyun.ecs(共1 个资源)",
              "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
              "regionId": "cn-northwest-1",
              "resourceType": "aliyun.ecs",
              "total": 1
            }],
            "name": "区域: 华北 2（北京）(共1 个资源)",
            "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
            "regionId": "cn-beijing",
            "total": 1
          }],
          "name": "云账号: aliyun(共5 个资源)",
          "accountId": "02f21cf0-255b-449f-a0d4-39b1d7fcbd6b",
          "type": "aliyun",
          "total": 7
        }], "name": "Cloud"
      }
      // this.result = this.$get(cloudTopologyUrl, response => {
        //let data = response.data;
        if (testData.children.length == 0) return;

        this.calculatePlatform(testData.children);

        this.initSVG(testData.children);

      // });

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
    //
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
    //
    createStage(d3, svg, cellSize, cellCountX, cellCountY) {
      return svg.attr('viewBox', [
        -cellSize * cellCountX, 0,
        cellSize * cellCountY * 2, cellSize * cellCountX
      ].join(' '));
    },
    //
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
    //
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
    //
    calculatePlatform(cloudaccounts) {
      var totalNumber = 0;
      var position0 = [1, 1]

      for (var i = 0; i < cloudaccounts.length; i++) {
        totalNumber = cloudaccounts[i].total + totalNumber;
        let regionList = cloudaccounts[i].children;
        let regionListN = []
        regionList.forEach((d, rindex) => {
          var resourceTypeList = d.children
          var resourceTypeListN = []
          var availableSquareList = []
          var basePlat = {}
          var regionNode = Object.assign({}, d);
          this.rebuildSublist(resourceTypeList);
          this.initSubAvailabList(availableSquareList, [0, 0], resourceTypeList[0], basePlat)

          resourceTypeList.forEach((item, idx) => {
            var rnode = this.getAiliableSquareList(availableSquareList, resourceTypeList[0], item, basePlat, 1)
            rnode.regionId = item.regionId
            item.posNode = rnode;
            resourceTypeListN.push(rnode)
          })

          var regionname = d.name;
          var regionId = d.regionId;
          regionList[rindex].area = basePlat.xasis * basePlat.yasis
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
        this.initSubAvailabList(availableRegionList, [0, 0], regionList[0], baseRegionPlat)

        regionList.forEach((item) => {
          var rnode = this.getAiliableSquareList(availableRegionList, regionList[0], item, baseRegionPlat)
          rnode.accountId = item.accountId
          item.posNode = rnode;
          regionListN.push(rnode)
        })
        cloudaccounts[i].area = baseRegionPlat.xasis * baseRegionPlat.yasis
        cloudaccounts[i].sizeX = baseRegionPlat.xasis
        cloudaccounts[i].sizeY = baseRegionPlat.yasis
        cloudaccounts[i].regionList = regionListN;
      }
      cloudaccounts.sort((a, b) => {
        return b.area - a.area
      })
      var availableRegionList = []
      var baseCloudPlat = {}
      this.initSubAvailabList(availableRegionList, position0, cloudaccounts[0], baseCloudPlat)

      cloudaccounts.forEach((item, i) => {
        var rnode = this.getAiliableSquareList(availableRegionList, cloudaccounts[0], item, baseCloudPlat)
        item.basePosition = position0;
        rnode.sizeY = rnode.sizeY + 2
        item.sizeY = rnode.sizeY
        if (i != 0) {
          if (rnode.starPosition[1] != position0[1])
            rnode.starPosition[1] = rnode.starPosition[1] + 2

        }
        item.posNode = rnode;

      })
      cellCountX = math.max(baseCloudPlat.xasis, baseCloudPlat.yasis) + 5
      cellCountY = cellCountX
      this.calActualPosition(cloudaccounts)

      return cloudaccounts;
    },
    //
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
    //
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
    //
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
    //
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
                .attr('imageType', imageType)
              // .attr('transform', ' scale(0.5)')
            })
            .merge(boxes)
            .each(function (d) {
              let pos = pointTransform([(d.position[0] - 1) * cellSize - cellSize, (d.position[1]) * cellSize - cellSize])
              d3.select(this).select('image')
                .attr('x', pos[0]).attr('y', pos[1])
            }).on('click', function (event, d) {
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
    //
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
    //
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
    //
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
    //
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
    //加备注
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
    //
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
                  .attr('stroke-width', '2');
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
    //
    _toRadians() {
      return (
        (degrees) => degrees * (Math.PI / 180)
      )
    },
    //
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
    //
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
          rgtext.position = [item2.posX - 1, item2.posY + item2.sizeY - 1]
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
              let box = {}
              box.position = [item3.posX + i4 % (item3.sizeX - 1), item3.posY + Math.floor(i4 / (item3.sizeY - 1))]
              // box.type = item3.resourceType
              box.iconType = 'type1'
              box.id = item4.name.split(':')[1]
              let typeStr = item3.resourceType.toLowerCase()
              box.type = item4.type
              if (typeStr.includes("ec2") || typeStr.includes("ecs") || typeStr.includes("servericon")) {
                box.type = 'server'
              }
              box.riskType = 'normal'
              if (item4.value > 1) {
                box.riskType = 'risk'
              }
              if (item4.value == 1) {
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
              nodetext.border = false
              nodetext.id = box.id + 'text'
              textData.push(nodetext)
            })
          })

        })
      })

    },
    //
    initSubAvailabList(availableSquareList, starPosition, firstSquare, basePlat) {
      var squareNode = {}
      squareNode.starPosition = starPosition
      squareNode.sizeX = firstSquare.sizeX;
      squareNode.sizeY = firstSquare.sizeY;
      squareNode.area = squareNode.sizeX * squareNode.sizeY

      basePlat.starPosition = starPosition;
      basePlat.xasis = firstSquare.sizeX;
      basePlat.yasis = firstSquare.sizeY;

      availableSquareList.push(squareNode)
    },
    //
    rebuildSublist(sList) {
      sList.forEach((item, index) => {
        var totalItem = item.total;
        var rSizeX = Math.ceil(Math.sqrt(totalItem)) + 1
        var rSizeY = rSizeX
        sList[index].area = rSizeX * rSizeY
        sList[index].sizeX = rSizeX
        sList[index].sizeY = rSizeY
      })
      sList.sort((a, b) => {
        return b.area - a.area
      })
    },
    //
    calActualPosition(xList) {
      xList.forEach((item) => {
        item.posX = item.basePosition[0] + item.posNode.starPosition[0]
        item.posY = item.basePosition[1] + item.posNode.starPosition[1]
        var nextBasePos = [item.posX, item.posY]
        item.children.forEach((item1) => {
          item1.posX = nextBasePos[0] + item1.posNode.starPosition[0]
          item1.posY = nextBasePos[1] + item1.posNode.starPosition[1]
          var nextBasePos1 = [item1.posX, item1.posY]
          item1.children.forEach((item2) => {
            item2.posX = nextBasePos1[0] + item2.posNode.starPosition[0]
            item2.posY = nextBasePos1[1] + item2.posNode.starPosition[1]
          })

        })
      })
    },
    //
    getAiliableSquareList(availableSquareList, baseSquare, square, basePlat, padding) {
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
          basePlat.xasis = basePlat.xasis + baseSquare.sizeX;
        } else {
          Object.assign(squareNode, {starPosition: [basePlat.starPosition[0], basePlat.yasis + basePlat.starPosition[1]]});
          basePlat.yasis = basePlat.yasis + baseSquare.sizeY
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
    //选择云账号
    topoSwitch(accountId) {
      this.accountId = accountId;
      this.init();
    },
    //选择云账号名称
    selectAccount(accountId, accountName) {
      this.accountId = accountId;
      this.accountName = accountName;
    },
  },

  mounted() {
    this.init();
    //弹框：全局点击事件监听，因为On click方法里获取的this是当前点击元素，获取不到this.dialogVisible，没办法做弹框
    document.addEventListener('click', (e) => {
      let thisClassName = e.target.className;
      if (thisClassName.baseVal === 'clicked-box') {
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
</style>
