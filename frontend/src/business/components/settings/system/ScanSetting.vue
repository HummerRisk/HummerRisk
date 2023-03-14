<template>
  <div v-loading="result.loading">
    <!--检测参数设置-->
    <el-form :model="formInline" :rules="rules" ref="formInline" class="demo-form-inline"
             :disabled="show" v-loading="loading" size="small">
      <el-row>
        <el-col>
          <el-form-item :label="$t('system_parameter_setting.skip_db_update')" prop="skipDbUpdate">
            <el-select style="width: 100%;" v-model="formInline.skipDbUpdate" :placeholder="$t('system_parameter_setting.skip_db_update')">
              <el-option
                v-for="item in items"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-form-item :label="$t('system_parameter_setting.security_checks')" prop="securityChecks">
            <el-input v-model="formInline.securityChecks" :placeholder="$t('system_parameter_setting.security_checks')"
                      v-on:input="change()"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-form-item :label="$t('system_parameter_setting.ignore_unfixed')" prop="ignoreUnfixed">
            <el-select style="width: 100%;" v-model="formInline.ignoreUnfixed" :placeholder="$t('system_parameter_setting.ignore_unfixed')">
              <el-option
                v-for="item in items"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-form-item :label="$t('rule.severity')" prop="severity">
            <el-input v-model="formInline.severity" placeholder="CRITICAL,HIGH,MEDIUM,LOW,UNKNOWN"
                      v-on:input="change()"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-form-item :label="$t('system_parameter_setting.offline_scan')" prop="offlineScan">
            <el-select style="width: 100%;" v-model="formInline.offlineScan" :placeholder="$t('system_parameter_setting.offline_scan')">
              <el-option
                v-for="item in items"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div>
<!--      <el-button type="primary" @click="updateVulnDb" size="small">-->
<!--        {{ $t('commons.online_update_vuln_db') }}-->
<!--      </el-button>-->
<!--      <el-button type="primary" @click="updateVulnDbOffline" size="small">-->
<!--        {{ $t('commons.offline_update_vuln_db') }}-->
<!--      </el-button>-->
      <el-button @click="edit" v-if="showEdit" size="small">{{ $t('commons.edit') }}</el-button>
      <el-button type="success" @click="save('formInline')" v-if="showSave" :disabled="disabledSave" size="small">
        {{ $t('commons.save') }}
      </el-button>
      <el-button @click="cancel" type="info" v-if="showCancel" size="small">{{ $t('commons.cancel') }}</el-button>
    </div>

    <el-drawer
      size="50%"
      :title="$t('commons.offline_update_vuln_db')"
      :append-to-body="true"
      :before-close="innerDrawerClose"
      :visible.sync="innerDrawer">
      <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form">
        <el-form-item :label="$t('oss.object_file')" :rules="{required: true, message: $t('oss.object_file') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <upload v-on:appendUpload="appendUpload" v-model="form.path"/>
        </el-form-item>
      </el-form>
      <dialog-footer
        @cancel="innerDrawer = false"
        @confirm="uploadFile()"/>
    </el-drawer>
  </div>
</template>

<script>
/* eslint-disable */
import Upload from "@/business/components/settings/head/Upload";
import DialogFooter from "@/business/components/common/components/DialogFooter";

