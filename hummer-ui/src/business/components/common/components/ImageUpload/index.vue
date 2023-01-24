<template>
  <div v-loading="result.loading">
    <!--文件上传入口-->
    <!-- 上传组件 -->
    <el-upload action drag :auto-upload="true" :on-change="handleChange" :http-request="submit" list-type="picture" :on-progress="uploadProcess"
               :before-upload="beforeUpload" :on-exceed="handleExceed" :on-remove="handleRemove" ref="pluginPath" :file-list="fileList" :limit="1">
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">{{ $t('package.upload_text1') }}<em>{{ $t('package.upload_text2') }}</em></div>
      <div class="el-upload__tip" slot="tip">{{ $t('package.upload_tip', ['10M']) }}</div>
    </el-upload>

    <!-- 进度显示 -->
    <div class="progress-box">
      <span>{{ $t('package.percent') }}：{{ percentage.toFixed() }}%</span>
    </div>
  </div>
</template>
<script>

export default {
  name: "ImageUpload",
  data(){
    return {
      loading:false,
      percentage:0,
      // 文件类型, 例如['png', 'jpg', 'jpeg']
      fileType: ['png', 'jpg', 'jpeg', 'webp', 'bmp', 'tif', 'gif', 'apng'],
      // 大小限制(MB)
      fileSize: 10,
      fileList: [],
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      result: {},
      uploadSuccess: true,
    }
  },
  props:['url', 'param'],
  created() {
    this.location = window.location.href.split("#")[0];
    if(this.param.pluginIcon && this.param.pluginIcon.indexOf("/") > -1) {
      this.fileList = [
        {name: this.param.pluginIcon, url: this.location + this.param.pluginIcon, pluginIcon: this.param.pluginIcon}
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
          if (file.type?file.type.indexOf(type) > -1:file.name.indexOf(type) > -1) return true;
          if (fileExtension && fileExtension.indexOf(type) > -1) return true;
          return false;
        });
        if (!isTypeOk) {
          this.$error(this.$t('commons.adv_search.file_type_warn') + this.fileType.join("/") + this.$t('commons.adv_search.file_type_warn2'));
          this.fileList = [];
          this.uploadSuccess = false;
          return false;
        }
      }
      // 校检文件大小
      if (this.fileSize) {
        const isLt = file.size / 1024 / 1024 < this.fileSize;
        if (!isLt) {
          this.$error(this.$t('commons.file_size_warn') + this.fileSize + ' MB!');
          this.fileList = [];
          this.uploadSuccess = false;
          return false;
        }
      }
    },
    handleExceed(){
    },
    handleRemove(file, fileList) {
      this.param.url = file.pluginIcon;
      this.result = this.$post(this.url + "/" + this.param.id, this.param, () => {
        this.$message({
          message: this.$t("commons.delete_success"),
          duration: 1000
        });
      });
      this.percentage = 0;
    },
    submit(file) {
      if(!this.uploadSuccess) return;
      const interval = setInterval(() => {
        if (this.percentage >= 100) {
          clearInterval(interval);
          return;
        }
        this.percentage += 1; //进度条进度
      }, 80);
      this.result = this.$fileUpload(this.url, file.file, null, this.param, response => {
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
</style>
