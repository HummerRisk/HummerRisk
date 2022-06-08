<template>
  <container>

    <aside-container>
      <current-user/>
      <el-divider/>
      <h1>{{ $t('commons.setting') }}</h1>
      <setting-menu/>
    </aside-container>

    <main-container>
      <keep-alive>
        <router-view/>
      </keep-alive>
    </main-container>
  </container>
</template>

<script>
import CurrentUser from "./CurrentUser";
import SettingMenu from "./SettingMenu";
import AsideContainer from "../common/components/AsideContainer";
import Container from "../common/components/Container";
import MainContainer from "../common/components/MainContainer";

export default {
    name: "Setting",
    components: {MainContainer, Container, AsideContainer, SettingMenu, CurrentUser},
  }
</script>

<style scoped>
  .aside-container {
    height: calc(100vh - 40px);
    padding: 20px;
  }

  .main-container {
    height: calc(100vh - 40px);
  }

  h1 {
    font-size: 20px;
    font-weight: 500;
  }

</style>

