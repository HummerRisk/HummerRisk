<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('k8s.k8s_settings_list')"
                      @create="create" :createTip="$t('k8s.k8s_create')"
                      @validate="validate" :validateTip="$t('account.one_validate')"
                      :show-validate="true" :show-create="true"
                      :items="items" :columnNames="columnNames" @delete="deleteBatch" :show-delete="true"
                      :checkedColumnNames="checkedColumnNames" :checkAll="checkAll" :isIndeterminate="isIndeterminate"
                      @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange" @handleCheckAllChange="handleCheckAllChange"/>
      </template>

      <hide-table
        :table-data="tableData"
        @sort-change="sort"
        @filter-change="filter"
        @select-all="select"
        @select="select"
      >
        <el-table-column type="selection" min-width="40">
        </el-table-column>
        <el-table-column type="index" min-width="40"/>
        <el-table-column prop="name" v-if="checkedColumnNames.includes('name')" :label="$t('k8s.name')" min-width="150" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('log')" :label="$t('k8s.status')" min-width="120" prop="log" show-overflow-tooltip>
          <el-button @click="showLog(scope.row)" plain size="mini" type="success" v-if="scope.row.k8sStatus === 'FINISHED'">
            <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
          </el-button>
          <el-button @click="showLog(scope.row)" plain size="mini" type="danger" v-else-if="scope.row.k8sStatus === 'ERROR'">
            <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
          </el-button>
          <el-button @click="showLog(scope.row)" plain size="mini" type="warning" v-else-if="scope.row.k8sStatus === 'WARNING'">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
          </el-button>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('status')" :label="$t('image.result_status')" min-width="120" prop="resultStatus" sortable show-overflow-tooltip>
          <el-button @click="showResultLog(scope.row)" plain size="mini" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="mini" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="mini" type="success" v-else-if="scope.row.resultStatus === 'FINISHED'">
            <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="mini" type="danger" v-else-if="scope.row.resultStatus === 'ERROR'">
            <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="mini" type="warning" v-else-if="scope.row.resultStatus === 'WARNING'">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
          </el-button>
          <el-button @click="noWarnLog(scope.row)" plain size="mini" type="info" v-else-if="scope.row.resultStatus === null">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_no_warn') }}
          </el-button>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('result')" :label="$t('k8s.vuln_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="210">
          <el-tooltip effect="dark" :content="$t('history.result') + ' CRITICAL:' + scope.row.critical + ' HIGH:' +  scope.row.high + ' MEDIUM:' + scope.row.medium + ' LOW:' + scope.row.low + ' UNKNOWN:' + scope.row.unknown" placement="top">
            <div class="txt-click" @click="goResource(scope.row)">
              <span style="background-color: #8B0000;color: white;padding: 3px;">{{ 'C:' + scope.row.critical }}</span>
              <span style="background-color: #FF4D4D;color: white;padding: 3px;">{{ 'H:' +  scope.row.high }}</span>
              <span style="background-color: #FF8000;color: white;padding: 3px;">{{ 'M:' + scope.row.medium }}</span>
              <span style="background-color: #eeab80;color: white;padding: 3px;">{{ 'L:' + scope.row.low }}</span>
              <span style="background-color: #d5d0d0;color: white;padding: 3px;">{{ 'U:' + scope.row.unknown }}</span>
            </div>
          </el-tooltip>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('configResult')" :label="$t('k8s.config_compliance')" prop="returnConfigSum" sortable show-overflow-tooltip min-width="190">
          <el-tooltip effect="dark" :content="$t('history.config_result') + ' CRITICAL:' + scope.row.configCritical + ' HIGH:' +  scope.row.configHigh + ' MEDIUM:' + scope.row.configMedium + ' LOW:' + scope.row.configLow" placement="top">
            <div class="txt-click" @click="goConfigResource(scope.row)">
              <span style="background-color: #8B0000;color: white;padding: 3px;">{{ 'C:' + scope.row.configCritical }}</span>
              <span style="background-color: #FF4D4D;color: white;padding: 3px;">{{ 'H:' +  scope.row.configHigh }}</span>
              <span style="background-color: #FF8000;color: white;padding: 3px;">{{ 'M:' + scope.row.configMedium }}</span>
              <span style="background-color: #d5d0d0;color: white;padding: 3px;">{{ 'L:' + scope.row.configLow }}</span>
            </div>
          </el-tooltip>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('kubenchResult')" :label="$t('k8s.kubench_compliance')" prop="kubenchResult" sortable show-overflow-tooltip min-width="190">
          <el-tooltip effect="dark" :content="$t('k8s.kubench_result') + ' FAIL:' + scope.row.fail + ' WARN:' +  scope.row.warn + ' INFO:' + scope.row.info + ' PASS:' + scope.row.pass" placement="top">
            <div class="txt-click" @click="goKubenchResource(scope.row)">
              <span style="background-color: #8B0000;color: white;padding: 3px;">{{ 'F:' + scope.row.fail }}</span>
              <span style="background-color: #FF4D4D;color: white;padding: 3px;">{{ 'W:' +  scope.row.warn }}</span>
              <span style="background-color: #FF8000;color: white;padding: 3px;">{{ 'I:' + scope.row.info }}</span>
              <span style="background-color: #d5d0d0;color: white;padding: 3px;">{{ 'P:' + scope.row.pass }}</span>
            </div>
          </el-tooltip>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('returnSum')" :label="$t('commons.compliance_scan_statistics')" prop="returnSum" sortable show-overflow-tooltip min-width="170">
          <el-tooltip class="item txt-click" effect="dark" :content="$t('history.resource_result')" placement="top">
            <span v-if="scope.row.cloudReturnSum == null && scope.row.cloudResourcesSum == null"> N/A</span>
            <span @click="goCloudResource(scope.row)">
              <span style="background-color: #ad1414;color: white;padding: 3px;">{{ 'Risk:' }}{{ scope.row.cloudReturnSum }}</span>
              <span style="background-color: #d5d0d0;color: white;padding: 3px;">{{ 'Sum:' }}{{ scope.row.cloudResourcesSum }}</span>
            </span>
          </el-tooltip>
        </el-table-column>
        <el-table-column prop="scanTime" min-width="200" v-if="checkedColumnNames.includes('scanTime')" :label="$t('commons.last_scan_time')" sortable>
          <template v-slot:default="scope">
            <span v-if="scope.row.resultStatus !== null"><i class="el-icon-time"/> {{ scope.row.scanTime | timestampFormatDate }}</span>
            <span v-if="scope.row.resultStatus === null">--</span>
          </template>
        </el-table-column>
        <el-table-column min-width="160" v-if="checkedColumnNames.includes('updateTime')" :label="$t('account.update_time')" sortable
                         prop="updateTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" v-if="checkedColumnNames.includes('userName')" :label="$t('account.creator')" min-width="110" show-overflow-tooltip/>
        <el-table-column min-width="230" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create k8s-->
    <el-drawer class="rtl" :title="$t('k8s.k8s_create')" :visible.sync="createVisible" size="70%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="k8sResult.loading">
      <div>
        <div v-for="(form, index) in addAccountForm" :key="index">
          <el-form :model="form" label-position="right" label-width="150px" size="medium" :rules="rule" :ref="'addAccountForm' + index">
            <el-form-item :label="$t('k8s.name')" ref="name" prop="name">
              <el-input v-model="form.name" autocomplete="off" :placeholder="$t('k8s.name')"/>
            </el-form-item>
            <el-form-item :label="$t('k8s.platform')" :rules="{required: true, message: $t('k8s.platform') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="form.pluginId" :placeholder="$t('k8s.platform')" @change="changePluginForAdd(form)">
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
            <div v-for="(tmp, index) in form.tmpList" :key="index">
              <el-form-item v-if="tmp.inputType === 'password'" :label="tmp.label" style="margin-bottom: 29px">
                <el-input :type="tmp.inputType" v-model="tmp.input" autocomplete="new-password" show-password :placeholder="tmp.description"/>
              </el-form-item>
              <el-form-item v-if="tmp.inputType !== 'password' && tmp.inputType !== 'boolean'" :label="tmp.label">
                <el-input v-if="tmp.inputType === 'textarea'" :type="tmp.inputType" @input="change($event)" :rows="10" v-model="tmp.input" autocomplete="off" :placeholder="tmp.description"/>
                <el-input v-if="tmp.inputType !== 'textarea'" :type="tmp.inputType" @input="change($event)" v-model="tmp.input" autocomplete="off" :placeholder="tmp.description"/>
              </el-form-item>
            </div>
            <el-form-item v-if="form.isProxy && form.pluginId" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="form.proxyId" :placeholder="$t('commons.proxy')">
                <el-option
                  v-for="item in proxys"
                  :key="item.id"
                  :label="item.proxyIp"
                  :value="item.id">
                  &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item v-if="form.pluginId" :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-switch v-model="form.isProxy"></el-switch>
            </el-form-item>
            <el-form-item v-if="index > 0" :label="$t('k8s.delete_this_k8s')">
              <el-button type="danger" icon="el-icon-delete" plain size="small" @click="deleteAccount(addAccountForm, form)">{{ $t('commons.delete') }}</el-button>
            </el-form-item>
          </el-form>
          <el-divider><i class="el-icon-first-aid-kit"> {{ (index + 1) }}</i></el-divider>
          <div style="margin: 10px;">
            <el-popover placement="right-end" title="Notice" width="800" trigger="click">
              <hr-code-edit :read-only="true" width="800px" height="300px" :data.sync="content" :modes="modes" :mode="'html'"/>
              <el-button icon="el-icon-warning" plain size="mini" slot="reference" style="color: red">
                <span>{{ $t('k8s.k8s_note') }}</span>
              </el-button>
            </el-popover>
          </div>
        </div>
        <proxy-dialog-create-footer
          @cancel="createVisible = false"
          @addAccount="addAccount(addAccountForm)"
          @confirm="saveAccount(addAccountForm, 'add')"/>
      </div>
    </el-drawer>
    <!--Create k8s-->

    <!--Update k8s-->
    <el-drawer class="rtl" :title="$t('k8s.k8s_update')" :visible.sync="updateVisible" size="70%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="k8sResult.loading">
      <div>
        <el-form :model="form" label-position="right" label-width="150px" size="small" :rules="rule" ref="accountForm">
          <el-form-item :label="$t('k8s.name')"  ref="name" prop="name">
            <el-input v-model="form.name" autocomplete="off" :placeholder="$t('k8s.name')"/>
          </el-form-item>
          <el-form-item :label="$t('k8s.platform')" :rules="{required: true, message: $t('k8s.platform') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" disabled filterable :clearable="true" v-model="form.pluginId" :placeholder="$t('k8s.platform')" @change="changePlugin(form.pluginId)">
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
          <div v-for="(tmp, index) in tmpList" :key="index">
            <el-form-item v-if="tmp.inputType === 'password'" :label="tmp.label" style="margin-bottom: 29px">
              <el-input :type="tmp.inputType" v-model="tmp.input" @input="change($event)" autocomplete="new-password" show-password :placeholder="tmp.description"/>
            </el-form-item>
            <el-form-item v-if="tmp.inputType !== 'password' && tmp.inputType !== 'boolean'" :label="tmp.label">
              <el-input v-if="tmp.inputType === 'textarea'" :type="tmp.inputType" :rows="10" v-model="tmp.input" @input="change($event)" autocomplete="off" :placeholder="tmp.description"/>
              <el-input v-if="tmp.inputType !== 'textarea'" :type="tmp.inputType" v-model="tmp.input" @input="change($event)" autocomplete="off" :placeholder="tmp.description"/>
            </el-form-item>
          </div>
        </el-form>
        <div style="margin: 10px;">
          <el-popover placement="right-end" title="Notice" width="800" trigger="click">
            <hr-code-edit :read-only="true" width="800px" height="300px" :data.sync="content" :modes="modes" :mode="'html'"/>
            <el-button icon="el-icon-warning" plain size="mini" slot="reference" style="color: red">
              <span>{{ $t('k8s.k8s_note') }}</span>
            </el-button>
          </el-popover>
        </div>
        <proxy-dialog-footer
          @cancel="updateVisible = false"
          @confirm="editAccount(form, 'update')"/>
      </div>
    </el-drawer>
    <!--Update k8s-->

    <!--Install log-->
    <el-drawer class="rtl" :title="$t('k8s.install_log')" :visible.sync="logVisible" size="85%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="k8sResult.loading">
      <el-row class="el-form-item-dev">
        <div>
          <el-row>
            <el-col :span="24">
              <div class="grid-content bg-purple-light">
                <span class="grid-content-log-span"> {{ $t('k8s.name') }} : {{ logForm.name }}</span>
                <span class="grid-content-log-span">
                  <img :src="require(`@/assets/img/platform/${logForm.pluginIcon?logForm.pluginIcon:'k8s.png'}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                </span>
                <span class="grid-content-status-span" v-if="logForm.resultStatus === 'WARNING'" style="color: #f88f57">
                  <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'FINISHED'" style="color: #7ebf50">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'ERROR'" style="color: red;">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </span>
              </div>
            </el-col>
          </el-row>
        </div>
        <el-table style="width: 100%" :data="installData" :show-header="false">
          <el-table-column prop="status" width="300">{{ $t('k8s.status') }}</el-table-column>
          <el-table-column prop="status" min-width="300"
                           column-key="status"
                           :filters="statusFilters"
                           v-slot:default="scope"
                           :filter-method="filterStatus">
            <div @click="validateK8s(scope.row)" style="cursor:pointer;">
              <el-button size="mini" type="warning" v-if="scope.row.status === 'DELETE'">
                {{ $t('account.DELETE') }}
              </el-button>
              <el-button size="mini" type="success" v-else-if="scope.row.status === 'VALID'">
                {{ $t('account.VALID') }}
              </el-button>
              <el-button size="mini" type="danger" v-else-if="scope.row.status === 'INVALID'">
                {{ $t('account.INVALID') }}
              </el-button>
            </div>
          </el-table-column>
          <el-table-column prop="operator" min-width="300" fixed="right" v-slot:default="scope">
            <el-button type="primary" size="mini" @click="validateK8s(scope.row)">
              {{ $t('account.validate') }}
            </el-button>
          </el-table-column>
        </el-table>
        <el-table style="width: 100%" :data="installData" :show-header="false">
          <el-table-column prop="status" width="300">{{ $t('k8s.operator_status') }}</el-table-column>
          <el-table-column prop="operatorStatus" min-width="300"
                           column-key="operatorStatus"
                           :filters="statusFilters"
                           v-slot:default="scope"
                           :filter-method="filterOperatorStatus">
            <div @click="validateOperator(scope.row)" style="cursor:pointer;">
              <el-button size="mini" type="warning" v-if="scope.row.operatorStatus === 'DELETE'">
                {{ $t('account.DELETE') }}
              </el-button>
              <el-button size="mini" type="success" v-else-if="scope.row.operatorStatus === 'VALID'">
                {{ $t('account.VALID') }}
              </el-button>
              <el-button size="mini" type="danger" v-else-if="scope.row.operatorStatus === 'INVALID'">
                {{ $t('account.INVALID') }}
              </el-button>
            </div>
          </el-table-column>
          <el-table-column prop="operator" min-width="300" fixed="right" v-slot:default="scope">
            <el-button type="primary" size="mini" @click="reinstallOperator(scope.row)">
              {{ $t('k8s.reinstall_operator') }}
            </el-button>
          </el-table-column>
        </el-table>
        <el-table style="width: 100%" :data="installData" :show-header="false">
          <el-table-column prop="status" width="300">{{ $t('k8s.kubench_status') }}</el-table-column>
          <el-table-column prop="kubenchStatus" min-width="300"
                           column-key="kubenchStatus"
                           :filters="statusFilters"
                           v-slot:default="scope"
                           :filter-method="filterKubenchStatus">
            <div @click="validateKubench(scope.row)" style="cursor:pointer;">
              <el-button size="mini" type="warning" v-if="scope.row.kubenchStatus === 'DELETE'">
                {{ $t('account.DELETE') }}
              </el-button>
              <el-button size="mini" type="success" v-else-if="scope.row.kubenchStatus === 'VALID'">
                {{ $t('account.VALID') }}
              </el-button>
              <el-button size="mini" type="danger" v-else-if="scope.row.kubenchStatus === 'INVALID'">
                {{ $t('account.INVALID') }}
              </el-button>
            </div>
          </el-table-column>
          <el-table-column prop="operator" min-width="300" fixed="right" v-slot:default="scope">
            <el-button type="primary" size="mini" @click="reinstallKubench(scope.row)">
              {{ $t('k8s.reinstall_kubench') }}
            </el-button>
          </el-table-column>
        </el-table>

        <br>
        <div>
          <el-row>
            <el-col :span="24">
              <div class="grid-content bg-purple-light-log">
                <span class="grid-content-log-span" style="color: red;"> {{ $t('k8s.install_log') }}</span>
                <span class="grid-content-log-span">
                </span>
                <span class="grid-content-status-span">
                </span>
              </div>
            </el-col>
          </el-row>
        </div>
        <el-table :show-header="false" :data="logData" class="adjust-table table-content" :row-class-name="tableRowClassName">
          <el-table-column type="index" min-width="40"/>
          <el-table-column min-width="160" prop="createTime">
            <template v-slot:default="scope">
              <span v-bind:class="{true: 'color-red', false: ''}[scope.row.result == false]">
                      {{ scope.row.createTime | timestampFormatDate }}
                </span>
            </template>
          </el-table-column>
          <el-table-column min-width="120" prop="operator">
            <template v-slot:default="scope">
              <span v-bind:class="{true: 'color-red', false: ''}[scope.row.result == false]">
                      {{ scope.row.operator }}
                </span>
            </template>
          </el-table-column>
          <el-table-column min-width="240" prop="operation" fixed="right">
            <template v-slot:default="scope">
              <span v-bind:class="{true: 'color-red', false: ''}[scope.row.result == false]">
                      {{ scope.row.operation }}
                      {{ scope.row.output }}
                </span>
            </template>
          </el-table-column>
        </el-table>
        <table-pagination :change="searchLogData" :current-page.sync="logPage" :page-size.sync="logSize" :total="logTotal"/>
      </el-row>
      <template v-slot:footer>
        <dialog-footer
          @cancel="logVisible = false"
          @confirm="logVisible = false"/>
      </template>
    </el-drawer>
    <!--Install log-->

    <!--Result log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logResultVisible" size="90%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="k8sResult.loading">
      <el-row class="el-form-item-dev" v-if="logResultData.length == 0">
        <span>{{ $t('resource.i18n_no_data') }}<br></span>
      </el-row>
      <el-row class="el-form-item-dev" v-if="logResultData.length > 0" v-loading="k8sResult.loading">
        <div>
          <el-row>
            <el-col :span="24">
              <div class="grid-content bg-purple-light">
                <span class="grid-content-log-span"> {{ logResultForm.name }}</span>
                <span class="grid-content-log-span">
                  <img :src="require(`@/assets/img/platform/${logResultForm.pluginIcon?logResultForm.pluginIcon:'k8s.png'}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                </span>
                <span class="grid-content-status-span" v-if="logResultForm.resultStatus === 'APPROVED'" style="color: #579df8">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logResultForm.resultStatus === 'FINISHED'" style="color: #7ebf50">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logResultForm.resultStatus === 'ERROR'" style="color: red;">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </span>
              </div>
            </el-col>
          </el-row>
        </div>
        <el-table :show-header="false" :data="logResultData" class="adjust-table table-content">
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
        <log-form :logForm="logResultForm"/>
      </el-row>
      <template v-slot:footer>
        <dialog-footer
            @cancel="logResultVisible = false"
            @confirm="logResultVisible = false"/>
      </template>
    </el-drawer>
    <!--Result log-->

    <!-- 一键检测选择规则组 -->
    <el-dialog :close-on-click-modal="false"
               :modal-append-to-body="false"
               :title="$t('commons.k8s_scan') + ' | ' + $t('k8s.name') + ':' + accountWithGroup.name"
               :visible.sync="scanVisible"
               class="" width="70%">
      <div v-loading="groupResult.loading">
        <el-card shadow="hover" class="box-card el-box-card">
          <div slot="header" class="clearfix">
              <span>
                <img :src="require(`@/assets/img/platform/${accountWithGroup.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
             &nbsp;&nbsp; {{ accountWithGroup.pluginName }} {{ $t('commons.safety_scan') }}
              </span>
          </div>
          <el-checkbox-group v-model="checkedScans">
            <el-checkbox v-for="(scan, index) in scans" :label="scan.id" :value="scan.id" :key="index" border >
              {{ scan.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-card>
        <el-card shadow="hover" class="box-card el-box-card">
          <div slot="header" class="clearfix">
              <span>
                <img :src="require(`@/assets/img/platform/${accountWithGroup.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
             &nbsp;&nbsp; {{ accountWithGroup.pluginName }} {{ $t('rule.rule_set') }} {{ $t('dashboard.i18n_policy_comliance') }}
              </span>
            <el-button style="float: right; padding: 3px 0" type="text"  @click="handleCheckAllByAccount">{{ $t('account.i18n_sync_all') }}</el-button>
          </div>
          <el-checkbox-group v-model="checkedGroups">
            <el-checkbox v-for="(group, index) in groups" :label="group.id" :value="group.id" :key="index" border >
              {{ group.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-card>
        <dialog-footer
            @cancel="scanVisible = false"
            @confirm="scanGroup()"/>
      </div>
    </el-dialog>
    <!-- 一键检测选择检测组 -->

  </main-container>
</template>

<script>
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperator from "@/business/components/common/components/TableOperator";
import Container from "@/business/components/common/components/Container";
import MainContainer from "@/business/components/common/components/MainContainer";
import TableOperators from "@/business/components/common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {K8S_CONFIGS} from "@/business/components/common/components/search/search-components";
import ProxyDialogFooter from "@/business/components/common/components/ProxyDialogFooter";
import ProxyDialogCreateFooter from "@/business/components/common/components/ProxyDialogCreateFooter";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import HrCodeEdit from "@/business/components/common/components/HrCodeEdit";
import HideTable from "@/business/components/common/hideTable/HideTable";
import LogForm from "@/business/components/k8s/home/LogForm";
import {nativePluginUrl, pluginByIdUrl, proxyListAllUrl} from "@/api/system/system";
import {
  addK8sUrl,
  deleteK8ssUrl,
  deleteK8sUrl,
  getCloudNativeResultUrl,
  getCloudNativeResultWithBLOBsUrl,
  k8sDownloadUrl,
  k8sInstallLogUrl,
  k8sListUrl,
  k8sValidatesUrl,
  k8sValidateUrl,
  kubenchStatusValidateUrl,
  logK8sUrl,
  operatorStatusValidateUrl,
  reinstallKubenchUrl,
  reinstallOperatorUrl,
  scanK8sUrl,
  updateK8sUrl
} from "@/api/k8s/k8s/k8s";
import {saveAs} from "@/common/js/FileSaver";
import {groupsByAccountId} from "@/api/cloud/rule/rule";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'k8s.name',
    props: 'name',
    disabled: false
  },
  {
    label: 'k8s.install_log',
    props: 'log',
    disabled: false
  },
  {
    label: 'image.result_status',
    props: 'status',
    disabled: false
  },
  {
    label: 'k8s.vuln_compliance',
    props: 'result',
    disabled: false
  },
  {
    label: 'k8s.config_compliance',
    props: 'configResult',
    disabled: false
  },
  {
    label: 'k8s.kubench_compliance',
    props: 'kubenchResult',
    disabled: false
  },
  {
    label: 'dashboard.i18n_not_compliance',
    props: 'returnSum',
    disabled: false
  },
  {
    label: 'commons.last_scan_time',
    props: 'scanTime',
    disabled: false
  },
  {
    label: 'account.update_time',
    props: 'updateTime',
    disabled: false
  },
  {
    label: 'account.creator',
    props: 'userName',
    disabled: false
  }
];

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
    ProxyDialogFooter,
    ProxyDialogCreateFooter,
    HrCodeEdit,
    HideTable,
    LogForm,
  },
  provide() {
    return {
      search: this.search,
    }
  },
  data() {
    return {
      credential: {},
      result: {},
      k8sResult: {},
      condition: {
        components: K8S_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      selectIds: new Set(),
      createVisible: false,
      updateVisible: false,
      innerDrawer: false,
      innerDrawerProxy: false,
      plugins: [],
      proxys: [],
      tmpList: [],
      item: {},
      form: {},
      addAccountForm: [ { "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] } ],
      proxyForm: {},
      script: '',
      direction: 'rtl',
      rule: {
        name: [
          {required: true, message: this.$t('commons.input_name'), trigger: 'blur'},
          {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
          {
            required: true,
            message: this.$t("workspace.special_characters_are_not_supported"),
            trigger: 'blur'
          }
        ],
        proxyIp: [
          {required: true, message: this.$t('proxy.proxy_ip'), trigger: 'blur'},
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
        ],
        proxyPort: [
          {required: true, message: this.$t('proxy.proxy_port'), trigger: 'blur'},
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
        ],
        proxyName: [
          {required: false, message: this.$t('proxy.proxy_name'), trigger: 'blur'},
          {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
        ],
        proxyPassword: [
          {required: false, message: this.$t('proxy.proxy_password'), trigger: 'blur'},
          {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
        ],
      },
      buttons: [
        {
          tip: this.$t('k8s.execute_scan'), icon: "el-icon-s-promotion", type: "success",
          exec: this.openScanGroup
        }, {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        }, {
          tip: this.$t('resource.scan_vuln_search'), icon: "el-icon-share", type: "info",
          exec: this.handleVuln
        }, {
          tip: this.$t('resource.download_report'), icon: "el-icon-bottom", type: "warning",
          exec: this.handleDownload
        }, {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      statusFilters: [
        {text: this.$t('account.INVALID'), value: 'INVALID'},
        {text: this.$t('account.VALID'), value: 'VALID'},
        {text: this.$t('account.DELETE'), value: 'DELETE'}
      ],
      proxyType: [
        {id: 'Http', value: "Http"},
        {id: 'Https', value: "Https"},
      ],
      modes: ['text', 'html'],
      content: '注：HummerRisk已实现自动添加，如需手动添加可执行以下命令\n' +
        '\n' +
        '手动添加：\n' +
        '# 1.添加 chart 仓库\n' +
        '# 如果可以访问 github，可以使用 trivy 官方仓库\n' +
        'helm repo add hummer https://aquasecurity.github.io/helm-charts/\n' +
        '# 或\n' +
        '# 如果访问 github 异常，使用国内仓库，使用是一样的\n' +
        'helm repo add hummer https://registry.hummercloud.com/repository/charts\n' +
        '\n' +
        '# 2.更新仓库源\n' +
        'helm repo update\n' +
        '\n' +
        '# 3.开始安装, 可以自定义应用名称和NameSpace, 注意 trivy.serverURL 的 IP 地址需要替换为 Trivy 实际的 IP 地址\n' +
        '如果 hummerrisk 以主机方式运行，则<hummerrisk-trivy-server-ip>为主机 IP。\n' +
        '如果hummerrisk 在 k8s 上运行，则<hummerrisk-trivy-server-ip>和端口为节点IP和 NodePort 端口，若配置的有 ingress 则可配置域名\n' +
        '\n' +
        'helm install trivy-operator hummer/trivy-operator \\\n' +
        '--namespace trivy-system \\\n' +
        '--set trivy.repository="registry.cn-beijing.aliyuncs.com/hummerrisk/trivy" \\\n' +
        '--set trivy.dbRepository="reg.hummercloud.com/trivy/trivy-db" \\\n' +
        '--set trivy.dbRepositoryInsecure="true" \\\n' +
        '--set trivy.ignoreUnfixed=true \\\n' +
        '--set trivy.skipUpdate=true \\\n' +
        '--set image.repository="registry.cn-beijing.aliyuncs.com/hummerrisk/trivy-operator" \\\n' +
        '--set nodeCollector.repository="registry.cn-beijing.aliyuncs.com/hummerrisk/node-collector" \\\n' +
        '--create-namespace \\\n' +
        '\n' +
        '# 4.检测operator是否启动成功\n' +
        'kubectl get pod -A|grep trivy-operator\n' +
        'trivy-system   trivy-operator-69f99f79c4-lvzvs           1/1     Running            0          118s\n',
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
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'k8s.name',
          id: 'name'
        },
        {
          name: 'account.creator',
          id: 'userName'
        }
      ],
      checkAll: true,
      isIndeterminate: false,
      logVisible: false,
      logForm: {},
      logData: [],
      installData: [],
      logPage: 1,
      logSize: 5,
      logTotal: 0,
      logResultVisible: false,
      logResultForm: {},
      logResultData: [],
      detailForm: {},
      scanVisible: false,
      groupResult: {},
      checkedGroups: [],
      checkedScans: ['vuln', 'config', 'kubench'],
      groups: [],
      scans: [
        {id: 'vuln', name: this.$t('commons.vuln_scan')},
        {id: 'config', name: this.$t('commons.config_scan')},
        {id: 'kubench', name: this.$t('commons.kubench_scan')},
      ],
      accountWithGroup: {pluginIcon: 'k8s.png'},
    }
  },
  watch: {
    '$route': 'init'
  },
  methods: {
    handleCheckedColumnNamesChange(value) {
      const checkedCount = value.length;
      this.checkAll = checkedCount === this.columnNames.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.columnNames.length;
      this.checkedColumnNames = value;
    },
    handleCheckAllChange(val) {
      this.checkedColumnNames = val ? this.columnNames.map((ele) => ele.props) : [];
      this.isIndeterminate = false;
      this.checkAll = val;
    },
    handleVuln() {
      window.open('http://www.cnnvd.org.cn/','_blank','');
    },
    create() {
      this.addAccountForm = [ { "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] } ];
      this.createVisible = true;
      this.activePlugin();
      this.activeProxy();
    },
    //查询插件
    activePlugin() {
      this.result = this.$get(nativePluginUrl, response => {
        let data = response.data;
        this.plugins =  data;
      });
    },
    //查询代理
    activeProxy() {
      this.result = this.$get(proxyListAllUrl, response => {
        this.proxys = response.data;
      });
    },
    //校验云原生账号
    validate() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('k8s.please_choose_k8s'));
        return;
      }
      this.$alert(this.$t('account.one_validate') + this.$t('k8s.k8s_setting') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.k8sResult = this.$request({
              method: 'POST',
              url: k8sValidatesUrl,
              data: Array.from(this.selectIds),
              headers: {
                'Content-Type': undefined
              }
            }, res => {
              if (res.data.length == 0) {
                this.$success(this.$t('commons.success'));
              } else {
                let name = '';
                for (let item of res.data) {
                  name = name + ' ' + item.name + ';';
                }
                this.$error(this.$t('k8s.failed_k8s') + name);
              }
              this.search();
            });
          }
        }
      });
    },
    validateK8s(row) {
      this.$alert(this.$t('account.validate') + this.$t('k8s.k8s_setting') + ' : ' + row.name +  " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.k8sResult = this.$post(k8sValidateUrl + row.id, {}, response => {
              let data = response.data;
              if (data) {
                if (data.flag) {
                  this.$success(this.$t('account.success'));
                } else {
                  this.$error(data.message, 10000);
                }
              } else {
                this.$error(this.$t('account.error'), 10000);
              }
              this.search();
            });
          }
        }
      });
    },
    validateOperator(row) {
      this.$alert(this.$t('account.validate') + this.$t('k8s.operator_status') + ' : ' + row.name +  " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.k8sResult = this.$post(operatorStatusValidateUrl + row.id, {}, response => {
              let data = response.data;
              if (data) {
                if (data.flag) {
                  this.$success(this.$t('account.success'));
                  row.operatorStatus = "VALID";
                } else {
                  this.$error(data.message, 10000);
                  row.operatorStatus = "INVALID";
                }
              } else {
                this.$error(this.$t('account.error'));
              }
              this.search();
              this.showLog(row);
            });
          }
        }
      });
    },
    validateKubench(row) {
      this.$alert(this.$t('account.validate') + this.$t('k8s.kubench_status') + ' : ' + row.name +  " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.k8sResult = this.$post(kubenchStatusValidateUrl + row.id, {}, response => {
              let data = response.data;
              if (data) {
                if (data.flag) {
                  this.$success(this.$t('account.success'));
                  row.kubenchStatus = "VALID";
                } else {
                  this.$error(data.message, 10000);
                  row.kubenchStatus = "INVALID";
                }
              } else {
                this.$error(this.$t('account.error'));
              }
              this.search();
              this.showLog(row);
            });
          }
        }
      });
    },
    select(selection) {
      this.selectIds.clear();
      selection.forEach(s => {
        this.selectIds.add(s.id)
      });
    },
    //查询列表
    search() {
      let url = k8sListUrl + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleEdit(tmp) {
      this.form = tmp;
      this.item.credential = tmp.credential;
      if (!this.form.proxyId) {
        this.form.proxyId = "";
      }
      this.updateVisible = true;
      this.activePlugin();
      this.activeProxy();
      this.changePlugin(tmp.pluginId, 'edit');
    },
    handleClose() {
      this.createVisible = false;
      this.updateVisible = false;
      this.logVisible = false;
      this.logResultVisible = false;
    },
    handleDelete(obj) {
      this.$alert(this.$t('commons.delete_confirm') + obj.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$post(deleteK8sUrl + obj.id, {}, () => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    change(e) {
      this.$forceUpdate();
    },
    init() {
      this.selectIds.clear();
      this.search();
    },
    sort(column) {
      _sort(column, this.condition);
      this.init();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.init();
    },
    filterStatus(value, row) {
      return row.status === value;
    },
    filterOperatorStatus(value, row) {
      return row.operatorStatus === value;
    },
    filterKubenchStatus(value, row) {
      return row.kubenchStatus === value;
    },
    //新增云原生账号信息/选择插件查询云原生账号信息
    async changePluginForAdd (form){
      this.k8sResult = await this.$get(pluginByIdUrl + form.pluginId, response => {
        let fromJson = typeof(response.data) === 'string'?JSON.parse(response.data):response.data;
        form.tmpList = fromJson.data;
        for (let tmp of form.tmpList) {
          if (tmp.defaultValue !== undefined) {
            tmp.input = tmp.defaultValue;
          }
        }
      });
    },
    //编辑云原生账号信息/选择插件查询云原生账号信息
    async changePlugin (pluginId, type){
      this.k8sResult = await this.$get(pluginByIdUrl + pluginId, response => {
        let fromJson = typeof(response.data) === 'string'?JSON.parse(response.data):response.data;
        this.tmpList = fromJson.data;
        if (type === 'edit') {
          let credentials = typeof(this.item.credential) === 'string'?JSON.parse(this.item.credential):this.item.credential;
          for (let tmp of this.tmpList) {
            if (credentials[tmp.name] === undefined) {
              tmp.input = tmp.defaultValue?tmp.defaultValue:"";
            } else {
              tmp.input = credentials[tmp.name];
            }
          }
        } else {
          for (let tmp of this.tmpList) {
            if (tmp.defaultValue !== undefined) {
              tmp.input = tmp.defaultValue;
            }
          }
        }
      });
    },
    //保存云原生账号信息
    saveAccount(addAccountForm, type){
      for (let item of addAccountForm) {
        if (!item.tmpList.length) {
          this.$warning(this.$t('commons.no_plugin_param'));
          return;
        }
        let data = {}, key = {};
        for (let tmp of item.tmpList) {
          if(!tmp.input) {
            this.$warning(this.$t('vuln.no_plugin_param') + tmp.label);
            return;
          }
          key[tmp.name] = tmp.input.trim();
        }
        data["credential"] = JSON.stringify(key);
        data["name"] = item.name;
        data["pluginId"] = item.pluginId;
        if (item.isProxy) data["proxyId"] = item.proxyId;

        if (type === 'add') {
          this.k8sResult = this.$post(addK8sUrl, data,response => {
            if (response.success) {
              this.$success(this.$t('commons.create_success'));
              this.search();
              this.handleClose();
            } else {
              this.$error(response.message);
            }
          });
        }
      }
    },
    //编辑云原生账号信息
    editAccount(item, type){
      if (!this.tmpList.length) {
        this.$error(this.$t('account.i18n_account_cloud_plugin_param'));
        return;
      }
      this.$refs['accountForm'].validate(valid => {
        if (valid) {
          let data = {}, key = {};
          for (let tmp of this.tmpList) {
            key[tmp.name] = tmp.input;
          }
          data["credential"] = JSON.stringify(key);
          data["name"] = item.name;
          data["pluginId"] = item.pluginId;
          if (item.isProxy) data["proxyId"] = item.proxyId;

          if (type === 'add') {
            this.k8sResult = this.$post(addK8sUrl, data,response => {
              if (response.success) {
                this.$success(this.$t('commons.create_success'));
                this.search();
                this.handleClose();
              } else {
                this.$error(response.message);
              }
            });
          } else {
            data["id"] = item.id;
            this.k8sResult = this.$post(updateK8sUrl, data,response => {
              if (response.success) {
                this.$success(this.$t('commons.update_success'));
                this.handleClose();
                this.search();
              } else {
                this.$error(response.message);
              }
            });
          }
        } else {
          this.$error(this.$t('rule.full_param'));
          return false;
        }
      });
    },
    addAccount (addAccountForm) {
      let newParam = { "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] };
      addAccountForm.push(newParam);
    },
    deleteAccount (parameter, p) {
      for (let i in parameter) {
        if (parameter[i].name === p.name) {
          parameter.splice(i, 1);
          return;
        }
      }
    },
    searchLogData(item) {
      let url = k8sInstallLogUrl + this.logPage + '/' + this.logSize;
      this.k8sResult = this.$post(url, {id : item.id}, response => {
        let data = response.data;
        this.logData = data.listObject;
        this.logTotal = data.itemCount;
      });
    },
    showLog(item){
      let url = k8sInstallLogUrl + this.logPage + '/' + this.logSize;
      this.k8sResult = this.$post(url, {id : item.id}, response => {
        let data = response.data;
        this.logData = data.listObject;
        this.logTotal = data.itemCount;
      });
      this.logForm = item;
      this.installData = [];
      this.installData.push(item);
      this.logVisible = true;
    },
    reinstallOperator(item){
      this.$alert(this.$t('k8s.k8s_setting') + this.$t('k8s.reinstall_operator') + ' : ' + item.name +  " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.k8sResult = this.$post(reinstallOperatorUrl + item.id, {}, response => {
              this.$success(this.$t('commons.success'));
              this.showLog(item);
            });
          }
        }
      });
    },
    tableRowClassName({row, rowIndex}) {
      if (this.tableRow) {
        if (rowIndex % 4 === 0) {
          return 'success-row';
        } else if (rowIndex % 2 === 0) {
          return 'warning-row';
        } else {
          return '';
        }
      }
    },
    reinstallKubench(item){
      this.$alert(this.$t('k8s.k8s_setting') + this.$t('k8s.reinstall_operator') + ' : ' + item.name +  " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.k8sResult = this.$post(reinstallKubenchUrl + item.id, {}, response => {
              this.$success(this.$t('commons.success'));
              this.showLog(item);
            });
          }
        }
      });
    },
    goResource (params) {
      if (!params.resultId) {
        this.$warning(this.$t('resource.i18n_no_warn'));
        return;
      }
      if (params.returnSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      let p = '/k8s/result-details/' + params.resultId;
      this.$router.push({
        path: p
      }).catch(error => error);
    },
    goCloudResource (params) {
      if (params.cloudResourcesSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      let p = '/k8s/cloud-result-details/' + params.id;
      this.$router.push({
        path: p
      }).catch(error => error);
    },
    goConfigResource (params) {
      if (!params.resultId) {
        this.$warning(this.$t('resource.i18n_no_warn'));
        return;
      }
      if (params.returnConfigSum == 0) {
        this.$warning(this.$t('resource.no_resources_allowed'));
        return;
      }
      let p = '/k8s/result-config-details/' + params.resultId;
      this.$router.push({
        path: p
      }).catch(error => error);
    },
    goKubenchResource (params) {
      if (!params.resultId) {
        this.$warning(this.$t('resource.i18n_no_warn'));
        return;
      }
      let p = '/k8s/result-kubench-details/' + params.resultId;
      this.$router.push({
        path: p
      }).catch(error => error);
    },
    async showResultLog (result) {
      if (!result.resultId) {
        this.$warning(this.$t('resource.i18n_no_warn'));
        return;
      }
      this.k8sResult = this.$get(logK8sUrl + result.resultId, response => {
        this.logResultData = response.data;
      });
      this.k8sResult = await this.$get(getCloudNativeResultWithBLOBsUrl + result.resultId, response => {
        this.logResultForm = response.data;
        this.logResultForm.vulnerabilityReport = JSON.parse(this.logResultForm.vulnerabilityReport);
        this.logResultForm.configAuditReport = JSON.parse(this.logResultForm.configAuditReport);
      });
      this.logResultVisible = true;
    },
    noWarnLog(item) {
      this.$warning(item.name + this.$t('resource.i18n_no_warn'));
    },
    getStatus () {
      if (this.checkStatus(this.tableData)) {
        this.search();
        clearInterval(this.timer);
      } else {
        for (let data of this.tableData) {
          this.$get(getCloudNativeResultUrl + data.resultId, response => {
            let result = response.data;
            if (result && data.resultStatus !== result.resultStatus) {
              data.resultStatus = result.resultStatus;
              data.returnSum = result.returnSum;
              data.critical = result.critical;
              data.high = result.high;
              data.medium = result.medium;
              data.low = result.low;
              data.unknown = result.unknown;
              data.fail = result.fail;
              data.warn = result.warn;
              data.info = result.info;
              data.pass = result.pass;
              data.cloudResourcesSum = result.cloudResourcesSum;
              data.cloudReturnSum = result.cloudReturnSum;
            }
          });
        }
      }
    },
    //是否是结束状态，返回false代表都在运行中，true代表已结束
    checkStatus (tableData) {
      let sum = 0;
      for (let row of tableData) {
        if (row.resultStatus && row.resultStatus != 'ERROR' && row.resultStatus != 'FINISHED' && row.resultStatus != 'WARNING') {
          sum++;
        }
      }
      return sum == 0;
    },
    handleDownload(item) {
      this.result = this.$post(k8sDownloadUrl, {
        id: item.id
      }, response => {
        if (response.success) {
          let blob = new Blob([response.data], { type: "application/json" });
          saveAs(blob, item.name + ".json");
        }
      }, error => {
        console.log("下载报错", error);
      });
    },
    scanGroup () {
      if (this.accountWithGroup.status === 'INVALID') {
        this.$warning(item.name + ':' + this.$t('k8s.failed_status'));
        return;
      }
      let account = this.$t('account.one_scan') + ' Kubernetes';
      this.$alert( account + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            let params1 = {
              id: this.accountWithGroup.id,
              resultId: this.accountWithGroup.resultId?this.accountWithGroup.resultId:"",
              k8sGroups: this.checkedScans,
              ruleGroups: this.checkedGroups
            }
            this.groupResult = this.$post(scanK8sUrl, params1,response => {
              if (response.success) {
                this.$success(this.$t('schedule.event_start'));
                this.scanVisible = false;
                this.search();
              } else {
                this.$error(response.message);
              }
            });
          }
        }
      });
    },
    initGroups(pluginId) {
      this.result = this.$get(groupsByAccountId + pluginId,response => {
        this.groups = response.data;
        this.scanVisible = true;
        this.checkedGroups = [];
        this.checkedScans = ['vuln', 'config', 'kubench'];
      });
    },
    openScanGroup(account) {
      if (account.status === 'INVALID') {
        this.$warning(account.name + ':' + this.$t('account.failed_status'));
        return;
      }
      this.accountWithGroup = account;
      this.initGroups(account.pluginId);
    },
    handleCheckAllByAccount() {
      if (this.checkedGroups.length === this.groups.length) {
        this.checkedGroups = [];
      } else {
        let arr = [];
        this.checkedGroups = [];
        for (let group of this.groups) {
          arr.push(group.id);
        }
        let concatArr = this.checkedGroups.concat(arr);
        this.checkedGroups = Array.from(concatArr);
      }
    },
    deleteBatch() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('commons.please_select') + this.$t('account.k8s'));
        return;
      }
      this.$alert(this.$t('oss.delete_batch') + this.$t('account.k8s') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$request({
              method: 'POST',
              url: deleteK8ssUrl,
              data: Array.from(this.selectIds),
              headers: {
                'Content-Type': undefined
              }
            }, res => {
              this.$success(this.$t('commons.success'));
              this.search();
            });
          }
        }
      });
    },
  },
  computed: {
    codemirror() {
      return this.$refs.cmEditor.codemirror;
    }
  },
  created() {
    this.init();
    this.timer = setInterval(this.getStatus, 10000);
  },
  beforeDestroy() {
    clearInterval(this.timer);
  }
}
</script>

