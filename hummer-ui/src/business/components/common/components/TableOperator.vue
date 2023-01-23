<template>
  <span>
    <slot name="front"></slot>
    <table-operator-button :tip="tip1" icon="el-icon-edit" @exec="editClick" @click.stop="editClickStop"/>
    <slot name="middle"></slot>
    <table-operator-button :tip="tip2" icon="el-icon-delete" type="danger" @exec="deleteClick" @click.stop="deleteClickStop"/>
    <slot name="behind"></slot>
  </span>

</template>

<script>
import TableOperatorButton from "./TableOperatorButton";

/* eslint-disable */
    export default {
      name: "TableOperator",
      components: {TableOperatorButton},
      props: {
        tip1: {
          type: String,
          default() {
            return this.$t('commons.edit');
          }
        },
        tip2: {
          type: String,
          default() {
            return this.$t('commons.delete');
          }
        },
      },
      methods: {
        editClick() {
          this.$emit('editClick');
        },
        editClickStop() {
          this.$emit('editClickStop');
        },
        deleteClick() {
          this.$emit('deleteClick');
        },
        deleteClickStop() {
          this.$emit('deleteClickStop');
        }
      }
    }
</script>

<style scoped>

</style>
