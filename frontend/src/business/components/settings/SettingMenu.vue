<template>
  <el-menu menu-trigger="click" :default-active="$route.path" router class="setting">
    <el-submenu index="1" v-permission="['admin']">
      <template v-slot:title>
        <font-awesome-icon class="icon account" :icon="['far', 'address-card']" size="lg"/>
        <span>{{ $t('commons.system') }}</span>
      </template>
      <el-menu-item v-for="menu in systems" :key="menu.index" :index="menu.index" class="setting-item">
        {{ $t(menu.title) }}
      </el-menu-item>
    </el-submenu>

    <el-submenu index="4">
      <template v-slot:title>
        <font-awesome-icon class="icon" :icon="['far', 'user']" size="lg"/>
        <span>{{ $t('commons.personal_info') }}</span>
      </template>
      <el-menu-item v-for="menu in persons" :key="menu.index" :index="menu.index" class="setting-item"
                    v-permission="menu.roles">
        {{ $t(menu.title) }}
      </el-menu-item>
    </el-submenu>

  </el-menu>
</template>

<script>
import Setting from "@/business/components/settings/router";

export default {
  name: "SettingMenu",
  data() {
    let getMenus = function (group) {
      let menus = [];
      Setting.children.forEach(child => {
        if (child.meta[group] === true) {
          let menu = {index: Setting.path + "/" + child.path}
          menu.title = child.meta.title;
          menu.roles = child.meta.roles;
          menus.push(menu);
        }
      })
      return menus;
    }
    return {
      systems: getMenus('system'),
      persons: getMenus('person'),
    }
  },
  mounted() {
  }
}
</script>

<style scoped>
.setting {
  border-right: 0;
}

.setting .setting-item {
  height: 40px;
  line-height: 40px;
}

.icon {
  width: 24px;
  margin-right: 10px;
}

.account {
  color: #5a78f0;
}

</style>
