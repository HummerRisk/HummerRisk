<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('image.image_repo_list')"
                      @create="create" :createTip="$t('image.repo_create')"
                      :show-create="true" @delete="deleteBatch" :show-delete="true"
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
        <el-table-column type="selection" id="selection" prop="selection" min-width="50">
        </el-table-column>
        <el-table-column type="index" min-width="40"/>
        <el-table-column prop="name" v-if="checkedColumnNames.includes('name')" :label="$t('image.image_repo_name')" min-width="160">
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/repo/${scope.row.pluginIcon}`)" style="width: 24px; height: 24px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="repo" v-if="checkedColumnNames.includes('repo')" :label="$t('image.image_repo_url')" min-width="220" v-slot:default="scope">
          {{ scope.row.repo?scope.row.repo:"--" }}
        </el-table-column>
        <el-table-column prop="userName" v-if="checkedColumnNames.includes('userName')" :label="$t('image.image_repo_user_name')" min-width="110" v-slot:default="scope">
          {{ scope.row.userName?scope.row.userName:"--" }}
        </el-table-column>
        <el-table-column prop="status" v-if="checkedColumnNames.includes('status')" min-width="130" :label="$t('image.image_repo_status')"
                         column-key="status"
                         :filters="statusFilters"
                         :filter-method="filterStatus">
          <template v-slot:default="{row}">
            <image-status :row="row"/>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" v-if="checkedColumnNames.includes('createTime')" :label="$t('commons.create_time')" min-width="160" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="140" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators v-if="scope.row.pluginIcon !== 'other.png'" :buttons="buttons" :row="scope.row"/>
            <table-operators v-if="scope.row.pluginIcon === 'other.png'" :buttons="buttons2" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>

      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create imageRepo-->
    <el-drawer class="rtl" :title="$t('image.repo_create')" :visible.sync="createVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <div v-loading="viewResult.loading">
        <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form">
          <el-form-item :label="$t('image.image_repo_name')" ref="name" prop="name">
            <el-input v-model="form.name" autocomplete="off" :placeholder="$t('image.image_repo_name')"/>
          </el-form-item>
          <el-form-item :label="$t('image.image_repo_type')" :rules="{required: true, message: $t('image.image_repo_type') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" filterable :clearable="true" v-model="form.pluginIcon" :placeholder="$t('image.image_repo_type')" @change="this.selectAccount">
              <el-option
                v-for="item in plugins"
                :key="item.value"
                :label="item.id"
                :value="item.value">
                <img :src="require(`@/assets/img/repo/${item.value}`)" style="width: 20px; height: 16px; vertical-align:middle" alt=""/>
                &nbsp;&nbsp; {{ item.id }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-show = "form.pluginIcon === 'aliyun.png' || form.pluginIcon === 'qcloud.png'|| form.pluginIcon === 'aws.png'" :label="$t('image.is_bind_account')" >
            <el-switch v-model="form.isBindAccount"></el-switch>
          </el-form-item >
          <el-form-item v-show = "!form.isBindAccount && (form.pluginIcon === 'aliyun.png' || form.pluginIcon === 'qcloud.png'|| form.pluginIcon === 'aws.png')" :label="!!cloudPluginDic[form.pluginIcon]?cloudPluginDic[form.pluginIcon].akTitle:''">
            <el-input v-model="form.ak" :placeholder="'Please fill in '+(!!cloudPluginDic[form.pluginIcon]?cloudPluginDic[form.pluginIcon].akTitle:'')"></el-input>
          </el-form-item>
          <el-form-item  v-show="!form.isBindAccount && (form.pluginIcon === 'aliyun.png' || form.pluginIcon === 'qcloud.png'|| form.pluginIcon === 'aws.png')" :label="!!cloudPluginDic[form.pluginIcon]?cloudPluginDic[form.pluginIcon].skTitle:''">
            <el-input type="password" v-model="form.sk" :placeholder="'Please fill in '+(!!cloudPluginDic[form.pluginIcon]?cloudPluginDic[form.pluginIcon].skTitle:'')"></el-input>
          </el-form-item>
          <el-form-item v-show = "!!form.isBindAccount && (form.pluginIcon === 'aliyun.png' || form.pluginIcon === 'qcloud.png'|| form.pluginIcon === 'aws.png')" :label="$t('account.cloud_account')" ref="accountId" prop="accountId">
            <el-select style="width: 100%;" filterable :clearable="true" v-model="form.accountId" :placeholder="$t('account.cloud_account')" >
              <el-option
                v-for="item in accounts"
                :key="item.id"
                :label="item.name"
                :value="item.id">
                <img :src="require(`@/assets/img/repo/${item.pluginIcon}`)" style="width: 20px; height: 16px; vertical-align:middle" alt=""/>
                &nbsp;&nbsp; {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('image.image_repo_url')" ref="repo" prop="repo">
            <el-input v-model="form.repo" autocomplete="off" :placeholder="$t('image.image_repo_url_desc')"/>
          </el-form-item>
          <el-form-item :label="$t('image.image_repo_user_name')" ref="userName" prop="userName">
            <el-input v-model="form.userName" autocomplete="off" :placeholder="$t('image.image_repo_user_name')"/>
          </el-form-item>
          <el-form-item :label="$t('image.image_repo_password')" ref="password" prop="password">
            <el-input v-model="form.password" autocomplete="off" :placeholder="$t('image.image_repo_password')" show-password/>
          </el-form-item>
        </el-form>
        <span style="color: red;"><I>{{ $t('image.image_repo_note') }}</I></span>
        <div style="margin: 10px;">
          <dialog-footer
            @cancel="createVisible = false"
            @confirm="saveRepo('form')"/>
        </div>
      </div>
    </el-drawer>
    <!--Create imageRepo-->

    <!--Update imageRepo-->
    <el-drawer class="rtl" :title="$t('image.repo_update')" :visible.sync="updateVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <div v-loading="viewResult.loading">
        <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form">
          <el-form-item :label="$t('image.image_repo_name')" ref="name" prop="name">
            <el-input v-model="form.name" autocomplete="off" :placeholder="$t('image.image_repo_name')"/>
          </el-form-item>
          <el-form-item :label="$t('image.image_repo_type')" :rules="{required: true, message: $t('image.image_repo_type') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="form.pluginIcon" :placeholder="$t('image.image_repo_type')">
              <el-option
                v-for="item in plugins"
                :key="item.value"
                :label="item.id"
                :value="item.value">
                <img :src="require(`@/assets/img/repo/${item.value}`)" style="width: 20px; height: 16px; vertical-align:middle" alt=""/>
                &nbsp;&nbsp; {{ item.id }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-show = "form.pluginIcon === 'aliyun.png' || form.pluginIcon === 'qcloud.png'|| form.pluginIcon === 'aws.png'" :label="$t('image.is_bind_account')" >
            <el-switch v-model="form.isBindAccount"></el-switch>
          </el-form-item >
          <el-form-item v-show = "!form.isBindAccount && (form.pluginIcon === 'aliyun.png' || form.pluginIcon === 'qcloud.png'|| form.pluginIcon === 'aws.png')" :label="!!cloudPluginDic[form.pluginIcon]?cloudPluginDic[form.pluginIcon].akTitle:''">
            <el-input v-model="form.ak" :placeholder="'Please fill in '+(!!cloudPluginDic[form.pluginIcon]?cloudPluginDic[form.pluginIcon].akTitle:'')"></el-input>
          </el-form-item>
          <el-form-item  v-show="!form.isBindAccount && (form.pluginIcon === 'aliyun.png' || form.pluginIcon === 'qcloud.png'|| form.pluginIcon === 'aws.png')" :label="!!cloudPluginDic[form.pluginIcon]?cloudPluginDic[form.pluginIcon].skTitle:''">
            <el-input type="password" v-model="form.sk" :placeholder="'Please fill in '+(!!cloudPluginDic[form.pluginIcon]?cloudPluginDic[form.pluginIcon].skTitle:'')"></el-input>
          </el-form-item>
          <el-form-item v-show = "!!form.isBindAccount && (form.pluginIcon === 'aliyun.png' || form.pluginIcon === 'qcloud.png'|| form.pluginIcon === 'aws.png')" :label="$t('account.cloud_account')" ref="accountId" prop="accountId">
            <el-select style="width: 100%;" filterable :clearable="true" v-model="form.accountId" :placeholder="$t('account.cloud_account')" >
              <el-option
                v-for="item in accounts"
                :key="item.id"
                :label="item.name"
                :value="item.id">
                <img :src="require(`@/assets/img/repo/${item.pluginIcon}`)" style="width: 20px; height: 16px; vertical-align:middle" alt=""/>
                &nbsp;&nbsp; {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('image.image_repo_url')" ref="repo" prop="repo">
            <el-input v-model="form.repo" autocomplete="off" :placeholder="$t('image.image_repo_url_desc')"/>
          </el-form-item>
          <el-form-item :label="$t('image.image_repo_user_name')" ref="userName" prop="userName">
            <el-input v-model="form.userName" autocomplete="off" :placeholder="$t('image.image_repo_user_name')"/>
          </el-form-item>
          <el-form-item :label="$t('image.image_repo_password')" ref="password" prop="password">
            <el-input v-model="form.password" autocomplete="off" :placeholder="$t('image.image_repo_password')" show-password/>
          </el-form-item>
        </el-form>
        <span style="color: red;"><I>{{ $t('image.image_repo_note') }}</I></span>
        <div style="margin: 10px;">
          <dialog-footer
            @cancel="updateVisible = false"
            @confirm="editRepo('form')"/>
        </div>
      </div>
    </el-drawer>
    <!--Update imageRepo-->

    <!--Image list-->
    <el-drawer class="rtl image-list" :title="$t('image.image_list')" :visible.sync="imageVisible" size="90%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <div v-loading="viewResult.loading">
        <span style="color: red;"><I>{{ $t('image.image_repo_note') }}</I></span>
        <table-header :condition.sync="imageCondition" @search="handleList"
                      @scan="saveAddAll" :scanTip="$t('image.one_scan')"
                      @setting="setting" :settingTip="$t('image.batch_settings_repo')"
                      :show-name="false" :show-scan="true" :show-setting="true"
                      :items="items2" :columnNames="columnNames2" :show-sync="true" @sync="handleSync"
                      :checkedColumnNames="checkedColumnNames2" :checkAll="checkAll2" :isIndeterminate="isIndeterminate2"
                      @handleCheckedColumnNamesChange="handleCheckedColumnNamesChange2" @handleCheckAllChange="handleCheckAllChange2"/>
        <hide-table
        :table-data="imageData"
        @sort-change="sort"
        @filter-change="filter"
        @select-all="select"
        @select="select"
        >
          <el-table-column type="selection" min-width="40">
          </el-table-column>
          <el-table-column type="index" min-width="40"/>
          <el-table-column prop="project" :label="'Project'" v-if="checkedColumnNames2.includes('project')" min-width="110" v-slot:default="scope">
            {{ scope.row.project?scope.row.project:'N/A' }}
          </el-table-column>
          <el-table-column prop="repository" v-if="checkedColumnNames2.includes('repository')" :label="'Repository'" min-width="150">
          </el-table-column>
          <el-table-column prop="path" v-if="checkedColumnNames2.includes('path')" :label="'ImagePath'" min-width="250">
          </el-table-column>
          <el-table-column min-width="90" v-if="checkedColumnNames2.includes('size')" :label="'Size'" prop="size" v-slot:default="scope">
            {{ scope.row.size?scope.row.size:'--' }}
          </el-table-column>
          <el-table-column min-width="90" v-if="checkedColumnNames2.includes('arch')" :label="'Arch'" prop="arch" v-slot:default="scope">
            {{ scope.row.arch?scope.row.arch:'--' }}
          </el-table-column>
          <el-table-column min-width="150" v-if="checkedColumnNames2.includes('pushTime')" :label="'PushTime'" prop="pushTime" v-slot:default="scope">
            {{ scope.row.pushTime?scope.row.pushTime:'--' }}
          </el-table-column>
          <el-table-column min-width="110" :label="$t('task.task_k8s')" v-if="checkedColumnNames2.includes('platform')" prop="platform" v-slot:default="scope">
            <el-button v-if="scope.row.imageRepoItemK8sDTOList.length > 0" slot="reference" size="mini" type="warning" plain @click="showK8s(scope.row)">
              {{ $t('k8s.platform') }}
            </el-button>
            <el-button v-if="scope.row.imageRepoItemK8sDTOList.length == 0" slot="reference" size="mini" type="primary" plain>
              {{ 'N/A' }}
            </el-button>
          </el-table-column>
          <el-table-column min-width="60" :label="$t('commons.operating')" fixed="right">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons3" :row="scope.row"/>
            </template>
          </el-table-column>
        </hide-table>
        <table-pagination :change="handleList" :current-page.sync="imagePage" :page-size.sync="imageSize" :total="imageTotal"/>
      </div>
      <div v-loading="viewResult.loading">
        <el-drawer
          class="rtl"
          size="60%"
          :title="$t('k8s.execute_scan')"
          :append-to-body="true"
          :before-close="innerClose"
          :visible.sync="innerK8s">
          <el-table border :data="k8sData" class="adjust-table table-content">
            <el-table-column type="index" min-width="1%"/>
            <el-table-column prop="k8sName" :label="'K8sName'" min-width="20%" v-slot:default="scope">
              {{ scope.row.k8sName?scope.row.k8sName:'N/A' }}
            </el-table-column>
            <el-table-column prop="namespace" :label="'Namespace'" min-width="20%">
            </el-table-column>
            <el-table-column prop="sourceType" :label="'SourceType'" min-width="15%">
            </el-table-column>
            <el-table-column min-width="40%" :label="'SourceName'" prop="sourceName" v-slot:default="scope">
              {{ scope.row.sourceName?scope.row.sourceName:'--' }}
            </el-table-column>
          </el-table>
          <dialog-footer
            @cancel="innerK8s = false"
            @confirm="innerK8s = false"/>
        </el-drawer>
      </div>
      <div v-loading="viewResult.loading">
        <el-drawer
          class="rtl"
          size="60%"
          :title="$t('k8s.execute_scan')"
          :append-to-body="true"
          :before-close="innerClose"
          :visible.sync="innerAdd">
          <el-form :model="addForm" label-position="right" label-width="150px" size="small" ref="addForm" :rules="rule">
            <el-form-item :label="$t('sbom.sbom_project')" :rules="{required: true, message: $t('sbom.sbom_project') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="addForm.sbomId" :placeholder="$t('sbom.sbom_project')" @change="changeSbom(addForm)">
                <el-option
                  v-for="item in sboms"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                  <i class="iconfont icon-SBOM sbom-icon"></i>
                  {{ item.name }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('sbom.sbom_project_version')" :rules="{required: true, message: $t('sbom.sbom_project_version') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="addForm.sbomVersionId" :placeholder="$t('sbom.sbom_project_version')">
                <el-option
                  v-for="item in versions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                  <i class="iconfont icon-lianmenglian sbom-icon-2"></i>
                  {{ item.name }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('image.image_name')" ref="name" prop="name">
              <el-input v-model="addForm.name" autocomplete="off" :placeholder="$t('image.image_name')"/>
            </el-form-item>
            <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-switch v-model="addForm.isProxy"></el-switch>
            </el-form-item>
            <el-form-item v-if="addForm.isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="addForm.proxyId" :placeholder="$t('commons.proxy')">
                <el-option
                  v-for="item in proxys"
                  :key="item.id"
                  :label="item.proxyIp"
                  :value="item.id">
                  &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}
                </el-option>
              </el-select>
            </el-form-item>
          </el-form>
          <dialog-footer
            @cancel="innerAdd = false"
            @confirm="saveAdd()"/>
        </el-drawer>
      </div>
      <div v-loading="viewResult.loading">
        <el-drawer
          class="rtl"
          size="60%"
          :title="$t('image.batch_settings_repo')"
          :append-to-body="true"
          :before-close="innerClose"
          :visible.sync="settingVisible">
          <el-form :model="settingForm" label-position="right" label-width="150px" size="small" ref="settingForm">
            <el-form-item :label="$t('image.image_repo_name')" ref="name" prop="name">
              <el-input v-model="settingForm.repo" @input="change($event)" autocomplete="off" :placeholder="$t('image.image_repo_name')"/>
            </el-form-item>
            <el-form-item :label="$t('image.image_repo_name_old')" ref="name" prop="name">
              <el-input disabled v-model="settingForm.repoOld" autocomplete="off" :placeholder="$t('image.image_repo_name_old')"/>
            </el-form-item>
          </el-form>
          <dialog-footer
            @cancel="settingVisible = false"
            @confirm="saveSetting()"/>
        </el-drawer>
      </div>
      <div v-loading="viewResult.loading">
        <el-drawer
            class="rtl"
            size="80%"
            :title="$t('image.image_sync_for_repo')"
            :append-to-body="true"
            :before-close="innerClose"
            :visible.sync="syncVisible">
          <span style="color: red;"><I>{{ $t('image.image_repo_note') }}</I></span>
          <sync-table-header @sync="sync" :sync-tip="$t('image.image_sync')" :title="$t('image.image_sync_log')" style="margin: 0 0 15px 0;"/>
          <el-table border :data="syncData" class="adjust-table table-content">
            <el-table-column type="index" min-width="2%"/>
            <el-table-column prop="operation" :label="$t('image.image_sync')" min-width="15%"/>
            <el-table-column prop="operator" :label="$t('resource.creator')" min-width="15%"/>
            <el-table-column prop="result" min-width="15%" :label="$t('image.image_repo_status')">
              <template v-slot:default="scope">
                <el-tooltip class="item" effect="dark" :content="scope.row.output" placement="top">
                  <el-tag size="mini" type="success" v-if="scope.row.result">
                    {{ $t('commons.success') }}
                  </el-tag>
                  <el-tag size="mini" type="danger" v-else-if="!scope.row.result">
                    {{ $t('commons.error') }}
                  </el-tag>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column prop="sum" :label="$t('resource.i18n_not_compliance')" min-width="12%"/>
            <el-table-column prop="createTime" :label="$t('commons.create_time')" min-width="20%" sortable>
              <template v-slot:default="scope">
                <span>{{ scope.row.createTime | timestampFormatDate }}</span>
              </template>
            </el-table-column>
          </el-table>
          <table-pagination :change="handleSync" :current-page.sync="syncPage" :page-size.sync="syncSize" :total="syncTotal"/>
          <div style="margin: 10px;">
            <dialog-footer
                @cancel="syncVisible = false"
                @confirm="syncVisible = false"/>
          </div>
        </el-drawer>
      </div>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="imageVisible = false"
          @confirm="saveAddAll()"/>
      </div>
    </el-drawer>
    <!--Image list-->

  </main-container>
</template>

<script>
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperators from "@/business/components/common/components/TableOperators";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import ImageStatus from "../head/ImageStatus";
import {_filter, _sort} from "@/common/js/utils";
import MainContainer from "../.././common/components/MainContainer";
import SyncTableHeader from "@/business/components/image/head/SyncTableHeader";
import {
  IMAGE_REPO_CONFIGS,
  IMAGE_REPO_IMAGE_CONFIGS
} from "@/business/components/common/components/search/search-components";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {
  addImageRepoUrl,
  deleteImageReposUrl,
  deleteImageRepoUrl,
  editImageRepoUrl,
  imageRepoListUrl,
  imageRepoSettingUrl,
  repoItemListUrl,
  repoSyncListUrl,
  scanImageRepoUrl,
  scanImagesRepoUrl,
  syncImageUrl
} from "@/api/k8s/image/image";
import {allSbomListUrl, allSbomVersionListUrl} from "@/api/k8s/sbom/sbom";
import {proxyListAllUrl} from "@/api/system/system";
import {allListUrl} from "@/api/cloud/account/account";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'image.image_repo_name',
    props: 'name',
    disabled: false
  },
  {
    label: 'image.image_repo_url',
    props: 'repo',
    disabled: false
  },
  {
    label: 'image.image_repo_user_name',
    props: 'userName',
    disabled: false
  },
  {
    label: 'image.image_repo_status',
    props: 'status',
    disabled: false
  },
  {
    label: 'commons.create_time',
    props: 'createTime',
    disabled: false
  },
];
const columnOptions2 = [
  {
    label: 'Project',
    props: 'project',
    disabled: false
  },
  {
    label: 'Repository',
    props: 'repository',
    disabled: false
  },
  {
    label: 'ImagePath',
    props: 'path',
    disabled: false
  },
  {
    label: 'Size',
    props: 'size',
    disabled: false
  },
  {
    label: 'Arch',
    props: 'arch',
    disabled: false
  },
  {
    label: 'PushTime',
    props: 'pushTime',
    disabled: false
  },
  {
    label: 'task.task_k8s',
    props: 'platform',
    disabled: false
  },
];

/* eslint-disable */
export default {
  name: "ImageRepo",
  components: {
    TablePagination,
    TableHeader,
    DialogFooter,
    ImageStatus,
    TableOperators,
    MainContainer,
    SyncTableHeader,
    HideTable,
  },
  data() {
    return {
      result: {},
      viewResult: {},
      createVisible: false,
      updateVisible: false,
      imageVisible: false,
      editPasswordVisible: false,
      btnAddRole: false,
      multipleSelection: [],
      userRole: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      condition: {
        components: IMAGE_REPO_CONFIGS
      },
      imageCondition: {
        components: IMAGE_REPO_IMAGE_CONFIGS
      },
      imagePage: 1,
      imageSize: 10,
      imageTotal: 0,
      tableData: [],
      form: {},
      direction: 'rtl',
      accounts:[],
      allAccounts:[],
      rule: {
        name: [
          {required: true, message: this.$t('commons.input_name'), trigger: 'blur'},
          {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
          {
            required: true,
            message: this.$t('workspace.special_characters_are_not_supported'),
            trigger: 'blur'
          }
        ],
      },
      buttons: [
        {
          tip: this.$t('image.image_list'), icon: "el-icon-more", type: "success",
          exec: this.handleList
        },
        {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        }, {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      buttons2: [
        {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        }, {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      buttons3: [
        {
          tip: this.$t('k8s.execute_scan'), icon: "el-icon-s-promotion", type: "success",
          exec: this.handleScan
        }
      ],
      statusFilters: [
        {text: this.$t('server.INVALID'), value: 'INVALID'},
        {text: this.$t('server.VALID'), value: 'VALID'},
        {text: this.$t('server.DELETE'), value: 'DELETE'}
      ],
      plugins: [
        {value: 'harbor.png', id: "Harbor"},
        {value: 'dockerhub.png', id: "DockerHub"},
        {value: 'nexus.png', id: "Nexus"},
        {value: 'aliyun.png', id: "Aliyun"},
        {value: 'qcloud.png', id: "Tencent"},
        {value: 'aws.png', id: "Aws"},
        {value: 'other.png', id: "Other"},
      ],
      cloudPluginDic:{
        "aliyun.png":{
          akTitle:"Access Key ID",
          skTitle:"Access Key Secret"
        },
        "qcloud.png":{
          akTitle:"Secret Id",
          skTitle:"Secret Key"
        },
        "aws.png":{
          akTitle:"accessKey",
          skTitle:"secretKey"
        },
      },
      imageData: [],
      syncData: [],
      syncVisible: false,
      repoId: "",
      innerAdd: false,
      addForm: {},
      sboms: [],
      versions: [],
      proxys: [],
      selectIds: new Set(),
      innerK8s: false,
      k8sData: [],
      handleItem: {},
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'image.image_repo_name',
          id: 'name'
        },
        {
          name: 'image.image_repo_url',
          id: 'repo'
        },
        {
          name: 'image.image_repo_user_name',
          id: 'userName'
        },
      ],
      checkAll: true,
      isIndeterminate: false,
      checkedColumnNames2: columnOptions2.map((ele) => ele.props),
      columnNames2: columnOptions2,
      //名称搜索
      items2: [
        {
          name: 'Project',
          id: 'project',
        },
        {
          name: 'Repository',
          id: 'repository',
        },
        {
          name: 'ImagePath',
          id: 'path',
        },
        {
          name: 'Size',
          id: 'size',
        },
        {
          name: 'Arch',
          id: 'arch',
        },
      ],
      checkAll2: true,
      isIndeterminate2: false,
      settingForm: {},
      settingVisible: false,
      syncTotal: 0,
      syncPage: 1,
      syncSize: 10,
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
    create() {
      this.form = {isBindAccount:false};
      this.createVisible = true;
    },
    change(e) {
      this.$forceUpdate();
    },
    setting() {
      this.viewResult = this.$get(imageRepoSettingUrl + "/" + this.handleItem.id, response => {
        let repoOld = response.data?response.data.repoOld:this.imageData[0].path.split('/')[0];
        let repo = response.data?response.data.repo:'';
        this.settingForm.repoId = this.handleItem.id;
        this.settingForm.repoOld = repoOld;
        this.settingForm.repo = repo;
        this.settingVisible = true;
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
    select(selection) {
      this.selectIds.clear();
      selection.forEach(s => {
        this.selectIds.add(s.id)
      });
    },
    selectAllAccount(){
      this.$get(allListUrl, response => {
        this.allAccounts = response.data;
      });
    },
    selectAccount(){
      this.accounts = this.allAccounts.filter(item => { return item.pluginIcon === this.form.pluginIcon});
    },
    filterStatus(value, row) {
      return row.status === value;
    },
    handleEdit(row) {
      this.updateVisible = true;
      this.form = row;
      if(!!row.credential&&row.pluginIcon === 'aliyun.png'){
        this.form.ak = JSON.parse(row.credential).accessKey
      }else if(!!row.credential&&row.pluginIcon === 'qcloud.png'){
        this.form.ak = JSON.parse(row.credential).secretId
      }else if(!!row.credential&&row.pluginIcon === 'aws.png'){
        this.form.ak = JSON.parse(row.credential).accessKey
      }
      this.selectAccount()
    },
    search() {
      this.result = this.$post(this.buildPagePath(imageRepoListUrl), this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleClose() {
      this.form = {};
      this.createVisible =  false;
      this.updateVisible =  false;
      this.imageVisible = false;
      this.syncVisible = false;
    },
    buildPagePath(path) {
      return path + this.currentPage + "/" + this.pageSize;
    },
    editRepo(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          if(this.form.pluginIcon === "aliyun.png"&&!this.form.isBindAccount){
            this.form.credential = '{"accessKey":"'+this.form.ak+'","secretKey":"'+this.form.sk+'"}'
          }
          if(this.form.pluginIcon === "qcloud.png"&&!this.form.isBindAccount){
            this.form.credential = '{"secretId":"'+this.form.ak+'","secretKey":"'+this.form.sk+'","APPID":""}'
          }
          if(this.form.pluginIcon === "aws.png"&&!this.form.isBindAccount){
            this.form.credential = '{"accessKey":"'+this.form.ak+'","secretKey":"'+this.form.sk+'"}'
          }
          this.viewResult = this.$post(editImageRepoUrl, this.form, () => {
            this.$success(this.$t('commons.save_success'));
            this.search();
            this.updateVisible = false;
          });
        } else {
          return false;
        }
      });
    },
    saveRepo(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          if(this.form.pluginIcon === "aliyun.png"&&!this.form.isBindAccount){
            this.form.credential = '{"accessKey":"'+this.form.ak+'","secretKey":"'+this.form.sk+'"}'
          }
          if(this.form.pluginIcon === "qcloud.png"&&!this.form.isBindAccount){
            this.form.credential = '{"secretId":"'+this.form.ak+'","secretKey":"'+this.form.sk+'","APPID":""}'
          }
          if(this.form.pluginIcon === "aws.png"&&!this.form.isBindAccount){
            this.form.credential = '{"accessKey":"'+this.form.ak+'","secretKey":"'+this.form.sk+'"}'
          }
          this.viewResult = this.$post(addImageRepoUrl, this.form, () => {
            this.$success(this.$t('commons.save_success'));
            this.search();
            this.createVisible = false;
          });
        } else {
          return false;
        }
      });
    },
    handleDelete(obj) {
      this.$alert(this.$t('workspace.delete_confirm') + obj.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(deleteImageRepoUrl + obj.id, response => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    handleList(item) {
      if (item) {
        this.handleItem = item;
        this.imageCondition.repoId = this.handleItem.id;
      }
      this.viewResult = this.$post(repoItemListUrl + this.imagePage + "/" + this.imageSize, this.imageCondition, response => {
        let data = response.data;
        this.imageTotal = data.itemCount;
        this.imageData = data.listObject;
        this.imageVisible = true;
        this.repoId = this.handleItem.id;
      });
    },
    handleSync() {
      let url = repoSyncListUrl + '/' + this.syncPage + '/' + this.syncSize;
      this.viewResult = this.$post(url, {repoId: this.repoId}, response => {
        let data = response.data;
        this.syncData = data.listObject;
        this.syncTotal = data.itemCount;
        this.syncVisible = true;
      });
    },
    sync() {
      this.$get(syncImageUrl + this.repoId, response => {
        this.$success(this.$t('commons.success'));
        this.handleSync();
      });
    },
    innerClose() {
      this.innerAdd = false;
      this.innerK8s = false;
      this.settingVisible = false;
      this.syncVisible = false;
    },
    initSboms() {
      this.viewResult = this.$post(allSbomListUrl, {},response => {
        this.sboms = response.data;
      });
    },
    async changeSbom(item) {
      let params = {
        sbomId: item.sbomId
      };
      this.viewResult = await this.$post(allSbomVersionListUrl, params,response => {
        this.versions = response.data;
      });
    },

    //查询代理
    activeProxy() {
      this.viewResult = this.$get(proxyListAllUrl, response => {
        this.proxys = response.data;
      });
    },
    handleScan(item) {
      this.activeProxy();
      this.addForm = item;
      this.addForm.name = item.path;
      if(this.sboms && this.sboms.length > 0) {
        this.addForm.sbomId = this.sboms[0].id;
        this.initSbom({sbomId: this.addForm.sbomId});
      }
      this.innerAdd = true;
    },
    async initSbom(params) {
      this.viewResult = await this.$post(allSbomVersionListUrl, params,response => {
        this.versions = response.data;
        if(this.versions && this.versions.length > 0) this.addForm.sbomVersionId = this.versions[0].id;
      });
    },
    saveSetting() {
      this.$refs['settingForm'].validate(valid => {
        if (valid) {
          this.viewResult = this.$post(imageRepoSettingUrl, this.settingForm, response => {
            if (response.success) {
              this.$success(this.$t('commons.success'));
              this.settingVisible = false;
              this.handleList();
            } else {
              this.$error(this.$t('commons.error'));
            }
          });
        }
      });
    },
    saveAdd() {
      this.$refs['addForm'].validate(valid => {
        if (valid) {
          this.viewResult = this.$post(scanImageRepoUrl, this.addForm, response => {
            if (response.success) {
              this.$success(this.$t('schedule.event_start'));
              this.innerAdd = false;
              this.imageVisible = false;
              this.$router.push({
                path: '/image/image',
                query: {
                  date: new Date().getTime()
                },
              }).catch(error => error);
            } else {
              this.$error(this.$t('schedule.event_failed'));
            }
          });
        }
      });
    },
    saveAddAll() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('commons.please_select') + this.$t('image.image_url'));
        return;
      }
      this.$alert(this.$t('image.one_scan') + this.$t('image.image_scan') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.viewResult = this.$request({
              method: 'POST',
              url: scanImagesRepoUrl,
              data: Array.from(this.selectIds),
              headers: {
                'Content-Type': undefined
              }
            }, res => {
              if (res.success) {
                this.imageVisible = false;
                this.$success(this.$t('commons.success'));
                this.$router.push({
                  path: '/image/image',
                  query: {
                    date: new Date().getTime()
                  },
                }).catch(error => error);
              } else {
                this.$error(this.$t('account.error'));
              }
            });
          }
        }
      });
    },
    showK8s(item) {
      this.k8sData = item.imageRepoItemK8sDTOList;
      this.innerK8s = true;
    },
    deleteBatch() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('commons.please_select') + this.$t('image.image_repo'));
        return;
      }
      this.$alert(this.$t('oss.delete_batch') + this.$t('image.image_repo') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$request({
              method: 'POST',
              url: deleteImageReposUrl,
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
  created() {
    this.selectIds.clear();
    this.search();
    this.initSboms();
    this.selectAllAccount()
  }
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
  width: 100%;
}
.rtl >>> .el-form-item__content {
  width: 80%;
}
.table-card >>> .search {
  width: 450px !important;
}
.table-card >>> .search .el-input {
  width: 140px !important;
}
.image-list >>> .el-drawer__header {
  margin: 0;
}
/deep/ :focus{outline:0;}
</style>
