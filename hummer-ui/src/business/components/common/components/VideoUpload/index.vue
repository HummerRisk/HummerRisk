<template>
  <div class="component-upload-image">
    <el-upload
      class="avatar-uploader el-upload--text"
      action=""
      :limit="1"
      :http-request="upload"
      :on-remove="handleRemove"
      :show-file-list="false"
      :on-success="handleVideoSuccess"
      :on-error="handleUploadError"
      :before-upload="beforeUploadVideo"
      :on-progress="uploadVideoProcess"
    >
      <el-button v-if="video ==='' " size="small" type="primary">点击上传</el-button>
      <video v-if="videoFlag" id="video_player" ref="videoPlayer" @canplay="getDuration" class="avatar"
             controls="controls" preload="auto" width="400px" height="300px">
        <source :src="video">
      </video>
      <el-progress v-if="progressPercent > 0 && progressPercent!==100" style="width: 200px;margin-top: 8px"
                   :text-inside="true"
                   :stroke-width="20" :percentage="progressPercent"/>
    </el-upload>
    <P class="text">{{ $t('common.video_size_require') }}</P>
    <el-button v-if="video !== ''" size="small" @click="handleRemove(video)">移除</el-button>
  </div>
</template>

<script>
import {deleteFile, importVideoUpload} from '@/api/common/common'

export default {
  data() {
    return {
      dialogVisible: false,
      // 大小限制(MB)
      fileSize: 1024,
      // 文件类型, 例如['mp4', 'ogg', 'flv', 'avi', 'wmv', 'rmvb']
      fileType: ['mp4', 'ogg', 'flv', 'avi', 'wmv', 'rmvb'],
      videoUploadPercent: 0,
      videoFlag: false,
      progressPercent: 0,
      video: '',
      loadingUpload: false
    }
  },
  props: {
    value: String,
    uploading: false
  },
  methods: {
    upload(file) {
      const uploadProgressEvent = progressEvent => {
        this.progressPercent = Math.floor((progressEvent.loaded * 100) / progressEvent.total)
      }
      importVideoUpload(file, uploadProgressEvent).then(response => {
        let res = response.data
        this.$emit('input', res.url)
        this.video = res.url
        this.videoFlag = true
        // this.initVideo()
        this.msgSuccess(this.$t('common.success'))

      })
      this.loading.close()
    },
    handleBeforeUpload() {
      this.loading = this.$loading({
        lock: true,
        text: this.$t('common.uploading'),
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    handleUploadError() {
      this.$message({
        type: 'error',
        message: this.$t('common.upload_error')
      })
      this.loading.close()
    },
    //文件超出个数限制时的钩子
    handleExceed(files, fileList) {
      this.msgInfo(this.$t('shop.agency.upload_limit_count'))
    },
    handleRemove(video, fileList) {
      this.$emit('input', '')
      this.loadingUpload = true
      deleteFile(video).then(response => {
        let res = response.data
        if (!!res.status) {
          this.msgSuccess(this.$t('common.success'))
        }
        this.video = ""
        this.videoFlag = false
      })
      this.loading.close()
    },
    //验证方法：验证视频格式和视频大小
    beforeUploadVideo(file) {
      // 校检文件类型
      if (this.fileType) {
        let fileExtension = ''
        if (file.name.lastIndexOf('.') > -1) {
          fileExtension = file.name.slice(file.name.lastIndexOf('.') + 1)
        }
        const isTypeOk = this.fileType.some((type) => {
          if (file.type.indexOf(type) > -1) return true
          if (fileExtension && fileExtension.indexOf(type) > -1) return true
          return false
        })
        if (!isTypeOk) {
          this.$message.error(this.$t('common.file_type_warn') + this.fileType.join('/') + this.$t('common.file_type_warn2'))
          return false
        }
      }
      // 校检文件大小
      if (this.fileSize) {
        const isLt = file.size / 1024 / 1024 < this.fileSize
        if (!isLt) {
          this.$message.error(this.$t('common.file_size_warn') + this.fileSize + ' MB!')
          return false
        }
      }
      this.handleBeforeUpload()
    },
    //上传进度显示
    uploadVideoProcess(event, file, fileList) {
      this.videoFlag = true
      this.videoUploadPercent = file.percentage.toFixed(0)
    },
    //上传成功
    handleVideoSuccess(res, file) {                               //获取上传图片地址
      this.videoFlag = false
      this.videoUploadPercent = 0
      if (res.status === 200) {
        this.video = res.data.url
      } else {
        this.$message.error(this.$t('common.video_upload_error'))
      }
      this.loadingUpload = false
      this.$emit("update:uploading", this.loadingUpload)
    },
    getDuration() {
      this.$emit('update:duration', this.$refs.videoPlayer.duration)
    },
    initVideo() {
      this.$nextTick(() => {
        let player = this.$video('video_player', {
          controls: true,
          preload: 'auto',
          aspectRatio: '16:9',
          muted: false,
          language: 'zh-CN',
          fluid: true,
          controlBar: {
            timeDivider: true,
            durationDisplay: true,
            remainingTimeDisplay: false,
            fullscreenToggle: true
          }
        })
      })
    }
  },
  created() {
    if (this.value && this.value !== '') {
      this.video = this.value
      this.videoFlag = true
    }
    this.$emit("update:uploading", this.loadingUpload)
  }
}
</script>

<style scoped lang="scss">
.image {
  position: relative;

  .mask {
    opacity: 0;
    position: absolute;
    top: 0;
    width: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    transition: all 0.3s;
  }

  &:hover .mask {
    opacity: 1;
  }
}

.el-upload-dragger {
  width: 90%;
}

.api-upload {
  width: 80%;
}
</style>
