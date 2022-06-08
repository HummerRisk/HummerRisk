<template>

  <el-container class="container">
    <slot></slot>
  </el-container>

</template>

<script>
    export default {
      name: "Container"
    }
</script>

<style scoped>

  .container >>> span.title {
    font-size: 16px;
    font-weight: 500;
    margin-top: 0;
    text-overflow: ellipsis;
    overflow: hidden;
    word-wrap: break-word;
    white-space: nowrap;
  }

</style>
