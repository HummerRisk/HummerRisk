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
              <li><i>Report Generated On</i>: Fri, 22 Jul 2022 04:26:45 +0800</li>
              <li><i>Dependencies Scanned</i>:&nbsp;21 (21 unique)</li>
              <li><i>Vulnerable Dependencies</i>:&nbsp;<span id="vulnerableCount">14</span></li>
              <li><i>Vulnerabilities Found</i>:&nbsp;45</li>
              <li><i>Vulnerabilities Suppressed</i>:&nbsp;0</li>
              <li><i>NVD CVE Checked</i>: 2022-07-22T04:26:34</li>
              <li><i>NVD CVE Modified</i>: 2022-07-22T04:00:01</li>
              <li><i>VersionCheckOn</i>: 2022-07-03T20:50:46</li>
            </ul>
          </div>
        </el-row>
        <el-row :gutter="20" style="margin: 15px;">
          <div style="margin: 10px 0 0 0;">
            <el-collapse v-model="activeNames">
              <!-- 云账号 start -->
              <el-collapse-item name="1">
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
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                        </el-button>
                        <el-button plain size="medium" type="primary" v-else-if="scope.row.status === 'APPROVED'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                        </el-button>
                        <el-button plain size="medium" type="primary" v-else-if="scope.row.status === 'PROCESSING'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
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
                  <div style="margin: 10px 0 0 0;">
                    <el-card class="box-card">
                      <div slot="header" class="clearfix">
                        <div class="icon-title">
                          <span>{{ 'H' }}</span>
