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
      <el-menu-item :key="i.id" v-for="i in items" @click="changeProject(i.id)">
        <template slot="title">
          <div class="title">
            <img :src="require(`@/assets/img/platform/${i.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
            {{ i.name }} | {{ i.createTime | timestampFormatDate }}
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
          let project = this.searchArray.filter(p => p.id === this.currentProjectId);
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
          this.$emit("cloudAccountSwitch", projectId, project[0].name);
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
