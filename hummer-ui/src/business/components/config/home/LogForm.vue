<template>
  <div>
    <div class="ch-mar" v-if="logForm.resultJson">
      <h2>Summary:&nbsp;</h2>
      <ul style="margin: 5px 0 0 60px;">
        <li><i>Scan Name</i>: {{ logForm.name }}</li>
        <li><i>Scan User</i>:&nbsp;{{ logForm.userName }}</li>
        <li><i>ArtifactType</i>:&nbsp;{{ logForm.resultJson.ArtifactType }}</li>
        <li><i>ArtifactName</i>:&nbsp;{{ logForm.resultJson.ArtifactName }}</li>
        <li><i>SchemaVersion</i>:&nbsp;{{ logForm.resultJson.SchemaVersion }}</li>
        <li><i>Architecture</i>:&nbsp;{{ logForm.resultJson.Metadata.ImageConfig.architecture?logForm.resultJson.Metadata.ImageConfig.architecture:'N/A' }}</li>
        <li><i>Create Time</i>:&nbsp;{{ logForm.createTime | timestampFormatDate }}</li>
        <li><i>Result Status</i>:&nbsp;{{ logForm.resultStatus }}</li>
        <li><i>Results</i>: {{ logForm.returnSum }}</li>
      </ul>
    </div>
    <div class="ch-mar" v-if="logForm.resultJson">
      <div class="ch-mar-top">
        <h2>Details:&nbsp;</h2>
        <div class="ch-mar-top" v-if="logForm.resultJson.Results">
          <div class="ch-mar-top" :key="index" v-for="(result, index) in logForm.resultJson.Results">
            <div class="ch-mar" v-if="result">
              <h3>Report:&nbsp;</h3>
              <ul style="margin-left: 60px;">
                <li><i>Target</i>: {{ result.Target }}</li>
                <li><i>Class</i>:&nbsp;{{ result.Class }}</li>
                <li><i>Type</i>:&nbsp;{{ result.Type }}</li>
                <li><i>Successes</i>: {{ result.MisconfSummary.Successes }}</li>
                <li><i>Failures</i>:&nbsp;{{ result.MisconfSummary.Failures }}</li>
                <li><i>Exceptions</i>:&nbsp;{{ result.MisconfSummary.Exceptions }}</li>
              </ul>
            </div>
            <div v-if="result.Misconfigurations">
              <div class="ch-mar-top" :key="index" v-for="(misconfiguration, index) in result.Misconfigurations">
                <el-card v-bind:class="{ 'box-card box-card-critical': misconfiguration.Severity === 'CRITICAL',
                   'box-card box-card-high': misconfiguration.Severity === 'HIGH',
                   'box-card box-card-medium': misconfiguration.Severity === 'MEDIUM',
                   'box-card box-card-low': misconfiguration.Severity === 'LOW',
                   'box-card box-card-unknown': misconfiguration.Severity === 'UNKNOWN' }">
                  <div slot="header" class="clearfix">
                    <el-row>
                      <el-col v-bind:class="{ 'icon-title box-critical': misconfiguration.Severity === 'CRITICAL',
                                'icon-title box-high': misconfiguration.Severity === 'HIGH',
                                'icon-title box-medium': misconfiguration.Severity === 'MEDIUM',
                                'icon-title box-low': misconfiguration.Severity === 'LOW',
                                'icon-title box-unknown': misconfiguration.Severity === 'UNKNOWN' }"
                              :span="3">
                        <span>{{ misconfiguration.Severity.substring(0, 1) }}</span>
                      </el-col>
                      <el-col :span="15" style="margin: -7px 0 0 15px;">
                        <span style="font-size: 24px;font-weight: 500;">{{ misconfiguration.Title }}</span>
                      </el-col>
                      <el-col :span="6" style="float: right;">
                        <span style="font-size: 20px;color: #999;float: right;">{{ 'ID' }}</span>
                      </el-col>
                    </el-row>
                    <el-row style="font-size: 18px;padding: 10px;">
                      <el-col :span="20">
                        <span style="margin: 5px;"><a :href="misconfiguration.PrimaryURL" target="_blank">{{ misconfiguration.Namespace }}</a></span>
                        <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                        <span style="margin: 5px;">
                            <el-button v-bind:class="{ 'box-critical': misconfiguration.Severity === 'CRITICAL',
                              'box-high': misconfiguration.Severity === 'HIGH',
                              'box-medium': misconfiguration.Severity === 'MEDIUM', 'box-low': misconfiguration.Severity === 'LOW',
                              'box-unknown': misconfiguration.Severity === 'UNKNOWN' }" size="mini">{{ misconfiguration.Severity }}
                            </el-button>
                          </span>
                        <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                        <span style="color: #888;margin: 5px;">Type: {{ misconfiguration.Type }}</span>
                        <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                        <span style="color: #444;margin: 5px;">Status: {{ misconfiguration.Status }}</span>
                      </el-col>
                      <el-col :span="4" style="float: right;">
                        <span style="font-size: 20px;color: #000;float: right;">{{ misconfiguration.ID }}</span>
                      </el-col>
                    </el-row>
                  </div>
                  <div class="text item div-desc">
                    <el-row>
                      <span style="color: red;"><i class="el-icon-s-opportunity"></i>PrimaryURL:</span> &nbsp;{{ misconfiguration.PrimaryURL }}
                    </el-row>
                    <el-row>
                      <span style="color: red;">Description:</span> {{ misconfiguration.Description }}
                    </el-row>
                    <el-row>
                      <span style="color: red;">Message:</span> {{ misconfiguration.Message }}
                    </el-row>
                    <el-row>
                      <span style="color: red;">Query:</span> {{ misconfiguration.Query }}
                    </el-row>
                    <el-row>
                      <span style="color: red;">Resolution:</span> {{ misconfiguration.Resolution }}
                    </el-row>
                  </div>
                  <div class="text div-json">
                    <el-descriptions title="Layer" :column="2" v-if="misconfiguration.Layer">
                      <el-descriptions-item v-for="(vuln, index) in filterJson(misconfiguration.Layer)" :key="index" :label="vuln.key">
                          <span v-if="!vuln.flag" show-overflow-tooltip>
                            <el-tooltip class="item" effect="dark" :content="JSON.stringify(vuln.value)" placement="top-start">
                              <el-link type="primary" style="color: #0000e4;">{{ 'Details' }}</el-link>
                            </el-tooltip>
                          </span>
                        <el-tooltip v-if="vuln.flag && vuln.value" class="item" effect="light" :content="typeof(vuln.value) === 'boolean'?vuln.value.toString():vuln.value" placement="top-start">
                            <span class="table-expand-span-value">
                                {{ vuln.value }}
                            </span>
                        </el-tooltip>
                        <span v-if="vuln.flag && !vuln.value"> N/A</span>
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                  <div class="text div-json">
                    <el-descriptions title="CauseMetadata" :column="2" v-if="misconfiguration.CauseMetadata">
                      <el-descriptions-item v-for="(vuln, index) in filterJson(misconfiguration.CauseMetadata)" :key="index" :label="vuln.key">
                          <span v-if="!vuln.flag" show-overflow-tooltip>
                            <el-tooltip class="item" effect="dark" :content="JSON.stringify(vuln.value)" placement="top-start">
                              <el-link type="primary" style="color: #0000e4;">{{ 'Details' }}</el-link>
                            </el-tooltip>
                          </span>
                        <el-tooltip v-if="vuln.flag && vuln.value" class="item" effect="light" :content="typeof(vuln.value) === 'boolean'?vuln.value.toString():vuln.value" placement="top-start">
                            <span class="table-expand-span-value">
                                {{ vuln.value }}
                            </span>
                        </el-tooltip>
                        <span v-if="vuln.flag && !vuln.value"> N/A</span>
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                  <div class="text div-json">
                    <el-descriptions title="References" :column="2" v-if="misconfiguration.References">
                      <el-descriptions-item v-for="(Reference, index) in misconfiguration.References" :key="index" :label="index">
                        <span> {{ Reference }}</span>
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                </el-card>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

/* eslint-disable */
export default {
  components: {
  },
  props: {
    logForm: {}
  },
  data() {
    return {
      filterJson: this.filterJsonKeyAndValue,
    }
  },
  methods: {
    init() {
    },
    filterJsonKeyAndValue(json) {
      //json is json object , not array -- harris
      let list = json;
      if(typeof json === 'object') {
        list = json;
      } else {
        list = JSON.parse(json);
      }

      let jsonKeyAndValue = [];

      for (let item in list) {
        let flag = true;
        let value = list[item];
        //string && boolean的值直接显示, object是[{}]
        if (typeof (value) === 'number') {
          value = String(value);
        }
        if (typeof (value) === 'object') {
          if (value !== null && JSON.stringify(value) !== '[]' && JSON.stringify(value) !== '{}') {
            flag = false;
          }
          if (JSON.stringify(value) === '[]' || JSON.stringify(value) === '{}') {
            value = "";
          }
        }

        if (item.indexOf('$$') === -1 && item !== 'show') {
          let map = {key: item, value: value, flag: flag};
          jsonKeyAndValue.push(map);
        }
      }
      return jsonKeyAndValue;
    },
  },
  created() {
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
</style>

