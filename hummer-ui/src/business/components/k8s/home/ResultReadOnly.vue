<template>
  <div class="read-only">
    <el-form label-position="left" inline class="el-table-expand">
      <el-form-item :key="element.key" v-for="element in elements" :label="element.key + ' : '">
        <span v-if="!element.flag" show-overflow-tooltip>
          <el-tooltip class="item" effect="dark" :content="JSON.stringify(element.value)" placement="top">
            <span type="primary">{{ 'Details' }}</span>
          </el-tooltip>
        </span>
        <el-tooltip v-if="element.flag && !!element.value" class="item" effect="light" :content="typeof(element.value) === 'boolean'?element.value.toString():element.value" placement="top">
          <span class="table-expand-span-value">
              {{ element.value }}
          </span>
        </el-tooltip>
        <span v-if="element.flag && !element.value"> N/A</span>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
/* eslint-disable */

export default {
    name: "ResultReadOnly",
    props: {
      row: Object
    },
    components: {},
    data() {
      return {
        elements: [],
      }
    },
    methods: {
      init() {
        for (let item in this.row) {
          let flag = true;
          let value = this.row[item];
          //string && boolean的值直接显示, object是[{}]
          if (typeof (value) === 'number') {
            value = String(value);
          }
          if (typeof (value) === 'object') {
            if (value !== null && JSON.stringify(value) !== '[]' && JSON.stringify(value) !== '{}') {
              flag = false;
            }
            if (JSON.stringify(value) === '[]' || JSON.stringify(value) === '{}') {
              value = "";
            }
          }
          if (item.indexOf('$$') === -1 && item !== 'show') {
            let map = {key: item, value: value, flag: flag};
            this.elements.push(map);
          }
        }
      },
    },
    mounted() {
      this.init();
    }
  }
</script>

<style scoped>
  .el-table-expand {
    font-size: 0;
  }
  .el-table-expand label {
    width: 90px;
    color: #99a9bf;
  }
  .el-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    padding: 0 20px 0 50px;
    width: 47%;
    white-space: nowrap;
    /*overflow: hidden;*/
    text-overflow: ellipsis;
  }
  .el-table-expand >>> .el-form-item__content {
    width: 100%;
  }
  .el-table-expand .table-expand-span-value {
    max-width: 95%;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis; /*超出部分用...代替*/
    display: inline-block;
  }
  .rtl >>> .el-drawer__body {
    overflow-y: auto;
    padding: 20px;
  }
  .rtl >>> input {
    width: 100%;
  }
  .rtl >>> .el-select {
    width: 80%;
  }
  .rtl >>> .el-form-item__content {
    width: 75%;
  }
  .code-mirror {
    height: 600px !important;
  }
  .code-mirror >>> .CodeMirror {
    /* Set height, width, borders, and global font properties here */
    height: 600px !important;
  }
  .read-only >>> .el-form-item__label {
    color: #1e6427;
  }
  .read-only >>> .el-form-item__content {
    color: #11365d;
  }
</style>
