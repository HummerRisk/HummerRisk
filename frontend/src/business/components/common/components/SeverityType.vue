<template>
  <span>
    <span v-if="row.severity === 'CriticalRisk'" style="color: #8a1917;">
    {{ $t('rule.CriticalRisk') }}
    </span>
    <span v-if="row.severity === 'HighRisk'" style="color: #f84846;">
      {{ $t('rule.HighRisk') }}
    </span>
    <span v-else-if="row.severity === 'MediumRisk'" style="color: #fe9636;">
      {{ $t('rule.MediumRisk') }}
    </span>
    <span v-else-if="row.severity === 'LowRisk'" style="color: #4dabef;">
      {{ $t('rule.LowRisk') }}
    </span>
    <span v-else-if="row.severity === ''">
      N/A
    </span>
  </span>
</template>

<script>

  export default {
    name: "SeverityType",

    props: {
      row: Object
    },
    methods: {
    }
  }
</script>

<style scoped>

</style>
