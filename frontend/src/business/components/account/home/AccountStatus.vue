<template>
  <div @click="validate(row)">
    <el-tag size="mini" type="warning" v-if="row.status === 'DELETE'">
    {{ $t('account.DELETE') }}
    </el-tag>
    <el-tag size="mini" type="success" v-else-if="row.status === 'VALID'">
      {{ $t('account.VALID') }}
    </el-tag>
    <el-tag size="mini" type="danger" v-else-if="row.status === 'INVALID'">
      {{ $t('account.INVALID') }}
    </el-tag>
  </div>
</template>

<script>
  /* eslint-disable */
  export default {
    name: "AccountStatus",
    inject:['search'],
    props: {
      row: Object
    },
    methods: {
      validate(row) {
        this.$alert(this.$t('account.validate') + this.$t('account.oss_setting') + ' : ' + row.name +  " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.$post("/oss/validate/" + row.id, {}, response => {
                let data = response.data;
                if (data) {
                  if (data.flag) {
                    this.$success(this.$t('server.success'));
                  } else {
                    this.$error(data.message, 10000);
                  }
                } else {
                  this.$error(this.$t('account.error'));
                }
                this.$emit('search');
              });
            }
          }
        });
      },
    }
  }
</script>

<style scoped>

</style>
