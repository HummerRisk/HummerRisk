<template>
  <el-card class="box-card" shadow="always">
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="grid-content bg-purple dashboard-head-title">
          <span style="color: rgb(244, 67, 54);vertical-align: middle;">{{ $t('rule.HighRisk') }}</span>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="grid-content bg-purple dashboard-head-title">
          <span style="color: rgb(255, 152, 0);">{{ $t('rule.MediumRisk') }}</span>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="grid-content bg-purple dashboard-head-title">
          <span style="color: rgb(33, 150, 243);">{{ $t('rule.LowRisk') }}</span>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="grid-content bg-purple dashboard-head-title">
          <span style="color: rgb(76, 175, 80);">{{ $t('rule.Normal') }}</span>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="grid-content bg-purple">
          <div class="dashboard-head-value">
            <el-link class="md-primary"
                     @click="goResource('severity', 'HighRisk')">{{ !!severityList.HighRisk?severityList.HighRisk.returnSum:0 }}</el-link>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="grid-content bg-purple">
          <div class="dashboard-head-value">
            <el-link class="md-primary"
                     @click="goResource('severity', 'MediumRisk')">{{ !!severityList.MediumRisk?severityList.MediumRisk.returnSum:0 }}</el-link>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="grid-content bg-purple">
          <div class="dashboard-head-value">
          <el-link class="md-primary"
                   @click="goResource('severity', 'LowRisk')">{{ !!severityList.LowRisk?severityList.LowRisk.returnSum:0 }}</el-link>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="grid-content bg-purple">
          <div class="dashboard-head-value">
            <el-link class="md-primary"
                     @click="goResource('severity', 'Normal')">{{ !!severityList.Normal?severityList.Normal.returnSum:0 }}</el-link>
          </div>
        </div>
      </el-col>
    </el-row>
  </el-card>
</template>
<script>
/* eslint-disable */
  export default {
    name: "MdCardContentSeverity",
    components: {
    },
    data() {
      return {
        severityList: [],
      }
    },
    methods: {
      goResource () {
      },
      async init() {
        await this.$post('/dashboard/point/severity', {}, response => {
          for (let item of response.data) {
            this.severityList[item.severity] = item;
          }
        });
      },
    },
    mounted() {
      this.init();
    },
  }

</script>

<style scoped>
  .box-card {
    width: 100%;
  }
  .el-row {
    margin-bottom: 20px;
  }
  .el-col {
    border-radius: 4px;
  }
  .bg-purple-dark {
    background: #99a9bf;
  }
  .bg-purple {
    background: #ffffff;
  }
  .bg-purple-light {
    background: #e5e9f2;
  }
  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  }
  .row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
  }
  .dashboard-head-title {
    text-align: center;
    font-size: initial;
    vertical-align:middle;
    margin-top: 8px;
  }
  .dashboard-head-value {
    text-align: center;
    font-weight: 500;
    font-size: larger;
    vertical-align:middle;
  }
  .md-primary {
    font-size: larger;
    color: rgb(0,33,51);
    text-decoration:underline;
  }
</style>
