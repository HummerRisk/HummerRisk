<template>
  <div @click="validate(row)" style="cursor: pointer;">
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

import {validateUrl} from "@/api/cloud/account/account";

/* eslint-disable */
  export default {
    name: "AccountStatus",
    inject:['search'],
    props: {
      row: Object
    },
    methods: {
      validate(row) {
        this.$alert(this.$t('account.validate') + this.$t('account.cloud_account') + ' : ' + row.name +  " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              const loadingInstance = this.$loading({ text: this.$t('commons.validing') });
              this.$post(validateUrl + row.id, {}, response => {
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
                loadingInstance.close();
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
