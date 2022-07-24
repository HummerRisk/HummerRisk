<template>
  <div v-loading="result.loading">

    <el-card>

      <div class="filter-wrapper">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-select v-model="selectedTask" filterable clearable :placeholder="$t('task.please_filter_report')" style="width: 50%;" @change="search">
              <el-option
                v-for="item in tasks"
                :key="item.id"
                :label="item.taskName"
                :value="item">
              </el-option>
            </el-select>
            <el-button type="primary" plain icon="el-icon-download" @click="genReport"> {{ $t('task.download_report') }}</el-button>
          </el-col>
          <el-col :span="12" v-if="selectedTask">
            <el-row :gutter="20">
              <el-col :span="8" style="padding: 12px 0;color: #00bb00;">
                {{ $t('task.task_name') }} : {{ selectedTask.taskName }}
              </el-col>
              <el-col :span="8" style="padding: 12px 0;color: #00bb00;">
                {{ $t('commons.create_time') }} : <i class="el-icon-time"></i> {{ selectedTask.createTime | timestampFormatDate }}
              </el-col>
              <el-col :span="8">
                <el-button plain type="success" icon="el-icon-success">
                  {{ $t('resource.i18n_done') }}
                </el-button>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </div>

      <el-divider><i class="el-icon-tickets"></i></el-divider>

      <div ref="taskReport">
        <el-row :gutter="20">
          <el-image src="https://company.hummercloud.com/hummririsk/logo-dark.png" style="width: 40%;margin: 15px;"></el-image>
        </el-row>
        <el-row :gutter="20" style="color: #888;margin: 15px;">
          {{ $t('task.report_desc') }}
        </el-row>
        <el-row :gutter="20" style="margin: 15px;font-size: 20px;">
          <el-link type="primary" style="color: #000be9;font-size: 20px;" href="https://github.com/HummerRisk/HummerRisk" target="_blank">{{ 'Github Project' }}</el-link> |
          <el-link type="primary" style="color: #000be9;font-size: 20px;" href="https://docs.hummerrisk.com/" target="_blank">{{ 'HummerRisk Docs' }}</el-link> |
          <el-link type="primary" style="color: #000be9;font-size: 20px;" href="https://github.com/HummerRisk/HummerRisk/issues" target="_blank">{{ 'Getting Help: Github issues' }}</el-link>
        </el-row>
        <el-row :gutter="20" style="margin: 15px;">
          <h2>Project:&nbsp;</h2>
          <div style="margin: 10px 0 0 0;">
            Scan Information:<br/>
            <ul style="margin-left: 60px;">
              <li><i>HummerRisk version</i>: {{ version }}</li>
              <li><i>Report Generated On</i>: Fri, 22 Jul 2022 04:26:45 +0800</li>
              <li><i>Dependencies Scanned</i>:&nbsp;21 (21 unique)</li>
              <li><i>Vulnerable Dependencies</i>:&nbsp;<span id="vulnerableCount">14</span></li>
              <li><i>Vulnerabilities Found</i>:&nbsp;45</li>
              <li><i>Vulnerabilities Suppressed</i>:&nbsp;0</li>
              <li><i>NVD CVE Checked</i>: 2022-07-22T04:26:34</li>
              <li><i>NVD CVE Modified</i>: 2022-07-22T04:00:01</li>
              <li><i>VersionCheckOn</i>: 2022-07-03T20:50:46</li>
            </ul>
          </div>
        </el-row>
        <el-row :gutter="20" style="margin: 15px;">
          <div style="margin: 10px 0 0 0;">
            <el-collapse v-model="activeNames">
              <el-collapse-item name="1">
                <template slot="title">
                  {{ $t('account.account_setting') }} <i class="el-icon-cloudy" style="margin-left: 5px;padding-top: 3px;"></i>
                </template>
                <div>
                  <h2>Summary:&nbsp;</h2>
                  <div style="margin: 10px 0 0 0;">
                    <el-table :data="tableData" border stripe style="width: 100%">
                      <el-table-column prop="date" label="日期" width="180"></el-table-column>
                      <el-table-column prop="name" label="姓名" width="180"></el-table-column>
                      <el-table-column prop="address" label="地址"></el-table-column>
                    </el-table>
                  </div>
                </div>
                <div style="margin: 10px 0 0 0;">
                  <h2>Details:&nbsp;</h2>
                  <div style="margin: 10px 0 0 0;">
                    <el-card class="box-card">
                      <div slot="header" class="clearfix">
                        <div class="icon-title">
                          <span>{{ 'H' }}</span>
