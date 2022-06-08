<template>
  <container>
    <aside-container :enable-aside-hidden="false">
      <scenario-module
        @nodeSelectEvent="nodeChange"
        @refreshTable="refresh"
        @setModuleOptions="setModuleOptions"
        @enableTrash="false"
        :is-read-only="true"
        ref="nodeTree"/>
    </aside-container>

    <main-container>
      <scenario-list
        :select-node-ids="selectNodeIds"
        :referenced="true"
        :trash-enable="false"
        @selection="setData"
        ref="ScenarioList"/>
    </main-container>
  </container>
</template>

<script>
import ScenarioModule from "../.././common/components/ScenarioModule";
import ScenarioList from "./ScenarioList";
import Container from "../.././common/components/Container";
import AsideContainer from "../.././common/components/AsideContainer";
import MainContainer from "../.././common/components/MainContainer";

/* eslint-disable */
  export default {
    name: "AccountOverview",
    components: {
      Container,
      AsideContainer,
      MainContainer,
      ScenarioModule,
      ScenarioList,
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


</style>

