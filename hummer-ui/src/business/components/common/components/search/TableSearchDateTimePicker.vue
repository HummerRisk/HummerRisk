<template>
  <table-search-component v-model="component.operator.value" :component="component" @change="change">
    <template v-slot="scope">
      <el-date-picker v-model="scope.component.value" v-bind="scope.component.props"
                      :placeholder="$t('commons.date.select_date_time')" size="small"
                      :type="type" :key="type" value-format="timestamp"
                      :range-separator="$t('commons.date.range_separator')"
                      :start-placeholder="$t('commons.date.start_date_time')"
                      :end-placeholder="$t('commons.date.end_date_time')">
      </el-date-picker>
    </template>
  </table-search-component>

</template>

<script>
import TableSearchComponent from "./TableSearchComponet";
import {OPERATORS} from "./search-components"
/* eslint-disable */
  export default {
    name: "TableSearchDateTimePicker",
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
          return "datetimerange";
        } else {
          return "datetime";
        }
      }
    }
  }
</script>

<style scoped>

</style>
