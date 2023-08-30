<template>
  <container class="header-menu">
    <aside-container :enable-aside-hidden="false" :width="'200px'">
      <image-group
        @nodeSelectEvent="nodeChange"
        @refreshTable="refresh"
        @setModuleOptions="setModuleOptions"
        @enableTrash="false"
        :is-read-only="true"
        ref="nodeTree"/>
    </aside-container>

    <main-container>
      <image-list
        :select-node-ids="selectNodeIds"
        :referenced="true"
        :trash-enable="false"
        @selection="setData"
        ref="ScenarioList"/>
    </main-container>
  </container>
</template>

<script>
import ImageGroup from "@/business/components/image/home/ImageGroup";
import ImageList from "@/business/components/image/home/ImageList";
import Container from "@/business/components/common/components/Container";
import AsideContainer from "@/business/components/common/components/AsideContainer";
import MainContainer from "@/business/components/common/components/MainContainer";

/* eslint-disable */
export default {
  name: "ImagePage",
  components: {
    Container,
    AsideContainer,
    MainContainer,
    ImageGroup,
    ImageList,
  },
  data() {
    return {
      dialogVisible: false,
      selectNodeIds: [],
      currentScenario: [],
      currentScenarioIds: [],
    }
  },
  methods: {
    close() {
      this.refresh();
      this.dialogVisible = false;
    },
    open() {
      this.dialogVisible = true;
      if (this.$refs.ScenarioList) {
        this.$refs.ScenarioList.search();
      }
    },
    setModuleOptions(data) {
      this.moduleOptions = data;
    },
    refresh() {
      this.$refs.ScenarioList.search();
    },
    nodeChange(node, nodeIds, pNodes) {
      this.selectNodeIds = nodeIds;
    },
    setData(data) {
      this.currentScenario = Array.from(data).map(row => row);
      this.currentScenarioIds = Array.from(data).map(row => row.id);
    },
  },
}
</script>

<style scoped>
.header-menu >>> .main-container {
  padding: 0;
  margin: 0 0 0 5px;
}

</style>

