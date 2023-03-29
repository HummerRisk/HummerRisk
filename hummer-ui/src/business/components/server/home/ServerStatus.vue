<template>
  <div @click="validate(row)">
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
import {serverValidateUrl} from "@/api/k8s/server/server";

export default {
    name: "ServerStatus",
    props: {
      row: Object
    },
    methods: {
      validate(row) {
        this.$alert(this.$t('server.validate') + this.$t('server.server_status') + ' : ' + row.name +  " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.$post(serverValidateUrl + row.id, {}, response => {
                let data = response.data;
                if (data) {
                  if (data.flag) {
                    this.$success(this.$t('server.success'));
                  } else {
                    this.$error(data.message, 10000);
                  }
                } else {
                  this.$error(this.$t('server.error'));
                }
                this.$parent.search;
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
