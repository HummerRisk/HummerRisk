<template>
  <table-search-component v-model="component.operator.value" :component="component">
    <template v-slot="scope">
      <el-input v-model="scope.component.value" v-bind="props"
                :placeholder="$t('commons.input_content')" size="small"/>
    </template>
  </table-search-component>
</template>

<script>
import TableSearchComponent from "./TableSearchComponet";
/* eslint-disable */
  export default {
    name: "TableSearchInput",
    components: {TableSearchComponent},
    props: ['component'],
    data() {
      return {
        props: {
          maxlength: "60",
          showWordLimit: true,
          ...this.component.props
        }
      }
    }
  }
</script>

<style scoped>

</style>
