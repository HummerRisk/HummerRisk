<template>
  <el-menu :unique-opened="true" class="header-user-menu align-right"
           mode="horizontal"
           background-color="#df913c"
           text-color="#fff"
           active-text-color="#fff"
  >
    <el-submenu index="1">
      <template slot="title">
        <font-awesome-icon :icon="['far', 'headset']" style="color: #ffffff;" />
        <span>{{ $t('commons.support') }}</span>
      </template>
      <el-menu-item v-for="(key, value) in items" :key="key" @click="clickTo(key)">
        {{ value }}
      </el-menu-item>
    </el-submenu>
  </el-menu>

</template>
<script>
export default {
  components: {},
  data() {
    return {
      items: {
        Docs: 'https://docs.hummerrisk.com',
        Support: "https://jinshuju.net/f/eBnJ2q"
      }
    }
  },
  mounted() {
  },
  methods: {
    clickTo(url) {
      if (!url) return;
      window.open(url);
    }
  }
}
</script>

<style scoped>
.el-icon-check {
  color: #44b349;
  margin-left: 10px;
}
</style>
