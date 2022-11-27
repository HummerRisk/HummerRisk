<template>
  <span class="adv-search-bar">
    <el-input size="small" :placeholder="$t('commons.search_by') + $t(selectName) + $t('commons.search')"
              v-model="filterText" class="input-with-select search" maxlength="60" @change="search" clearable>
      <el-select v-model="select" slot="prepend" :placeholder="$t('commons.please_select')" @change="changeName" style="width:100%">
        <el-option
          v-for="item in items"
          :key="item.id" style="width:100%"
          :label="$t(item.name)"
          :value="item.id">
          &nbsp;&nbsp; {{ $t(item.name) }}
          </el-option>
      </el-select>
      <el-button slot="append" icon="el-icon-search" @click="search"></el-button>
    </el-input>
    <el-button icon="iconfont icon-shaixuan" size="small" @click="open" v-if="showOpen" style="margin: 10px;">{{ $t('el.table.confirmFilter') }}</el-button>
    <el-dialog :title="$t('commons.adv_search.combine')" :visible.sync="visible" custom-class="adv-dialog"
               :append-to-body="true">
      <div>
        <div class="search-items">
          <component class="search-item" v-for="(component, index) in config.components" :key="index"
                     :is="component.name" :component="component"/>
        </div>
      </div>
      <template v-slot:footer>
        <div class="dialog-footer">
          <el-button @click="reset">{{ $t('commons.adv_search.reset') }}</el-button>
          <el-button type="primary" @click="search">{{ $t('commons.adv_search.search') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </span>
</template>

<script>
import components from "@/business/components/common/components/search/search-components";
import {cloneDeep} from "lodash";
/* eslint-disable */
    export default {
      components: {...components},
      name: "TableSearchBar",
      props: {
        condition: {
          type: Object
        },
        showOpen: {
          type: Boolean,
          default: true
        },
        items: {
          type: [Object,Array],
          default: () => [
            {'id' : 'name', 'name' : 'commons.name'},
          ],
        },
        tip: {
          String,
          default() {
            return this.$t('commons.search_by_name');
          }
        }
      },
      data() {
        return {
          visible: false,
          config: this.init(),
          select: this.items[0].id,
          selectName: this.items[0].name,
          filterText: '',
        }
      },
      methods: {
        changeName(id) {
          for (let item of this.items) {
            if(item.id === id) {
              this.selectName = item.name;
              return;
            }
          }
        },
        search() {
          for (let item of this.items) {
            this.condition[item.id] = "";
          }
          let condition = {}
          this.config.components.forEach(component => {
            let operator = component.operator.value;
            let value = component.value;
            if (Array.isArray(value)) {
              if (value.length > 0) {
                condition[component.key] = {
                  operator: operator,
                  value: value
                }
              }
            } else {
              if (value !== undefined && value !== null && value !== "") {
                condition[component.key] = {
                  operator: operator,
                  value: value
                }
              }
            }
          });

          // 清除name
          if (this.filterText) {
            this.condition[this.select] = this.filterText;
          } else {
            this.condition[this.select] = undefined;
          }
          // 添加组合条件
          this.condition.combine = condition;
          this.$emit('update:condition', this.condition);
          this.$emit('search', condition);
          this.visible = false;
        },
        init() {
          let config = cloneDeep(this.condition);
          config.components.forEach(component => {
            let operator = component.operator.value;
            component.operator.value = operator === undefined ? component.operator.options[0].value : operator;
          })
          return config;
        },
        reset() {
          let source = this.condition.components;
          this.config.components.forEach((component, index) => {
            if (component.operator.value !== undefined) {
              let operator = source[index].operator.value;
              component.operator.value = operator === undefined ? component.operator.options[0].value : operator;
            }
            if (component.value !== undefined) {
              component.value = source[index].value;
            }
          })
          this.condition.combine = undefined;
          this.$emit('update:condition', this.condition);
          this.$emit('search');
        },
        open() {
          this.visible = true;
        },
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
.adv-search-bar {
}
.search {
  width: 500px;
  margin-left: 10px;
}
.search >>> .el-select .el-input {
  width: 120px;
}
.search >>> .el-input-group__prepend {
  background-color: #fff;
}
.dialog-footer {
  text-align: center;
}

.search-items {
  width: 100%;
}

@media only screen and (max-width: 1469px) {
  .search-item {
    width: 100%;
  }
}

@media only screen and (min-width: 1470px) {
  .search-item {
    width: 50%;
  }
}

.search-item {
  display: inline-block;
  margin-top: 10px;
}

.adv-search-bar >>> .iconfont {
  margin: 1px 3px 0 0;
  width: 24px;
  height: 18px;
  text-align: center;
  font-size: 12px;
}
</style>
