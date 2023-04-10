<template>
  <el-menu :unique-opened="true" class="header-user-menu align-right"
           mode="horizontal"
           background-color="#df913c"
           text-color="#fff"
           active-text-color="#fff"
  >
    <el-submenu index="1">
      <template slot="title">
        <span><i class="el-icon-question qa-help icon global"></i> {{ $t('commons.support') }}</span>
      </template>
      <el-menu-item v-for="key in items" :key="key.name" @click="clickTo(key.value)">
        {{ key.name }}
      </el-menu-item>
    </el-submenu>
  </el-menu>

</template>
<script>
export default {
  components: {},
  data() {
    return {
      items: [
        {name: this.$t('commons.help_documentation'), value: 'https://docs.hummerrisk.com'},
        {name: this.$t('commons.advisory'), value: "https://jinshuju.net/f/eBnJ2q"}
      ]
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
.icon {
  font-size: 16px;
  margin-right: 0;
  width: 16px;
  vertical-align: middle;
  height: 18px;
  text-align: center;
}

.global {
  color: #fff;
}
</style>
