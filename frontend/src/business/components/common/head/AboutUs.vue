<template>
  <el-dialog
    :title="$t('commons.about_us')"
    :visible.sync="dialogVisible" class="about-us">

    <el-row>
      <el-col class="version">
        <el-link :underline="false" :href="websiteUrl" target="_blank">
          <img class="logo github-icon" src="../../../../assets/img/logo/favicon-彩色.png"/>
        </el-link>
        <el-link class="url" :href="websiteUrl" target="_blank">
          <span>{{ websiteUrl }}</span>
        </el-link>
      </el-col>
    </el-row>

    <el-row>
      <el-col class="version">
        <el-link :underline="false" :href="githubUrl" target="_blank">
          <font-awesome-icon class="github-icon" :icon="['fab', 'github-square']"/>
        </el-link>
        <el-link class="url" :href="githubUrl" target="_blank">
          <span>{{ githubUrl }}</span>
        </el-link>
      </el-col>
    </el-row>

    <el-row>
      <el-col class="version">
        <el-link :underline="false" href="https://docs.hummerrisk.com/question/rule" target="_blank">
          <img class="logo github-icon" src="../../../../assets/img/engine/custodian.png"/>
        </el-link>
        <el-link class="url" href="https://docs.hummerrisk.com/question/rule" target="_blank">
          <span>{{ 'https://docs.hummerrisk.com/question/rule' }}</span>
        </el-link>
      </el-col>
    </el-row>

    <el-row>
      <el-col class="version">
        <el-link :underline="false" href="https://docs.hummerrisk.com/question/prowler" target="_blank">
          <img class="logo github-icon" src="../../../../assets/img/engine/prowler.png"/>
        </el-link>
        <el-link class="url" href="https://docs.hummerrisk.com/question/prowler" target="_blank">
          <span>{{ 'https://docs.hummerrisk.com/question/prowler' }}</span>
        </el-link>
      </el-col>
    </el-row>

    <el-row>
      <el-col class="version">
        <el-link :underline="false" href="https://docs.hummerrisk.com/question/nuclei" target="_blank">
          <img class="logo github-icon" src="../../../../assets/img/engine/nuclei.png"/>
        </el-link>
        <el-link class="url" href="https://docs.hummerrisk.com/question/nuclei" target="_blank">
          <span>{{ 'https://docs.hummerrisk.com/question/nuclei' }}</span>
        </el-link>
      </el-col>
    </el-row>


    <el-row>
      <el-col class="version">
        <el-link :underline="false" href="https://docs.hummerrisk.com/question/xray" target="_blank">
          <img class="logo github-icon" src="../../../../assets/img/engine/xray.png"/>
        </el-link>
        <el-link class="url" href="https://docs.hummerrisk.com/question/xray" target="_blank">
          <span>{{ 'https://docs.hummerrisk.com/question/xray' }}</span>
        </el-link>
      </el-col>
    </el-row>


    <el-row>
      <el-col class="version">
        <el-link :underline="false" href="https://github.com/aquasecurity/trivy" target="_blank">
          <img class="logo github-icon" src="../../../../assets/img/engine/trivy.png"/>
        </el-link>
        <el-link class="url" href="https://docs.hummerrisk.com/question/trivy" target="_blank">
          <span>{{ 'https://docs.hummerrisk.com/question/trivy' }}</span>
        </el-link>
      </el-col>
    </el-row>

    <el-row>
      <el-col class="version">
        <span><font-awesome-icon class="github-icon" :icon="['fas', 'code-branch']"/> {{ $t('commons.version') }}:</span> &nbsp;
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
        this.$get('/system/version', response => {
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
    vertical-align: middle
  }

  .version {
    height: 30px;
    line-height: 30px;
    margin-left: 5px;
  }

  .github-icon {
    font-size: 20px;
    margin-left: 5px;
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

</style>
