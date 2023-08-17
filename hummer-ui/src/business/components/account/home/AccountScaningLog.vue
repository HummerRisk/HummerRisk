<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <scan-header  :condition.sync="condition" @search="search"
                        :title="$t('scaning.scaning_log')"
                        @back="back" :backTip="$t('account.back_account')"
                        :show-save="false" :show-create="false" :show-setting="false"/>
        </template>

        <div>
          <el-card class="el-box-card">
            <div slot="header" class="clearfix">
              <span>
                <el-button type="primary" size="small" @click="scanGroup()">{{ $t('scaning.perform_detection') }}</el-button>
                <img :src="require(`@/assets/img/platform/${accountWithGroup.pluginIcon}`)" style=" margin-left: 15px;width: 16px; height: 16px; vertical-align:middle" alt=""/>
             &nbsp;&nbsp; {{ accountWithGroup.pluginName }} {{ $t('rule.rule_set') }} | {{ accountWithGroup.name }}
                <I style="color: red;">{{ $t('scaning.select_scaning') }}</I>
              </span>
              <el-button style="float: right; padding: 3px 0" type="text" @click="handleCheckAllByAccount">{{ $t('account.i18n_sync_all') }}</el-button>
            </div>
            <el-checkbox-group v-model="checkedGroups">
              <el-checkbox v-for="(group, index) in groups" :label="group.id" :value="group.id" :key="index" border >
                {{ group.name }}
              </el-checkbox>
            </el-checkbox-group>
          </el-card>
        </div>

        <el-row>
          <el-col :span="4">
            <div style="height: 600px;margin: 25px;">
              <el-steps direction="vertical" :active="cloudProject.processStep">
                <el-step :title="$t('scaning.initial_configuration')" :description="$t('scaning.waiting_configuration')">
                </el-step>
                <el-step :title="$t('scaning.perform_detection')" :description="$t('scaning.waiting_perform')">
                </el-step>
                <el-step :title="$t('scaning.output_log')" :description="$t('scaning.execute_output_log')">
                </el-step>
              </el-steps>
            </div>
          </el-col>
          <el-col :span="20">
            <div style="height: 221px;margin: 54px 0;">
              <div v-for="(cloudProcess, index) in cloudProcessList" :key="cloudProcess.processOrder">
                <el-row :id="'row' + cloudProcess.processOrder" v-if="cloudProcess.processOrder < 5" style="margin: 15px 0;">
                  <el-col :span="22">
                    <h5 v-bind:class="{
                                  'font-ing': cloudProcess.processRate > 0 && cloudProcess.processRate < 100,
                                  'font-end': cloudProcess.processRate === 0 || cloudProcess.processRate === 100 }">
                      <i v-if="cloudProcess.processRate > 0 && cloudProcess.processRate < 100" class="el-icon-loading"></i>
                      <i v-if="cloudProcess.processRate === 0" class="el-icon-video-pause"></i>
                      <i v-if="cloudProcess.processRate === 100" class="el-icon-circle-check"></i>
                      {{ cloudProcess.processName }}
                      <I v-if="cloudProcess.processRate > 0 && cloudProcess.processRate !== 100" style="float: right;margin-right: 23px;">{{ cloudProcess.execTime + $t('second.title') }}</I>
                      <I v-if="cloudProcess.processRate === 100" style="float: right;margin-right: 23px;">{{ cloudProcess.execTime }}</I>
                    </h5>
                    <el-progress :percentage="cloudProcess.processRate" :color="customColorMethod"></el-progress>
                  </el-col>
                </el-row>
              </div>
            </div>
            <div style="height: 231px;margin: 50px 0;">
              <div v-for="(cloudProcess, index) in cloudProcessList" :key="cloudProcess.processOrder">
                <el-row :id="'row' + cloudProcess.processOrder" v-if="cloudProcess.processOrder > 4" style="margin: 15px 0;">
                  <el-col :span="22">
                    <h5 v-bind:class="{
                                  'font-ing': cloudProcess.processRate > 0 && cloudProcess.processRate < 100,
                                  'font-end': cloudProcess.processRate === 0 || cloudProcess.processRate === 100 }">
                      <i v-if="cloudProcess.processRate > 0 && cloudProcess.processRate < 100" class="el-icon-loading"></i>
                      <i v-if="cloudProcess.processRate === 0" class="el-icon-video-pause"></i>
                      <i v-if="cloudProcess.processRate === 100" class="el-icon-circle-check"></i>
                      {{ cloudProcess.processName }}
                      <I v-if="cloudProcess.processRate > 0 && cloudProcess.processRate !== 100" style="float: right;margin-right: 23px;">{{ cloudProcess.execTime + $t('second.title') }}</I>
                      <I v-if="cloudProcess.processRate === 100" style="float: right;margin-right: 23px;">{{ cloudProcess.execTime }}</I>
                    </h5>
                    <el-progress :percentage="cloudProcess.processRate" :color="customColorMethod"></el-progress>
                  </el-col>
                </el-row>
              </div>
            </div>
            <div style="height: 300px;margin: 50px 0;">
              <el-row>
                <el-col :span="16">
                  <codemirror ref="cmEditor" v-model="script" class="code-mirror" :options="cmOptions" />
                </el-col>
                <el-col :span="6">
                  <el-card class="box-card">
                    <el-result v-if="!cloudProject.resultStatus" icon="info" :title="$t('scaning.no_scan')" :subTitle="$t('scaning.no_scan_ing')">
                      <template slot="extra">
                        <el-button type="primary" size="medium" @click="goResult">{{ $t('scaning.no_scan') }}</el-button>
                      </template>
                    </el-result>
                    <el-result v-if="cloudProject.resultStatus === 'APPROVED'" icon="warning" :title="$t('scaning.start_scan')" :subTitle="$t('scaning.start_scan_ing')">
                      <template slot="extra">
                        <el-button type="primary" size="medium" @click="goResult">{{ $t('scaning.start_scan') }}</el-button>
                      </template>
                    </el-result>
                    <el-result v-if="cloudProject.resultStatus === 'FINISHED'" icon="success" :title="$t('scaning.create_end')" :subTitle="$t('scaning.go_scan_result')">
                      <template slot="extra">
                        <el-button type="primary" size="medium" @click="goResult">{{ $t('scaning.go_result') }}</el-button>
                      </template>
                    </el-result>
                  </el-card>
                </el-col>
              </el-row>
            </div>
          </el-col>
        </el-row>
      </el-card>

    </main-container>
