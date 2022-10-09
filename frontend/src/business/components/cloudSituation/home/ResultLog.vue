<template>
  <div>
    <el-row class="el-form-item-dev" v-if="row.cloudTaskItemLogDTOs.length == 0">
      <span>{{ $t('resource.i18n_no_data') }}<br></span>
    </el-row>
    <el-row class="el-form-item-dev" v-if="row.cloudTaskItemLogDTOs.length > 0">
      <el-table :show-header="false" :data="row.cloudTaskItemLogDTOs" class="adjust-table table-content">
        <el-table-column>
          <template v-slot:default="scope">
            <div>
              <el-row>
                <el-col :span="24">
                  <div class="grid-content bg-purple-light">
                    <span class="grid-content-log-span"> {{ scope.row.resourceType }}</span>
                    <span class="grid-content-log-span">
                      <img :src="require(`@/assets/img/platform/${scope.row.accountUrl}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                       &nbsp;&nbsp; {{ scope.row.accountLabel }} | {{ scope.row.regionName }}
                    </span>
                    <span class="grid-content-status-span" v-if="scope.row.status === 'UNCHECKED'" style="color: #919398">
                      <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                    </span>
                    <span class="grid-content-status-span" v-else-if="scope.row.status === 'APPROVED'" style="color: #579df8">
                      <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                    </span>
                    <span class="grid-content-status-span" v-else-if="scope.row.status === 'PROCESSING'" style="color: #579df8">
                      <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                    </span>
                    <span class="grid-content-status-span" v-else-if="scope.row.status === 'FINISHED'" style="color: #7ebf50">
                      <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                    </span>
                    <span class="grid-content-status-span" v-else-if="scope.row.status === 'ERROR'" style="color: red;">
                      <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                    </span>
                    <span class="grid-content-status-span" v-else-if="scope.row.status === 'WARNING'" style="color: #dda450">
                      <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                    </span>

                </div>

                </el-col>
              </el-row>
            </div>
<!--            <div>
                <el-button plain size="small" @click="showInformation(scope.row, $t('resource.i18n_resource_scanning_log'))">
                  <i class="el-icon-tickets"></i> {{ $t('resource.i18n_resource_scanning_log') }}
                </el-button>
                <el-button plain size="small" @click="showInformation(scope.row, $t('resource.i18n_resource_scanning_api'))">
                  <i class="el-icon-document-checked"></i> {{ $t('resource.i18n_resource_scanning_api') }}
                </el-button>
                <el-button plain size="small" @click="showInformation(scope.row, $t('resource.i18n_resource_scanning_return'))">
                  <i class="el-icon-document"></i> {{ $t('resource.i18n_resource_scanning_return') }}
                </el-button>
            </div>-->
            <div>
              <el-drawer
                size="55%"
                :title="fileTitle"
                :append-to-body="true"
                :before-close="innerDrawerClose"
                :visible.sync="innerDrawer">
                <codemirror ref="cmEditor" v-model="script" class="code-mirror" :options="cmOptions" />
              </el-drawer>
            </div>
            <div class="bg-purple-div">
              <span v-for="(logItem, index) in scope.row.cloudResourceSyncItemLogs" :key="index"
                    v-bind:class="{true: 'color-red', false: ''}[logItem.result == false]">
                    {{ logItem.createTime | timestampFormatDate }}
                    {{ logItem.operator }}
                    {{ logItem.operation }}
                    {{ logItem.output }}
                    <br>
              </span>
              <div v-if="(scope.row.status === 'FINISHED' || scope.row.status === 'ERROR' || scope.row.status === 'WARNING')
                      && (scope.row.cloudResourceSyncItemLogs.length === 0)">
                {{ $t('resource.the_cloud_platform') }}
                {{ scope.row.accountLabel }} |
                {{ scope.row.regionName }}
                {{ $t('resource.not_currently') }}
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-row>
  </div>
</template>

<script>
/* eslint-disable */
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
      row: {cloudTaskItemLogDTOs: []},
    },
    methods: {
      init() {
      },
      getStatus () {
        if (this.checkStatus(this.row.cloudTaskItemLogDTOs)) {
          return;
        }
        let showLogTaskId = this.row.showLogTaskId;
        let url = "/cloud/sync/log/item/list/";
        this.$get(url + showLogTaskId, response => {
          for (let obj of response.data) {
            for (let item of this.row.cloudTaskItemLogDTOs) {
              if (obj.id === item.id) {
                item.status = obj.status;
                item.cloudResourceSyncItemLogs = obj.cloudResourceSyncItemLogs;
              }
            }
          }
        });
      },
      //是否是结束状态，返回false代表都在运行中，true代表已结束
      checkStatus (cloudTaskItemLogDTOs) {
        let sum = 0;
        for (let row of cloudTaskItemLogDTOs) {
          if (row.status != 'ERROR' && row.status != 'FINISHED' && row.status != 'WARNING') {
            sum++;
          }
        }
        return sum == 0;
      },
      innerDrawerClose() {
        this.innerDrawer = false;
      },
      showInformation(item, title) {
        this.$post("/resource/resourceLog", item.cloudTaskItem, response => {
          let data = response.data;
          switch (title) {
            case this.$t('resource.i18n_resource_scanning_log'):
              this.script = data?data.custodianRunLog:"[]";
              break;
            case this.$t('resource.i18n_resource_scanning_api'):
              this.script = data?data.metadata:"[]";
              break;
            case this.$t('resource.i18n_resource_scanning_return'):
              this.script = data?data.resources:"[]";
              break;
          }
          this.fileTitle = title;
          this.innerDrawer = true;
        });
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
  .error-log{
    color: #dd4444;
  }
  .code-mirror >>> .CodeMirror {
    /* Set height, width, borders, and global font properties here */
    height: 600px !important;
  }
</style>
