<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('oss.oss_account_list')"
                      @create="create" :createTip="$t('oss.create')"
                      @validate="validate" :validateTip="$t('account.one_validate')"
                      :show-validate="true" :show-create="true"
                      :items="items" :columnNames="columnNames"
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
        <el-table-column type="selection" min-width="50">
        </el-table-column>
        <el-table-column type="index" min-width="50"/>
        <el-table-column prop="name" :label="$t('oss.name')" v-if="checkedColumnNames.includes('name')" min-width="150" show-overflow-tooltip></el-table-column>
        <el-table-column :label="$t('account.cloud_platform')" v-if="checkedColumnNames.includes('pluginName')" min-width="150" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.pluginName }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" min-width="120" v-if="checkedColumnNames.includes('status')" :label="$t('account.status')"
                         column-key="status"
                         :filters="statusFilters"
                         :filter-method="filterStatus">
          <template v-slot:default="{row}">
            <div @click="validateRow(row)">
              <el-tag size="mini" type="warning" v-if="row.status === 'DELETE'">
                {{ $t('account.DELETE') }}
              </el-tag>
              <el-tag size="mini" type="success" v-else-if="row.status === 'VALID'">
                {{ $t('account.VALID') }}
              </el-tag>
              <el-tag size="mini" type="danger" v-else-if="row.status === 'INVALID'">
                {{ $t('account.INVALID') }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('syncStatus')" :label="$t('event.sync_status')" min-width="170" prop="status" sortable
                         show-overflow-tooltip>
          <el-button @click="showLog(scope.row)" plain size="mini" type="primary"
                     v-if="scope.row.syncStatus === 'UNCHECKED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showLog(scope.row)" plain size="mini" type="primary"
                     v-else-if="scope.row.syncStatus === 'APPROVED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showLog(scope.row)" plain size="mini" type="primary"
                     v-else-if="scope.row.syncStatus === 'PROCESSING'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showLog(scope.row)" plain size="mini" type="success"
                     v-else-if="scope.row.syncStatus === 'FINISHED'">
            <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
          </el-button>
          <el-button @click="showLog(scope.row)" plain size="mini" type="danger"
                     v-else-if="scope.row.syncStatus === 'ERROR'">
            <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
          </el-button>
          <el-button @click="showLog(scope.row)" plain size="mini" type="warning"
                     v-else-if="scope.row.syncStatus === 'WARNING'">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
          </el-button>
        </el-table-column>
        <el-table-column :label="$t('oss.bucket')" v-if="checkedColumnNames.includes('sum')" min-width="110">
          <template v-slot:default="scope">
            <el-link type="primary" @click="showBuckets(scope.row)">
              {{ scope.row.sum }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column min-width="180" v-if="checkedColumnNames.includes('createTime')" :label="$t('account.create_time')" sortable
                         prop="createTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('account.regions')" v-if="checkedColumnNames.includes('regions')" min-width="100">
          <template v-slot:default="scope">
            <regions :row="scope.row"/>
          </template>
        </el-table-column>
        <el-table-column min-width="180" v-if="checkedColumnNames.includes('updateTime')" :label="$t('account.update_time')" sortable
                         prop="updateTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" :label="$t('account.creator')" v-if="checkedColumnNames.includes('userName')" min-width="110" show-overflow-tooltip/>
        <el-table-column min-width="200" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--oss account-->
    <el-drawer class="rtl" :title="ossTitle" :visible.sync="visible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <div v-loading="cloudResult.loading">
        <el-form :model="form" label-position="right" label-width="150px" size="small" :rules="rule" ref="form">
          <el-form-item :label="$t('account.cloud_account')" :rules="{required: true, message: $t('account.cloud_account') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" :disabled="ossTitle!=$t('oss.create')" v-model="form.id" :placeholder="$t('account.please_choose_account')" @change="changeAccount(form.id)">
              <el-option
                v-for="item in accounts"
                :key="item.id"
                :label="item.name"
                :value="item.id">
                <img :src="require(`@/assets/img/platform/${item.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                &nbsp;&nbsp; {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item>
          <div v-for="(tmp, index) in tmpList" :key="index">
            <el-form-item v-if="tmp.inputType === 'password'" :label="tmp.label" style="margin-bottom: 29px">
              <el-input :type="tmp.inputType" v-model="tmp.input" @input="change($event)" autocomplete="new-password" show-password :placeholder="tmp.description"/>
            </el-form-item>
            <el-form-item v-if="tmp.inputType !== 'password' && tmp.inputType !== 'boolean'" :label="tmp.label">
              <el-input :type="tmp.inputType" v-model="tmp.input" @input="change($event)" autocomplete="off" :placeholder="tmp.description"/>
            </el-form-item>
          </div>
          <el-form-item v-if="form.isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="form.proxyId" :placeholder="$t('commons.proxy')">
              <el-option
                v-for="item in proxys"
                :key="item.id"
                :label="item.proxyIp"
                :value="item.id">
                &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-if="iamStrategyNotSupport.indexOf(form.pluginId) === -1" :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-switch v-model="form.isProxy"></el-switch>
          </el-form-item>
          <el-form-item v-if="script">
            <el-link v-if="iamStrategyNotSupport.indexOf(form.pluginId) === -1" type="danger" @click="innerDrawer = true">{{ $t('account.iam_strategy') }}</el-link>
            <div v-if="iamStrategyNotSupport.indexOf(form.pluginId) === -1">
              <el-drawer
                size="45%"
                :title="$t('account.iam_strategy')"
                :append-to-body="true"
                :before-close="innerDrawerClose"
                :visible.sync="innerDrawer">
                <el-form-item>
                  <codemirror ref="cmEditor" v-model="script" class="code-mirror" :options="cmOptions" />
                </el-form-item>
              </el-drawer>
            </div>
          </el-form-item>
        </el-form>
        <div style="color: red;font-style:italic;margin: 5px 0 10px 50px;">{{ $t('oss.cloud_note') }}</div>
        <dialog-footer
          @cancel="visible = false"
          @add="innerDrawerProxy = false"
          @confirm="saveOss(form, ossType)"/>
      </div>
    </el-drawer>
    <!--oss account-->

    <!--oss log-->
    <el-drawer class="rtl" :title="$t('oss.log_list')" :visible.sync="logVisible" size="65%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-row class="el-form-item-dev" v-if="logData.length == 0">
        <span>{{ $t('resource.i18n_no_data') }}<br></span>
      </el-row>
      <el-row class="el-form-item-dev" v-if="logData.length > 0">
        <div>
          <el-row>
            <el-col :span="24">
              <div class="grid-content bg-purple-light">
                <span class="grid-content-log-span">
                  <img :src="require(`@/assets/img/platform/${logForm.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                  {{ logForm.name }}
                </span>
                <span class="grid-content-log-span">
                  {{ $t('oss.bucket') }} : {{ logForm.sum }}
                </span>
                <span class="grid-content-status-span" v-if="logForm.status === 'APPROVED'" style="color: #579df8">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logForm.status === 'FINISHED'" style="color: #7ebf50">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logForm.status === 'ERROR'" style="color: red;">
                  <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                </span>
              </div>
            </el-col>
          </el-row>
        </div>
        <el-table :show-header="false" :data="logData" class="adjust-table table-content">
          <el-table-column>
            <template v-slot:default="scope">
              <div class="bg-purple-div">
                <span
                  v-bind:class="{true: 'color-red', false: ''}[scope.row.result == false]">
                      {{ scope.row.createTime | timestampFormatDate }}
                      {{ scope.row.operator }}
                      {{ scope.row.operation }}
                      {{ scope.row.output }}
                      : {{ scope.row.sum }}<br>
                </span>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-row>
      <template v-slot:footer>
        <dialog-footer
          @cancel="logVisible = false"
          @confirm="logVisible = false"/>
      </template>
    </el-drawer>
    <!--oss log-->

    <!--oss bucket-->
    <el-drawer class="rtl" :title="$t('oss.oss_bucket')" :visible.sync="bucketVisible" size="90%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <table-header :condition.sync="bucketCondition" @search="searchBuckets"
                    :show-name="false"
                    :items="items2" :columnNames="columnNames2"
                    :checkedColumnNames="checkedColumnNames2" :checkAll="checkAll2" :isIndeterminate="isIndeterminate2"
                    @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange2" @handleCheckAllChange="handleCheckAllChange2"/>
      <hide-table
        :table-data="bucketData"
        @sort-change="sort"
        @filter-change="filter"
        @select-all="select"
        @select="select"
      >
        <el-table-column type="index" min-width="50"/>
        <el-table-column prop="bucketName" v-if="checkedColumnNames2.includes('bucketName')" :label="$t('oss.bucket')" min-width="140" show-overflow-tooltip v-slot:default="scope">
          <el-link type="primary" @click="showObject(scope.row)">
            {{ scope.row.bucketName }}
          </el-link>
        </el-table-column>
        <el-table-column :label="$t('oss.name')" v-if="checkedColumnNames2.includes('name')" min-width="110" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="location" :label="$t('oss.location')" v-if="checkedColumnNames2.includes('location')" min-width="110" show-overflow-tooltip></el-table-column>
        <el-table-column prop="cannedAcl" :label="$t('oss.acl')" v-if="checkedColumnNames2.includes('cannedAcl')" min-width="110" show-overflow-tooltip v-slot:default="scope">
          <span v-if="scope.row.cannedAcl === 'public-read-write' || scope.row.cannedAcl === 'PublicReadWrite'">{{ $t('oss.public_read_write') }}</span>
          <span v-else-if="scope.row.cannedAcl === 'public-read' || scope.row.cannedAcl === 'PublicRead'">{{ $t('oss.public_read') }}</span>
          <span v-else-if="scope.row.cannedAcl === 'private' || scope.row.cannedAcl === 'Private'">{{ $t('oss.private') }}</span>
          <span v-else>{{ scope.row.cannedAcl }}</span>
        </el-table-column>
        <el-table-column prop="storageClass" :label="$t('oss.storage_class')" v-if="checkedColumnNames2.includes('storageClass')" min-width="110" show-overflow-tooltip v-slot:default="scope">
          <span v-if="scope.row.storageClass === 'Standard' || scope.row.storageClass === 'STANDARD'">{{ $t('oss.standard') }}</span>
          <span v-else-if="scope.row.storageClass === 'IA' || scope.row.storageClass === 'STANDARD_IA' || scope.row.storageClass === 'WARM'">{{ $t('oss.ia') }}</span>
          <span v-else-if="scope.row.storageClass === 'Archive' || scope.row.storageClass === 'ARCHIVE'">{{ $t('oss.archive') }}</span>
          <span v-else-if="scope.row.storageClass === 'COLD'">{{ $t('oss.cold') }}</span>
          <span v-else>{{ scope.row.storageClass }}</span>
        </el-table-column>
        <el-table-column prop="size" :label="$t('oss.oss_size')" v-if="checkedColumnNames2.includes('size')" min-width="100" show-overflow-tooltip></el-table-column>
        <el-table-column prop="objectNumber" :label="$t('oss.object_number')" v-if="checkedColumnNames2.includes('objectNumber')" min-width="100" show-overflow-tooltip></el-table-column>
      </hide-table>
      <table-pagination :change="searchBuckets" :current-page.sync="bucketPage" :page-size.sync="bucketPageSize" :total="bucketTotal"/>

      <el-drawer
        size="75%"
        :title="$t('oss.oss_object')"
        :append-to-body="true"
        :before-close="innerDrawerClose"
        :visible.sync="innerDrawer">
        <el-table border :data="objectData" class="adjust-table table-content" @sort-change="sort" stripe>
          <el-table-column type="index" min-width="1%"></el-table-column>
          <el-table-column prop="objectName" :label="$t('oss.object_name')" min-width="20%" show-overflow-tooltip v-slot:default="scope">
            <el-link v-if="scope.row.objectType==='BACK'" type="primary" style="color: red;" @click="backObject(scope.row)">
              <i class="el-icon-back"></i>  {{ scope.row.objectName }}
            </el-link>
            <el-link v-if="scope.row.objectType==='DIR'" type="primary" @click="selectObject(scope.row)">
              <i class="el-icon-folder-opened"></i>  {{ scope.row.objectName }}
            </el-link>
            <span v-if="scope.row.objectType==='FILE'" style="color: #336d9f">
              <i class="el-icon-document"></i> {{ scope.row.objectName }}
            </span>
          </el-table-column>
          <el-table-column prop="objectType" :label="$t('oss.object_type')" min-width="8%" show-overflow-tooltip v-slot:default="scope">
            <span v-if="scope.row.objectType==='DIR'">{{ $t('oss.object_dir') }}</span>
            <span v-if="scope.row.objectType==='FILE'">{{ $t('oss.object_file') }}</span>
            <span v-if="scope.row.objectType==='BACK'">{{ $t('vis.back') }}</span>
          </el-table-column>
          <el-table-column prop="objectSize" :label="$t('oss.oss_size')" min-width="10%" show-overflow-tooltip v-slot:default="scope">
            {{ scope.row.objectSize?scope.row.objectSize:'-' }}
          </el-table-column>
          <el-table-column prop="storageClass" :label="$t('oss.storage_class')" min-width="10%" show-overflow-tooltip v-slot:default="scope">
            <span v-if="scope.row.storageClass === 'Standard' || scope.row.storageClass === 'STANDARD'">{{ $t('oss.standard') }}</span>
            <span v-else-if="scope.row.storageClass === 'IA' || scope.row.storageClass === 'STANDARD_IA' || scope.row.storageClass === 'WARM'">{{ $t('oss.ia') }}</span>
            <span v-else-if="scope.row.storageClass === 'Archive' || scope.row.storageClass === 'ARCHIVE'">{{ $t('oss.archive') }}</span>
            <span v-else-if="scope.row.storageClass === 'COLD'">{{ $t('oss.cold') }}</span>
            <span v-else>{{ scope.row.storageClass }}</span>
          </el-table-column>
          <el-table-column min-width="15%" :label="$t('account.update_time')" sortable prop="lastModified">
            <template v-slot:default="scope">
              <span v-if="scope.row.lastModified">{{ scope.row.lastModified | timestampFormatDate }}</span>
              <span v-if="!scope.row.lastModified">{{ '-' }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-drawer>

    </el-drawer>
    <!--oss bucket-->

    <!-- 一键检测选择规则组 -->
    <el-dialog :close-on-click-modal="false"
               :modal-append-to-body="false"
               :title="$t('account.scan_group_quick')"
               :visible.sync="scanVisible"
               class="" width="70%">
      <div v-loading="groupResult.loading">
        <el-card class="box-card el-box-card">
          <div slot="header" class="clearfix">
              <span>
                <img :src="require(`@/assets/img/platform/${accountWithGroup.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
             &nbsp;&nbsp; {{ accountWithGroup.pluginName }} {{ $t('rule.rule_set') }} | {{ accountWithGroup.name }}
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
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import {OSS_BUCKET_CONFIGS, OSS_CONFIGS} from "@/business/components/common/components/search/search-components";
import {ACCOUNT_ID, ACCOUNT_NAME} from "@/common/js/constants";
import {saveAs} from "@/common/js/FileSaver";
import Regions from "@/business/components/account/home/Regions";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {proxyListAllUrl} from "@/api/system/system";
import {
  addOssUrl,
  deleteOssUrl,
  ossAccountsUrl, ossBatchSyncUrl, ossBucketListUrl,
  ossChangeAccountUrl,
  ossDownloadObjectUrl,
  ossGroupsUrl, ossIamStrategyUrl, ossListUrl,
  ossLogUrl, ossObjectsUrl,
  ossValidateUrl, updateOssUrl
} from "@/api/cloud/oss/oss";
import {ruleScanUrl} from "@/api/cloud/rule/rule";
import {getAccountUrl} from "@/api/cloud/account/account";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'oss.name',
    props: 'name',
    disabled: false
  },
  {
    label: 'account.cloud_platform',
    props: 'pluginName',
    disabled: false
  },
  {
    label: 'account.status',
    props: 'status',
    disabled: false
  },
  {
    label: 'event.sync_status',
    props: 'syncStatus',
    disabled: false
  },
  {
    label: 'oss.bucket',
    props: 'sum',
    disabled: false
  },
  {
    label: 'account.create_time',
    props: 'createTime',
    disabled: false
  },
  {
    label: 'account.regions',
    props: 'regions',
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
const columnOptions2 = [
  {
    label: 'oss.bucket',
    props: 'bucketName',
    disabled: false
  },
  {
    label: 'oss.name',
    props: 'name',
    disabled: false
  },
  {
    label: 'oss.location',
    props: 'location',
    disabled: false
  },
  {
    label: 'oss.acl',
    props: 'cannedAcl',
    disabled: false
  },
  {
    label: 'oss.storage_class',
    props: 'storageClass',
    disabled: false
  },
  {
    label: 'oss.oss_size',
    props: 'size',
    disabled: false
  },
  {
    label: 'oss.object_number',
    props: 'objectNumber',
    disabled: false
  },
];

/* eslint-disable */
export default {
  components: {
    TableOperators,
    MainContainer,
    Container,
    TableHeader,
    TablePagination,
    DialogFooter,
    Regions,
    HideTable,
  },
  data() {
    return {
      credential: {},
      result: {},
      cloudResult: {},
      groupResult: {},
      condition: {
        components: OSS_CONFIGS
      },
      bucketCondition: {
        components: OSS_BUCKET_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      script: '',
      direction: 'rtl',
      form: { "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] },
      visible: false,
      innerDrawer: false,
      accounts: [],
      proxys: [],
      tmpList: [],
      item: {},
      iamStrategyNotSupport: ['hummer-openstack-plugin', 'hummer-vsphere-plugin', 'hummer-server-plugin'],
      proxyForm: {},
      proxyType: [
        {id: 'Http', value: "Http"},
        {id: 'Https', value: "Https"},
      ],
      ossType: 'add',
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
          tip: this.$t('account.one_scan'), icon: "el-icon-s-promotion", type: "success",
          exec: this.handleScan
        },
        {
          tip: this.$t('commons.sync'), icon: "el-icon-refresh-right", type: "warning",
          exec: this.handleSync
        }, {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        }, {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      ossTitle: this.$t('oss.create'),
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
      logVisible: false,
      logData: [],
      logForm: {},
      bucketVisible: false,
      bucketData: [],
      bucketPage: 1,
      bucketPageSize: 10,
      bucketTotal: 0,
      ossId: "",
      objectData: [],
      path: "/",
      thisObject: {},
      statusFilters: [
        {text: this.$t('account.INVALID'), value: 'INVALID'},
        {text: this.$t('account.VALID'), value: 'VALID'},
        {text: this.$t('account.DELETE'), value: 'DELETE'}
      ],
      accountWithGroup: {pluginIcon: 'aliyun.png'},
      scanVisible: false,
      groups: [],
      checkedGroups: [],
      selectIds: new Set(),
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'oss.name',
          id: 'name'
        },
        {
          name: 'account.creator',
          id: 'userName'
        }
      ],
      checkAll: true,
      isIndeterminate: false,
      checkedColumnNames2: columnOptions2.map((ele) => ele.props),
      columnNames2: columnOptions2,
      //名称搜索
      items2: [
        {
          name: 'oss.name',
          id: 'name'
        },
        {
          name: 'oss.bucket',
          id: 'bucketName',
        }
      ],
      checkAll2: true,
      isIndeterminate2: false,
    }
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
    handleCheckedColumnNamesChange2(value) {
      const checkedCount = value.length;
      this.checkAll2 = checkedCount === this.columnNames2.length;
      this.isIndeterminate2 = checkedCount > 0 && checkedCount < this.columnNames2.length;
      this.checkedColumnNames2 = value;
    },
    handleCheckAllChange2(val) {
      this.checkedColumnNames2 = val ? this.columnNames2.map((ele) => ele.props) : [];
      this.isIndeterminate2 = false;
      this.checkAll2 = val;
    },
    //查询列表
    search() {
      let url = ossListUrl + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleClose() {
      this.visible =  false;
      this.logVisible = false;
      this.bucketVisible = false;
    },
    showRegions (tmp) {
      this.regions = tmp.regions;
    },
    //查询可以添加的云账号到对象存储账号列表中
    activeAccount() {
      this.result = this.$get(ossAccountsUrl, response => {
        let data = response.data;
        this.accounts =  data;
      });
    },
    //查询代理
    activeProxy() {
      this.result = this.$get(proxyListAllUrl, response => {
        this.proxys = response.data;
      });
    },
    init() {
      this.selectIds.clear();
      this.search();
      this.activeAccount();
      this.activeProxy();
    },
    select(selection) {
      this.selectIds.clear();
      selection.forEach(s => {
        this.selectIds.add(s.id)
      });
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
    create() {
      this.form = { "name":"", "pluginId": "", "isProxy": false, "proxyId": "", "script": "", "tmpList": [] };
      this.tmpList = [];
      this.ossTitle = this.$t('oss.create');
      this.visible = true;
    },
    handleEdit(item) {
      this.ossTitle = this.$t('oss.update');
      this.$get(ossIamStrategyUrl + item.id,res1 => {
        this.script = res1.data;
      });
      this.cloudResult = this.$get(ossChangeAccountUrl + item.id, response => {
        let fromJson = typeof(response.data) === 'string'?JSON.parse(response.data):response.data;
        let data = fromJson.data;
        this.form = item;
        let credentials = typeof(item.credential) === 'string'?JSON.parse(item.credential):item.credential;
        this.tmpList = data;
        for (let tmp of this.tmpList) {
          if (credentials[tmp.name] === undefined) {
            tmp.input = tmp.defaultValue?tmp.defaultValue:"";
          } else {
            tmp.input = credentials[tmp.name];
          }
        }
      });
      this.visible = true;
    },
    //选择插件查询云账号信息
    async changeAccount (accountId){
      this.$get(ossIamStrategyUrl + accountId,res1 => {
        this.script = res1.data;
      });
      this.cloudResult = await this.$get(ossChangeAccountUrl + accountId, response => {
        let fromJson = typeof(response.data) === 'string'?JSON.parse(response.data):response.data;
        let data = fromJson.data;
        this.$get(getAccountUrl + accountId,res => {
          this.form = res.data;
          let credentials = typeof(res.data.credential) === 'string'?JSON.parse(res.data.credential):res.data.credential;
          this.tmpList = data;
          for (let tmp of this.tmpList) {
            if (credentials[tmp.name] === undefined) {
              tmp.input = tmp.defaultValue?tmp.defaultValue:"";
            } else {
              tmp.input = credentials[tmp.name];
            }
          }
        });
      });
    },
    change(e) {
      this.$forceUpdate();
    },
    innerDrawerClose() {
      this.innerDrawer = false;
    },
    //编辑oss账号
    saveOss(item, type){
      if (!this.tmpList.length) {
        this.$warning(this.$t('account.i18n_account_cloud_plugin_param'));
        return;
      }
      this.$refs['form'].validate(valid => {
        if (valid) {
          let key = {};
          for (let tmp of this.tmpList) {
            if(!tmp.input) {
              this.$warning(this.$t('vuln.no_plugin_param') + tmp.label);
              return;
            }
            key[tmp.name] = tmp.input;
          }
          item["credential"] = JSON.stringify(key);
          item["name"] = item.name;
          item["pluginId"] = item.pluginId;

          if (type === 'add') {
            this.cloudResult = this.$post(addOssUrl, item,response => {
              if (response.success) {
                this.$success(this.$t('account.i18n_hr_create_success'));
                this.search();
                this.handleClose();
              } else {
                this.$error(response.message);
              }
            });
          } else {
            this.cloudResult = this.$post(updateOssUrl, item,response => {
              if (response.success) {
                this.$success(this.$t('account.i18n_hr_update_success'));
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
    showLog (item) {
      this.result = this.$get(ossLogUrl + item.id, response => {
        this.logData = response.data;
        this.logForm = item;
      });
      this.logVisible = true;
    },
    handleSync(item) {
      this.result = this.$get(ossBatchSyncUrl + item.id, response => {
        if(response.success) {
          this.$success(this.$t('event.sync'));
          this.search();
        }
      });
    },
    handleDelete(item) {
      this.$alert(this.$t('code.delete_confirm') + this.$t('oss.oss_setting') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(deleteOssUrl + item.id,  res => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    getStatus() {
      if (this.checkStatus(this.tableData)) {
        this.search();
        clearInterval(this.timer);
      }
      let url = ossListUrl + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        for (let data of response.data.listObject) {
          for (let item of this.tableData) {
            if (data.id == item.id) {
              item.syncStatus = data.syncStatus;
              item.sum = data.sum;
            }
          }
        }
      });
    },
    //是否是结束状态，返回false代表都在运行中，true代表已结束
    checkStatus(tableData) {
      let sum = 0;
      for (let row of tableData) {
        if (row.syncStatus != 'ERROR' && row.syncStatus != 'FINISHED' && row.syncStatus != 'WARNING') {
          sum++;
        }
      }
      return sum == 0;
    },
    showBuckets(item) {
      this.ossId = item.id;
      this.searchBuckets();
      this.bucketVisible = true;
    },
    searchBuckets() {
      let url = ossBucketListUrl + this.bucketPage + "/" + this.bucketPageSize;
      this.bucketCondition.ossId = this.ossId;
      this.result = this.$post(url, this.bucketCondition, response => {
        let data = response.data;
        this.bucketTotal = data.itemCount;
        this.bucketData = data.listObject;
      });
    },
    showObject(bucket) {
      this.path = '/';
      this.result = this.$get(ossObjectsUrl + bucket.id, response => {
        this.objectData = response.data;
        this.innerDrawer = true;
      });
    },
    getObjects(path) {
      if (path !== '' && path !== 'none') {
        this.path = path;
        this.result = this.$post(ossObjectsUrl + this.thisObject.bucketId, { "path" : path=="/"?"":path}, response => {
          this.objectData = response.data;
          this.innerDrawer = true;
        });
      }
    },
    backObject(item) {
      if (this.path === '/') {
        this.showObject(item);
      } else {
        this.thisObject = item;
        this.getObjects(item.id);
      }
    },
    selectObject(item) {
      this.thisObject = item;
      this.getObjects(item.id);
    },
    //校验云账号
    validate() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('account.please_choose_account'));
        return;
      }
      this.$alert(this.$t('account.one_validate') + this.$t('oss.oss_setting') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$request({
              method: 'POST',
              url: ossValidateUrl,
              data: Array.from(this.selectIds),
              headers: {
                'Content-Type': undefined
              }
            }, res => {
              if (res.data) {
                this.$success(this.$t('account.success'));
              } else {
                this.$error(this.$t('account.error'));
              }
              this.search();
            });
          }
        }
      });
    },
    validateRow(row) {
      this.$alert(this.$t('account.validate') + this.$t('oss.oss_setting') + ' : ' + row.name +  " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$post(ossValidateUrl + row.id, {}, response => {
              let data = response.data;
              if (data) {
                if (data.flag) {
                  this.$success(this.$t('server.success'));
                } else {
                  this.$error(data.message, 10000);
                }
              } else {
                this.$error(this.$t('account.error'));
              }
              this.search();
            });
          }
        }
      });
    },
    handleScan(account) {
      this.accountWithGroup = account;
      localStorage.setItem(ACCOUNT_ID, account.id);
      localStorage.setItem(ACCOUNT_NAME, account.name);
      this.initGroups(account.pluginId);
      this.scanVisible = true;
    },
    initGroups(pluginId) {
      this.result = this.$get(ossGroupsUrl + pluginId,response => {
        this.groups = response.data;
      });
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
    scanGroup () {
      let account = this.$t('account.one_scan') + this.$t('account.cloud_account');
      this.$alert( account + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            if (this.checkedGroups.length === 0) {
              this.$warning(this.$t('account.please_choose_rule_group'));
              return;
            }
            let params = {
              accountId: this.accountWithGroup.id,
              groups: this.checkedGroups
            }
            this.groupResult = this.$post(ruleScanUrl, params, () => {
              this.$success(this.$t('account.i18n_hr_create_success'));
              this.scanVisible = false;
              this.$router.push({
                path: '/account/result',
              }).catch(error => error);
            });
          }
        }
      });
    },
    objectDownload(item) {
      this.$alert(this.$t('server.download') + item.objectName + " ？", this.$t('server.download') + this.$t('oss.object_file'), {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$download(ossDownloadObjectUrl + item.bucketId, {
              objectId: item.id
            }, response => {
              let blob = new Blob([response.data], {type: "'application/octet-stream'"});
              saveAs(blob, item.objectName);
            }, error => {
              console.log("下载报错", error);
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
  margin-bottom: 5px;
}
.el-table {
  cursor: pointer;
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
.el-form-item-dev  >>> .el-form-item__content {
  margin-left: 0 !important;
}
.grid-content-log-span {
  width: 34%;
  float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0 6px 2px;
  color: #606266;
  padding: 0 1%;
}

.grid-content-status-span {
  width: 20%;float: left;
  vertical-align: middle;
  display:table-cell;
  margin: 6px 0;
  padding: 0 1%;
}
.bg-purple-dark {
  background: #99a9bf;
  min-height: 36px;
  color: #FFFFFF;
  margin-bottom: 5px;
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
.table-card >>> .el-table__header-wrapper {
  border-left: 1px solid #EBEEF5;
}
.table-card >>> .el-table__body-wrapper {
  border-left: 1px solid #e4e7ec;
}
.table-inner >>> .el-table__header-wrapper {
  border-left: 1px solid #EBEEF5;
  border-right: 1px solid #EBEEF5;
  border-top: 1px solid #EBEEF5;
}
.table-inner >>> .el-table__body-wrapper {
  border-left: 1px solid #EBEEF5;
  border-right: 1px solid #EBEEF5;
}
</style>

