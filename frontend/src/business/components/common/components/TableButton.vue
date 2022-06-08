<template>
  <el-button :disabled="disabled" @click="exec()" plain :type="type" :icon="icon" :size="size">
    {{ content }}
  </el-button>
</template>

<script>
/* eslint-disable */
  export default {
      name: "TableButton",
      data() {
        return {
          disabled: false
        }
      },
      props: {
        content: String,
        icon: {
          type: String,
          default: 'el-icon-question'
        },
        type: {
          type: String,
          default: null
        },
        effect: {
          type: String,
          default: 'dark'
        },
        size: {
          type: String,
          default: 'mini'
        },
      },
      mounted() {
      },
      methods: {
        exec() {
          this.$emit('click');
        }
      }
    }
</script>

<style scoped>

  .el-button {
    padding: 8px 10px;
  }
</style>
