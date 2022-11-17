<template>
    <main-container v-loading="result.loading">

      <el-card class="el-row-card">

        <template v-slot:header>

          <report-table-header :condition.sync="condition" @search="search"
                              :currentAccount="currentAccount" @cloudAccountSwitch="cloudAccountSwitch"
                               @openDownload="openDownload" @selectAccount="selectAccount" :show-open="true"/>
        </template>
        <el-row :gutter="20" class="el-row-body">
          <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="8" v-for="(data, index) in ftableData"
                  :key="index" class="el-col el-col-su">
            <el-card :body-style="{ padding: '15px' }">
              <div style="height: 110px;">
                <el-row :gutter="20">
                  <el-col :span="3">
                    <el-image style="border-radius: 50%;width: 16px; height: 16px; vertical-align:middle;" :src="require(`@/assets/img/platform/${data.pluginIcon}`)">
                      <div slot="error" class="image-slot">
                        <i class="el-icon-picture-outline"></i>
                      </div>
                    </el-image>
                    <div class="plugin">{{ data.pluginName }}</div>
                  </el-col>
                  <el-col :span="21">
                    <el-row>
                      <el-col :span="12">
                        <el-tooltip class="item" effect="dark" :content="data.name" placement="top">
                          <span class="da-na">
                            {{ data.name }}
                          </span>
                        </el-tooltip>
                      </el-col>
                      <el-col :span="12">
                        <el-tooltip class="item" effect="dark" :content="$t('history.resource_result') + ':' + data.returnSum + '/' + data.resourcesSum" placement="top">
                          <span v-if="data.status == 'risky'" style="color: red;float: right">
                            <i class="el-icon-warning"></i> {{ $t('resource.discover_risk') }}
                            <I style="color: #e8a97e;">{{ '(' + data.returnSum + '/' + data.resourcesSum + ')'}}</I>
                          </span>
                          <span v-if="data.status == 'risk_free'" style="color: green;float: right">
                            <i class="el-icon-warning"></i> {{ $t('resource.no_risk') }}
                            <I style="color: #e8a97e;">{{ '(' + data.returnSum + '/' + data.resourcesSum + ')'}}</I>
                          </span>
                        </el-tooltip>
                      </el-col>
                    </el-row>
                    <el-row class="desc">{{ data.description }}</el-row>
                  </el-col>
                </el-row>
              </div>
              <el-row :gutter="20" style="text-align: center">
                <el-col :span="12">
                  <hr-chart style="margin-left: 5%;" id="chart" ref="chart" :options="data.ruleOptions" :autoresize="true" :width="240" :height="150"></hr-chart>
                </el-col>
                <el-col :span="12">
                  <hr-chart style="margin-left: 5%;" id="chart" ref="chart" :options="data.resourceOptions" :autoresize="true" :width="240" :height="150"></hr-chart>
                </el-col>
              </el-row>
              <el-divider style="margin-top: 0;"></el-divider>
              <div style="padding: 0 14px 14px 14px;">
                <el-row>
                  <span style="color: #11365d;">{{ currentAccount }}</span>
                  <span style="color: #1e6427;">
                      ({{ data.level }})
                  </span>
                  <span>
                    <el-button size="mini" type="danger" class="round el-btn" round v-if="data.flag === true">
                        {{ $t('rule.tag_flag_true') }}
                      </el-button>
                      <el-button size="mini" type="success" class="round el-btn" round v-else-if="data.flag === false">
                        {{ $t('rule.tag_flag_false') }}
                      </el-button>
                      <span class="round">{{ data.createTime | timestampFormatDate }}</span>
                  </span>
                </el-row>
                <span class="button time pa-na">
                </span>
                <div class="bottom clearfix">
                  <el-button class="el-btn-btm" type="primary" plain size="small" @click="downloadReports(data)">{{ $t('report.download_group') }}</el-button>
                  <el-button class="el-btn-btm" type="success" plain size="small" @click="showDetails(data)">{{ $t('report.scan_details') }}</el-button>
                  <el-button class="el-btn-btm" type="warning" plain size="small" @click="handleList(data)">{{ $t('report.scan_rules') }}</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <f-table-pagination :change="search" :current-page.sync="fcurrentPage" :page-size.sync="fpageSize" :total="ftotal"/>
      </el-card>

      <!--检测报告详情-->
      <el-drawer class="btt" :title="$t('resource.report_detail')" :visible.sync="revisible" size="80%" :before-close="handleClose" :direction="directionB"
                 :destroy-on-close="true">
        <el-card class="table-card">
          <div style="margin-top: 15px;">
            <el-row>
              <el-col :span="4">
                <span style="color: #909090;">{{ $t('resource.scene_name') }}</span>
              </el-col>
              <el-col :span="8">
                <span>{{ group.name }}</span>
              </el-col>
              <el-col :span="4">
                <span style="color: #909090;">{{ $t('resource.activation_time') }}</span>
              </el-col>
              <el-col :span="8">
                <span>{{ group.createTime | timestampFormatDate }}</span>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="4">
                <span style="color: #909090;">{{ $t('resource.scene_description') }}</span>
              </el-col>
              <el-col :span="8">
                <span>{{ group.description }}</span>
              </el-col>
              <el-col :span="4">
                <span style="color: #909090;">{{ $t('resource.statistics') }}</span>
              </el-col>
              <el-col :span="8">
                <span v-if="group.status == 'risky'" style="color: red;cursor: pointer;" @click="openDownload"><i class="el-icon-download"></i> {{ $t('resource.download_report') }}</span>
                <span v-if="group.status == 'risk_free'" style="color: green;"> {{ group.state }}</span>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="4">
                <span style="color: #909090;">{{ $t('resource.equal_guarantee_level') }}</span>
              </el-col>
              <el-col :span="8">
                <span>{{ group.level }}</span>
              </el-col>
              <el-col :span="4">
                <span style="color: #909090;">{{ $t('resource.compliance_results') }}</span>
              </el-col>
              <el-col :span="8">
                <span v-if="group.status == 'risky'" style="color: red;"><i class="el-icon-warning"></i> {{ $t('resource.discover_risk') }}</span>
                <span v-if="group.status == 'risk_free'" style="color: green;"><i class="el-icon-warning"></i> {{ $t('resource.no_risk') }}</span>
              </el-col>
            </el-row>
          </div>
        </el-card>
        <el-card class="table-report-card">
          <section class="report-container">
            <main>
              <metric-chart v-if="content" :content="content"/>
            </main>
          </section>
        </el-card>
        <el-card v-if="resourceTableData.length>0" class="table-report-card-resource">
          <template v-slot:header>
            <table-header :condition.sync="resourceCondition"
                          @search="searchResource"
                          :title="$t('resource.cloud_resource_detail_result')"/>
          </template>
          <el-table border :data="resourceTableData" class="adjust-table table-content" @sort-change="sort" @filter-change="filter" :row-class-name="tableRowClassName">
            <!-- 展开 start -->
            <el-table-column type="expand" min-width="1%">
              <template v-slot:default="props">
                <el-divider><i class="el-icon-folder-opened"></i></el-divider>
                <el-form v-if="props.row.resource !== '[]'">
                  <result-read-only :row="typeof(props.row.resource) === 'string'?JSON.parse(props.row.resource):props.row.resource"></result-read-only>
                  <el-divider><i class="el-icon-document-checked"></i></el-divider>
                </el-form>
              </template>
            </el-table-column>
            <!-- 展开 end -->
            <el-table-column type="index" min-width="2%"/>
            <el-table-column v-slot:default="scope" :label="$t('resource.Hummer_ID')" min-width="15%">
              {{ scope.row.hummerId }}
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('rule.resource_type')" min-width="15%">
              {{ scope.row.resourceType }}
            </el-table-column>
            <el-table-column prop="regionName" :label="$t('account.regions')" min-width="12%">
              <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                {{ scope.row.regionName }}
              </span>
              </template>
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('rule.severity')" min-width="12%"
                             :sort-by="['CriticalRisk', 'HighRisk', 'MediumRisk', 'LowRisk']" prop="severity" :sortable="true"
                             show-overflow-tooltip>
              <severity-type :row="scope.row"></severity-type>
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('rule.rule_name')" min-width="16%" show-overflow-tooltip>
              {{ scope.row.ruleName }}
            </el-table-column>
            <el-table-column min-width="10%" :label="$t('commons.operating')" show-overflow-tooltip>
              <template v-slot:default="scope">
                <table-operators v-if="!!scope.row.suggestion" :buttons="resource_buttons2" :row="scope.row"/>
                <table-operators v-if="!scope.row.suggestion" :buttons="resource_buttons" :row="scope.row"/>
              </template>
            </el-table-column>
          </el-table>
          <table-pagination :change="searchResource" :current-page.sync="resourceCurrentPage" :page-size.sync="resourcePageSize" :total="resourceTotal"/>
        </el-card>
        <el-card class="table-report-card-resource">
          <template v-slot:header>
            <table-header :condition.sync="riskCondition"
                          @search="reportListSearch"
                          :title="$t('resource.regulation_list')"/>
          </template>
          <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" @filter-change="filter" :row-class-name="tableRowClassName">
            <el-table-column type="index" min-width="2%"/>
            <el-table-column prop="itemSortFirstLevel" :label="$t('resource.security_level')" min-width="10%" show-overflow-tooltip></el-table-column>
            <el-table-column prop="itemSortSecondLevel" :label="$t('resource.control_point')" min-width="10%" show-overflow-tooltip></el-table-column>
            <el-table-column prop="project" :label="$t('resource.basic_requirements_for_grade_protection')" min-width="47%" show-overflow-tooltip></el-table-column>
            <el-table-column :label="$t('resource.pre_check_results')" min-width="10%" show-overflow-tooltip>
              <template v-slot:default="scope">
                <el-tooltip class="item" effect="dark" :content="$t('resource.risk_of_non_compliance')" placement="top">
                  <span v-if="scope.row.status === 'risky'" style="color: red;">
                      {{ $t('resource.' + scope.row.status) }} <i class="el-icon-warning"></i>
                  </span>
                </el-tooltip>
                <el-tooltip class="item" effect="dark" :content="$t('resource.requirements_of_the_regulations')" placement="top">
                  <span v-if="scope.row.status === 'risk_free'" style="color: #00bb00;">
                      {{ $t('resource.' + scope.row.status) }} <i class="el-icon-warning"></i>
                  </span>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column v-slot:default="scope" :label="$t('resource.suggestions_for_improvement')" min-width="10%">
              <el-tooltip v-if="scope.row.status === 'risky'" class="item" effect="dark" :content="scope.row.improvement" placement="top">
                <span style="color: #0066ac;">
                  {{ $t('resource.suggestions_for_improvement') }} <i class="el-icon-question"></i>
                </span>
              </el-tooltip>
              <span v-if="scope.row.status === 'risk_free'">
                <i class="el-icon-minus"></i>
              </span>
            </el-table-column>
            <el-table-column min-width="8%" :label="$t('commons.operating')" fixed="right">
              <template v-slot:default="scope">
                <table-operators :buttons="buttons" :row="scope.row"/>
              </template>
            </el-table-column>
          </el-table>
          <table-pagination :change="reportListSearch" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
        </el-card>
      </el-drawer>
      <!--检测报告详情-->

      <!--regulation report-->
      <el-drawer class="btt" :title="$t('resource.regulation')" :visible.sync="regulationVisible"  size="60%" :before-close="handleCloseB" :direction="direction" :destroy-on-close="true">
        <el-card class="table-card" :body-style="{ padding: '15px', margin: '15px' }" v-for="(data, index) in regulationData" :key="data.id">
          <el-row class="el-row-c">
            <el-col :span="8"><span style="color: #215d9a;">{{ '(' + (index + 1) +') ' + $t('resource.basic_requirements_for_grade_protection') }}</span></el-col>
            <el-col :span="16"><span>{{ data.project }}</span></el-col>
          </el-row>
          <el-row class="el-row-c">
            <el-col :span="8"><span style="color: #215d9a;">{{ $t('resource.security_level') }}</span></el-col>
            <el-col :span="16"><span>{{ data.itemSortFirstLevel }}</span></el-col>
          </el-row>
          <el-row class="el-row-c">
            <el-col :span="8"><span style="color: #215d9a;">{{ $t('resource.control_point') }}</span></el-col>
            <el-col :span="16"><span>{{ data.itemSortSecondLevel }}</span></el-col>
          </el-row>
          <el-row class="el-row-c">
            <el-col :span="8"><span style="color: #215d9a;">{{ $t('resource.suggestions_for_improvement') }} <i class="el-icon-question"></i></span></el-col>
            <el-col :span="16"><span>{{ data.improvement }}</span></el-col>
          </el-row>
        </el-card>
      </el-drawer>
      <!--regulation report-->

      <!--Rule detail-->
      <el-drawer class="btt" :title="$t('resource.report_detail')" :visible.sync="visible" size="60%" :before-close="handleCloseB" :direction="directionB"
                 :destroy-on-close="true">
          <el-row class="el-row-c">
            <el-col :span="8"><span style="color: #909090;">{{ $t('resource.basic_requirements_for_grade_protection') }}</span></el-col>
            <el-col :span="16"><span>{{ detailForm.project }}</span></el-col>
          </el-row>
          <el-row class="el-row-c">
            <el-col :span="8"><span style="color: #909090;">{{ $t('resource.security_level') }}</span></el-col>
            <el-col :span="16"><span>{{ detailForm.itemSortFirstLevel }}</span></el-col>
          </el-row>
          <el-row class="el-row-c">
            <el-col :span="8"><span style="color: #909090;">{{ $t('resource.control_point') }}</span></el-col>
            <el-col :span="16"><span>{{ detailForm.itemSortSecondLevel }}</span></el-col>
          </el-row>
          <el-row class="el-row-c">
            <el-col :span="8"><span style="color: #909090;">{{ $t('resource.pre_check_results') }}</span></el-col>
            <el-col :span="16"><span>
              <el-tooltip class="item" effect="dark" :content="$t('resource.risk_of_non_compliance')" placement="top">
                  <span v-if="detailForm.status === 'risky'" style="color: red;">
                      {{ $t('resource.' + detailForm.status) }} <i class="el-icon-warning"></i>
                  </span>
                </el-tooltip>
                <el-tooltip class="item" effect="dark" :content="$t('resource.requirements_of_the_regulations')" placement="top">
                  <span v-if="detailForm.status === 'risk_free'" style="color: #00bb00;">
                      {{ $t('resource.' + detailForm.status) }} <i class="el-icon-warning"></i>
                  </span>
                </el-tooltip>
            </span></el-col>
          </el-row>
          <el-row class="el-row-c">
            <el-col :span="8"><span style="color: #909090;">{{ $t('resource.i18n_detail') }} <i class="el-icon-question"></i></span></el-col>
            <el-col :span="16">
              <span>
                <el-table :data="detailForm.cloudTaskList" style="width: 100%">
                    <el-table-column :label="$t('rule.rule_name')" min-width="75%">
                      <template slot-scope="scope">
                        <span v-if="!!scope.row.returnSum && scope.row.returnSum>0"><i class="el-icon-warning-outline" style="color: red"></i> {{ scope.row.taskName }}</span>
                        <span v-else><i class="el-icon-circle-check" style="color: #00bb00"></i> {{ scope.row.taskName }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="returnSum" :label="$t('resource.risk_point')" min-width="25%">
                      <template slot-scope="scope">
                        <el-link v-if="!!scope.row.returnSum && scope.row.returnSum>0" style="color: red;" @click="innerDrawerOpen(scope.row)"> {{ scope.row.returnSum?scope.row.returnSum:0 }} / {{ scope.row.resourcesSum?scope.row.resourcesSum:0 }}</el-link>
                        <span v-else> {{ scope.row.returnSum?scope.row.returnSum:0 }} / {{ scope.row.resourcesSum?scope.row.resourcesSum:0 }}</span>
                      </template>
                    </el-table-column>
                </el-table>
              </span>
            </el-col>
          </el-row>
          <el-row class="el-row-c">
            <el-col :span="8"><span style="color: #909090;">{{ $t('resource.suggestions_for_improvement') }} <i class="el-icon-question"></i></span></el-col>
            <el-col :span="16"><span>{{ detailForm.improvement }}</span></el-col>
          </el-row>
          <template v-slot:footer>
            <dialog-footer
              @cancel="visible = false"
              @confirm="visible = false"/>
          </template>
          <el-drawer
            size="50%"
            :title="$t('rule.rule_detail')"
            :append-to-body="true"
            :before-close="innerDrawerClose"
            :visible.sync="innerDrawer">
            <el-form :model="ruleForm" label-position="right" label-width="120px" size="small" ref="ruleForm">
              <el-form-item :label="$t('rule.rule_name')">
                <el-input v-model="ruleForm.name" autocomplete="off" :placeholder="$t('rule.rule_name')"/>
              </el-form-item>
              <el-form-item :label="$t('rule.rule_description')">
                <el-input v-model="ruleForm.description" autocomplete="off" :placeholder="$t('rule.rule_description')"/>
              </el-form-item>
              <el-form-item :label="$t('account.cloud_platform')">
                <el-select style="width: 100%;" v-model="ruleForm.pluginId" :placeholder="$t('account.please_choose_plugin')">
                  <el-option
                    v-for="item in plugins"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                    <img :src="require(`@/assets/img/platform/${item.icon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                    &nbsp;&nbsp; {{ item.name }}
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('rule.rule_tag')">
                <el-select style="width: 100%;" multiple v-model="ruleForm.tags" :placeholder="$t('rule.please_choose_tag')">
                  <el-option
                    v-for="item in tags"
                    :key="item.tagKey"
                    :label="item.tagName"
                    :value="item.tagKey">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('rule.severity')">
                <el-select style="width: 100%;" v-model="ruleForm.severity" :placeholder="$t('rule.please_choose_severity')">
                  <el-option
                    v-for="item in severityOptions"
                    :key="item.value"
                    :label="item.key"
                    :value="item.value">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('rule.rule_set')">
                <el-select style="width: 100%;" multiple filterable v-model="ruleForm.ruleSets">
                  <el-option
                    v-for="item in ruleSetOptions"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('rule.inspection_report')">
                <el-select style="width: 100%;" multiple filterable collapse-tags v-model="ruleForm.inspectionSeports">
                  <el-option
                    v-for="item in inspectionSeportOptions"
                    :key="item.id"
                    :label="item.id + '. ' + item.project.substring(0, 50) + '...'"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('rule.rule_yml')">
                <codemirror ref="cmEditor" v-model="ruleForm.script" class="code-mirror" :options="cmOptions" />
              </el-form-item>
              <el-form-item>
                <el-table
                  :data="ruleForm.parameter"
                  style="width: 100%">
                  <el-table-column v-slot:default="scope" label="Key" min-width="20%">
                    <el-input v-model="scope.row.key" :placeholder="$t('commons.input_content')"></el-input>
                  </el-table-column>
                  <el-table-column v-slot:default="scope" :label="$t('rule.middleware_name')" min-width="30%">
                    <el-input v-model="scope.row.name" :placeholder="$t('commons.input_content')"></el-input>
                  </el-table-column>
                  <el-table-column v-slot:default="scope" :label="$t('rule.middleware_parameter_default')" min-width="30%">
                    <el-input v-model="scope.row.defaultValue" :placeholder="$t('commons.input_content')"></el-input>
                  </el-table-column>
                  <el-table-column v-slot:default="scope" :label="$t('rule.required')" min-width="20%">
                    <el-switch v-model="scope.row.required" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                  </el-table-column>
                </el-table>
              </el-form-item>
            </el-form>
          </el-drawer>
      </el-drawer>
      <!--Rule detail-->

      <!-- 合并下载报告 -->
      <el-drawer class="rtl" :title="$t('resource.merge_resource')" :visible.sync="infoVisible" size="80%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <el-table border :data="accountData" class="adjust-table table-content" @sort-change="sort"
                  :row-class-name="tableRowClassName" @select-all="select" @select="select" style="margin: 1%;">
          <el-table-column type="selection" min-width="5%">
          </el-table-column>
          <el-table-column type="index" min-width="5%"/>
          <el-table-column prop="name" :label="$t('account.name')" min-width="12%" show-overflow-tooltip></el-table-column>
          <el-table-column :label="$t('account.cloud_platform')" min-width="10%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.pluginName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column min-width="15%" :label="$t('account.create_time')" sortable
                           prop="createTime">
            <template v-slot:default="scope">
              <span><i class="el-icon-time"></i> {{ scope.row.createTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column min-width="15%" :label="$t('account.update_time')" sortable
                           prop="updateTime">
            <template v-slot:default="scope">
              <span><i class="el-icon-time"></i> {{ scope.row.updateTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="userName" :label="$t('account.creator')" min-width="8%" show-overflow-tooltip/>
        </el-table>
        <table-pagination :change="search" :current-page.sync="accountPage" :page-size.sync="accountSize" :total="accountTotal"/>
        <el-row style="margin: 3%;">
          <span style="color: red;font-style: italic; font-weight: bold;">{{ $t('resource.desc') }}</span>
        </el-row>
        <el-button type="primary" style="margin-left: 45%;" @click="downloadReports">{{ $t('resource.download_report') }}</el-button>
      </el-drawer>
      <!-- 合并下载报告 -->

      <!--rule list-->
      <el-drawer class="rtl" :title="$t('rule.rule_list')" :visible.sync="listVisible" size="85%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <table-header :condition.sync="ruleCondition"
                      @search="handleListSearch"
                      :show-create="false" :show-open="false" :show-name="false"
                      style="margin: 0 15px 15px 15px;"/>
        <el-table border :data="ruleData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                  @filter-change="filter">
          <el-table-column type="index" min-width="2%"/>
          <el-table-column prop="name" :label="$t('rule.rule_name')" min-width="18%" show-overflow-tooltip></el-table-column>
          <el-table-column :label="$t('rule.resource_type')" min-width="10%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span v-for="(resourceType, index) in scope.row.types" :key="index">[{{ resourceType }}] </span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('account.cloud_platform')" min-width="11%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.pluginName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column min-width="8%" :label="$t('rule.severity')" column-key="severity">
            <template v-slot:default="{row}">
              <severity-type :row="row"></severity-type>
            </template>
          </el-table-column>
          <el-table-column prop="description" :label="$t('rule.description')" min-width="28%" show-overflow-tooltip></el-table-column>
          <el-table-column :label="$t('rule.status')" min-width="7%" show-overflow-tooltip>
            <template v-slot:default="scope">
              <el-switch @change="changeStatus(scope.row)" v-model="scope.row.status"/>
            </template>
          </el-table-column>
          <el-table-column prop="lastModified" min-width="15%" :label="$t('rule.last_modified')" sortable>
            <template v-slot:default="scope">
              <span><i class="el-icon-time"></i> {{ scope.row.lastModified | timestampFormatDate }}</span>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="handleListSearch" :current-page.sync="ruleListPage" :page-size.sync="ruleListPageSize" :total="ruleListTotal"/>
      </el-drawer>
      <!--rule list-->

    </main-container>
</template>

<script>
import TableOperators from "@/business/components/common/components/TableOperators";
import MainContainer from "@/business/components/common/components/MainContainer";
import Container from "@/business/components/common/components/Container";
import TableHeader from "@/business/components/common/components/TableHeader";
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableOperator from "@/business/components/common/components/TableOperator";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import CenterChart from "@/business/components/common/components/CenterChart";
import MetricChart from "./MetricChart";
import {_filter, _sort, getCurrentAccountID} from "@/common/js/utils";
import {severityOptions} from "@/common/js/constants";
import {saveAs} from "@/common/js/FileSaver.js";
import FTablePagination from "../../common/pagination/FTablePagination";
import ReportTableHeader from "@/business/components/report/head/ReportTableHeader";
import {RULE_CONFIGS, RULE_GROUP_CONFIGS} from "../../common/components/search/search-components";
import HrChart from "@/business/components/common/chart/HrChart";
import SeverityType from "@/business/components/common/components/SeverityType";
import ResultReadOnly from "@/business/components/report/head/ResultReadOnly";
import echarts from 'echarts';

/* eslint-disable */
  export default {
    components: {
      TableOperators,
      MainContainer,
      Container,
      TableHeader,
      TablePagination,
      TableOperator,
      DialogFooter,
      CenterChart,
      MetricChart,
      FTablePagination,
      ReportTableHeader,
      HrChart,
      SeverityType,
      ResultReadOnly,
      echarts
    },
    data() {
      return {
        result: {},
        content: {},
        tableData: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        ruleForm: {parameter:[]},
        tags: [],
        currentAccount: '',
        plugins: [],
        severityOptions: [],
        ruleSetOptions: [],
        inspectionSeportOptions: [],
        condition: {
          components: RULE_GROUP_CONFIGS
        },
        riskCondition: {},
        resourceCondition: {},
        buttons: [
          {
            tip: this.$t('resource.i18n_detail'), icon: "el-icon-notebook-2", type: "primary",
            exec: this.handleDetails
          }
        ],
        platforms: [
          {text: this.$t('account.aliyun'), value: 'hummer-aliyun-plugin'},
          {text: this.$t('account.tencent'), value: 'hummer-qcloud-plugin'},
          {text: this.$t('account.huawei'), value: 'hummer-huawei-plugin'},
          {text: this.$t('account.aws'), value: 'hummer-aws-plugin'},
          {text: this.$t('account.azure'), value: 'hummer-azure-plugin'},
          {text: this.$t('account.vsphere'), value: 'hummer-vsphere-plugin'},
          {text: this.$t('account.openstack'), value: 'hummer-openstack-plugin'},
          {text: this.$t('account.huoshan'), value: 'hummer-huoshan-plugin'},
          {text: this.$t('account.baidu'), value: 'hummer-baidu-plugin'},
          {text: this.$t('account.qiniu'), value: 'hummer-qiniu-plugin'},
          {text: this.$t('account.qingcloud'), value: 'hummer-qingcloud-plugin'},
          {text: this.$t('account.ucloud'), value: 'hummer-ucloud-plugin'},
          {text: this.$t('account.k8s'), value: 'hummer-k8s-plugin'}
        ],
        visible: false,
        revisible: false,
        accountId: '',
        accountIds: [],
        direction: 'rtl',
        directionB: 'btt',
        detailForm: {},
        innerDrawer: false,
        cmOptions: {
          tabSize: 4,
          mode: {
            name: 'shell',
            json: true
          },
          theme: 'bespin',
          lineNumbers: true,
          line: true,
          indentWithTabs: true,
        },
        groupId: "",
        groupName: "",
        infoVisible: false,
        accountData: [],
        accountPage: 1,
        accountSize: 10,
        accountTotal: 0,
        selectIds: new Set(),
        group: {},
        ftableData: [],
        fcurrentPage: 1,
        fpageSize: 12,
        ftotal: 0,
        listVisible: false,
        ruleCondition: {
          components: RULE_CONFIGS
        },
        ruleData: [],
        itemId: "",
        ruleListPage: 1,
        ruleListPageSize: 10,
        ruleListTotal: 0,
        ruleOptions: {},
        resourceOptions: {},
        resourceTableData: [],
        resourceCurrentPage: 1,
        resourcePageSize: 10,
        resourceTotal: 0,
        resource_buttons: [
          {
            tip: this.$t('resource.regulation'), icon: "el-icon-document", type: "warning",
            exec: this.showSeverityDetail
          },
        ],
        resource_buttons2: [
          {
            tip: this.$t('rule.suggestion'), icon: "el-icon-share", type: "primary",
            exec: this.handleSuggestion
          },
          {
            tip: this.$t('resource.regulation'), icon: "el-icon-document", type: "warning",
            exec: this.showSeverityDetail
          },
        ],
        regulationData: [],
        regulationVisible: false,
      }
    },
    methods: {
      sort(column) {
        _sort(column, this.condition);
        this.init();
      },
      filter(filters) {
        _filter(filters, this.condition);
        this.init();
      },
      select(selection) {
        this.selectIds.clear();
        selection.forEach(s => {
          this.selectIds.add(s.id)
        });
      },
      cloudAccountSwitch (accountId) {
        this.accountId = accountId;
        this.search();
      },
      async search () {
        this.condition.accountId = this.accountId;
        this.result = await this.$post("/resource/ruleGroup/list/" + this.fcurrentPage + "/" + this.fpageSize, this.condition, response => {
          let data = response.data;
          this.ftotal = data.itemCount;
          this.ftableData = data.listObject;
          for(let tableData of this.ftableData) {
            tableData.ruleOptions = {
              tooltip: {
                trigger: 'axis',
                  axisPointer: {
                  type: 'shadow' // 'shadow' as default; can also be 'line' or 'shadow'
                }
              },
              legend: {},
              grid: {
                left: '3%',
                  right: '4%',
                  bottom: '3%',
                  containLabel: true
              },
              xAxis: {
                type: 'value',
                minInterval: 1
              },
              yAxis: {
                type: 'category',
                  data: [this.$t('rule.rule')]
              },
              series: [
                {
                  name: this.$t('report.have_risk_rule'),
                  type: 'bar',
                  stack: 'total',
                  label: {
                    show: true
                  },
                  emphasis: {
                    focus: 'series'
                  },
                  data: [tableData.riskRuleSum]
                },
                {
                  name:  this.$t('report.no_risk_rule'),
                  type: 'bar',
                  stack: 'total',
                  label: {
                    show: true
                  },
                  emphasis: {
                    focus: 'series'
                  },
                  data: [tableData.ruleSum - tableData.riskRuleSum]
                },
              ],
              color: ['#267dc9', '#11cfae', '#893fdc']
            };
            tableData.resourceOptions = {
              tooltip: {
                trigger: 'item'
              },
              legend: {
                top: '0.1%',
                left: 'center'
              },
              series: [
                {
                  name: this.$t('history.scan_resources'),
                  type: 'pie',
                  radius: ['40%', '70%'],
                  avoidLabelOverlap: false,
                  itemStyle: {
                    borderRadius: 10,
                    borderColor: '#fff',
                    borderWidth: 2
                  },
                  label: {
                    show: false,
                    position: 'center'
                  },
                  emphasis: {
                    label: {
                      show: true,
                      fontSize: '20',
                      fontWeight: 'bold'
                    }
                  },
                  labelLine: {
                    show: false
                  },
                  data: [
                    { value: tableData.returnSum, name: this.$t('report.have_risk_resource') },
                    { value: tableData.resourcesSum, name: this.$t('report.no_risk_resource') }
                  ]
                }
              ],
              color: ['#009ef0', '#627dec', '#11cfae', '#893fdc', '#89ffff','#0051a4']
            };
          }
        });
      },
      async reportIsoSearch() {
        await this.$get("/resource/report/iso/" + this.accountId + '/' + this.groupId, response => {
          this.content = response.data;
          this.content.groupName = this.groupName;
          this.reportListSearch();
          this.searchResource();
        });
      },
      async reportListSearch() {
        let url = "/resource/reportList/" + this.currentPage + "/" + this.pageSize;
        //在这里实现事件
        this.riskCondition.accountId = this.accountId;
        this.riskCondition.groupId = this.groupId;
        await this.$post(url, this.riskCondition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
      },
      async searchResource() {
        let url = "/resource/resourceList/" + this.resourceCurrentPage + "/" + this.resourcePageSize;
        //在这里实现事件
        this.resourceCondition.accountId = this.accountId;
        this.resourceCondition.groupId = this.groupId;
        await this.$post(url, this.resourceCondition, response => {
          let data = response.data;
          this.resourceTotal = data.itemCount;
          this.resourceTableData = data.listObject;
        });
      },
      goResource (params) {
        this.$router.push({
          path: '/resource/resultdetails/' + params.id
        }).catch(error => error);
      },
      tagLists() {
        let url = "/rule/ruleTags";
        this.result = this.$get(url, response => {
          this.tags = response.data;
        });
        if (!!getCurrentAccountID()) {
          this.accountId = getCurrentAccountID();
        }
      },
      //查询插件
      activePlugin() {
        let url = "/plugin/cloud";
        this.result = this.$get(url, response => {
          let data = response.data;
          this.plugins =  data;
        });
      },
      innerDrawerOpen (item) {
        this.innerDrawer = true;
        this.viewRule(item);
      },
      async viewRule (item) {
        await this.$get("/rule/getRuleByTaskId/" + item.id, response => {
          this.ruleForm = response.data;
          if (typeof(this.ruleForm.parameter) == 'string') this.ruleForm.parameter = JSON.parse(this.ruleForm.parameter);
          this.ruleForm.tagKey = this.ruleForm.tags[0];
        });
      },
      severityOptionsFnc () {
        this.severityOptions = severityOptions;
      },
      ruleSetOptionsFnc () {
        this.$post("/resource/rule/groups" , {"accountId":this.accountId}, res => {
          this.ruleSetOptions = res.data;
        });
      },
      inspectionSeportOptionsFnc () {
        this.$get("/rule/all/ruleInspectionReport", res => {
          this.inspectionSeportOptions = res.data;
        });
      },
      init() {
        this.tagLists();
        this.activePlugin();
        this.severityOptionsFnc();
        this.ruleSetOptionsFnc();
        this.inspectionSeportOptionsFnc();
        this.accountList();
        this.search();
      },
      filterAccount (tag) {
        if (tag.name) {
          this.condition.pluginId = tag.name;
          this.search();
        }
      },
      tableRowClassName({row, rowIndex}) {
        if (rowIndex % 4 === 0) {
          return 'success-row';
        } else if (rowIndex % 2 === 0) {
          return 'warning-row';
        } else {
          return '';
        }
      },
      handleClick(tab) {
        this.reportIsoSearch();
      },
      handleClose() {
        this.infoVisible = false;
        this.revisible = false;
        this.listVisible = false;
      },
      handleCloseB() {
        this.visible = false;
        this.regulationVisible = false;
      },
      innerDrawerClose() {
        this.innerDrawer = false;
      },
      handleDetails (item) {
        this.detailForm = item;
        this.visible=true;
      },
      handleScan () {
        this.$get("/rule/reScan/" + item.id + "/" + item.accountId, response => {
          if (response.success) {
            this.search();
          }
        });
      },
      openDownload() {
        this.infoVisible = true;
        this.accountIds = [];
        this.accountIds.push(this.accountId);
      },
      download() {
        let myDate = new Date();
        this.$alert(this.$t('resource.download_report_description_start') + myDate.toLocaleString() + this.$t('resource.download_report_description_end'), this.$t('resource.download_report'), {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              let columns = [
                {value: this.$t('resource.Hummer_ID'), key: "hummerId"},
                {value: this.$t('dashboard.resource_name'), key: "resourceName"},
                {value: this.$t('rule.resource_type'), key: "resourceType"},
                {value: this.$t('account.region_id'), key: "regionId"},
                {value: this.$t('account.region_name'), key: "regionName"},
                {value: this.$t('rule.rule_name'), key: "ruleName"},
                {value: this.$t('rule.rule_description'), key: "ruleDescription"},
                {value: this.$t('rule.severity'), key: "severity"},
                {value: this.$t('resource.audit_name'), key: "auditName"},
                {value: this.$t('resource.basic_requirements_for_grade_protection'), key: "basicRequirements"},
                {value: this.$t('resource.suggestions_for_improvement'), key: "improvement"},
              ];
              this.accountIds = this.accountIds.concat(Array.from(this.selectIds));
              this.result = this.$download("/resource/export", {
                columns: columns,
                accountIds: this.accountIds,
              }, response => {
                if (response.status === 201) {
                  let blob = new Blob([response.data], {type: "'application/octet-stream'"});
                  saveAs(blob, this.$t("resource.resource_report_xlsx"));
                }
              }, error => {
                console.log("导出报错", error);
              });
            }
          }
        });
      },
      accountList() {
        let url = "/account/list/" + this.accountPage + "/" + this.accountSize;
        this.result = this.$post(url, {}, response => {
          let data = response.data;
          this.accountTotal = data.itemCount;
          this.accountData = data.listObject;
        });
      },
      showDetails(data) {
        this.group = data;
        this.groupId = data.id;
        this.reportIsoSearch();
        this.reportListSearch();
        this.revisible = true;
      },
      downloadReports(data) {
        let myDate = new Date();
        this.$alert(this.$t('resource.download_report_description_start') + myDate.toLocaleString() + this.$t('resource.download_report_description_end'), this.$t('resource.download_report'), {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              let columns = [
                {value: this.$t('resource.Hummer_ID'), key: "hummerId"},
                {value: this.$t('dashboard.resource_name'), key: "resourceName"},
                {value: this.$t('rule.resource_type'), key: "resourceType"},
                {value: this.$t('account.region_id'), key: "regionId"},
                {value: this.$t('account.region_name'), key: "regionName"},
                {value: this.$t('rule.rule_name'), key: "ruleName"},
                {value: this.$t('rule.rule_description'), key: "ruleDescription"},
                {value: this.$t('rule.severity'), key: "severity"},
                {value: this.$t('resource.audit_name'), key: "auditName"},
                {value: this.$t('resource.basic_requirements_for_grade_protection'), key: "basicRequirements"},
                {value: this.$t('resource.suggestions_for_improvement'), key: "improvement"},
              ];
              this.result = this.$download("/resource/groupExport", {
                columns: columns,
                accountId: this.accountId,
                groupId: data.id,
              }, response => {
                if (response.status === 201) {
                  let blob = new Blob([response.data], {type: "'application/octet-stream'"});
                  saveAs(blob, this.$t("resource.resource_report_xlsx"));
                }
              }, error => {
                this.$error('导出报错' + error);
              });
            }
          }
        });
      },
      handleList(item) {
        this.ruleListPage = 1;
        this.ruleListPageSize = 10;
        this.ruleListTotal = 0;
        this.ruleForm = [];
        this.itemId = item.id;
        this.handleListSearch();
        this.listVisible = true;
      },
      handleListSearch () {
        this.ruleCondition.combine = {group: {operator: 'in', value: this.itemId }};
        let url = "/rule/list/" + this.ruleListPage + "/" + this.ruleListPageSize;
        this.result = this.$post(url, this.ruleCondition, response => {
          let data = response.data;
          this.ruleListTotal = data.itemCount;
          this.ruleData = data.listObject;
        });
      },
      selectAccount(accountId, accountName) {
        this.accountId = accountId;
        this.currentAccount = accountName;
      },
      handleSuggestion(item) {
        window.open(item.suggestion,'_blank','');
      },
      showSeverityDetail(item) {
        this.$get("/resource/regulation/" + item.ruleId, response => {
          if (response.success) {
            this.regulationData = response.data;
            this.regulationVisible = true;
          }
        });
      },
    },
    created() {
      this.init();
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

  .el-form-item-dev  >>> .el-form-item__content {
    margin-left: 0 !important;
  }

  .grid-content-log-span {
    width: 40%;float: left;
    vertical-align: middle;
    display:table-cell;
    margin: 6px 0;
  }

  .grid-content-status-span {
    width: 20%;float: left;
    vertical-align: middle;
    display:table-cell;
    margin: 6px 0;
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

  .el-icon-close-detail {
    float: right;
    cursor:pointer;
  }

  .view-text{
    display: inline-block;
    white-space: nowrap;
    width: 100%;
    overflow: hidden;
    text-overflow:ellipsis;
  }
  .text-click {
    color: #0066ac;
  }
  .table-card {
    margin-bottom: 15px;
  }
  .table-card >>> .el-card__body {
    padding: 0 !important;
  }

  .table-card >>> span {
    margin: 50px !important;
  }

  .report-container .el-tabs__header {
    margin-bottom: 1px;
  }

  .report-container {
    height: calc(100vh - 550px);
    min-height: 600px;
    overflow-y: auto;
  }
  .table-report-card {
    margin-bottom: 15px;
  }

  .table-report-card-resource {
    margin-bottom: 15px;
  }

  .table-report-card >>> .el-card__body {
    height: 170px;
  }

  .el-row-c {
    margin: 50px;
  }

  .border-card {
  }

  .rtl >>> .el-drawer{
    overflow: scroll;
  }

  .el-row-card {
    padding: 0 20px 0 20px;
    margin: 0 0 20px 0;
  }
  .el-row-card >>> .el-card__body {
    margin: 0;
  }

  .table-card >>> .issue-list {
    width: 100%;
    text-align: center;
  }
  .table-card >>> .arrows{
    display: inline-block;
    cursor: pointer;
    margin: 0 10px;
    font-width: bold;
    color: red;
  }
  .table-card >>> .issue-item{
    text-align: left;
    display: inline-block;
    padding-bottom: 11px;
    border-bottom: 1px solid #E2E2E2;
    margin: 10px 10px;
    cursor: pointer;
  }
  .table-card >>> .issue-item-active{
    color: #0261D5;
    border-bottom: 2px solid #0261D5;
  }
  .el-col-su >>> .el-card {
    margin: 10px 0;
  }
  .vue-select-image >>> .vue-select-image__img {
    width: 120px;
    height: 100px;
  }
  .el-row-body >>> .el-card__body >>> .el-divider {
    margin: 5px 0;
  }
  .plugin {
    color: #215d9a;
    font-size: 13px;
    margin-top: 25px;
    width: 1px;
    max-height: 110px;
    /*文字竖排显示*/
    writing-mode: vertical-lr;/*从左向右 从右向左是 writing-mode: vertical-rl;*/
    writing-mode: tb-lr;/*IE浏览器的从左向右 从右向左是 writing-mode: tb-rl；*/
  }
  .desc {
    color: #888888;
    font-size: 13px;
    margin-top: 10px;
    line-height: 20px;
    overflow: hidden;
    text-overflow: ellipsis;
    display:-webkit-box;
    -webkit-box-orient:vertical;
    -webkit-line-clamp:6;
    height: 110px;
  }
  .edit_dev >>> .el-transfer-panel {
    width: 40%;
    text-align: left;
  }
  .edit-dev-drawer >>> .el-drawer__header {
    margin-bottom: 0;
  }
  .edit-dev-drawer >>> .el-transfer-panel__body{
    height: 500px;
  }
  .edit-dev-drawer >>> .el-transfer-panel__list{
    height: 500px;
    padding-bottom: 50px;
  }
  .el-trans {
    width: 100%;
    text-align: center;
    display: inline-block;
  }
  .table-content {
    width: 100%;
  }
  .el-row-body {
    line-height: 1.15;
  }
  .time {
    font-size: 13px;
    color: #999;
  }
  .round {
    font-size: 13px;
    margin: 0 0 0 5px;
    padding: 1px 3px 1px 3px;
    float: right;
  }
  .bottom {
    margin-top: 13px;
    line-height: 13px;
  }
  .button {
    padding: 0;
    float: right;
    white-space:nowrap;
    text-overflow:ellipsis;
    -o-text-overflow:ellipsis;
    overflow:hidden;
  }
  .da-na {
    width: 100%;
    white-space:nowrap;
    text-overflow:ellipsis;
    overflow:hidden;
    float: left;
    color: red;
  }
  .pa-na {
    max-width: 60%;
    white-space:nowrap;
    text-overflow:ellipsis;
    -o-text-overflow:ellipsis;
    overflow:hidden;
  }
  .pa-time {
    display:inline-block;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    color: #1e6427;
    float: left;
  }
  .pa-time2 {
    display:inline-block;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    color: red;
    float: left;
  }
  .button-drop {
    float: right;
  }
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
  }
  .el-icon-arrow-down {
    font-size: 12px;
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
  .account-change {
    margin: 0 0 10px 0;
  }
  .el-btn {
    transform: scale(0.9);
  }
  .el-btn-btm {
    width: 30%;
  }
  .bottom >>> .el-button--small {
    padding: 9px 1%;
  }
  .rtl >>> .el-drawer__body {
    overflow-y: auto;
    padding: 0 20px;
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
  .rtl >>> #el-drawer__title {
    margin: 0;
  }
  /deep/ :focus{outline:0;}
</style>
