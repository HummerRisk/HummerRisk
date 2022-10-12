<template>
  <div>
    <el-tag size="mini" type="danger" v-if="row.severity === 'HighRisk'">
    {{ $t('rule.HighRisk') }}
    </el-tag>
    <el-tag size="mini" type="warning" v-else-if="row.severity === 'MediumRisk'">
      {{ $t('rule.MediumRisk') }}
    </el-tag>
    <el-tag size="mini" type="success" v-else-if="row.severity === 'LowRisk'">
      {{ $t('rule.LowRisk') }}
    </el-tag>
    <el-tag size="mini" type="primary" v-else-if="row.severity === ''">
      N/A
    </el-tag>
  </div>
</template>

<script>

  export default {
    name: "RuleType",

    props: {
      row: Object
    },
    methods: {
    }
  }
</script>

<style scoped>

</style>
