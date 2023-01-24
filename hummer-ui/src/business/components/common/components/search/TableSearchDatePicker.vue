<template>
  <table-search-component v-model="component.operator.value" :component="component">
    <template v-slot="scope">
      <el-date-picker
        v-model="scope.component.value" v-bind="scope.component.props"
        :placeholder="$t('commons.date.select_date')" size="small"
        :type="type" :key="type" value-format="timestamp"
        :range-separator="$t('commons.date.range_separator')"
        :start-placeholder="$t('commons.date.start_date')"
        :end-placeholder="$t('commons.date.end_date')">
      </el-date-picker>
    </template>
  </table-search-component>

</template>

<script>
import TableSearchComponent from "./TableSearchComponet";
import {OPERATORS} from "./search-components"
/* eslint-disable */
  export default {
    name: "TableSearchDatePicker",
    components: {TableSearchComponent},
    props: ['component'],
    methods: {
      change(value) {
        if (value === OPERATORS.BETWEEN.value) {
          if (!Array.isArray(this.component.value)) {
            this.component.value = [];
          }
        } else {
          if (Array.isArray(this.component.value)) {
            this.component.value = "";
          }
        }
      }
    },
    computed: {
      type() {
        if (this.component.operator.value === OPERATORS.BETWEEN.value) {
          return "daterange";
        } else {
          return "date";
        }
      }
    }
  }
</script>

<style scoped>

</style>