<!--                          <span>{{ currentUser.name.substring(0, 1) }}</span>-->
                        </div>
                        <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>
                      </div>
                      <div class="text item">

                      </div>
                      <div class="text item">

                      </div>
                    </el-card>
                  </div>
                </div>
              </el-collapse-item>
              <el-collapse-item name="2">
                <template slot="title">
                  {{ $t('vuln.vuln_setting') }} <i class="el-icon-crop" style="margin-left: 5px;padding-top: 2px;"></i>
                </template>
                <div></div>
                <div></div>
              </el-collapse-item>
              <el-collapse-item name="3">
                <template slot="title">
                  {{ $t('server.server_setting') }} <i class="el-icon-monitor" style="margin-left: 5px;padding-top: 2px;"></i>
                </template>
                <div></div>
                <div></div>
                <div></div>
              </el-collapse-item>
              <el-collapse-item name="4">
                <template slot="title">
                  {{ $t('image.image_scan') }} <i class="el-icon-picture-outline" style="margin-left: 5px;padding-top: 2px;"></i>
                </template>
                <div></div>
                <div></div>
              </el-collapse-item>
              <el-collapse-item name="5">
                <template slot="title">
                  {{ $t('package.package_scan') }} <i class="el-icon-box" style="margin-left: 5px;padding-top: 2px;"></i>
                </template>

                <div class="filter-wrapper">
                  <el-input
                    class="search"
                    type="text"
                    size="small"
                    :placeholder="$t('task.filter_okr')"
                    prefix-icon="el-icon-search"
                    maxlength="60"
                    v-model="filterText" clearable/>
                </div>

                <vue-okr-tree
                  ref="tree"
                  :data="testData"
                  :left-data="testLeftData"
                  only-both-tree
                  direction="horizontal"
                  show-collapsable
                  node-key="id"
                  label-class-name='no-padding'
                  default-expand-all
                  :render-content="renderContent"
                  :filter-node-method="filterNode"
                ></vue-okr-tree>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-row>
      </div>

    </el-card>

  </div>
</template>

