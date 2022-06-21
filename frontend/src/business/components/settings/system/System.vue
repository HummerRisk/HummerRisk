<template>
  <div v-loading="result.loading">
    <el-card class="table-card">

      <el-button icon="el-icon-video-play" type="primary" @click="updateSystem"></el-button>

      <el-descriptions :title="$t('system.system')" direction="vertical" :column="2" border>
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
    }
  },
  mounted() {
    this.search()
  },
  created() {
  },
  methods: {
    search() {
      this.$get('/system/searchSystem', response => {
        console.log(response)
        this.datas = response.data;
      });
    },
    updateSystem() {
      this.$get('/system/updateSystem', response => {
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
