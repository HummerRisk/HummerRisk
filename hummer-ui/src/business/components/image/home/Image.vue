<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('image.image_list')"
                      @create="create" :createTip="$t('image.create')"
                      :show-create="true"
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
        <el-table-column prop="name" :label="$t('image.image_name')" v-if="checkedColumnNames.includes('name')" min-width="180" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 24px; height: 24px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }} {{scope.row.pluginIcon}}
              </span>
          </template>
        </el-table-column>
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('returnSum')" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="200">
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
        <el-table-column v-slot:default="scope" v-if="checkedColumnNames.includes('resultStatus')" :label="$t('image.result_status')" min-width="140" prop="resultStatus" sortable show-overflow-tooltip>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
            <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="success" v-else-if="scope.row.resultStatus === 'FINISHED'">
            <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="danger" v-else-if="scope.row.resultStatus === 'ERROR'">
            <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
          </el-button>
          <el-button @click="showResultLog(scope.row)" plain size="medium" type="warning" v-else-if="scope.row.resultStatus === 'WARNING'">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_has_warn') }}
          </el-button>
          <el-button @click="noWarnLog(scope.row)" plain size="medium" type="info" v-else-if="scope.row.resultStatus === null">
            <i class="el-icon-warning"></i> {{ $t('resource.i18n_no_warn') }}
          </el-button>
        </el-table-column>
        <el-table-column prop="scanTime" min-width="200" v-if="checkedColumnNames.includes('scanTime')" :label="$t('commons.last_scan_time')" sortable>
          <template v-slot:default="scope">
            <span v-if="scope.row.resultStatus !== null"><i class="el-icon-time"/> {{ scope.row.scanTime | timestampFormatDate }}</span>
            <span v-if="scope.row.resultStatus === null">--</span>
          </template>
        </el-table-column>
        <el-table-column prop="imageUrl" v-slot:default="scope" v-if="checkedColumnNames.includes('imageUrl')" :label="$t('image.image_url')" min-width="230" show-overflow-tooltip>
          <div v-if="scope.row.type==='repo'">
            <el-tooltip class="item" effect="dark" :content="scope.row.imageUrl + ':' + scope.row.imageTag" placement="top-start">
              <span>{{ scope.row.imageUrl + ':' + scope.row.imageTag }}</span>
            </el-tooltip>
          </div>
          <div v-if="scope.row.type==='image'">
            <el-tooltip class="item" effect="dark" :content="scope.row.imageUrl + ':' + scope.row.imageTag" placement="top-start">
              <span>{{ scope.row.imageUrl + ':' + scope.row.imageTag }}</span>
            </el-tooltip>
          </div>
          <div v-if="scope.row.type==='tar'">
            <el-tooltip class="item" effect="dark" :content="scope.row.path" placement="top-start">
              <span>{{ scope.row.path }}</span>
            </el-tooltip>
          </div>
        </el-table-column>
        <el-table-column prop="imageRepoName" v-slot:default="scope" v-if="checkedColumnNames.includes('imageRepoName')" :label="$t('image.image_repo_name')" min-width="150" show-overflow-tooltip>
          {{ scope.row.imageRepoName?scope.row.imageRepoName:$t('image.no_image_repo') }}
        </el-table-column>
        <el-table-column prop="size" v-slot:default="scope" v-if="checkedColumnNames.includes('size')" :label="$t('fs.size')" min-width="100" show-overflow-tooltip>
          <span v-if="scope.row.size">{{ scope.row.size }}</span>
          <span v-else>{{ '--' }}</span>
        </el-table-column>
        <el-table-column min-width="160" v-if="checkedColumnNames.includes('createTime')" :label="$t('account.create_time')" sortable
                         prop="createTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.createTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="160" v-if="checkedColumnNames.includes('updateTime')" :label="$t('commons.update_time')" sortable
                         prop="updateTime">
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" :label="$t('account.creator')" v-if="checkedColumnNames.includes('userName')" min-width="100" show-overflow-tooltip/>
        <el-table-column min-width="230" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>

      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create image-->
    <el-drawer class="rtl" :title="$t('image.create')" :visible.sync="createVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form" :rules="rule">
        <el-form-item :label="$t('sbom.sbom_project')" :rules="{required: true, message: $t('sbom.sbom_project') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" filterable :clearable="true" v-model="form.sbomId" :placeholder="$t('sbom.sbom_project')" @change="changeSbom(form)">
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
          <el-select style="width: 100%;" filterable :clearable="true" v-model="form.sbomVersionId" :placeholder="$t('sbom.sbom_project_version')">
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
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('image.image_name')"/>
        </el-form-item>
        <el-form-item :label="$t('image.is_select_repo')" :rules="{required: true, message: $t('image.image_repo') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-switch v-model="form.isImageRepo"></el-switch>
        </el-form-item>
        <el-form-item v-if="form.isImageRepo" :label="$t('image.image_repo')" :rules="{required: true, message: $t('image.image_repo') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" filterable :clearable="true" v-model="form.repoId" :placeholder="$t('image.image_repo_url')" @change="changeImage">
            <el-option
              v-for="item in repos"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              &nbsp;&nbsp; {{ item.name + ':' + item.repo }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-switch v-model="form.isProxy"></el-switch>
        </el-form-item>
        <el-form-item v-if="form.isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
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
        <el-form-item :label="$t('commons.remark')" ref="type" prop="type">
          <el-popover placement="right-end" :title="$t('image.image_type')" width="800" trigger="click">
            <hr-code-edit :read-only="true" height="200px" :data.sync="content" :modes="modes" :mode="'html'"/>
            <el-button icon="el-icon-warning" plain size="mini" slot="reference">
              {{ $t('image.image_type') }}
            </el-button>
          </el-popover>
        </el-form-item>
        <el-form-item :label="$t('image.image_type')" ref="type" prop="type">
          <el-radio v-model="form.type" label="repo">{{ $t('image.image_rp') }}</el-radio>
          <el-radio v-model="form.type" label="image">{{ $t('image.image_u') }}</el-radio>
          <el-radio v-model="form.type" label="tar">{{ $t('image.image_tar') }}</el-radio>
        </el-form-item>
        <el-form-item v-if="form.type==='repo'" :label="$t('image.image_list')" ref="type" prop="type">
          <el-select style="width: 100%;" filterable :clearable="true" v-model="form.repoItemId" :placeholder="$t('image.image_list')">
            <el-option
              v-for="item in images"
              :key="item.id"
              :label="item.path"
              :value="item.id">
              &nbsp;&nbsp; {{ item.path }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type==='image'" :label="$t('image.image_url_tag')" ref="type" prop="type">
          <el-input class="input-inline-i" v-model="form.imageUrl" autocomplete="off" :placeholder="$t('image.image_url')"/>
          {{ ' : ' }}
          <el-input class="input-inline-t" v-model="form.imageTag" autocomplete="off" :placeholder="$t('image.image_tag')"/>
        </el-form-item>
        <el-form-item v-if="form.type==='tar'" :label="$t('image.image_url')" ref="type" prop="type">
          <image-tar-upload v-on:appendTar="appendTar" v-model="form.path" :param="form.path"/>
        </el-form-item>
        <el-form-item>
          <span style="color: red">{{ $t('image.image_note') }}</span>
        </el-form-item>
      </el-form>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="createVisible = false"
          @confirm="save('form')"/>
      </div>
    </el-drawer>
    <!--Create image-->

    <!--Update image-->
    <el-drawer class="rtl" :title="$t('image.update')" :visible.sync="updateVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="small" ref="form" :rules="rule">
        <el-form-item :label="$t('sbom.sbom_project')" :rules="{required: true, message: $t('sbom.sbom_project') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" filterable :clearable="true" v-model="form.sbomId" :placeholder="$t('sbom.sbom_project')" @change="changeSbom(form)">
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
          <el-select style="width: 100%;" filterable :clearable="true" v-model="form.sbomVersionId" :placeholder="$t('sbom.sbom_project_version')">
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
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('image.image_name')"/>
        </el-form-item>
        <el-form-item :label="$t('image.is_select_repo')" :rules="{required: true, message: $t('image.image_repo') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-switch v-model="form.isImageRepo"></el-switch>
        </el-form-item>
        <el-form-item v-if="form.isImageRepo" :label="$t('image.image_repo')" :rules="{required: true, message: $t('image.image_repo') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-select style="width: 100%;" filterable :clearable="true" v-model="form.repoId" :placeholder="$t('image.image_repo_url')" @change="changeImage">
            <el-option
              v-for="item in repos"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              &nbsp;&nbsp; {{ item.name + ':' + item.repo }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-switch v-model="form.isProxy"></el-switch>
        </el-form-item>
        <el-form-item v-if="form.isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
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
        <el-form-item :label="$t('commons.remark')" ref="type" prop="type">
          <el-popover placement="right-end" :title="$t('image.image_type')" width="800" trigger="click">
            <hr-code-edit :read-only="true" height="200px" :data.sync="content" :modes="modes" :mode="'html'"/>
            <el-button icon="el-icon-warning" plain size="mini" slot="reference">
              {{ $t('image.image_type') }}
            </el-button>
          </el-popover>
        </el-form-item>
        <el-form-item :label="$t('image.image_type')" ref="type" prop="type">
          <el-radio v-model="form.type" label="repo">{{ $t('image.image_rp') }}</el-radio>
          <el-radio v-model="form.type" label="image">{{ $t('image.image_u') }}</el-radio>
          <el-radio v-model="form.type" label="tar">{{ $t('image.image_tar') }}</el-radio>
        </el-form-item>
        <el-form-item v-if="form.type==='repo'" :label="$t('image.image_list')" ref="type" prop="type">
          <el-select style="width: 100%;" filterable :clearable="true" v-model="form.repoItemId" :placeholder="$t('image.image_list')">
            <el-option
              v-for="item in images"
              :key="item.id"
              :label="item.path"
              :value="item.id">
              &nbsp;&nbsp; {{ item.path }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type==='image'" :label="$t('image.image_url_tag')" ref="type" prop="type">
          <el-input class="input-inline-i" v-model="form.imageUrl" autocomplete="off" :placeholder="$t('image.image_url')"/>
          {{ ' : ' }}
          <el-input class="input-inline-t" v-model="form.imageTag" autocomplete="off" :placeholder="$t('image.image_tag')"/>
        </el-form-item>
        <el-form-item v-if="form.type==='tar'" :label="$t('image.image_url')" ref="type" prop="type">
          <image-tar-upload v-on:appendTar="appendTar" v-model="form.path" :param="form.path"/>
        </el-form-item>
        <el-form-item>
          <span style="color: red">{{ $t('image.image_note') }}</span>
        </el-form-item>
      </el-form>
      <div style="margin: 10px;">
        <dialog-footer
          @cancel="updateVisible = false"
          @confirm="update('form')"/>
      </div>
    </el-drawer>
    <!--Update image-->

    <!--Result log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="85%"
               :before-close="handleClose" :direction="direction"
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
                  {{ logForm.name }}
                  <i class="el-icon-document-copy" @click="copy(logForm)" style="display: none;"></i>
                </span>
                <span class="grid-content-log-span">
                  <img :src="require(`@/assets/img/platform/docker.png`)"
                       style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ logForm.imageName }}
                </span>
                <span class="grid-content-status-span" v-if="logForm.resultStatus === 'APPROVED'"
                      style="color: #579df8">
                  <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'FINISHED'"
                      style="color: #7ebf50">
                  <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'ERROR'" style="color: red;">
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
                      {{ scope.row.output }}<br>
                </span>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <log-form :logForm="logForm"/>
      </el-row>
      <template v-slot:footer>
        <dialog-footer
            @cancel="logVisible = false"
            @confirm="logVisible = false"/>
      </template>
    </el-drawer>
    <!--Result log-->

  </main-container>
</template>

<script>
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperators from "@/business/components/common/components/TableOperators";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import TableOperatorButton from "@/business/components/common/components/TableOperatorButton";
import ImageStatus from "../head/ImageStatus";
import {_filter, _sort} from "@/common/js/utils";
import LogForm from "@/business/components/image/home/LogForm";
import HrCodeEdit from "@/business/components/common/components/HrCodeEdit";
import ImageUpload from "../head/ImageUpload";
import ImageTarUpload from "../head/ImageTarUpload";
import HideTable from "@/business/components/common/hideTable/HideTable";
import MainContainer from "@/business/components/common/components/MainContainer";
import {IMAGE_CONFIGS} from "@/business/components/common/components/search/search-components";
import {
  addImageUrl,
  allImageReposUrl,
  changeImageUrl,
  deleteImageUrl,
  getImageResultUrl,
  getImageResultWithBLOBsUrl,
  imageDownloadUrl,
  imageListUrl,
  imageLogUrl,
  scanImageUrl,
  updateImageUrl
} from "@/api/k8s/image/image";
import {allSbomListUrl, allSbomVersionListUrl} from "@/api/k8s/sbom/sbom";
import {proxyListAllUrl} from "@/api/system/system";
import {saveAs} from "@/common/js/FileSaver";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'image.image_name',
    props: 'name',
    disabled: false
  },
  {
    label: 'resource.i18n_not_compliance',
    props: 'returnSum',
    disabled: false
  },
  {
    label: 'image.result_status',
    props: 'resultStatus',
    disabled: false
  },
  {
    label: 'commons.last_scan_time',
    props: 'scanTime',
    disabled: false
  },
  {
    label: 'image.image_url',
    props: 'imageUrl',
    disabled: false
  },
  {
    label: 'image.image_size',
    props: 'size',
    disabled: false
  },
  {
    label: 'image.image_repo_name',
    props: 'imageRepoName',
    disabled: false
  },
  {
    label: 'commons.create_time',
    props: 'createTime',
    disabled: false
  },
  {
    label: 'commons.update_time',
    props: 'updateTime',
    disabled: false
  },
  {
    label: 'account.creator',
    props: 'userName',
    disabled: false
  },
];

