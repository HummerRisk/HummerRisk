<template>
  <span class="adv-search-bar">
    <el-button icon="el-icon-refresh" size="small" @click="refresh">{{ $t('commons.refresh') }}</el-button>
    <el-button icon="el-icon-download" size="small" @click="download">{{ $t('server.download') }}</el-button>
    <el-button icon="el-icon-setting" size="small" @click="list">{{ $t('commons.list') }}</el-button>

    <el-dialog :title="$t('commons.list_item')" :visible.sync="visible" custom-class="adv-dialog" width="50%"
               :append-to-body="true">
      <div>
        <div class="search-items">
          <el-row :gutter="10">
            <el-checkbox class="check-all"
              v-model="checkAll"
              :indeterminate="isIndeterminate"
              @change="handleCheckAllChange"
            >{{ $t('account.i18n_sync_all') }}</el-checkbox>
          </el-row>
          <el-row :gutter="10">
            <el-checkbox-group
              v-model="checkItem"
              @change="handleCheckedColumnNamesChange">
              <el-col v-for="column in columnNames" :key="column.props" :span="11" class="search-item">
                <el-checkbox border
                  :key="column.props"
                  :label="column.props"
                >{{ $t(column.label) }}</el-checkbox>
              </el-col>
              </el-checkbox-group>
          </el-row>
        </div>
      </div>
      <template v-slot:footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="search">{{ $t('commons.confirm') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </span>
</template>

<script>
/* eslint-disable */
  export default {
    name: "TableAdvSearchBar",
    props: {
      condition: Object,
      columnNames: {
        type: [Object,Array],
      },
      checkAll: true,
      isIndeterminate: false,
      checkedColumnNames: {
        type: [Object,Array],
      },
    },
    watch:{
      checkedColumnNames(){
        this.checkItem = this.checkedColumnNames;
      }
    },
    data() {
      return {
        visible: false,
        checkItem: this.checkedColumnNames,
      }
    },
    methods: {
      refresh() {
        this.$emit('search');
      },
      list() {
        this.visible = true;
      },
      download() {

      },
      reset() {

      },
      search() {
        this.visible = false;
      },
      handleCheckedColumnNamesChange(value) {
        console.log("234231", value)
        this.$emit('handleCheckedColumnNamesChange', value);
      },
      handleCheckAllChange(val) {
        this.$emit('handleCheckAllChange', val);
      },
    },
    created() {
    }
  }
</script>

<style>
  @media only screen and (min-width: 1870px) {
    .el-dialog.adv-dialog {
      width: 70%;
    }
  }

  @media only screen and (min-width: 1650px) and (max-width: 1869px) {
    .el-dialog.adv-dialog {
      width: 80%;
    }
  }

  @media only screen and (min-width: 1470px) and (max-width: 1649px) {
    .el-dialog.adv-dialog {
      width: 90%;
    }
  }

  @media only screen and (max-width: 1469px) {
    .el-dialog.adv-dialog {
      width: 70%;
      min-width: 695px;
    }
  }
</style>

<style scoped>
.search-item {
  margin: 0 0 5px 10px;
}
.check-all {
  float: right;
  margin: 0 10px 5px 10px;
}
</style>
