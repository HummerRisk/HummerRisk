<template>
  <el-main class="main-container">
    <slot></slot>
  </el-main>
</template>

<script>
  export default {
    name: "MainContainer"
  }
</script>

<style scoped>

  .main-container {
    padding: 15px;
    height: calc(100vh - 80px);
  }

</style>
