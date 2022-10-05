<template>

    <div class="dialog-footer">
      <el-button v-if="showCancel" @click="cancel">{{ $t('commons.cancel') }}</el-button>
      <el-button v-if="showConfirm" type="primary" @click="confirm" @keydown.enter.native.prevent>{{ $t('commons.confirm') }}</el-button>
    </div>

</template>

<script>
    export default {
      name: "DialogFooter",
      props: {
        showCancel: {
          type: Boolean,
          default: true
        },
        showConfirm: {
          type: Boolean,
          default: true
        },
      },
      methods: {
        cancel() {
          this.$emit("cancel");
        },
        confirm() {
          this.$emit("confirm");
        },
      }
    }
</script>

<style scoped>
.dialog-footer {
  text-align: center;
}
</style>
