<template>
  <div>
    <el-dropdown split-button type="primary" @click="handleClick" size="mini">
      <i class="el-icon-download"/> {{ $t('server.download') }}
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item>JSON</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>

  export default {
    name: "Download",

    methods: {
      handleClick() {
        this.$emit("handleClick");
      },
    }
  }
</script>

<style scoped>

</style>
