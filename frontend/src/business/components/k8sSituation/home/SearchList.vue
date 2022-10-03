<template>
  <div>
    <el-input :placeholder="$t('resource.search_by_name')"
              prefix-icon="el-icon-search"
              v-model="searchString"
              clearable
              class="search-input"
              size="small"/>
    <div v-if="items.length === 0" style="text-align: center; margin: 15px 0">
        <span style="font-size: 15px; color: #8a8b8d;">
          {{ $t('resource.i18n_no_data') }}
        </span>
    </div>
    <div v-else style="height: 150px;overflow: auto">
      <el-menu-item :key="i.id" v-for="i in items" @click="change(i.id)">
        <template slot="title">
          <div class="title">
            <img :src="require(`@/assets/img/platform/${i.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
            {{ i.name }}
          </div>
          <i class="el-icon-check" v-if="i.id === currentAccountId"></i>
        </template>
      </el-menu-item>
    </div>

  </div>
</template>

<script>
/* eslint-disable */
export default {
  name: "SearchList",
  props: {
    items: Array,
    options: Object,
    currentAccount: String
  },
  mounted() {
    this.init();
  },
  computed: {
  },
  data() {
    return {
      searchArray: [],
      searchString: '',
      currentAccountId: "",
    }
  },
  watch: {
    searchString(val) {
      this.query(val)
    }
  },
  methods: {
    init() {
      this.searchArray = this .items;
    },
    query(queryString) {
      this.items = queryString ? this.searchArray.filter(this.createFilter(queryString)) : this.searchArray;
    },
    createFilter(queryString) {
      return item => {
        return (item.name.toLowerCase().indexOf(queryString.toLowerCase()) !== -1);
      };
    },
    change(accountId) {
      this.changeAccountName(accountId);
    },
    changeAccountName(accountId) {
      if (accountId) {
        let account = this.searchArray.filter(p => p.id === accountId);
        if (account.length > 0) {
          this.$emit("cloudAccountSwitch", accountId, !!this.currentAccount?this.currentAccount:account[0].name, account[0].pluginIcon);
        }
      }
    }
  }
}
</script>

<style scoped>

.search-input {
  padding: 0;
  margin-top: -5px;
}

.search-input >>> .el-input__inner {
  border-radius: 0;
}

.title {
  display: inline-block;
  padding-left: 15px;
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.el-icon-check {
  color: #773888;
  margin-left: 10px;
}
</style>
