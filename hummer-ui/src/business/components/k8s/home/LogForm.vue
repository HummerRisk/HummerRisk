<template>
  <div class="ch-mar" v-if="logForm.vulnerabilityReport || logForm.configAuditReport">
    <el-tabs type="border-card" @tab-click="showCodemirror">
      <el-tab-pane label="VulnerabilityReport" v-if="logForm.vulnerabilityReport">
        <div class="ch-mar-top">
          <h2>Summary:&nbsp;</h2>
          <ul style="margin-left: 60px;">
            <li><i>Api Version</i>: {{ logForm.vulnerabilityReport.apiVersion }}</li>
            <li><i>Kind</i>: {{ logForm.vulnerabilityReport.kind }}</li>
          </ul>
          <div class="ch-mar-top" v-if="logForm.vulnerabilityReport.items">
            <div class="ch-mar-top" :key="index" v-for="(item, index) in logForm.vulnerabilityReport.items">
              <el-card class="box-card">
                <div class="ch-mar">
                  <h3>Report:&nbsp;{{ item.metadata.name }}</h3>
                  <ul style="margin-left: 60px;">
                    <li><i>Namespace</i>: {{ item.metadata.namespace }}</li>
                    <li><i>Repository</i>: {{ item.report.artifact.repository }}</li>
                    <li><i>Critical Count</i>: {{ item.report.summary.criticalCount }}</li>
                    <li><i>High Count</i>: {{ item.report.summary.highCount }}</li>
                    <li><i>Low Count</i>:&nbsp;{{ item.report.summary.lowCount }}</li>
                    <li><i>Medium Count</i>:&nbsp;{{ item.report.summary.mediumCount }}</li>
                    <li><i>Unknown Count</i>:&nbsp;{{ item.report.summary.unknownCount }}</li>
                  </ul>
                </div>
                <div class="box-chr2 box-card" :key="index" v-for="(vulnerability, index) in item.report.vulnerabilities">
                  <el-card>
                    <div slot="header" class="clearfix clearfix-dev">
                      <el-row>
                        <el-col v-bind:class="{ 'icon-title box-critical': vulnerability.severity === 'CRITICAL',
                                  'icon-title box-high': vulnerability.severity === 'HIGH',
                                  'icon-title box-medium': vulnerability.severity === 'MEDIUM',
                                  'icon-title box-low': vulnerability.severity === 'LOW',
                                  'icon-title box-unknown': vulnerability.severity === 'UNKNOWN' }"
                                :span="3">
                          <span>{{ vulnerability.severity.substring(0, 1) }}</span>
                        </el-col>
                        <el-col :span="15" style="margin: -7px 0 0 15px;">
                          <span style="font-size: 24px;font-weight: 500;">{{ vulnerability.title }}</span>
                        </el-col>
                        <el-col :span="6" style="float: right;">
                          <span style="font-size: 20px;color: #999;float: right;">{{ 'SCORE' }}</span>
                        </el-col>
                      </el-row>
                      <el-row style="font-size: 18px;padding: 10px;">
                        <el-col :span="20">
                          <span style="color: #888;margin: 5px;">{{ 'VULNERABILITY' }}</span>
                          <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                          <span style="margin: 5px;"><a :href="vulnerability.primaryLink" target="_blank">{{ vulnerability.vulnerabilityID?vulnerability.vulnerabilityID:vulnerability.vulnerabilityId }}</a></span>
                          <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                          <span style="margin: 5px;">
                                  <el-button v-bind:class="{ 'box-critical': vulnerability.severity === 'CRITICAL',
                                    'box-high': vulnerability.severity === 'HIGH',
                                    'box-medium': vulnerability.severity === 'MEDIUM', 'box-low': vulnerability.severity === 'LOW',
                                    'box-unknown': vulnerability.severity === 'UNKNOWN' }" size="mini">{{ vulnerability.severity }}
                                  </el-button>
                                </span>
                          <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                          <span style="color: #444;margin: 5px;">RESOURCE: {{ vulnerability.resource }}</span>
                        </el-col>
                        <el-col :span="4" style="float: right;">
                          <span style="font-size: 20px;color: #000;float: right;">{{ vulnerability.score }}</span>
                        </el-col>
                      </el-row>
                      <div class="text item div-desc">
                        <el-row>
                          <i class="el-icon-s-opportunity"></i> {{ vulnerability.primaryLink }}
                        </el-row>
                        <el-row v-if="item.report.artifact && item.report.registry">
                          <i class="el-icon-info"></i> {{ 'Risk image: ' }} {{ item.report.registry.server }}/{{ item.report.artifact.repository }}:{{ item.report.artifact.tag }}
                        </el-row>
                      </div>
                    </div>
                    <div class="text div-json">
                    <el-descriptions title="Vulnerability" :column="2">
                      <el-descriptions-item label="fixedVersion">
                        {{ vulnerability.fixedVersion }}
                      </el-descriptions-item>
                      <el-descriptions-item label="installedVersion">
                        {{ vulnerability.installedVersion }}
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                  </el-card>
                </div>
              </el-card>
            </div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="ConfigAuditReport" v-if="logForm.configAuditReport">
        <div class="ch-mar-top">
          <h2>Summary:&nbsp;</h2>
          <ul style="margin-left: 60px;">
            <li><i>Api Version</i>: {{ logForm.configAuditReport.apiVersion }}</li>
            <li><i>Kind</i>: {{ logForm.configAuditReport.kind }}</li>
          </ul>
          <div class="ch-mar-top" v-if="logForm.configAuditReport.items">
            <div class="ch-mar-top" :key="index" v-for="(item, index) in logForm.configAuditReport.items">
              <el-card class="box-card">
                <div class="ch-mar">
                  <h3>Report:&nbsp;{{ item.metadata.name }}</h3>
                  <ul style="margin-left: 60px;">
                    <li><i>Namespace</i>: {{ item.metadata.namespace }}</li>
                    <li><i>Critical Count</i>: {{ item.report.summary.criticalCount }}</li>
                    <li><i>High Count</i>: {{ item.report.summary.highCount }}</li>
                    <li><i>Medium Count</i>:&nbsp;{{ item.report.summary.mediumCount }}</li>
                    <li><i>Low Count</i>:&nbsp;{{ item.report.summary.lowCount }}</li>
                  </ul>
                </div>
                <div v-if="item.report">
                  <div :key="index" v-for="(check, index) in item.report.checks">
                    <div class="box-chr" v-if="!check.success">
                      <div slot="header" class="clearfix clearfix-dev">
                        <el-row>
                          <el-col v-bind:class="{ 'icon-title box-critical': check.severity === 'CRITICAL',
                                  'icon-title box-high': check.severity === 'HIGH',
                                  'icon-title box-medium': check.severity === 'MEDIUM',
                                  'icon-title box-low': check.severity === 'LOW',
                                  'icon-title box-unknown': check.severity === 'UNKNOWN' }"
                                  :span="3">
                            <span>{{ check.severity.substring(0, 1) }}</span>
                          </el-col>
                          <el-col :span="15" style="margin: -7px 0 0 15px;">
                            <span style="font-size: 24px;font-weight: 500;">{{ check.title }}</span>
                          </el-col>
                          <el-col :span="6" style="float: right;">
                            <span style="font-size: 20px;color: #999;float: right;">{{ 'CHECKID' }}</span>
                          </el-col>
                        </el-row>
                        <el-row style="font-size: 18px;padding: 10px;">
                          <el-col :span="20">
                            <span style="color: #888;margin: 5px;">{{ 'CHECKS' }}</span>
                            <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                            <span style="margin: 5px;">{{ check.category }}</span>
                            <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                            <span style="margin: 5px;">
                                <el-button v-bind:class="{ 'box-critical': check.severity === 'CRITICAL',
                                  'box-high': check.severity === 'HIGH',
                                  'box-medium': check.severity === 'MEDIUM', 'box-low': check.severity === 'LOW',
                                  'box-unknown': check.severity === 'UNKNOWN' }" size="mini">{{ check.severity }}
                                </el-button>
                              </span>
                            <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                            <span style="color: #444;margin: 5px;">SUCCESS: {{ check.success }}</span>
                          </el-col>
                          <el-col :span="4" style="float: right;">
                            <span style="font-size: 20px;color: #000;float: right;">{{ check.checkID }}</span>
                          </el-col>
                        </el-row>
                      </div>
                      <div class="text item div-desc">
                        <el-row>
                          <i class="el-icon-s-opportunity"></i> {{ check.description }}
                        </el-row>
                      </div>
                    </div>
                  </div>
                </div>
              </el-card>
            </div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="KubeBench" v-if="logForm.kubeBench">
        <codemirror ref="cmEditor" v-model="logForm.kubeBench" class="code-mirror" :options="cmOptions" />
      </el-tab-pane>
      <el-tab-pane label="RuleGroup" v-if="logForm.cloudNativeId">
        <rule-group-details :accountId="logForm.cloudNativeId"/>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>

