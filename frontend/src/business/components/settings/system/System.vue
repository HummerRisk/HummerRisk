<template>
  <div v-loading="result.loading">
    <el-card class="table-card">

      <el-row style="margin-top: 3%;">
        <el-col :span="6">
          <el-button icon="el-icon-refresh" size="small" plain @click="updateSystem">{{ $t('system.update') }}</el-button>
        </el-col>
        <el-col :span="6" style="float: right;">
          <el-input
          class="search"
          type="text"
          size="small"
          :placeholder="$t('commons.search_by_name')"
          prefix-icon="el-icon-search"
          maxlength="60"
          v-model="searchValue" clearable @change="search(searchValue)"/>
        </el-col>
      </el-row>

      <el-divider><i class="el-icon-cpu"></i></el-divider>

      <el-descriptions class="margin-top" :title="$t('system.system')" :column="1" border>
        <el-descriptions-item :key="index" v-for="(data, index) in datas" :label="data.paramKey">{{ data.paramValue }}</el-descriptions-item>
      </el-descriptions>

    </el-card>
  </div>
</template>

<script>

export default {
  name: "System",
  components: {
  },
  data() {
    return {
      result: {},
      loading: false,
      datas: [],
      searchValue: '',
    }
  },
  mounted() {
    this.search(null);
  },
  created() {
  },
  methods: {
    search(value) {
      this.$get('/system/searchSystem', response => {
        this.datas = response.data;
        if(value) {
          let searchForValues = [];
          this.datas.forEach(data => {
            if(data.paramValue.indexOf(value) > -1) {
              searchForValues.push(data);
            }
          });
          this.datas = searchForValues;
        }
      });
    },
    updateSystem() {
      this.$get('/system/updateSystem', response => {
        this.$success(this.$t('organization.integration.successful_operation'));
        this.search();
      });
    }
  }

}
</script>

<style scoped>
.rtl >>> .el-drawer__body {
  overflow-y: auto;
  padding: 20px;
}
.rtl >>> input {
  width: 100%;
}
.rtl >>> .el-select {
  width: 100%;
}
.rtl >>> .el-form-item__content {
  width: 80%;
}
/deep/ :focus{outline:0;}

</style>
