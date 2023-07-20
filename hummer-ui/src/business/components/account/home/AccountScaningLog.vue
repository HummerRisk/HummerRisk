<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <scan-header  :condition.sync="condition"
                        :title="$t('scaning.scaning_log')"
                        @back="back" :backTip="$t('account.back_account')"
                        :show-save="false" :show-create="false" :show-setting="false" :show-clean="false"/>
        </template>

        <el-row>
          <el-col :span="4">
            <div style="height: 600px;margin: 25px;">
              <el-steps direction="vertical" :active="1">
                <el-step title="初始化配置" description="等待检测项配置">
                </el-step>
                <el-step title="执行检测" description="初始化检测信息">
                </el-step>
                <el-step title="输出日志" description="执行检测Log信息">
                </el-step>
              </el-steps>
            </div>
          </el-col>
          <el-col :span="20">
            <div style="height: 221px;margin: 54px 0;">
              <el-row style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage1 > 0 && percentage1 < 100,
                                  'font-end': percentage1 === 0 || percentage1 === 100 }">
                    <i v-if="percentage1 > 0 && percentage1 < 100" class="el-icon-loading"></i>
                    <i v-if="percentage1 === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage1 === 100" class="el-icon-circle-check"></i>
                    {{ '初始化云账号信息...' }}
                    <I style="float: right;margin-right: 23px;">{{ seconds + $t('second.title') }}</I>
                    <I v-if="minutes > 0" style="float: right">{{ minutes + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage1" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage > 0 && percentage < 100,
                                  'font-end': percentage === 0 || percentage === 100 }">
                    <i v-if="percentage > 0 && percentage < 100" class="el-icon-loading"></i>
                    <i v-if="percentage === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage === 100" class="el-icon-circle-check"></i>
                    {{ '初始化区域信息...' }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I style="float: right;margin-right: 23px;">{{ seconds + $t('second.title') }}</I>
                    <I v-if="minutes > 0" style="float: right">{{ minutes + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage > 0 && percentage < 100,
                                  'font-end': percentage === 0 || percentage === 100 }">
                    <i v-if="percentage > 0 && percentage < 100" class="el-icon-loading"></i>
                    <i v-if="percentage === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage === 100" class="el-icon-circle-check"></i>
                    {{ '初始化规则组信息...' }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I style="float: right;margin-right: 23px;">{{ seconds + $t('second.title') }}</I>
                    <I v-if="minutes > 0" style="float: right">{{ minutes + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage > 0 && percentage < 100,
                                  'font-end': percentage === 0 || percentage === 100 }">
                    <i v-if="percentage > 0 && percentage < 100" class="el-icon-loading"></i>
                    <i v-if="percentage === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage === 100" class="el-icon-circle-check"></i>
                    {{ '初始化规则信息...' }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I style="float: right;margin-right: 23px;">{{ seconds + $t('second.title') }}</I>
                    <I v-if="minutes > 0" style="float: right">{{ minutes + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
            </div>
            <div style="height: 231px;margin: 50px 0;">
              <el-row style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage > 0 && percentage < 100,
                                  'font-end': percentage === 0 || percentage === 100 }">
                    <i v-if="percentage > 0 && percentage < 100" class="el-icon-loading"></i>
                    <i v-if="percentage === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage === 100" class="el-icon-circle-check"></i>
                    {{ '初始化检测环境...' }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I style="float: right;margin-right: 23px;">{{ seconds + $t('second.title') }}</I>
                    <I v-if="minutes > 0" style="float: right">{{ minutes + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage > 0 && percentage < 100,
                                  'font-end': percentage === 0 || percentage === 100 }">
                    <i v-if="percentage > 0 && percentage < 100" class="el-icon-loading"></i>
                    <i v-if="percentage === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage === 100" class="el-icon-circle-check"></i>
                    {{ '创建检测任务...' }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I style="float: right;margin-right: 23px;">{{ seconds + $t('second.title') }}</I>
                    <I v-if="minutes > 0" style="float: right">{{ minutes + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage > 0 && percentage < 100,
                                  'font-end': percentage === 0 || percentage === 100 }">
                    <i v-if="percentage > 0 && percentage < 100" class="el-icon-loading"></i>
                    <i v-if="percentage === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage === 100" class="el-icon-circle-check"></i>
                    {{ '创建检测规则...' }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I style="float: right;margin-right: 23px;">{{ seconds + $t('second.title') }}</I>
                    <I v-if="minutes > 0" style="float: right">{{ minutes + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage > 0 && percentage < 100,
                                  'font-end': percentage === 0 || percentage === 100 }">
                    <i v-if="percentage > 0 && percentage < 100" class="el-icon-loading"></i>
                    <i v-if="percentage === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage === 100" class="el-icon-circle-check"></i>
                    {{ '检测任务构建...' }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I style="float: right;margin-right: 23px;">{{ seconds + $t('second.title') }}</I>
                    <I v-if="minutes > 0" style="float: right">{{ minutes + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage" :color="customColorMethod"></el-progress>
                </el-col>
              </el-row>
              <el-row style="margin: 15px 0;">
                <el-col :span="22">
                  <h5 v-bind:class="{
                                  'font-ing': percentage > 0 && percentage < 100,
                                  'font-end': percentage === 0 || percentage === 100 }">
                    <i v-if="percentage > 0 && percentage < 100" class="el-icon-loading"></i>
                    <i v-if="percentage === 0" class="el-icon-video-pause"></i>
                    <i v-if="percentage === 100" class="el-icon-circle-check"></i>
                    {{ '开始执行检测...' }} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <I style="float: right;margin-right: 23px;">{{ seconds + $t('second.title') }}</I>
                    <I v-if="minutes > 0" style="float: right">{{ minutes + $t('minute.title') }}:</I>
                  </h5>
                  <el-progress :percentage="percentage" :color="customColorMethod"></el-progress>
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
                    <el-result v-if="!resultStatus" icon="warning" title="正在检测" subTitle="检测进行中">
                      <template slot="extra">
                        <el-button type="primary" size="medium">正在检测</el-button>
                      </template>
                    </el-result>
                    <el-result v-if="resultStatus" icon="success" title="检测完成" subTitle="进入检测结果">
                      <template slot="extra">
                        <el-button type="primary" size="medium">进入结果</el-button>
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
  getAccountUrl,
  ruleListUrl,
} from "@/api/cloud/account/account";

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
    props: ["id"],
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
        },
        script: '',
        percentage1: 1,
        percentage: 0,
        customColor: '#409eff',
        customColors: [
          {color: '#f56c6c', percentage: 20},
          {color: '#e6a23c', percentage: 40},
          {color: '#5cb87a', percentage: 60},
          {color: '#1989fa', percentage: 80},
          {color: '#6f7ad3', percentage: 100}
        ],
        minutes: 0,
        seconds: 0,
        intervalId: null,
        resultStatus: true,
      }
    },
    created() {
      this.init();
    },
    methods: {
      init() {
        this.accountId = this.$route.params.id;
        this.search();
        this.startTimer();
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
      async search() {
        if (!this.accountId) {
          return;
        }
        this.result = await this.$get(getAccountUrl + this.accountId, res => {
          this.accountName = res.data.name;
          this.regions = typeof(res.data.regions) == 'string'?JSON.parse(res.data.regions):res.data.regions;
          this.condition.pluginId = res.data.pluginId;
          this.condition.accountId = this.accountId;
          this.condition.status = true;
          let url = ruleListUrl + this.currentPage + "/" + this.pageSize;
          this.$post(url, this.condition, response => {
            let data = response.data;
            this.total = data.itemCount;
            this.tableData = data.listObject;
            for (let obj of this.tableData) {
              obj.parameter = typeof(obj.parameter) == 'string'?JSON.parse(obj.parameter):obj.parameter;
              if (!!obj.regions && obj.regions.length > 0) {
                obj.regions = typeof(obj.regions) == 'string'?JSON.parse(obj.regions):obj.regions;
              } else {
                obj.regions = [];
                if (!!this.regions) {
                  for (let option of this.regions) {
                    obj.regions.push(option["regionId"]);
                  }
                }
              }
            }
          });
        });
      },
      back () {
        this.$router.push({
          path: '/account/cloudaccount',
        }).catch(error => error);
      },
      goResult() {
        this.$router.push({
          name: 'cloudResult',
          params: {id: this.accountWithGroup.id},
        }).catch(error => error);
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
        this.intervalId = setInterval(() => {
          this.seconds++;
          if (this.seconds === 60) {
            this.seconds = 0;
            this.minutes++;
          }
        }, 1000);
      },
      stopTimer() {
        clearInterval(this.intervalId)
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
  /deep/ :focus{outline:0;}
</style>
