<template>
  <tip-button
    :disabled="disabled || isReadOnly"
    @click="exec"
    @clickStop="clickStop"
    :type="type"
    :tip="tip"
    :icon="icon" size="mini" circle/>
</template>

<script>
import TableButton from "./TableButton";
import TipButton from "./TipButton";
/* eslint-disable */
  export default {
    name: "TableOperatorButton",
    components: {TipButton, TableButton},
    data() {
      return {
        isReadOnly: false
      }
    },
    props: {
      icon: {
        type: String,
        default: 'el-icon-question'
      },
      type: {
        type: String,
        default: 'primary'
      },
      tip: {
        type: String
      },
      disabled: {
        type: Boolean,
        default: false
      },
    },
    mounted() {
    },
    methods: {
      exec() {
        this.$emit('exec');
      },
      clickStop() {
        this.$emit('clickStop');
      }
    }
  }
</script>

<style scoped>
</style>
