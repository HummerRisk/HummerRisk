<template>
  <div align="middle">
    <hr-chart :options="options" :width="400" :height="223"></hr-chart>
  </div>
</template>

<script>
import echarts from 'echarts';
import HrChart from "@/business/components/common/chart/HrChart";
/* eslint-disable */
export default {
  name: "ServerChart",
  components: {
    HrChart,
    echarts,
  },
  props: {
    data: {},
  },
  data() {
    return {
      options: {},
    }
  },
  methods: {
    init() {
      this.options = {
        series: [
          {
            type: 'treemap',
            data: [
              {
                name: 'nodeA',
                value: 10,
                children: [
                  {
                    name: this.$t('server.server1'),
                    value: 5
                  },
                  {
                    name: this.$t('server.server2'),
                    value: 5
                  }
                ]
              },
              {
                name: 'nodeB',
                value: 20,
                children: [
                  {
                    name: 'nodeBa',
                    value: 10,
                    children: [
                      {
                        name: this.$t('server.server3'),
                        value: 20
                      },
                      {
                        name: this.$t('server.server4'),
                        value: 20
                      }
                    ]
                  }
                ]
              },
              {
                name: 'nodeC',
                value: 20,
                children: [
                  {
                    name: 'nodeCa',
                    value: 20,
                    children: [
                      {
                        name: this.$t('server.server5'),
                        value: 20
                      },
                      {
                        name: this.$t('server.server6'),
                        value: 20
                      }
                    ]
                  }
                ]
              },
              {
                name: 'nodeD',
                value: 20,
                children: [
                  {
                    name: 'nodeDa',
                    value: 20,
                    children: [
                      {
                        name: this.$t('server.server7'),
                        value: 20
                      }
                    ]
                  }
                ]
              },
              {
                name: 'nodeE',
                value: 20,
                children: [
                  {
                    name: 'nodeEa',
                    value: 20,
                    children: [
                      {
                        name: this.$t('server.server8'),
                        value: 20
                      }
                    ]
                  }
                ]
              },
              {
                name: 'nodeF',
                value: 20,
                children: [
                  {
                    name: 'nodeFa',
                    value: 20,
                    children: [
                      {
                        name: this.$t('server.server9'),
                        value: 20
                      },
                      {
                        name: this.$t('server.server10'),
                        value: 20
                      }
                    ]
                  }
                ]
              }
            ]
          }
        ],
        color: ['#11cfae', '#009ef0', '#627dec', '#893fdc', '#1eb27f','#0051a4']
      };
    },
  },
  created() {
    this.init();
  },
}

</script>

<style scoped>

</style>
