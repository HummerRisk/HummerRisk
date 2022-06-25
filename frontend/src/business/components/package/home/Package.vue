<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search"
                      :title="$t('package.package_setting_list')"
                      @create="create" :createTip="$t('package.create')"
                      :show-create="true"/>

      </template>
      <el-row :gutter="20" class="el-row-body">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6" v-for="(data, index) in tableData"
                :key="index" class="el-col el-col-su" style="margin: 10px 0 5px 0;">
          <el-card :body-style="{ padding: '0' }">
            <el-image style="width: 100%; height: 220px;"
                      :src="data.pluginIcon==='package.png'?require(`@/assets/img/platform/${data.pluginIcon}`):`${location}${data.pluginIcon}`"
                      :fit="'fill'">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <div style="padding: 14px;">
              <span>{{ data.name }}</span>
              <el-button size="medium" type="primary" class="round" round>{{ data.size }}</el-button>
              <span class="button time">
                <span v-bind:class="{true: 'color-red', false: ''}[!data.packageName]">{{ data.packageName?data.packageName:'No package' }}</span>
              </span>
              <div class="bottom clearfix">
                <time class="time">{{ data.updateTime | timestampFormatDate }} | {{ data.userName }}{{ $t('dashboard.i18n_create') }}</time>
                <el-dropdown class="button" @command="(command)=>{handleCommand(command, data)}">
                  <span class="el-dropdown-link">
                    {{ $t('package.operate') }}
                    <i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="scan">{{ $t('package.scan') }}</el-dropdown-item>
                    <el-dropdown-item command="edit">{{ $t('package.edit') }}</el-dropdown-item>
                    <el-dropdown-item command="delete">{{ $t('package.delete') }}</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create package-->
    <el-drawer class="rtl" :title="$t('package.create')" :visible.sync="createVisible" size="50%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="addPackageForm" label-position="right" label-width="150px" size="small" :rules="rule" ref="addForm">
        <el-steps :active="active" finish-status="success">
          <el-step :title="$t('package.package_name')" icon="el-icon-edit"></el-step>
          <el-step :title="$t('package.package_icon')" icon="el-icon-picture"></el-step>
          <el-step :title="$t('package.package')" icon="el-icon-upload"></el-step>
        </el-steps>
        <div v-if="active == 1">
            <span>
                <h1>{{ $t('package.package_name') }}</h1>
            </span>
          <div class="app">
            <el-form-item :label="$t('package.name')" ref="name" prop="name">
              <el-input v-model="addPackageForm.name" autocomplete="off" :placeholder="$t('package.name')"/>
            </el-form-item>
            <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-switch v-model="addPackageForm.isProxy"></el-switch>
            </el-form-item>
            <el-form-item v-if="addPackageForm.isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-select style="width: 100%;" v-model="addPackageForm.proxyId" :placeholder="$t('commons.proxy')">
                <el-option
                  v-for="item in proxys"
                  :key="item.id"
                  :label="item.proxyIp"
                  :value="item.id">
                  &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}
                </el-option>
              </el-select>
            </el-form-item>
          </div>
        </div>
        <div v-if="active == 2">
          <div class="app">
            <el-form-item :label="$t('package.package_icon')" ref="pluginIcon" prop="pluginIcon">
              <image-upload url="/package/uploadImg" :param="addPackageForm"/>
            </el-form-item>
          </div>
        </div>
        <div v-if="active == 3">
            <span>
                <h1>{{ $t('package.package') }}</h1>
            </span>
          <div class="app">
            <div class="app">
              <el-form-item :label="$t('package.package')" ref="package" prop="package">
                <file-upload url="/package/uploadPackage" :param="addPackageForm"/>
              </el-form-item>
            </div>
          </div>
        </div>
      </el-form>
      <dialog-footer
        @cancel="createVisible = false"
        :show-pre="preVisible" @preStep="pre()"
        :show-next="nextVisible" @nextStep="next('add')"
        :show-confirm="confirmVisible" @confirm="handleClose"/>
    </el-drawer>
    <!--Create package-->

    <!--Update package-->
    <el-drawer class="rtl" :title="$t('package.update')" :visible.sync="updateVisible" size="50%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="editPackageForm" label-position="right" label-width="150px" size="small" :rules="rule" ref="editForm">
        <el-steps :active="active" finish-status="success">
          <el-step :title="$t('package.package_name')" icon="el-icon-edit"></el-step>
          <el-step :title="$t('package.package_icon')" icon="el-icon-picture"></el-step>
          <el-step :title="$t('package.package')" icon="el-icon-upload"></el-step>
        </el-steps>
        <div v-if="active == 1">
          <span>
            <h1>{{ $t('package.package_name') }}</h1>
          </span>
          <div class="app">
            <el-form-item :label="$t('package.name')" ref="name" prop="name">
              <el-input v-model="editPackageForm.name" autocomplete="off" :placeholder="$t('package.name')"/>
            </el-form-item>
            <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-switch v-model="editPackageForm.isProxy"></el-switch>
            </el-form-item>
            <el-form-item v-if="editPackageForm.isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-select style="width: 100%;" v-model="editPackageForm.proxyId" :placeholder="$t('commons.proxy')">
                <el-option
                  v-for="item in proxys"
                  :key="item.id"
                  :label="item.proxyIp"
                  :value="item.id">
                  &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}
                </el-option>
              </el-select>
            </el-form-item>
          </div>
        </div>
        <div v-if="active == 2">
          <div class="app">
            <el-form-item :label="$t('package.package_icon')" ref="pluginIcon" prop="pluginIcon">
              <image-upload url="/package/uploadImg" :param="editPackageForm"/>
            </el-form-item>
          </div>
        </div>
        <div v-if="active == 3">
          <span>
            <h1>{{ $t('package.package') }}</h1>
          </span>
          <div class="app">
            <div class="app">
              <el-form-item :label="$t('package.package')" ref="package" prop="package">
                <file-upload url="/package/uploadPackage" :param="editPackageForm"/>
              </el-form-item>
            </div>
          </div>
        </div>
      </el-form>
      <dialog-footer
        @cancel="updateVisible = false"
        :show-pre="preVisible" @preStep="pre()"
        :show-next="nextVisible" @nextStep="next('edit')"
        :show-confirm="confirmVisible" @confirm="save(editPackageForm, 'edit')"/>
    </el-drawer>
    <!--Update package-->

  </main-container>