<style scoped>
.table-content {
  width: 100%;
}

.el-table {
  cursor: pointer;
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

.rtl >>> .el-drawer__body {
  overflow-y: auto;
  padding: 20px;
}
.rtl >>> input {
  width: 100%;
}
.rtl >>> .el-select {
  width: 80%;
}
.rtl >>> .el-form-item__content {
  width: 75%;
}
.code-mirror {
  height: 600px !important;
}
.code-mirror >>> .CodeMirror {
  /* Set height, width, borders, and global font properties here */
  height: 600px !important;
}
/deep/ :focus{outline:0;}
.el-box-card {
  margin: 10px 0;
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
  width: 20%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
}

.el-form-item-dev >>> .bg-purple-light {
  background: #f2f2f2;
}

.el-form-item-dev >>> .bg-purple-light-log {
  background: #dddddd;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.box-card >>> .clearfix-dev {
  background-color: aliceblue;
  padding: 18px 20px;
  border-bottom: 1px solid #ebeef5;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}
.table-content {
  width: 100%;
}

.el-table {
  cursor: pointer;
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

.rtl >>> .el-drawer__body {
  overflow-y: auto;
  padding: 20px;
}
.rtl >>> input {
  width: 100%;
}
.rtl >>> .el-select {
  width: 80%;
}
.rtl >>> .el-form-item__content {
  width: 75%;
}
.code-mirror {
  height: 600px !important;
}
.code-mirror >>> .CodeMirror {
  /* Set height, width, borders, and global font properties here */
  height: 600px !important;
}
/deep/ :focus{outline:0;}
.el-box-card {
  margin: 10px 0;
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
  width: 20%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
}

.el-form-item-dev >>> .bg-purple-light {
  background: #f2f2f2;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.box-card >>> .clearfix-dev {
  background-color: aliceblue;
  padding: 18px 20px;
  border-bottom: 1px solid #ebeef5;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}

.table-content {
  width: 100%;
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
.rtl >>> .el-drawer__body {
  overflow-y: auto;
  padding: 20px;
}
.rtl >>> input {
  width: 100%;
}
.rtl >>> .el-select {
  width: 80%;
}
.rtl >>> .el-form-item__content {
  width: 75%;
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
  width: 39%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0 6px 2px;
  color: #606266;
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
.txt-click {
  cursor:pointer;
}
.txt-click:hover {
  color: aliceblue;
  text-shadow: 1px 1px 1px #000;
}
.el-box-card >>> .el-checkbox {
  margin: 5px 0;
}
</style>

