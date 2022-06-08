<template>
  <el-card class="box-card" shadow="always">
    <el-row>
      <el-col :span="8">
        <el-card :body-style="{ padding: '0px' }" style="text-align: center">
          <el-button class="md-button-dashboard bg-purple-dark" @click="goResource('count', 'all')">
            <I class="md-primary">
              {{ $t('dashboard.i18n_cost') }}
              <i class="el-icon-right"></i>
            </I>
          </el-button>
          <div style="padding: 14px;" class="md-card-content-dashboard-i">
            <i style="color: #f6ebbc;font-size: 48px;" class="el-icon-money"></i>
          </div>
          <div style="padding: 14px;" class="md-card-content-dashboard-t">
            <I><u style="color: red;font-size: 30px;">{{ costYAxis }}</u></I>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card :body-style="{ padding: '0px' }" style="text-align: center">
          <el-button class="md-button-dashboard bg-purple-dark" @click="goResource('count', 'all')">
            <I class="md-primary">
              {{ $t('dashboard.i18n_security') }}
              <i class="el-icon-right"></i>
            </I>
          </el-button>
          <div style="padding: 14px;" class="md-card-content-dashboard-i">
            <i style="color: #11cfae;font-size: 48px;" class="el-icon-bangzhu"></i>
          </div>
          <div style="padding: 14px;" class="md-card-content-dashboard-t">
            <I><u style="color: red;font-size: 30px;">{{ safetyYAxis }}</u></I>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="8">
        <el-card :body-style="{ padding: '0px' }" style="text-align: center">
          <el-button class="md-button-dashboard bg-purple-dark" @click="goResource('count', 'all')">
            <I class="md-primary">
              {{ $t('dashboard.i18n_tagging') }}
              <i class="el-icon-right"></i>
            </I>
          </el-button>
          <div style="padding: 14px;" class="md-card-content-dashboard-i">
            <i style="color: #627dec;font-size: 48px;" class="el-icon-price-tag"></i>
          </div>
          <div style="padding: 14px;" class="md-card-content-dashboard-t">
            <I><u style="color: red;font-size: 30px;">{{ taggingYAxis }}</u></I>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card :body-style="{ padding: '0px' }" style="text-align: center">
          <el-button class="md-button-dashboard bg-purple-dark" @click="goResource('count', 'all')">
            <I class="md-primary">
              {{ $t('dashboard.i18n_other') }}
              <i class="el-icon-right"></i>
            </I>
          </el-button>
          <div style="padding: 14px;" class="md-card-content-dashboard-i">
            <i style="color: #0051a4;font-size: 48px;" class="el-icon-data-board"></i>
          </div>
          <div style="padding: 14px;" class="md-card-content-dashboard-t">
            <I><u style="color: red;font-size: 30px;">{{ otherYAxis }}</u></I>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </el-card>
</template>
<script>
/* eslint-disable */
  export default {
    name: "MdCardContentOverall",
    components: {
    },
    data() {
      return {
        totalPolicy: [],
        totalPecent: 0,
        costYAxis: 0,
        safetyYAxis: 0,
        taggingYAxis: 0,
        otherYAxis: 0,
      }
    },
    methods: {
      init() {
        this.$post('/dashboard/totalPolicy', {}, response => {
          this.totalPolicy = response.data;
          for (let item of this.totalPolicy) {
            this.totalPolicy[item.xAxis] = item;
          }
          this.costYAxis = this.totalPolicy.cost.yAxis;
          this.safetyYAxis = this.totalPolicy.safety.yAxis;
          this.taggingYAxis = this.totalPolicy.tagging.yAxis;
          this.otherYAxis = this.totalPolicy.other.yAxis;
          this.totalPecent = (this.totalPolicy[0].returnTotal / this.totalPolicy[0].resourceTotal) * 100;
        });
      },
      goResource() {},
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
  .md-card-content-dashboard-i {
    text-align: center;
    width: 50%;
    float: left;
    padding: 0;
    margin: 3% 0;
  }

  .md-card-content-dashboard-t {
    text-align: left;
    width: 50%;
    float: left;
    padding: 0;
    margin: 6% 0 15% 0;
  }

</style>