import RuleGroupDetails from "./RuleGroupDetails";

/* eslint-disable */
export default {
  components: {
    RuleGroupDetails,
  },
  props: {
    logForm: {}
  },
  data() {
    return {
      cmOptions: {
        tabSize: 4,
        mode: {
          name: 'shell',
          json: true
        },
        theme: 'bespin',
        lineNumbers: true,
        line: true,
        indentWithTabs: true,
        location: "",
      },
    }
  },
  methods: {
    init() {
    },
    showCodemirror() {
      setTimeout(() => {
        this.$refs.cmEditor.codemirror.refresh();
      }, 50);
    },
  },
  computed: {
    codemirror() {
      return this.$refs.cmEditor.codemirror;
    }
  },
  activated() {
    this.init();
  }
}
</script>

<style scoped>
.box-card >>> .el-card__header {
  background-color: aliceblue;
}
.box-card {
  width: 99%;
  border-top-width: 5px;
}
.box-chr {
  margin: 10px 0 10px 0;
  padding: 15px;
  box-shadow: #6e6464 0px 0px 3px;
  background-color: aliceblue;
}

.box-chr2 {
  margin: 10px 0 10px 0;
  box-shadow: #6e6464 0px 0px 5px;
}

.box-card-critical {
  border-top-color: #8B0000;
}
.box-card-high {
  border-top-color: #FF4D4D;
}
.box-card-medium {
  border-top-color: #FF8000;
}
.box-card-low {
  border-top-color: #336D9F;
}
.box-card-unknown {
  border-top-color: #67C23A;
}
.box-critical {
  color: #ffffff;
  background-color: #8B0000;
}
.box-high {
  color: #ffffff;
  background-color: #FF4D4D;
}
.box-medium {
  color: #ffffff;
  background-color: #FF8000;
}
.box-low {
  color: #ffffff;
  background-color: #336D9F;
}
.box-unknown {
  color: #ffffff;
  background-color: #67C23A;
}
.icon-title {
  color: #fff;
  width: 30px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border-radius: 30px;
  font-size: 14px;
  margin: -7px 0 0 15px;
}
.div-desc {
  background-color: #ecebf5;
  color: blueviolet;
  padding: 15px;
}
.div-json {
  padding: 15px;
}
.ch-mar {
  margin: 10px;
}
.ch-mar-top {
  margin: 10px 0 0 0;
}
.code-mirror {
  height: 600px !important;
}
.code-mirror >>> .CodeMirror {
  /* Set height, width, borders, and global font properties here */
  height: 600px !important;
}
</style>

