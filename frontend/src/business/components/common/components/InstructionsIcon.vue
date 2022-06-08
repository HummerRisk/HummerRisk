<template>
  <el-tooltip class="instructions-icon" :effect="effect" :placement="placement">
    <template v-slot:content>
      {{ content }}
    </template>
    <i :style="{'font-size': size + 'px'}" class="el-icon-info"></i>
  </el-tooltip>
</template>

<script>
/* eslint-disable */
    export default {
      name: "InstructionsIcon",
      props: {
        content: String,
        icon: {
          type: String,
          default: 'el-icon-question'
        },
        placement: {
          type: String,
          default: 'top-start'
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
          default: '16'
        },
      },
    }
</script>

<style scoped>

  .el-icon-info {
    color: #606266;
  }

  .el-icon-info:hover {
    color: #409EFF;
  }

</style>
