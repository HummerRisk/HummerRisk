<template>
  <div v-loading="result.loading">
    <container class="container">
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
    <container class="container">
      <el-col :span="24">
        <el-card shadow="always">
          <el-col :span="4" class="co-el-img">
            <el-col :span="21" class="co-el-img co-el-img-div">
              <el-image
                        :src="require(`@/assets/img/panel/img.png`)"
                        :fit="'fill'">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <div class="el-total">{{ $t('resource.i18n_not_compliance') }}</div>
            </el-col>
            <el-col :span="1">
              <div class="split"></div>
            </el-col>
          </el-col>
          <el-col :span="20">
            <el-table
              :data="tableData"
              style="width: 100%">
              <el-table-column prop="status" :label="$t('resource.status')" min-width="100" v-slot:default="scope">
                <el-button plain size="mini" type="primary" class="el-btn" v-if="scope.row.status === 'APPROVED'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </el-button>
                <el-button plain size="mini" type="success" class="el-btn" v-if="scope.row.status === 'FINISHED'">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </el-button>
                <el-button plain size="mini" type="danger" class="el-btn" v-if="scope.row.status === 'ERROR'">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </el-button>
                <el-button plain size="mini" type="warning" class="el-btn" v-if="scope.row.status === 'WARNING'">
                  <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                </el-button>
              </el-table-column>
              <el-table-column prop="cloud" :label="$t('commons.cloud_scan')" min-width="100">
              </el-table-column>
              <el-table-column prop="vuln" :label="$t('dashboard.vuln_scan')" min-width="100">
              </el-table-column>
              <el-table-column prop="server" :label="$t('dashboard.server_scan')" min-width="100">
              </el-table-column>
              <el-table-column prop="k8s" :label="$t('commons.k8s_scan')" min-width="100">
              </el-table-column>
              <el-table-column prop="config" :label="$t('dashboard.config_scan')" min-width="100">
              </el-table-column>
              <el-table-column prop="image" :label="$t('dashboard.image_scan')" min-width="100">
              </el-table-column>
              <el-table-column prop="code" :label="$t('dashboard.code_scan')" min-width="100">
              </el-table-column>
              <el-table-column prop="fs" :label="$t('dashboard.fs_scan')" min-width="100">
              </el-table-column>
            </el-table>
          </el-col>
        </el-card>
      </el-col>
    </container>
  </div>
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
      tableData: [],
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
  width: 160px;
}

.el-total {
  font-size: 12px;
  margin: 10px;
  text-align: center;
}
</style>

