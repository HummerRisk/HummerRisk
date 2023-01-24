<template>
  <div>
    <el-tag size="medium" type="primary" v-if="row.type === 'manual'">
      {{ $t('task.task_manual') }}
    </el-tag>
    <el-tag size="medium" type="primary" v-else-if="row.type === 'quartz'">
      {{ $t('task.task_quartz') }}
    </el-tag>
  </div>
</template>

<script>

  export default {
    name: "TaskType",

    props: {
      row: Object
    },
    methods: {
    }
  }
</script>

<style scoped>

</style>