</template>

<script>
import MainContainer from "../../common/components/MainContainer";
import TableHeader from "../head/TableHeader";
import {PACKAGE_CONFIGS} from "@/business/components/common/components/search/search-components";
import {_filter, _sort} from "@/common/js/utils";
import TablePagination from "../../common/pagination/TablePagination";
import DialogFooter from "../head/DialogFooter";
import ImageUpload from "@/business/components/common/components/ImageUpload/index";
import FileUpload from "@/business/components/common/components/FileUpload/index";

/* eslint-disable */
export default {
  name: "Package",
  filters: {
    btnTextFilter(val) {
      return val ? 'Pause' : 'Continue';
    }
  },
  components: {
    MainContainer,
    TableHeader,
    TablePagination,
    DialogFooter,
    ImageUpload,
    FileUpload,
  },
  data() {
    return {
      result: {},
      condition: {
        components: PACKAGE_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 20,
      total: 0,
      loading: false,
      createVisible: false,
      updateVisible: false,
      addPackageForm: {},
      editPackageForm: {},
      direction: 'rtl',
      rule: {
        name: [
          {required: true, message: this.$t('commons.input_name'), trigger: 'blur'},
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
          {
            required: true,
            message: this.$t("workspace.special_characters_are_not_supported"),
            trigger: 'blur'
          }
        ],
      },
      percent: 0,
      pluginIcon: '',
      upload: true,
      percentCount: 0,
      active: 1,
      preVisible: false,
      nextVisible: true,
      confirmVisible: false,
      location: "",
      proxys: [],
    }
  },
  methods: {
    create() {
      this.createVisible = true;
      this.active = 1;
      this.preVisible = false;
      this.confirmVisible = false;
      this.nextVisible = true;
      this.addPackageForm = {};
    },
    save(item, type) {
      if(type==='add') {
        if(!this.id) {
          this.result = this.$post('/package/addPackage', this.addPackageForm, response => {
            let data = response.data;
            this.addPackageForm = data;
          });
        }
      }else {
        this.result = this.$post('/package/editPackage', this.editPackageForm, response => {
          let data = response.data;
          this.editPackageForm = data;
          this.handleClose();
        });
      }
    },
    //查询代理
    activeProxy() {
      let url = "/proxy/list/all";
      this.result = this.$get(url, response => {
        this.proxys = response.data;
      });
    },
    handleCommand(command, data) {
      switch (command) {
        case "scan":
          this.scan(data);
          break;
        case "edit":
          this.updateVisible = true;
          this.active = 1;
          this.preVisible = false;
          this.confirmVisible = false;
          this.nextVisible = true;
          this.editPackageForm = data;
          this.id = data.id;
          break;
        case "delete":
          this.delete(data);
          break;
        default:
          break;
      }
    },
    scan(data) {
      if(!data.path) {
        this.$warning(this.$t('package.no_package'));
        return;
      }
      this.$alert(this.$t('package.one_scan') + this.$t('package.package_rule') + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.$get('/package/scan/' + data.id, response => {
              if (response.success) {
                this.$success(this.$t('schedule.event_success'));
              } else {
                this.$error(this.$t('schedule.event_failed'));
              }
            });
            this.$router.push({
              path: '/package/result',
            }).catch(error => error);
          }
        }
      });
    },
    delete(data) {
      this.$get('/package/deletePackage/' + data.id, () => {
        this.search();
      });
    },
    //查询列表
    search() {
      let url = "/package/packageList/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
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
    handleClose() {
      this.createVisible =  false;
      this.updateVisible =  false;
      this.search();
    },
    next(type) {
      if (this.active === 1) {
        if(type==='add') {
          if (!this.addPackageForm.name) {
            this.$error(this.$t('package.name_not_null'));
            return;
          }
          this.save(this.addPackageForm, 'add');
        }else{
          if (!this.editPackageForm.name) {
            this.$error(this.$t('package.name_not_null'));
            return;
          }
        }
        this.active = 2;
      } else if (this.active === 2) {
        this.active = 3;
        this.confirmVisible = true;
        this.nextVisible = false;
      }
      this.preVisible = true;
    },
    pre() {
      if (this.active === 3) {
        this.active = 2;
        this.confirmVisible = false;
        this.nextVisible = true;
      } else if (this.active === 2) {
        this.active = 1;
        this.preVisible = false;
      }
    },
},
  created() {
    this.search();
    this.activeProxy();
    this.location = window.location.href.split("#")[0];
  }
}
</script>

<style scoped>
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
  line-height: 12px;
}

.button {
  padding: 0;
  float: right;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}
.el-icon-arrow-down {
  font-size: 12px;
}

.image {
  width: 100%;
  display: block;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.image-slot {
  width: 100%;
  height: 230px;
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

.el-col-su >>> .el-card {
  margin: 10px;
}
</style>

