<template>
  <div>
    <div class="ch-mar" v-if="logForm.returnJson">
      <h2>Summary:&nbsp;</h2>
      <ul style="margin: 5px 0 0 60px;">
        <li><i>Scan Name</i>: {{ logForm.name }}</li>
        <li><i>Scan User</i>:&nbsp;{{ logForm.userName }}</li>
        <li><i>ArtifactType</i>:&nbsp;{{ logForm.returnJson.ArtifactType }}</li>
        <li><i>ArtifactName</i>:&nbsp;{{ logForm.returnJson.ArtifactName }}</li>
        <li><i>SchemaVersion</i>:&nbsp;{{ logForm.returnJson.SchemaVersion }}</li>
        <li><i>Architecture</i>:&nbsp;{{ logForm.returnJson.Metadata.ImageConfig?logForm.returnJson.Metadata.ImageConfig.architecture:'N/A' }}</li>
        <li><i>Create Time</i>:&nbsp;{{ logForm.createTime | timestampFormatDate }}</li>
        <li><i>Result Status</i>:&nbsp;{{ logForm.resultStatus }}</li>
        <li><i>Vulnerabilities Found</i>: {{ logForm.returnSum }}</li>
      </ul>
    </div>
    <div class="ch-mar" v-if="logForm.returnJson">
      <div class="ch-mar-top">
        <h2>Details:&nbsp;</h2>
        <div class="ch-mar-top" v-if="logForm.returnJson.Results">
          <div class="ch-mar-top" :key="index" v-for="(result, index) in logForm.returnJson.Results">
            <div class="ch-mar" v-if="result">
              <h3>Report:&nbsp;</h3>
              <ul style="margin-left: 60px;">
                <li><i>Target</i>: {{ result.Target }}</li>
                <li><i>Class</i>:&nbsp;{{ result.Class }}</li>
                <li><i>Type</i>:&nbsp;{{ result.Type }}</li>
              </ul>
            </div>
            <div v-if="result.Vulnerabilities">
              <div class="ch-mar-top" :key="index" v-for="(vulnerability, index) in result.Vulnerabilities">
                <el-card shadow="hover" v-bind:class="{ 'box-card box-card-critical': vulnerability.Severity === 'CRITICAL',
                   'box-card box-card-high': vulnerability.Severity === 'HIGH',
                   'box-card box-card-medium': vulnerability.Severity === 'MEDIUM',
                   'box-card box-card-low': vulnerability.Severity === 'LOW',
                   'box-card box-card-unknown': vulnerability.Severity === 'UNKNOWN' }">
                  <div slot="header" class="clearfix">
                    <el-row>
                      <el-col v-bind:class="{ 'icon-title box-critical': vulnerability.Severity === 'CRITICAL',
                                'icon-title box-high': vulnerability.Severity === 'HIGH',
                                'icon-title box-medium': vulnerability.Severity === 'MEDIUM',
                                'icon-title box-low': vulnerability.Severity === 'LOW',
                                'icon-title box-unknown': vulnerability.Severity === 'UNKNOWN' }"
                              :span="3">
                        <span>{{ vulnerability.Severity.substring(0, 1) }}</span>
                      </el-col>
                      <el-col :span="15" style="margin: -7px 0 0 15px;">
                        <span style="font-size: 24px;font-weight: 500;">{{ vulnerability.Title }}</span>
                      </el-col>
                      <el-col :span="6" style="float: right;">
                        <span style="font-size: 20px;color: #999;float: right;">{{ 'SEVERITY SOURCE' }}</span>
                      </el-col>
                    </el-row>
                    <el-row style="font-size: 18px;padding: 10px;">
                      <el-col :span="20">
                        <span style="margin: 5px;"><a :href="vulnerability.PrimaryURL" target="_blank">{{ vulnerability.VulnerabilityID }}</a></span>
                        <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                        <span style="margin: 5px;">
                            <el-button v-bind:class="{ 'box-critical': vulnerability.Severity === 'CRITICAL',
                              'box-high': vulnerability.Severity === 'HIGH',
                              'box-medium': vulnerability.Severity === 'MEDIUM', 'box-low': vulnerability.Severity === 'LOW',
                              'box-unknown': vulnerability.Severity === 'UNKNOWN' }" size="mini">{{ vulnerability.Severity }}
                            </el-button>
                          </span>
                        <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                        <span style="color: #888;margin: 5px;">INSTALLED VERSION: {{ vulnerability.InstalledVersion }}</span>
                        <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                        <span style="color: #444;margin: 5px;">PkgName: {{ vulnerability.PkgName }}</span>
                      </el-col>
                      <el-col :span="4" style="float: right;">
                        <span style="font-size: 20px;color: #000;float: right;">{{ vulnerability.SeveritySource }}</span>
                      </el-col>
                    </el-row>
                  </div>
                  <div class="text item div-desc">
                    <el-row>
                      <span style="color: red;"><i class="el-icon-s-opportunity"></i>PrimaryURL:</span> &nbsp;{{ vulnerability.PrimaryURL }}
                    </el-row>
                    <el-row>
                      <span style="color: red;">Description:</span> {{ vulnerability.Description }}
                    </el-row>
                    <el-row>
                      <span style="color: red;">PublishedDate:</span> {{ vulnerability.PublishedDate }}
                    </el-row>
                    <el-row>
                      <span style="color: red;">LastModifiedDate:</span> {{ vulnerability.LastModifiedDate }}
                    </el-row>
                    <el-row>
                      <span style="color: red;">FixedVersion:</span> {{ vulnerability.FixedVersion }}
                    </el-row>
                    <el-row>
                      <span style="color: red;">DataSource:</span> {{ vulnerability.DataSource.ID }} | {{ vulnerability.DataSource.Name }} | {{ vulnerability.DataSource.URL }}
                    </el-row>
                  </div>
                  <div class="text div-json">
                    <el-descriptions title="Layer" :column="2" v-if="vulnerability.Layer">
                      <el-descriptions-item v-for="(vuln, index) in filterJson(vulnerability.Layer)" :key="index" :label="vuln.key">
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
                    <el-descriptions title="CweIDs" :column="2" v-if="vulnerability.CweIDs">
                      <el-descriptions-item v-for="(CweID, index) in vulnerability.CweIDs" :key="index" :label="index">
                        <span> {{ CweID }}</span>
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                  <div class="text div-json">
                    <el-descriptions title="References" :column="2" v-if="vulnerability.References">
                      <el-descriptions-item v-for="(Reference, index) in vulnerability.References" :key="index" :label="index">
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