</template>

<script>
import TableOperators from "@/business/components/common/components/TableOperators";
import MainContainer from "@/business/components/common/components/MainContainer";
import Container from "@/business/components/common/components/Container";
import ScanHeader from "../head/ScanHeader";
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableOperator from "@/business/components/common/components/TableOperator";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {_sort} from "@/common/js/utils";
import {
  cloudTaskLogByAccountIdUrl,
  cloudTaskLogByIdUrl,
  getAccountUrl,
  ruleListUrl,
} from "@/api/cloud/account/account";
import {groupsByAccountId, ruleScanUrl} from "@/api/cloud/rule/rule";
import FakeProgress from "fake-progress";
import {processCreateUrl, processListUrl, projectScanUrl} from "@/api/cloud/project/project";

/* eslint-disable */
  export default {
    name: "AccountScan",
    components: {
      TableOperators,
      MainContainer,
      Container,
      ScanHeader,
      TablePagination,
      TableOperator,
      DialogFooter
    },
    props: {
      account: {},
    },
    watch: {
      '$route': 'init'
    },
    data() {
      return {
        result: {},
        form: {},
        regions: {},
        tableData: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        selectIds: new Set(),
        regionsVisible: false,
        direction: 'rtl',
        rule: {
        },
        accountId: '',
        accountName: '',
        condition: {
          components: []
        },
        accountWithGroup: {pluginIcon: 'aliyun.png'},
        checkedGroups: [],
        groups: [],
        checkAll: false,
        ruleList: [],
        params: [],
        cmOptions: {
          tabSize: 4,
          mode: {
            name: 'json',
            json: true
          },
          theme: 'bespin',
          lineNumbers: true,
          line: true,
          indentWithTabs: true,
          styleActiveLine: true, // 高亮选中行
          //是否为只读,如果为"nocursor" 不仅仅为只读 连光标都无法在区域聚焦
          readOnly: true,
          viewportMargin: 30
        },
        customColor: '#409eff',
        customColors: [
          {color: '#f56c6c', percentage: 20},
          {color: '#e6a23c', percentage: 40},
          {color: '#5cb87a', percentage: 60},
          {color: '#1989fa', percentage: 80},
          {color: '#6f7ad3', percentage: 100}
        ],
        timer: '',
        script: '',
        cloudProject: {
          resultStatus: null,
          processStep: 1,
        },
        projectId: '',
        cloudProcessList: [
          {
            processStep: 1,
            processOrder: 1,
            processName: this.$t('scaning.init_cloud_account_info'),
            processRate: 0,
            execTime: 0,
            fake: new FakeProgress({
              timeConstant : 3000,
              autoStart : true
            }),
          },
          {
            processStep: 1,
            processOrder: 2,
            processName: this.$t('scaning.init_cloud_region_info'),
            processRate: 0,
            execTime: 0,
            fake: new FakeProgress({
              timeConstant : 3000,
              autoStart : true
            }),
          },
          {
            processStep: 1,
            processOrder: 3,
            processName: this.$t('scaning.init_cloud_group_info'),
            processRate: 0,
            execTime: 0,
            fake: new FakeProgress({
              timeConstant : 3000,
              autoStart : true
            }),
          },
          {
            processStep: 1,
            processOrder: 4,
            processName: this.$t('scaning.init_cloud_rule_info'),
            processRate: 0,
            execTime: 0,
            fake: new FakeProgress({
              timeConstant : 3000,
              autoStart : true
            }),
          },
          {
            processStep: 2,
            processOrder: 5,
            processName: this.$t('scaning.init_env_info'),
            processRate: 0,
            execTime: 0,
            fake: new FakeProgress({
              timeConstant : 5000,
              autoStart : true
            }),
          },
          {
            processStep: 2,
            processOrder: 6,
            processName: this.$t('scaning.create_scan_info'),
            processRate: 0,
            execTime: 0,
            fake: new FakeProgress({
              timeConstant : 5000,
              autoStart : true
            }),
          },
          {
            processStep: 2,
            processOrder: 7,
            processName: this.$t('scaning.create_scan_group'),
            processRate: 0,
            execTime: 0,
            fake: new FakeProgress({
              timeConstant : 10000,
              autoStart : true
            }),
          },
          {
            processStep: 2,
            processOrder: 8,
            processName: this.$t('scaning.create_scan_task'),
            processRate: 0,
            execTime: 0,
            fake: new FakeProgress({
              timeConstant : 10000,
              autoStart : true
            }),
          },
          {
            processStep: 3,
            processOrder: 9,
            processName: this.$t('scaning.start_scan_task'),
            processRate: 0,
            execTime: 0,
            fake: new FakeProgress({
              timeConstant : 10000,
              autoStart : true
            }),
          },
        ],
        cloudProcessLogList: [],
      }
    },
    created() {
      this.accountId = this.$route.params.id;
      this.init();
    },
    methods: {
      init() {
        this.search();
      },
      search() {
        this.$get(getAccountUrl + this.accountId,res => {
          this.accountWithGroup = res.data;
          this.initGroups(this.accountWithGroup.pluginId);
        });
      },
      sort(column) {
        _sort(column, this.condition);
        this.init();
      },
      select(selection) {
        this.selectIds.clear()
        selection.forEach(s => {
          this.selectIds.add(s.id)
        })
      },
      back () {
        this.$router.push({
          path: '/account/cloudaccount',
        }).catch(error => error);
      },
      goResult() {
        if (!this.cloudProject.resultStatus) {
          this.$warning(this.$t('scaning.no_scan_ing'));
          return;
        } else if (this.cloudProject.resultStatus === 'APPROVED') {
          this.$warning(this.$t('scaning.start_scan_ing'));
          return;
        } else if (this.cloudProject.resultStatus === 'FINISHED') {
          this.$router.push({
            name: 'cloudResult',
            params: {id: this.accountWithGroup.id},
          }).catch(error => error);
        }
      },
      customColorMethod(percentage) {
        if (percentage < 30) {
          return '#909399';
        } else if (percentage < 70) {
          return '#e6a23c';
        } else {
          return '#67c23a';
        }
      },
      getStatus() {
        let params = {
          projectId: this.projectId
        };
        this.$post(processListUrl, params, res => {
          let data = res.data;
          console.log(data.processOrder, data.resultStatus, data)
          this.cloudProject = {
            id: data.id,
            projectId: data.projectId,
            processStep: data.processStep,
            processName: data.processName,
            processRate: data.processRate,
            processOrder: data.processOrder,
            execTime: data.execTime,
            resultStatus: data.resultStatus
          };
          console.log(data.processStep === 3 && data.processOrder === 9 && data.resultStatus === 'FINISHED')
          if (data.processStep === 3 && data.processOrder === 9 && data.resultStatus === 'FINISHED') {
            this.goAnchor("row9");
            clearInterval(this.timer);
          } else {
            this.cloudProcessLogList = data.cloudProcessLogList;//日志
            for (let cloudProcessLog of this.cloudProcessLogList) {
              let row = this.timestampFormatDate(cloudProcessLog.createTime) + ' ' + cloudProcessLog.operator + ' ' + cloudProcessLog.operation + ' ' + cloudProcessLog.output;
              if (!this.script.includes(row)) {
                this.showScript(row);
              }
            }
            for (let cloudProcess of this.cloudProcessList) {//静态模拟数据
              if (cloudProcess.processOrder === data.processOrder && cloudProcess.processStep === data.processStep) {
                cloudProcess.id = data.id;
                cloudProcess.projectId = data.projectId;
                cloudProcess.processName = data.processName;
                cloudProcess.processRate = data.processRate;
                cloudProcess.status = data.resultStatus;

                //进度条前端一直转不会到100%
                cloudProcess.processRate = parseInt(cloudProcess.fake.progress * 100);
                let randomNumber = Math.round(Math.random() * 4000 ) + 1000;//毫秒
                cloudProcess.execTime = parseInt(cloudProcess.execTime);
                setTimeout(() => {
                  cloudProcess.execTime++;
                }, randomNumber);
                cloudProcess.processRate = 100;
                cloudProcess.fake.end();

                console.log("cloudProcess", cloudProcess)
                this.$post(processCreateUrl, cloudProcess, response => {
                  console.log(response.data)
                  cloudProcess.id = response.data.id;
                  // cloudProcess = response.data;
                });
              }
            }
          }
        });
      },
      initGroups(pluginId) {
        this.result = this.$get(groupsByAccountId + pluginId,response => {
          this.groups = response.data;
        });
      },
      scanGroup () {
        let account = this.$t('account.one_scan') + this.$t('account.cloud_account');
        this.$alert( account + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              if (this.checkedGroups.length === 0) {
                this.$warning(this.$t('account.please_choose_rule_group'));
                return;
              }
              this.cloudProject.resultStatus = 1;
              let params = {
                accountId: this.accountWithGroup.id,
                groups: this.checkedGroups
              }
              this.$post(projectScanUrl, params, res => {
                let data = res.data;
                this.projectId = data;
                this.timer = setInterval(this.getStatus, 1000);
              });
            }
          }
        });
      },
      handleCheckAllByAccount() {
        if (this.checkedGroups.length === this.groups.length) {
          this.checkedGroups = [];
        } else {
          let arr = [];
          this.checkedGroups = [];
          for (let group of this.groups) {
            arr.push(group.id);
          }
          let concatArr = this.checkedGroups.concat(arr);
          this.checkedGroups = Array.from(concatArr);
        }
      },
      showScript(data) {
        this.script = this.script + data + "\r";
        // 获取滚动信息  注意是cmEditor.codemirror
        let sc = this.$refs.cmEditor.codemirror.getScrollInfo();
        // 滚动 注意是cmEditor.codemirror
        this.$refs.cmEditor.codemirror.scrollTo(sc.left,( sc.height + sc.top));
      },
      timestampFormatDate (timestamp) {
        if (!timestamp) {
          return timestamp;
        }

        let date = new Date(timestamp);

        let y = date.getFullYear();

        let MM = date.getMonth() + 1;
        MM = MM < 10 ? ('0' + MM) : MM;

        let d = date.getDate();
        d = d < 10 ? ('0' + d) : d;

        let h = date.getHours();
        h = h < 10 ? ('0' + h) : h;

        let m = date.getMinutes();
        m = m < 10 ? ('0' + m) : m;

        let s = date.getSeconds();
        s = s < 10 ? ('0' + s) : s;

        return y + '-' + MM + '-' + d + ' ' + h + ':' + m + ':' + s;
      },
      goAnchor(id) {
        let anchor = document.getElementById(id);
        anchor.scrollIntoView();
      },
    },
    computed: {
      codemirror() {
        return this.$refs.cmEditor.codemirror;
      }
    },
  }
</script>

<style scoped>
  .color1 {
    color: #f56c6c;
  }
  .color2 {
    color: #e6a23c;
  }
  .color3 {
    color: #5cb87a;
  }
  .color4 {
    color: #1989fa;
  }
  .color5 {
    color: #6f7ad3;
  }
  .font-ing {
    color: #1989fa;
  }
  .font-end {
    color: #000000;
  }
  .box-card >>> .el-card__body {
    padding: 0;
    height: 300px;
  }
  .el-box-card {
    margin: 10px 0;
  }
  .el-box-card >>> .el-checkbox {
    margin: 5px 0;
  }
  .el-box-card >>> .el-card__body {
    padding: 10px;
    marigin: 5px;
  }
  .vue-codemirror, .CodeMirror {
    height: 100% !important;
  }
  /deep/ :focus{outline:0;}
</style>
