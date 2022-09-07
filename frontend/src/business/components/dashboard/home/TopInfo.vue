<template>
  <container class="container" v-loading="result.loading">
    <el-col :span="4">
      <el-card shadow="always" class="hr-card-index-1">
        <span class="hr-card-data">
          <span class="hr-card-data-digital">{{ topInfo.users }}</span>
          <span class="hr-card-data-unit"> {{ 'Users' }}</span>
        </span>
        <span class="hr-card-desc">{{ $t('dashboard.users') }}</span>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="always" class="hr-card-index-2">
        <span class="hr-card-data">
          <span class="hr-card-data-digital">{{ topInfo.accounts }}</span>
          <span class="hr-card-data-unit"> {{ 'Accounts' }}</span>
        </span>
        <span class="hr-card-desc">{{ $t('dashboard.accounts') }}</span>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="always" class="hr-card-index-3">
        <span class="hr-card-data">
          <span class="hr-card-data-digital">{{ topInfo.rules }}</span>
          <span class="hr-card-data-unit"> {{ 'Rules' }}</span>
        </span>
        <span class="hr-card-desc">{{ $t('dashboard.rules') }}</span>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="always" class="hr-card-index-4">
        <span class="hr-card-data">
          <span class="hr-card-data-digital">{{ topInfo.results }}</span>
          <span class="hr-card-data-unit"> {{ 'Results' }}</span>
        </span>
        <span class="hr-card-desc">{{ $t('dashboard.results') }}</span>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="always" class="hr-card-index-5">
        <span class="hr-card-data">
          <span class="hr-card-data-digital">{{ topInfo.tasks }}</span>
          <span class="hr-card-data-unit"> {{ 'Tasks' }}</span>
        </span>
        <span class="hr-card-desc">{{ $t('dashboard.tasks') }}</span>
      </el-card>
    </el-col>
    <el-col :span="4">
      <el-card shadow="always" class="hr-card-index-6">
        <span class="hr-card-data">
          <current-user/>
        </span>
        <span class="hr-card-desc">{{ currentUser.email }}</span>
      </el-card>
    </el-col>
  </container>
</template>

<script>
import Container from "../.././common/components/Container";
import CurrentUser from "@/business/components/settings/CurrentUser";
import {getCurrentUser} from "@/common/js/utils";

/* eslint-disable */
export default {
  components: {
    Container,
    CurrentUser,
  },
  data() {
    return {
      result: {},
      currentUser: {},
      topInfo: {},
    }
  },
  methods: {
    init() {
      this.result = this.$post("/dashboard/topInfo", {}, response => {
        let data = response.data;
        this.topInfo = data;
      });
    },
  },
  created() {
    this.currentUser = getCurrentUser();
    this.init();
  }
}
</script>

<style scoped>
.table-card {
  min-height: 10%;
}

.hr-card-index-1 .hr-card-data-digital {
  color: #0051a4;
}

.hr-card-index-1 {
  border-left-color: #0051a4;
  border-left-width: 3px;
}

.hr-card-index-2 .hr-card-data-digital {
  color: #65A2FF;
}

.hr-card-index-2 {
  border-left-color: #65A2FF;
  border-left-width: 3px;
}

.hr-card-index-3 .hr-card-data-digital {
  color: #E69147;
}

.hr-card-index-3 {
  border-left-color: #E69147;
  border-left-width: 3px;
}

.hr-card-index-4 .hr-card-data-digital {
  color: #E6113C;
}

.hr-card-index-4 {
  border-left-color: #E6113C;
  border-left-width: 3px;
}

.hr-card-index-5 .hr-card-data-digital {
  color: #44B349;
}

.hr-card-index-5 {
  border-left-color: #44B349;
  border-left-width: 3px;
}

.hr-card-index-6 .hr-card-data-digital {
  color: #893fdc;
}

.hr-card-index-6 {
  border-left-color: #893fdc;
  border-left-width: 3px;
}

.hr-card-data {
  text-align: left;
  display: block;
  margin-bottom: 5px;
}

.hr-card-desc {
  display: block;
  text-align: left;
}

.hr-card-data-digital {
  font-size: 21px;
}

.hr-card-data-unit {
  color: #8492a6;
  font-size: 10px;
}

.container {
  padding: 3px 15px;
}
</style>

