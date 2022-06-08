<template>
  <el-card>
    <el-tabs class="system-setting" v-model="activeName">
      <el-tab-pane :label="$t('system_parameter_setting.mailbox_service_settings')" name="email">
        <email-setting/>
      </el-tab-pane>
      <el-tab-pane :label="$t('system_parameter_setting.wei_service_settings')" name="wei">
        <wechat-setting/>
      </el-tab-pane>
      <el-tab-pane :label="$t('system_parameter_setting.ding_service_settings')" name="ding">
        <ding-setting/>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script>
import EmailSetting from "./EmailSetting";
import WechatSetting from "./WechatSetting";
import DingSetting from "./DingSetting";
/* eslint-disable */
export default {
  name: "SystemParameterSetting",
  components: {
    EmailSetting, WechatSetting, DingSetting
  },
  data() {
    return {
      activeName: 'email'
    }
  }
}
</script>

<style scoped>

</style>
