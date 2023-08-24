<template>
  <div v-loading="result.loading" class="main-cond">
    <el-input :placeholder="$t('resource.search_by_name')"
              prefix-icon="el-icon-search"
              v-model="searchString"
              clearable
              class="search-input"
              size="small"/>
    <div v-if="items.length === 0" class="v-dar">
        <span class="v-dar-span">
          {{ $t('resource.i18n_no_data') }}
        </span>
    </div>
    <div v-else class="e-dar">
      <el-menu-item :key="i.id" v-for="i in items" @click="changeProject(i.id)">
        <template slot="title">
          <div class="title">
            <img :src="require(`@/assets/img/platform/${i.pluginIcon}`)" class="e-dar-img" alt=""/>
            {{ i.accountName }} | {{ i.createTime | timestampFormatDate }} {{ $t('dashboard.i18n_scan') }}
          </div>
          <i class="el-icon-check" v-if="i.id === currentProjectId"></i>
        </template>
      </el-menu-item>
    </div>

  </div>
</template>

<script>
import {allProjectListUrl} from "@/api/cloud/project/project";

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
      currentProjectId: '',
    }
  },
  props: {
    projectId: String
  },
  watch: {
    searchString(val) {
      this.query(val)
    },
    projectId() {
      this.init();
    },
  },
  methods: {
    async init () {
      this.currentProjectId = this.projectId;
      await this.$get(allProjectListUrl, response => {
        this.items = response.data;
        this.searchArray = response.data;
        if (this.currentProjectId) {
          this.changeProject(this.currentProjectId);
        } else {
          if (this.items.length > 0) {
            this.changeProject(this.items[0].id);
          }
        }
      });
    },
    query(queryString) {
      this.items = queryString ? this.searchArray.filter(this.createFilter(queryString)) : this.searchArray;
    },
    createFilter(queryString) {
      return item => {
        return (item.accountName.toLowerCase().indexOf(queryString.toLowerCase()) !== -1);
      };
    },
    changeProject(projectId) {
      if (projectId) {
        let project = this.searchArray.filter(p => p.id === projectId);
        if (project.length > 0) {
          this.$emit("projectSwitch", projectId, project[0].accountName, project[0].createTime);
        }
      }
    }
  }
}
</script>

<style scoped>

.main-cond {
  width: 400px;
}

.v-dar {
  text-align: center;
  margin: 15px 0;
}

.v-dar-span {
  font-size: 15px;
  color: #8a8b8d;
}

.e-dar {
  height: 150px;
  overflow: auto;
}

.e-dar-img {
  width: 16px;
  height: 16px;
  vertical-align:middle;
}

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
  max-width: 400px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.el-icon-check {
  color: #773888;
  margin-left: 10px;
}
</style>
