<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <template v-slot:header>
        <table-header :condition.sync="condition"
                      @search="search"
                      :title="$t('server.certificate_list')"
                      @create="create"
                      :createTip="$t('server.create_certificate')"
                      :show-create="true"/>
      </template>

      <el-table border :data="tableData" class="adjust-table table-content" @sort-change="sort" :row-class-name="tableRowClassName"
                @filter-change="filter">
        <!-- 展开 start -->
        <el-table-column type="expand" min-width="1%">
          <template slot-scope="props">
            <el-form>
              <codemirror ref="cmEditor" v-if="props.row.public_key" v-model="props.row.public_key" class="code-mirror" :options="cmOptions" />
              <codemirror ref="cmEditor" v-else v-model="props.row.password" class="code-mirror" :options="cmOptions" />
            </el-form>
          </template>
        </el-table-column >
        <!-- 展开 end -->
        <el-table-column type="index" min-width="2%"/>
        <el-table-column prop="name" :label="$t('commons.name')" min-width="18%" show-overflow-tooltip></el-table-column>
        <el-table-column prop="description" :label="$t('commons.description')" min-width="20%" show-overflow-tooltip></el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('server.is_public_key')" min-width="10%" show-overflow-tooltip>
          <el-tag size="mini" type="danger" v-if="scope.row.isPublicKey === 'no'">
            {{ $t('rule.no_public_key') | $t('commons.password') }}
          </el-tag>
          <el-tag size="mini" type="warning" v-else-if="scope.row.isPublicKey === 'files'">
            {{ $t('rule.file_public_key') }}
          </el-tag>
          <el-tag size="mini" type="success" v-else-if="scope.row.isPublicKey === 'str'">
            {{ $t('rule.str_public_key') }}
          </el-tag>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('account.creator')" min-width="10%" show-overflow-tooltip>
          {{ scope.row.user }}
        </el-table-column>
        <el-table-column prop="lastModified" min-width="15%" :label="$t('package.last_modified')" sortable>
          <template v-slot:default="scope">
            <span>{{ scope.row.lastModified | timestampFormatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="10%" :label="$t('commons.operating')">
          <template v-slot:default="scope">
            <table-operators :buttons="buttons" :row="scope.row"/>
          </template>
        </el-table-column>
      </el-table>
      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create Certificate-->
    <el-drawer class="rtl" :title="$t('server.create_certificate')" :visible.sync="createVisible" size="70%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="createForm" label-position="right" label-width="120px" size="small" :rules="rule" ref="createForm">
        <el-form-item :label="$t('commons.name')" prop="name">
          <el-input v-model="createForm.name" autocomplete="off" :placeholder="$t('commons.name')"/>
        </el-form-item>
        <el-form-item :label="$t('commons.description')" prop="description">
          <el-input v-model="createForm.description" autocomplete="off" :placeholder="$t('commons.description')"/>
        </el-form-item>
        <el-form-item :label="$t('server.is_public_key')" ref="type" prop="type" :rules="{required: true, message: $t('server.is_public_key') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-radio v-model="createForm.isPublicKey" label="no">{{ $t('server.no_public_key') }}</el-radio>
          <el-radio v-model="createForm.isPublicKey" label="str">{{ $t('server.str_public_key') }}</el-radio>
          <el-radio v-model="createForm.isPublicKey" label="file">{{ $t('server.file_public_key') }}</el-radio>
        </el-form-item>
        <el-form-item v-if="createForm.isPublicKey === 'no'" :label="$t('commons.password')" ref="password">
          <el-input type="password" v-model="createForm.password" autocomplete="off" :placeholder="$t('commons.password')" show-password/>
        </el-form-item>
        <el-form-item v-if="createForm.isPublicKey === 'str'" :label="$t('server.public_key')" ref="password">
          <el-input type="textarea" :rows="10" v-model="createForm.publicKey" autocomplete="off" :placeholder="$t('server.public_key')"/>
        </el-form-item>
        <el-form-item v-if="createForm.isPublicKey === 'file'" :label="$t('server.public_key')" ref="password">
          <server-key-upload v-on:appendTar="append" v-model="createForm.publicKeyPath" :param="createForm.publicKeyPath"/>
        </el-form-item>
<!--        <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">-->
<!--          <el-switch v-model="form.isProxy"></el-switch>-->
<!--        </el-form-item>-->
<!--        <el-form-item v-if="form.isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">-->
<!--          <el-select style="width: 100%;" filterable :clearable="true" v-model="form.proxyId" :placeholder="$t('commons.proxy')">-->
<!--            <el-option-->
<!--              v-for="item in proxys"-->
<!--              :key="item.id"-->
<!--              :label="item.proxyIp"-->
<!--              :value="item.id">-->
<!--              &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}-->
<!--            </el-option>-->
<!--          </el-select>-->
<!--        </el-form-item>-->
      </el-form>
      <dialog-footer
        @cancel="createVisible = false"
        @confirm="saveCertificate(createForm, 'createForm')"/>
    </el-drawer>
    <!--Create Certificate-->

    <!--Update Certificate-->
    <el-drawer class="rtl" :title="$t('server.update_certificate')" :visible.sync="updateVisible" size="70%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="updateForm" label-position="right" label-width="120px" size="small" :rules="rule" ref="updateForm">
        <el-form-item :label="$t('commons.name')" prop="name">
          <el-input v-model="updateForm.name" autocomplete="off" :placeholder="$t('commons.name')"/>
        </el-form-item>
        <el-form-item :label="$t('commons.description')" prop="description">
          <el-input v-model="updateForm.description" autocomplete="off" :placeholder="$t('commons.description')"/>
        </el-form-item>
        <el-form-item :label="$t('server.is_public_key')" ref="type" prop="type" :rules="{required: true, message: $t('server.is_public_key') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-radio v-model="updateForm.isPublicKey" label="no">{{ $t('server.no_public_key') }}</el-radio>
          <el-radio v-model="updateForm.isPublicKey" label="str">{{ $t('server.str_public_key') }}</el-radio>
          <el-radio v-model="updateForm.isPublicKey" label="file">{{ $t('server.file_public_key') }}</el-radio>
        </el-form-item>
        <el-form-item v-if="updateForm.isPublicKey === 'no'" :label="$t('commons.password')" ref="password">
          <el-input type="password" v-model="updateForm.password" autocomplete="off" :placeholder="$t('commons.password')" show-password/>
        </el-form-item>
        <el-form-item v-if="updateForm.isPublicKey === 'str'" :label="$t('server.public_key')" ref="password">
          <el-input type="textarea" :rows="10" v-model="updateForm.publicKey" autocomplete="off" :placeholder="$t('server.public_key')"/>
        </el-form-item>
        <el-form-item v-if="updateForm.isPublicKey === 'file'" :label="$t('server.public_key')" ref="password">
          <server-key-upload v-on:appendTar="append" v-model="updateForm.publicKeyPath" :param="updateForm.publicKeyPath"/>
        </el-form-item>
      </el-form>
      <dialog-footer
        @cancel="updateVisible = false"
        @confirm="saveCertificate(updateForm, 'updateForm')"/>
    </el-drawer>
    <!--Update Certificate-->

  </main-container>
</template>

<script>
import TableOperators from "../../common/components/TableOperators";
import MainContainer from "../../common/components/MainContainer";
import Container from "../../common/components/Container";
import TableHeader from "@/business/components/common/components/TableHeader";
import TablePagination from "../../common/pagination/TablePagination";
import TableOperator from "../../common/components/TableOperator";
import DialogFooter from "../head/DialogFooter";
import {_filter, _sort} from "@/common/js/utils";
import {SERVER_CERTIFICATE_CONFIGS} from "../../common/components/search/search-components";
import ServerKeyUpload from "@/business/components/server/head/ServerKeyUpload";

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
    ServerKeyUpload,
  },
  data() {
    return {
      result: {},
      condition: {
        components: SERVER_CERTIFICATE_CONFIGS
      },
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      createForm: {},
      updateForm: {},
      createVisible: false,
      updateVisible: false,
      severityOptions: [],
      direction: 'rtl',
      rule: {
        name: [
          {required: true, message: this.$t('rule.input_name'), trigger: 'blur'},
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
          {
            required: true,
            message: this.$t('rule.special_characters_are_not_supported'),
            trigger: 'blur'
          }
        ],
        description: [
          {required: true, message: this.$t('rule.input_description'), trigger: 'blur'},
          {min: 2, max: 100, message: this.$t('commons.input_limit', [2, 100]), trigger: 'blur'},
          {
            required: true,
            message: this.$t('rule.special_characters_are_not_supported'),
            trigger: 'blur'
          }
        ]
      },
      buttons: [
        {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        },
        {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
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
      keyFile: Object,
    }
  },

  watch: {
    '$route': 'init'
  },

  methods: {
    create() {
      this.createForm = { isPublicKey: 'no' };
      this.createVisible = true;
    },
    handleEdit(item) {
      this.updateForm = Object.assign({}, item);
      this.updateVisible = true;
    },
    handleClose() {
      this.createVisible =  false;
      this.updateVisible =  false;
    },
    handleDelete(item) {
      this.$alert(this.$t('account.delete_confirm') + item.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/server/deleteCertificate/" + item.id, () => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    //查询列表
    search() {
      let url = "/server/certificateList/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    init() {
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
    tableRowClassName({row, rowIndex}) {
      if (rowIndex%4 === 0) {
        return 'success-row';
      } else if (rowIndex%2 === 0) {
        return 'warning-row';
      } else {
        return '';
      }
    },
    append(file) {
      this.keyFile = file;
    },
    saveCertificate(form, type) {
      this.$refs[type].validate(valid => {
        if (valid) {
          if(type === 'createForm') {
            if (!form.isProxy) {
              form.proxyId = null;
            }
            let formData = new FormData();
            if (this.keyFile) {
              formData.append("keyFile", this.keyFile);
            }
            formData.append("request", new Blob([JSON.stringify(form)], {type: "application/json"}));
            let axiosRequestConfig = {
              method: "POST",
              url: "/server/addCertificate",
              data: formData,
              headers: {
                "Content-Type": 'multipart/form-data'
              }
            };
            this.result = this.$request(axiosRequestConfig, (res) => {
              if (res.success) {
                this.$success(this.$t('commons.save_success'));
                this.search();
                this.createVisible = false;
              }
            });
          } else {
            if (!form.isProxy) {
              form.proxyId = null;
            }
            let formData = new FormData();
            if (this.keyFile) {
              formData.append("keyFile", this.keyFile);
            }
            formData.append("request", new Blob([JSON.stringify(form)], {type: "application/json"}));
            let axiosRequestConfig = {
              method: "POST",
              url: "/server/editCertificate",
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
          }
        }
      });
    },
  },
  computed: {
    codemirror() {
      return this.$refs.cmEditor.codemirror
    }
  },
  created() {
    this.init();
  }

}
</script>

<style scoped>
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
/deep/ :focus{outline:0;}
</style>
