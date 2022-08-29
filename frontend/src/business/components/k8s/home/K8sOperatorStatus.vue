<template>
  <div @click="validate(row)">
    <el-tag size="mini" type="warning" v-if="row.operatorStatus === 'DELETE'">
    {{ $t('account.DELETE') }}
    </el-tag>
    <el-tag size="mini" type="success" v-else-if="row.operatorStatus === 'VALID'">
      {{ $t('account.VALID') }}
    </el-tag>
    <el-tag size="mini" type="danger" v-else-if="row.operatorStatus === 'INVALID'">
      {{ $t('account.INVALID') }}
    </el-tag>
  </div>
</template>

<script>
  /* eslint-disable */
  export default {
    name: "K8sOperatorStatus",
    props: {
      row: Object
    },
    methods: {
      validate(row) {
        this.$alert(this.$t('account.validate') + this.$t('k8s.k8s_setting') + ' : ' + row.name +  " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.$post("/cloud/native/operatorStatusValidate/" + row.id, {}, response => {
                if (response.data) {
                  this.$success(this.$t('account.success'));
                } else {
                  this.$error(this.$t('account.error'));
                }
                this.$parent.search;
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
