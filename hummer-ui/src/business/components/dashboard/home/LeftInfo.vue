<template>
  <container v-loading="result.loading" class="container">
    <el-col :span="10" style="padding: 0 10px 0 0;">
      <el-row>
        <el-card class="table-card" v-loading="result.loading">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.cloud_account_statistics') }}</span>
          </template>
          <cloud-chart ref="cloudChart" :width="cloudChart"/>
        </el-card>
      </el-row>
      <el-row>
        <el-card class="table-card" v-loading="result.loading">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.code_statistics') }}</span>
          </template>
          <code-chart ref="codeChart" :width="codeChart"/>
        </el-card>
      </el-row>
      <el-row>
        <el-card class="table-card" v-loading="result.loading">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.server_statistics') }}</span>
          </template>
          <server-chart/>
        </el-card>
      </el-row>
    </el-col>
    <el-col :span="14" style="padding: 0;">
      <el-card class="table-card" v-loading="result.loading">
        <el-calendar>
          <!-- 这里使用的是 2.5 slot 语法，对于新项目请使用 2.6 slot 语法-->
          <template
            slot="dateCell"
            slot-scope="{date, data}">
            <p v-if="(taskCalendar.indexOf(data.day) === -1) && !data.isSelected" :class="''">
              {{ data.day.split('-').slice(1).join('-') }}
            </p>
            <p v-if="(taskCalendar.indexOf(data.day) > -1) || data.isSelected" :class="'is-selected'" @click="goTask()">
              {{ data.day.split('-').slice(1).join('-') }} {{ $t('dashboard.data_task') }}
            </p>
          </template>
        </el-calendar>
      </el-card>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <span class="title">{{ $t('dashboard.k8s_statistics') }}</span>
        </template>
        <cloud-native-chart/>
      </el-card>
    </el-col>
  </container>
</template>

<script>
import Container from "../.././common/components/Container";
import CloudChart from "@/business/components/dashboard/head/CloudChart";
import CodeChart from "@/business/components/dashboard/head/CodeChart.vue";
import ServerChart from "@/business/components/dashboard/head/ServerChart";
import CloudNativeChart from "@/business/components/dashboard/head/CloudNativeChart";
import {taskCalendarUrl} from "@/api/cloud/dashboard/dashboard";

/* eslint-disable */
export default {
  components: {
    Container,
    CloudChart,
    CodeChart,
    ServerChart,
    CloudNativeChart,
  },
  data() {
    return {
      result: {},
      cloudChart: 390,
      codeChart: 400,
      taskCalendar: [],
    }
  },
  methods: {
    /** 查询节假日管理 - 法定节假日列表 */
    getList() {
      this.result = this.$get(taskCalendarUrl, response => {
        let data = response.data;
        for (let obj of data) {
          this.taskCalendar.push(obj.day);
        }
      });
    },
    goTask() {
      this.$alert(this.$t('dashboard.comfirm_task'), '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$router.push({
              path: '/task/list',
            }).catch(error => error);
          }
        }
      });
    },
  },
  created() {
    this.getList();
    this.$nextTick(() => {
      this.cloudChart = this.$refs.cloudChart.$el.offsetWidth;
      this.vulnChart = this.$refs.vulnChart.$el.offsetWidth;
    });
  }
}
</script>

<style scoped>
.table-card {
  margin-bottom: 2%;
  min-height: 10%;
}
.is-selected {
  color: #1989FA;
}

.container {
  padding: 3px 5px 3px 15px;
}

.is-selected {
  color: white;
  background: green;
}
</style>

