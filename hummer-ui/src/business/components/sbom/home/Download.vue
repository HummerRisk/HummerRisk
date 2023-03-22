<template>
  <div>
    <el-dropdown split-button type="primary" @click="handleClick" size="mini" @command="(command)=>{handleEdit(command)}">
      <i class="el-icon-download"/> {{ $t('server.download') }}
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item command="json">JSON</el-dropdown-item>
        <el-dropdown-item command="txt">TXT</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>

import {saveAs} from "@/common/js/FileSaver";
import {sbomDownloadUrl} from "@/api/k8s/sbom/sbom";

export default {
    name: "Download",
    props: {
      params: Object
    },
    data() {
      return {
        type: '',
        sourceId: '',
        fileType: 'json',
        name: '',
      }
    },
    methods: {
      handleClick() {
        this.name = this.params.name;
        if (this.params.codeId) {
          this.type = 'code';
          this.sourceId = this.params.id;
        } else if (this.params.imageId) {
          this.type = 'image';
          this.sourceId = this.params.id;
        } else if (this.params.fsId) {
          this.type = 'fs';
          this.sourceId = this.params.id;
        } else {
          return;
        }
        this.$post(sbomDownloadUrl, {
          type: this.type,
          sourceId: this.sourceId,
          fileType: this.fileType
        }, response => {
          if (response.success) {
            let blob = new Blob([response.data], { type: "application/json" });
            saveAs(blob, this.name + "." + this.fileType);
          }
        }, error => {
          console.log("下载报错", error);
        });
      },
      handleEdit(item) {
        this.fileType = item;
      },
      init() {
      },
    },
    created() {
      this.init();
    }
  }
</script>

<style scoped>

</style>
