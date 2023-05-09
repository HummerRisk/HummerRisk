<template>
  <container v-loading="result.loading" class="container">
    <el-col :span="24" style="padding: 0;">
      <el-row>
        <el-card class="table-card">
          <el-row>
            <el-col :span="24">
              <span class="hr-card-data">
                <span class="hr-card-data-digital">{{ $t('commons.associated_assets') }}</span>
                <span class="hr-card-data-unit"> {{ 'Assets' }}</span>
              </span>
              <span class="hr-card-desc"></span>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-row class="lb-row-txt-white">
                <el-col :span="6">
                  <!-- cloud -->
                  <el-card class="cloud-card">
                    <div slot="header" class="clearfix">
                      <span>{{ $t('commons.cloud_scan') }}</span>
                    </div>
                    <div class="text item">
                      <el-row>
                        <el-col :span="8">
                          <h1 class="cloud-h4">{{ cloudInfo.clouds }}</h1>
                        </el-col>
                        <el-col :span="8">
                          <h1 class="cloud-h3">{{ cloudInfo.accounts }}</h1>
                        </el-col>
                        <el-col :span="8">
                          <h1 class="cloud-h1">{{ cloudInfo.resources }}</h1>
                        </el-col>
                      </el-row>
                      <el-row>
                        <el-col :span="8">
                          <h5 class="cloud-h2">{{ $t('account.cloud_platform') }}</h5>
                        </el-col>
                        <el-col :span="8">
                          <h5 class="cloud-h2">{{ $t('account.cloud_account') }}</h5>
                        </el-col>
                        <el-col :span="8">
                          <h5 class="cloud-h2">{{ $t('commons.cloud_resource') }}</h5>
                        </el-col>
                      </el-row>
                      <el-row>
                        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-for="(item, index) in cloudInfo.plugins" :key="index" style="margin-top: 8px;">
                          <img v-if="item.exist" :src="require(`@/assets/img/platform/${item.icon}`)" style="width: 25px; height: 25px; vertical-align:middle" alt=""/>
                          <img v-if="!item.exist" :src="require(`@/assets/img/platform/dark/${item.icon}`)" style="width: 25px; height: 25px; vertical-align:middle" alt=""/>
                        </el-col>
                      </el-row>
                    </div>
                  </el-card>
                  <!-- cloud -->
                </el-col>
                <el-col :span="6">
                  <!-- k8s & server -->
                  <el-card class="cloud-card">
                    <div slot="header" class="clearfix">
                      <span>{{ $t('commons.k8s_scan') }} & {{ $t('dashboard.server_scan') }}</span>
                    </div>
                    <div class="text item">
                      <el-row>
                        <el-col :span="8">
                          <h1 class="cloud-h4">{{ k8sInfo.clouds }}</h1>
                        </el-col>
                        <el-col :span="8">
                          <h1 class="cloud-h3">{{ k8sInfo.accounts }}</h1>
                        </el-col>
                        <el-col :span="8">
                          <h1 class="cloud-h1">{{ k8sInfo.resources }}</h1>
                        </el-col>
                      </el-row>
                      <el-row>
                        <el-col :span="8">
                          <h5 class="cloud-h2">{{ $t('commons.k8s_platform') }}</h5>
                        </el-col>
                        <el-col :span="8">
                          <h5 class="cloud-h2">{{ $t('commons.k8s_account') }}</h5>
                        </el-col>
                        <el-col :span="8">
                          <h5 class="cloud-h2">{{ $t('commons.k8s_resource') }}</h5>
                        </el-col>
                      </el-row>
                      <el-row>
                        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-for="(item, index) in k8sInfo.plugins" :key="index" style="margin-top: 8px;">
                          <img v-if="item.exist" :src="require(`@/assets/img/platform/${item.icon}`)" style="width: 25px; height: 25px; vertical-align:middle" alt=""/>
                          <img v-if="!item.exist" :src="require(`@/assets/img/platform/dark/${item.icon}`)" style="width: 25px; height: 25px; vertical-align:middle" alt=""/>
                        </el-col>
                      </el-row>
                    </div>
                    <div class="cs-scan">
                      <span></span>
                    </div>
                    <div class="text item">
                      <el-row>
                        <el-col :span="8">
                          <h1 class="cloud-h5">{{ serverInfo.param1 }}</h1>
                        </el-col>
                        <el-col :span="8">
                          <h1 class="cloud-h4">{{ serverInfo.param2 }}</h1>
                        </el-col>
                        <el-col :span="8">
                          <h1 class="cloud-h4">{{ serverInfo.param3 }}</h1>
                        </el-col>
                      </el-row>
                      <el-row>
                        <el-col :span="8">
                          <h5 class="cloud-h2">{{ $t('server.server_1') }}{{ $t('server.public_key') }}</h5>
                        </el-col>
                        <el-col :span="8">
                          <h5 class="cloud-h2">{{ 'Linux' }}</h5>
                        </el-col>
                        <el-col :span="8">
                          <h5 class="cloud-h2">{{ 'Windows' }}</h5>
                        </el-col>
                      </el-row>
                    </div>
                  </el-card>
                  <!-- k8s -->
                </el-col>
                <el-col :span="6">
                  <!-- image & config -->
                  <el-card class="cloud-card">
                    <div slot="header" class="clearfix">
                      <span>{{ $t('dashboard.image_scan') }} & {{ $t('dashboard.config_scan') }}</span>
                    </div>
                    <div class="text item">
                      <el-row>
                        <el-col :span="8">
                          <h1 class="cloud-h5">{{ imageInfo.param1 }}</h1>
                        </el-col>
                        <el-col :span="8">
                          <h1 class="cloud-h1">{{ imageInfo.param2 }}</h1>
                        </el-col>
                        <el-col :span="8">
                          <h1 class="cloud-h3">{{ imageInfo.param3 }}</h1>
                        </el-col>
                      </el-row>
                      <el-row>
                        <el-col :span="8">
                          <h5 class="cloud-h2">{{ $t('image.image_repo') }}</h5>
                        </el-col>
                        <el-col :span="8">
                          <h5 class="cloud-h2">{{ $t('k8s.image') }}</h5>
                        </el-col>
                        <el-col :span="8">
                          <h5 class="cloud-h2">{{ $t('dashboard.image_scan') }}</h5>
                        </el-col>
                      </el-row>
                      <el-row style="margin-top: 4px;">
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #8B0000;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #FF4D4D;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #FF8000;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #336D9F;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                      </el-row>
                    </div>
                    <div class="cs-scan">
                      <span></span>
                    </div>
                    <div class="text item">
                      <el-row>
                        <el-col :span="12">
                          <h1 class="cloud-h1">{{ configInfo.param1 }}</h1>
                        </el-col>
                        <el-col :span="12">
                          <h1 class="cloud-h3">{{ configInfo.param2 }}</h1>
                        </el-col>
                      </el-row>
                      <el-row>
                        <el-col :span="12">
                          <h5 class="cloud-h2">{{ $t('config.config_settings') }}</h5>
                        </el-col>
                        <el-col :span="12">
                          <h5 class="cloud-h2">{{ $t('dashboard.config_scan') }}</h5>
                        </el-col>
                      </el-row>
                      <el-row style="margin-top: 4px;">
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #8B0000;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #FF4D4D;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #FF8000;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #336D9F;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                      </el-row>
                    </div>
                  </el-card>
                  <!-- image & config -->
                </el-col>
                <el-col :span="6">
                  <!-- code & fs -->
                  <el-card class="cloud-card">
                    <div slot="header" class="clearfix">
                      <span>{{ $t('dashboard.code_scan') }} & {{ $t('dashboard.fs_scan') }}</span>
                    </div>
                    <div class="text item">
                      <el-row>
                        <el-col :span="12">
                          <h1 class="cloud-h1">{{ codeInfo.param1 }}</h1>
                        </el-col>
                        <el-col :span="12">
                          <h1 class="cloud-h3">{{ codeInfo.param2 }}</h1>
                        </el-col>
                      </el-row>
                      <el-row>
                        <el-col :span="12">
                          <h5 class="cloud-h2">{{ $t('code.code') }}</h5>
                        </el-col>
                        <el-col :span="12">
                          <h5 class="cloud-h2">{{ $t('dashboard.code_scan') }}</h5>
                        </el-col>
                      </el-row>
                      <el-row style="margin-top: 4px;">
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #8B0000;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #FF4D4D;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #FF8000;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #336D9F;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                      </el-row>
                    </div>
                    <div class="cs-scan">
                      <span></span>
                    </div>
                    <div class="text item">
                      <el-row>
                        <el-col :span="12">
                          <h1 class="cloud-h1">{{ fsInfo.param1 }}</h1>
                        </el-col>
                        <el-col :span="12">
                          <h1 class="cloud-h3">{{ fsInfo.param2 }}</h1>
                        </el-col>
                      </el-row>
                      <el-row>
                        <el-col :span="12">
                          <h5 class="cloud-h2">{{ $t('oss.object_file') }}</h5>
                        </el-col>
                        <el-col :span="12">
                          <h5 class="cloud-h2">{{ $t('dashboard.fs_scan') }}</h5>
                        </el-col>
                      </el-row>
                      <el-row style="margin-top: 4px;">
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #8B0000;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #FF4D4D;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #FF8000;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                        <el-col :span="6">
                          <div style="height: 8px;background-color: #336D9F;margin: 1px;border-radius: 1px;"></div>
                        </el-col>
                      </el-row>
                    </div>
                  </el-card>
                  <!-- code & fs -->
                </el-col>
              </el-row>
            </el-col>
          </el-row>
        </el-card>
      </el-row>
    </el-col>
  </container>
