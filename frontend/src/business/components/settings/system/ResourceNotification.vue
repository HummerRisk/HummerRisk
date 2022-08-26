<template>
  <div>
    <el-row>
      <el-col :span="10">
        <h3>{{ $t('system_parameter_setting.message.task_notification') }}</h3>
        <el-button icon="el-icon-circle-plus-outline" plain size="mini" @click="handleAddTaskModel">
          {{ $t('system_parameter_setting.message.create_new_notification') }}
        </el-button>
        <el-popover placement="right-end" title="Example" width="600" trigger="click">
          <hr-code-edit :read-only="true" width="600px" height="400px" :data.sync="content" :modes="modes" :mode="'html'"/>
          <el-button icon="el-icon-warning" plain size="mini" slot="reference">
            {{ $t('system_parameter_setting.message.mail_template_example') }}
          </el-button>
        </el-popover>
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="24">
        <el-table :data="resourceTask" class="tb-edit" border :cell-style="rowClass" :header-cell-style="headClass">
          <el-table-column :label="$t('schedule.event')" min-width="15%" prop="events">
            <template slot-scope="scope">
              <el-select v-model="scope.row.event" :placeholder="$t('system_parameter_setting.message.select_events')"
                         size="mini" prop="events" :disabled="!scope.row.isSet">
                <el-option v-for="item in eventOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column :label="$t('schedule.receiver')" prop="userIds" min-width="20%">
            <template v-slot:default="{row}">
              <el-select v-model="row.userIds" filterable multiple size="mini"
                         :placeholder="$t('commons.please_select')" style="width: 100%;" :disabled="!row.isSet">
                <el-option v-for="item in receiverOptions" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column :label="$t('schedule.receiving_mode')" min-width="20%" prop="type">
            <template slot-scope="scope">
              <el-select v-model="scope.row.type" :placeholder="$t('system_parameter_setting.message.select_receiving_method')"
                         size="mini" :disabled="!scope.row.isSet" @change="handleEdit(scope.$index, scope.row)">
                <el-option v-for="item in receiveTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column :label="$t('commons.operating')" fixed="right" min-width="25%" prop="result">
            <template v-slot:default="scope">
              <el-button type="primary" size="mini" v-if="scope.row.isSet" @click="handleAddTask(scope.$index,scope.row)">
                {{ $t('commons.add') }}
              </el-button>
              <el-button size="mini" v-if="scope.row.isSet" @click.native.prevent="removeRowTask(scope.$index,resourceTask)">
                {{ $t('commons.cancel') }}
              </el-button>
              <el-button type="primary" size="mini" v-if="!scope.row.isSet" @click="handleEditTask(scope.$index,scope.row)">
                {{ $t('commons.edit') }}
              </el-button>
              <el-button type="danger" icon="el-icon-delete" size="mini" v-show="!scope.row.isSet"
                @click.native.prevent="deleteRowTask(scope.$index,scope.row)"></el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import HrCodeEdit from "@/business/components/common/components/HrCodeEdit";

