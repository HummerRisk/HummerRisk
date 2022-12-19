<template>
  <div>
    <el-row>
      <el-col :span="24">
        <h3 style="margin: 15px;">{{ $t('system_parameter_setting.message.task_notification') }}</h3>
        <el-button icon="el-icon-circle-plus-outline" plain size="mini" @click="handleAddTaskModel">
          {{ $t('system_parameter_setting.message.create_new_notification') }}
        </el-button>
        <el-popover placement="right-end" title="Example" width="600" trigger="click">
          <hr-code-edit :read-only="true" width="600px" height="400px" :data.sync="content" :modes="modes" :mode="'html'"/>
          <el-button plain size="mini" slot="reference">
            <i class="iconfont icon-guanyuyunguanjia"></i> {{ $t('system_parameter_setting.message.cloud_mail_template_example') }}
          </el-button>
        </el-popover>
        <el-popover placement="right-end" title="Example" width="600" trigger="click">
          <hr-code-edit :read-only="true" width="600px" height="400px" :data.sync="content2" :modes="modes" :mode="'html'"/>
          <el-button plain size="mini" slot="reference">
            <i class="iconfont icon-adsyunyuanshengshujuku"></i> {{ $t('system_parameter_setting.message.k8s_mail_template_example') }}
          </el-button>
        </el-popover>
        <el-popover placement="right-end" title="Example" width="600" trigger="click">
          <hr-code-edit :read-only="true" width="600px" height="400px" :data.sync="content3" :modes="modes" :mode="'html'"/>
          <el-button plain size="mini" slot="reference">
            <i class="iconfont icon-fuwuqi"></i> {{ $t('system_parameter_setting.message.server_mail_template_example') }}
          </el-button>
        </el-popover>
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="24">
        <el-table :data="resourceTask" class="tb-edit" border :cell-style="rowClass" :header-cell-style="headClass">
          <el-table-column type="index" min-width="40"/>
          <el-table-column :label="$t('schedule.event')" min-width="100" prop="events">
            <template slot-scope="scope">
              <el-select v-model="scope.row.event" :placeholder="$t('system_parameter_setting.message.select_events')"
                         size="mini" prop="events" :disabled="!scope.row.isSet">
                <el-option v-for="item in eventOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column :label="$t('schedule.receiver')" prop="userIds" min-width="150">
            <template v-slot:default="{row}">
              <el-select v-model="row.userIds" filterable multiple size="mini"
                         :placeholder="$t('commons.please_select')" :disabled="!row.isSet">
                <el-option v-for="item in receiverOptions" :key="item.id" :label="item.name" :value="item.id"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column :label="$t('schedule.receiving_mode')" min-width="120" prop="type" fixed="right">
            <template slot-scope="scope">
              <el-select v-model="scope.row.type" :placeholder="$t('system_parameter_setting.message.select_receiving_method')"
                         size="mini" :disabled="!scope.row.isSet" @change="handleEdit(scope.$index, scope.row)">
                <el-option v-for="item in receiveTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column :label="$t('commons.operating')" fixed="right" min-width="120" prop="result">
            <template v-slot:default="scope">
              <el-button type="primary" size="mini" v-if="scope.row.isSet" @click="handleAddTask(scope.$index, scope.row)">
                {{ $t('commons.add') }}
              </el-button>
              <el-button size="mini" v-if="scope.row.isSet" @click.native.prevent="removeRowTask(scope.$index, resourceTask)">
                {{ $t('commons.cancel') }}
              </el-button>
              <el-button type="primary" size="mini" v-if="!scope.row.isSet" @click="handleEditTask(scope.$index, scope.row)">
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
        '        <p>尊敬的用户, 您好, 您的多云安全合规检测结果如下:</p>\n' +
        '    </div>\n' +
        '    <div style="text-align: left;">\n' +
        '        <p style="margin-left: 10px;">不合规资源总数/资源总数 : #{returnSum}/#{resourcesSum}</p>\n' +
        '    </div>\n' +
        '    <div class="email-table">\n' +
        '        <table class="alter">\n' +
        '            <thead style="background: #87CEFA;">\n' +
        '                <tr class="alter">\n' +
        '                   <th>规则名称</th>\n' +
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
      content2: '<!DOCTYPE html>\n' +
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
        '        <p>尊敬的用户, 您好, 您的K8s安全风险检测结果如下:</p>\n' +
        '    </div>\n' +
        '    <div style="text-align: left;">\n' +
        '        <p style="margin-left: 10px;">漏洞总数 : #{returnSum}</p>\n' +
        '    </div>\n' +
        '    <div class="email-table">\n' +
        '        <table class="alter">\n' +
        '            <thead style="background: #87CEFA;">\n' +
        '                <tr class="alter">\n' +
        '                   <th>Resource</th>\n' +
        '                   <th>VulnerabilityID</th>\n' +
        '                   <th>Severity</th>\n' +
        '                   <th>InstalledVersion</th>\n' +
        '                   <th>FixedVersion</th>\n' +
        '                   <th>Score</th>\n' +
        '                   <th>primaryLink</th>\n' +
        '                </tr>\n' +
        '            </thead>\n' +
        '            <tbody>\n' +
        '                <tr th:each="resource:${resources}">\n' +
        '                   <td th:text="${resource.resource}"/>\n' +
        '                   <td th:text="${resource.vulnerabilityId}"/>\n' +
        '                   <td th:text="${resource.severity}"/>\n' +
        '                   <td th:text="${resource.installedVersion}"/>\n' +
        '                   <td th:text="${resource.fixedVersion}"/>\n' +
        '                   <td th:text="${resource.score}"/>\n' +
        '                   <td th:text="${resource.primaryLink}"/>\n' +
        '                </tr>\n' +
        '            </tbody>\n' +
        '        </table>\n' +
        '    </div>\n' +
        '    <h5 style="color: red;">注：更多详情请登录 HummerRisk 平台查看。</h5>\n' +
        '  \n' +
        '  </div>\n' +
        '</body>\n' +
        '</html>',
      content3: '<!DOCTYPE html>\n' +
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
        '        <p>尊敬的用户, 您好, 您的主机安全风险检测结果如下:</p>\n' +
        '    </div>\n' +
        '    <div style="text-align: left;">\n' +
        '        <p style="margin-left: 10px;">风险总数 : #{returnSum}</p>\n' +
        '    </div>\n' +
        '    <div class="email-table">\n' +
        '        <table class="alter">\n' +
        '            <thead style="background: #87CEFA;">\n' +
        '                <tr class="alter">\n' +
        '                   <th>主机分组</th>\n' +
        '                   <th>主机名称</th>\n' +
        '                   <th>IP:Port</th>\n' +
        '                   <th>规则名称</th>\n' +
        '                   <th>检测状态</th>\n' +
        '                   <th>风险等级</th>\n' +
        '                   <th>是否安全</th>\n' +
        '                </tr>\n' +
        '            </thead>\n' +
        '            <tbody>\n' +
        '                <tr th:each="resource:${resources}">\n' +
        '                   <td th:text="${resource.groupName}"/>\n' +
        '                   <td th:text="${resource.serverName}"/>\n' +
        '                   <td th:text="${resource.ip}"/>\n' +
        '                   <td th:text="${resource.ruleName}"/>\n' +
        '                   <td th:text="${resource.resultStatus}"/>\n' +
        '                   <td th:text="${resource.severity}"/>\n' +
        '                   <td th:text="${resource.isSeverity}"/>\n' +
        '                </tr>\n' +
        '            </tbody>\n' +
        '        </table>\n' +
        '    </div>\n' +
        '    <h5 style="color: red;">注：更多详情请登录 HummerRisk 平台查看。</h5>\n' +
        '  \n' +
        '  </div>\n' +
        '</body>\n' +
        '</html>',
      content4: '<!DOCTYPE html>\n' +
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
        '        <p>尊敬的用户, 您好, 您的漏洞安全合规检测结果如下:</p>\n' +
        '    </div>\n' +
        '    <div style="text-align: left;">\n' +
        '        <p style="margin-left: 10px;">不合规资源总数/资源总数 : #{returnSum}/#{resourcesSum}</p>\n' +
        '    </div>\n' +
        '    <div class="email-table">\n' +
        '        <table class="alter">\n' +
        '            <thead style="background: #87CEFA;">\n' +
        '                <tr class="alter">\n' +
        '                   <th>规则名称</th>\n' +
        '                   <th>漏洞引擎</th>\n' +
        '                   <th>漏洞配置名称</th>\n' +
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
      content5: '<!DOCTYPE html>\n' +
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
        '        <p>尊敬的用户, 您好, 您的部署安全风险检测结果如下:</p>\n' +
        '    </div>\n' +
        '    <div style="text-align: left;">\n' +
        '        <p style="margin-left: 10px;">漏洞总数 : #{returnSum}</p>\n' +
        '    </div>\n' +
        '    <div class="email-table">\n' +
        '        <table class="alter">\n' +
        '            <thead style="background: #87CEFA;">\n' +
        '                <tr class="alter">\n' +
        '                   <th>ID</th>\n' +
        '                   <th>Type</th>\n' +
        '                   <th>Severity</th>\n' +
        '                   <th>Status</th>\n' +
        '                   <th>Title</th>\n' +
        '                   <th>primaryUrl</th>\n' +
        '                </tr>\n' +
        '            </thead>\n' +
        '            <tbody>\n' +
        '                <tr th:each="resource:${resources}">\n' +
        '                   <td th:text="${resource.itemId}"/>\n' +
        '                   <td th:text="${resource.type}"/>\n' +
        '                   <td th:text="${resource.severity}"/>\n' +
        '                   <td th:text="${resource.status}"/>\n' +
        '                   <td th:text="${resource.title}"/>\n' +
        '                   <td th:text="${resource.primaryUrl}"/>\n' +
        '                </tr>\n' +
        '            </tbody>\n' +
        '        </table>\n' +
        '    </div>\n' +
        '    <h5 style="color: red;">注：更多详情请登录 HummerRisk 平台查看。</h5>\n' +
        '  \n' +
        '  </div>\n' +
        '</body>\n' +
        '</html>',
      content6: '<!DOCTYPE html>\n' +
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
        '        <p>尊敬的用户, 您好, 您的镜像安全风险检测结果如下:</p>\n' +
        '    </div>\n' +
        '    <div style="text-align: left;">\n' +
        '        <p style="margin-left: 10px;">漏洞总数 : #{returnSum}</p>\n' +
        '    </div>\n' +
        '    <div class="email-table">\n' +
        '        <table class="alter">\n' +
        '            <thead style="background: #87CEFA;">\n' +
        '                <tr class="alter">\n' +
        '                   <th>PkgName</th>\n' +
        '                   <th>VulnerabilityID</th>\n' +
        '                   <th>Severity</th>\n' +
        '                   <th>InstalledVersion</th>\n' +
        '                   <th>FixedVersion</th>\n' +
        '                   <th>primaryUrl</th>\n' +
        '                </tr>\n' +
        '            </thead>\n' +
        '            <tbody>\n' +
        '                <tr th:each="resource:${resources}">\n' +
        '                   <td th:text="${resource.pkgName}"/>\n' +
        '                   <td th:text="${resource.vulnerabilityId}"/>\n' +
        '                   <td th:text="${resource.severity}"/>\n' +
        '                   <td th:text="${resource.installedVersion}"/>\n' +
        '                   <td th:text="${resource.fixedVersion}"/>\n' +
        '                   <td th:text="${resource.primaryUrl}"/>\n' +
        '                </tr>\n' +
        '            </tbody>\n' +
        '        </table>\n' +
        '    </div>\n' +
        '    <h5 style="color: red;">注：更多详情请登录 HummerRisk 平台查看。</h5>\n' +
        '  \n' +
        '  </div>\n' +
        '</body>\n' +
        '</html>',
      content7: '<!DOCTYPE html>\n' +
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
        '        <p>尊敬的用户, 您好, 您的源码安全风险检测结果如下:</p>\n' +
        '    </div>\n' +
        '    <div style="text-align: left;">\n' +
        '        <p style="margin-left: 10px;">漏洞总数 : #{returnSum}</p>\n' +
        '    </div>\n' +
        '    <div class="email-table">\n' +
        '        <table class="alter">\n' +
        '            <thead style="background: #87CEFA;">\n' +
        '                <tr class="alter">\n' +
        '                   <th>PkgName</th>\n' +
        '                   <th>VulnerabilityID</th>\n' +
        '                   <th>Severity</th>\n' +
        '                   <th>InstalledVersion</th>\n' +
        '                   <th>FixedVersion</th>\n' +
        '                   <th>primaryUrl</th>\n' +
        '                </tr>\n' +
        '            </thead>\n' +
        '            <tbody>\n' +
        '                <tr th:each="resource:${resources}">\n' +
        '                   <td th:text="${resource.pkgName}"/>\n' +
        '                   <td th:text="${resource.vulnerabilityId}"/>\n' +
        '                   <td th:text="${resource.severity}"/>\n' +
        '                   <td th:text="${resource.installedVersion}"/>\n' +
        '                   <td th:text="${resource.fixedVersion}"/>\n' +
        '                   <td th:text="${resource.primaryUrl}"/>\n' +
        '                </tr>\n' +
        '            </tbody>\n' +
        '        </table>\n' +
        '    </div>\n' +
        '    <h5 style="color: red;">注：更多详情请登录 HummerRisk 平台查看。</h5>\n' +
        '  \n' +
        '  </div>\n' +
        '</body>\n' +
        '</html>',
      content8: '<!DOCTYPE html>\n' +
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
        '        <p>尊敬的用户, 您好, 您的文件安全风险检测结果如下:</p>\n' +
        '    </div>\n' +
        '    <div style="text-align: left;">\n' +
        '        <p style="margin-left: 10px;">漏洞总数 : #{returnSum}</p>\n' +
        '    </div>\n' +
        '    <div class="email-table">\n' +
        '        <table class="alter">\n' +
        '            <thead style="background: #87CEFA;">\n' +
        '                <tr class="alter">\n' +
        '                   <th>PkgName</th>\n' +
        '                   <th>VulnerabilityID</th>\n' +
        '                   <th>Severity</th>\n' +
        '                   <th>InstalledVersion</th>\n' +
        '                   <th>FixedVersion</th>\n' +
        '                   <th>primaryUrl</th>\n' +
        '                </tr>\n' +
        '            </thead>\n' +
        '            <tbody>\n' +
        '                <tr th:each="resource:${resources}">\n' +
        '                   <td th:text="${resource.pkgName}"/>\n' +
        '                   <td th:text="${resource.vulnerabilityId}"/>\n' +
        '                   <td th:text="${resource.severity}"/>\n' +
        '                   <td th:text="${resource.installedVersion}"/>\n' +
        '                   <td th:text="${resource.fixedVersion}"/>\n' +
        '                   <td th:text="${resource.primaryUrl}"/>\n' +
        '                </tr>\n' +
        '            </tbody>\n' +
        '        </table>\n' +
        '    </div>\n' +
        '    <h5 style="color: red;">注：更多详情请登录 HummerRisk 平台查看。</h5>\n' +
        '  \n' +
        '  </div>\n' +
        '</body>\n' +
        '</html>',
      textContentCloud:  '尊敬的用户, 您好, 您的多云安全合规检测结果如下:\n' +
        '\n' +
        '云账号名称 : #{name}\n' +
        '\n' +
        '不合规资源总数/资源总数 : #{returnSum}/#{resourcesSum}\n' +
        '\n' +
        '高危风险 : #{critical}\n' +
        '\n' +
        '高风险 : #{high}\n' +
        '\n' +
        '中风险 : #{medium}\n' +
        '\n' +
        '低风险 : #{low}\n' +
        '\n' +
        '注：更多详情请登录 HummerRisk 平台查看。\n',
      textContentVuln:  '尊敬的用户, 您好, 您的漏洞安全检测结果如下:\n' +
        '\n' +
        '漏洞配置名称 : #{name}\n' +
        '\n' +
        '不合规资源总数/资源总数 : #{returnSum}/#{resourcesSum}\n' +
        '\n' +
        '高危风险 : #{critical}\n' +
        '\n' +
        '高风险 : #{high}\n' +
        '\n' +
        '中风险 : #{medium}\n' +
        '\n' +
        '低风险 : #{low}\n' +
        '\n' +
        '注：更多详情请登录 HummerRisk 平台查看。\n',
      textContentServer:  '尊敬的用户, 您好, 您的主机安全检测结果如下:\n' +
        '\n' +
        '主机名称 : #{name}\n' +
        '\n' +
        '风险总数 : #{returnSum}\n' +
        '\n' +
        '高危风险 : #{critical}\n' +
        '\n' +
        '高风险 : #{high}\n' +
        '\n' +
        '中风险 : #{medium}\n' +
        '\n' +
        '低风险 : #{low}\n' +
        '\n' +
        '注：更多详情请登录 HummerRisk 平台查看。\n',
      textContentK8s:  '尊敬的用户, 您好, 您的K8s安全检测结果如下:\n' +
        '\n' +
        'K8s名称 : #{name}\n' +
        '\n' +
        '风险总数 : #{returnSum}\n' +
        '\n' +
        '高危风险(Critical)  : #{critical}\n' +
        '\n' +
        '高风险(High) : #{high}\n' +
        '\n' +
        '中等风险(Medium) : #{medium}\n' +
        '\n' +
        '低风险(Low) : #{low}\n' +
        '\n' +
        '无风险(Unknown) : #{unknown}\n' +
        '\n' +
        '注：更多详情请登录 HummerRisk 平台查看。\n',
      textContentConfig:  '尊敬的用户, 您好, 您的部署安全检测结果如下:\n' +
        '\n' +
        '部署配置名称 : #{name}\n' +
        '\n' +
        '风险总数 : #{returnSum}\n' +
        '\n' +
        '高危风险(Critical)  : #{critical}\n' +
        '\n' +
        '高风险(High) : #{high}\n' +
        '\n' +
        '中等风险(Medium) : #{medium}\n' +
        '\n' +
        '低风险(Low) : #{low}\n' +
        '\n' +
        '无风险(Unknown) : #{unknown}\n' +
        '\n' +
        '注：更多详情请登录 HummerRisk 平台查看。\n',
      textContentImage:  '尊敬的用户, 您好, 您的镜像安全检测结果如下:\n' +
        '\n' +
        '镜像名称 : #{name}\n' +
        '\n' +
        '风险总数 : #{returnSum}\n' +
        '\n' +
        '高危风险(Critical)  : #{critical}\n' +
        '\n' +
        '高风险(High) : #{high}\n' +
        '\n' +
        '中等风险(Medium) : #{medium}\n' +
        '\n' +
        '低风险(Low) : #{low}\n' +
        '\n' +
        '无风险(Unknown) : #{unknown}\n' +
        '\n' +
        '注：更多详情请登录 HummerRisk 平台查看。\n',
      textContentCode:  '尊敬的用户, 您好, 您的源码安全检测结果如下:\n' +
        '\n' +
        '源码名称 : #{name}\n' +
        '\n' +
        '风险总数 : #{returnSum}\n' +
        '\n' +
        '高危风险(Critical)  : #{critical}\n' +
        '\n' +
        '高风险(High) : #{high}\n' +
        '\n' +
        '中等风险(Medium) : #{medium}\n' +
        '\n' +
        '低风险(Low) : #{low}\n' +
        '\n' +
        '无风险(Unknown) : #{unknown}\n' +
        '\n' +
        '注：更多详情请登录 HummerRisk 平台查看。\n',
      textContentFs:  '尊敬的用户, 您好, 您的文件安全检测结果如下:\n' +
        '\n' +
        '文件名称 : #{name}\n' +
        '\n' +
        '风险总数 : #{returnSum}\n' +
        '\n' +
        '高危风险(Critical)  : #{critical}\n' +
        '\n' +
        '高风险(High) : #{high}\n' +
        '\n' +
        '中等风险(Medium) : #{medium}\n' +
        '\n' +
        '低风险(Low) : #{low}\n' +
        '\n' +
        '无风险(Unknown) : #{unknown}\n' +
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
        {value: 'EXECUTE_CLOUD', label: this.$t('commons.cloud_scan')},
        {value: 'EXECUTE_VULN', label: this.$t('dashboard.vuln_scan')},
        {value: 'EXECUTE_SERVER', label: this.$t('dashboard.server_scan')},
        {value: 'EXECUTE_K8S', label: this.$t('dashboard.k8s_scan')},
        {value: 'EXECUTE_CONFIG', label: this.$t('dashboard.config_scan')},
        {value: 'EXECUTE_IMAGE', label: this.$t('dashboard.image_scan')},
        {value: 'EXECUTE_CODE', label: this.$t('dashboard.code_scan')},
        {value: 'EXECUTE_FS', label: this.$t('dashboard.fs_scan')},
      ],
      receiveTypeOptions: [
        {value: 'EMAIL', label: this.$t('system_parameter_setting.message.mail')},
        {value: 'NAIL_ROBOT', label: this.$t('system_parameter_setting.message.nail_robot')},
        {value: 'WECHAT_ROBOT', label: this.$t('system_parameter_setting.message.enterprise_wechat_robot')},
        {value: 'WEBHOOK', label: 'WebHook'}
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
      Task.textTemplate = this.textContentCloud;
      Task.isSet = true;
      Task.identification = '';
      Task.taskType = "RESOURCE_TASK";
      this.resourceTask.push(Task)
    },
    handleAddTask(index, data) {
      if (data.event && data.userIds.length > 0 && data.type) {
          this.addTask(data);
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
      switch (data.event) {
        case 'EXECUTE_CLOUD':
          data.textTemplate = this.textContentCloud;
          break;
        case 'EXECUTE_VULN':
          data.textTemplate = this.textContentVuln;
          data.template = this.content4;
          break;
        case 'EXECUTE_SERVER':
          data.textTemplate = this.textContentServer;
          data.template = this.content3;
          break;
        case 'EXECUTE_K8S':
          data.textTemplate = this.textContentK8s;
          data.template = this.content2;
          break;
        case 'EXECUTE_CONFIG':
          data.textTemplate = this.textContentConfig;
          data.template = this.content5;
          break;
        case 'EXECUTE_IMAGE':
          data.textTemplate = this.textContentImage;
          data.template = this.content6;
          break;
        case 'EXECUTE_CODE':
          data.textTemplate = this.textContentCode;
          data.template = this.content7;
          break;
        case 'EXECUTE_FS':
          data.textTemplate = this.textContentFs;
          data.template = this.content8;
          break;
      }
      this.result = this.$post("/notice/save/message/task", data, () => {
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

.iconfont {
  margin: 1px 5px 0 0;
  text-align: center;
  font-size: 12px;
}
</style>
