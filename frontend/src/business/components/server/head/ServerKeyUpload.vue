<template>
  <div v-loading="result.loading">
    <!--文件上传入口-->
    <!-- 上传组件 -->
    <el-upload action drag :auto-upload="false" :on-change="handleChange"
               ref="path" :file-list="fileList" :limit="1">
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">{{ $t('package.upload_text1') }}<em>{{ $t('package.upload_text2') }}</em></div>
      <div class="el-upload__tip" slot="tip">{{ $t('server.upload_tip', ['50M']) }}</div>
      <div class="el-upload__tip content" slot="tip">
        <span>{{ 'Format (*.gem/*rsa)' }}</span>
      </div>
    </el-upload>

  </div>
</template>
<script>
export default {
  name: "ServerKeyUpload",
  data(){
    return {
      loading:false,
      result: {},
      // 文件类型, 例如
      // Tar archive format (*.gem);
      fileType: ['gem', ""],
      // 大小限制(MB)
      fileSize: 50,
      fileList: [],
      headers: {
        'Content-Type': 'multipart/form-data'
      },
    }
  },
  props:['param'],
  created() {
    if(this.param) {
      this.fileList = [
        {name: this.param, path: this.param}
      ];
    }
  },
  methods:{
    handleChange(file, fileList) { //文件数量改变
      if(this.fileList.length>1) return;
      this.fileList = fileList;
      this.uploadValidate(file);
    },
    uploadValidate(file) {
      // 校检文件类型
      if (this.fileType) {
        this.result.loading = true;
        let fileExtension = "";
        if (file.name.lastIndexOf(".") > -1) {
          fileExtension = file.name.slice(file.name.lastIndexOf(".") + 1);
        }

        const isTypeOk = this.fileType.some((type) => {
          if (file.type?file.type.indexOf(type):file.name.indexOf(type) > -1) return true;
          if (fileExtension && fileExtension.indexOf(type) > -1) return true;
          return false;
        });
        if (!isTypeOk) {
          this.$message.error(this.$t('commons.adv_search.file_type_warn') + this.fileType.join("/") + this.$t('commons.adv_search.file_type_warn2'));
          return false;
        }
      }
      // 校检文件大小
      if (this.fileSize) {
        const isLt = file.size / 1024 / 1024 < this.fileSize;
        if (!isLt) {
          this.$message.error(this.$t('common.file_size_warn') + this.fileSize + ' MB!');
          return false;
        }
      }
      this.$emit('append', file.raw);
      this.result.loading = false;
    },
  }
}
</script>
<style scoped>

.content {
  color: red;
}
</style>