/* eslint-disable */
export default {
  name: "ResourceNotification",
  components: {
    HrCodeEdit
  },
  props: {
    receiverOptions: {
      type: Array
    }
  },
  data() {
    return {
      modes: ['text', 'html'],
      content: '<!DOCTYPE html>\n' +
        '<html lang="en">\n' +
        '<head>\n' +
        '    <meta charset="UTF-8">\n' +
        '    <title>HummerRisk</title>\n' +
        '    <style type="text/css">\n' +
        '        .email-table table {\n' +
        '          font-size: 12px;\n' +
        '          table-layout: fixed;\n' +
        '          empty-cells: show;\n' +
        '          border-collapse: collapse;\n' +
        '          margin: 0 0;\n' +
        '          border: 1px solid #cad9ea;\n' +
        '          color: #666;\n' +
        '        }\n' +
        '        .email-table td {\n' +
        '          height: 30px;\n' +
        '          word-wrap: break-word;\n' +
        '        }\n' +
        '        .email-table h1, h2, h3 {\n' +
        '          font-size: 12px;\n' +
        '          margin: 0;\n' +
        '          padding: 0;\n' +
        '        }\n' +
        '        .email-table th {\n' +
        '          background-repeat: repeat-x;\n' +
        '          height: 30px;\n' +
        '        }\n' +
        '        .email-table td, .email-table th {\n' +
        '          border: 1px solid #cad9ea;\n' +
        '          padding: 0 1em 0;\n' +
        '        }\n' +
        '        .email-table tr.alter {\n' +
        '          background-color: #f5fafe;\n' +
        '        }\n' +
        '    </style>\n' +
        '</head>\n' +
        '<body>\n' +
        '  <div>\n' +
        '    <div style="text-align: left">\n' +
        '        <p>尊敬的用户, 您好, 您的安全合规检测结果如下:</p>\n' +
        '    </div>\n' +
        '    <div style="text-align: left;">\n' +
        '        <p style="margin-left: 10px;">不合规资源总数/资源总数 : #{returnSum}/#{resourcesSum}</p>\n' +
        '    </div>\n' +
        '    <div class="email-table">\n' +
        '        <table class="alter">\n' +
        '            <thead style="background: #87CEFA;">\n' +
        '                <tr class="alter">\n' +
        '                   <th>规则</th>\n' +
        '                   <th>云平台</th>\n' +
        '                   <th>云账号</th>\n' +
        '                   <th>不合规资源数量</th>\n' +
        '                   <th>资源总数</th>\n' +
        '                   <th>风险等级</th>\n' +
        '                   <th>创建人</th>\n' +
        '                </tr>\n' +
        '            </thead>\n' +
        '            <tbody>\n' +
        '                <tr th:each="resource:${resources}">\n' +
        '                   <td th:text="${resource.description}"/>\n' +
        '                   <td th:text="${resource.pluginName}"/>\n' +
        '                   <td th:text="${resource.accountName}"/>\n' +
        '                   <td th:text="${resource.returnSum}"/>\n' +
        '                   <td th:text="${resource.resourcesSum}"/>\n' +
        '                   <td th:text="${resource.severity}"/>\n' +
        '                   <td th:text="${resource.applyUser}"/>\n' +
        '                </tr>\n' +
        '            </tbody>\n' +
        '        </table>\n' +
        '    </div>\n' +
        '    <h5 style="color: red;">注：更多详情请登录 HummerRisk 平台查看。</h5>\n' +
        '  \n' +
        '  </div>\n' +
        '</body>\n' +
        '</html>',
      textContent:  '尊敬的用户, 您好, 您的安全合规检测结果如下:\n' +
                    '\n' +
                    '不合规资源总数/资源总数 : #{returnSum}/#{resourcesSum}\n' +
                    '\n' +
                    '注：更多详情请登录 HummerRisk 平台查看。\n',
      resourceTask: [{
        taskType: "RESOURCE_TASK",
        event: "",
        userIds: [],
        type: [],
        isSet: true,
        identification: "",
        isReadOnly: false,
      }],
      eventOptions: [
        {value: 'EXECUTE_SUCCESSFUL', label: this.$t('schedule.event_success')},
        {value: 'EXECUTE_FAILED', label: this.$t('schedule.event_failed')}
      ],
      receiveTypeOptions: [
        {value: 'EMAIL', label: this.$t('system_parameter_setting.message.mail')},
        {value: 'NAIL_ROBOT', label: this.$t('system_parameter_setting.message.nail_robot')},
        {value: 'WECHAT_ROBOT', label: this.$t('system_parameter_setting.message.enterprise_wechat_robot')}
      ],
    }
  },
  activated() {
    this.initForm();
  },
  methods: {
    initForm() {
      this.result = this.$get('/notice/search/message/type/' + "RESOURCE_TASK", response => {
        this.resourceTask = response.data;
      })
    },
    handleEdit(index, data) {
      data.isReadOnly = true;
      if (data.type === 'EMAIL') {
        data.isReadOnly = !data.isReadOnly;
      }
    },
    handleAddTaskModel() {
      let Task = {};
      Task.event = [];
      Task.userIds = [];
      Task.type = '';
      Task.template = this.content;
      Task.textTemplate = this.textContent;
      Task.isSet = true;
      Task.identification = '';
      Task.taskType = "RESOURCE_TASK";
      this.resourceTask.push(Task)
    },
    handleAddTask(index, data) {
      if (data.event && data.userIds.length > 0 && data.type) {
        if (data.type === 'NAIL_ROBOT' || data.type === 'WECHAT_ROBOT') {
          this.addTask(data)
        } else {
          this.addTask(data)
        }
      } else {
        this.$warning(this.$t('system_parameter_setting.message.message'));
      }
    },
    handleEditTask(index, data) {
      data.isSet = true;
      if (data.type === 'EMAIL') {
        data.isReadOnly = false;
      } else {
        data.isReadOnly = true;
      }
    },
    addTask(data) {
      this.result = this.$post("/notice/save/message/cloudTask", data, () => {
        this.initForm();
        this.$success(this.$t('commons.save_success'));
      })
    },
    removeRowTask(index, data) { //移除
      if (!data[index].identification) {
        data.splice(index, 1)
      } else {
        data[parseInt(index)].isSet = false;
      }
    },
    deleteRowTask(index, data) { //删除
      this.result = this.$get("/notice/delete/message/" + data.identification, response => {
        this.$success(this.$t('commons.delete_success'));
        this.initForm();
      });
    },
    rowClass() {
      return "text-align:center";
    },
    headClass() {
      return "text-align:center;background:'#ededed'";
    },
  }
}
</script>

<style scoped>
.el-row {
  margin-bottom: 10px;
}

.el-button {
  margin-left: 10px;
}
</style>
