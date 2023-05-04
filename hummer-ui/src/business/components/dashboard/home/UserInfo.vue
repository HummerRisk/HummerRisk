<template>
  <container v-loading="result.loading" class="container">
    <el-col :span="24" style="padding: 0;">
      <el-card class="table-card" v-loading="result.loading">
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
    },
  },
  created() {
    this.currentUser = getCurrentUser();
    this.init();
  }
}
</script>

<style scoped>
.container {
  padding: 3px 15px 3px 5px;
}

.table-card {
  min-height: 10%;
}

.table-card >>> .el-card__body {
  padding-top: 10px;
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
  margin: 5px 0;
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

.split {
  height: 80px;
  border-left: 1px solid #D8DBE1;
}

.co-el-img {
  padding: 0 !important;
}

.co-el-img-div {
  margin-top: 3%;
}

.co-el-img >>> .el-image {
  display: table-cell;
  padding: 0;
}

.cp-el-i {
  margin: 1%;
}

.co-el-i{
  width: 70px;
  height: 70px;
}

.el-btn {
  min-width: 100px;
}

.el-total {
  font-size: 12px;
  margin: 10px;
  text-align: center;
}

.scan-span {
  font-style:italic;
  color: #72abf9;
}

.scan-span-ing {
  font-style:italic;
}
</style>

