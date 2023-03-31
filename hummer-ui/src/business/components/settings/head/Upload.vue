<template>
  <div v-loading="result.loading">
    <!--文件上传入口-->
    <!-- 上传组件 -->
    <el-upload action drag :auto-upload="false" :on-change="handleChange"
               ref="path" :file-list="fileList" :limit="1">
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">{{ $t('package.upload_text1') }}<em>{{ $t('package.upload_text2') }}</em></div>
      <div class="el-upload__tip" slot="tip">{{ $t('commons.upload_tip', ['5GB']) }}</div>
      <div class="el-upload__tip content" slot="tip">
        <div>{{ 'Tar archive format (*.tar.gz)' }}</div>
      </div>
    </el-upload>

  </div>
</template>
<script>
export default {
  name: "Upload",
  data(){
    return {
      loading:false,
      result: {},
      // 文件类型, 例如
      fileType: ['tar.gz'],
      // 大小限制(MB)
      fileSize: 5000,
      fileList: [],
      headers: {
        'Content-Type': 'multipart/form-data'
      },
    }
  },
  props:['param'],
  created() {
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
        let fileExtension = "";
        if (file.name.lastIndexOf(".") > -1) {
          fileExtension = file.name.slice(file.name.lastIndexOf(".") + 1);
        }

        const isTypeOk = this.fileType.some((type) => {
          if (file.type?file.type.indexOf(type) > -1:file.name.indexOf(type) > -1) return true;
          if (fileExtension && fileExtension.indexOf(type) > -1) return true;
          return false;
        });
        if (!isTypeOk) {
          this.$error(this.$t('commons.adv_search.file_type_warn') + this.fileType.join("/") + this.$t('commons.adv_search.file_type_warn2'));
          this.fileList = [];
          return false;
        }
      }
      // 校检文件大小
      if (this.fileSize) {
        const isLt = file.size / 1024 / 1024 < this.fileSize;
        if (!isLt) {
          this.$message.error(this.$t('commons.file_size_warn') + this.fileSize + ' MB!');
          return false;
        }
      }
      this.$emit('appendUpload', file.raw);
      this.result.loading = false;
    },
  }
}
</script>
<style scoped>
.progress-box {
  box-sizing: border-box;
  width: 360px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  padding: 8px 10px;
  background-color: #ecf5ff;
  font-size: 14px;
  border-radius: 4px;
}
.content {
  color: red;
}
</style>