export default {
  name: "ScanSetting",
  components: {
    DialogFooter,
    Upload,
  },
  data() {
    return {
      formInline: {},
      input: '',
      visible: true,
      result: {},
      showEdit: true,
      showSave: false,
      showCancel: false,
      show: true,
      disabledSave: false,
      loading: false,
      rules: {
        skipDbUpdate: [
          {
            required: true,
            message: 'SkipDbUpdate',
            trigger: ['change', 'blur']
          },
        ],
        securityChecks: [
          {
            required: true,
            message: 'SecurityChecks',
            trigger: ['change', 'blur']
          }
        ],
        ignoreUnfixed: [
          {
            required: true,
            message: 'IgnoreUnfixed',
            trigger: ['change', 'blur']
          }
        ],
        severity: [
          {
            required: true,
            message: 'severity',
            trigger: ['change', 'blur']
          }
        ],
        offlineScan: [
          {
            required: true,
            message: 'offlineScan',
            trigger: ['change', 'blur']
          }
        ],
      },
      items: [
        {id: 'true', name: 'True'},
        {id: 'false', name: 'False'}
      ],
      innerDrawer: false,
      form: {},
      objectFile: Object,
    }
  },

  created() {
    this.query()
  },
  methods: {
    query() {
      this.result = this.$get("/system/scanSetting/info", response => {
        this.$set(this.formInline, "skipDbUpdate", response.data[0].paramValue);
        this.$set(this.formInline, "securityChecks", response.data[1].paramValue);
        this.$set(this.formInline, "ignoreUnfixed", response.data[2].paramValue);
        this.$set(this.formInline, "severity", response.data[3].paramValue);
        this.$set(this.formInline, "offlineScan", response.data[4].paramValue);
        this.$nextTick(() => {
          this.$refs.formInline.clearValidate();
        })
      })
    },
    change() {
      if (!this.formInline.skipDbUpdate || !this.formInline.securityChecks || !this.formInline.ignoreUnfixed ||
        !this.formInline.severity || !this.formInline.offlineScan) {
        this.disabledSave = true;
      } else {
        this.disabledSave = false;
      }
    },
    edit() {
      this.showEdit = false;
      this.showSave = true;
      this.showCancel = true;
      this.show = false;
    },
    save(formInline) {
      this.showEdit = true;
      this.showCancel = false;
      this.showSave = false;
      this.show = true;
      let param = [
        {paramKey: "scansetting.skipDbUpdate", paramValue: this.formInline.skipDbUpdate, type: "boolean", sort: 1},
        {paramKey: "scansetting.securityChecks", paramValue: this.formInline.securityChecks, type: "text", sort: 2},
        {paramKey: "scansetting.ignoreUnfixed", paramValue: this.formInline.ignoreUnfixed, type: "boolean", sort: 3},
        {paramKey: "scansetting.severity", paramValue: this.formInline.severity, type: "text", sort: 4},
        {paramKey: "scansetting.offlineScan", paramValue: this.formInline.offlineScan, type: "boolean", sort: 5}
      ]

      this.$refs[formInline].validate(valid => {
        if (valid) {
          this.result = this.$post("/system/edit/scanSetting", param, response => {
            let flag = response.success;
            if (flag) {
              this.$success(this.$t('commons.save_success'));
            } else {
              this.$message.error(this.$t('commons.save_failed'));
            }
          });
        } else {
          return false;
        }
      })
    },
    cancel() {
      this.showEdit = true;
      this.showCancel = false;
      this.showSave = false;
      this.show = true;
      this.query();
    },
    updateVulnDb() {
      this.result = this.$get("/system/updateVulnDb", response => {
        this.$success(this.$t('commons.success'));
      });
    },
    updateVulnDbOffline() {
      this.form = {};
      this.objectFile = Object;
      this.innerDrawer = true;
    },
    innerDrawerClose() {
      this.innerDrawer = false;
    },
    appendUpload(file) {
      this.objectFile = file;
    },
    uploadFile() {
      let formData = new FormData();
      if (this.objectFile) {
        formData.append("objectFile", this.objectFile);
      }
      formData.append("request", new Blob([JSON.stringify(this.uploadForm)], {type: "application/json"}));
      let axiosRequestConfig = {
        method: "POST",
        url: "/system/updateVulnDbOffline",
        data: formData,
        headers: {
          "Content-Type": 'multipart/form-data'
        }
      };
      this.result = this.$request(axiosRequestConfig, (res) => {
        if (res.success) {
          this.$success(this.$t('commons.save_success'));
          this.query();
          this.innerDrawer = false;
        }
      });
    },
  }
}
</script>

<style scoped>

</style>