</template>

<script>
import Container from "../.././common/components/Container";
import {
  cloudInfoUrl, codeInfoUrl,
  configInfoUrl, fsInfoUrl,
  imageInfoUrl,
  k8sInfoUrl,
  serverInfoUrl,
} from "@/api/cloud/dashboard/dashboard";

/* eslint-disable */
export default {
  components: {
    Container,
  },
  data() {
    return {
      result: {},
      cloudInfo: {},
      k8sInfo: {},
      serverInfo: {},
      imageInfo: {},
      configInfo: {},
      codeInfo: {},
      fsInfo: {},
    }
  },
  methods: {
    init() {
      this.result = this.$post(cloudInfoUrl, {}, response => {
        let data = response.data;
        this.cloudInfo = data;
      });
      this.result = this.$post(k8sInfoUrl, {}, response => {
        let data = response.data;
        this.k8sInfo = data;
      });
      this.result = this.$post(serverInfoUrl, {}, response => {
        let data = response.data;
        this.serverInfo = data;
      });
      this.result = this.$post(imageInfoUrl, {}, response => {
        let data = response.data;
        this.imageInfo = data;
      });
      this.result = this.$post(configInfoUrl, {}, response => {
        let data = response.data;
        this.configInfo = data;
      });
      this.result = this.$post(codeInfoUrl, {}, response => {
        let data = response.data;
        this.codeInfo = data;
      });
      this.result = this.$post(fsInfoUrl, {}, response => {
        let data = response.data;
        this.fsInfo = data;
      });
    },
  },
  created() {
    this.init();
  }
}
</script>

