<template>
  <span>
    <table-operator-button v-for="(btn, index) in buttons" :key="index"
                              :tip="btn.tip" :icon="btn.icon" :type="btn.type"
                              @exec="click(btn)" @click.stop="clickStop(btn)"/>
  </span>
</template>

<script>
import TableOperatorButton from "./TableOperatorButton";
/* eslint-disable */
  export default {
    name: "TableOperators",
    components: {TableOperatorButton},
    props: {
      row: Object,
      buttons: Array
    },
    methods: {
      click(btn) {
        if (btn.exec instanceof Function) {
          btn.exec(this.row);
        }
      },
      clickStop(btn) {
        if (btn.stop instanceof Function) {
          btn.stop(this.row);
        }
      },
    },
    computed: {
    }
  }
</script>

<style scoped>

</style>
