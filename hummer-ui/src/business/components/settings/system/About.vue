<template>
  <div v-loading="result.loading">
    <el-card class="about-card">

      <el-row>
        <img :src="require(`@/assets/img/logo/logo-dark.png`)" class="image"/>
      </el-row>

      <div style="padding: 14px;text-align: center;">
        <el-row>
          <el-col :span="12" class="col-na">
            <span>{{ $t('system.authorized_to') }}</span>
          </el-col>
          <el-col :span="10" class="col-te">
            <span>{{ $t('HummerCloud') }}</span>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12" class="col-na">
            <span>{{ $t('system.expiration') }}</span>
          </el-col>
          <el-col :span="10" class="col-te">
            <span>{{ $t('2035-12-30') }}</span>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12" class="col-na">
            <span>{{ $t('system.version') }}</span>
          </el-col>
          <el-col :span="10" class="col-te">
            <span>{{ $t('企业版') }}</span>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12" class="col-na">
            <span>{{ $t('system.version_number') }}</span>
          </el-col>
          <el-col :span="10" class="col-te">
            <span>{{ $t('v1.1.0') }}</span>
          </el-col>
        </el-row>
      </div>

      <div style="padding: 14px;">
        <el-row>
          <el-col :span="24" class="license-le">
            <el-button plain size="medium" type="primary">{{ $t('system.update_license') }}</el-button>
            <el-button plain size="medium">{{ $t('dashboard.i18n_other') }}</el-button>
          </el-col>
        </el-row>
      </div>

      <el-drawer class="rtl" :title="$t('user.create')" :visible.sync="dialogVisible" size="60%" :before-close="handleClose" :direction="direction"
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
/* eslint-disable */
  export default {
    name: "AboutUs",
    data() {
      return {
        result: false,
        dialogVisible: false,
        direction: 'rtl',
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
    },
    methods: {
      handleClose() {
        this.dialogVisible = false;
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

</style>
