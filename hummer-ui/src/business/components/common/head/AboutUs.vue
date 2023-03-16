<template>
  <el-dialog
    :title="$t('commons.about_us')"
    :visible.sync="dialogVisible" class="about-us">

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

    <el-row>
      <el-col class="version">
        <span><font-awesome-icon :icon="['fas', 'code-branch']"/> {{ $t('commons.version') }}:</span> &nbsp;
        <span>{{ version }}</span>
      </el-col>
    </el-row>

  </el-dialog>

</template>

<script>
/* eslint-disable */
  export default {
    name: "AboutUs",
    data() {
      return {
        dialogVisible: false,
        githubUrl: 'https://github.com/HummerRisk/HummerRisk',
        websiteUrl: 'https://docs.hummerrisk.com',
        version: 'v1.0.0',
        items: [
          {img: require(`@/assets/img/about/docs.png`), url: 'https://docs.hummerrisk.com'},
          {img: require(`@/assets/img/about/github.png`), url: 'https://github.com/HummerRisk/HummerRisk'},
          {img: require(`@/assets/img/about/custodian.png`), url: 'https://docs.hummerrisk.com/related/opensource-tool/custodian/'},
          {img: require(`@/assets/img/about/prowler.png`), url: 'https://docs.hummerrisk.com/related/opensource-tool/prowler/'},
          {img: require(`@/assets/img/about/trivy.png`), url: 'https://docs.hummerrisk.com/related/opensource-tool/trivy/'},
          {img: require(`@/assets/img/about/kubench.png`), url: 'https://docs.hummerrisk.com/related/opensource-tool/kube-bench/'},
        ],
      }
    },
    created() {
      this.getVersion();
    },
    methods: {
      open() {
        this.dialogVisible = true;
      },
      getVersion() {
        this.$get('/system/system/version', response => {
          this.version = response.data;
        });
      }
    }
  }
</script>

<style scoped>

  .logo {
    height: 20px;
    line-height: 30px;
    vertical-align: middle;
  }

  .version {
    height: 30px;
    line-height: 30px;
    margin: 15px 5px;
    color: #215d9a;
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

</style>
