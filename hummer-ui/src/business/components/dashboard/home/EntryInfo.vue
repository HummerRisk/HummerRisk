<template>
  <container v-loading="result.loading" class="container">
    <el-col :span="24" style="padding: 0;">
      <el-row>
        <el-card class="table-card">
          <el-row>
            <el-col :span="24">
              <span class="hr-card-data">
                <span class="hr-card-data-digital">{{ $t('commons.fast_entry') }}</span>
                <span class="hr-card-data-unit"> {{ 'Entry' }}</span>
              </span>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-row class="lb-row-txt-white">
                <el-col :span="4">
                  <el-button size="small" @click="goResource('cloud')"><i class="iconfont icon-guanyuyunguanjia"></i> {{ $t('commons.cloud_entry') }}</el-button>
                </el-col>
                <el-col :span="4">
                  <el-button size="small" @click="goResource('k8s')"><i class="iconfont icon-adsyunyuanshengshujuku"></i> {{ $t('commons.k8s_entry') }}</el-button>
                </el-col>
                <el-col :span="4">
                  <el-button size="small" @click="goResource('server')"><i class="iconfont icon-fuwuqi"></i> {{ $t('commons.server_entry') }}</el-button>
                </el-col>
                <el-col :span="4">
                  <el-button size="small" @click="goResource('image')"><i class="iconfont icon-jingxiang2"></i> {{ $t('commons.image_entry') }}</el-button>
                </el-col>
                <el-col :span="4">
                  <el-button size="small" @click="goResource('code')"><i class="iconfont icon-yuandaimaxiayoudaima"></i> {{ $t('commons.code_entry') }}</el-button>
                </el-col>
                <el-col :span="4">
                  <el-button size="small" @click="goResource('fs')"><i class="iconfont icon-wendang"></i> {{ $t('commons.fs_entry') }}</el-button>
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
import {topInfoUrl} from "@/api/cloud/dashboard/dashboard";

/* eslint-disable */
export default {
  components: {
    Container,
  },
  data() {
    return {
      result: {},
      currentUser: {},
      topInfo: {},
    }
  },
  methods: {
    init() {
      this.result = this.$post(topInfoUrl, {}, response => {
        let data = response.data;
        this.topInfo = data;
      });
    },
    goResource(type) {
      switch (type) {
        case 'cloud':
          this.$router.push({
            name: 'cloudAccount'
          }).catch(error => error);
          break;
        case 'k8s':
          this.$router.push({
            path: '/k8s/k8s'
          }).catch(error => error);
          break;
        case 'server':
          this.$router.push({
            path: '/server/server'
          }).catch(error => error);
          break;
        case 'image':
          this.$router.push({
            path: '/image/image'
          }).catch(error => error);
          break;
        case 'code':
          this.$router.push({
            path: '/code/code'
          }).catch(error => error);
          break;
        case 'fs':
          this.$router.push({
            path: '/fs/fs'
          }).catch(error => error);
          break;
      }
    },
  },
  created() {
    this.currentUser = getCurrentUser();
    this.init();
  }
}
</script>

<style scoped>
.container {
  padding: 3px 5px 3px 15px;
}

.table-card {
  min-height: 10%;
}

.table-card >>> .el-card__body {
  padding-top: 10px;
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
  min-width: 140px;
}

.iconfont {
  vertical-align: middle;
  margin-right: 5px;
  width: 20px;
  height: 15px;
  text-align: center;
  font-size: 14px;
}

</style>

