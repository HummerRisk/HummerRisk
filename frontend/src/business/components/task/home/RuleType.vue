<template>
  <div>
    <el-tag size="mini" type="primary" v-if="row.ruleType === 'rule'">
      {{ $t('task.task_rule') }}
    </el-tag>
    <el-tag size="mini" type="primary" v-else-if="row.ruleType === 'tag'">
      {{ $t('task.task_tag') }}
    </el-tag>
    <el-tag size="mini" type="primary" v-else-if="row.ruleType === 'group'">
      {{ $t('task.task_group') }}
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
