<template>
  <el-card class="box-card" shadow="always">
    <el-tabs type="card" @tab-click="filterRules">
      <el-tab-pane :label="$t('rule.all')"></el-tab-pane>
      <el-tab-pane
        :key="tag.tagKey"
        v-for="tag in tags"
        :label="tag.tagName">
      </el-tab-pane>
    </el-tabs>

    <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
              @filter-change="filter" @select-all="select" @select="select">
      <el-table-column :label="$t('rule.rule_name')" width="350" show-overflow-tooltip>
        <template v-slot:default="scope">
          <el-link type="primary" @click="goRule(scope.row)">
            <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column :label="$t('rule.rule_tag')" width="150" show-overflow-tooltip>
        <template v-slot:default="scope">
          {{ scope.row.tagName }}
        </template>
      </el-table-column>
      <el-table-column :label="$t('dashboard.i18n_compliance_ratio')" width="150" show-overflow-tooltip>
        <template v-slot:default="scope">
          {{ !!scope.row.ratio?scope.row.ratio:'0.00%' }}
        </template>
      </el-table-column>
      <el-table-column :label="$t('dashboard.i18n_severity_resource_number')" width="250" show-overflow-tooltip>
        <template v-slot:default="scope">
          <span v-if="scope.row.severity == 'HighRisk'" style="color: #f84846;"> {{ $t('rule.HighRisk') }}</span>
          <span v-else-if="scope.row.severity == 'MediumRisk'" style="color: #fe9636;"> {{ $t('rule.MediumRisk') }}</span>
          <span v-else-if="scope.row.severity == 'LowRisk'" style="color: #4dabef;"> {{ $t('rule.LowRisk') }}</span>
          <span v-else> N/A</span>
          <span> | ({{ scope.row.ruSum?scope.row.ruSum:0 }}/{{ scope.row.reSum?scope.row.reSum:0 }})</span>
          <span> &nbsp;&nbsp;<i :class="scope.row.assets" ></i></span>
        </template>
      </el-table-column>
    </el-table>
    <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
  </el-card>
</template>
<script>
import {_filter, _sort} from "@/common/js/utils";
import TablePagination from "../../common/pagination/TablePagination";

const assets = [
    {key: "ec2", value: "el-icon-s-platform"},
    {key: "ecs", value: "el-icon-s-platform"},
    {key: "cvm", value: "el-icon-s-platform"},
    {key: "ami", value: "el-icon-picture"},
    {key: "s3", value: "el-icon-folder-opened"},
    {key: "oss", value: "el-icon-folder-opened"},
    {key: "obs", value: "el-icon-folder-opened"},
    {key: "cos", value: "el-icon-folder-opened"},
    {key: "security-group", value: "el-icon-lock"},
    {key: "network-addr", value: "el-icon-connection"},
    {key: "etc", value: "el-icon-s-platform"},
    {key: "asg", value: "el-icon-s-operation"},
    {key: "elb", value: "el-icon-s-operation"},
    {key: "slb", value: "el-icon-s-operation"},
    {key: "lambda", value: "el-icon-data-board"},
    {key: "autoscaling", value: "el-icon-s-operation"},
    {key: "account", value: "el-icon-s-custom"},
    {key: "eip", value: "el-icon-s-grid"},
    {key: "ip", value: "el-icon-s-grid"},
    {key: "ebs", value: "el-icon-wallet"},
    {key: "iam", value: "el-icon-s-tools"},
    {key: "rds", value: "el-icon-s-finance"},
    {key: "tag", value: "el-icon-s-flag"},
    {key: "vpc", value: "el-icon-menu"},
    {key: "vm", value: "el-icon-s-platform"},
    {key: "networksecuritygroup", value: "el-icon-lock"},
    {key: "disk", value: "el-icon-wallet"},
    {key: "storage", value: "el-icon-wallet"},
    {key: "others", value: "el-icon-s-unfold"},
  ];
  /* eslint-disable */
  export default {
    name: "MdCardContentRule",
    components: {
      TablePagination,
    },
    data() {
      return {
        tags: [],
        tableData: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        condition: {
          components: Object
        },
      }
    },
    methods: {
      goResource () {
      },
      init() {
        this.tagLists();
        this.search();
      },
      tagLists() {
        let url = "/rule/ruleTags";
        this.result = this.$get(url, response => {
          this.tags = response.data;
        });
      },
      filterRules (tag) {
        let key = "";
        for (let obj of this.tags) {
          if (tag.label == obj.tagName) {
            key = obj.tagKey;
            break;
          } else {
            key = 'all';
          }
        }
        if (key != 'all') {
          this.condition.tagKey = tag.key;
        }
        this.search();
      },
      //查询列表
      async search() {
        let url = "/dashboard/point/target/" + this.currentPage + "/" + this.pageSize;
        this.result = await this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
          for (let data of this.tableData) {
            this.getAssets(data);
          }
        });
      },
      sort(column) {
        _sort(column, this.condition);
        this.init();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.init();
      },
      select(selection) {
        this.selectIds.clear()
        selection.forEach(s => {
          this.selectIds.add(s.id)
        })
      },
      tableRowClassName({row, rowIndex}) {
        if (rowIndex%4 === 0) {
          return 'success-row';
        } else if (rowIndex%2 === 0) {
          return 'warning-row';
        } else {
          return '';
        }
      },
      async getAssets (item) {
        await this.$get("rule/getResourceTypesById/" + item.id, response => {
          item.assets = this.checkoutAssets(response.data);
        });
      },
      checkoutAssets (resource) {
        for(let item of assets){
          if (resource.indexOf(item.key) > -1) {
            return item.value;
          }
        }
        return "cloud_done";
      },
    },
    mounted() {
      this.init();
    },
  }

</script>

<style scoped>
  .box-card {
    width: 100%;
  }
  .el-row {
    margin-bottom: 20px;
  }
  .el-col {
    border-radius: 4px;
  }
  .bg-purple-dark {
    background: #99a9bf;
  }
  .bg-purple {
    background: #ffffff;
  }
  .bg-purple-light {
    background: #e5e9f2;
  }
  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  }
  .row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
  }
  .dashboard-head-title {
    text-align: center;
    font-size: initial;
    vertical-align:middle;
    margin-top: 8px;
  }
  .dashboard-head-value {
    text-align: center;
    font-weight: 500;
    font-size: larger;
    vertical-align:middle;
  }
  .md-primary {
    font-size: larger;
    color: rgb(0,33,51);
    text-decoration:underline;
  }
</style>