/* eslint-disable */
export default {
  name: "imageSetting",
  components: {
    TablePagination,
    TableHeader,
    TableOperators,
    DialogFooter,
    TableOperatorButton,
    ImageStatus,
    HrCodeEdit,
    ImageUpload,
    ImageTarUpload,
    MainContainer,
    HideTable,
    LogForm,
  },
  data() {
    return {
      queryPath: imageListUrl,
      deletePath: deleteImageUrl,
      createPath: addImageUrl,
      updatePath: updateImageUrl,
      result: {},
      createVisible: false,
      updateVisible: false,
      editPasswordVisible: false,
      btnAddRole: false,
      multipleSelection: [],
      userRole: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      condition: {
        components: IMAGE_CONFIGS
      },
      tableData: [],
      form: {},
      direction: 'rtl',
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
          tip: this.$t('account.scan'), icon: "el-icon-s-promotion", type: "success",
          exec: this.handleScan
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
        {text: this.$t('server.INVALID'), value: 'INVALID'},
        {text: this.$t('server.VALID'), value: 'VALID'},
        {text: this.$t('server.DELETE'), value: 'DELETE'}
      ],
      proxyForm: {isProxy: false, proxyId: 0},
      proxys: [],
      repos: [],
      location: "",
      modes: ['text', 'html'],
      content: this.$t('image.image_support'),
      tarFile: Object,
      sboms: [],
      versions: [],
      images: [],
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'image.image_name',
          id: 'name'
        },
        {
          name: 'image.image_url',
          id: 'repo'
        },
        {
          name: 'image.image_repo_name',
          id: 'imageRepoName'
        },
      ],
      checkAll: true,
      isIndeterminate: false,
      logVisible: false,
      detailVisible: false,
      logForm: {},
      logData: [],
      detailForm: {},
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
    handleVuln() {
      window.open('http://www.cnnvd.org.cn/','_blank','');
    },
    select(selection) {
    },
    create() {
      this.form = {type: 'image'};
      if(this.sboms && this.sboms.length > 0) {
        this.form.sbomId = this.sboms[0].id;
        this.initSbom({sbomId: this.form.sbomId});
      }
      this.createVisible = true;
    },
    async initSbom(params) {
      await this.$post(allSbomVersionListUrl, params,response => {
        this.versions = response.data;
        if(this.versions && this.versions.length > 0) this.form.sbomVersionId = this.versions[0].id;
      });
    },
    initSboms() {
      this.result = this.$post(allSbomListUrl, {},response => {
        this.sboms = response.data;
      });
    },
    changeSbom(item) {
      let params = {
        sbomId: item.sbomId
      };
      this.result = this.$post(allSbomVersionListUrl, params,response => {
        this.versions = response.data;
      });
    },
    sort(column) {
      _sort(column, this.condition);
      this.search();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.search();
    },
    filterStatus(value, row) {
      return row.status === value;
    },
    handleScan(data) {
      if(data.type === 'image') {
        if(!data.imageUrl || !data.imageUrl) {
          this.$warning(this.$t('image.no_image'));
          return;
        }
      } else if (data.type === 'tar') {
        if(!data.path) {
          this.$warning(this.$t('image.no_package'));
          return;
        }
      }
      this.$alert(this.$t('image.one_scan') + this.$t('image.image_rule') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$get(scanImageUrl + data.id, response => {
              if (response.success) {
                this.$success(this.$t('schedule.event_start'));
                this.search();
              } else {
                this.$error(this.$t('schedule.event_failed'));
              }
            });
          }
        }
      });
    },
    save(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          let formData = new FormData();
          if (this.tarFile) {
            formData.append("tarFile", this.tarFile);
          }
          formData.append("request", new Blob([JSON.stringify(this.form)], {type: "application/json"}));
          let axiosRequestConfig = {
            method: "POST",
            url: this.createPath,
            data: formData,
            headers: {
              "Content-Type": 'multipart/form-data'
            }
          };
          this.result = this.$request(axiosRequestConfig, () => {
            this.$success(this.$t('commons.save_success'));
            this.search();
            this.createVisible = false;
          });
        } else {
          return false;
        }
      });
    },
    update(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          let formData = new FormData();
          if (this.tarFile) {
            formData.append("tarFile", this.tarFile);
          }
          formData.append("request", new Blob([JSON.stringify(this.form)], {type: "application/json"}));
          let axiosRequestConfig = {
            method: "POST",
            url: this.updatePath,
            data: formData,
            headers: {
              "Content-Type": 'multipart/form-data'
            }
          };
          this.result = this.$request(axiosRequestConfig, (res) => {
            if (res.success) {
              this.$success(this.$t('commons.save_success'));
              this.search();
              this.updateVisible = false;
            }
          });
        } else {
          return false;
        }
      });
    },
    handleEdit(row) {
      this.changeSbom({sbomId: row.sbomId});
      this.updateVisible = true;
      this.form = row;
      if(this.form.repo) this.form.isImageRepo = true;
      if(this.form.proxyId) this.form.isProxy = true;
      if(this.form.pluginIcon !== 'docker.png') this.form.isImageIcon = true;
      this.changeImage(row.repoId);
    },
    search() {
      this.result = this.$post(this.buildPagePath(this.queryPath), this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
      this.initSboms();
    },
    handleClose() {
      this.form = {};
      this.createVisible =  false;
      this.updateVisible =  false;
      this.logVisible = false;
      this.detailVisible = false;
    },
    //查询代理
    activeProxy() {
      this.result = this.$get(proxyListAllUrl, response => {
        this.proxys = response.data;
      });
    },
    //查询仓库
    activeRepo() {
      this.result = this.$get(allImageReposUrl, response => {
        this.repos = response.data;
      });
    },
    buildPagePath(path) {
      return path + this.currentPage + "/" + this.pageSize;
    },
    tableRowClassName({row, rowIndex}) {
      if (rowIndex%4 === 0) {
        return 'success-row';
      } else if (rowIndex%2 === 0) {
        return 'warning-row';
      } else {
        return '';
      }
    },
    handleDelete(obj) {
      this.$alert(this.$t('workspace.delete_confirm') + obj.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get(this.deletePath + obj.id, response => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    appendTar(file) {
      this.tarFile = file;
    },
    changeImage(id) {
      this.$post(changeImageUrl, {repoId: id}, response => {
        this.images = response.data;
      });
    },
    noWarnLog(item) {
      this.$warning(item.name + this.$t('resource.i18n_no_warn'));
    },
    showResultLog(result) {
      this.result = this.$get(imageLogUrl + result.resultId, response => {
        this.logData = response.data;
      });
      this.result = this.$get(getImageResultWithBLOBsUrl + result.resultId, response => {
        this.logForm = response.data;
        this.logForm.resultJson = JSON.parse(this.logForm.resultJson);
      });
      this.logVisible = true;
    },
    getStatus() {
      if (this.checkStatus(this.tableData)) {
        this.search();
        clearInterval(this.timer);
      } else {
        for (let data of this.tableData) {
          this.$get(getImageResultUrl + data.resultId, response => {
            let result = response.data;
            if (result && data.resultStatus !== result.resultStatus) {
              data.resultStatus = result.resultStatus;
              data.returnSum = result.returnSum;
              data.critical = result.critical ? result.critical : 0;
              data.high = result.high ? result.high : 0;
              data.medium = result.medium ? result.medium : 0;
              data.low = result.low ? result.low : 0;
              data.unknown = result.unknown ? result.unknown : 0;
            }
          });
        }
      }
    },
    //是否是结束状态，返回false代表都在运行中，true代表已结束
    checkStatus(tableData) {
      let sum = 0;
      for (let row of tableData) {
        if (row.resultStatus && row.resultStatus != 'ERROR' && row.resultStatus != 'FINISHED' && row.resultStatus != 'WARNING') {
          sum++;
        }
      }
      return sum == 0;
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
      let p = '/image/resultdetails/' + params.resultId;
      this.$router.push({
        path: p
      }).catch(error => error);
    },
    handleDownload(item) {
      if (!item.resultId) {
        this.$warning(this.$t('resource.i18n_no_warn'));
        return;
      }
      this.$post(imageDownloadUrl, {
        id: item.resultId
      }, response => {
        if (response.success) {
          let blob = new Blob([response.data], {type: "application/json"});
          saveAs(blob, item.name + ".json");
        }
      }, error => {
        console.log("下载报错", error);
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
  computed: {
    codemirror() {
      return this.$refs.cmEditor.codemirror;
    }
  },
  activated() {
    this.search();
    this.activeProxy();
    this.activeRepo();
    this.location = window.location.href.split("#")[0];
    this.timer = setInterval(this.getStatus, 10000);
  },
  beforeDestroy() {
    clearInterval(this.timer);
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
/deep/ :focus{outline:0;}
.el-row-card {
  padding: 0 5px 0 5px;
  margin: 0.5% 1% 0.5% 1%;
}
.split {
  height: 70px;
  border-left: 1px solid #D8DBE1;
}
.cl-ver-col {
  vertical-align: middle;
}
.cl-mid-row {
  margin: 0 0 1% 0;
}
.cl-btn-mid-row {
  margin: 1%;
}
.cl-span-col {
  margin: 1% 0;
}
.cl-btn-col {
  margin: 4% 0;
}
.cl-data-col {
  color: #888;
  font-size: 10px;
}
.cl-btn-data-col {
  color: #77aff9;
}

.input-inline-i{
  display:inline;
}

.input-inline-i >>> .el-input__inner {
  width: 68%;
}

.input-inline-t{
  display:inline;
}

.input-inline-t >>> .el-input__inner {
  width: 30%;
}

.co-el-img >>> .el-image {
  display: table-cell;
  left: 15%;
}
.cp-el-i {
  margin: 0.5% 1% 0.5% 1%;
}
.co-el-i{
  width: 60px;
  height: 60px;
}
.word-wrap{
  width: 98%;
  display:block;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
}
.table-content {
  width: 100%;
}

.el-form-item-dev >>> .el-form-item__content {
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

.tag-v {
  margin: 10px;
  cursor: pointer;
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

.el-form-item-dev >>> .el-form-item__content {
  margin-left: 0 !important;
}

.grid-content-log-span {
  width: 39%;
  float: left;
  vertical-align: middle;
  display: table-cell;
  margin: 6px 0 6px 2px;
  color: #606266;
}

.grid-content-status-span {
  width: 20%;
  float: left;
  vertical-align: middle;
  display: table-cell;
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

.txt-click {
  cursor: pointer;
}

.txt-click:hover {
  color: aliceblue;
  text-shadow: 1px 1px 1px #000;
}

* {
  touch-action: pan-y;
}

/deep/ :focus {
  outline: 0;
}
</style>
