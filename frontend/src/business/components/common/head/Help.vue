<template>
  <ul class="header-user-menu align-right el-menu--horizontal el-menu"
      style="background-color: #df913c;" @mouseenter="setColor(true)" @mouseout="setColor(false)">
    <li role="menuitem" aria-haspopup="true" class="el-submenu" tabindex="0">
      <div class="el-submenu__title" @click="helpDoc" id="help"
           style="border-bottom-color: transparent; color: rgb(255, 255, 255); background-color: #df913c;">
        <i class="el-icon-question qa-help"></i>  {{ $t('commons.help_documentation') }}
      </div>
    </li>
  </ul>

</template>
<script>
export default {
  components: {},
  data() {
    return {}
  },
  mounted() {
  },
  methods: {
    setColor(e) {
      if (e) {
        document.querySelector("#help").setAttribute("style", "background-color:#b26133;color:white;");
      } else {
        document.querySelector("#help").setAttribute("style", "background-color:#df913c;color:white;");
      }
    },
    helpDoc() {
      window.open("https://docs.hummerrisk.com");
    }
  }
}
</script>

<style scoped>
.qa-help {
  color: #fff;
  margin: 0;
  font-size: 14px;
}
</style>
