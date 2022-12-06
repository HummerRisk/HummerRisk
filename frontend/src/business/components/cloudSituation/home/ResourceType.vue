<template>
  <div>
    <el-button slot="reference" size="mini" type="primary" plain @click="showRegions">
      {{ $t('rule.resource_type') }}
    </el-button>
    <!--regions-->
    <el-drawer class="rtl" :title="$t('rule.resource_type')" :visible.sync="regionsVisible" size="45%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-table :border="true" :stripe="true" :data="string2PrettyFormat" class="adjust-table table-content">
        <el-table-column type="index" min-width="10%"/>
        <el-table-column prop="resourceType" :label="$t('rule.resource_type')" min-width="45%"></el-table-column>
        <el-table-column prop="count" :label="$t('event.data_count')" min-width="45%"></el-table-column>
      </el-table>
    </el-drawer>
    <!--regions-->
  </div>
</template>

<script>
  /* eslint-disable */
  export default {
    name: "ResourceType",
    props: {
      syncId: String,
    },
    data() {
      return {
        string2PrettyFormat: [],
        regionsVisible: false,
        direction: 'rtl',
      }
    },
    created() {
    },
    methods: {
      showRegions() {
        this.result = this.$get("/cloud/sync/resourceType/list/" +  this.syncId,response => {
          this.string2PrettyFormat = response.data
          this.regionsVisible =  true;
        });
      },
      handleClose() {
        this.regionsVisible =  false;
      },
    },
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
    width: 80%;
  }
  .rtl >>> .el-form-item__content {
    width: 60%;
  }
  .code-mirror {
    height: 600px !important;
  }
  .code-mirror >>> .CodeMirror {
    /* Set height, width, borders, and global font properties here */
    height: 600px !important;
  }
  /deep/ :focus{outline:0;}
</style>
