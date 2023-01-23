<template>
  <div>
    <tag v-for="(role, index) in roles"
            :key="index"
            :effect="effect"
            :type="type"
            :content="$t('role.' + role.id)"/>
  </div>
</template>

<script>
import Tag from "./Tag";
/* eslint-disable */
  export default {
    name: "RolesTag",
    components: {Tag},
    props: {
      type: {
        type: String,
        default: 'primary',
      },
      roles: {
        type: Array
      },
      effect: {
        type: String,
        default: 'dark',
      }
    }
  }
</script>

<style scoped>

</style>
