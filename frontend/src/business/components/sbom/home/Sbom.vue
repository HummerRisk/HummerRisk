<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <el-row :gutter="20">
        <el-col :span="6" style="max-height: 468px;">
          <el-card class="box-card" style="max-height: 468px;">
            <div slot="header" class="clearfix">
              <span>{{ $t('sbom.applications') }} <i class="el-icon-info"/></span>
            </div>
            <main-container class="box-dev">
              <slot name="header"></slot>
              <div>
                <slot name="header">
                  <el-input prefix-icon="el-icon-search" @change="search" :placeholder="$t('sbom.applications_search')" v-model="filterText" size="small" :clearable="true"/>
                </slot>
                <el-collapse accordion>
                  <el-collapse-item v-for="(application,index) in this.applications" :key="index">
                    <template slot="title">
                      {{index+1}} <i class="header-icon el-icon-folder-opened"></i>  {{ application.name }}
                    </template>
                    <el-table border :data="application.sbomVersionList" class="adjust-table table-content" :show-header="false"
                              style="cursor:pointer;" @row-click="selectVersion">
                      <el-table-column show-overflow-tooltip>
                        <template v-slot:default="scope">
                          <span style="cursor:pointer; color: #215d9a;" >
                             <i class="el-icon-s-grid" style="color: #0a7be0"></i>  {{ scope.row.name }}
                          </span>
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </main-container>
          </el-card>
        </el-col>
        <el-col :span="18" style="max-height: 468px;padding: 0;">
          <el-card class="box-card" style="max-height: 468px;">
            <template v-slot:header>
              <div class="clearfix">
                <el-col :span="24">
                  <span style="color: #215d9a;">
                     <i class="el-icon-s-grid"></i>  {{ sbomVersion?sbomVersion.sbomName:'N/A' }} ({{ sbomVersion?sbomVersion.sbomDesc:'N/A' }}) -
                    <span style="color: #0a7be0;">{{ sbomVersion?sbomVersion.name:'N/A' }}</span>
                  </span>
                </el-col>
              </div>
            </template>
            <el-col :span="12" style="margin: 15px 0 15px 0;">
              <span style="color: #13161d;">
                 <i class="el-icon-time"></i>  {{ $t('account.create_time') }} :
                <span style="color: #888888;">{{ (sbomVersion?sbomVersion.createTime:'N/A') | timestampFormatDate }}</span>
              </span>
            </el-col>
            <el-col :span="12" style="margin: 15px 0 15px 0;">
              <span style="color: #13161d;">
                 <i class="el-icon-postcard"></i>  {{ $t('sbom.version_desc') }} :
                <span style="color: #888888;">{{ sbomVersion?sbomVersion.description:'N/A' }}</span>
              </span>
            </el-col>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-tabs type="card" @tab-click="filterTag">
                  <el-tab-pane
                    :key="tag.id"
                    v-for="tag in tags"
                    :label="tag.id">
                    <span slot="label"><i :class="tag.icon"></i> {{ tag.name }}</span>
                  </el-tab-pane>
                </el-tabs>
              </el-col>
            </el-row>

            <!-- code start -->
            <el-table border v-if="tag=='code'" :data="codeData" class="adjust-table table-content" stripe style="min-height: 317px;cursor:pointer;" max-height="318" @row-click="selectVuln">
              <el-table-column type="index" min-width="1%"/>
              <el-table-column prop="name" :label="$t('code.name')" min-width="10%" show-overflow-tooltip>
                <template v-slot:default="scope">
                  <span>
                    <img :src="require(`@/assets/img/code/${scope.row.pluginIcon}`)" style="width: 16px; height: 12px; vertical-align:middle" alt=""/>
                     &nbsp;&nbsp; {{ scope.row.name }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="6%">
                <el-tooltip effect="dark" :content="$t('history.result')" placement="top">
                  <el-link type="primary" class="text-click" @click="showCodeResource(scope.row)">{{ scope.row.returnSum }}</el-link>
                </el-tooltip>
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('image.result_status')" min-width="12%" prop="resultStatus" sortable show-overflow-tooltip>
                <el-button @click="showCodeResultLog(scope.row)" plain size="mini" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </el-button>
                <el-button @click="showCodeResultLog(scope.row)" plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </el-button>
                <el-button @click="showCodeResultLog(scope.row)" plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </el-button>
                <el-button @click="showCodeResultLog(scope.row)" plain size="mini" type="success" v-else-if="scope.row.resultStatus === 'FINISHED'">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </el-button>
                <el-button @click="showCodeResultLog(scope.row)" plain size="mini" type="danger" v-else-if="scope.row.resultStatus === 'ERROR'">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </el-button>
                <el-button @click="showCodeResultLog(scope.row)" plain size="mini" type="warning" v-else-if="scope.row.resultStatus === 'WARNING'">
                  <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                </el-button>
              </el-table-column>
              <el-table-column prop="updateTime" min-width="15%" :label="$t('image.last_modified')" sortable>
                <template v-slot:default="scope">
                  <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
                </template>
              </el-table-column>
              <el-table-column min-width="12%" v-slot:default="scope" :label="$t('commons.operating')">
                <download :params="scope.row"/>
              </el-table-column>
            </el-table>

            <!-- code Result detail-->
            <el-drawer class="rtl" :title="$t('code.result_details_list')" :visible.sync="codeVisible" size="85%" :before-close="handleClose" :direction="direction"
                       :destroy-on-close="true">
              <code-result-details :id="codeResultId"/>
              <template v-slot:footer>
                <dialog-footer
                  @cancel="codeVisible = false"
                  @confirm="codeVisible = false"/>
              </template>
            </el-drawer>
            <!-- code Result detail-->

            <!-- code Result log-->
            <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logCodeVisible" size="85%" :before-close="handleClose" :direction="direction"
                       :destroy-on-close="true">
              <el-row class="el-form-item-dev" v-if="logCodeData.length == 0">
                <span>{{ $t('resource.i18n_no_data') }}<br></span>
              </el-row>
              <el-row class="el-form-item-dev" v-if="logCodeData.length > 0">
                <div>
                  <el-row>
                    <el-col :span="24">
                      <div class="grid-content bg-purple-light">
                        <span class="grid-content-log-span"> {{ logCodeForm.name }}</span>
                        <span class="grid-content-log-span">
                          <img :src="require(`@/assets/img/platform/docker.png`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                         &nbsp;&nbsp; {{ logCodeForm.imageName }}
                        </span>
                                  <span class="grid-content-status-span" v-if="logCodeForm.resultStatus === 'APPROVED'" style="color: #579df8">
                          <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                        </span>
                                  <span class="grid-content-status-span" v-else-if="logCodeForm.resultStatus === 'FINISHED'" style="color: #7ebf50">
                          <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                        </span>
                                  <span class="grid-content-status-span" v-else-if="logCodeForm.resultStatus === 'ERROR'" style="color: red;">
                          <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                        </span>
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <el-table :show-header="false" :data="logCodeData" class="adjust-table table-content">
                  <el-table-column>
                    <template v-slot:default="scope">
                      <div class="bg-purple-div">
                        <span
                          v-bind:class="{true: 'color-red', false: ''}[scope.row.result == false]">
                              {{ scope.row.createTime | timestampFormatDate }}
                              {{ scope.row.operator }}
                              {{ scope.row.operation }}
                              {{ scope.row.output }}<br>
                        </span>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
                <div style="margin: 10px;" v-if="logCodeForm.returnJson">
                  <h2>Summary:&nbsp;</h2>
                  <ul style="margin-left: 60px;">
                    <li><i>Scan Name</i>: {{ logCodeForm.name }}</li>
                    <li><i>Scan User</i>:&nbsp;{{ logCodeForm.userName }}</li>
                    <li><i>ArtifactType</i>:&nbsp;{{ logCodeForm.returnJson.ArtifactType }}</li>
                    <li><i>ArtifactName</i>:&nbsp;{{ logCodeForm.returnJson.ArtifactName }}</li>
                    <li><i>SchemaVersion</i>:&nbsp;{{ logCodeForm.returnJson.SchemaVersion }}</li>
                    <li><i>Architecture</i>:&nbsp;{{ logCodeForm.returnJson.Metadata.ImageConfig.architecture }}</li>
                    <li><i>Create Time</i>:&nbsp;{{ logCodeForm.createTime | timestampFormatDate }}</li>
                    <li><i>Result Status</i>:&nbsp;{{ logCodeForm.resultStatus }}</li>
                    <li><i>Vulnerabilities Found</i>: {{ logCodeForm.returnSum }}</li>
                  </ul>
                </div>
                <div style="margin: 10px;" v-if="logCodeForm.returnJson">
                  <div style="margin: 10px 0 0 0;">
                    <h2>Details:&nbsp;</h2>
                    <div style="margin: 10px 0 0 0;">
                      <div style="margin: 10px 0 0 0;" :key="index" v-for="(result, index) in logCodeForm.returnJson.Results">
                        <div style="margin: 10px;" v-if="result">
                          <h3>Summary:&nbsp;</h3>
                          <ul style="margin-left: 60px;">
                            <li><i>Target</i>: {{ result.Target }}</li>
                            <li><i>Class</i>:&nbsp;{{ result.Class }}</li>
                            <li><i>Type</i>:&nbsp;{{ result.Type }}</li>
                          </ul>
                        </div>
                        <div style="margin: 10px 0 0 0;" :key="index" v-for="(vulnerability, index) in result.Vulnerabilities">
                          <el-card v-bind:class="{ 'box-card2 box-card-critical': vulnerability.Severity === 'CRITICAL',
                           'box-card2 box-card-high': vulnerability.Severity === 'HIGH',
                           'box-card2 box-card-medium': vulnerability.Severity === 'MEDIUM',
                           'box-card2 box-card-low': vulnerability.Severity === 'LOW',
                           'box-card2 box-card-unknown': vulnerability.Severity === 'UNKNOWN' }">
                            <div slot="header" class="clearfix">
                              <el-row>
                                <el-col v-bind:class="{ 'icon-title box-critical': vulnerability.Severity === 'CRITICAL',
                                'icon-title box-high': vulnerability.Severity === 'HIGH',
                                'icon-title box-medium': vulnerability.Severity === 'MEDIUM',
                                'icon-title box-low': vulnerability.Severity === 'LOW',
                                'icon-title box-unknown': vulnerability.Severity === 'UNKNOWN' }"
                                        :span="3">
                                  <span>{{ vulnerability.Severity.substring(0, 1) }}</span>
                                </el-col>
                                <el-col :span="15" style="margin: -7px 0 0 15px;">
                                  <span style="font-size: 24px;font-weight: 500;">{{ vulnerability.Title }}</span>
                                </el-col>
                                <el-col :span="6" style="float: right;">
                                  <span style="font-size: 20px;color: #999;float: right;">{{ 'SEVERITY SOURCE' }}</span>
                                </el-col>
                              </el-row>
                              <el-row style="font-size: 18px;padding: 10px;">
                                <el-col :span="20">
                                  <span style="margin: 5px;"><a :href="vulnerability.PrimaryURL">{{ vulnerability.VulnerabilityID }}</a></span>
                                  <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                  <span style="margin: 5px;">
                                    <el-button v-bind:class="{ 'box-critical': vulnerability.Severity === 'CRITICAL',
                                      'box-high': vulnerability.Severity === 'HIGH',
                                      'box-medium': vulnerability.Severity === 'MEDIUM', 'box-low': vulnerability.Severity === 'LOW',
                                      'box-unknown': vulnerability.Severity === 'UNKNOWN' }" size="mini">{{ vulnerability.Severity }}
                                    </el-button>
                                  </span>
                                  <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                  <span style="color: #888;margin: 5px;">INSTALLED VERSION: {{ vulnerability.InstalledVersion }}</span>
                                  <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                  <span style="color: #444;margin: 5px;">PkgName: {{ vulnerability.PkgName }}</span>
                                </el-col>
                                <el-col :span="4" style="float: right;">
                                  <span style="font-size: 20px;color: #000;float: right;">{{ vulnerability.SeveritySource }}</span>
                                </el-col>
                              </el-row>
                            </div>
                            <div class="text item div-desc">
                              <el-row>
                                <span style="color: red;"><i class="el-icon-s-opportunity"></i>PrimaryURL:</span> &nbsp;{{ vulnerability.PrimaryURL }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">Description:</span> {{ vulnerability.Description }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">PublishedDate:</span> {{ vulnerability.PublishedDate }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">LastModifiedDate:</span> {{ vulnerability.LastModifiedDate }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">FixedVersion:</span> {{ vulnerability.FixedVersion }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">DataSource:</span> {{ vulnerability.DataSource.ID }} | {{ vulnerability.DataSource.Name }} | {{ vulnerability.DataSource.URL }}
                              </el-row>
                            </div>
                            <div class="text div-json">
                              <el-descriptions title="Layer" :column="2">
                                <el-descriptions-item v-for="(vuln, index) in filterJson(vulnerability.Layer)" :key="index" :label="vuln.key">
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
                                <el-descriptions-item v-for="(CweID, index) in vulnerability.CweIDs" :key="index" :label="index">
                                  <span> {{ CweID }}</span>
                                </el-descriptions-item>
                              </el-descriptions>
                            </div>
                            <div class="text div-json">
                              <el-descriptions title="References" :column="2">
                                <el-descriptions-item v-for="(Reference, index) in vulnerability.References" :key="index" :label="index">
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
              </el-row>
              <template v-slot:footer>
                <dialog-footer
                  @cancel="logCodeVisible = false"
                  @confirm="logCodeVisible = false"/>
              </template>
            </el-drawer>
            <!-- code Result log-->

            <!-- code end -->

            <!-- image start -->
            <el-table border v-if="tag=='image'" :data="imageData" class="adjust-table table-content" stripe style="min-height: 317px;cursor:pointer;" max-height="318" @row-click="selectVuln">
              <el-table-column type="index" min-width="1%"/>
              <el-table-column prop="name" :label="$t('image.image_name')" min-width="10%" show-overflow-tooltip>
                <template v-slot:default="scope">
                  <span>
                    <img :src="require(`@/assets/img/platform/docker.png`)" style="width: 16px; height: 12px; vertical-align:middle" alt=""/>
                     &nbsp;&nbsp; {{ scope.row.name }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('image.image_url')" min-width="17%" show-overflow-tooltip>
                <el-row v-if="scope.row.type==='repo'">{{ scope.row.imageUrl }}:{{ scope.row.imageTag }}</el-row>
                <el-row v-if="scope.row.type==='image'">{{ scope.row.imageUrl }}:{{ scope.row.imageTag }}</el-row>
                <el-row v-if="scope.row.type==='tar'">{{ scope.row.path }}</el-row>
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="7%">
                <el-tooltip effect="dark" :content="$t('history.result')" placement="top">
                  <el-link type="primary" class="text-click" @click="showImageResource(scope.row)">{{ scope.row.returnSum }}</el-link>
                </el-tooltip>
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('image.result_status')" min-width="11%" prop="resultStatus" sortable show-overflow-tooltip>
                <el-button @click="showImageResultLog(scope.row)" plain size="mini" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </el-button>
                <el-button @click="showImageResultLog(scope.row)" plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </el-button>
                <el-button @click="showImageResultLog(scope.row)" plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </el-button>
                <el-button @click="showImageResultLog(scope.row)" plain size="mini" type="success" v-else-if="scope.row.resultStatus === 'FINISHED'">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </el-button>
                <el-button @click="showImageResultLog(scope.row)" plain size="mini" type="danger" v-else-if="scope.row.resultStatus === 'ERROR'">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </el-button>
                <el-button @click="showImageResultLog(scope.row)" plain size="mini" type="warning" v-else-if="scope.row.resultStatus === 'WARNING'">
                  <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                </el-button>
              </el-table-column>
              <el-table-column prop="updateTime" min-width="14%" :label="$t('image.last_modified')" sortable>
                <template v-slot:default="scope">
                  <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
                </template>
              </el-table-column>
              <el-table-column min-width="14%" v-slot:default="scope" :label="$t('commons.operating')">
                <download :params="scope.row"/>
              </el-table-column>
            </el-table>

            <!-- image Result detail-->
            <el-drawer class="rtl" :title="$t('image.result_details_list')" :visible.sync="imageVisible" size="85%" :before-close="handleClose" :direction="direction"
                       :destroy-on-close="true">
              <image-result-details :id="imageResultId"/>
              <template v-slot:footer>
                <dialog-footer
                  @cancel="imageVisible = false"
                  @confirm="imageVisible = false"/>
              </template>
            </el-drawer>
            <!-- image Result detail-->

            <!-- image Result log-->
            <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logImageVisible" size="85%" :before-close="handleClose" :direction="direction"
                       :destroy-on-close="true">
              <el-row class="el-form-item-dev" v-if="logImageData.length == 0">
                <span>{{ $t('resource.i18n_no_data') }}<br></span>
              </el-row>
              <el-row class="el-form-item-dev" v-if="logImageData.length > 0">
                <div>
                  <el-row>
                    <el-col :span="24">
                      <div class="grid-content bg-purple-light">
                        <span class="grid-content-log-span"> {{ logImageForm.name }}</span>
                        <span class="grid-content-log-span">
                  <img :src="require(`@/assets/img/platform/docker.png`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ logImageForm.imageName }}
                </span>
                        <span class="grid-content-status-span" v-if="logImageForm.resultStatus === 'APPROVED'" style="color: #579df8">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </span>
                        <span class="grid-content-status-span" v-else-if="logImageForm.resultStatus === 'FINISHED'" style="color: #7ebf50">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </span>
                        <span class="grid-content-status-span" v-else-if="logImageForm.resultStatus === 'ERROR'" style="color: red;">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </span>
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <el-table :show-header="false" :data="logImageData" class="adjust-table table-content">
                  <el-table-column>
                    <template v-slot:default="scope">
                      <div class="bg-purple-div">
                <span
                  v-bind:class="{true: 'color-red', false: ''}[scope.row.result == false]">
                      {{ scope.row.createTime | timestampFormatDate }}
                      {{ scope.row.operator }}
                      {{ scope.row.operation }}
                      {{ scope.row.output }}<br>
                </span>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
                <div style="margin: 10px;" v-if="logImageForm.resultJson">
                  <h2>Summary:&nbsp;</h2>
                  <ul style="margin-left: 60px;">
                    <li><i>Scan Name</i>: {{ logImageForm.name }}</li>
                    <li><i>Scan User</i>:&nbsp;{{ logImageForm.userName }}</li>
                    <li><i>ArtifactType</i>:&nbsp;{{ logImageForm.resultJson.ArtifactType }}</li>
                    <li><i>ArtifactName</i>:&nbsp;{{ logImageForm.resultJson.ArtifactName }}</li>
                    <li><i>SchemaVersion</i>:&nbsp;{{ logImageForm.resultJson.SchemaVersion }}</li>
                    <li><i>Architecture</i>:&nbsp;{{ logImageForm.resultJson.Metadata.ImageConfig.architecture }}</li>
                    <li><i>Create Time</i>:&nbsp;{{ logImageForm.createTime | timestampFormatDate }}</li>
                    <li><i>Result Status</i>:&nbsp;{{ logImageForm.resultStatus }}</li>
                    <li><i>Vulnerabilities Found</i>: {{ logImageForm.returnSum }}</li>
                  </ul>
                </div>
                <div style="margin: 10px;" v-if="logImageForm.resultJson">
                  <div style="margin: 10px 0 0 0;">
                    <h2>Details:&nbsp;</h2>
                    <div style="margin: 10px 0 0 0;">
                      <div style="margin: 10px 0 0 0;" :key="index" v-for="(result, index) in logImageForm.resultJson.Results">
                        <div style="margin: 10px;" v-if="result">
                          <h3>Summary:&nbsp;</h3>
                          <ul style="margin-left: 60px;">
                            <li><i>Target</i>: {{ result.Target }}</li>
                            <li><i>Class</i>:&nbsp;{{ result.Class }}</li>
                            <li><i>Type</i>:&nbsp;{{ result.Type }}</li>
                          </ul>
                        </div>
                        <div style="margin: 10px 0 0 0;" :key="index" v-for="(vulnerability, index) in result.Vulnerabilities">
                          <el-card v-bind:class="{ 'box-card2 box-card-critical': vulnerability.Severity === 'CRITICAL',
                           'box-card2 box-card-high': vulnerability.Severity === 'HIGH',
                           'box-card2 box-card-medium': vulnerability.Severity === 'MEDIUM',
                           'box-card2 box-card-low': vulnerability.Severity === 'LOW',
                           'box-card2 box-card-unknown': vulnerability.Severity === 'UNKNOWN' }">
                            <div slot="header" class="clearfix">
                              <el-row>
                                <el-col v-bind:class="{ 'icon-title box-critical': vulnerability.Severity === 'CRITICAL',
                                'icon-title box-high': vulnerability.Severity === 'HIGH',
                                'icon-title box-medium': vulnerability.Severity === 'MEDIUM',
                                'icon-title box-low': vulnerability.Severity === 'LOW',
                                'icon-title box-unknown': vulnerability.Severity === 'UNKNOWN' }"
                                        :span="3">
                                  <span>{{ vulnerability.Severity.substring(0, 1) }}</span>
                                </el-col>
                                <el-col :span="15" style="margin: -7px 0 0 15px;">
                                  <span style="font-size: 24px;font-weight: 500;">{{ vulnerability.Title }}</span>
                                </el-col>
                                <el-col :span="6" style="float: right;">
                                  <span style="font-size: 20px;color: #999;float: right;">{{ 'SEVERITY SOURCE' }}</span>
                                </el-col>
                              </el-row>
                              <el-row style="font-size: 18px;padding: 10px;">
                                <el-col :span="20">
                                  <span style="margin: 5px;"><a :href="vulnerability.PrimaryURL">{{ vulnerability.VulnerabilityID }}</a></span>
                                  <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                  <span style="margin: 5px;">
                                    <el-button v-bind:class="{ 'box-critical': vulnerability.Severity === 'CRITICAL',
                                      'box-high': vulnerability.Severity === 'HIGH',
                                      'box-medium': vulnerability.Severity === 'MEDIUM', 'box-low': vulnerability.Severity === 'LOW',
                                      'box-unknown': vulnerability.Severity === 'UNKNOWN' }" size="mini">{{ vulnerability.Severity }}
                                    </el-button>
                                  </span>
                                  <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                  <span style="color: #888;margin: 5px;">INSTALLED VERSION: {{ vulnerability.InstalledVersion }}</span>
                                  <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                  <span style="color: #444;margin: 5px;">PkgName: {{ vulnerability.PkgName }}</span>
                                </el-col>
                                <el-col :span="4" style="float: right;">
                                  <span style="font-size: 20px;color: #000;float: right;">{{ vulnerability.SeveritySource }}</span>
                                </el-col>
                              </el-row>
                            </div>
                            <div class="text item div-desc">
                              <el-row>
                                <span style="color: red;"><i class="el-icon-s-opportunity"></i>PrimaryURL:</span> &nbsp;{{ vulnerability.PrimaryURL }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">Description:</span> {{ vulnerability.Description }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">PublishedDate:</span> {{ vulnerability.PublishedDate }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">LastModifiedDate:</span> {{ vulnerability.LastModifiedDate }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">FixedVersion:</span> {{ vulnerability.FixedVersion }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">DataSource:</span> {{ vulnerability.DataSource.ID }} | {{ vulnerability.DataSource.Name }} | {{ vulnerability.DataSource.URL }}
                              </el-row>
                            </div>
                            <div class="text div-json">
                              <el-descriptions title="Layer" :column="2">
                                <el-descriptions-item v-for="(vuln, index) in filterJson(vulnerability.Layer)" :key="index" :label="vuln.key">
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
                                <el-descriptions-item v-for="(CweID, index) in vulnerability.CweIDs" :key="index" :label="index">
                                  <span> {{ CweID }}</span>
                                </el-descriptions-item>
                              </el-descriptions>
                            </div>
                            <div class="text div-json">
                              <el-descriptions title="References" :column="2">
                                <el-descriptions-item v-for="(Reference, index) in vulnerability.References" :key="index" :label="index">
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
              </el-row>
              <template v-slot:footer>
                <dialog-footer
                  @cancel="logImageVisible = false"
                  @confirm="logImageVisible = false"/>
              </template>
            </el-drawer>
            <!-- image Result log-->

            <!-- image end -->

            <!-- fs start -->
            <el-table border v-if="tag=='fs'" :data="fsData" class="adjust-table table-content" stripe style="min-height: 317px;cursor:pointer;" max-height="318" @row-click="selectVuln">
              <el-table-column type="index" min-width="1%"/>
              <el-table-column prop="name" :label="$t('fs.name')" min-width="10%" show-overflow-tooltip>
                <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/fs/${scope.row.pluginIcon}`)" style="width: 30px; height: 25px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
                </template>
              </el-table-column>
              <el-table-column prop="fileName" :label="$t('fs.file_name')" min-width="10%" show-overflow-tooltip>
                <template v-slot:default="scope">
                  &nbsp;&nbsp; {{ scope.row.fileName }}
                </template>
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="15%">
                <el-tooltip effect="dark" :content="$t('history.result') + ' CRITICAL:' + scope.row.critical + ' HIGH:' +  scope.row.high + ' MEDIUM:' + scope.row.medium + ' LOW:' + scope.row.low + ' UNKNOWN:' + scope.row.unknown" placement="top">
                  <el-link type="primary" class="text-click" @click="showFsResource(scope.row)">
                    {{ 'C:' + scope.row.critical + ' H:' +  scope.row.high + ' M:' + scope.row.medium + ' L:' + scope.row.low + ' U:' + scope.row.unknown}}
                  </el-link>
                </el-tooltip>
              </el-table-column>
              <el-table-column v-slot:default="scope" :label="$t('image.result_status')" min-width="12%" prop="resultStatus" sortable show-overflow-tooltip>
                <el-button @click="showFsResultLog(scope.row)" plain size="medium" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </el-button>
                <el-button @click="showFsResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </el-button>
                <el-button @click="showFsResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </el-button>
                <el-button @click="showFsResultLog(scope.row)" plain size="medium" type="success" v-else-if="scope.row.resultStatus === 'FINISHED'">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </el-button>
                <el-button @click="showFsResultLog(scope.row)" plain size="medium" type="danger" v-else-if="scope.row.resultStatus === 'ERROR'">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </el-button>
                <el-button @click="showFsResultLog(scope.row)" plain size="medium" type="warning" v-else-if="scope.row.resultStatus === 'WARNING'">
                  <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                </el-button>
              </el-table-column>
              <el-table-column prop="updateTime" min-width="14%" :label="$t('image.last_modified')" sortable>
                <template v-slot:default="scope">
                  <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
                </template>
              </el-table-column>
              <el-table-column min-width="14%" v-slot:default="scope" :label="$t('commons.operating')">
                <download :params="scope.row"/>
              </el-table-column>
            </el-table>

            <!-- fs Result detail-->
            <el-drawer class="rtl" :title="$t('fs.result_details_list')" :visible.sync="fsVisible" size="85%" :before-close="handleClose" :direction="direction"
                       :destroy-on-close="true">
              <fs-result-details :id="fsResultId"/>
              <template v-slot:footer>
                <dialog-footer
                  @cancel="fsVisible = false"
                  @confirm="fsVisible = false"/>
              </template>
            </el-drawer>
            <!-- fs Result detail-->

            <!-- fs Result log-->
            <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logFsVisible" size="85%" :before-close="handleClose" :direction="direction"
                       :destroy-on-close="true">
              <el-row class="el-form-item-dev" v-if="logFsData.length == 0">
                <span>{{ $t('resource.i18n_no_data') }}<br></span>
              </el-row>
              <el-row class="el-form-item-dev" v-if="logFsData.length > 0">
                <div>
                  <el-row>
                    <el-col :span="24">
                      <div class="grid-content bg-purple-light">
                        <span class="grid-content-log-span"> {{ logFsForm.name }}</span>
                        <span class="grid-content-log-span">
                  <img :src="require(`@/assets/img/fs/fs.png`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ logFsForm.name }}
                </span>
                        <span class="grid-content-status-span" v-if="logFsForm.resultStatus === 'APPROVED'" style="color: #579df8">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </span>
                        <span class="grid-content-status-span" v-else-if="logFsForm.resultStatus === 'FINISHED'" style="color: #7ebf50">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </span>
                        <span class="grid-content-status-span" v-else-if="logFsForm.resultStatus === 'ERROR'" style="color: red;">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </span>
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <el-table :show-header="false" :data="logImageData" class="adjust-table table-content">
                  <el-table-column>
                    <template v-slot:default="scope">
                      <div class="bg-purple-div">
                        <span
                          v-bind:class="{true: 'color-red', false: ''}[scope.row.result == false]">
                              {{ scope.row.createTime | timestampFormatDate }}
                              {{ scope.row.operator }}
                              {{ scope.row.operation }}
                              {{ scope.row.output }}<br>
                        </span>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
                <div style="margin: 10px;" v-if="logFsForm.returnJson">
                  <h2>Summary:&nbsp;</h2>
                  <ul style="margin-left: 60px;">
                    <li><i>Scan Name</i>: {{ logFsForm.name }}</li>
                    <li><i>Scan User</i>:&nbsp;{{ logFsForm.userName }}</li>
                    <li><i>ArtifactType</i>:&nbsp;{{ logFsForm.returnJson.ArtifactType }}</li>
                    <li><i>ArtifactName</i>:&nbsp;{{ logFsForm.returnJson.ArtifactName }}</li>
                    <li><i>SchemaVersion</i>:&nbsp;{{ logFsForm.returnJson.SchemaVersion }}</li>
                    <li><i>Architecture</i>:&nbsp;{{ logFsForm.returnJson.Metadata.ImageConfig?logFsForm.returnJson.Metadata.ImageConfig.architecture:'N/A' }}</li>
                    <li><i>Create Time</i>:&nbsp;{{ logFsForm.createTime | timestampFormatDate }}</li>
                    <li><i>Result Status</i>:&nbsp;{{ logFsForm.resultStatus }}</li>
                    <li><i>Vulnerabilities Found</i>: {{ logFsForm.returnSum }}</li>
                  </ul>
                </div>
                <div style="margin: 10px;" v-if="logFsForm.returnJson">
                  <div style="margin: 10px 0 0 0;">
                    <h2>Details:&nbsp;</h2>
                    <div style="margin: 10px 0 0 0;">
                      <div style="margin: 10px 0 0 0;" :key="index" v-for="(result, index) in logFsForm.returnJson.Results">
                        <div style="margin: 10px;" v-if="result">
                          <h3>Summary:&nbsp;</h3>
                          <ul style="margin-left: 60px;">
                            <li><i>Target</i>: {{ result.Target }}</li>
                            <li><i>Class</i>:&nbsp;{{ result.Class }}</li>
                            <li><i>Type</i>:&nbsp;{{ result.Type }}</li>
                          </ul>
                        </div>
                        <div style="margin: 10px 0 0 0;" :key="index" v-for="(vulnerability, index) in result.Vulnerabilities">
                          <el-card v-bind:class="{ 'box-card2 box-card-critical': vulnerability.Severity === 'CRITICAL',
                           'box-card2 box-card-high': vulnerability.Severity === 'HIGH',
                           'box-card2 box-card-medium': vulnerability.Severity === 'MEDIUM',
                           'box-card2 box-card-low': vulnerability.Severity === 'LOW',
                           'box-card2 box-card-unknown': vulnerability.Severity === 'UNKNOWN' }">
                            <div slot="header" class="clearfix">
                              <el-row>
                                <el-col v-bind:class="{ 'icon-title box-critical': vulnerability.Severity === 'CRITICAL',
                                'icon-title box-high': vulnerability.Severity === 'HIGH',
                                'icon-title box-medium': vulnerability.Severity === 'MEDIUM',
                                'icon-title box-low': vulnerability.Severity === 'LOW',
                                'icon-title box-unknown': vulnerability.Severity === 'UNKNOWN' }"
                                        :span="3">
                                  <span>{{ vulnerability.Severity.substring(0, 1) }}</span>
                                </el-col>
                                <el-col :span="15" style="margin: -7px 0 0 15px;">
                                  <span style="font-size: 24px;font-weight: 500;">{{ vulnerability.Title }}</span>
                                </el-col>
                                <el-col :span="6" style="float: right;">
                                  <span style="font-size: 20px;color: #999;float: right;">{{ 'SEVERITY SOURCE' }}</span>
                                </el-col>
                              </el-row>
                              <el-row style="font-size: 18px;padding: 10px;">
                                <el-col :span="20">
                                  <span style="margin: 5px;"><a :href="vulnerability.PrimaryURL">{{ vulnerability.VulnerabilityID }}</a></span>
                                  <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                  <span style="margin: 5px;">
                                    <el-button v-bind:class="{ 'box-critical': vulnerability.Severity === 'CRITICAL',
                                      'box-high': vulnerability.Severity === 'HIGH',
                                      'box-medium': vulnerability.Severity === 'MEDIUM', 'box-low': vulnerability.Severity === 'LOW',
                                      'box-unknown': vulnerability.Severity === 'UNKNOWN' }" size="mini">{{ vulnerability.Severity }}
                                    </el-button>
                                  </span>
                                  <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                  <span style="color: #888;margin: 5px;">INSTALLED VERSION: {{ vulnerability.InstalledVersion }}</span>
                                  <span style="color: #bbb;margin: 5px;">{{ '|' }}</span>
                                  <span style="color: #444;margin: 5px;">PkgName: {{ vulnerability.PkgName }}</span>
                                </el-col>
                                <el-col :span="4" style="float: right;">
                                  <span style="font-size: 20px;color: #000;float: right;">{{ vulnerability.SeveritySource }}</span>
                                </el-col>
                              </el-row>
                            </div>
                            <div class="text item div-desc">
                              <el-row>
                                <span style="color: red;"><i class="el-icon-s-opportunity"></i>PrimaryURL:</span> &nbsp;{{ vulnerability.PrimaryURL }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">Description:</span> {{ vulnerability.Description }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">PublishedDate:</span> {{ vulnerability.PublishedDate }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">LastModifiedDate:</span> {{ vulnerability.LastModifiedDate }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">FixedVersion:</span> {{ vulnerability.FixedVersion }}
                              </el-row>
                              <el-row>
                                <span style="color: red;">DataSource:</span> {{ vulnerability.DataSource.ID }} | {{ vulnerability.DataSource.Name }} | {{ vulnerability.DataSource.URL }}
                              </el-row>
                            </div>
                            <div class="text div-json">
                              <el-descriptions title="Layer" :column="2">
                                <el-descriptions-item v-for="(vuln, index) in filterJson(vulnerability.Layer)" :key="index" :label="vuln.key">
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
                                <el-descriptions-item v-for="(CweID, index) in vulnerability.CweIDs" :key="index" :label="index">
                                  <span> {{ CweID }}</span>
                                </el-descriptions-item>
                              </el-descriptions>
                            </div>
                            <div class="text div-json">
                              <el-descriptions title="References" :column="2">
                                <el-descriptions-item v-for="(Reference, index) in vulnerability.References" :key="index" :label="index">
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
              </el-row>
              <template v-slot:footer>
                <dialog-footer
                  @cancel="logImageVisible = false"
                  @confirm="logImageVisible = false"/>
              </template>
            </el-drawer>
            <!-- fs Result log-->

            <!-- fs end -->

          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 25px;" v-if="content">
        <el-col :span="24">
          <el-card class="box-card">
            <template v-slot:header>
              <div slot="header" class="clearfix">
                <span>{{ $t('resource.i18n_not_compliance') }}</span>
              </div>
            </template>
            <section class="report-container">
              <main>
                <metric-chart :content="content"/>
              </main>
            </section>
            <code-result-details v-if="codeResultId" :id="codeResultId"/>
            <image-result-details v-if="imageResultId" :id="imageResultId"/>
            <fs-result-details v-if="fsResultId" :id="fsResultId"/>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </main-container>
</template>

<script>
import MainContainer from "@/business/components/common/components/MainContainer";
import Download from "@/business/components/sbom/home/Download";
import CodeResultDetails from "@/business/components/sbom/home/CodeResultDetails";
import ImageResultDetails from "@/business/components/sbom/home/ImageResultDetails";
import FsResultDetails from "@/business/components/sbom/home/FsResultDetails";
import MetricChart from "@/business/components/common/chart/MetricChart";

/* eslint-disable */
export default {
  components: {
    MainContainer,
    Download,
    CodeResultDetails,
    ImageResultDetails,
    FsResultDetails,
    MetricChart,
  },
  data() {
    return {
      result: {},
      loading: false,
      filterText: "",
      applications: [],
      sbomVersion: {},
      tags: [
        {id: 'code', name: this.$t('sbom.target_code'), icon: 'iconfont icon-yuandaimaxiayoudaima'},
        {id: 'image', name: this.$t('sbom.target_image'), icon: 'iconfont icon-jingxiang2'},
        {id: 'fs', name: this.$t('sbom.target_fs'), icon: 'iconfont icon-wendang'},
      ],
      tag: 'code',
      codeData: [],
      imageData: [],
      fsData: [],
      direction: 'rtl',
      codeVisible: false,
      imageVisible: false,
      fsVisible: false,
      logCodeVisible: false,
      logImageVisible: false,
      logFsVisible: false,
      codeResultId: '',
      imageResultId: '',
      fsResultId: '',
      logCodeData: {},
      logCodeForm: [],
      logImageData: {},
      logImageForm: [],
      logFsData: {},
      logFsForm: [],
      filterJson: this.filterJsonKeyAndValue,
      content: {
        critical: 0,
        high: 0,
        medium: 0,
        low: 0,
        unknown: 0,
        total: 0,
      },
    }
  },
  methods: {
    search() {
      let params = {
        name: this.filterText
      };
      this.result = this.$post("/sbom/applications", params, response => {
        if (response.success) {
          this.applications = response.data;
          for(let sbom of this.applications) {
            for(let version of sbom.sbomVersionList) {
              this.sbomVersion = version;
              break;
            }
            break;
          }
          this.searchScan();
        } else {
          this.$error(response.message);
        }
      });
    },
    filterTag (tag) {
      this.tag = tag.label;
    },
    searchScan() {
      if(this.sbomVersion) {
        this.$get("/sbom/historyCodeResult/" + this.sbomVersion.id, response => {
          this.codeData = response.data;
        });
        this.$get("/sbom/historyImageResult/" + this.sbomVersion.id, response => {
          this.imageData = response.data;
        });
        this.$get("/sbom/historyFsResult/" + this.sbomVersion.id, response => {
          this.fsData = response.data;
        });
      }
    },
    handleClick(item) {
    },
    selectVersion(item) {
      this.sbomVersion = item;
      this.searchScan();
    },
    showCodeResource(item) {
      this.codeResultId = item.id;
      this.codeVisible = true;
    },
    showImageResource(item) {
      this.imageResultId = item.id;
      this.imageVisible = true;
    },
    showFsResource(item) {
      this.fsResultId = item.id;
      this.fsVisible = true;
    },
    showCodeResultLog(result) {
      let logUrl = "/sbom/codeLog/";
      this.result = this.$get(logUrl + result.id, response => {
        this.logCodeData = response.data;
      });
      let resultUrl = "/sbom/getCodeResult/";
      this.result = this.$get(resultUrl + result.id, response => {
        this.logCodeForm = response.data;
        this.logCodeForm.returnJson = JSON.parse(this.logCodeForm.returnJson);
      });
      this.logCodeVisible = true;
    },
    showImageResultLog(result) {
      let logUrl = "/sbom/imageLog/";
      this.result = this.$get(logUrl + result.id, response => {
        this.logImageData = response.data;
      });
      let resultUrl = "/image/getImageResultWithBLOBs/";
      this.result = this.$get(resultUrl + result.id, response => {
        this.logImageForm = response.data;
        this.logImageForm.resultJson = JSON.parse(this.logImageForm.resultJson);
      });
      this.logImageVisible = true;
    },
    showFsResultLog(result) {
      let logUrl = "/fs/log/";
      this.result = this.$get(logUrl + result.id, response => {
        this.logFsData = response.data;
      });
      let resultUrl = "/fs/getFsResult/";
      this.result = this.$get(resultUrl + result.id, response => {
        this.logFsForm = response.data;
        this.logFsForm.returnJson = JSON.parse(this.logFsForm.returnJson);
      });
      this.logFsVisible = true;
    },
    handleClose() {
      this.codeVisible = false;
      this.imageVisible = false;
      this.logCodeVisible = false;
      this.logImageVisible = false;
      this.fsVisible = false;
      this.logFsVisible = false;
    },
    selectVuln(item) {
      this.codeResultId = "";
      this.imageResultId = "";
      this.fsResultId = "";
      if(item.codeId) {
        this.codeResultId = item.id;
        this.result = this.$get("/sbom/codeMetricChart/"+ this.codeResultId, response => {
          this.content = response.data;
        });
      } else if(item.imageId) {
        this.imageResultId = item.id;
        this.result = this.$get("/sbom/imageMetricChart/"+ this.imageResultId, response => {
          this.content = response.data;
        });
      } else if(item.fsId) {
        this.fsResultId = item.id;
        this.result = this.$get("/sbom/fsMetricChart/"+ this.fsResultId, response => {
          this.content = response.data;
        });
      }
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
    this.search();
  }
}
</script>

<style scoped>
.box-card {
  margin: 10px 0 0 0;
}
.box-card >>> .el-card__header {
  padding: 5px 20px;
  background-color: aliceblue;
  color: #888888;
}
.box-card >>> .el-card__body {
  padding: 10px;
  min-height: 405px;
}
.rtl >>> .el-drawer__body {
  overflow-y: auto;
  padding: 0 20px 20px 20px;
}
.rtl >>> input {
  width: 100%;
}
.rtl >>> .el-select {
  width: 80%;
}
.rtl >>> .el-form-item__content {
  width: 60%;
}
.box-dev {
  max-height: 426px;
}
/deep/ :focus{outline:0;}
/deep/ .scrollbar {
  white-space: nowrap;
}
.el-scrollbar {
  display: flex;
  justify-content: space-around;
  padding: 0 10px;
}
/deep/ .el-scrollbar__wrap {
  overflow: scroll;
  width: 110%;
  height: 100%;
}
.grid-content {
  min-height: 36px;
}
.bg-purple {
  background: aliceblue;
  width: 100%;
}
.header-icon{
  margin: 0 10px 0 5px;
}
.iconfont {
  margin-right: 10px;
  width: 24px;
  height: 18px;
  text-align: center;
  font-size: 18px;
}
.main-content-box {
  padding: 0;
  margin: 10px 0 0 10px;
}
.table-content {
  width: 100%;
}
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
}
.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  padding: 10px 10%;
  width: 47%;
}
.tag-v{
  margin: 10px;
  cursor:pointer;
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
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
}

.grid-content-log-span {
  width: 39%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0 6px 2px;
  color: #606266;
}

.grid-content-status-span {
  width: 20%;float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
}
.pure-span {
  color: #606266;
  margin: 10px 0;
}

.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  padding: 10px 2%;
  width: 46%;
}

.div-desc {
  background-color: #ecebf5;
  color: blueviolet;
  padding: 15px;
}

.div-json {
  padding: 15px;
}
.box-card2 >>> .el-card__body {
  padding: 10px;
  min-height: 405px;
}
.box-card2 >>> .el-card__header {
  padding: 5px 20px;
  background-color: aliceblue;
}
.box-card2 {
  margin: 10px 0 0 0;
  width: 99%;
  border-top-width: 5px;
}
.box-card-critical {
  border-top-color: #8B0000;
}
.box-card-high {
  border-top-color: #FF4D4D;
}
.box-card-medium {
  border-top-color: #FF8000;
}
.box-card-low {
  border-top-color: #336D9F;
}
.box-card-unknown {
  border-top-color: #67C23A;
}
.box-critical {
  color: #ffffff;
  background-color: #8B0000;
}
.box-high {
  color: #ffffff;
  background-color: #FF4D4D;
}
.box-medium {
  color: #ffffff;
  background-color: #FF8000;
}
.box-low {
  color: #ffffff;
  background-color: #336D9F;
}
.box-unknown {
  color: #ffffff;
  background-color: #67C23A;
}
.icon-title {
  color: #fff;
  width: 30px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border-radius: 30px;
  font-size: 14px;
  margin: -7px 0 0 15px;
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
* { touch-action: pan-y; }
/deep/ :focus{outline:0;}
</style>

