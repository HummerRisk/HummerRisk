<template>
  <div v-loading="result.loading">

    <el-card>

      <div class="filter-wrapper">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-select v-model="selectedTask" filterable clearable :placeholder="$t('task.please_filter_report')" style="width: 50%;" @change="search">
              <el-option
                v-for="item in tasks"
                :key="item.id"
                :label="item.taskName"
                :value="item">
              </el-option>
            </el-select>
            <el-button type="primary" plain icon="el-icon-download" @click="genReport"> {{ $t('task.download_report') }}</el-button>
          </el-col>
          <el-col :span="12" v-if="selectedTask">
            <el-row :gutter="20">
              <el-col :span="8" style="padding: 12px 0;color: #00bb00;">
                {{ $t('task.task_name') }} : {{ selectedTask.taskName }}
              </el-col>
              <el-col :span="8" style="padding: 12px 0;color: #00bb00;">
                {{ $t('commons.create_time') }} : <i class="el-icon-time"></i> {{ selectedTask.createTime | timestampFormatDate }}
              </el-col>
              <el-col :span="8">
                <el-button plain type="success" icon="el-icon-success">
                  {{ $t('resource.i18n_done') }}
                </el-button>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </div>

      <el-divider><i class="el-icon-tickets"></i></el-divider>

      <div ref="taskReport">
        <el-row :gutter="20">
          <el-image src="https://company.hummercloud.com/hummririsk/logo-dark.png" style="width: 40%;margin: 15px;"></el-image>
        </el-row>
        <el-row :gutter="20" style="color: #888;margin: 15px;">
          {{ $t('task.report_desc') }}
        </el-row>
        <el-row :gutter="20" style="margin: 15px;font-size: 20px;">
          <el-link type="primary" style="color: #000be9;font-size: 20px;" href="https://github.com/HummerRisk/HummerRisk" target="_blank">{{ 'Github Project' }}</el-link> |
          <el-link type="primary" style="color: #000be9;font-size: 20px;" href="https://docs.hummerrisk.com/" target="_blank">{{ 'HummerRisk Docs' }}</el-link> |
          <el-link type="primary" style="color: #000be9;font-size: 20px;" href="https://github.com/HummerRisk/HummerRisk/issues" target="_blank">{{ 'Getting Help: Github issues' }}</el-link>
        </el-row>
        <el-row :gutter="20" style="margin: 15px;">
          <h2>Project:&nbsp;</h2>
          <div style="margin: 10px 0 0 0;">
            Scan Information:<br/>
            <ul style="margin-left: 60px;">
              <li><i>HummerRisk version</i>: {{ version }}</li>
              <li><i>Report Generated On</i>: {{ report.lastFireTime | timestampFormatDate }}</li>
              <li><i>Apply User</i>:&nbsp;{{ report.applyUser }}</li>
              <li><i>Task Name</i>:&nbsp;{{ report.taskName }}</li>
              <li><i>Description</i>:&nbsp;{{ report.description }}</li>
              <li><i>Task Status</i>:&nbsp;{{ report.status }}</li>
            </ul>
          </div>
        </el-row>
        <el-row :gutter="20" style="margin: 15px;">
          <div style="margin: 10px 0 0 0;">
            <el-collapse v-model="activeNames">
              <!-- 云账号 start -->
              <el-collapse-item name="1" v-if="report.historyCloudTaskDTOList && JSON.stringify(report.historyCloudTaskDTOList) !== '[]'">
                <template slot="title">
                  {{ $t('account.account_setting') }} <i class="el-icon-cloudy" style="margin-left: 5px;padding-top: 3px;"></i>
                </template>
                <div>
                  <h2>Summary:&nbsp;</h2>
                  <div style="margin: 10px 0 0 0;">
                    <el-table :data="report.historyCloudTaskDTOList" border stripe style="width: 100%">
                      <el-table-column type="index" min-width="2%"/>
                      <el-table-column v-slot:default="scope" :label="$t('resource.i18n_task_type')" min-width="15%" show-overflow-tooltip>
                        <span>
                          <template v-for="tag in tagSelect">
                            <span :key="tag.value" v-if="scope.row.ruleTags">
                              <span :key="tag.tagKey" v-if="scope.row.ruleTags.indexOf(tag.tagKey) > -1"> {{ tag.tagName }}</span>
                            </span>
                          </template>
                          <span v-if="!!scope.row.resourceTypes && scope.row.resourceTypes.indexOf('.')===-1"> {{ scope.row.resourceTypes }}</span>
                          <span v-if="!!scope.row.resourceTypes && scope.row.resourceTypes.indexOf('.')>-1">
                            <template v-for="type in resourceTypes" >
                              <span :key="type.value" v-if="scope.row.resourceTypes">
                                <span :key="type.value" v-if="scope.row.resourceTypes.indexOf(type.value) > -1"> [{{ type.value }}]</span>
                              </span>
                            </template>
                          </span>
                        </span>
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('rule.rule_name')" min-width="20%" show-overflow-tooltip>
                        <el-link type="primary" :underline="false" class="md-primary text-click">
                          {{ scope.row.taskName }}
                        </el-link>
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('account.creator')" min-width="6%" show-overflow-tooltip>
                        {{ scope.row.applyUser }}
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('rule.severity')" min-width="8%" :sort-by="['HighRisk', 'MediumRisk', 'LowRisk']" prop="severity" :sortable="true"  show-overflow-tooltip>
                        <span v-if="scope.row.severity == 'HighRisk'" style="color: #f84846;"> {{ $t('rule.HighRisk') }}</span>
                        <span v-else-if="scope.row.severity == 'MediumRisk'" style="color: #fe9636;"> {{ $t('rule.MediumRisk') }}</span>
                        <span v-else-if="scope.row.severity == 'LowRisk'" style="color: #4dabef;"> {{ $t('rule.LowRisk') }}</span>
                        <span v-else> N/A</span>
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('resource.status')" min-width="10%" prop="status" sortable show-overflow-tooltip>
                        <el-button plain size="medium" type="primary" v-if="scope.row.status === 'UNCHECKED'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                        </el-button>
                        <el-button plain size="medium" type="primary" v-else-if="scope.row.status === 'APPROVED'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                        </el-button>
                        <el-button plain size="medium" type="primary" v-else-if="scope.row.status === 'PROCESSING'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                        </el-button>
                        <el-button plain size="medium" type="success" v-else-if="scope.row.status === 'FINISHED'">
                          <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                        </el-button>
                        <el-button plain size="medium" type="danger" v-else-if="scope.row.status === 'ERROR'">
                          <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                        </el-button>
                        <el-button plain size="medium" type="warning" v-else-if="scope.row.status === 'WARNING'">
                          <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                        </el-button>
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="6%">
                        <el-tooltip class="item" effect="dark" :content="$t('history.resource_result')" placement="top">
                          <span v-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
                          <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum == 0)">
                            {{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}
                          </span>
                          <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)">
                            <el-link type="primary" class="text-click" @click="goResource(scope.row)">{{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}</el-link>
                          </span>
                        </el-tooltip>
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('resource.status_on_off')" prop="returnSum" sortable show-overflow-tooltip min-width="8%">
                        <span v-if="scope.row.returnSum == 0" style="color: #46ad59;">{{ $t('resource.i18n_compliance_true') }}</span>
                        <span v-else-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)" style="color: #f84846;">{{ $t('resource.i18n_compliance_false') }}</span>
                        <span v-else-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
                      </el-table-column>
                      <el-table-column prop="createTime" min-width="14%" :label="$t('account.update_time')" sortable show-overflow-tooltip>
                        <template v-slot:default="scope">
                          <span>{{ scope.row.createTime | timestampFormatDate }}</span>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </div>
                <div style="margin: 10px 0 0 0;">
                  <h2>Details:&nbsp;</h2>
                  <div style="margin: 10px 0 0 0;" :key="historyCloudResource.id" v-for="historyCloudResource in report.historyCloudResourceReportDTOList">
                    <el-card class="box-card">
                      <div slot="header" class="clearfix">
                        <el-row>
                          <el-col class="icon-title" :span="3">
                            <span>{{ historyCloudResource.severity.substring(0, 1) }}</span>
                          </el-col>
                          <el-col :span="15" style="margin: -7px 0 0 15px;">
                            <span style="font-size: 24px;font-weight: 500;">{{ historyCloudResource.resourceName }}</span>
                            <span style="font-size: 20px;color: #888;margin-left: 5px;"> - {{ historyCloudResource.resourceType }}</span>
                          </el-col>
                          <el-col :span="6" style="float: right;">
                            <span style="font-size: 20px;color: #999;float: right;">{{ 'RESOURCE' }}</span>
                          </el-col>
                        </el-row>
                        <el-row style="font-size: 18px;padding: 10px;">
                          <el-col :span="20">
                            <span style="color: #888;margin: 5px;">{{ 'VULNERABILITY' }}</span>
                            <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                            <span style="margin: 5px;">{{ historyCloudResource.regionName }} | {{ historyCloudResource.regionId }}</span>
                            <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                            <span style="margin: 5px;"><el-button type="danger" size="mini">{{ historyCloudResource.severity }}</el-button></span>
                            <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                            <span style="color: #444;margin: 5px;">CREATE TIME: {{ historyCloudResource.updateTime | timestampFormatDate }}</span>
                          </el-col>
                          <el-col :span="4" style="float: right;">
                            <span style="font-size: 20px;color: #000;float: right;">{{ historyCloudResource.returnSum }}/{{ historyCloudResource.resourcesSum }}</span>
                          </el-col>
                        </el-row>

                        <div class="text div-json" v-if="JSON.stringify(historyCloudResource.resourceItemList) !== '[]'">
                          <el-descriptions :column="2" v-for="(resourceItem, index) in historyCloudResource.resourceItemList" :key="index" :title="'Vulnerability' + (index+1)">
                            <el-descriptions-item v-for="(vuln, index) in filterJson(resourceItem.resource)" :key="index" :label="vuln.key">
                              <span v-if="!vuln.flag" show-overflow-tooltip>
                                <el-tooltip class="item" effect="dark" :content="JSON.stringify(vuln.value)" placement="top-start">
                                  <el-link type="primary" style="color: #0000e4;">{{ 'Details' }}</el-link>
                                </el-tooltip>
                              </span>
                              <el-tooltip v-if="vuln.flag && vuln.value" class="item" effect="light" :content="typeof(vuln.value) === 'boolean'?vuln.value.toString():vuln.value" placement="top-start">
                                <span class="table-expand-span-value">
                                    {{ vuln.value }}
                                </span>
                              </el-tooltip>
                              <span v-if="vuln.flag && !vuln.value"> N/A</span>
                            </el-descriptions-item>
                          </el-descriptions>
                        </div>
                      </div>
                    </el-card>
                  </div>
                </div>
              </el-collapse-item>
              <!-- 云账号 end -->
              <!-- 漏洞检测 start -->
              <el-collapse-item name="2" v-if="report.historyVulnTaskDTOList && JSON.stringify(report.historyVulnTaskDTOList) !== '[]'">
                <template slot="title">
                  {{ $t('vuln.vuln_setting') }} <i class="el-icon-crop" style="margin-left: 5px;padding-top: 2px;"></i>
                </template>
                <div>
                  <h2>Summary:&nbsp;</h2>
                  <div style="margin: 10px 0 0 0;">
                    <el-table :data="report.historyVulnTaskDTOList" border stripe style="width: 100%">
                      <el-table-column prop="name" :label="$t('vuln.name')" min-width="10%" show-overflow-tooltip>
                        <template v-slot:default="scope">
                          <span>
                            <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                             &nbsp;&nbsp; {{ scope.row.accountName }}
                          </span>
                        </template>
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('resource.i18n_task_type')" min-width="9%" show-overflow-tooltip>
                        <span>
                          <template v-for="tag in tagSelect">
                            <span :key="tag.value" v-if="scope.row.ruleTags">
                              <span :key="tag.tagKey" v-if="scope.row.ruleTags.indexOf(tag.tagKey) > -1"> {{ tag.tagName }}</span>
                            </span>
                          </template>
                          <span v-if="!!scope.row.resourceTypes && scope.row.resourceTypes.indexOf('.')===-1"> {{ scope.row.resourceTypes }}</span>
                          <span v-if="!!scope.row.resourceTypes && scope.row.resourceTypes.indexOf('.')>-1">
                            <template v-for="type in resourceTypes" >
                              <span :key="type.value" v-if="scope.row.resourceTypes">
                                <span :key="type.value" v-if="scope.row.resourceTypes.indexOf(type.value) > -1"> [{{ type.value }}]</span>
                              </span>
                            </template>
                          </span>
                        </span>
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('rule.rule_name')" min-width="12%" show-overflow-tooltip>
                        <el-link type="primary" :underline="false" class="md-primary text-click" @click="showTaskDetail(scope.row)">
                          {{ scope.row.taskName }}
                        </el-link>
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('account.creator')" min-width="6%" show-overflow-tooltip>
                        {{ scope.row.applyUser }}
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('rule.severity')" min-width="8%" :sort-by="['HighRisk', 'MediumRisk', 'LowRisk']" prop="severity" :sortable="true"  show-overflow-tooltip>
                        <span v-if="scope.row.severity == 'HighRisk'" style="color: #f84846;"> {{ $t('rule.HighRisk') }}</span>
                        <span v-else-if="scope.row.severity == 'MediumRisk'" style="color: #fe9636;"> {{ $t('rule.MediumRisk') }}</span>
                        <span v-else-if="scope.row.severity == 'LowRisk'" style="color: #4dabef;"> {{ $t('rule.LowRisk') }}</span>
                        <span v-else> N/A</span>
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('resource.status')" min-width="12%" prop="status" sortable show-overflow-tooltip>
                        <el-button plain size="medium" type="primary" v-if="scope.row.status === 'UNCHECKED'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                        </el-button>
                        <el-button plain size="medium" type="primary" v-else-if="scope.row.status === 'APPROVED'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                        </el-button>
                        <el-button plain size="medium" type="primary" v-else-if="scope.row.status === 'PROCESSING'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                        </el-button>
                        <el-button plain size="medium" type="success" v-else-if="scope.row.status === 'FINISHED'">
                          <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                        </el-button>
                        <el-button plain size="medium" type="danger" v-else-if="scope.row.status === 'ERROR'">
                          <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                        </el-button>
                        <el-button plain size="medium" type="warning" v-else-if="scope.row.status === 'WARNING'">
                          <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                        </el-button>
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="6%">
                        <el-tooltip class="item" effect="dark" :content="$t('history.resource_result')" placement="top">
                          <span v-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
                          <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum == 0)">
                            {{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}
                          </span>
                          <span v-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)">
                            <el-link type="primary" class="text-click" @click="goResource(scope.row)">{{ scope.row.returnSum }}/{{ scope.row.resourcesSum }}</el-link>
                          </span>
                        </el-tooltip>
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('resource.status_on_off')" prop="returnSum" sortable show-overflow-tooltip min-width="8%">
                        <span v-if="scope.row.returnSum == 0" style="color: #46ad59;">{{ $t('resource.i18n_compliance_true') }}</span>
                        <span v-else-if="(scope.row.returnSum != null) && (scope.row.returnSum > 0)" style="color: #f84846;">{{ $t('resource.i18n_compliance_false') }}</span>
                        <span v-else-if="scope.row.returnSum == null && scope.row.resourcesSum == null"> N/A</span>
                      </el-table-column>
                      <el-table-column prop="createTime" min-width="13%" :label="$t('account.update_time')" sortable show-overflow-tooltip>
                        <template v-slot:default="scope">
                          <span>{{ scope.row.createTime | timestampFormatDate }}</span>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </div>
                <div>
                  <h2>Details:&nbsp;</h2>
                  <div style="margin: 10px 0 0 0;" :key="historyVulnResource.id" v-for="historyVulnResource in report.historyVulnResourceReportDTOList">
                    <el-card class="box-card">
                      <div slot="header" class="clearfix">
                        <el-row>
                          <el-col class="icon-title" :span="3">
                            <span>{{ historyVulnResource.severity.substring(0, 1) }}</span>
                          </el-col>
                          <el-col :span="15" style="margin: -7px 0 0 15px;">
                            <span style="font-size: 24px;font-weight: 500;">{{ historyVulnResource.resourceName }}</span>
                            <span style="font-size: 20px;color: #888;margin-left: 5px;"> - {{ historyVulnResource.resourceType }}</span>
                          </el-col>
                          <el-col :span="6" style="float: right;">
                            <span style="font-size: 20px;color: #999;float: right;">{{ 'RESOURCE' }}</span>
                          </el-col>
                        </el-row>
                        <el-row style="font-size: 18px;padding: 10px;">
                          <el-col :span="20">
                            <span style="color: #888;margin: 5px;">{{ 'VULNERABILITY' }}</span>
                            <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                            <span style="margin: 5px;">{{ historyVulnResource.regionName }}</span>
                            <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                            <span style="margin: 5px;"><el-button type="danger" size="mini">{{ historyVulnResource.severity }}</el-button></span>
                            <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                            <span style="color: #444;margin: 5px;">CREATE TIME: {{ historyVulnResource.updateTime | timestampFormatDate }}</span>
                          </el-col>
                          <el-col :span="4" style="float: right;">
                            <span style="font-size: 20px;color: #000;float: right;">{{ historyVulnResource.returnSum }}/{{ historyVulnResource.resourcesSum }}</span>
                          </el-col>
                        </el-row>

                        <div class="text div-json" v-if="JSON.stringify(historyVulnResource.resourceItemList) !== '[]'">
                          <el-descriptions :column="2" v-for="(resourceItem, index) in historyVulnResource.resourceItemList" :key="index" :title="'Vulnerability' + (index+1)">
                            <el-descriptions-item v-for="(vuln, index) in filterJson(resourceItem.resource)" :key="index" :label="vuln.key">
                              <span v-if="!vuln.flag" show-overflow-tooltip>
                                <el-tooltip class="item" effect="dark" :content="JSON.stringify(vuln.value)" placement="top-start">
                                  <el-link type="primary" style="color: #0000e4;">{{ 'Details' }}</el-link>
                                </el-tooltip>
                              </span>
                              <el-tooltip v-if="vuln.flag && vuln.value" class="item" effect="light" :content="typeof(vuln.value) === 'boolean'?vuln.value.toString():vuln.value" placement="top-start">
                                <span class="table-expand-span-value">
                                    {{ vuln.value }}
                                </span>
                              </el-tooltip>
                              <span v-if="vuln.flag && !vuln.value"> N/A</span>
                            </el-descriptions-item>
                          </el-descriptions>
                        </div>
                      </div>
                    </el-card>
                  </div>
                </div>
              </el-collapse-item>
              <!-- 漏洞检测 end -->
              <!-- 虚拟机 start -->
              <el-collapse-item name="3" v-if="report.historyServerTaskList && JSON.stringify(report.historyServerTaskList) !== '[]'">
                <template slot="title">
                  {{ $t('server.server_setting') }} <i class="el-icon-monitor" style="margin-left: 5px;padding-top: 2px;"></i>
                </template>
                <div>
                  <h2>Details:&nbsp;</h2>
                  <div style="margin: 10px 0 0 0;">
                    <el-table :data="report.historyServerTaskList" border stripe style="width: 100%">
                      <el-table-column type="index" min-width="3%"/>
                      <el-table-column prop="serverGroupName" :label="$t('server.server_group_name')" min-width="11%" show-overflow-tooltip></el-table-column>
                      <el-table-column prop="serverName" :label="$t('server.server_name')" min-width="11%" show-overflow-tooltip></el-table-column>
                      <el-table-column prop="ip" :label="'IP'" min-width="10%" show-overflow-tooltip></el-table-column>
                      <el-table-column prop="ruleName" :label="$t('server.rule_name')" min-width="11%" show-overflow-tooltip></el-table-column>
                      <el-table-column min-width="8%" :label="$t('server.severity')" column-key="severity">
                        <template v-slot:default="{row}">
                          <severity-type :row="row"/>
                        </template>
                      </el-table-column>
                      <el-table-column v-slot:default="scope" :label="$t('server.result_status')" min-width="15%" prop="resultStatus" sortable show-overflow-tooltip>
                        <el-button plain size="medium" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                        </el-button>
                        <el-button plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                        </el-button>
                        <el-button plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                        </el-button>
                        <el-button plain size="medium" type="success" v-else-if="scope.row.resultStatus === 'FINISHED'">
                          <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                        </el-button>
                        <el-button plain size="medium" type="danger" v-else-if="scope.row.resultStatus === 'ERROR'">
                          <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                        </el-button>
                        <el-button plain size="medium" type="warning" v-else-if="scope.row.resultStatus === 'WARNING'">
                          <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                        </el-button>
                      </el-table-column>
                      <el-table-column prop="updateTime" min-width="20%" :label="$t('server.last_modified')" sortable>
                        <template v-slot:default="scope">
                          <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </div>
              </el-collapse-item>
              <!-- 虚拟机 end -->
              <!-- 镜像检测 start -->
              <el-collapse-item name="4" v-if="report.historyImageReportDTOList && JSON.stringify(report.historyImageReportDTOList) !== '[]'">
                <template slot="title">
                  {{ $t('image.image_scan') }} <i class="el-icon-picture-outline" style="margin-left: 5px;padding-top: 2px;"></i>
                </template>
                <div v-for="historyImageReport in report.historyImageReportDTOList" :key="historyImageReport.id" style="border-style:ridge;padding: 15px;">
                  <h2>Summary:&nbsp;</h2>
                  <ul style="margin-left: 60px;">
                    <li><i>Scan Name</i>: {{ historyImageReport.name }}</li>
                    <li><i>Rule Name</i>: {{ historyImageReport.ruleDesc }}</li>
                    <li><i>Scan User</i>:&nbsp;{{ historyImageReport.userName }}</li>
                    <li><i>Severity</i>:&nbsp;{{ historyImageReport.severity }}</li>
                    <li><i>Create Time</i>:&nbsp;{{ historyImageReport.createTime | timestampFormatDate }}</li>
                    <li><i>Result Status</i>:&nbsp;{{ historyImageReport.resultStatus }}</li>
                    <li><i>Vulnerabilities Found</i>: {{ historyImageReport.returnSum }}</li>
                  </ul>
                  <h2>Details:&nbsp;</h2>
                  <el-tabs>
                    <el-tab-pane label="Vuln" name="imageGrypeJson">
                      <div style="margin: 10px 0 0 0;">
                        <el-collapse v-model="activeNames">
                          <el-collapse-item title="Vuln:" name="6">
                            <div style="margin: 10px 0 0 0;" :key="imageJson.id" v-for="imageJson in historyImageReport.imageTrivyJsonWithBLOBsList">
                              <el-card class="box-card">
                                <div style="margin: 10px;" v-if="imageJson.trivyJson">
                                  <h2>Summary:&nbsp;</h2>
                                  <ul style="margin-left: 60px;">
                                    <li><i>Scan Name</i>: {{ imageJson.name }}</li>
                                    <li><i>Scan User</i>:&nbsp;{{ imageJson.userName }}</li>
                                    <li><i>ArtifactType</i>:&nbsp;{{ imageJson.trivyJson.ArtifactType }}</li>
                                    <li><i>ArtifactName</i>:&nbsp;{{ imageJson.trivyJson.ArtifactName }}</li>
                                    <li><i>SchemaVersion</i>:&nbsp;{{ imageJson.trivyJson.SchemaVersion }}</li>
                                    <li><i>Architecture</i>:&nbsp;{{ imageJson.trivyJson.Metadata.ImageConfig.architecture }}</li>
                                    <li><i>Create Time</i>:&nbsp;{{ imageJson.createTime | timestampFormatDate }}</li>
                                    <li><i>Result Status</i>:&nbsp;{{ imageJson.resultStatus }}</li>
                                    <li><i>Vulnerabilities Found</i>: {{ imageJson.returnSum }}</li>
                                  </ul>
                                </div>
                                <div style="margin: 10px;" v-if="imageJson.trivyJson">
                                  <div style="margin: 10px 0 0 0;">
                                    <h2>Details:&nbsp;</h2>
                                    <div style="margin: 10px 0 0 0;">
                                      <div style="margin: 10px 0 0 0;" :key="index" v-for="(result, index) in imageJson.trivyJson.Results">
                                        <div style="margin: 10px;" v-if="result">
                                          <h3>Summary:&nbsp;</h3>
                                          <ul style="margin-left: 60px;">
                                            <li><i>Target</i>: {{ result.Target }}</li>
                                            <li><i>Class</i>:&nbsp;{{ result.Class }}</li>
                                            <li><i>Type</i>:&nbsp;{{ result.Type }}</li>
                                          </ul>
                                        </div>
                                        <div style="margin: 10px 0 0 0;" :key="index" v-for="(vulnerabilitiy, index) in result.Vulnerabilities">
                                          <el-card class="box-card">
                                            <div slot="header" class="clearfix">
                                              <el-row>
                                                <el-col class="icon-title" :span="3">
                                                  <span>{{ vulnerabilitiy.Severity.substring(0, 1) }}</span>
                                                </el-col>
                                                <el-col :span="15" style="margin: -7px 0 0 15px;">
                                                  <span style="font-size: 24px;font-weight: 500;">{{ vulnerabilitiy.Title }}</span>
                                                </el-col>
                                                <el-col :span="6" style="float: right;">
                                                  <span style="font-size: 20px;color: #999;float: right;">{{ 'SEVERITY SOURCE' }}</span>
                                                </el-col>
                                              </el-row>
                                              <el-row style="font-size: 18px;padding: 10px;">
                                                <el-col :span="20">
                                                  <span style="margin: 5px;"><a :href="vulnerabilitiy.PrimaryURL">{{ vulnerabilitiy.VulnerabilityID }}</a></span>
                                                  <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                                  <span style="margin: 5px;"><el-button type="danger" size="mini">{{ vulnerabilitiy.Severity }}</el-button></span>
                                                  <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                                  <span style="color: #888;margin: 5px;">INSTALLED VERSION: {{ vulnerabilitiy.InstalledVersion }}</span>
                                                  <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                                  <span style="color: #444;margin: 5px;">PkgName: {{ vulnerabilitiy.PkgName }}</span>
                                                </el-col>
                                                <el-col :span="4" style="float: right;">
                                                  <span style="font-size: 20px;color: #000;float: right;">{{ vulnerabilitiy.SeveritySource }}</span>
                                                </el-col>
                                              </el-row>
                                            </div>
                                            <div class="text item div-desc">
                                              <el-row>
                                                <span style="color: red;"><i class="el-icon-s-opportunity"></i>PrimaryURL:</span> &nbsp;{{ vulnerabilitiy.PrimaryURL }}
                                              </el-row>
                                              <el-row>
                                                <span style="color: red;">Description:</span> {{ vulnerabilitiy.Description }}
                                              </el-row>
                                              <el-row>
                                                <span style="color: red;">PublishedDate:</span> {{ vulnerabilitiy.PublishedDate }}
                                              </el-row>
                                              <el-row>
                                                <span style="color: red;">LastModifiedDate:</span> {{ vulnerabilitiy.LastModifiedDate }}
                                              </el-row>
                                              <el-row>
                                                <span style="color: red;">FixedVersion:</span> {{ vulnerabilitiy.FixedVersion }}
                                              </el-row>
                                              <el-row>
                                                <span style="color: red;">DataSource:</span> {{ vulnerabilitiy.DataSource.ID }} | {{ vulnerabilitiy.DataSource.Name }} | {{ vulnerabilitiy.DataSource.URL }}
                                              </el-row>
                                            </div>
                                            <div class="text div-json">
                                              <el-descriptions title="Layer" :column="2">
                                                <el-descriptions-item v-for="(vuln, index) in filterJson(vulnerabilitiy.Layer)" :key="index" :label="vuln.key">
                          <span v-if="!vuln.flag" show-overflow-tooltip>
                            <el-tooltip class="item" effect="dark" :content="JSON.stringify(vuln.value)" placement="top-start">
                              <el-link type="primary" style="color: #0000e4;">{{ 'Details' }}</el-link>
                            </el-tooltip>
                          </span>
                                                  <el-tooltip v-if="vuln.flag && vuln.value" class="item" effect="light" :content="typeof(vuln.value) === 'boolean'?vuln.value.toString():vuln.value" placement="top-start">
                            <span class="table-expand-span-value">
                                {{ vuln.value }}
                            </span>
                                                  </el-tooltip>
                                                  <span v-if="vuln.flag && !vuln.value"> N/A</span>
                                                </el-descriptions-item>
                                              </el-descriptions>
                                            </div>
                                            <div class="text div-json">
                                              <el-descriptions title="CweIDs" :column="2">
                                                <el-descriptions-item v-for="(CweID, index) in vulnerabilitiy.CweIDs" :key="index" :label="index">
                                                  <span> {{ CweID }}</span>
                                                </el-descriptions-item>
                                              </el-descriptions>
                                            </div>
                                            <div class="text div-json">
                                              <el-descriptions title="References" :column="2">
                                                <el-descriptions-item v-for="(Reference, index) in vulnerabilitiy.References" :key="index" :label="index">
                                                  <span> {{ Reference }}</span>
                                                </el-descriptions-item>
                                              </el-descriptions>
                                            </div>
                                          </el-card>
                                        </div>
                                      </div>
                                    </div>
                                  </div>
                                </div>
                              </el-card>
                            </div>
                          </el-collapse-item>
                        </el-collapse>
                      </div>
                    </el-tab-pane>
                    <el-tab-pane label="Sbom" name="imageSyftJson">
                      <div style="margin: 10px 0 0 0;">
                        <el-collapse-item title="Sbom:" name="7">
                          <div style="margin: 10px 0 0 0;" :key="index" v-for="(imageSyftJson,index) in historyImageReport.imageSyftJsonList">
                            <el-card class="box-card">
                              <div slot="header" class="clearfix">
                                <el-row>
                                  <el-col :span="15" style="margin: -7px 0 0 15px;">
                                    <span style="font-size: 24px;font-weight: 500;">{{ imageSyftJson.name }}</span>
                                    <span style="font-size: 20px;color: #888;margin-left: 5px;">- {{ imageSyftJson.version?imageSyftJson.version:'N/A' }}</span>
                                  </el-col>
                                  <el-col :span="8" style="float: right;">
                                    <span style="font-size: 20px;color: #999;float: right;">{{ 'LANGUAGE' }}</span>
                                  </el-col>
                                </el-row>
                                <el-row style="font-size: 18px;padding: 10px;">
                                  <el-col :span="20">
                                    <span style="color: #888;margin: 5px;">{{ 'SBOM' }}</span>
                                    <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                    <span style="color: #444;margin: 5px;">TYPE: {{ imageSyftJson.type }}</span>
                                    <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                    <span style="color: #444;margin: 5px;">FOUNDBY: {{ imageSyftJson.foundBy }}</span>
                                    <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                    <span style="color: #444;margin: 5px;">METADATA TYPE: {{ imageSyftJson.metadataType }}</span>
                                  </el-col>
                                  <el-col :span="4" style="float: right;">
                                    <span style="font-size: 20px;color: #000;float: right;">{{ imageSyftJson.language }}</span>
                                  </el-col>
                                </el-row>
                                <el-row style="font-size: 18px;padding: 10px;">
                                  <span style="color: #444;margin: 5px;">PURL: {{ imageSyftJson.purl }}</span>
                                </el-row>
                              </div>
                              <div class="text div-json">
                                <el-descriptions title="Metadata" :column="2">
                                  <el-descriptions-item v-for="(meta, index) in filterJson(imageSyftJson.metadata)" :key="index" :label="meta.key">
                                    <span v-if="!meta.flag" show-overflow-tooltip>
                                      <el-tooltip class="item" effect="dark" :content="JSON.stringify(meta.value)" placement="top-start">
                                        <el-link type="primary" style="color: #0000e4;">{{ 'Details' }}</el-link>
                                      </el-tooltip>
                                    </span>
                                    <el-tooltip v-if="meta.flag && meta.value" class="item" effect="light" :content="typeof(meta.value) === 'boolean'?meta.value.toString():meta.value" placement="top-start">
                                    <span class="table-expand-span-value">
                                        {{ meta.value }}
                                    </span>
                                    </el-tooltip>
                                    <span v-if="meta.flag && !meta.value"> N/A</span>
                                  </el-descriptions-item>
                                </el-descriptions>
                              </div>
                              <div class="text div-json">
                                <el-descriptions title="Locations" :column="2">
                                  <el-descriptions-item v-for="(location, index) in JSON.parse(imageSyftJson.locations)" :key="index" :label="location.path">
                                    <span> {{ location.layerID }}</span>
                                  </el-descriptions-item>
                                  <el-descriptions-item label="Licenses">
                                    <span> {{ imageSyftJson.licenses }}</span>
                                  </el-descriptions-item>
                                  <el-descriptions-item label="Cpes">
                                    <span> {{ imageSyftJson.cpes }}</span>
                                  </el-descriptions-item>
                                </el-descriptions>
                              </div>
                            </el-card>
                          </div>
                        </el-collapse-item>
                      </div>
                    </el-tab-pane>
                  </el-tabs>
                </div>
              </el-collapse-item>
              <!-- 镜像检测 end -->
              <!-- 源码检测 start -->
              <el-collapse-item name="10" v-if="report.historyCodeReportDTOList && JSON.stringify(report.historyCodeReportDTOList) !== '[]'">
                <template slot="title">
                  {{ $t('code.code_scan') }} <i class="el-icon-box" style="margin-left: 5px;padding-top: 2px;"></i>
                </template>
                <div v-for="historycodeReport in report.historyCodeReportDTOList" :key="historycodeReport.id" style="border-style:ridge;padding: 15px;">
                  <h2>Summary:&nbsp;</h2>
                  <ul style="margin-left: 60px;">
                    <li><i>Scan Name</i>: {{ historycodeReport.name }}</li>
                    <li><i>Scan User</i>:&nbsp;{{ historycodeReport.userName }}</li>
                    <li><i>Rule Name</i>: {{ historycodeReport.ruleDesc }}</li>
                    <li><i>Severity</i>:&nbsp;{{ historycodeReport.severity }}</li>
                    <li><i>Create Time</i>:&nbsp;{{ historycodeReport.createTime | timestampFormatDate }}</li>
                    <li><i>Result Status</i>:&nbsp;{{ historycodeReport.resultStatus }}</li>
                    <li><i>Vulnerabilities Found</i>: {{ historycodeReport.returnSum }}</li>
                  </ul>
                  <div style="margin: 10px 0 0 0;" v-if="JSON.stringify(historycodeReport.codeResultItemList) !== '[]'">
                    <h3>Vuln:&nbsp;</h3>
                    <el-table :data="historycodeReport.codeResultItemList" border stripe style="width: 100%">
                      <el-table-column type="index" min-width="2%"/>
                      <el-table-column :label="'Title'" min-width="17%" prop="title">
                      </el-table-column>
                      <el-table-column :label="'InstalledVersion'" min-width="10%" prop="installedVersion">
                      </el-table-column>
                      <el-table-column min-width="7%" :label="'PkgName'" prop="pkgName">
                      </el-table-column>
                      <el-table-column min-width="10%" :label="'VulnerabilityID'" prop="vulnerabilityId">
                      </el-table-column>
                      <el-table-column min-width="7%" :label="'Severity'" prop="severity">
                      </el-table-column>
                      <el-table-column min-width="10%" :label="'SeveritySource'" prop="severitySource">
                      </el-table-column>
                      <el-table-column min-width="12%" :label="'PrimaryURL'" prop="primaryUrl" v-slot:default="scope">
                        <el-link type="primary" style="color: #0000e4;" :href="scope.row.primaryUrl" target="_blank">{{ scope.row.primaryUrl }}</el-link>
                      </el-table-column>
                    </el-table>
                  </div>
                  <div style="margin: 10px;" v-if="historycodeReport.returnJson">
                    <div style="margin: 10px 0 0 0;">
                      <h2>Details:&nbsp;</h2>
                      <div style="margin: 10px 0 0 0;">
                        <div style="margin: 10px 0 0 0;" :key="index" v-for="(result, index) in JSON.parse(historycodeReport.returnJson).Results">
                          <div style="margin: 10px;" v-if="result">
                            <h3>Summary:&nbsp;</h3>
                            <ul style="margin-left: 60px;">
                              <li><i>Target</i>: {{ result.Target }}</li>
                              <li><i>Class</i>:&nbsp;{{ result.Class }}</li>
                              <li><i>Type</i>:&nbsp;{{ result.Type }}</li>
                            </ul>
                          </div>
                          <div style="margin: 10px 0 0 0;" :key="index" v-for="(vulnerabilitiy, index) in result.Vulnerabilities">
                            <el-card class="box-card">
                              <div slot="header" class="clearfix">
                                <el-row>
                                  <el-col class="icon-title" :span="3">
                                    <span>{{ vulnerabilitiy.Severity.substring(0, 1) }}</span>
                                  </el-col>
                                  <el-col :span="15" style="margin: -7px 0 0 15px;">
                                    <span style="font-size: 24px;font-weight: 500;">{{ vulnerabilitiy.Title }}</span>
                                  </el-col>
                                  <el-col :span="6" style="float: right;">
                                    <span style="font-size: 20px;color: #999;float: right;">{{ 'SEVERITY SOURCE' }}</span>
                                  </el-col>
                                </el-row>
                                <el-row style="font-size: 18px;padding: 10px;">
                                  <el-col :span="20">
                                    <span style="margin: 5px;"><a :href="vulnerabilitiy.PrimaryURL">{{ vulnerabilitiy.VulnerabilityID }}</a></span>
                                    <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                    <span style="margin: 5px;"><el-button type="danger" size="mini">{{ vulnerabilitiy.Severity }}</el-button></span>
                                    <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                    <span style="color: #888;margin: 5px;">INSTALLED VERSION: {{ vulnerabilitiy.InstalledVersion }}</span>
                                    <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                    <span style="color: #444;margin: 5px;">PkgName: {{ vulnerabilitiy.PkgName }}</span>
                                  </el-col>
                                  <el-col :span="4" style="float: right;">
                                    <span style="font-size: 20px;color: #000;float: right;">{{ vulnerabilitiy.SeveritySource }}</span>
                                  </el-col>
                                </el-row>
                              </div>
                              <div class="text item div-desc">
                                <el-row>
                                  <span style="color: red;"><i class="el-icon-s-opportunity"></i>PrimaryURL:</span> &nbsp;{{ vulnerabilitiy.PrimaryURL }}
                                </el-row>
                                <el-row>
                                  <span style="color: red;">Description:</span> {{ vulnerabilitiy.Description }}
                                </el-row>
                                <el-row>
                                  <span style="color: red;">PublishedDate:</span> {{ vulnerabilitiy.PublishedDate }}
                                </el-row>
                                <el-row>
                                  <span style="color: red;">LastModifiedDate:</span> {{ vulnerabilitiy.LastModifiedDate }}
                                </el-row>
                                <el-row>
                                  <span style="color: red;">FixedVersion:</span> {{ vulnerabilitiy.FixedVersion }}
                                </el-row>
                                <el-row>
                                  <span style="color: red;">DataSource:</span> {{ vulnerabilitiy.DataSource.ID }} | {{ vulnerabilitiy.DataSource.Name }} | {{ vulnerabilitiy.DataSource.URL }}
                                </el-row>
                              </div>
                              <div class="text div-json">
                                <el-descriptions title="Layer" :column="2">
                                  <el-descriptions-item v-for="(vuln, index) in filterJson(vulnerabilitiy.Layer)" :key="index" :label="vuln.key">
                          <span v-if="!vuln.flag" show-overflow-tooltip>
                            <el-tooltip class="item" effect="dark" :content="JSON.stringify(vuln.value)" placement="top-start">
                              <el-link type="primary" style="color: #0000e4;">{{ 'Details' }}</el-link>
                            </el-tooltip>
                          </span>
                                    <el-tooltip v-if="vuln.flag && vuln.value" class="item" effect="light" :content="typeof(vuln.value) === 'boolean'?vuln.value.toString():vuln.value" placement="top-start">
                            <span class="table-expand-span-value">
                                {{ vuln.value }}
                            </span>
                                    </el-tooltip>
                                    <span v-if="vuln.flag && !vuln.value"> N/A</span>
                                  </el-descriptions-item>
                                </el-descriptions>
                              </div>
                              <div class="text div-json">
                                <el-descriptions title="CweIDs" :column="2">
                                  <el-descriptions-item v-for="(CweID, index) in vulnerabilitiy.CweIDs" :key="index" :label="index">
                                    <span> {{ CweID }}</span>
                                  </el-descriptions-item>
                                </el-descriptions>
                              </div>
                              <div class="text div-json">
                                <el-descriptions title="References" :column="2">
                                  <el-descriptions-item v-for="(Reference, index) in vulnerabilitiy.References" :key="index" :label="index">
                                    <span> {{ Reference }}</span>
                                  </el-descriptions-item>
                                </el-descriptions>
                              </div>
                            </el-card>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </el-collapse-item>
              <!-- 源码检测 end -->
            </el-collapse>
          </div>
        </el-row>
      </div>

    </el-card>

  </div>