<!--                          <span>{{ currentUser.name.substring(0, 1) }}</span>-->
                        </div>
                        <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>
                      </div>
                      <div class="text item">

                      </div>
                      <div class="text item">

                      </div>
                    </el-card>
                  </div>
                </div>
              </el-collapse-item>
              <!-- 云账号 end -->
              <!-- 漏洞检测 start -->
              <el-collapse-item name="2">
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
                             &nbsp;&nbsp; {{ $t(scope.row.accountName) }}
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
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                        </el-button>
                        <el-button plain size="medium" type="primary" v-else-if="scope.row.status === 'APPROVED'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                        </el-button>
                        <el-button plain size="medium" type="primary" v-else-if="scope.row.status === 'PROCESSING'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
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
                        <div class="icon-title">
                          <span>{{ historyVulnResource.severity.substring(0, 1) }}</span>
                        </div>
                        <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>
                      </div>
                      <div class="text item">

                      </div>
                      <div class="text item">

                      </div>
                    </el-card>
                  </div>
                </div>
              </el-collapse-item>
              <!-- 漏洞检测 end -->
              <!-- 虚拟机 start -->
              <el-collapse-item name="3">
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
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                        </el-button>
                        <el-button plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                        </el-button>
                        <el-button plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
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
              <el-collapse-item name="4">
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
                  <div style="margin: 10px 0 0 0;">
                    <h3>Vuln:&nbsp;</h3>
                    <el-table :data="historyImageReport.imageGrypeTableList" border stripe style="width: 100%">
                      <el-table-column type="index" min-width="5%"/>
                      <el-table-column :label="'Name'" min-width="15%" prop="name">
                      </el-table-column>
                      <el-table-column :label="'Installed'" min-width="15%" prop="installed">
                      </el-table-column>
                      <el-table-column min-width="10%" :label="'FixedIn'" prop="fixedIn">
                      </el-table-column>
                      <el-table-column min-width="10%" :label="'Type'" prop="type">
                      </el-table-column>
                      <el-table-column min-width="15%" :label="'Vulnerability'" prop="vulnerability">
                      </el-table-column>
                      <el-table-column min-width="15%" :label="'Severity'" prop="severity">
                      </el-table-column>
                    </el-table>
                  </div>
                  <div style="margin: 10px 0 0 0;">
                    <h3>Sbom:&nbsp;</h3>
                    <el-table :data="historyImageReport.imageSyftTableList" border stripe style="width: 100%">
                      <el-table-column type="index" min-width="5%"/>
                      <el-table-column :label="'Name'" min-width="35%" prop="name">
                      </el-table-column>
                      <el-table-column :label="'Version'" min-width="35%" prop="version">
                      </el-table-column>
                      <el-table-column min-width="20%" :label="'Type'" prop="type">
                      </el-table-column>
                    </el-table>
                  </div>
                  <h2>Details:&nbsp;</h2>
                  <div style="margin: 10px 0 0 0;">
                    <h3>Vuln:&nbsp;</h3>
                    <div style="margin: 10px 0 0 0;" :key="imageGrypeJson.id" v-for="imageGrypeJson in historyImageReport.imageGrypeJsonList">
                      <el-card class="box-card">
                        <div slot="header" class="clearfix">
                          <div class="icon-title">
                            <span>{{ imageGrypeJson.severity.substring(0, 1) }}</span>
                          </div>
                        </div>
                        <div class="text item">

                        </div>
                        <div class="text item">

                        </div>
                      </el-card>
                    </div>
                  </div>
                </div>
              </el-collapse-item>
              <!-- 镜像检测 end -->
              <!-- 软件包检测 start -->
              <el-collapse-item name="5">
                <template slot="title">
                  {{ $t('package.package_scan') }} <i class="el-icon-box" style="margin-left: 5px;padding-top: 2px;"></i>
                </template>
                <div v-for="historyPackageReport in report.historyPackageReportDTOList" :key="historyPackageReport.id" style="border-style:ridge;padding: 15px;">
                  <h2>Summary:&nbsp;</h2>
                  <ul style="margin-left: 60px;">
                    <li><i>Scan Name</i>: {{ historyPackageReport.name }}</li>
                    <li><i>Package Name</i>: {{ historyPackageReport.packageName }}</li>
                    <li><i>Package Size</i>:&nbsp;{{ historyPackageReport.size }}</li>
                    <li><i>Rule Name</i>: {{ historyPackageReport.ruleDesc }}</li>
                    <li><i>Scan User</i>:&nbsp;{{ historyPackageReport.userName }}</li>
                    <li><i>Severity</i>:&nbsp;{{ historyPackageReport.severity }}</li>
                    <li><i>Create Time</i>:&nbsp;{{ historyPackageReport.createTime | timestampFormatDate }}</li>
                    <li><i>Result Status</i>:&nbsp;{{ historyPackageReport.resultStatus }}</li>
                    <li><i>Vulnerabilities Found</i>: {{ historyPackageReport.returnSum }}</li>
                  </ul>
                  <div style="margin: 10px 0 0 0;">
                    <h3>Vuln:&nbsp;</h3>
                    <el-table :data="historyPackageReport.packageDependencyJsonList" border stripe style="width: 100%">
                      <el-table-column type="index" min-width="5%"/>
                      <el-table-column :label="'FileName'" min-width="20%" prop="fileName">
                      </el-table-column>
                      <el-table-column :label="'FilePath'" min-width="25%" prop="filePath">
                      </el-table-column>
                      <el-table-column min-width="5%" :label="'IsVirtual'" prop="isVirtual">
                      </el-table-column>
                      <el-table-column min-width="10%" :label="'Md5'" prop="md5">
                      </el-table-column>
                      <el-table-column min-width="15%" :label="'Sha1'" prop="sha1">
                      </el-table-column>
                      <el-table-column min-width="15%" :label="'Sha256'" prop="sha256">
                      </el-table-column>
                    </el-table>
                  </div>
                  <div style="margin: 10px 0 0 0;">
                    <h2>Details:&nbsp;</h2>
                    <div class="filter-wrapper">
                      <el-input
                        class="search"
                        type="text"
                        size="small"
                        :placeholder="$t('task.filter_okr')"
                        prefix-icon="el-icon-search"
                        maxlength="60"
                        v-model="filterText" clearable/>
                    </div>
                  </div>
                  <div v-for="packageDependencyJson in historyPackageReport.packageDependencyJsonList" :key="packageDependencyJson.id">
                    <h3>Vuln:&nbsp;</h3>
                    <ul style="margin-left: 60px;">
                      <li><i>FileName</i>: {{ packageDependencyJson.fileName }}</li>
                      <li><i>FilePath</i>: {{ packageDependencyJson.filePath }}</li>
                      <li><i>IsVirtual</i>:&nbsp;{{ packageDependencyJson.isVirtual }}</li>
                      <li><i>Md5</i>: {{ packageDependencyJson.md5 }}</li>
                      <li><i>Sha1</i>:&nbsp;{{ packageDependencyJson.sha1 }}</li>
                      <li><i>Sha256</i>:&nbsp;{{ packageDependencyJson.sha256 }}</li>
                    </ul>
                    <div style="margin: 10px;">
                      <vue-okr-tree
                        ref="tree"
                        :data="packageDependencyJson.vulnerabilities | packageDependencyJsonRight"
                        :left-data="packageDependencyJson.vulnerabilities | packageDependencyJsonLeft"
                        only-both-tree
                        direction="horizontal"
                        show-collapsable
                        node-key="id"
                        label-class-name='no-padding'
                        default-expand-all
                        :render-content="renderContent"
                        :filter-node-method="filterNode"
                      ></vue-okr-tree>
                    </div>
                  </div>
                </div>
              </el-collapse-item>
              <!-- 软件包检测 end -->
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
      activeNames: ['1','2','3','4','5'],
      version: 'v1.0.0',
      report: {},
      tagSelect: [],
      resourceTypes: [],
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
          console.log(this.report)
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
      this.exportDom();
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
      console.log('111', h, node)
      if (node.level === 1) {
        return (
          <div class={cls}>
            <div class="diy-con-name">
              Package Scan: {node.data.name}
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
    }
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

