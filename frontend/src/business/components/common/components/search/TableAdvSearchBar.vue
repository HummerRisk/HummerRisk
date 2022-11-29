<template>
  <span class="adv-search-bar">
    <el-button icon="el-icon-refresh" size="small" @click="refresh">
      <span v-if="showOpen">{{ $t('commons.refresh') }}</span>
    </el-button>
    <el-button icon="el-icon-download" size="small" @click="download">
      <span v-if="showOpen">{{ $t('server.download') }}</span>
    </el-button>
    <el-button icon="el-icon-setting" size="small" @click="list">
      <span v-if="showOpen">{{ $t('commons.list') }}</span>
    </el-button>

    <el-dialog :title="$t('commons.list_item')" :visible.sync="visible" custom-class="adv-dialog" width="60%"
               :append-to-body="true">
      <div class="columns">
         <div class="fl">
          <el-row :gutter="10">
            <div class="ht">
              <b>{{ $t('commons.list_field') }}</b>
              <el-checkbox class="check-all"
                v-model="checkAllChild"
                :indeterminate="isIndeterminate"
                @change="handleCheckAllChange">
                {{ $t('account.i18n_sync_all') }}</el-checkbox>
            </div>
          </el-row>
          <el-row :gutter="10">
            <el-checkbox-group
              v-model="checkItem"
              @change="handleCheckedColumnNamesChange">
              <el-col v-for="column in columnNames" :key="column.props" :span="11" class="search-item">
                <el-checkbox border
                  :key="column.props"
                  :label="column.props"
                  :disabled="column.disabled"
                >{{ $t(column.label) }}</el-checkbox>
              </el-col>
              </el-checkbox-group>
          </el-row>
        </div>
         <div class="fr">
           <div class="ht"><b>{{ $t('commons.selected_fields') }}</b></div>
           <ul>
             <span v-for="(item, index) in columnNames" :key="index">
               <li v-if="checkItem.includes(item.props)">
                 {{ $t(item.label) }}
               </li>
             </span>
           </ul>
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
      columnNames: {
        type: [Object,Array],
      },
      checkAll: {
        type: Boolean,
        default: true
      },
      isIndeterminate: {
        type: Boolean,
        default: false
      },
      checkedColumnNames: {
        type: [Object,Array],
      },
      showOpen: {
        type: Boolean,
        default: true
      },
    },
    watch:{
      checkedColumnNames(){
        this.checkItem = this.checkedColumnNames;
      },
      checkAll() {
        this.checkAllChild = this.checkAll;
      }
    },
    data() {
      return {
        visible: false,
        checkItem: this.checkedColumnNames,
        checkAllChild: this.checkAll,
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
        this.$emit('download');
      },
      reset() {

      },
      search() {
        this.visible = false;
      },
      handleCheckedColumnNamesChange(value) {
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
/deep/ .el-dialog__header{ padding:12px; border-bottom:1px solid #eee;}
/deep/ .el-dialog__title{ font-size:16px;}
/deep/ .el-dialog__body{padding:20px;}
.column-dialog /deep/ .el-dialog{ width:40%; min-width:550px;}
.columns{ display: flex;}
.columns .ht{ margin-bottom:10px;}
.columns .ht .ck{ margin-left:12px;}
.columns .fl{ flex:1;}
.columns .fl .el-checkbox-group{ overflow: hidden;}
.columns .fl .el-checkbox-group .el-checkbox{ margin-right:0; float:left; width:70%; margin-top:10px;}
.columns .fr { width:180px; border-left:1px solid #f1f1f1; padding-left:20px; margin-left:20px;}
.columns .fr ul{ max-height:375px; overflow-y:auto;}
.columns .fr ul li{ margin-bottom:5px;}
</style>
