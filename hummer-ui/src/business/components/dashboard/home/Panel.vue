<template>
  <main-container class="main-container" v-loading="result.loading">
    <el-card shadow="hover" class="table-card">
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
          <img src="@/assets/img/panel/cloud5.png" class="title-img" alt="">
        </div>
      </el-row>
      <el-row :gutter="20" class="el-row2">
        <div class="title2">
          <img src="@/assets/img/panel/cloud4.png" class="title-img2" alt="">
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
      let p = '/account/cloud-account';
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
  padding: 15px;
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
  margin: 6% 0 0 3%;
  font-size: 16px;
  color: #171d2c;
  letter-spacing: 0;
  line-height: 18px;
  text-align: center;
  width: 47%;
}
.welcome2 {
  float: right;
  margin: 6% 3% 0 0;
  font-size: 16px;
  color: #171d2c;
  letter-spacing: 0;
  line-height: 18px;
  text-align: center;
  width: 47%;
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
  color: #193f67;
  font-size: 48px;
  padding: 45px 35px 0 120px;
}
.main-container >>> .lb5-txt-white {
  color: #215d9a;
  font-size: 18px;
  padding: 5px 35px 0 120px;
}
.main-container >>> .lb-row-txt-white {
  padding: 15px 35px 0 120px;
}
.title-img {
  width: 70%;
  height: 70%;
  min-height: 390px;
  text-align: center;
  margin: 15% 0 10% 15%;
}
.title-img2 {
  width: 70%;
  height: 70%;
  min-height: 390px;
  text-align: center;
  margin: 15% 0 10% 15%;
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
  margin-top: 5%;
}
.cloud-desc-div1 {
  margin: 2% 0 0 0;
  color: #215d9a;
  text-align: center;
}
.cloud-desc-div2 {
  margin: 2% 0 0 0;
  color: #215d9a;
  text-align: center;
}
.cloud-desc-div3 {
  margin: 2% 0 0 0;
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
.el-row2 {
  background-color: #fafafa;
}
</style>

