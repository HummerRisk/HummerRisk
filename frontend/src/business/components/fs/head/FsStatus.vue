<template>
  <div>
    <el-tag size="mini" type="warning" v-if="row.status === 'DELETE'">
    {{ $t('server.DELETE') }}
    </el-tag>
    <el-tag size="mini" type="success" v-else-if="row.status === 'VALID'">
      {{ $t('server.VALID') }}
    </el-tag>
    <el-tag size="mini" type="danger" v-else-if="row.status === 'INVALID'">
      {{ $t('server.INVALID') }}
    </el-tag>
  </div>
</template>

<script>
  /* eslint-disable */
  export default {
    name: "ImageStatus",
    props: {
      row: Object
    },
    methods: {
    }
  }
</script>

<style scoped>

</style>
