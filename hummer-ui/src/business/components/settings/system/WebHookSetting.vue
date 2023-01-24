<template>
  <div v-loading="result.loading">
    <el-table :border="true" :stripe="true" :data="tableData" class="adjust-table table-content">
      <el-table-column type="index" min-width="40"/>
      <el-table-column prop="name" :label="$t('system_parameter_setting.webhook.name')" min-width="130"></el-table-column>
      <el-table-column prop="webhook" :label="$t('system_parameter_setting.webhook.webhook')" min-width="250"></el-table-column>
      <el-table-column :label="$t('rule.status')" width="80" show-overflow-tooltip>
        <template v-slot:default="scope">
          <el-switch @change="changeStatus(scope.row)" v-model="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column min-width="160" :label="$t('commons.create_time')" sortable
                       prop="createTime">
        <template v-slot:default="scope">
          <span>{{ scope.row.createTime | timestampFormatDate }}</span>
        </template>
      </el-table-column>
      <el-table-column min-width="160" :label="$t('commons.update_time')" sortable
                       prop="updateTime">
        <template v-slot:default="scope">
          <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
        </template>
      </el-table-column>
      <el-table-column min-width="100" :label="$t('commons.operating')" fixed="right">
        <template v-slot:default="scope">
          <table-operators :buttons="buttons" :row="scope.row"/>
        </template>
      </el-table-column>
    </el-table>
    <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
    <div>
      <el-button type="primary" @click="create()" size="small">
        {{ $t('system_parameter_setting.webhook.create') }}
      </el-button>
    </div>

    <!--Create webhook-->
    <el-drawer class="rtl" :title="$t('system_parameter_setting.webhook.create')" :visible.sync="createVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="medium" :rules="rule" ref="form">
        <el-form-item :label="$t('system_parameter_setting.webhook.name')" ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('system_parameter_setting.webhook.name')"/>
        </el-form-item>
        <el-form-item :label="$t('system_parameter_setting.webhook.webhook')" ref="webhook" prop="webhook">
          <el-input type="textarea" :rows="5" v-model="form.webhook" autocomplete="off" :placeholder="$t('system_parameter_setting.webhook.webhook')"/>
        </el-form-item>
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
        <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-switch v-model="form.isProxy"></el-switch>
        </el-form-item>
      </el-form>
      <dialog-footer
        @cancel="createVisible = false"
        @confirm="save(form, 'add')"/>
    </el-drawer>
    <!--Create webhook-->

    <!--Update webhook-->
    <el-drawer class="rtl" :title="$t('system_parameter_setting.webhook.update')" :visible.sync="updateVisible" size="60%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-form :model="form" label-position="right" label-width="150px" size="medium" :rules="rule" ref="form">
        <el-form-item :label="$t('system_parameter_setting.webhook.name')" ref="name" prop="name">
          <el-input v-model="form.name" autocomplete="off" :placeholder="$t('system_parameter_setting.webhook.name')"/>
        </el-form-item>
        <el-form-item :label="$t('system_parameter_setting.webhook.webhook')" ref="webhook" prop="webhook">
          <el-input type="textarea" :rows="5" v-model="form.webhook" autocomplete="off" :placeholder="$t('system_parameter_setting.webhook.webhook')"/>
        </el-form-item>
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
        <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">
          <el-switch v-model="form.isProxy"></el-switch>
        </el-form-item>
      </el-form>
      <dialog-footer
        @cancel="updateVisible = false"
        @confirm="save(form, 'update')"/>
    </el-drawer>
    <!--Update webhook-->
  </div>
</template>

<script>
import TableOperators from "@/business/components/common/components/TableOperators";
import TablePagination from "@/business/components/common/pagination/TablePagination";
import DialogFooter from "@/business/components/common/components/DialogFooter";

/* eslint-disable */
export default {
  name: "WebHookSetting",
  components: {
    TableOperators,
    TablePagination,
    DialogFooter,
  },
  data() {
    return {
      input: '',
      visible: true,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      result: {},
      loading: false,
      tableData: [],
      createVisible: false,
      updateVisible: false,
      form: {},
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
        webhook: [
          {required: true, message: this.$t('system_parameter_setting.webhook.webhook'), trigger: 'blur'},
          {min: 2, max: 500, message: this.$t('commons.input_limit', [2, 500]), trigger: 'blur'},
          {
            required: true,
            message: this.$t("workspace.special_characters_are_not_supported"),
            trigger: 'blur'
          }
        ],
      },
      buttons: [
        {
          tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
          exec: this.handleEdit
        }, {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete
        }
      ],
      proxys: [],
    }
  },

  created() {
    this.init();
  },
  methods: {
    init(){
      this.search();
      this.activeProxy();
    },
    handleClose() {
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
    search () {
      let url = "/system/webhookList/" + this.currentPage + "/" + this.pageSize;
      this.result = this.$post(url, {}, response => {
        let data = response.data;
        this.total = data.itemCount;
        this.tableData = data.listObject;
      });
    },
    handleEdit(item) {
      this.form = item;
      this.updateVisible = true;
    },
    save(form, type) {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (type === 'add') {
            this.result = this.$post("/system/add/webhook", form,response => {
              if (response.success) {
                this.$success(this.$t('commons.create_success'));
                this.handleClose();
                this.search();
              } else {
                this.$error(response.message);
              }
            });
          } else {
            this.result = this.$post("/system/edit/webhook", form,response => {
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
    create() {
      this.form = {};
      this.createVisible = true;
    },
    handleDelete(obj) {
      this.$alert(this.$t('commons.delete_confirm') + obj.name + " ？", '', {
        confirmButtonText: this.$t('commons.confirm'),
        callback: (action) => {
          if (action === 'confirm') {
            this.result = this.$get("/system/delete/webhook/" + obj.id, () => {
              this.$success(this.$t('commons.delete_success'));
              this.search();
            });
          }
        }
      });
    },
    changeStatus (item) {
      this.result = this.$post('/system/changeStatus', {id: item.id, status: item.status?1:0}, response => {
        if (item.status == 1) {
          this.$success(this.$t('rule.change_status_on'));
        } else if (item.status == 0) {
          this.$success(this.$t('rule.change_status_off'));
        }
        this.search();
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
  width: 80%;
}
.rtl >>> .el-form-item__content {
  width: 75%;
}
</style>
