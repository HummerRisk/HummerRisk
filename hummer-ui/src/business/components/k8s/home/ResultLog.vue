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
                    <span class="grid-content-log-span"> {{ scope.row.rule.name }}</span>
                    <span class="grid-content-log-span">
                      <img :src="require(`@/assets/img/platform/${scope.row.rule.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                       &nbsp;&nbsp; {{ scope.row.rule.pluginName }} | {{ scope.row.cloudTaskItem.regionName }}
                    </span>
                    <span class="grid-content-status-span" v-if="scope.row.cloudTaskItem.status === 'UNCHECKED'" style="color: #919398">
                      <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                    </span>
                    <span class="grid-content-status-span" v-else-if="scope.row.cloudTaskItem.status === 'APPROVED'" style="color: #579df8">
                      <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                    </span>
                    <span class="grid-content-status-span" v-else-if="scope.row.cloudTaskItem.status === 'PROCESSING'" style="color: #579df8">
                      <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                    </span>
                    <span class="grid-content-status-span" v-else-if="scope.row.cloudTaskItem.status === 'FINISHED'" style="color: #7ebf50">
                      <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                    </span>
                    <span class="grid-content-status-span" v-else-if="scope.row.cloudTaskItem.status === 'ERROR'" style="color: red;">
                      <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                    </span>
                    <span class="grid-content-status-span" v-else-if="scope.row.cloudTaskItem.status === 'WARNING'" style="color: #dda450">
                      <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                    </span>
                </div>
                </el-col>
              </el-row>
            </div>
            <div>
                <el-button plain size="small" @click="showInformation(scope.row, $t('resource.i18n_resource_scanning_log'))">
                  <i class="el-icon-tickets"></i> {{ $t('resource.i18n_resource_scanning_log') }}
                </el-button>
                <el-button plain size="small" @click="showInformation(scope.row, $t('resource.i18n_resource_scanning_api'))">
                  <i class="el-icon-document-checked"></i> {{ $t('resource.i18n_resource_scanning_api') }}
                </el-button>
                <el-button plain size="small" @click="showInformation(scope.row, $t('resource.i18n_resource_scanning_return'))">
                  <i class="el-icon-document"></i> {{ $t('resource.i18n_resource_scanning_return') }}
                </el-button>
                <el-button plain size="small" @click="copy(scope.row.cloudTaskItem)" style="display: none;">
                  <i class="el-icon-document-copy"></i> {{ $t('task.execute') }}
                </el-button>
            </div>
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
              <span v-for="(logItem, index) in scope.row.cloudTaskItemLogList" :key="index"
                    v-bind:class="{true: 'color-red', false: ''}[logItem.result == false]">
                    {{ logItem.createTime | timestampFormatDate }}
                    {{ logItem.operator }}
                    {{ logItem.operation }}
                    {{ logItem.output }}<br>
              </span>
              <div v-if="(scope.row.cloudTaskItem.status === 'FINISHED' || scope.row.cloudTaskItem.status === 'ERROR' || scope.row.cloudTaskItem.status === 'WARNING')
                      && (scope.row.cloudTaskItemLogList.length === 0)">
                {{ $t('resource.the_k8s_platform') }}
                {{ scope.row.cloudTaskItem.accountLabel }} |
                {{ scope.row.cloudTaskItem.regionName }}
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
import {cloudTaskLogByIdUrl} from "@/api/cloud/account/account";
import {resourceLogUrl} from "@/api/cloud/resource/resource";

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
        this.$get(cloudTaskLogByIdUrl + showLogTaskId, response => {
          for (let obj of response.data) {
            for (let item of this.row.cloudTaskItemLogDTOs) {
              if (obj.cloudTaskItem.id === item.cloudTaskItem.id) {
                item.cloudTaskItem.status = obj.cloudTaskItem.status;
                item.cloudTaskItemLogList = obj.cloudTaskItemLogList;
              }
            }
          }
        });
      },
      //是否是结束状态，返回false代表都在运行中，true代表已结束
      checkStatus (cloudTaskItemLogDTOs) {
        let sum = 0;
        for (let row of cloudTaskItemLogDTOs) {
          if (row.cloudTaskItem.status != 'ERROR' && row.cloudTaskItem.status != 'FINISHED' && row.cloudTaskItem.status != 'WARNING') {
            sum++;
          }
        }
        return sum == 0;
      },
      innerDrawerClose() {
        this.innerDrawer = false;
      },
      showInformation(item, title) {
        this.$post(resourceLogUrl, item.cloudTaskItem, response => {
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
      copy(row) {
        let input = document.createElement("input");
        document.body.appendChild(input);
        input.value = row['command'];
        input.select();
        if (input.setSelectionRange) {
          input.setSelectionRange(0, input.value.length);
        }
        document.execCommand("copy");
        document.body.removeChild(input);
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
