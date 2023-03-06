<template>
  <div class="md-card-overall">
      <el-row :gutter="24" class="el-row-mr" @click="goResource('count', 'all')">
          <el-button class="md-button-dashboard bg-purple-dark">
            <I class="md-primary">
              <el-col :span="8" class="el-col-mr">{{ $t('dashboard.i18n_non_compliance_number') }} / {{ $t('dashboard.i18n_total') }}</el-col>
            &nbsp;&nbsp;<el-col :span="8" class="el-col-mr">{{ $t('dashboard.i18n_non_compliance_proportion') }}</el-col>
              <el-col :span="8"></el-col>
            </I>
          </el-button>
      </el-row>
      <el-row :gutter="24" class="el-row-mr" @click="goResource('count', 'all')">
        <span class="md-headline" style="font-size: 20px;" layout="row" layout-align="start center">
          <I class="md-primary">
            <el-col :span="8" class="el-col-mr">{{ returnTotal }} &nbsp;/ {{ resourceTotal }}</el-col>&nbsp;&nbsp;
            <el-col :span="8" class="el-col-mr">
              <span style="color: #0000cc"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{{ totalPecent?totalPecent.toFixed(2):0 }}% </span>
            </el-col>
            <el-col :span="8"></el-col>
          </I>
        </span>
      </el-row>
      <el-progress :percentage="totalPecent" :color="customColor" :format="format"></el-progress>
      <el-row :gutter="24" class="el-row-mr">
        <el-col :span="10">
          <dashboard-pie-chart></dashboard-pie-chart>
        </el-col>
        <el-col :span="14">
          <rule-group-pie-chart></rule-group-pie-chart>
        </el-col>
      </el-row>
  </div>
</template>
<script>
import DashboardPieChart from "@/business/components/common/chart/DashboardPieChart";
import RuleGroupPieChart from "@/business/components/common/chart/RuleGroupPieChart";
import {dashboardTotalPolicyUrl} from "@/api/cloud/dashboard/dashboard";
/* eslint-disable */
  export default {
    name: "MdCardContentOverall",
    components: {
      RuleGroupPieChart,
      DashboardPieChart,
    },
    data() {
      return {
        totalPolicy: [],
        totalPecent: 0,
        returnTotal: "",
        resourceTotal: "",
        customColor: '#409eff',
      }
    },
    methods: {
      init() {
        this.$post(dashboardTotalPolicyUrl, {}, response => {
          this.totalPolicy = response.data;
          for (let item of this.totalPolicy) {
            this.totalPolicy[item.xAxis] = item;
          }
          this.returnTotal = this.totalPolicy[0].returnTotal;
          this.resourceTotal = this.totalPolicy[0].resourceTotal;
          let p = this.totalPolicy[0].returnTotal?this.totalPolicy[0].returnTotal:0;
          let g = this.totalPolicy[0].resourceTotal?this.totalPolicy[0].resourceTotal:0;
          if(g!=0) this.totalPecent = (p / g).toFixed(4) * 100;
        });
      },
      goResource() {

      },
      format (percentage) {
        // 不显示百分比
        return "";
      },
    },
    created() {
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
    background-color: #d1d7db;
    padding: 0;
    height: 35px;
  }
  .bg-purple {
    background: #d3dce6;
  }
  .bg-purple-light {
    background: #e5e9f2;
  }
  .grid-content {
    border-radius: 4px;
    min-height: 24px;
  }
  .row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
  }
  .md-button-dashboard {
    margin: 9px 2px;
    width: 100%;
    text-align: left;
    vertical-align: auto;
  }

  .md-card-dashboard-div1 {
    width: 46%;
    height: 126px;
    float: right;
    border: solid 1px #F4F4F4;
    margin: 1% 1.5%;
    cursor: pointer;
  }

  .md-card-dashboard-div2 {
    width: 46%;
    height: 126px;
    float: right;
    border: solid 1px #F4F4F4;
    margin: 1% 2.5% 1% 1%;
    cursor: pointer;
  }

  .md-card-dashboard-div3 {
    width: 46%;
    height: 126px;
    float: right;
    border: solid 1px #F4F4F4;
    margin: 1% 2.5% 1% 1%;
  }
  .md-primary {
    color: #1c4a6e;
    margin-left: 5px;
  }
  .el-row-mr {
    margin: 0;
  }
  .el-col-mr {
    margin-left: 5%;
  }
  .md-card-overall {
    width: 100%;
    min-height: 340px;
  }
</style>
