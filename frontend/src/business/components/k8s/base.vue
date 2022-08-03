<template>
  <el-col>
    <k8s-header-menus/>
    <div>
      <transition>
        <keep-alive>
          <router-view :baseUrl="baseUrl"/>
        </keep-alive>
      </transition>
    </div>
  </el-col>
</template>
<script>

import K8sHeaderMenus from "./head/K8sHeaderMenus";

export default {
    name: "k8s",
    components: {K8sHeaderMenus},
    data() {
      return {
        baseUrl: "k8s"
      }
    }
  }

</script>

<style scoped>

</style>
