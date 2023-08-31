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
          <el-card shadow="hover" class="el-box-card">
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
              <el-row id="row1" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                'font-ing': cloudProcessAccount.processRate > 0 && cloudProcessAccount.processRate < 100,
                                'font-end': cloudProcessAccount.processRate === 0 || cloudProcessAccount.processRate === 100 }">
                    <i v-if="cloudProcessAccount.processRate > 0 && cloudProcessAccount.processRate < 100" class="el-icon-loading"></i>
                    <i v-if="cloudProcessAccount.processRate === 0" class="el-icon-video-pause"></i>
                    <i v-if="cloudProcessAccount.processRate === 100" class="el-icon-circle-check"></i>
                    {{ cloudProcessAccount.processName }}
                    <I v-if="cloudProcessAccount.processRate > 0" style="float: right;margin-right: 23px;">{{ cloudProcessAccount.execTime + $t('second.title') }}</I>
                  </h5>
                  <el-progress :percentage="cloudProcessAccount.processRate" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row2" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                'font-ing': cloudProcessRegion.processRate > 0 && cloudProcessRegion.processRate < 100,
                                'font-end': cloudProcessRegion.processRate === 0 || cloudProcessRegion.processRate === 100 }">
                    <i v-if="cloudProcessRegion.processRate > 0 && cloudProcessRegion.processRate < 100" class="el-icon-loading"></i>
                    <i v-if="cloudProcessRegion.processRate === 0" class="el-icon-video-pause"></i>
                    <i v-if="cloudProcessRegion.processRate === 100" class="el-icon-circle-check"></i>
                    {{ cloudProcessRegion.processName }}
                    <I v-if="cloudProcessRegion.processRate > 0" style="float: right;margin-right: 23px;">{{ cloudProcessRegion.execTime + $t('second.title') }}</I>
                  </h5>
                  <el-progress :percentage="cloudProcessRegion.processRate" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row3" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                'font-ing': cloudProcessGroup.processRate > 0 && cloudProcessGroup.processRate < 100,
                                'font-end': cloudProcessGroup.processRate === 0 || cloudProcessGroup.processRate === 100 }">
                    <i v-if="cloudProcessGroup.processRate > 0 && cloudProcessGroup.processRate < 100" class="el-icon-loading"></i>
                    <i v-if="cloudProcessGroup.processRate === 0" class="el-icon-video-pause"></i>
                    <i v-if="cloudProcessGroup.processRate === 100" class="el-icon-circle-check"></i>
                    {{ cloudProcessGroup.processName }}
                    <I v-if="cloudProcessGroup.processRate > 0" style="float: right;margin-right: 23px;">{{ cloudProcessGroup.execTime + $t('second.title') }}</I>
                  </h5>
                  <el-progress :percentage="cloudProcessGroup.processRate" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row4" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                'font-ing': cloudProcessRule.processRate > 0 && cloudProcessRule.processRate < 100,
                                'font-end': cloudProcessRule.processRate === 0 || cloudProcessRule.processRate === 100 }">
                    <i v-if="cloudProcessRule.processRate > 0 && cloudProcessRule.processRate < 100" class="el-icon-loading"></i>
                    <i v-if="cloudProcessRule.processRate === 0" class="el-icon-video-pause"></i>
                    <i v-if="cloudProcessRule.processRate === 100" class="el-icon-circle-check"></i>
                    {{ cloudProcessRule.processName }}
                    <I v-if="cloudProcessRule.processRate > 0" style="float: right;margin-right: 23px;">{{ cloudProcessRule.execTime + $t('second.title') }}</I>
                  </h5>
                  <el-progress :percentage="cloudProcessRule.processRate" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
            </div>
            <div style="height: 231px;margin: 50px 0;">
              <el-row id="row5" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                'font-ing': cloudProcessEnv.processRate > 0 && cloudProcessEnv.processRate < 100,
                                'font-end': cloudProcessEnv.processRate === 0 || cloudProcessEnv.processRate === 100 }">
                    <i v-if="cloudProcessEnv.processRate > 0 && cloudProcessEnv.processRate < 100" class="el-icon-loading"></i>
                    <i v-if="cloudProcessEnv.processRate === 0" class="el-icon-video-pause"></i>
                    <i v-if="cloudProcessEnv.processRate === 100" class="el-icon-circle-check"></i>
                    {{ cloudProcessEnv.processName }}
                    <I v-if="cloudProcessEnv.processRate > 0" style="float: right;margin-right: 23px;">{{ cloudProcessEnv.execTime + $t('second.title') }}</I>
                  </h5>
                  <el-progress :percentage="cloudProcessEnv.processRate" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row6" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                'font-ing': cloudProcessScan.processRate > 0 && cloudProcessScan.processRate < 100,
                                'font-end': cloudProcessScan.processRate === 0 || cloudProcessScan.processRate === 100 }">
                    <i v-if="cloudProcessScan.processRate > 0 && cloudProcessScan.processRate < 100" class="el-icon-loading"></i>
                    <i v-if="cloudProcessScan.processRate === 0" class="el-icon-video-pause"></i>
                    <i v-if="cloudProcessScan.processRate === 100" class="el-icon-circle-check"></i>
                    {{ cloudProcessScan.processName }}
                    <I v-if="cloudProcessScan.processRate > 0" style="float: right;margin-right: 23px;">{{ cloudProcessScan.execTime + $t('second.title') }}</I>
                  </h5>
                  <el-progress :percentage="cloudProcessScan.processRate" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row7" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                'font-ing': cloudProcessScanGroup.processRate > 0 && cloudProcessScanGroup.processRate < 100,
                                'font-end': cloudProcessScanGroup.processRate === 0 || cloudProcessScanGroup.processRate === 100 }">
                    <i v-if="cloudProcessScanGroup.processRate > 0 && cloudProcessScanGroup.processRate < 100" class="el-icon-loading"></i>
                    <i v-if="cloudProcessScanGroup.processRate === 0" class="el-icon-video-pause"></i>
                    <i v-if="cloudProcessScanGroup.processRate === 100" class="el-icon-circle-check"></i>
                    {{ cloudProcessScanGroup.processName }}
                    <I v-if="cloudProcessScanGroup.processRate > 0" style="float: right;margin-right: 23px;">{{ cloudProcessScanGroup.execTime + $t('second.title') }}</I>
                  </h5>
                  <el-progress :percentage="cloudProcessScanGroup.processRate" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row8" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                'font-ing': cloudProcessScanTask.processRate > 0 && cloudProcessScanTask.processRate < 100,
                                'font-end': cloudProcessScanTask.processRate === 0 || cloudProcessScanTask.processRate === 100 }">
                    <i v-if="cloudProcessScanTask.processRate > 0 && cloudProcessScanTask.processRate < 100" class="el-icon-loading"></i>
                    <i v-if="cloudProcessScanTask.processRate === 0" class="el-icon-video-pause"></i>
                    <i v-if="cloudProcessScanTask.processRate === 100" class="el-icon-circle-check"></i>
                    {{ cloudProcessScanTask.processName }}
                    <I v-if="cloudProcessScanTask.processRate > 0" style="float: right;margin-right: 23px;">{{ cloudProcessScanTask.execTime + $t('second.title') }}</I>
                  </h5>
                  <el-progress :percentage="cloudProcessScanTask.processRate" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row9" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                'font-ing': cloudProcessStartTask.processRate > 0 && cloudProcessStartTask.processRate < 100,
                                'font-end': cloudProcessStartTask.processRate === 0 || cloudProcessStartTask.processRate === 100 }">
                    <i v-if="cloudProcessStartTask.processRate > 0 && cloudProcessStartTask.processRate < 100" class="el-icon-loading"></i>
                    <i v-if="cloudProcessStartTask.processRate === 0" class="el-icon-video-pause"></i>
                    <i v-if="cloudProcessStartTask.processRate === 100" class="el-icon-circle-check"></i>
                    {{ cloudProcessStartTask.processName }}
                    <I v-if="cloudProcessStartTask.processRate > 0" style="float: right;margin-right: 23px;">{{ cloudProcessStartTask.execTime + $t('second.title') }}</I>
                  </h5>
                  <el-progress :percentage="cloudProcessStartTask.processRate" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
            </div>
            <div style="height: 300px;margin: 50px 0;">
              <el-row>
                <el-col :span="16">
                  <codemirror ref="cmEditor" v-model="script" class="code-mirror" :options="cmOptions" />
                </el-col>
                <el-col :span="6">
                  <el-card shadow="hover" class="box-card">
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
import {
  processCreateUrl,
  processListUrl,
  processLogListUrl,
  processUpdateUrl,
  projectScanUrl
} from "@/api/cloud/project/project";

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
        script: '',
        cloudProject: {
          resultStatus: null,
          processStep: 1,
          processOrder: 1,
        },
        projectId: '',
        cloudProcessAccount: {
          processStep: 1,
          processOrder: 1,
          processName: this.$t('scaning.init_cloud_account_info'),
          processRate: 0,
          execTime: 0,
          fake: new FakeProgress({
            timeConstant : 3000,
            autoStart : true
          }),
          intervalId: '',
        },
        cloudProcessRegion: {
          processStep: 1,
          processOrder: 2,
          processName: this.$t('scaning.init_cloud_region_info'),
          processRate: 0,
          execTime: 0,
          fake: new FakeProgress({
            timeConstant : 3000,
            autoStart : true
          }),
          intervalId: '',
        },
        cloudProcessGroup: {
          processStep: 1,
          processOrder: 3,
          processName: this.$t('scaning.init_cloud_group_info'),
          processRate: 0,
          execTime: 0,
          fake: new FakeProgress({
            timeConstant : 3000,
            autoStart : true
          }),
          intervalId: '',
        },
        cloudProcessRule: {
          processStep: 1,
          processOrder: 4,
          processName: this.$t('scaning.init_cloud_rule_info'),
          processRate: 0,
          execTime: 0,
          fake: new FakeProgress({
            timeConstant : 3000,
            autoStart : true
          }),
          intervalId: '',
        },
        cloudProcessEnv: {
          processStep: 2,
          processOrder: 5,
          processName: this.$t('scaning.init_env_info'),
          processRate: 0,
          execTime: 0,
          fake: new FakeProgress({
            timeConstant : 5000,
            autoStart : true
          }),
          intervalId: '',
        },
        cloudProcessScan: {
          processStep: 2,
          processOrder: 6,
          processName: this.$t('scaning.create_scan_info'),
          processRate: 0,
          execTime: 0,
          fake: new FakeProgress({
            timeConstant : 5000,
            autoStart : true
          }),
          intervalId: '',
        },
        cloudProcessScanGroup: {
          processStep: 2,
          processOrder: 7,
          processName: this.$t('scaning.create_scan_group'),
          processRate: 0,
          execTime: 0,
          fake: new FakeProgress({
            timeConstant : 10000,
            autoStart : true
          }),
          intervalId: '',
        },
        cloudProcessScanTask: {
          processStep: 2,
          processOrder: 8,
          processName: this.$t('scaning.create_scan_task'),
          processRate: 0,
          execTime: 0,
          fake: new FakeProgress({
            timeConstant : 10000,
            autoStart : true
          }),
          intervalId: '',
        },
        cloudProcessStartTask: {
          processStep: 3,
          processOrder: 9,
          processName: this.$t('scaning.start_scan_task'),
          processRate: 0,
          execTime: 0,
          fake: new FakeProgress({
            timeConstant : 10000,
            autoStart : true
          }),
          intervalId: '',
        },
        cloudProcessLogList: [],
        timeDifferenceInSeconds: 0,
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
        } else if (this.cloudProject.resultStatus === 'APPROVED') {
          this.$warning(this.$t('scaning.start_scan_ing'));
        } else if (this.cloudProject.resultStatus === 'FINISHED') {
          this.$router.push({
            path: '/account/resultdetails/' + this.projectId,
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
      startTimer() {
        //第一步
        this.cloudProcessAccount.intervalId = setInterval(() => {
          this.cloudProcessAccount.execTime++;
          //进度条前端一直转不会到100%
          this.cloudProcessAccount.processRate = parseInt(this.cloudProcessAccount.fake.progress * 100);
        }, 1000);
        setTimeout(() => {
          this.cloudProcessAccount.processRate = 100;
          this.cloudProcessAccount.fake.end();
          clearInterval(this.cloudProcessAccount.intervalId);

          let accountParams = {
            projectId: this.projectId,
            processStep: this.cloudProcessAccount.processStep,
            processOrder: this.cloudProcessAccount.processOrder,
            execTime: this.cloudProcessAccount.execTime,
          };
          this.$post(processUpdateUrl, accountParams, response => {
            this.cloudProject = response.data;
            this.cloudProcessAccount.processName = response.data.processName;
          });

          //第二步
          this.cloudProcessRegion.intervalId = setInterval(() => {
            this.cloudProcessRegion.execTime++;
            //进度条前端一直转不会到100%
            this.cloudProcessRegion.processRate = parseInt(this.cloudProcessRegion.fake.progress * 100);
          }, 1000);
          setTimeout(() => {
            this.cloudProcessRegion.processRate = 100;
            this.cloudProcessRegion.fake.end();
            clearInterval(this.cloudProcessRegion.intervalId);

            let regionParams = {
              projectId: this.projectId,
              processStep: this.cloudProcessRegion.processStep,
              processOrder: this.cloudProcessRegion.processOrder,
              execTime: this.cloudProcessRegion.execTime,
            };
            this.$post(processUpdateUrl, regionParams, response => {
              this.cloudProject = response.data;
              this.cloudProcessRegion.processName = response.data.processName;
            });

            //第三步
            this.cloudProcessGroup.intervalId = setInterval(() => {
              this.cloudProcessGroup.execTime++;
              //进度条前端一直转不会到100%
              this.cloudProcessGroup.processRate = parseInt(this.cloudProcessGroup.fake.progress * 100);
            }, 1000);
            setTimeout(() => {
              this.cloudProcessGroup.processRate = 100;
              this.cloudProcessGroup.fake.end();
              clearInterval(this.cloudProcessGroup.intervalId);

              let groupParams = {
                projectId: this.projectId,
                processStep: this.cloudProcessGroup.processStep,
                processOrder: this.cloudProcessGroup.processOrder,
                execTime: this.cloudProcessGroup.execTime,
              };
              this.$post(processUpdateUrl, groupParams, response => {
                this.cloudProject = response.data;
                this.cloudProcessGroup.processName = response.data.processName;
              });

              //第四步
              this.cloudProcessRule.intervalId = setInterval(() => {
                this.cloudProcessRule.execTime++;
                //进度条前端一直转不会到100%
                this.cloudProcessRule.processRate = parseInt(this.cloudProcessRule.fake.progress * 100);
              }, 1000);
              setTimeout(() => {
                this.cloudProcessRule.processRate = 100;
                this.cloudProcessRule.fake.end();
                clearInterval(this.cloudProcessRule.intervalId);

                let ruleParams = {
                  projectId: this.projectId,
                  processStep: this.cloudProcessRule.processStep,
                  processOrder: this.cloudProcessRule.processOrder,
                  execTime: this.cloudProcessRule.execTime,
                };
                this.$post(processUpdateUrl, ruleParams, response => {
                  this.cloudProject = response.data;
                  this.cloudProcessRule.processName = response.data.processName;
                });

                //第五步
                this.cloudProcessEnv.intervalId = setInterval(() => {
                  this.cloudProcessEnv.execTime++;
                  //进度条前端一直转不会到100%
                  this.cloudProcessEnv.processRate = parseInt(this.cloudProcessEnv.fake.progress * 100);
                }, 1000);
                setTimeout(() => {
                  this.cloudProcessEnv.processRate = 100;
                  this.cloudProcessEnv.fake.end();
                  clearInterval(this.cloudProcessEnv.intervalId);

                  let envParams = {
                    projectId: this.projectId,
                    processStep: this.cloudProcessEnv.processStep,
                    processOrder: this.cloudProcessEnv.processOrder,
                    execTime: this.cloudProcessEnv.execTime,
                  };
                  this.$post(processUpdateUrl, envParams, response => {
                    this.cloudProject = response.data;
                    this.cloudProcessEnv.processName = response.data.processName;
                  });

                  //第六步
                  this.cloudProcessScan.intervalId = setInterval(() => {
                    this.cloudProcessScan.execTime++;
                    //进度条前端一直转不会到100%
                    this.cloudProcessScan.processRate = parseInt(this.cloudProcessScan.fake.progress * 100);
                  }, 1000);
                  setTimeout(() => {
                    this.cloudProcessScan.processRate = 100;
                    this.cloudProcessScan.fake.end();
                    clearInterval(this.cloudProcessScan.intervalId);

                    let scanParams = {
                      projectId: this.projectId,
                      processStep: this.cloudProcessScan.processStep,
                      processOrder: this.cloudProcessScan.processOrder,
                      execTime: this.cloudProcessScan.execTime,
                    };
                    this.$post(processUpdateUrl, scanParams, response => {
                      this.cloudProject = response.data;
                      this.cloudProcessScan.processName = response.data.processName;
                    });

                    //第七步
                    this.cloudProcessScanGroup.intervalId = setInterval(() => {
                      this.cloudProcessScanGroup.execTime++;
                      //进度条前端一直转不会到100%
                      this.cloudProcessScanGroup.processRate = parseInt(this.cloudProcessScanGroup.fake.progress * 100);
                    }, 1000);
                    setTimeout(() => {
                      this.cloudProcessScanGroup.processRate = 100;
                      this.cloudProcessScanGroup.fake.end();
                      clearInterval(this.cloudProcessScanGroup.intervalId);

                      let scanGroupParams = {
                        projectId: this.projectId,
                        processStep: this.cloudProcessScanGroup.processStep,
                        processOrder: this.cloudProcessScanGroup.processOrder,
                        execTime: this.cloudProcessScanGroup.execTime,
                      };
                      this.$post(processUpdateUrl, scanGroupParams, response => {
                        this.cloudProject = response.data;
                        this.cloudProcessScanGroup.processName = response.data.processName;
                      });

                      //第八步
                      this.cloudProcessScanTask.intervalId = setInterval(() => {
                        this.cloudProcessScanTask.execTime++;
                        //进度条前端一直转不会到100%
                        this.cloudProcessScanTask.processRate = parseInt(this.cloudProcessScanTask.fake.progress * 100);
                      }, 1000);
                      setTimeout(() => {
                        this.cloudProcessScanTask.processRate = 100;
                        this.cloudProcessScanTask.fake.end();
                        clearInterval(this.cloudProcessScanTask.intervalId);

                        let scanTaskParams = {
                          projectId: this.projectId,
                          processStep: this.cloudProcessScanTask.processStep,
                          processOrder: this.cloudProcessScanTask.processOrder,
                          execTime: this.cloudProcessScanTask.execTime,
                        };
                        this.$post(processUpdateUrl, scanTaskParams, response => {
                          this.cloudProject = response.data;
                          this.cloudProcessScanTask.processName = response.data.processName;
                        });

                        //第九步
                        this.cloudProcessStartTask.intervalId = setInterval(() => {
                          this.cloudProcessStartTask.execTime++;
                          //进度条前端一直转不会到100%
                          this.cloudProcessStartTask.processRate = parseInt(this.cloudProcessStartTask.fake.progress * 100);
                          this.goAnchor("row9");
                          this.getStatus();
                        }, 1000);
                        setTimeout(() => {
                          this.cloudProcessStartTask.processRate = 100;
                          this.cloudProcessStartTask.fake.end();
                          clearInterval(this.cloudProcessStartTask.intervalId);

                          let startTaskParams = {
                            projectId: this.projectId,
                            processStep: this.cloudProcessStartTask.processStep,
                            processOrder: this.cloudProcessStartTask.processOrder,
                            execTime: this.cloudProcessStartTask.execTime,
                          };
                          this.$post(processUpdateUrl, startTaskParams, response => {
                            let data = response.data;
                            this.cloudProject = {
                              resultStatus: data.status,
                              processStep: data.processStep,
                              processOrder: data.processOrder,
                            };
                            this.cloudProcessStartTask.processName = data.processName;
                          });

                        }, Math.round(Math.random() * 9000 ) + 12000);

                      }, Math.round(Math.random() * 9000 ) + 9000);

                    }, Math.round(Math.random() * 9000 ) + 6000);

                  }, Math.round(Math.random() * 9000 ) + 1000);

                }, Math.round(Math.random() * 9000 ) + 1000);

              }, Math.round(Math.random() * 5000 ) + 1000);

            }, Math.round(Math.random() * 5000 ) + 1000);

          }, Math.round(Math.random() * 5000 ) + 1000);

        }, Math.round(Math.random() * 5000 ) + 1000);

      },
      getStatus() {
        let params = {
          projectId: this.projectId,
        };
        this.$post(processLogListUrl, params, res => {
          let data = res.data;
          if(!data) return;
          this.cloudProcessLogList = data;//日志
          for (let cloudProcessLog of this.cloudProcessLogList) {
            let row = this.timestampFormatDate(cloudProcessLog.createTime) + ' ' + cloudProcessLog.operator + ' ' + cloudProcessLog.operation + ' ' + cloudProcessLog.output;
            if (!this.script.includes(row)) {
              this.showScript(row);
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
                this.startTimer();
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
