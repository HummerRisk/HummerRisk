<template>
  <div v-loading="result.loading">
    <container class="container" >
      <el-col :span="24">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.image_statistics') }}</span>
          </template>
          <image-chart/>
        </el-card>
      </el-col>
    </container>
    <container class="container" >
      <el-col :span="12">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.config_statistics') }}</span>
          </template>
          <config-chart/>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="table-card">
          <template v-slot:header>
            <span class="title">{{ $t('dashboard.fs_statistics') }}</span>
          </template>
          <fs-chart/>
        </el-card>
      </el-col>
    </container>
    <container class="container">
      <el-col :span="24">
        <el-card shadow="always">
          <el-col :span="24">
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
              <el-table-column prop="cloud" :label="$t('commons.cloud_scan')" min-width="100" v-slot:default="scope">
                <span v-if="scope.row.status === 'APPROVED'" class="scan-span">{{ scope.row.cloud }}</span>
                <span v-else class="scan-span-ing">{{ scope.row.cloud }}</span>
              </el-table-column>
              <el-table-column prop="server" :label="$t('dashboard.server_scan')" min-width="100" v-slot:default="scope">
                <span v-if="scope.row.status === 'APPROVED'" class="scan-span">{{ scope.row.server }}</span>
                <span v-else class="scan-span-ing">{{ scope.row.server }}</span>
              </el-table-column>
              <el-table-column prop="k8s" :label="$t('commons.k8s_scan')" min-width="100" v-slot:default="scope">
                <span v-if="scope.row.status === 'APPROVED'" class="scan-span">{{ scope.row.k8s }}</span>
                <span v-else class="scan-span-ing">{{ scope.row.k8s }}</span>
              </el-table-column>
              <el-table-column prop="config" :label="$t('dashboard.config_scan')" min-width="100" v-slot:default="scope">
                <span v-if="scope.row.status === 'APPROVED'" class="scan-span">{{ scope.row.config }}</span>
                <span v-else class="scan-span-ing">{{ scope.row.config }}</span>
              </el-table-column>
              <el-table-column prop="image" :label="$t('dashboard.image_scan')" min-width="100" v-slot:default="scope">
                <span v-if="scope.row.status === 'APPROVED'" class="scan-span">{{ scope.row.image }}</span>
                <span v-else class="scan-span-ing">{{ scope.row.image }}</span>
              </el-table-column>
              <el-table-column prop="code" :label="$t('dashboard.code_scan')" min-width="100" v-slot:default="scope">
                <span v-if="scope.row.status === 'APPROVED'" class="scan-span">{{ scope.row.code }}</span>
                <span v-else class="scan-span-ing">{{ scope.row.code }}</span>
              </el-table-column>
              <el-table-column prop="fs" :label="$t('dashboard.fs_scan')" min-width="100" v-slot:default="scope" fixed="right">
                <span v-if="scope.row.status === 'APPROVED'" class="scan-span">{{ scope.row.fs }}</span>
                <span v-else class="scan-span-ing">{{ scope.row.fs }}</span>
              </el-table-column>
            </el-table>
          </el-col>
        </el-card>
      </el-col>
    </container>
  </div>
</template>

<script>
import ImageChart from "@/business/components/dashboard/head/ImageChart";
import FsChart from "@/business/components/fs/head/LeftChart";
import Container from "@/business/components/common/components/Container";
import ConfigChart from "@/business/components/config/head/LeftChart";
import {topScanInfoUrl} from "@/api/cloud/dashboard/dashboard";

/* eslint-disable */
export default {
  components: {
    ImageChart,
    ConfigChart,
    Container,
    FsChart,
  },
  data() {
    return {
      result: {},
      tableData: [],
    }
  },
  methods: {
    init() {
      this.result = this.$get(topScanInfoUrl, response => {
        let data = response.data;
        this.tableData = data;
      });
    },
  },
  created() {
    this.init();
  }
}
</script>

<style scoped>
.table-card {
  min-height: 10%;
}

.container {
  padding: 3px 15px;
}
</style>

