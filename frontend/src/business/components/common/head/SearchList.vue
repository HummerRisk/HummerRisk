<template>
  <div v-loading="result.loading" style="width: 300px;">
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
import {getCurrentAccountID, getCurrentUser, hasRoles} from "@/common/js/utils";
import {ACCOUNT, ACCOUNT_ID, ACCOUNT_NAME, ROLE_ADMIN} from "@/common/js/constants";

/* eslint-disable */
export default {
  name: "SearchList",
  props: {
    options: Object,
    currentAccount: String
  },
  created() {
    this.init();
  },
  computed: {
  },
  data() {
    return {
      result: {},
      items: [],
      searchArray: [],
      searchString: '',
      userId: getCurrentUser().id,
      currentAccountId: localStorage.getItem(ACCOUNT_ID),
    }
  },
  watch: {
    searchString(val) {
      this.query(val)
    }
  },
  methods: {
    init: function () {
      if (hasRoles(ROLE_ADMIN)) {
        this.result = this.$get("/account/allList", response => {
          this.items = response.data;
          this.searchArray = response.data;
          if (!localStorage.getItem(ACCOUNT_ID)) {
            let userLastAccountId = getCurrentUser().lastAccountId;
            if (userLastAccountId) {
              // id 是否存在
              if (this.searchArray.length > 0 && this.searchArray.map(p => p.id).indexOf(userLastAccountId) !== -1) {
                localStorage.setItem(ACCOUNT_ID, userLastAccountId);
                let account = this.searchArray.filter(p => p.id === userLastAccountId);
                if(account) {
                  localStorage.setItem(ACCOUNT_NAME, account[0].name);
                  localStorage.setItem(ACCOUNT, JSON.stringify(account[0]));
                }
              }
            }
          }
          let accountId = getCurrentAccountID();
          if (accountId) {
            // 保存的 accountId 在当前云张号列表是否存在; 切换工作空间后
            if (this.searchArray.length > 0 && this.searchArray.map(p => p.id).indexOf(accountId) === -1) {
              this.change(this.items[0].id);
            }
          } else {
            if (this.items.length > 0) {
              this.change(this.items[0].id);
            }
          }
          this.changeAccountName(accountId);
        })
      }
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
      let currentAccountId = getCurrentAccountID();
      if (accountId === currentAccountId) {
        return;
      }
      this.$post("/user/update/current", {id: this.userId, lastAccountId: accountId}, () => {
        localStorage.setItem(ACCOUNT_ID, accountId);
        let account = this.searchArray.filter(p => p.id === accountId);
        if(account) {
          localStorage.setItem(ACCOUNT_NAME, account[0].name);
          localStorage.setItem(ACCOUNT, JSON.stringify(account[0]));
        }

        this.currentAccountId = accountId;
        this.$emit("cloudAccountSwitch", accountId, account[0].name);
        this.changeAccountName(accountId);
      });
    },
    changeAccountName(accountId) {
      if (accountId) {
        let account = this.searchArray.filter(p => p.id === accountId);
        if (account.length > 0) {
          this.$emit("update:currentAccount", !!this.currentAccount?this.currentAccount:account[0].name);
        }
      } else {
        this.$emit("update:currentAccount", this.$t('account.select'));
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
  max-width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.el-icon-check {
  color: #773888;
  margin-left: 10px;
}
</style>
