<template>

    <div class="dialog-footer">
      <el-button @click="cancel">{{ $t('commons.cancel') }}</el-button>
      <el-button v-if="showPre" @click="preStep">{{ $t('account.pre_step') }}</el-button>
      <el-button v-if="showNext" type="success" @click="nextStep">{{ $t('account.next_step') }}</el-button>
      <el-button v-if="showConfirm" type="primary" @click="confirm" @keydown.enter.native.prevent>{{ $t('commons.confirm') }}</el-button>
    </div>

</template>

<script>
    export default {
      name: "DialogFooter",
      props: {
        showPre: {
          type: Boolean,
          default: false
        },
        showNext: {
          type: Boolean,
          default: true
        },
        showConfirm: {
          type: Boolean,
          default: false
        },
      },
      methods: {
        cancel() {
          this.$emit("cancel");
        },
        nextStep() {
          this.$emit("nextStep");
        },
        preStep() {
          this.$emit("preStep");
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
