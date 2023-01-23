<template>
  <div v-loading="result.loading">
    <!--邮件表单-->
    <el-form :model="formInline" :rules="rules" ref="formInline" class="demo-form-inline"
             :disabled="show" v-loading="loading" size="small">
      <el-row>
        <el-col>
          <el-form-item label="AppKey" prop="appKey">
            <el-input v-model="formInline.appKey" placeholder="AppKey"
                      v-on:input="change()"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-form-item label="AgentId" prop="agentId">
            <el-input v-model="formInline.agentId" placeholder="AgentId"
                      v-on:input="change()"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-form-item label="AppSecret" prop="appSecret">
            <el-input v-model="formInline.appSecret" placeholder="AppSecret"
                      autocomplete="new-password" show-password type="text" @focus="changeType" ref="input"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-form-item :label="$t('system_parameter_setting.test_user')" prop="testUser">
            <el-input v-model="formInline.testUser" :placeholder="$t('system_parameter_setting.test_user')"
                      v-on:input="change()"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <div style="border: 0;margin-bottom: 20px;margin-top: 20px">
        <el-link type="primary" href="https://developers.dingtalk.com/document/app/document-upgrade-notice#/serverapi2/pgoxpy" target="_blank">{{ $t('system_parameter_setting.basic_dingding') }}</el-link>
      </div>
      <div style="border: 0;margin-bottom: 20px">
        <el-link type="primary" href="https://developers.dingtalk.com/document/app/document-upgrade-notice#/faquestions/eovtrt" target="_blank">{{ $t('system_parameter_setting.message_limit_dingding') }}</el-link>
      </div>
      <template v-slot:footer>
      </template>
    </el-form>
    <div>
      <el-button type="primary" @click="testConnection('formInline')" :disabled="disabledConnection" size="small">
        {{ $t('system_parameter_setting.test_connection') }}
      </el-button>
      <el-button @click="edit" v-if="showEdit" size="small">{{ $t('commons.edit') }}</el-button>
      <el-button type="success" @click="save('formInline')" v-if="showSave" :disabled="disabledSave" size="small">
        {{ $t('commons.save') }}
      </el-button>
      <el-button @click="cancel" type="info" v-if="showCancel" size="small">{{ $t('commons.cancel') }}</el-button>
    </div>
  </div>
</template>

<script>
/* eslint-disable */
export default {
  name: "DingSetting",
  data() {
    return {
      formInline: {},
      input: '',
      visible: true,
      result: {},
      showEdit: true,
      showSave: false,
      showCancel: false,
      show: true,
      disabledConnection: false,
      disabledSave: false,
      loading: false,
      rules: {
        appKey: [
          {
            required: true,
            message: 'AppKey',
            trigger: ['change', 'blur']
          },
        ],
        agentId: [
          {
            required: true,
            message: 'AgentId',
            trigger: ['change', 'blur']
          }
        ],
        appSecret: [
          {
            required: true,
            message: 'AppSecret',
            trigger: ['change', 'blur']
          }]
      }
    }
  },

  created() {
    this.query()
  },
  methods: {
    changeType() {
      this.$refs.input = 'password'
    },
    query() {
      this.result = this.$get("/system/dingding/info", response => {
        this.$set(this.formInline, "appKey", response.data[0].paramValue);
        this.$set(this.formInline, "agentId", response.data[1].paramValue);
        this.$set(this.formInline, "appSecret", response.data[2].paramValue);
        this.$set(this.formInline, "testUser", response.data[3].paramValue);
        this.$nextTick(() => {
          this.$refs.formInline.clearValidate();
        })
      })
    },
    change() {
      if (!this.formInline.appKey || !this.formInline.agentId || !this.formInline.appSecret) {
        this.disabledConnection = true;
        this.disabledSave = true;
      } else {
        this.disabledConnection = false;
        this.disabledSave = false;
      }
    },
    testConnection(formInline) {
      let param = {
        "dingding.appKey": this.formInline.appKey,
        "dingding.agentId": this.formInline.agentId,
        "dingding.appSecret": this.formInline.appSecret,
        "dingding.testUser": this.formInline.testUser,
      };
      this.$refs[formInline].validate((valid) => {
        if (valid) {
          this.result = this.$post("/system/testConnection/dingding", param, response => {
            this.$success(this.$t('commons.connection_successful'));
          })
        } else {
          return false;
        }
      })
    },
    edit() {
      this.showEdit = false;
      this.showSave = true;
      this.showCancel = true;
      this.show = false;
    },
    save(formInline) {
      this.showEdit = true;
      this.showCancel = false;
      this.showSave = false;
      this.show = true;
      let param = [
        {paramKey: "dingding.appKey", paramValue: this.formInline.appKey, type: "text", sort: 1},
        {paramKey: "dingding.agentId", paramValue: this.formInline.agentId, type: "text", sort: 2},
        {paramKey: "dingding.appSecret", paramValue: this.formInline.appSecret, type: "password", sort: 3},
        {paramKey: "dingding.testUser", paramValue: this.formInline.testUser, type: "text", sort: 4}
      ]

      this.$refs[formInline].validate(valid => {
        if (valid) {
          this.result = this.$post("/system/edit/dingding", param, response => {
            let flag = response.success;
            if (flag) {
              this.$success(this.$t('commons.save_success'));
            } else {
              this.$message.error(this.$t('commons.save_failed'));
            }
          });
        } else {
          return false;
        }
      })
    },
    cancel() {
      this.showEdit = true;
      this.showCancel = false;
      this.showSave = false;
      this.show = true;
      this.query();
    }

  }
}
</script>

<style scoped>

</style>
