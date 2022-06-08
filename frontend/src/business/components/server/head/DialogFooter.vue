<template>

  <div class="dialog-footer">
    <el-button @click="cancel">{{ $t('commons.cancel') }}</el-button>
    <el-button :disabled="rule" type="primary" @click="confirm" @keydown.enter.native.prevent>{{ $t('commons.confirm') }}</el-button>
  </div>

</template>

<script>
/* eslint-disable */
export default {
  name: "DialogFooter",
  data() {
    return {
      rule: false,
    }
  },
  methods: {
    cancel() {
      this.$emit("cancel");
    },
    confirm() {
      this.$emit("confirm");
    }
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: center;
}
</style>
