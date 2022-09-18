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
          {{$t('commons.welcome')}}
        </div>
        <div class="title">
          <img src="@/assets/img/panel/cloud.png" class="title-img" alt="">
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
  width: 50%;
}

.welcome {
  float: left;
  margin-top: 50px;
  font-size: 14px;
  color: #999999;
  letter-spacing: 0;
  line-height: 18px;
  text-align: center;
  width: 50%;
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
  margin: 5% 0 5% 5%;
}
</style>

