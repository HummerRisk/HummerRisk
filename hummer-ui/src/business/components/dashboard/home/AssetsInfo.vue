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
                  <el-card class="cloud-card">
                    <div slot="header" class="clearfix">
                      <span>{{ $t('commons.cloud_scan') }}</span>
                    </div>
                    <div class="text item">
                      <el-row>
                        <el-col :span="8">
                          <h1 class="cloud-h1">{{ cloudInfo.clouds }}</h1>
                        </el-col>
                        <el-col :span="8">
                          <h1 class="cloud-h1">{{ cloudInfo.accounts }}</h1>
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
                          <img :src="require(`@/assets/img/platform/${item.icon}`)" style="width: 25px; height: 25px; vertical-align:middle" alt=""/>
                        </el-col>
                      </el-row>
                    </div>
                  </el-card>
                </el-col>
                <el-col :span="6">
                  <el-card class="cloud-card">

                  </el-card>
                </el-col>
                <el-col :span="6">
                  <el-card class="cloud-card">

                  </el-card>
                </el-col>
                <el-col :span="6">
                  <el-card class="cloud-card">

                  </el-card>
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
import {getCurrentUser} from "@/common/js/utils";
import {cloudInfoUrl, topInfoUrl} from "@/api/cloud/dashboard/dashboard";

/* eslint-disable */
export default {
  components: {
    Container,
  },
  data() {
    return {
      result: {},
      cloudInfo: {},
    }
  },
  methods: {
    init() {
      this.result = this.$post(cloudInfoUrl, {}, response => {
        let data = response.data;
        this.cloudInfo = data;
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
  min-height: 270px;
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
  font-style:italic;
  margin: 8px 5px 0 5px;
}

.cloud-h2 {
  font-size: 12px;
  font-weight: normal;
  color: #888;
  margin: 5px 5px 0 5px;
}

</style>

