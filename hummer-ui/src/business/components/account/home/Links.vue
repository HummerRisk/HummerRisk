<template>
  <div>
    <el-popover
      ref="popover"
      placement="right"
      width="830"
      trigger="hover"
      v-if="ossList.length > 0 || cloudEventSyncLogList.length > 0 || imageRepoList.length > 0 || cloudResourceSyncList.length > 0">
      <div>
        <el-row v-if="ossList.length > 0"><h3>{{ $t('oss.oss_setting') }}</h3></el-row>
        <el-table v-if="ossList.length > 0" :border="true" :stripe="true" :data="ossList" class="adjust-table table-content">
          <el-table-column type="index" min-width="50"/>
          <el-table-column prop="name" :label="$t('oss.name')" min-width="150" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('account.cloud_platform')" min-width="120" show-overflow-tooltip v-slot:default="scope">
            {{ scope.row.pluginName }}
          </el-table-column>
          <el-table-column :label="$t('oss.bucket')" min-width="100">
            <template v-slot:default="scope">
              {{ scope.row.sum }}
            </template>
          </el-table-column>
          <el-table-column prop="status" min-width="110" :label="$t('account.status')">
            <template v-slot:default="{row}">
              <div>
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
          <el-table-column v-slot:default="scope" :label="$t('event.sync_status')" min-width="130" prop="status" sortable
                           show-overflow-tooltip>
            <el-button plain size="mini" type="primary" v-if="scope.row.syncStatus === 'UNCHECKED'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button plain size="mini" type="primary" v-else-if="scope.row.syncStatus === 'APPROVED'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button plain size="mini" type="primary" v-else-if="scope.row.syncStatus === 'PROCESSING'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button plain size="mini" type="success" v-else-if="scope.row.syncStatus === 'FINISHED'">
              <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
            </el-button>
            <el-button plain size="mini" type="danger" v-else-if="scope.row.syncStatus === 'ERROR'">
              <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
            </el-button>
            <el-button plain size="mini" type="warning" v-else-if="scope.row.syncStatus === 'WARNING'">
              <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
            </el-button>
          </el-table-column>
          <el-table-column min-width="160" :label="$t('account.create_time')" sortable
                           prop="createTime">
            <template v-slot:default="scope">
              <span>{{ scope.row.createTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
        </el-table>
        <el-row v-if="cloudEventSyncLogList.length > 0"><h3>{{ $t('event.audit') }}</h3></el-row>
        <el-table v-if="cloudEventSyncLogList.length > 0" :border="true" :stripe="true" :data="cloudEventSyncLogList" class="adjust-table table-content">
          <el-table-column type="index" min-width="50"/>
          <el-table-column prop="accountName" :label="$t('event.cloud_account_name')" min-width="150" show-overflow-tooltip>
            <template v-slot:default="scope">
                <span v-if="scope.row.accountName">
                  <img :src="require(`@/assets/img/platform/${scope.row.accountIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                   &nbsp;&nbsp; {{ scope.row.accountName }}
                </span>
            </template>
          </el-table-column>
          <el-table-column prop="dataCount" :label="$t('event.data_count')" min-width="60" v-slot:default="scope">
            {{ scope.row.dataCount }}
          </el-table-column>
          <el-table-column :label="$t('event.sync_time_section')" min-width="270">
            <template v-slot:default="scope">
            <span>{{
                scope.row.requestStartTime | timestampFormatDate
              }} - {{ scope.row.requestEndTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column v-slot:default="scope" :label="$t('event.sync_status')" min-width="130">
            <el-button  plain size="mini" type="primary" v-if="scope.row.status === 0">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button  plain size="mini" type="success"
                        v-else-if="scope.row.status === 1">
              <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
            </el-button>
            <el-button  plain size="mini" type="danger" v-else-if="scope.row.status === 2">
              <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
            </el-button>
            <el-button  plain size="mini" type="warning"
                        v-else-if="scope.row.status === 3">
              <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
            </el-button>
          </el-table-column>
          <el-table-column prop="createTime" :label="$t('event.sync_time')" min-width="160">
            <template v-slot:default="scope">
              <span>{{ scope.row.createTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
        </el-table>
        <el-row v-if="imageRepoList.length > 0"><h3>{{ $t('image.image_repo') }}</h3></el-row>
        <el-table v-if="imageRepoList.length > 0" :border="true" :stripe="true" :data="imageRepoList" class="adjust-table table-content">
          <el-table-column type="index" min-width="50"/>
          <el-table-column prop="name" :label="$t('image.image_repo_name')" min-width="150">
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/repo/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="repo" :label="$t('image.image_repo_url')" min-width="220" v-slot:default="scope">
            {{ scope.row.repo?scope.row.repo:"--" }}
          </el-table-column>
          <el-table-column prop="userName" :label="$t('image.image_repo_user_name')" min-width="110" v-slot:default="scope">
            {{ scope.row.userName?scope.row.userName:"--" }}
          </el-table-column>
          <el-table-column prop="status" min-width="130" :label="$t('image.image_repo_status')">
            <template v-slot:default="{row}">
              <image-status :row="row"/>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" :label="$t('commons.create_time')" min-width="160">
            <template v-slot:default="scope">
              <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
        </el-table>
        <el-row v-if="cloudResourceSyncList.length > 0" ><h3>{{ $t('account.sync_log') }}</h3></el-row>
        <el-table v-if="cloudResourceSyncList.length > 0" :border="true" :stripe="true" :data="cloudResourceSyncList" class="adjust-table table-content">
          <el-table-column type="index" min-width="50"/>
          <el-table-column prop="accountName" :label="$t('event.cloud_account_name')" min-width="150" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.accountName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="resourcesSum" :label="$t('event.data_count')" min-width="160"/>
          <el-table-column prop="resourceTypes" :label="$t('dashboard.resource_type')" min-width="170">
            <template v-slot:default="scope">
              <el-button slot="reference" size="mini" type="primary" plain>
                {{ $t('rule.resource_type') }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="status" min-width="130" :label="$t('code.status')">
            <template v-slot:default="scope">
              <el-button plain size="mini" type="primary"
                         v-if="scope.row.status === 'UNCHECKED'">
                <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
              </el-button>
              <el-button plain size="mini" type="primary"
                         v-else-if="scope.row.status === 'APPROVED'">
                <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
              </el-button>
              <el-button plain size="mini" type="primary"
                         v-else-if="scope.row.status === 'RUNNING'">
                <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
              </el-button>
              <el-button plain size="mini" type="primary"
                         v-else-if="scope.row.status === 'PROCESSING'">
                <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
              </el-button>
              <el-button plain size="mini" type="success"
                         v-else-if="scope.row.status === 'FINISHED'">
                <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
              </el-button>
              <el-button plain size="mini" type="danger"
                         v-else-if="scope.row.status === 'ERROR'">
                <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
              </el-button>
              <el-button plain size="mini" type="warning"
                         v-else-if="scope.row.status === 'WARNING'">
                <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" :label="$t('k8s.sync_time')" min-width="160" sortable>
            <template v-slot:default="scope">
              <span>{{ scope.row.createTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-button v-if="ossList.length > 0 || cloudEventSyncLogList.length > 0 || imageRepoList.length > 0 || cloudResourceSyncList.length > 0" slot="reference" size="mini" type="primary" plain @click="showLinks">
        {{ $t('vis.linked') }}
      </el-button>
    </el-popover>
    <el-button v-if="ossList.length == 0 && cloudEventSyncLogList.length == 0 && imageRepoList.length == 0 && cloudResourceSyncList.length == 0" slot="reference" size="mini" type="info" plain>
      {{ $t('vis.not_linked') }}
    </el-button>

    <!--links-->
    <el-drawer class="rtl" :title="$t('vis.linked') + $t('dashboard.accounts')" :visible.sync="linksVisible" size="80%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-row v-if="ossList.length > 0"><h3>{{ $t('oss.oss_setting') }}</h3></el-row>
      <el-table v-if="ossList.length > 0" :border="true" :stripe="true" :data="ossList" class="adjust-table table-content">
        <el-table-column type="index" min-width="50"/>
        <el-table-column prop="name" :label="$t('oss.name')" min-width="150" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('account.cloud_platform')" min-width="120" show-overflow-tooltip v-slot:default="scope">
          {{ scope.row.pluginName }}
        </el-table-column>
        <el-table-column :label="$t('oss.bucket')" min-width="100">
          <template v-slot:default="scope">
            {{ scope.row.sum }}
          </template>
        </el-table-column>
        <el-table-column prop="status" min-width="110" :label="$t('account.status')">
          <template v-slot:default="{row}">
            <div>
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
        <el-table-column v-slot:default="scope" :label="$t('event.sync_status')" min-width="140" prop="status" sortable
                         show-overflow-tooltip>
          <el-button plain size="mini" type="primary" v-if="scope.row.syncStatus === 'UNCHECKED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button plain size="mini" type="primary" v-else-if="scope.row.syncStatus === 'APPROVED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button plain size="mini" type="primary" v-else-if="scope.row.syncStatus === 'PROCESSING'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button plain size="mini" type="success" v-else-if="scope.row.syncStatus === 'FINISHED'">
            <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
          </el-button>
          <el-button plain size="mini" type="danger" v-else-if="scope.row.syncStatus === 'ERROR'">
            <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
          </el-button>
          <el-button plain size="mini" type="warning" v-else-if="scope.row.syncStatus === 'WARNING'">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
          </el-button>
        </el-table-column>
        <el-table-column min-width="180" :label="$t('account.create_time')" sortable
                         prop="createTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="130" :label="$t('commons.fast_entry')" fixed="right">
          <el-button plain size="mini" type="success" @click="goOss()">
            <i class="el-icon-right"></i> {{ $t('oss.oss_setting') }}
          </el-button>
        </el-table-column>
      </el-table>
      <el-row v-if="cloudEventSyncLogList.length > 0"><h3>{{ $t('event.audit') }}</h3></el-row>
      <el-table v-if="cloudEventSyncLogList.length > 0" :border="true" :stripe="true" :data="cloudEventSyncLogList" class="adjust-table table-content">
        <el-table-column type="index" min-width="50"/>
        <el-table-column prop="accountName" :label="$t('event.cloud_account_name')" min-width="150" show-overflow-tooltip>
          <template v-slot:default="scope">
                <span v-if="scope.row.accountName">
                  <img :src="require(`@/assets/img/platform/${scope.row.accountIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                   &nbsp;&nbsp; {{ scope.row.accountName }}
                </span>
          </template>
        </el-table-column>
        <el-table-column prop="dataCount" :label="$t('event.data_count')" min-width="60" v-slot:default="scope">
          {{ scope.row.dataCount }}
        </el-table-column>
        <el-table-column :label="$t('event.sync_time_section')" min-width="270">
          <template v-slot:default="scope">
            <span>{{
                scope.row.requestStartTime | timestampFormatDate
              }} - {{ scope.row.requestEndTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('event.sync_status')" min-width="140">
          <el-button  plain size="mini" type="primary" v-if="scope.row.status === 0">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button  plain size="mini" type="success"
                      v-else-if="scope.row.status === 1">
            <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
          </el-button>
          <el-button  plain size="mini" type="danger" v-else-if="scope.row.status === 2">
            <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
          </el-button>
          <el-button  plain size="mini" type="warning"
                      v-else-if="scope.row.status === 3">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
          </el-button>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('event.sync_time')" min-width="180">
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="130" :label="$t('commons.fast_entry')" fixed="right">
          <el-button plain size="mini" type="success" @click="goEvent()">
            <i class="el-icon-right"></i> {{ $t('event.audit') }}
          </el-button>
        </el-table-column>
      </el-table>
      <el-row v-if="imageRepoList.length > 0"><h3>{{ $t('image.image_repo') }}</h3></el-row>
      <el-table v-if="imageRepoList.length > 0" :border="true" :stripe="true" :data="imageRepoList" class="adjust-table table-content">
        <el-table-column type="index" min-width="50"/>
        <el-table-column prop="name" :label="$t('image.image_repo_name')" min-width="150">
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/repo/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="repo" :label="$t('image.image_repo_url')" min-width="220" v-slot:default="scope">
          {{ scope.row.repo?scope.row.repo:"--" }}
        </el-table-column>
        <el-table-column prop="userName" :label="$t('image.image_repo_user_name')" min-width="110" v-slot:default="scope">
          {{ scope.row.userName?scope.row.userName:"--" }}
        </el-table-column>
        <el-table-column prop="status" min-width="140" :label="$t('image.image_repo_status')">
          <template v-slot:default="{row}">
            <image-status :row="row"/>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('commons.create_time')" min-width="180">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="130" :label="$t('commons.fast_entry')" fixed="right">
          <el-button plain size="mini" type="success" @click="goRepo()">
            <i class="el-icon-right"></i> {{ $t('image.image_repo') }}
          </el-button>
        </el-table-column>
      </el-table>
      <el-row v-if="cloudResourceSyncList.length > 0" ><h3>{{ $t('account.sync_log') }}</h3></el-row>
      <el-table v-if="cloudResourceSyncList.length > 0" :border="true" :stripe="true" :data="cloudResourceSyncList" class="adjust-table table-content">
        <el-table-column type="index" min-width="50"/>
        <el-table-column prop="accountName" :label="$t('event.cloud_account_name')" min-width="150" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.accountName }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="resourcesSum" :label="$t('event.data_count')" min-width="160"/>
        <el-table-column prop="resourceTypes" :label="$t('dashboard.resource_type')" min-width="170">
          <template v-slot:default="scope">
            <el-button slot="reference" size="mini" type="primary" plain>
              {{ $t('rule.resource_type') }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="status" min-width="140" :label="$t('code.status')">
          <template v-slot:default="scope">
            <el-button plain size="mini" type="primary"
                       v-if="scope.row.status === 'UNCHECKED'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button plain size="mini" type="primary"
                       v-else-if="scope.row.status === 'APPROVED'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button plain size="mini" type="primary"
                       v-else-if="scope.row.status === 'RUNNING'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button plain size="mini" type="primary"
                       v-else-if="scope.row.status === 'PROCESSING'">
              <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
            </el-button>
            <el-button plain size="mini" type="success"
                       v-else-if="scope.row.status === 'FINISHED'">
              <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
            </el-button>
            <el-button plain size="mini" type="danger"
                       v-else-if="scope.row.status === 'ERROR'">
              <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
            </el-button>
            <el-button plain size="mini" type="warning"
                       v-else-if="scope.row.status === 'WARNING'">
              <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('k8s.sync_time')" min-width="180" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="130" :label="$t('commons.fast_entry')" fixed="right">
          <el-button plain size="mini" type="success" @click="goSyncLog()">
            <i class="el-icon-right"></i> {{ $t('account.sync_log') }}
          </el-button>
        </el-table-column>
      </el-table>
    </el-drawer>
    <!--links-->
  </div>
</template>

<script>
  /* eslint-disable */
  import TableOperators from "@/business/components/common/components/TableOperators.vue";
  import ImageStatus from "@/business/components/image/head/ImageStatus.vue";
  import ResourceType from "@/business/components/cloudSituation/home/ResourceType.vue";

  export default {
    name: "Links",
    components: {ResourceType, ImageStatus, TableOperators},
    props: {
      row: Object,
    },
    watch: {
      row() {
        this.init();
      },
    },
    data() {
      return {
        accountId: '',
        ossList: [],
        cloudEventSyncLogList: [],
        imageRepoList: [],
        cloudResourceSyncList: [],
        linksVisible: false,
        direction: 'rtl',
      }
    },
    created() {
      this.init();
    },
    methods: {
      init() {
        this.ossList = this.row.ossList?this.row.ossList:[];
        this.cloudEventSyncLogList = this.row.cloudEventSyncLogList?this.row.cloudEventSyncLogList:[];
        this.imageRepoList = this.row.imageRepoList?this.row.imageRepoList:[];
        this.cloudResourceSyncList = this.row.cloudResourceSyncList?this.row.cloudResourceSyncList:[];
      },
      showLinks() {
        this.linksVisible =  true;
      },
      handleClose() {
        this.linksVisible =  false;
      },
      goOss() {
        this.$router.push({
          path: '/oss/account'
        }).catch(error => error);
      },
      goEvent() {
        this.$router.push({
          path: '/event/sync'
        }).catch(error => error);
      },
      goRepo() {
        this.$router.push({
          path: '/image/image-repo'
        }).catch(error => error);
      },
      goSyncLog() {
        this.$router.push({
          path: '/cloud-situation/cloud-sync-log'
        }).catch(error => error);
      },
    },
  }
</script>

<style scoped>
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
    width: 60%;
  }
  .rtl >>> h3 {
    margin: 20px;
  }
  .code-mirror {
    height: 600px !important;
  }
  .code-mirror >>> .CodeMirror {
    /* Set height, width, borders, and global font properties here */
    height: 600px !important;
  }
  .rtl >>> .el-drawer__header {
    margin-bottom: 0;
  }
  /deep/ :focus{outline:0;}
</style>
