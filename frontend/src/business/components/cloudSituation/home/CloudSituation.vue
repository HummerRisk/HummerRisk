<template>
  <container class="header-menu">
    <aside-container :enable-aside-hidden="false">
      <cloud-module
        @nodeSelectEvent="nodeChange"
        @refreshTable="refresh"
        @setModuleOptions="setModuleOptions"
        @enableTrash="false"
        :is-read-only="true"
        ref="nodeTree"/>
    </aside-container>

    <main-container>
      <scenario-source-list
        :select-node-ids="selectNodeIds"
        :referenced="true"
        :trash-enable="false"
        @selection="setData"
        ref="ScenarioList"/>
    </main-container>
  </container>
</template>
<script>

import CloudModule from "./CloudModule";
import ScenarioSourceList from "./ScenarioSourceList";
import Container from "../.././common/components/Container";
import AsideContainer from "../.././common/components/AsideContainer";
import MainContainer from "../.././common/components/MainContainer";
/* eslint-disable */
export default {
  name: "Situation",
  components: {
    Container,
    AsideContainer,
    MainContainer,
    CloudModule,
    ScenarioSourceList,
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
      if (this.$refs.CloudNativeModule) {
        this.$refs.CloudNativeModule.search();
      }
    },
    setModuleOptions(data) {
      this.moduleOptions = data;
    },
    refresh() {
      this.$refs.HistoryList.search();
    },
    nodeChange(node, nodeIds, pNodes) {
      this.selectNodeIds = nodeIds;
      localStorage.setItem('selectK8sIds', this.selectNodeIds[0]);
    },
    setData(data) {
      this.currentScenario = Array.from(data).map(row => row);
      this.currentScenarioIds = Array.from(data).map(row => row.id);
    },
  },
}

</script>

<style scoped>
.hr-container >>> span.title {
  font-size: 16px;
  font-weight: 500;
  margin-top: 0;
  text-overflow: ellipsis;
  overflow: hidden;
  word-wrap: break-word;
  white-space: nowrap;
}
.hr-main-container {
  padding: 15px;
  height: calc(100vh - 80px);
}
.header-menu >>> .main-container {
  padding: 0;
  margin: 0 0 0 10px;
}
</style>
