<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <el-row :gutter="20">
        <el-col :span="6" style="max-height: 468px;padding-right: 0;">
          <el-card class="box-card" style="max-height: 489px;">
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
                      {{index+1}} <i class="header-icon el-icon-folder-opened"></i>  {{application.name}}
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
        <el-col :span="18" style="max-height: 468px;padding-left: 0;">
          <main-container class="main-content-box">
            <el-card class="table-card">
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
              <el-table border v-if="tag=='code'" :data="codeData" class="adjust-table table-content" stripe style="min-height: 317px;" max-height="318">
                <el-table-column type="index" min-width="2%"/>
                <el-table-column prop="name" :label="$t('code.name')" min-width="10%" show-overflow-tooltip>
                  <template v-slot:default="scope">
                    <span>
                      <img :src="require(`@/assets/img/code/${scope.row.pluginIcon}`)" style="width: 30px; height: 25px; vertical-align:middle" alt=""/>
                       &nbsp;&nbsp; {{ $t(scope.row.name) }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="6%">
                  <el-tooltip effect="dark" :content="$t('history.result')" placement="top">
                    <el-link type="primary" class="text-click" @click="goResource(scope.row)">{{ scope.row.returnSum }}</el-link>
                  </el-tooltip>
                </el-table-column>
                <el-table-column v-slot:default="scope" :label="$t('image.result_status')" min-width="12%" prop="resultStatus" sortable show-overflow-tooltip>
                  <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                  </el-button>
                  <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                  </el-button>
                  <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
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
                </el-table-column>
                <el-table-column prop="updateTime" min-width="15%" :label="$t('image.last_modified')" sortable>
                  <template v-slot:default="scope">
                    <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
                  </template>
                </el-table-column>
                <el-table-column min-width="12%" :label="$t('commons.operating')">
                  <download/>
                </el-table-column>
              </el-table>
              <el-table border v-if="tag=='image'" :data="imageData" class="adjust-table table-content" stripe style="min-height: 317px;" max-height="318">
                <el-table-column type="index" min-width="2%"/>
                <el-table-column prop="name" :label="$t('image.image_name')" min-width="10%" show-overflow-tooltip>
                  <template v-slot:default="scope">
                    <span>
                      <img :src="require(`@/assets/img/platform/docker.png`)" style="width: 30px; height: 25px; vertical-align:middle" alt=""/>
                       &nbsp;&nbsp; {{ $t(scope.row.name) }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column v-slot:default="scope" :label="$t('image.image_url')" min-width="20%" show-overflow-tooltip>
                  <el-row v-if="scope.row.type==='image'">{{ scope.row.imageUrl }}:{{ scope.row.imageTag }}</el-row>
                  <el-row v-if="scope.row.type==='tar'">{{ scope.row.path }}</el-row>
                </el-table-column>
                <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="7%">
                  <el-tooltip effect="dark" :content="$t('history.result')" placement="top">
                    <el-link type="primary" class="text-click" @click="goResource(scope.row)">{{ scope.row.returnSum }}</el-link>
                  </el-tooltip>
                </el-table-column>
                <el-table-column v-slot:default="scope" :label="$t('image.result_status')" min-width="12%" prop="resultStatus" sortable show-overflow-tooltip>
                  <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                  </el-button>
                  <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                  </el-button>
                  <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
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
                </el-table-column>
                <el-table-column prop="updateTime" min-width="14%" :label="$t('image.last_modified')" sortable>
                  <template v-slot:default="scope">
                    <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
                  </template>
                </el-table-column>
                <el-table-column min-width="11%" :label="$t('commons.operating')">
                  <download/>
                </el-table-column>
              </el-table>
              <el-table border v-if="tag=='package'" :data="packageData" class="adjust-table table-content" stripe style="min-height: 317px;" max-height="318">
                <el-table-column type="index" min-width="2%"/>
                <el-table-column prop="name" :label="$t('package.name')" min-width="10%" show-overflow-tooltip></el-table-column>
                <el-table-column prop="packageName" :label="$t('package.package_name')" min-width="10%" show-overflow-tooltip></el-table-column>
                <el-table-column v-slot:default="scope" :label="$t('resource.i18n_not_compliance')" prop="returnSum" sortable show-overflow-tooltip min-width="6%">
                  <el-tooltip effect="dark" :content="$t('history.result')" placement="top">
                    <el-link type="primary" class="text-click" @click="goResource(scope.row)">{{ scope.row.returnSum }}</el-link>
                  </el-tooltip>
                </el-table-column>
                <el-table-column v-slot:default="scope" :label="$t('image.result_status')" min-width="12%" prop="resultStatus" sortable show-overflow-tooltip>
                  <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-if="scope.row.resultStatus === 'UNCHECKED'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                  </el-button>
                  <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'APPROVED'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
                  </el-button>
                  <el-button @click="showResultLog(scope.row)" plain size="medium" type="primary" v-else-if="scope.row.resultStatus === 'PROCESSING'">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}...
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
                </el-table-column>
                <el-table-column prop="updateTime" min-width="15%" :label="$t('image.last_modified')" sortable>
                  <template v-slot:default="scope">
                    <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
                  </template>
                </el-table-column>
                <el-table-column min-width="12%" :label="$t('commons.operating')">
                  <download/>
                </el-table-column>
              </el-table>
            </el-card>
          </main-container>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
<!--          <task-order :taskOrder="taskOrder"/>-->
        </el-col>
      </el-row>
    </el-card>
  </main-container>
</template>

<script>
import MainContainer from "@/business/components/common/components/MainContainer";
import Download from "@/business/components/sbom/home/Download";

/* eslint-disable */
export default {
  components: {
    MainContainer,
    Download,
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
        {id: 'package', name: this.$t('sbom.target_package'), icon: 'iconfont icon-ruanjiankaifabao'},
      ],
      tag: 'code',
      codeData: [],
      imageData: [],
      packageData: [],
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
              return;
            }
            return;
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
      console.log(this.sbomVersion);
      if(this.sbomVersion) {
        this.$get("/sbom/historyCodeResult/" + this.sbomVersion.id, response => {
          this.codeData = response.data;
        });
        this.$get("/sbom/historyImageTask/" + this.sbomVersion.id, response => {
          this.imageData = response.data;
        });
        this.$get("/sbom/historyPackageTask/" + this.sbomVersion.id, response => {
          this.packageData = response.data;
        });
      }
    },
    handleClick(item) {
      console.log(1212,item);
    },
    selectVersion(item) {
      this.sbomVersion = item;
      this.searchScan();
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
</style>

