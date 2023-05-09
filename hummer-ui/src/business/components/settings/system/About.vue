<template>
  <div v-loading="result.loading">
    <el-card class="about-card">

      <el-row>
        <img :src="require(`@/assets/img/logo/license.png`)" class="image"/>
      </el-row>

      <div style="padding: 14px;text-align: center;">
        <el-row>
          <el-col :span="12" class="col-na">
            <span>{{ $t('system.authorized_to') }}</span>
          </el-col>
          <el-col :span="10" class="col-te">
            <span>{{ license?license.company:$t('system.no_license') }}</span>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12" class="col-na">
            <span>{{ $t('system.expiration') }}</span>
          </el-col>
          <el-col :span="10" class="col-te">
            <span>{{ license?license.expiration:'--' }}</span>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12" class="col-na">
            <span>{{ $t('system.version') }}</span>
          </el-col>
          <el-col :span="10" class="col-te">
            <span>{{ expirationTime?$t('system.enterprise'):$t('system.community') }}</span>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12" class="col-na">
            <span>{{ $t('system.version_number') }}</span>
          </el-col>
          <el-col :span="10" class="col-te">
            <span>{{ license?license.expiration:'v1.1.0' }}</span>
          </el-col>
        </el-row>
      </div>

      <div style="padding: 14px;">
        <el-row>
          <el-col :span="24" class="license-le">
            <el-button plain size="medium" type="primary" @click="updateLicense">{{ $t('system.update_license') }}</el-button>
            <el-button plain size="medium" @click="others">{{ $t('dashboard.i18n_other') }}</el-button>
          </el-col>
        </el-row>
      </div>

      <el-drawer class="rtl" :title="$t('system.update_license')" :visible.sync="licenseVisible" size="60%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form">
          <el-form-item :label="$t('oss.object_file')" :rules="{required: true, message: $t('oss.object_file') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <upload v-on:appendUpload="appendUpload" v-model="form.path"/>
          </el-form-item>
        </el-form>
        <dialog-footer
          @cancel="licenseVisible = false"
          @confirm="uploadFile()"/>
      </el-drawer>

      <el-drawer class="rtl" :title="$t('dashboard.i18n_other')" :visible.sync="dialogVisible" size="60%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-descriptions class="margin-top" title="" :column="1" size="medium" border>
          <el-descriptions-item v-for="item in items" :key="item.url">
            <template slot="label">
              <img class="logo github-icon" :src="item.img"/>
            </template>
            <el-link class="url" :href="item.url" target="_blank">
              <span>{{ item.url }}</span>
            </el-link>
          </el-descriptions-item>
        </el-descriptions>
      </el-drawer>
    </el-card>
  </div>

</template>

<script>

import Upload from "@/business/components/settings/head/Upload";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {updateLicenseUrl} from "@/api/system/system";
import {getLicense} from '@/common/js/auth';

/* eslint-disable */
  export default {
    components: {
      Upload,
      DialogFooter,
    },
    name: "AboutUs",
    data() {
      return {
        result: false,
        dialogVisible: false,
        licenseVisible: false,
        direction: 'rtl',
        form: {},
        objectFile: Object,
        license: null,
        expirationTime: false,
        githubUrl: 'https://github.com/HummerRisk/HummerRisk',
        websiteUrl: 'https://hummerrisk.com',
        items: [
          {img: require(`@/assets/img/about/docs.png`), url: 'https://hummerrisk.com'},
          {img: require(`@/assets/img/about/github.png`), url: 'https://github.com/HummerRisk/HummerRisk'},
          {img: require(`@/assets/img/about/custodian.png`), url: 'https://docs.hummerrisk.com/related/opensource-tool/custodian/'},
          {img: require(`@/assets/img/about/prowler.png`), url: 'https://docs.hummerrisk.com/related/opensource-tool/prowler/'},
          {img: require(`@/assets/img/about/trivy.png`), url: 'https://docs.hummerrisk.com/related/opensource-tool/trivy/'},
          {img: require(`@/assets/img/about/kubench.png`), url: 'https://docs.hummerrisk.com/related/opensource-tool/kube-bench/'},
        ],
      }
    },
    created() {
      this.init();
    },
    methods: {
      init() {
        this.search();
      },
      search() {
        let licenseKey = getLicense();
        if (licenseKey && licenseKey != 'null') {
          this.license = licenseKey;
          let date =new Date();
          let now = Date.parse(date);
          if(licenseKey.expiration > now) this.expirationTime = true;
        }
      },
      handleClose() {
        this.dialogVisible = false;
        this.licenseVisible = false;
      },
      updateLicense() {
        this.licenseVisible = true;
      },
      others() {
        this.dialogVisible = true;
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
          url: updateLicenseUrl,
          data: formData,
          headers: {
            "Content-Type": 'multipart/form-data'
          }
        };
        this.result = this.$request(axiosRequestConfig, (res) => {
          if (res.success) {
            this.$success(this.$t('commons.save_success'));
            this.licenseVisible = false;
            this.search();
          }
        });
      },
    }
  }
</script>

<style scoped>

  .logo {
    height: 20px;
    line-height: 30px;
    vertical-align: middle;
  }

  .github-icon {
    font-size: 20px;
    border-radius: 50%;
    width: 30px;
    height: 30px;
    text-align: center;
  }

  .el-row {
    margin-bottom: 3%;
  }

  .url {
    margin-left: 5px;
  }

  .about-us >>> .el-dialog {
    width: 500px;
  }

  .margin-top >>> .el-descriptions-item__label {
    text-align: center;
  }

  .image {
    width: 100%;
    display: block;
  }

  .col-na {
    font-size: 16px;
    font-weight: bold;
  }

  .col-te {
    color: #888;
  }

  .about-card {
    width: 70%;
    margin-top: 12%;
    margin-left: 15%;
  }

  .license-le {
    text-align: center;
  }

  .about-card >>> .el-card__body {
    padding: 0;
  }

</style>
