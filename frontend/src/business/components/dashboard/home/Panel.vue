<template>
  <main-container class="main-container" v-loading="result.loading">
    <el-card class="table-card">
      <el-row type="flex">
        <el-col :span="24" class="image">
          <div>
            <h1 class="lb1-txt-white">{{ $t('commons.cloud_native_platform') }}</h1>
            <h5 class="lb5-txt-white">{{ $t('commons.cloud_native_platform_desc') }}</h5>
            <el-row class="lb-row-txt-white">
              <span>
                <el-button type="warning" @click="goCloud"><i class="iconfont icon-guanyuyunguanjia"></i> {{ $t('commons.cloud_desc') }}</el-button>
                <el-button type="warning" @click="goK8s"><i class="iconfont icon-adsyunyuanshengshujuku"></i> {{ $t('commons.cloud_native_desc') }}</el-button>
              </span>
            </el-row>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <div class="welcome">
          <div><h1>{{ $t('commons.cloud_mix_scan') }}</h1></div>
          <div class="border"></div>
          <div class="cloud_scan_desc">{{ $t('commons.cloud_scan_desc') }}</div>
          <div class="cloud-first-div">
            <el-row :gutter="20">
              <el-col :span="8">
                <img src="@/assets/img/panel/first.png" class="cloud-first" alt="">
              </el-col>
              <el-col :span="8">
                <img src="@/assets/img/panel/second.png" class="cloud-second" alt="">
              </el-col>
              <el-col :span="8">
                <img src="@/assets/img/panel/third.png" class="cloud-third" alt="">
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="cloud-desc-div1">
                  <p>{{ $t('commons.cloud_first') }}</p>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="cloud-desc-div2">
                  <p>{{ $t('commons.cloud_second') }}</p>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="cloud-desc-div3">
                  <p>{{ $t('commons.cloud_third') }}</p>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
        <div class="title">
          <img src="@/assets/img/panel/cloud.png" class="title-img" alt="">
        </div>
      </el-row>
      <el-row :gutter="20">
        <div class="title2">
          <img src="@/assets/img/panel/native.png" class="title-img2" alt="">
        </div>
        <div class="welcome2">
          <div><h1>{{ $t('commons.cloud_native') }}</h1></div>
          <div class="border"></div>
          <div class="cloud_scan_desc">{{ $t('commons.cloud_native_scan_desc') }}</div>
          <div class="cloud-first-div">
            <el-row :gutter="20">
              <el-col :span="6">
                <img src="@/assets/img/panel/native1.png" class="cloud-native-rule" alt="">
              </el-col>
              <el-col :span="6">
                <img src="@/assets/img/panel/native2.png" class="cloud-native-rule" alt="">
              </el-col>
              <el-col :span="6">
                <img src="@/assets/img/panel/native3.png" class="cloud-native-rule" alt="">
              </el-col>
              <el-col :span="6">
                <img src="@/assets/img/panel/native4.png" class="cloud-native-rule" alt="">
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="cloud-desc-div1">
                  <p>{{ $t('commons.cloud_native_first') }}</p>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="cloud-desc-div2">
                  <p>{{ $t('commons.cloud_native_second') }}</p>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="cloud-desc-div3">
                  <p>{{ $t('commons.cloud_native_third') }}</p>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="cloud-desc-div3">
                  <p>{{ $t('commons.cloud_native_fourth') }}</p>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-row>
    </el-card>
  </main-container>
</template>

<script>
import MainContainer from "../../common/components/MainContainer";

/* eslint-disable */
export default {
  components: {
    MainContainer,
  },
  data() {
    return {
      result: {},
    }
  },
  methods: {
    goCloud() {
      let p = '/account/cloudaccount';
      this.$router.push({
        path: p
      }).catch(error => error);
    },
    goK8s() {
      let p = '/k8s/k8s';
      this.$router.push({
        path: p
      }).catch(error => error);
    },
  },
}
</script>

<style scoped>
.main-container {
  padding: 5px;
}
.container {
  width: 30%;
  min-width: 600px;
  max-width: 740px;
  height: 560px;
  /*background-color: #fff;*/
  margin: auto;
  position: absolute;
  top: 17%;
  left: 10%;
  right: 10%;
  box-shadow: #dddddd 0 0 10px;
}
.title {
  float: right;
  font-size: 32px;
  letter-spacing: 0;
  width: 48%;
}
.title2 {
  float: left;
  font-size: 32px;
  letter-spacing: 0;
  width: 48%;
}
.welcome {
  float: left;
  margin: 60px 0 0 50px;
  font-size: 16px;
  color: #171d2c;
  letter-spacing: 0;
  line-height: 18px;
  text-align: center;
  width: 48%;
}
.welcome2 {
  float: right;
  margin: 60px 50px 0 0;
  font-size: 16px;
  color: #171d2c;
  letter-spacing: 0;
  line-height: 18px;
  text-align: center;
  width: 48%;
}
.image {
  background: url(../../../../assets/img/panel/panel.png);
  background-size:cover;
  width: 100%;
  height: 260px;
}
.main-container >>> .el-card__body {
  padding:  0;
}
.main-container >>> .lb1-txt-white {
  color: white;
  font-size: 48px;
  padding: 45px 35px 0 120px;
}
.main-container >>> .lb5-txt-white {
  color: white;
  font-size: 18px;
  padding: 5px 35px 0 120px;
}
.main-container >>> .lb-row-txt-white {
  padding: 15px 35px 0 120px;
}
.cloud {
  width: 100%;
  height: 360px;
}
.title-img {
  width: 70%;
  text-align: center;
  margin: 5% 0 5% 15%;
}
.title-img2 {
  width: 70%;
  text-align: center;
  margin: 5% 0 5% 15%;
}
.border {
  height: 2px;
  margin: 20px auto 20px;
  width: 180px;
  background: #df913c;
}
.cloud-first {
  width: 153px;
}
.cloud-second {
  width: 130px;
}
.cloud-third {
  width: 144px;
}
.cloud-first-div {
  margin-top: 50px;
}
.cloud-desc-div1 {
  margin: 25px 0 0 0;
  color: #215d9a;
  text-align: center;
}
.cloud-desc-div2 {
  margin: 25px 0 0 0;
  color: #215d9a;
  text-align: center;
}
.cloud-desc-div3 {
  margin: 25px 0 0 0;
  color: #215d9a;
  text-align: center;
}
.cloud_scan_desc {
  font-size: 15px;
  line-height: 28px;
}
.cloud-native-rule {
  width: 75px;
  background-color: #215d9a;
}
</style>

