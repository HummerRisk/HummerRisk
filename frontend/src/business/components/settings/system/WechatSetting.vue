<template>
  <div v-loading="result.loading">
    <!--邮件表单-->
    <el-form :model="formInline" :rules="rules" ref="formInline" class="demo-form-inline"
             :disabled="show" v-loading="loading" size="small">
      <el-row>
        <el-col>
          <el-form-item label="cropid" prop="cropId">
            <el-input v-model="formInline.cropId" placeholder="cropid"
                      v-on:input="change()"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-form-item label="agentid" prop="agentId">
            <el-input v-model="formInline.agentId" placeholder="agentid"
                      v-on:input="change()"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-form-item label="secret" prop="secret">
            <el-input v-model="formInline.secret" placeholder="secret"
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
        <el-link type="primary" href="https://work.weixin.qq.com/api/doc/90000/90135/90665" target="_blank">{{ $t('system_parameter_setting.basic_wechat') }}</el-link>
      </div>
      <div style="border: 0;margin-bottom: 20px">
        <el-link type="primary" href="https://work.weixin.qq.com/api/doc/90000/90135/90312" target="_blank">{{ $t('system_parameter_setting.message_limit_wechat') }}</el-link>
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
  name: "WechatSetting",
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
        cropId: [
          {
            required: true,
            message: 'cropid',
            trigger: ['change', 'blur']
          },
        ],
        agentId: [
          {
            required: true,
            message: 'agentid',
            trigger: ['change', 'blur']
          }
        ],
        secret: [
          {
            required: true,
            message: 'secret',
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
      this.result = this.$get("/system/wechat/info", response => {
        this.$set(this.formInline, "cropId", response.data[0].paramValue);
        this.$set(this.formInline, "agentId", response.data[1].paramValue);
        this.$set(this.formInline, "secret", response.data[2].paramValue);
        this.$set(this.formInline, "testUser", response.data[3].paramValue);
        this.$nextTick(() => {
          this.$refs.formInline.clearValidate();
        })
      })
    },
    change() {
      if (!this.formInline.cropId || !this.formInline.agentId || !this.formInline.secret) {
        this.disabledConnection = true;
        this.disabledSave = true;
      } else {
        this.disabledConnection = false;
        this.disabledSave = false;
      }
    },
    testConnection(formInline) {
      let param = {
        "wechat.cropId": this.formInline.cropId,
        "wechat.agentId": this.formInline.agentId,
        "wechat.secret": this.formInline.secret,
        "wechat.testUser": this.formInline.testUser
      };
      this.$refs[formInline].validate((valid) => {
        if (valid) {
          this.result = this.$post("/system/testConnection/wechat", param, response => {
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
        {paramKey: "wechat.cropId", paramValue: this.formInline.cropId, type: "text", sort: 1},
        {paramKey: "wechat.agentId", paramValue: this.formInline.agentId, type: "text", sort: 2},
        {paramKey: "wechat.secret", paramValue: this.formInline.secret, type: "password", sort: 3},
        {paramKey: "wechat.testUser", paramValue: this.formInline.testUser, type: "text", sort: 4},
      ]

      this.$refs[formInline].validate(valid => {
        if (valid) {
          this.result = this.$post("/system/edit/wechat", param, response => {
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
