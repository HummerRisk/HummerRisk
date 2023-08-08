<template>
  <el-card class="cloud-card" v-loading="result.loading">
    <div class="text item">
    <el-col :span="24">
      <el-col :span="24">
          <el-table
            :data="tableData"
            style="width: 100%">
            <el-table-column prop="status" :label="$t('resource.status')" min-width="150" v-slot:default="scope">
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
            <el-table-column prop="cloud" :label="$t('commons.cloud_scan')" min-width="150" v-slot:default="scope">
              <span v-if="scope.row.status === 'APPROVED'" class="scan-span">{{ scope.row.cloud }}</span>
              <span v-else class="scan-span-ing">{{ scope.row.cloud }}</span>
            </el-table-column>
          </el-table>
        </el-col>
    </el-col>
    </div>
  </el-card>
</template>

<script>
import {topScanInfoUrl} from "@/api/cloud/dashboard/dashboard";

/* eslint-disable */
export default {
  components: {
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

.cloud-card {
  margin: 2%;
  padding: 2%;
  min-height: 278px;
}
</style>