<script>
//OKR树
import {VueOkrTree} from 'vue-okr-tree';
/* eslint-disable */
export default {
  components: {
    VueOkrTree,
  },
  data () {
    return {
      result: {},
      loading: false,
      filterText: '',
      tasks: [],
      selectedTask: null,
      activeNames: ['1','2','3','4','5'],
      version: 'v1.0.0',
      testData: [{
        id: 1,
        label: 'xxx科技有有限公司',
        content: '这是一个有活力的公司',
        children: [{
          id: 2,
          label: '产品研发部',
          content: '这是一个有活力的产品研发部',
          children: [{
            id: 3,
            label: '研发-前端',
            content: '这是一个有活力的研发-前端',
          }, {
            id: 4,
            label: '研发-后端',
            content: '这是一个有活力的研发-后端',
          }, {
            id: 5,
            label: 'UI 设计',
            content: '这是一个有活力的UI 设计',
          }]
        }, {
          id: 6,
          label: '销售部',
          content: '这是一个有活力的销售部',
          children: [{
            id: 7,
            label: '销售一部',
            content: '这是一个有活力的销售一部',
          },{
            id: 8,
            label: '销售二部',
            content: '这是一个有活力的销售二部',
          }
          ]
        },{
          id: 9,
          label: '财务部',
          content: '这是一个有活力的务部',
        }]
      }],
      testLeftData: [{
        id: 1,
        label: 'xxx科技有有限公司',
        content: '这是一个有活力的公司',
        children: [{
          id: 12,
          label: '(左)产品研发部',
          content: '这是一个有活力的产品研发部',
          children: [{
            id: 13,
            label: '(左)研发-前端',
            content: '这是一个有活力的研发-前端',
          }, {
            id: 14,
            label: '(左)研发-后端',
            content: '这是一个有活力的研发-后端',
          }, {
            id: 15,
            label: '(左)UI 设计',
            content: '这是一个有活力的UI 设计',
          }]
        }, {
          id: 16,
          label: '(左)销售部',
          children: [{
            id: 17,
            label: '(左)销售一部',
            content: '这是一个有活力的销售一部',
          },{
            id: 18,
            label: '(左)销售二部',
            content: '这是一个有活力的销售二部',
          }
          ]
        },{
          id: 19,
          label: '(左)财务部',
          content: '这是一个有活力的财务部',
        }]
      }],
      tableData: [{
        date: '2016-05-02',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1518 弄'
      }, {
        date: '2016-05-04',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1517 弄'
      }, {
        date: '2016-05-01',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1519 弄'
      }, {
        date: '2016-05-03',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1516 弄'
      }],
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    }
  },
  methods: {
    init() {
      this.result = this.$get("/task/allTaskList", response => {
        this.tasks = response.data;
      });
    },
    search() {
      if (this.selectedTask) {
        this.result = this.$get("/task/report/" + this.selectedTask.id, response => {
          console.log(response.data);
        });
      }
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    genReport() {
      if(!this.selectedTask) {
        this.$warning(this.$t('task.no_selected_task'));
        return;
      }
      this.exportDom();
    },
    renderContent (h, node) {
      const cls = ['diy-wrapper']
      if (node.isCurrent) {
        cls.push('current-select')
      }
      if (node.isLeftChild) {
        cls.push('left-child')
      }
      let spanLeft = 8;
      let spanRight = 16;
      let span1 = this.$t('task.artifact');
      let span2 = this.$t('task.related_vulnerabilities');
      let span3 = this.$t('task.match_details');
      let span4 = this.$t('task.vulnerability');
      return (
        <div class={cls}>
          <div class="diy-con-name">
            <el-row>
              <el-col span={spanLeft} class="diy-con-left">{span1}</el-col>
              <el-col span={spanRight} class="diy-con-right">{node.data.content}</el-col>
            </el-row>
          </div>
          <div class="diy-con-content">
            <el-row>
              <el-col span={spanLeft} class="diy-con-left">{span2}</el-col>
              <el-col span={spanRight} class="diy-con-right">{node.data.content}</el-col>
            </el-row>
          </div>
          <div class="diy-con-name">
            <el-row>
              <el-col span={spanLeft} class="diy-con-left">{span3}</el-col>
              <el-col span={spanRight} class="diy-con-right">{node.data.content}</el-col>
            </el-row>
          </div>
          <div class="diy-con-content">
            <el-row>
              <el-col span={spanLeft} class="diy-con-left">{span4}</el-col>
              <el-col span={spanRight} class="diy-con-right">{node.data.content}</el-col>
            </el-row>
          </div>
        </div>
      )
    },
    //相关获取 dom 元素的方法
    html() {
      const temp = [
        '<!doctype html>',
        '<html>',
        '<head>',
        window.document.head.innerHTML,
        '</head>',
        '<body>',
        this.$refs.taskReport.innerHTML,
        '</body>',
        '</html>'
      ];
      return temp.join('');
    },
    //构造html页面，并使用 createObjectURL构造一个文件流并下载
    exportDom() {
      const link = document.createElement('a');
      document.body.appendChild(link);
      const url = URL.createObjectURL(new Blob([this.html()], { type: "text/plain;charset='utf-8'" }));
      link.href = url;
      link.download = 'HummerRisk-Report' + '.html';
      link.click();
      window.URL.revokeObjectURL(url);
    },
    getVersion() {
      this.$get('/system/version', response => {
        this.version = response.data;
      });
    }
  },
  created() {
    this.init();
    this.getVersion();
  },
}
</script>

<style scoped>
.el-card >>> .label-class-blue {
  color: #1989fa;
}
.el-card >>> .label-bg-blue {
  background: #1989fa;
  color: #fff;
}
.el-card >>> .diy-wrapper {
  padding:10px
}
.el-card >>> .no-padding {
  padding: 0 !important;
}
.diy-wrapper >>> .left-child {
  border: 1px solid red;
}
.el-card >>> .org-chart-node-label-inner {
  border-style: solid;
  border-left-color: #ff0000;
  border-left-width: 5px;
  border-right-color:#fff;
  border-top-color:#fff;
  border-bottom-color:#fff;
}

.el-card >>> .diy-con-name {
  margin: 8px 3px;
}

.el-card >>> .diy-con-content {
  margin: 8px 3px;
}

.el-card >>> .diy-con-left {
  text-align: left;
  color: tomato;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
  font-size: 14px;
}

.el-card >>> .diy-con-right {
  text-align: right;
  color: #888;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
  cursor:pointer;
  font-size: 12px;
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
  clear: both;
}

.box-card >>> .el-card__header {
  background-color: aliceblue;
}

.box-card {
  width: 99%;
  border-top-color: #ff0000;
  border-top-width: 5px;
}

.icon-title {
  color: #fff;
  width: 30px;
  background-color: #72dc91;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border-radius: 30px;
  font-size: 14px;
}

</style>

