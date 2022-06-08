<template>
  <div>
    <!--文件上传入口-->
    <!-- 上传组件 -->
    <el-upload action drag :auto-upload="true" :on-change="handleChange" :http-request="submit" :on-progress="uploadProcess"
               :before-upload="beforeUpload" :on-exceed="handleExceed" :on-remove="handleRemove" ref="pluginIcon" :file-list="fileList" :limit="1">
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">{{ $t('package.upload_text1') }}<em>{{ $t('package.upload_text2') }}</em></div>
      <div class="el-upload__tip" slot="tip">{{ $t('package.upload_tip2', ['500M']) }}</div>
      <div class="el-upload__tip content" slot="tip">
        <span>{{ $t('package.content') }}</span>
      </div>
      <div class="el-upload__tip content" slot="tip">
        <span>{{ 'Zip archive format (*.zip, *.ear, *.war, *.jar, *.sar, *.apk, .nupkg)' }}</span>
      </div>
      <div class="el-upload__tip content" slot="tip">
        <span>{{ 'Tape Archive Format (.tar)' }}</span>
      </div>
      <div class="el-upload__tip content" slot="tip">
        <span>{{ 'Gzip format (*.gz, .tgz); Bzip2 format (.bz2, *.tbz2)' }}</span>
      </div>
    </el-upload>

    <!-- 进度显示 -->
    <div class="progress-box">
      <span>{{ $t('package.percent') }}：{{ percentage.toFixed() }}%</span>
    </div>
  </div>
</template>
<script>
export default {
  name: "FileUpload",
  data(){
    return {
      loading:false,
      percentage:0,
      // 文件类型, 例如
      // Zip archive format (*.zip, *.ear, *.war, *.jar, *.sar, *.apk, .nupkg);
      // Tape Archive Format (.tar);
      // Gzip format (*.gz, .tgz); Bzip2 format (.bz2, *.tbz2);
      fileType: ['zip', 'ear', 'war', 'jar','sar', 'apk', 'nupkg', 'tar', 'gz', 'tgz', 'bz2', 'tbz2'],
      // 大小限制(MB)
      fileSize: 500,
      fileList: [],
      headers: {
        'Content-Type': 'multipart/form-data'
      },
    }
  },
  props:['url', 'param'],
  created() {
    if(this.param.packageName) {
      this.fileList = [
        {name: this.param.packageName, path: this.param.path}
      ];
      this.percentage = 100;
    }
  },
  methods:{
    handleChange(file, fileList) { //文件数量改变
      if(this.fileList.length>1) return;
      this.fileList = fileList;
    },
    uploadProcess(event, file, fileList) {
      this.percentage = file.percentage.toFixed(0);
    },
    beforeUpload(file){
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
          if (file.type.indexOf(type) > -1) return true;
          if (fileExtension && fileExtension.indexOf(type) > -1) return true;
          return false;
        });
        if (!isTypeOk) {
          this.$message.error(this.$t('common.file_type_warn') + this.fileType.join("/") + this.$t('common.file_type_warn2'));
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
    },
    handleExceed(){
    },
    handleRemove(file, fileList) {
      this.param.url = file.path;
      this.$post(this.url + "/" + this.param.id, this.param, response => {
        this.$message({
          message: this.$t("commons.delete_success"),
          duration: 1000
        });
      });
      this.percentage = 0;
    },
    submit(file) {
      const interval = setInterval(() => {
        if (this.percentage >= 100) {
          clearInterval(interval);
          return;
        }
        this.percentage += 1; //进度条进度
      }, 80);
      this.$fileUpload(this.url, file.file, null, this.param, response => {
        if(response.success) {
          this.percentage = 100;//上传成功后直接给了100
          this.$message({
            message: this.$t("commons.save_success"),
            duration: 1000
          });
        } else {
          this.percentage = 99;
          this.$message({
            message: this.$t("commons.save_failed"),
            duration: 1000
          });
        }
      });
    }
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
