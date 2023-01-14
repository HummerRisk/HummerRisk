<template>
  <div style="margin-top: 10%;">
    <hr-chart :options="options"></hr-chart>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import HrChart from "@/business/components/common/chart/HrChart";

let ruleGroup = [];
let groupName = ['rule'];
let color = ['#11cfae', '#009ef0', '#627dec', '#0051a4', '#893fdc', '#89ffff', '#0099ff'];
/* eslint-disable */
export default {
  name: "RuleGroupPieChart",
  components: {
    HrChart,
    echarts
  },
  data() {
    return {
      options: {
        legend: {},
        tooltip: {},
        dataset: {
          dimensions: groupName,
          source: ruleGroup
        },
        xAxis: {type: 'category'},
        yAxis: {},
        series: [
          {type: 'bar'},
          {type: 'bar'},
          {type: 'bar'}
        ],
        grid: {
          left: "0%",
          right: "2%",
          width:"100%",
          bottom: "2%",
          top: "30%",
          containLabel: true
        },
        color: color
      },
    }
  },
  methods: {
    init () {
      this.$post("/dashboard/distribution", {group: "vulnRuleGroup", limit: 6}, response => {
        let data = '{"rule":"' + this.$t('vuln.rule_group_vuln') + '", "key1":value1, "key2":value2, "key3":value3}';
        let i = 1;
        for (let obj of response.data) {
          groupName.push(obj.xAxis);
          data = data.replace('key' + i, obj.xAxis).replace('value' + i, obj.yAxis);
          i++;
        }
        if(data.indexOf("key3")) {
          data = data.replace(", \"key3\":value3", "");
        }
        ruleGroup.push(typeof(data) == 'string'?JSON.parse(data):data);
      });
    },
  },
  created() {
    this.init();
  },
}

</script>

<style scoped>

  .echarts {
    margin: 0 auto;
    max-width: 300px;
    max-height: 200px;
  }

</style>
