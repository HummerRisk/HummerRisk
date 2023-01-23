<template>
  <div>
    <el-card>
      <el-tabs class="system-setting" v-model="activeName">
        <el-tab-pane :label="$t('system_parameter_setting.message.setting')" name="message">
          <message-notification/>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import MessageNotification from "./MessageNotification"

export default {
  name: "MessageSetting",
  components: {
    MessageNotification
  },
  data(){
    return{
      activeName: 'message'
    }
  }
}
</script>

<style scoped>

</style>
