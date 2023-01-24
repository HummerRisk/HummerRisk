<template>
  <div>
    <el-alert
      title="Notice:"
      type="info"
      show-icon>
      <template v-slot:default>
        {{ $t('system_parameter_setting.message.notes') }}
      </template>
    </el-alert>
    <resource-notification :receiverOptions="receiverOptions"/>
  </div>
</template>

<script>
import {getCurrentUser} from "@/common/js/utils";
import ResourceNotification from "@/business/components/settings/system/ResourceNotification";
import Container from "@/business/components/common/components/Container";
import MainContainer from "@/business/components/common/components/MainContainer";
/* eslint-disable */
export default {
  name: "MessageNotification",
  components: {
    ResourceNotification,
    Container,
    MainContainer,
  },
  data() {

    return {
      receiverOptions: [],
    }
  },

  activated() {
    this.initUserList();
  },
  methods: {
    handleEdit(index, data) {
      data.isReadOnly = true;
      if (data.type === 'EMAIL') {
        data.isReadOnly = !data.isReadOnly
      }
    },
    currentUser: () => {
      return getCurrentUser();
    },

    initUserList() {
      this.result = this.$get('user/list/all', response => {
        this.receiverOptions = response.data
      });
    }
  }
}
</script>

<style scoped>

</style>
