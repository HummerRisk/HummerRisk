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
          <i class="el-icon-check" v-if="i.id === k8sId"></i>
        </template>
      </el-menu-item>
    </div>

  </div>
</template>

<script>
import {getK8sID, getK8sName, getCurrentUser, getCurrentAccountID} from "@/common/js/utils";
import {ACCOUNT, ACCOUNT_ID, ACCOUNT_NAME, K8S, K8S_ID, K8S_NAME, ROLE_ADMIN} from "@/common/js/constants";

/* eslint-disable */
export default {
  name: "SearchList",
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
      k8sId: getK8sID(),
      k8sName: getK8sName(),
    }
  },
  watch: {
    searchString(val) {
      this.query(val)
    }
  },
  methods: {
    async init () {
      await this.$get("/k8s/allCloudNativeList", response => {
        this.items = response.data;
        this.searchArray = response.data;
        if(!this.k8sId) {
          this.k8sId = this.items[0].id;
          this.k8sName = this.items[0].name;
        }
        localStorage.setItem(K8S_ID, this.k8sId);
        localStorage.setItem(K8S_NAME, this.k8sName);
        localStorage.setItem(K8S, JSON.stringify(this.items[0]));
        this.changecurrentAccount(this.k8sId);
      });
    },
    query(queryString) {
      this.items = queryString ? this.searchArray.filter(this.createFilter(queryString)) : this.searchArray;
    },
    createFilter(queryString) {
      return item => {
        return (item.name.toLowerCase().indexOf(queryString.toLowerCase()) !== -1);
      };
    },
    change(k8sId) {
      let currentAccountId = getCurrentAccountID();
      if (k8sId === currentAccountId) {
        return;
      }
      localStorage.setItem(K8S_ID, k8sId);
      let account = this.searchArray.filter(p => p.id === k8sId);
      if(account) {
        this.k8sName = account[0].name;
        localStorage.setItem(K8S_NAME, this.k8sName);
        localStorage.setItem(ACCOUNT, JSON.stringify(account[0]));
      }

      this.k8sId = k8sId;
      this.$emit("k8SSwitch", k8sId, this.k8sName);
      this.changecurrentAccount(k8sId);
    },
    changecurrentAccount(accountId) {
      if (accountId) {
        let account = this.searchArray.filter(p => p.id === accountId);
        if (account.length > 0) {
          this.$emit("selectAccount", accountId, !!this.k8sName?this.k8sName:account[0].name);
        }
      } else {
        this.$emit("selectAccount", null, this.$t('account.select'));
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
