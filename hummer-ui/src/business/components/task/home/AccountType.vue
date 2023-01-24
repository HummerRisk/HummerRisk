<template>
  <div>
    <el-tag size="mini" type="primary" v-if="row.accountType === 'cloudAccount'">
      {{ $t('task.task_cloud') }}
    </el-tag>
    <el-tag size="mini" type="primary" v-else-if="row.accountType === 'vulnAccount'">
      {{ $t('task.task_vuln') }}
    </el-tag>
    <el-tag size="mini" type="primary" v-else-if="row.accountType === 'serverAccount'">
      {{ $t('task.task_server') }}
    </el-tag>
    <el-tag size="mini" type="primary" v-else-if="row.accountType === 'imageAccount'">
      {{ $t('task.task_image') }}
    </el-tag>
  </div>
</template>

<script>

  export default {
    name: "AccountType",

    props: {
      row: Object
    },
    methods: {
    }
  }
</script>

<style scoped>

</style>
