<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('oss.oss_bucket')"
                      @create="create" :createTip="$t('oss.create_bucket')"
                      @deleteSelect="deleteSelect" :deleteTip="$t('oss.delete_batch')" :show-delete="true" :show-create="true"
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
        <el-table-column type="selection" min-width="40">
        </el-table-column>
        <el-table-column type="index" min-width="40"/>
        <el-table-column prop="bucketName" :label="$t('oss.bucket')" v-if="checkedColumnNames.includes('bucketName')" min-width="160" show-overflow-tooltip v-slot:default="scope">
          <el-link type="primary" @click="showObject(scope.row)">
            {{ scope.row.bucketName }}
          </el-link>
        </el-table-column>
        <el-table-column :label="$t('oss.name')" v-if="checkedColumnNames.includes('name')" min-width="110" show-overflow-tooltip>
          <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
          </template>
        </el-table-column>
        <el-table-column prop="location" v-if="checkedColumnNames.includes('location')" :label="$t('oss.location')" min-width="110" show-overflow-tooltip></el-table-column>
        <el-table-column prop="cannedAcl" v-if="checkedColumnNames.includes('cannedAcl')" :label="$t('oss.acl')" min-width="110" show-overflow-tooltip v-slot:default="scope">
          <span v-if="scope.row.cannedAcl === 'public-read-write' || scope.row.cannedAcl === 'PublicReadWrite'">{{ $t('oss.public_read_write') }}</span>
          <span v-else-if="scope.row.cannedAcl === 'public-read' || scope.row.cannedAcl === 'PublicRead'">{{ $t('oss.public_read') }}</span>
          <span v-else-if="scope.row.cannedAcl === 'private' || scope.row.cannedAcl === 'Private'">{{ $t('oss.private') }}</span>
          <span v-else>{{ scope.row.cannedAcl }}</span>
        </el-table-column>
        <el-table-column prop="storageClass" v-if="checkedColumnNames.includes('storageClass')" :label="$t('oss.storage_class')" min-width="100" show-overflow-tooltip v-slot:default="scope">
          <span v-if="scope.row.storageClass === 'Standard' || scope.row.storageClass === 'STANDARD'">{{ $t('oss.standard') }}</span>
          <span v-else-if="scope.row.storageClass === 'IA' || scope.row.storageClass === 'STANDARD_IA' || scope.row.storageClass === 'WARM'">{{ $t('oss.ia') }}</span>
          <span v-else-if="scope.row.storageClass === 'Archive' || scope.row.storageClass === 'ARCHIVE'">{{ $t('oss.archive') }}</span>
          <span v-else-if="scope.row.storageClass === 'COLD'">{{ $t('oss.cold') }}</span>
          <span v-else>{{ scope.row.storageClass }}</span>
        </el-table-column>
        <el-table-column prop="size" v-if="checkedColumnNames.includes('size')" :label="$t('oss.oss_size')" min-width="90" show-overflow-tooltip></el-table-column>
        <el-table-column prop="objectNumber" v-if="checkedColumnNames.includes('objectNumber')" :label="$t('oss.object_number')" min-width="90" show-overflow-tooltip></el-table-column>
        <el-table-column min-width="50" :label="$t('commons.operating')" fixed="right">
          <template v-slot:default="scope">
            <table-operators :buttons="bucketButtons" :row="scope.row"/>
          </template>
        </el-table-column>
      </hide-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--oss bucket-->
    <el-drawer class="rtl" :title="$t('oss.oss_bucket')" :visible.sync="bucketVisible" size="80%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true" v-loading="ossResult.loading">
      <el-row class="el-btn">
        <el-button type="primary" icon="el-icon-upload2" size="medium" plain @click="upload">{{ $t('oss.i18n_upload') }}</el-button>
        <el-button type="success" icon="el-icon-folder-add" size="medium" plain @click="addDir">{{ $t('oss.add_dir') }}</el-button>
        <el-button type="danger" icon="el-icon-folder-delete" size="medium" plain @click="deleteSelects">{{ $t('commons.delete') }}</el-button>
        <el-button type="info" icon="el-icon-refresh" size="medium" plain @click="refresh">{{ $t('commons.refresh') }}</el-button>
      </el-row>
      <el-table :border="true" :data="objectData" class="adjust-table table-content table-inner" @sort-change="sort" stripe @select-all="selectObjects" @select="selectObjects">
        <el-table-column type="selection" min-width="50">
        </el-table-column>
        <el-table-column type="index" min-width="50"></el-table-column>
        <el-table-column prop="objectName" :label="$t('oss.object_name')" min-width="200" show-overflow-tooltip v-slot:default="scope">
          <el-link v-if="scope.row.objectType==='BACK'" type="primary" style="color: red;" @click="backObject(scope.row)">
            <i class="el-icon-back"></i>  {{ scope.row.objectName }}
          </el-link>
          <el-link v-if="scope.row.objectType==='DIR'" type="primary" @click="selectObject(scope.row)">
            <i class="el-icon-folder-opened"></i>  {{ scope.row.objectName }}
          </el-link>
          <span v-if="scope.row.objectType==='FILE'" style="color: #336d9f" @click="objectDownload(scope.row)">
            <i class="el-icon-document"></i> {{ scope.row.objectName }}
          </span>
        </el-table-column>
        <el-table-column prop="objectType" :label="$t('oss.object_type')" min-width="100" show-overflow-tooltip v-slot:default="scope">
          <span v-if="scope.row.objectType==='DIR'">{{ $t('oss.object_dir') }}</span>
          <span v-if="scope.row.objectType==='FILE'">{{ $t('oss.object_file') }}</span>
          <span v-if="scope.row.objectType==='BACK'">{{ $t('vis.back') }}</span>
        </el-table-column>
        <el-table-column prop="objectSize" :label="$t('oss.oss_size')" min-width="120" show-overflow-tooltip v-slot:default="scope">
          {{ scope.row.objectSize?scope.row.objectSize:'-' }}
        </el-table-column>
        <el-table-column prop="storageClass" :label="$t('oss.storage_class')" min-width="120" show-overflow-tooltip v-slot:default="scope">
          <span v-if="scope.row.storageClass === 'Standard' || scope.row.storageClass === 'STANDARD'">{{ $t('oss.standard') }}</span>
          <span v-else-if="scope.row.storageClass === 'IA' || scope.row.storageClass === 'STANDARD_IA' || scope.row.storageClass === 'WARM'">{{ $t('oss.ia') }}</span>
          <span v-else-if="scope.row.storageClass === 'Archive' || scope.row.storageClass === 'ARCHIVE'">{{ $t('oss.archive') }}</span>
          <span v-else-if="scope.row.storageClass === 'COLD'">{{ $t('oss.cold') }}</span>
          <span v-else>{{ scope.row.storageClass }}</span>
        </el-table-column>
        <el-table-column min-width="160" :label="$t('account.update_time')" sortable prop="lastModified">
          <template v-slot:default="scope">
            <span v-if="scope.row.lastModified">{{ scope.row.lastModified | timestampFormatDate }}</span>
            <span v-if="!scope.row.lastModified">{{ '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="90" :label="$t('commons.operating')">
          <template v-slot:default="scope">
            <table-operators v-if="scope.row.objectType==='DIR'" :buttons="objectButtons1" :row="scope.row"/>
            <table-operators v-if="scope.row.objectType==='FILE'" :buttons="objectButtons2" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>

      <el-drawer
        size="40%"
        :title="$t('oss.i18n_upload')"
        :append-to-body="true"
        :before-close="innerDrawerClose"
        :visible.sync="innerDrawer1">
        <el-form :model="uploadForm" label-position="right" label-width="150px" size="small" ref="form">
          <el-form-item :label="$t('oss.object_file')" :rules="{required: true, message: $t('oss.object_file') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <upload v-on:appendUpload="appendUpload" v-model="uploadForm.path"/>
          </el-form-item>
          <div style="color: red;font-style:oblique;margin: 10px 0 10px 100px;">
            <div>{{ $t('oss.bucket_tips9') }}</div>
            <div>{{ $t('oss.bucket_tips2') }}</div>
            <div>{{ $t('oss.bucket_tips10') }}</div>
            <div>{{ $t('oss.bucket_tips8') }}</div>
            <div>{{ $t('oss.bucket_tips11') }}</div>
          </div>
        </el-form>
        <dialog-footer
          @cancel="innerDrawer1 = false"
          @confirm="uploadFile()"/>
      </el-drawer>

      <el-drawer
        size="70%"
        :title="$t('oss.add_dir')"
        :append-to-body="true"
        :before-close="innerDrawerClose"
        :visible.sync="innerDrawer2">
        <el-form :model="dirForm" label-position="right" label-width="150px" size="small" ref="form">
          <el-form-item :label="$t('oss.dir_name')" :rules="{required: true, message: $t('oss.dir_name') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-input type="text" class="dir" v-model="dirForm.dir" @input="change($event)" autocomplete="off" :placeholder="$t('oss.dir_name')"/>
          </el-form-item>
          <div style="color: red;font-style:oblique;margin: 10px 0 10px 100px;">
            <div>{{ $t('oss.bucket_tips1') }}</div>
            <div>{{ $t('oss.bucket_tips2') }}</div>
            <div>{{ $t('oss.bucket_tips3') }}</div>
            <div>{{ $t('oss.bucket_tips4') }}</div>
            <div>{{ $t('oss.bucket_tips5') }}</div>
          </div>
        </el-form>
        <dialog-footer
          @cancel="innerDrawer2 = false"
          @confirm="submitDir()"/>
      </el-drawer>

    </el-drawer>
    <!--oss bucket-->

    <!--create oss bucket-->
    <el-drawer class="rtl" :title="ossTitle" :visible.sync="visible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <div v-loading="ossResult.loading">
        <el-form :model="form" label-position="right" label-width="150px" size="small" :rules="rule" ref="form">
          <el-form-item :label="$t('oss.bucket_name')" :rules="{required: true, message: $t('oss.bucket_name') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-input type="text" v-model="form.bucketName" @input="change($event)" autocomplete="off" :placeholder="$t('oss.bucket_name')"/>
          </el-form-item>
          <el-form-item :label="$t('oss.oss_account')" :rules="{required: true, message: $t('oss.oss_account') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="form.ossId" :placeholder="$t('account.please_choose_account')" @change="changeAccount(form.ossId)">
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
          <el-form-item v-if="bucketParams.showLocation" :label="$t('account.regions')" :rules="{required: true, message: $t('account.regions') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="form.location" :placeholder="$t('account.please_choose_region')">
              <el-option
                v-for="item in bucketParams.locationList"
                :key="item.regionId"
                :label="item.regionName"
                :value="item.regionId">
                &nbsp;&nbsp; {{ item.regionName }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-if="bucketParams.showStorageClass" :label="$t('oss.storage_class')" :rules="{required: true, message: $t('oss.storage_class') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="form.storageClass" :placeholder="$t('oss.storage_class')">
              <el-option
                v-for="item in bucketParams.storageList"
                :key="item.value"
                :label="item.key"
                :value="item.value">
                &nbsp;&nbsp; {{ item.key }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-if="bucketParams.showCannedAcl" :label="$t('oss.read_acl')" :rules="{required: true, message: $t('oss.read_acl') + $t('commons.cannot_be_empty'), trigger: 'change'}">
            <el-select style="width: 100%;" v-model="form.cannedAcl" :placeholder="$t('oss.read_acl')">
              <el-option
                v-for="item in bucketParams.cannedAclList"
                :key="item.value"
                :label="item.key"
                :value="item.value">
                &nbsp;&nbsp; {{ item.key }}
              </el-option>
            </el-select>
          </el-form-item>
          <div style="color: red;font-style:oblique;margin: 10px 0 10px 100px;">
            <div>{{ $t('oss.bucket_tips6') }}</div>
            <div>{{ $t('oss.bucket_tips2') }}</div>
            <div>{{ $t('oss.bucket_tips7') }}</div>
            <div>{{ $t('oss.bucket_tips8') }}</div>
          </div>
        </el-form>
        <dialog-footer
          @cancel="visible = false"
          @confirm="createBucket()"/>
      </div>
    </el-drawer>
    <!--create oss bucket-->

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
import {OSS_BUCKET_CONFIGS} from "@/business/components/common/components/search/search-components";
import {saveAs} from "@/common/js/FileSaver";
import Upload from "@/business/components/oss/head/Upload";
import HideTable from "@/business/components/common/hideTable/HideTable";

const columnOptions = [
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
  name: "Result",
  components: {
    TableOperators,
    MainContainer,
    Container,
    TableHeader,
    TablePagination,
    DialogFooter,
    Upload,
    HideTable,
  },
  data() {
    return {
      result: {},
      ossResult: {},
      condition: {
        components: OSS_BUCKET_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      path: '/',
      thisObject: {},
      bucketVisible: false,
      visible: false,
      direction: 'rtl',
      objectData: [],
      form: {},
      ossTitle: this.$t('oss.create_bucket'),
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
      accounts: [],
      bucketParams: [],
      locationList: [],
      storageList: [],
      cannedAclList: [],
      showLocation: false,
      showCannedAcl: false,
      showStorageClass: false,
      bucketButtons: [
        {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      selectIds: new Set(),
      selectObjectIds: new Set(),
      objectButtons1: [
        {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.dirDelete
        }
      ],
      objectButtons2: [
        {
          tip: this.$t('server.download'), icon: "el-icon-download", type: "success",
          exec: this.objectDownload
        },
        {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.objectDelete
        }
      ],
      bucketOss: {},
      innerDrawer1: false,
      innerDrawer2: false,
      objectFile: Object,
      uploadForm: {},
      dirForm: {},
      checkedColumnNames: columnOptions.map((ele) => ele.props),
      columnNames: columnOptions,
      //名称搜索
      items: [
        {
          name: 'oss.name',
          id: 'name'
        },
        {
          name: 'oss.bucket',
          id: 'bucketName',
        }
      ],
      checkAll: true,
      isIndeterminate: false,
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
    select(selection) {
      this.selectIds.clear();
      selection.forEach(s => {
        this.selectIds.add(s.id)
      });
    },
    selectObjects(selection) {
      this.selectObjectIds.clear();
      selection.forEach(s => {
        this.selectObjectIds.add(s.id)
      });
    },
    //查询列表
    search() {
      let url = "/oss/bucketList/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleClose() {
      this.visible =  false;
      this.bucketVisible = false;
    },
    sort(column) {
      _sort(column, this.condition);
      this.init();
    },
    filter(filters) {
      _filter(filters, this.condition);
      this.init();
    },
    init() {
      this.selectIds.clear();
      this.selectObjectIds.clear();
      this.search();
      this.activeAccount();
    },
    showObject(bucket) {
      this.path = '/';
      this.bucketOss = bucket;
      this.result = this.$get("/oss/objects/" + bucket.id, response => {
        this.objectData = response.data;
        this.bucketVisible = true;
      });
    },
    getObjects(path) {
      if (path !== '' && path !== 'none') {
        this.path = path;
        this.result = this.$post("/oss/objects/" + this.thisObject.bucketId, { "path" : path=="/"?"":path}, response => {
          this.objectData = response.data;
          this.bucketVisible = true;
        });
      }
    },
    backObject(item) {
      if (this.path === '/') {
        this.showObject(item);
      } else {
        this.selectObject(item);
      }
    },
    selectObject(item) {
      this.thisObject = item;
      this.getObjects(item.id);
    },
    create() {
      this.form = {};
      this.bucketParams = {};
      this.ossTitle = this.$t('oss.create_bucket');
      this.visible = true;
    },
    //查询对象存储账号
    activeAccount() {
      let url = "/oss/allList";
      this.result = this.$get(url, response => {
        let data = response.data;
        this.accounts =  data;
      });
    },
    //选择插件查询对象存储账号信息
    changeAccount (ossId){
      this.$get("/oss/support/bucketAddforOssId/" + ossId,response => {
        this.bucketParams = response.data;
      });
    },
    createBucket() {
      this.result = this.$post("/oss/create", this.form, response => {
        if (response.success) {
          this.$success(this.$t('commons.create_success'));
          this.search();
          this.handleClose();
        } else {
          this.$error(response.message);
        }
      });
    },
    getBucketLocation (ossId) {
      this.locationList = [];
      this.$get('bucket/support/regions/' + ossId, response => {
        this.locationList = response.data;
      });
    },
    getStorageList (ossId) {
      this.storageList = [];
      this.$get('bucket/support/regions/' + ossId + '/params/storageClass', response => {
        this.storageList = response.data;
      });
    },
    getCannedACL (ossId) {
      this.cannedAclList = [];
      this.$get('bucket/support/' + ossId + '/params/cannedACL', response => {
        this.cannedAclList = response.data;
      });
    },
    change(e) {
      this.$forceUpdate();
    },
    handleDelete(item) {
      this.$alert(this.$t('commons.delete') + this.$t('oss.bucket') + item.bucketName + " ？", this.$t('commons.delete') + this.$t('oss.bucket'), {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.ossResult = this.$get("/oss/deleteBucket/" + item.id, response => {
              if (response.success) {
                this.$success(this.$t('commons.delete_success'));
                this.refresh();
              } else {
                this.$error(response.message);
              }
            });
          }
        }
      });
    },
    deleteSelect() {
      if (this.selectIds.size === 0) {
        this.$warning(this.$t('oss.please_choose_bucket'));
        return;
      }
      this.$alert(this.$t('oss.delete_batch') + this.$t('oss.bucket') + " ？", this.$t('commons.delete') + this.$t('oss.bucket'), {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.ossResult = this.$post("/oss/deleteByBatch", this.selectIds, response => {
              if (response.success) {
                this.$success(this.$t('commons.delete_success'));
                this.refresh();
              } else {
                this.$error(response.message);
              }
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
            this.ossResult = this.$download("/oss/downloadObject/" + item.bucketId, {
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
    dirDelete(item) {
      this.$alert(this.$t('commons.delete') + this.$t('oss.object_dir') + item.objectName + " ？", this.$t('commons.delete') + this.$t('oss.object_dir'), {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.ossResult = this.$post("/oss/deleteObject/" + item.bucketId, {objectId: item.id},response => {
              if (response.success) {
                this.$success(this.$t('commons.delete_success'));
                this.refresh();
              } else {
                this.$error(response.message);
              }
            });
          }
        }
      });
    },
    objectDelete(item) {
      this.$alert(this.$t('commons.delete') + this.$t('oss.object_file') + item.objectName + " ？", this.$t('commons.delete') + this.$t('oss.object_file'), {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.ossResult = this.$post("/oss/deleteObject/" + item.bucketId, {objectId: item.id},response => {
              if (response.success) {
                this.$success(this.$t('commons.delete_success'));
                this.refresh();
              } else {
                this.$error(response.message);
              }
            });
          }
        }
      });
    },
    deleteSelects() {
      if (this.selectObjectIds.size === 0) {
        this.$warning(this.$t('oss.please_choose_object'));
        return;
      }
      this.$alert(this.$t('oss.delete_batch') + this.$t('oss.object_file') + " ？", this.$t('commons.delete') + this.$t('oss.object_file'), {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.ossResult = this.$post("/oss/deleteObjects/" + this.bucketOss.id, Array.from(this.selectObjectIds), response => {
              if (response.success) {
                this.$success(this.$t('commons.delete_success'));
                this.refresh();
              } else {
                this.$error(response.message);
              }
            });
          }
        }
      });
    },
    upload() {
      this.innerDrawer1 = true;
    },
    uploadFile() {
      let formData = new FormData();
      if (this.objectFile) {
        formData.append("objectFile", this.objectFile);
      }
      formData.append("request", new Blob([JSON.stringify({path: this.path})], {type: "application/json"}));
      let axiosRequestConfig = {
        method: "POST",
        url: "/oss/uploadFile/" + this.bucketOss.id,
        data: formData,
        headers: {
          "Content-Type": 'multipart/form-data'
        }
      };
      this.ossResult = this.$request(axiosRequestConfig, (res) => {
        if (res.success) {
          this.$success(this.$t('commons.save_success'));
          this.innerDrawer1 = false;
          this.objectFile = null;
          this.uploadForm = {};
          this.refresh();
        }
      });
    },
    addDir() {
      this.innerDrawer2 = true;
    },
    submitDir() {
      this.ossResult = this.$post("/oss/createDir/" + this.bucketOss.id, {dir: this.path + this.dirForm.dir}, response => {
        if (response.success) {
          this.$success(this.$t('commons.save_success'));
          this.dirForm = {};
          this.innerDrawer2 = false;
          this.refresh();
        } else {
          this.$error(response.message);
        }
      });
    },
    innerDrawerClose() {
      this.innerDrawer1 = false;
      this.innerDrawer2 = false;
    },
    refresh() {
      if (this.path === '/') {
        this.showObject(this.bucketOss);
      } else {
        this.selectObject(this.thisObject);
      }
    },
    appendUpload(file) {
      this.objectFile = file;
    },
  },
  created() {
    this.init();
  },
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
  padding: 0 20px 20px 20px;
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
.dir >>> .el-input__inner {
  width: 90%;
}
.el-btn {
  margin: 0 0 10px 10px;
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