<style scoped>
.container {
  padding: 3px 5px 3px 15px;
}

.table-card {
  min-height: 380px;
}

.cloud-card {
  margin: 2%;
  padding: 2%;
  min-height: 278px;
}

.hr-card-index-1 .hr-card-data-digital {
  color: #0051a4;
}

.hr-card-index-1 {
  border-left-color: #0051a4;
  border-left-width: 3px;
}

.hr-card-index-2 .hr-card-data-digital {
  color: #65A2FF;
}

.hr-card-index-2 {
  border-left-color: #65A2FF;
  border-left-width: 3px;
}

.hr-card-index-3 .hr-card-data-digital {
  color: #E69147;
}

.hr-card-index-3 {
  border-left-color: #E69147;
  border-left-width: 3px;
}

.hr-card-index-4 .hr-card-data-digital {
  color: #E6113C;
}

.hr-card-index-4 {
  border-left-color: #E6113C;
  border-left-width: 3px;
}

.hr-card-index-5 .hr-card-data-digital {
  color: #44B349;
}

.hr-card-index-5 {
  border-left-color: #44B349;
  border-left-width: 3px;
}

.hr-card-index-6 .hr-card-data-digital {
  color: #893fdc;
}

.hr-card-index-6 {
  border-left-color: #893fdc;
  border-left-width: 3px;
}

.hr-card-data {
  text-align: left;
  display: block;
  margin: 5px 0;
}

.hr-card-desc {
  display: block;
  text-align: left;
}

.hr-card-data-digital {
  font-size: 21px;
}

.hr-card-data-unit {
  color: #8492a6;
  font-size: 10px;
}

.split {
  height: 80px;
  border-left: 1px solid #D8DBE1;
}

.co-el-img {
  padding: 0 !important;
}

.co-el-img-div {
  margin-top: 3%;
}

.co-el-img >>> .el-image {
  display: table-cell;
  padding: 0;
}

.cp-el-i {
  margin: 1%;
}

.co-el-i{
  width: 70px;
  height: 70px;
}

.el-btn {
  min-width: 100px;
}

.el-total {
  font-size: 12px;
  margin: 10px;
  text-align: center;
}

.scan-span {
  font-style:italic;
  color: #72abf9;
}

.scan-span-ing {
  font-style:italic;
}

.lb-row-txt-white {
}

.lb-row-txt-white >>> .el-button {
  min-width: 150px;
}

.cloud-card >>> .el-card__header {
  padding: 10px;
}

.cloud-h1 {
  font-size: 24px;
  font-weight: bold;
  /*font-style:italic;//斜体*/
  margin: 8px 5px 0 5px;
}

.cloud-h2 {
  font-weight: normal;
  margin: 5px 0;
  color: #8492a6;
  font-size: 12px;
  display:inline-block;
  transform: scale(0.83);
}

.cloud-h3 {
  font-size: 24px;
  font-weight: bold;
  color: rgb(247, 105, 100);
  margin: 8px 5px 0 5px;
}

.cloud-h4 {
  font-size: 24px;
  font-weight: bold;
  color: rgb(255, 165, 61);
  margin: 8px 5px 0 5px;
}

.cloud-h5 {
  font-size: 24px;
  font-weight: bold;
  color: rgb(110, 116, 142);
  margin: 8px 5px 0 5px;
}

.cs-scan {
  padding: 0 0 10px 10px;
  margin: 10px 0;
  border-bottom: 1px solid #EBEEF5;
}

.cs-card >>> .el-card__body {
  padding: 10px;
}

.server-h1 {
  font-size: 16px;
  font-weight: bold;
  font-style:italic;
  margin: 8px 5px 0 5px;
}

.hr-card-data-unit-server {
  color: #8492a6;
  font-size: 12px;
  display:inline-block;
  transform: scale(0.58);
}

</style>

