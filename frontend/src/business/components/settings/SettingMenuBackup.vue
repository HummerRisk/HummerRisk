<template>
  <el-menu menu-trigger="click" :default-active="$route.path" router>
    <el-submenu index="1" v-permission="['admin']">
      <template v-slot:title>
        <font-awesome-icon class="icon account" :icon="['far', 'address-card']" size="lg"/>
        <span>{{ $t('commons.system') }}</span>
      </template>
      <el-menu-item index="/setting/user">{{ $t('commons.user') }}</el-menu-item>
      <el-menu-item index="/setting/systemparametersetting">{{ $t('commons.system_parameter_setting') }}</el-menu-item>
      <el-menu-item index="/setting/messagesetting">{{ $t('system_parameter_setting.message.setting') }}</el-menu-item>
      <el-menu-item index="/setting/webmsg">{{ $t('webmsg.web_msg') }}</el-menu-item>
    </el-submenu>

    <el-submenu index="4">
      <template v-slot:title>
        <font-awesome-icon class="icon" :icon="['far', 'user']" size="lg"/>
        <span>{{ $t('commons.personal_info') }}</span>
      </template>
      <el-menu-item index="/setting/personsetting">{{ $t('commons.personal_setting') }}</el-menu-item>
      <el-menu-item v-permission="['admin']"
                    index="/setting/apikeys">API Keys
      </el-menu-item>
    </el-submenu>

  </el-menu>
</template>

<script>

  export default {
    name: "SettingMenuBackup",
    data() {
      return {
      }
    },
    mounted() {
    },
  }
</script>

<style scoped>
  .el-menu {
    border-right: 0;
  }

  .el-menu-item {
    height: 40px;
    line-height: 40px;
  }

  .icon {
    width: 24px;
    margin-right: 10px;
  }

  .account {
    color: #5a78f0;
  }

</style>
