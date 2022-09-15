<template>
  <main-container class="main-container" v-loading="result.loading">
  </main-container>
</template>

<script>
import MainContainer from "../../common/components/MainContainer";

/* eslint-disable */
export default {
  components: {
    MainContainer,
  },
  data() {
    return {
      result: {},
    }
  },
  methods: {
  },
}
</script>

<style scoped>
.main-container {
  padding: 5px;
}
</style>

