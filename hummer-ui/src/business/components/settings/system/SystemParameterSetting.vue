<template>
  <el-card shadow="hover">
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
      <el-tab-pane :label="$t('system_parameter_setting.webhook_service_settings')" name="webhook">
        <web-hook-setting/>
      </el-tab-pane>
      <el-tab-pane :label="$t('system_parameter_setting.trivy_settings')" name="scan">
        <scan-setting/>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script>
import EmailSetting from "@/business/components/settings/system/EmailSetting";
import WechatSetting from "@/business/components/settings/system/WechatSetting";
import DingSetting from "@/business/components/settings/system/DingSetting";
import WebHookSetting from "@/business/components/settings/system/WebHookSetting";
import ScanSetting from "@/business/components/settings/system/ScanSetting";
/* eslint-disable */
export default {
  name: "SystemParameterSetting",
  components: {
    EmailSetting,
    WechatSetting,
    DingSetting,
    WebHookSetting,
    ScanSetting,
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
