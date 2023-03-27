<template>
  <div>
    <el-row class="el-form-item-dev" v-if="row.regionLogs.length == 0">
      <span>{{ $t('resource.i18n_no_data') }}<br></span>
    </el-row>
    <el-row class="el-form-item-dev" v-if="row.regionLogs.length > 0">
      <el-table :show-header="false" :data="row.regionLogs" class="adjust-table table-content">
        <el-table-column>
          <template v-slot:default="scope">
            <div>
              <el-row>
                <el-col :span="24">
                  <div class="grid-content bg-purple-light">
                    <span class="grid-content-log-span">
                       {{ $t('event.cloud_account') }}: {{ row.account.name }}
                    </span>
                    <span class="grid-content-status-span" v-if="scope.row.status === 0" style="color: #579df8">
                      <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                    </span>
                    <span class="grid-content-status-span" v-else-if="scope.row.status === 1" style="color: #7ebf50">
                      <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                    </span>
                    <span class="grid-content-status-span" v-else-if="scope.row.status === 2" style="color: red;">
                      <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                    </span>
                    <span class="grid-content-status-span">
                       {{ $t('event.region') }}: {{ scope.row.region }}
                    </span>
                </div>
                </el-col>
              </el-row>
            </div>
            <div class="bg-purple-div">
              <span>
                  {{ $t('el.datepicker.startTime') }}:{{scope.row.startTime | timestampFormatDate}}
              </span>
              <br>
              <span>
                  {{ $t('el.datepicker.endTime') }}:{{scope.row.endTime | timestampFormatDate}} {{scope.row.exception}}
              </span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-row>
  </div>
</template>

<script>
/* eslint-disable */
  import {cloudEventSyncLogRegionListUrl} from "@/api/cloud/event/event";

export default {
    name: "ResultLog",
    components: {
    },
    data() {
      return {
        timer: '',
        fileTitle: '',
        innerDrawer: false,
        script: '',
        cmOptions: {
          tabSize: 4,
          mode: {
            name: 'json',
            json: true
          },
          theme: 'bespin',
          lineNumbers: true,
          line: true,
          indentWithTabs: true,
        },
      }
    },
    activated() {
    },
    props: {
      row: {regionLogs: []},
    },
    methods: {
      init() {
      },
      getStatus () {
        if (this.checkStatus(this.row.regionLogs)) {
          return;
        }
        let showLogTaskId = this.row.showLogTaskId;
        this.post(cloudEventSyncLogRegionListUrl + showLogTaskId,{}, response => {
          for (let obj of response.data) {
            for (let item of this.row.regionLogs) {
              if (obj.id === item.id) {
                item = obj
              }
            }
          }
        });
      },
      //是否是结束状态，返回false代表都在运行中，true代表已结束
      checkStatus (regionLogs) {
        let sum = 0;
        for (let row of regionLogs) {
          if (row.status != 2 && row.status != 1) {
            sum++;
          }
        }
        return sum == 0;
      },
      innerDrawerClose() {
        this.innerDrawer = false;
      },
    },

    created() {
      this.init();
    },
    mounted() {
      this.timer = setInterval(this.getStatus,10000);
    },
    beforeDestroy() {
      clearInterval(this.timer);
    }
  }
</script>

<style scoped>
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
    background: #d3dce6;
  }
  .bg-purple-light {
    background: #f2f2f2;
  }
  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  }

  .grid-content-log-span {
    width: 38%;
    float: left;
    vertical-align: middle;
    display:table-cell;
    margin: 6px 1%;
  }

  .grid-content-status-span {
    width: 18%;float: left;
    vertical-align: middle;
    display:table-cell;
    margin: 6px 1%;
  }

  .bg-purple-div {
    margin: 10px;
  }
  .code-mirror {
    height: 600px !important;
  }
  .code-mirror >>> .CodeMirror {
    /* Set height, width, borders, and global font properties here */
    height: 600px !important;
  }
</style>
