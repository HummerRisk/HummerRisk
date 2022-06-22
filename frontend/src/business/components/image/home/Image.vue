<template>
  <div v-loading="result.loading">

    <el-card class="table-card">
      <template v-slot:header>
        <table-header :condition.sync="condition" @search="search" @create="create"
                      :create-tip="$t('image.create')" :title="$t('image.image_list')"/>
      </template>

      <el-card class="table-card el-row-card" :body-style="{ padding: '0' }" :key="index" v-for="(data, index) in tableData">
        <el-row style="margin: 2%;">
          <el-col :span="3">
            <el-image style="width: 100px; height: 100px;"
                      :src="data.pluginIcon==='docker.png'?require(`@/assets/img/platform/${data.pluginIcon}`):`${location}${data.pluginIcon}`"
                      :fit="'fill'">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </el-col>
          <el-col :span="1">
            <div class="split"></div>
          </el-col>
          <el-col :span="20">
            <el-row>
              <el-col :span="20" class="cl-ver-col">
                <el-row class="cl-mid-row">
                  <el-col :span="3" class="cl-span-col">{{ $t('image.image_name') }}</el-col>
                  <el-col :span="3" class="cl-span-col">{{ $t('image.image_status') }}</el-col>
                  <el-col :span="5" class="cl-span-col">{{ $t('image.image_url') }}</el-col>
                  <el-col :span="3" class="cl-span-col">{{ $t('image.image_size') }}</el-col>
                  <el-col :span="5" class="cl-span-col">{{ $t('image.image_repo_name') }}</el-col>
                  <el-col :span="5" class="cl-span-col">{{ $t('commons.update_time') }}</el-col>
                </el-row>
                <el-row class="cl-mid-row">
                  <el-col :span="3" class="cl-data-col">{{ data.name }}</el-col>
                  <el-col :span="3" class="cl-data-col">
                    <el-tag size="mini" type="warning" v-if="data.status === 'DELETE'">
                      {{ $t('server.DELETE') }}
                    </el-tag>
                    <el-tag size="mini" type="success" v-else-if="data.status === 'VALID'">
                      {{ $t('server.VALID') }}
                    </el-tag>
                    <el-tag size="mini" type="danger" v-else-if="data.status === 'INVALID'">
                      {{ $t('server.INVALID') }}
                    </el-tag>
                  </el-col>
                  <el-col :span="5" class="cl-data-col">
                    {{ data.type==='image'?(data.imageUrl + ':' + data.imageTag):data.path }}
                  </el-col>
                  <el-col :span="3" class="cl-data-col">{{ data.size }}</el-col>
                  <el-col :span="5" class="cl-data-col">{{ data.image_repo_name?data.image_repo_name:$t('image.no_image_repo') }}</el-col>
                  <el-col :span="5" class="cl-data-col">{{ data.updateTime | timestampFormatDate }}</el-col>
                </el-row>
              </el-col>
              <el-col :span="4" class="cl-ver-col">
                <el-row class="cl-btn-mid-row">
                  <el-col :span="8" class="cl-btn-col">
                    <el-button @click="handleScan(data)" circle icon="el-icon-s-promotion" size="mini">
                    </el-button>
                  </el-col>
                  <el-col :span="8" class="cl-btn-col">
                    <el-button @click="handleEdit(data)" circle icon="el-icon-edit" size="mini">
                    </el-button>
                  </el-col>
                  <el-col :span="8" class="cl-btn-col">
                    <el-button @click="handleDelete(data)" circle icon="el-icon-delete" size="mini" type="danger">
                    </el-button>
                  </el-col>
                </el-row>
                <el-row class="cl-btn-mid-row">
                  <el-col :span="8" class="cl-btn-data-col">
                    {{ $t('account.scan') }}
                  </el-col>
                  <el-col :span="8" class="cl-btn-data-col">
                    {{ $t('commons.edit') }}
                  </el-col>
                  <el-col :span="8" class="cl-btn-data-col">
                    {{ $t('commons.delete') }}
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </el-card>

      <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    </el-card>

    <!--Create image-->
    <!--Create image-->

  </div>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "../../common/components/TableHeader";
import TableOperators from "../../common/components/TableOperators";
import DialogFooter from "../../common/components/DialogFooter";
import TableOperatorButton from "../../common/components/TableOperatorButton";
import ImageStatus from "../head/ImageStatus";
import {_filter, _sort} from "@/common/js/utils";
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
  },
  data() {
    return {
      queryPath: '/image/imageList/',
      deletePath: '/image/deleteimage/',
      createPath: '/image/addimage/',
      updatePath: '/image/editimage/',
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
      condition: {},
      tableData: [],
      form: {},
      direction: 'rtl',
      rule: {
        name: [
          {required: true, message: this.$t('commons.input_name'), trigger: 'blur'},
          {min: 2, max: 50, message: this.$t('commons.input_limit', [2, 50]), trigger: 'blur'},
          {
            required: true,
            message: this.$t('workspace.special_characters_are_not_supported'),
            trigger: 'blur'
          }
        ],
      },
      buttons: [
        {
          tip: this.$t('account.scan'), icon: "el-icon-s-promotion", type: "",
          exec: this.handleScan
        }, {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
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
      location: "",
    }
  },
  created() {
    this.search();
    this.activeProxy();
    this.location = window.location.href.split("#")[0];
  },
  methods: {
    create() {
      this.form = {};
      this.createVisible = true;
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
    handleScan(item) {

    },
    handleEdit(row) {
      this.updateVisible = true;
      this.form = row;
    },
    search() {
      this.result = this.$post(this.buildPagePath(this.queryPath), this.condition, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleClose() {
      this.form = {};
      this.createVisible =  false;
      this.updateVisible =  false;
    },
    //查询代理
    activeProxy() {
      let url = "/proxy/list/all";
      this.result = this.$get(url, response => {
        this.proxys = response.data;
      });
    },
    buildPagePath(path) {
      return path + "/" + this.currentPage + "/" + this.pageSize;
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
      this.$alert(this.$t('image.delete_confirm') + obj.name + " ？", '', {
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
  padding: 0 20px 0 20px;
  margin: 2%;
}
.split {
  height: 120px;
  border-left: 1px solid #D8DBE1;
}
.cl-ver-col {
  vertical-align: middle;
  margin-top: 2%;
}
.cl-mid-row {
  margin: 1%;
}
.cl-btn-mid-row {
  margin: 2%;
}
.cl-span-col {
  margin: 1% 0;
}
.cl-btn-col {
  margin: 3% 0;
}
.cl-data-col {
  color: #888;
  font-size: 10px;
}
.cl-btn-data-col {
  color: #77aff9;
}
</style>