</template>

<script>
//OKR树
import {VueOkrTree} from 'vue-okr-tree';
import RuleType from "@/business/components/task/home/RuleType";
import SeverityType from "@/business/components/task/home/SeverityType";
/* eslint-disable */
export default {
  components: {
    VueOkrTree,
    RuleType,
    SeverityType,
  },
  data () {
    return {
      result: {},
      loading: false,
      filterText: '',
      tasks: [],
      selectedTask: null,
      activeNames: ['1','2','3','4','5','6','7','8','9', '10'],
      version: 'v1.0.0',
      report: {},
      tagSelect: [],
      resourceTypes: [],
      filterJson: this.filterJsonKeyAndValue,
      imageJsonActiveName: 'imageTrivyJson',
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    }
  },
  methods: {
    init() {
      this.result = this.$get("/task/allTaskList", response => {
        this.tasks = response.data;
      });
      this.initSelect();
    },
    async initSelect () {
      this.tagSelect = [];
      await this.$get("/tag/rule/list", response => {
        this.tagSelect = response.data;
      });
      this.resourceTypes = [];
      await this.$get("/rule/all/resourceTypes", response => {
        for (let item of response.data) {
          let typeItem = {};
          typeItem.value = item.name;
          typeItem.label = item.name;
          this.resourceTypes.push(typeItem);
        }
      });
    },
    search() {
      if (this.selectedTask) {
        this.result = this.$get("/task/report/" + this.selectedTask.id, response => {
          this.report = response.data;
        });
      }
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    genReport() {
      if(!this.selectedTask) {
        this.$warning(this.$t('task.no_selected_task'));
        return;
      }
      // this.exportDom();
    },
    renderContent (h, node) {
      const cls = ['diy-wrapper']
      if (node.isCurrent) {
        cls.push('current-select')
      }
      if (node.isLeftChild) {
        cls.push('left-child')
      }
      let spanLeft = 8;
      let spanRight = 16;
      if (node.level === 1) {
        return (
          <div class={cls}>
            <div class="diy-con-name">
              Scan: {node.data.name}
            </div>
            <div class="diy-con-content">
              Description: {node.data.description}
            </div>
          </div>
        )
      } else {
        return (
          <div class={cls}>
            <div class="diy-con-name">
              <el-row>
                <el-col span={spanLeft} class="diy-con-left">Name</el-col>
                <el-col span={spanRight} class="diy-con-right-cve">{node.data.name}</el-col>
              </el-row>
            </div>
            <div class="diy-con-content">
              <el-row>
                <el-col span={spanLeft} class="diy-con-left">Severity</el-col>
                <el-col span={spanRight} class="diy-con-right">{node.data.severity}</el-col>
              </el-row>
            </div>
            <div class="diy-con-name">
              <el-row>
                <el-col span={spanLeft} class="diy-con-left">Source</el-col>
                <el-col span={spanRight} class="diy-con-right">{node.data.source}</el-col>
              </el-row>
            </div>
            <div class="diy-con-content">
              <el-row>
                <el-col span={spanLeft} class="diy-con-left">Notes</el-col>
                <el-col span={spanRight} class="diy-con-right">{!!node.data.description?node.data.description.substr(0,60):'N/A'}</el-col>
              </el-row>
            </div>
          </div>
        )
      }
    },
    //相关获取 dom 元素的方法
    html() {
      const temp = [
        '<!doctype html>',
        '<html>',
        '<head>',
        window.document.head.innerHTML,
        '</head>',
        '<body>',
        this.$refs.taskReport.innerHTML,
        '</body>',
        '</html>'
      ];
      return temp.join('');
    },
    //构造html页面，并使用 createObjectURL构造一个文件流并下载
    exportDom() {
      const link = document.createElement('a');
      document.body.appendChild(link);
      const url = URL.createObjectURL(new Blob([this.html()], { type: "text/plain;charset='utf-8'" }));
      link.href = url;
      link.download = 'HummerRisk-Report' + '.html';
      link.click();
      window.URL.revokeObjectURL(url);
    },
    getVersion() {
      this.$get('/system/version', response => {
        this.version = response.data;
      });
    },
    filterJsonKeyAndValue(json) {
      //json is json object , not array -- harris
      let list = json;
      if(typeof json === 'object') {
        list = json;
      } else {
        list = JSON.parse(json);
      }

      let jsonKeyAndValue = [];

      for (let item in list) {
        let flag = true;
        let value = list[item];
        //string && boolean的值直接显示, object是[{}]
        if (typeof (value) === 'number') {
          value = String(value);
        }
        if (typeof (value) === 'object') {
          if (value !== null && JSON.stringify(value) !== '[]' && JSON.stringify(value) !== '{}') {
            flag = false;
          }
          if (JSON.stringify(value) === '[]' || JSON.stringify(value) === '{}') {
            value = "";
          }
        }

        if (item.indexOf('$$') === -1 && item !== 'show') {
          let map = {key: item, value: value, flag: flag};
          jsonKeyAndValue.push(map);
        }
      }
      return jsonKeyAndValue;
    },
  },
  activated() {
    this.init();
    this.getVersion();
  },
}
</script>

<style scoped>
.el-card >>> .label-class-blue {
  color: #1989fa;
}
.el-card >>> .label-bg-blue {
  background: #1989fa;
  color: #fff;
}
.el-card >>> .diy-wrapper {
  padding:10px
}
.el-card >>> .no-padding {
  padding: 0 !important;
}
.diy-wrapper >>> .left-child {
  border: 1px solid red;
}
.el-card >>> .org-chart-node-label-inner {
  border-style: solid;
  border-left-color: #ff0000;
  border-left-width: 5px;
  border-right-color:#fff;
  border-top-color:#fff;
  border-bottom-color:#fff;
}

.el-card >>> .diy-con-name {
  margin: 8px 3px;
}

.el-card >>> .diy-con-content {
  margin: 8px 3px;
}

.el-card >>> .diy-con-left {
  text-align: left;
  color: tomato;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
  font-size: 14px;
}

.el-card >>> .diy-con-right {
  text-align: right;
  color: #888888;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
  font-size: 12px;
}

.el-card >>> .diy-con-right-cve {
  text-align: right;
  color: #32CD32;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
  cursor:pointer;
  font-size: 12px;
}

.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}

.box-card >>> .el-card__header {
  background-color: aliceblue;
}

.div-desc {
  background-color: #ecebf5;
  color: blueviolet;
  padding: 15px;
}

.div-json {
  padding: 15px;
}

.box-card {
  width: 99%;
  border-top-color: #ff0000;
  border-top-width: 5px;
}

.icon-title {
  color: #fff;
  width: 30px;
  background-color: #32CD32;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border-radius: 30px;
  font-size: 14px;
}

</style>

