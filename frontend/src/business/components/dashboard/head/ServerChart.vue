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
                name: this.$t('server.server_1'),
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
                name: this.$t('server.server_2'),
                value: 20,
                children: [
                  {
                    name: this.$t('server.server_2'),
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
                name: this.$t('server.server_3'),
                value: 20,
                children: [
                  {
                    name: this.$t('server.server_3'),
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
                name: this.$t('server.server_4'),
                value: 20,
                children: [
                  {
                    name: this.$t('server.server_4'),
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
                name: this.$t('server.server_5'),
                value: 20,
                children: [
                  {
                    name: this.$t('server.server_5'),
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
                name: this.$t('server.server_6'),
                value: 20,
                children: [
                  {
                    name: this.$t('server.server_6'),
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
        grid: {
          top: '10%',
          left: '1%',
          right: '2%',
          bottom: '2%',
          containLabel: true
        },
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
