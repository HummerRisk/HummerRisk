<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <scan-header  :condition.sync="condition"
                        :title="$t('scaning.scaning_log')"
                        @back="back" :backTip="$t('account.back_account')"
                        :show-save="false" :show-create="false" :show-setting="false" :show-clean="false"/>
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
              <el-steps direction="vertical" :active="activeStep">
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
                                  'font-ing': percentage1 > 0 && percentage1 < 100,
                                  'font-end': percentage1 === 0 || percentage1 === 100 }">
                    <i v-if="percentage1 > 0 && percentage1 < 100" class="el-icon-loading"></i>
                    <i v-if="percentage1 === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage1 === 100" class="el-icon-circle-check"></i>
                    {{ $t('scaning.init_cloud_account_info') }}
                    <I v-if="seconds1 > 0" style="float: right;margin-right: 23px;">{{ seconds1 + $t('second.title') }}</I>
                    <I v-if="seconds1 > 0 && minutes1 > 0" style="float: right">{{ minutes1 + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage1" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row2" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage2 > 0 && percentage2 < 100,
                                  'font-end': percentage2 === 0 || percentage2 === 100 }">
                    <i v-if="percentage2 > 0 && percentage2 < 100" class="el-icon-loading"></i>
                    <i v-if="percentage2 === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage2 === 100" class="el-icon-circle-check"></i>
                    {{ $t('scaning.init_cloud_region_info') }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I v-if="seconds2 > 0" style="float: right;margin-right: 23px;">{{ seconds2 + $t('second.title') }}</I>
                    <I v-if="seconds2 > 0 && minutes2 > 0" style="float: right">{{ minutes2 + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage2" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row3" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage3 > 0 && percentage3 < 100,
                                  'font-end': percentage3 === 0 || percentage3 === 100 }">
                    <i v-if="percentage3 > 0 && percentage3 < 100" class="el-icon-loading"></i>
                    <i v-if="percentage3 === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage3 === 100" class="el-icon-circle-check"></i>
                    {{ $t('scaning.init_cloud_group_info') }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I v-if="seconds3 > 0" style="float: right;margin-right: 23px;">{{ seconds3 + $t('second.title') }}</I>
                    <I v-if="seconds3 > 0 && minutes3 > 0" style="float: right">{{ minutes3 + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage3" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row4" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage4 > 0 && percentage4 < 100,
                                  'font-end': percentage4 === 0 || percentage4 === 100 }">
                    <i v-if="percentage4 > 0 && percentage4 < 100" class="el-icon-loading"></i>
                    <i v-if="percentage4 === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage4 === 100" class="el-icon-circle-check"></i>
                    {{ $t('scaning.init_cloud_rule_info') }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I v-if="seconds4 > 0" style="float: right;margin-right: 23px;">{{ seconds4 + $t('second.title') }}</I>
                    <I v-if="seconds4 > 0 && minutes4 > 0" style="float: right">{{ minutes4 + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage4" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
            </div>
            <div style="height: 231px;margin: 50px 0;">
              <el-row id="row5" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage5 > 0 && percentage5 < 100,
                                  'font-end': percentage5 === 0 || percentage5 === 100 }">
                    <i v-if="percentage5 > 0 && percentage5 < 100" class="el-icon-loading"></i>
                    <i v-if="percentage5 === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage5 === 100" class="el-icon-circle-check"></i>
                    {{ $t('scaning.init_env_info') }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I v-if="seconds5 > 0" style="float: right;margin-right: 23px;">{{ seconds5 + $t('second.title') }}</I>
                    <I v-if="seconds5 > 0 && minutes5 > 0" style="float: right">{{ minutes5 + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage5" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row6" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage6 > 0 && percentage6 < 100,
                                  'font-end': percentage6 === 0 || percentage6 === 100 }">
                    <i v-if="percentage6 > 0 && percentage6 < 100" class="el-icon-loading"></i>
                    <i v-if="percentage6 === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage6 === 100" class="el-icon-circle-check"></i>
                    {{ $t('scaning.create_scan_info') }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I v-if="seconds6 > 0" style="float: right;margin-right: 23px;">{{ seconds6 + $t('second.title') }}</I>
                    <I v-if="seconds6 > 0 && minutes6 > 0" style="float: right">{{ minutes6 + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage6" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row7" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage7 > 0 && percentage7 < 100,
                                  'font-end': percentage7 === 0 || percentage7 === 100 }">
                    <i v-if="percentage7 > 0 && percentage7 < 100" class="el-icon-loading"></i>
                    <i v-if="percentage7 === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage7 === 100" class="el-icon-circle-check"></i>
                    {{ $t('scaning.create_scan_rule') }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I v-if="seconds7 > 0" style="float: right;margin-right: 23px;">{{ seconds7 + $t('second.title') }}</I>
                    <I v-if="seconds7 > 0 && minutes7 > 0" style="float: right">{{ minutes7 + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage7" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row8" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage8 > 0 && percentage8 < 100,
                                  'font-end': percentage8 === 0 || percentage8 === 100 }">
                    <i v-if="percentage8 > 0 && percentage8 < 100" class="el-icon-loading"></i>
                    <i v-if="percentage8 === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage8 === 100" class="el-icon-circle-check"></i>
                    {{ $t('scaning.create_scan_task') }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I v-if="seconds8 > 0" style="float: right;margin-right: 23px;">{{ seconds8 + $t('second.title') }}</I>
                    <I v-if="seconds8 > 0 && minutes8 > 0" style="float: right">{{ minutes8 + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage8" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row id="row9" style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage9 > 0 && percentage9 < 100,
                                  'font-end': percentage9 === 0 || percentage9 === 100 }">
                    <i v-if="percentage9 > 0 && percentage9 < 100" class="el-icon-loading"></i>
                    <i v-if="percentage9 === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage9 === 100" class="el-icon-circle-check"></i>
                    {{ $t('scaning.start_scan_task') }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I v-if="seconds9 > 0" style="float: right;margin-right: 23px;">{{ seconds9 + $t('second.title') }}</I>
                    <I v-if="seconds9 > 0 && minutes9 > 0" style="float: right">{{ minutes9 + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage9" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
            </div>
            <div style="height: 300px;margin: 50px 0;">
              <el-row>
                <el-col :span="16">
                  <codemirror ref="cmEditor" v-model="script" class="code-mirror" :options="cmOptions" />
                </el-col>
                <el-col :span="6">
                  <el-card class="box-card">
                    <el-result v-if="resultStatus == 0" icon="info" :title="$t('scaning.no_scan')" :subTitle="$t('scaning.no_scan_ing')">
                      <template slot="extra">
                        <el-button type="primary" size="medium" @click="goResult">{{ $t('scaning.no_scan') }}</el-button>
                      </template>
                    </el-result>
                    <el-result v-if="resultStatus == 1" icon="warning" :title="$t('scaning.start_scan')" :subTitle="$t('scaning.start_scan_ing')">
                      <template slot="extra">
                        <el-button type="primary" size="medium" @click="goResult">{{ $t('scaning.start_scan') }}</el-button>
                      </template>
                    </el-result>
                    <el-result v-if="resultStatus == 2" icon="success" :title="$t('scaning.create_end')" :subTitle="$t('scaning.go_scan_result')">
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
        script: '',
        percentage1: 0,
        percentage2: 0,
        percentage3: 0,
        percentage4: 0,
        percentage5: 0,
        percentage6: 0,
        percentage7: 0,
        percentage8: 0,
        percentage9: 0,
        percentage: 0,
        customColor: '#409eff',
        customColors: [
          {color: '#f56c6c', percentage: 20},
          {color: '#e6a23c', percentage: 40},
          {color: '#5cb87a', percentage: 60},
          {color: '#1989fa', percentage: 80},
          {color: '#6f7ad3', percentage: 100}
        ],
        minutes1: 0,
        seconds1: 0,
        intervalId1: null,
        minutes2: 0,
        seconds2: 0,
        intervalId2: null,
        minutes3: 0,
        seconds3: 0,
        intervalId3: null,
        minutes4: 0,
        seconds4: 0,
        intervalId4: null,
        minutes5: 0,
        seconds5: 0,
        intervalId5: null,
        minutes6: 0,
        seconds6: 0,
        intervalId6: null,
        minutes7: 0,
        seconds7: 0,
        intervalId7: null,
        minutes8: 0,
        seconds8: 0,
        intervalId8: null,
        minutes9: 0,
        seconds9: 0,
        intervalId9: null,
        resultStatus: 0,
        resultTag: false,
        activeStep: 1,
        fake1: new FakeProgress({
          timeConstant : 1000,
          autoStart : true
        }),
        fake2: new FakeProgress({
          timeConstant : 1000,
          autoStart : true
        }),
        fake3: new FakeProgress({
          timeConstant : 2000,
          autoStart : true
        }),
        fake4: new FakeProgress({
          timeConstant : 2000,
          autoStart : true
        }),
        fake5: new FakeProgress({
          timeConstant : 10000,
          autoStart : true
        }),
        fake6: new FakeProgress({
          timeConstant : 10000,
          autoStart : true
        }),
        fake7: new FakeProgress({
          timeConstant : 20000,
          autoStart : true
        }),
        fake8: new FakeProgress({
          timeConstant : 30000,
          autoStart : true
        }),
        fake9: new FakeProgress({
          timeConstant : 10000,
          autoStart : true
        }),
        timer: '',
      }
    },
    created() {
      this.accountId = this.$route.params.id;
      this.init();
    },
    methods: {
      init() {
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
        if (this.resultStatus === 0) {
          this.$warning(this.$t('scaning.no_scan_ing'));
          return;
        } else if (this.resultStatus === 1) {
          this.$warning(this.$t('scaning.start_scan_ing'));
          return;
        } else {
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
      startTimer() {
        //第一步
        this.goAnchor('row1');
        this.intervalId1 = setInterval(() => {
          this.seconds1++;
          if (this.seconds1 === 60) {
            this.seconds1 = 0;
            this.minutes1++;
          }
          //进度条前端一直转不会到100%
          this.percentage1 = parseInt(this.fake1.progress * 100);
        }, 1000);
        setTimeout(() => {
          this.percentage1 = 100;
          this.fake1.end();
          clearInterval(this.intervalId1);

          this.script = this.script + this.$t('scaning.init_cloud_account_info') + "\r" + "init cloud account info start" + "\r" + "init cloud account info end" + "\r";
          // 获取滚动信息  注意是cmEditor.codemirror
          let sc = this.$refs.cmEditor.codemirror.getScrollInfo();
          // 滚动 注意是cmEditor.codemirror
          this.$refs.cmEditor.codemirror.scrollTo(sc.left,( sc.height + sc.top));

          //第二步
          this.goAnchor('row2');
          this.intervalId2 = setInterval(() => {
            this.seconds2++;
            if (this.seconds2 === 60) {
              this.seconds2 = 0;
              this.minutes2++;
            }
            //进度条前端一直转不会到100%
            this.percentage2 = parseInt(this.fake2.progress * 100);
          }, 1000);
          setTimeout(() => {
            this.percentage2 = 100;
            this.fake2.end();
            clearInterval(this.intervalId2);

            this.script = this.script + this.$t('scaning.init_cloud_region_info') + "\r" + "init cloud account regions start" + "\r" + "init cloud account regions end" + "\r";
            // 获取滚动信息  注意是cmEditor.codemirror
            let sc = this.$refs.cmEditor.codemirror.getScrollInfo();
            // 滚动 注意是cmEditor.codemirror
            this.$refs.cmEditor.codemirror.scrollTo(sc.left,( sc.height + sc.top));

            //第三步
            this.goAnchor('row3');
            this.intervalId3 = setInterval(() => {
              this.seconds3++;
              if (this.seconds3 === 60) {
                this.seconds3 = 0;
                this.minutes3++;
              }
              //进度条前端一直转不会到100%
              this.percentage3 = parseInt(this.fake3.progress * 100);
            }, 1000);
            setTimeout(() => {
              this.percentage3 = 100;
              this.fake3.end();
              clearInterval(this.intervalId3);

              this.script = this.script + this.$t('scaning.init_cloud_group_info') + "\r" + "init rule group start" + "\r" + "init rule group end" + "\r";
              // 获取滚动信息  注意是cmEditor.codemirror
              let sc = this.$refs.cmEditor.codemirror.getScrollInfo();
              // 滚动 注意是cmEditor.codemirror
              this.$refs.cmEditor.codemirror.scrollTo(sc.left,( sc.height + sc.top));

              //第四步
              this.goAnchor('row4');
              this.intervalId4 = setInterval(() => {
                this.seconds4++;
                if (this.seconds4 === 60) {
                  this.seconds4 = 0;
                  this.minutes4++;
                }
                //进度条前端一直转不会到100%
                this.percentage4 = parseInt(this.fake4.progress * 100);
              }, 1000);
              setTimeout(() => {
                this.percentage4 = 100;
                this.fake4.end();
                clearInterval(this.intervalId4);
                this.activeStep = 2;

                this.script = this.script + this.$t('scaning.init_cloud_rule_info') + "\r" + "init rules start" + "\r" + "init rules end" + "\r";
                // 获取滚动信息  注意是cmEditor.codemirror
                let sc = this.$refs.cmEditor.codemirror.getScrollInfo();
                // 滚动 注意是cmEditor.codemirror
                this.$refs.cmEditor.codemirror.scrollTo(sc.left,( sc.height + sc.top));

                //第五步
                this.goAnchor('row5');
                this.intervalId5 = setInterval(() => {
                  this.seconds5++;
                  if (this.seconds5 === 60) {
                    this.seconds5 = 0;
                    this.minutes5++;
                  }
                  //进度条前端一直转不会到100%
                  this.percentage5 = parseInt(this.fake5.progress * 100);
                }, 1000);
                setTimeout(() => {
                  this.percentage5 = 100;
                  this.fake5.end();
                  clearInterval(this.intervalId5);

                  this.script = this.script + this.$t('scaning.init_env_info') + "\r" + "init env start" + "\r" + "init env end" + "\r";
                  // 获取滚动信息  注意是cmEditor.codemirror
                  let sc = this.$refs.cmEditor.codemirror.getScrollInfo();
                  // 滚动 注意是cmEditor.codemirror
                  this.$refs.cmEditor.codemirror.scrollTo(sc.left,( sc.height + sc.top));

                  //第六步
                  this.goAnchor('row6');
                  this.intervalId6 = setInterval(() => {
                    this.seconds6++;
                    if (this.seconds6 === 60) {
                      this.seconds6 = 0;
                      this.minutes6++;
                    }
                    //进度条前端一直转不会到100%
                    this.percentage6 = parseInt(this.fake6.progress * 100);
                  }, 1000);
                  setTimeout(() => {
                    this.percentage6 = 100;
                    this.fake6.end();
                    clearInterval(this.intervalId6);

                    this.script = this.script + this.$t('scaning.create_scan_info') + "\r" + "create scan start" + "\r" + "create scan end" + "\r";
                    // 获取滚动信息  注意是cmEditor.codemirror
                    let sc = this.$refs.cmEditor.codemirror.getScrollInfo();
                    // 滚动 注意是cmEditor.codemirror
                    this.$refs.cmEditor.codemirror.scrollTo(sc.left,( sc.height + sc.top));

                    //第七步
                    this.goAnchor('row7');
                    this.intervalId7 = setInterval(() => {
                      this.seconds7++;
                      if (this.seconds7 === 60) {
                        this.seconds7 = 0;
                        this.minutes7++;
                      }
                      //进度条前端一直转不会到100%
                      this.percentage7 = parseInt(this.fake7.progress * 100);
                    }, 1000);
                    setTimeout(() => {
                      this.percentage7 = 100;
                      this.fake7.end();
                      clearInterval(this.intervalId7);

                      this.script = this.script + this.$t('scaning.create_scan_rule') + "\r" + "create scan rule start" + "\r" + "create scan rule end" + "\r";
                      // 获取滚动信息  注意是cmEditor.codemirror
                      let sc = this.$refs.cmEditor.codemirror.getScrollInfo();
                      // 滚动 注意是cmEditor.codemirror
                      this.$refs.cmEditor.codemirror.scrollTo(sc.left,( sc.height + sc.top));

                      //第八步
                      this.goAnchor('row8');
                      this.intervalId8 = setInterval(() => {
                        this.seconds8++;
                        if (this.seconds8 === 60) {
                          this.seconds8 = 0;
                          this.minutes8++;
                        }
                        //进度条前端一直转不会到100%
                        this.percentage8 = parseInt(this.fake8.progress * 100);
                      }, 1000);
                      setTimeout(() => {
                        this.percentage8 = 100;
                        this.fake8.end();
                        clearInterval(this.intervalId8);

                        this.script = this.script + this.$t('scaning.create_scan_task') + "\r" + "create scan task start" + "\r" + "create scan task end" + "\r";
                        // 获取滚动信息  注意是cmEditor.codemirror
                        let sc = this.$refs.cmEditor.codemirror.getScrollInfo();
                        // 滚动 注意是cmEditor.codemirror
                        this.$refs.cmEditor.codemirror.scrollTo(sc.left,( sc.height + sc.top));

                        //第九步
                        this.goAnchor('row9');
                        this.intervalId9 = setInterval(() => {
                          this.seconds9++;
                          if (this.seconds9 === 60) {
                            this.seconds9 = 0;
                            this.minutes9++;
                          }
                          //进度条前端一直转不会到100%
                          this.percentage9 = parseInt(this.fake9.progress * 100);

                        }, 1000);
                        this.timer = setInterval(this.getStatus, 5000);

                      }, Math.round(Math.random() * 9000 ) + 90000);

                    }, Math.round(Math.random() * 9000 ) + 60000);

                  }, Math.round(Math.random() * 9000 ) + 40000);

                }, Math.round(Math.random() * 9000 ) + 20000);

              }, Math.round(Math.random() * 9000 ) + 1000);

            }, Math.round(Math.random() * 9000 ) + 1000);

          }, Math.round(Math.random() * 9000 ) + 1000);

        }, Math.round(Math.random() * 9000 ) + 1000);

      },
      stopTimer() {
        clearInterval(this.intervalId1);
      },
      getStatus() {
        if (this.resultTag) {
          this.showScript();
          clearInterval(this.timer);
          clearInterval(this.intervalId9);
          this.percentage9 = 100;
          this.fake9.end();
          this.activeStep = 3;
          this.resultStatus = 2;
        }
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
              this.resultStatus = 1;
              let params = {
                accountId: this.accountWithGroup.id,
                groups: this.checkedGroups
              }
              this.startTimer();
              this.$post(ruleScanUrl, params, res => {
                this.resultTag = true;
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
      showScript() {
        this.script = this.script + this.$t('scaning.start_scan_task') + "\r" + "scan start" + "\r";
        // 获取滚动信息  注意是cmEditor.codemirror
        let sc = this.$refs.cmEditor.codemirror.getScrollInfo();
        // 滚动 注意是cmEditor.codemirror
        this.$refs.cmEditor.codemirror.scrollTo(sc.left,( sc.height + sc.top));

        this.$get(cloudTaskLogByAccountIdUrl + this.accountId, response => {
          for (let logItem of response.data) {
            let str = this.timestampFormatDate(logItem.createTime) + ' ' + logItem.operator + ' ' + logItem.operation + ' ' + logItem.output;
            this.script = this.script + str + "\r";
            // 获取滚动信息  注意是cmEditor.codemirror
            let sc = this.$refs.cmEditor.codemirror.getScrollInfo();
            // 滚动 注意是cmEditor.codemirror
            this.$refs.cmEditor.codemirror.scrollTo(sc.left,( sc.height + sc.top));
          }
        });
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
