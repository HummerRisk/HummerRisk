<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <table-header :condition.sync="condition" @search="search"
                        :title="$t('server.server_list')" class="table-header-l"
                        @create="create" :createTip="$t('server.server_create')" :showValidateName="false" :show-delete="true"
                        @validate="validate" :validateTip="$t('server.one_validate')" :showDeleteName="false"
                        :show-validate="true" :show-scan="false" :show-create="true" :show-filter="false" @delete="deleteServers"
                        :items="items" :columnNames="columnNames" :show-open="false" :show-upload="true" @upload="upload"
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
          <el-table-column type="selection" min-width="50"/>
          <el-table-column type="index" min-width="50"/>
          <el-table-column prop="name" v-if="checkedColumnNames.includes('name')" :label="$t('server.server_name')" min-width="170" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span>
                <img :src="require(`@/assets/img/platform/${scope.row.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                 &nbsp;&nbsp; {{ scope.row.name }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="ip" :label="'IP:Port'" v-if="checkedColumnNames.includes('ip')" min-width="170" show-overflow-tooltip v-slot:default="scope">
            {{ scope.row.ip }} : {{ scope.row.port }}
          </el-table-column>
          <el-table-column prop="status" v-if="checkedColumnNames.includes('status')" min-width="100" :label="$t('server.server_status')"
                           column-key="status"
                           :filters="statusFilters"
                           :filter-method="filterStatus">
            <template v-slot:default="{row}">
              <div @click="validateStatus(row)" style="cursor:pointer;">
                <el-tag size="mini" type="info" v-if="row.status === 'UNLINK'">
                  {{ $t('server.UNLINK') }}
                </el-tag>
                <el-tag size="mini" type="warning" v-else-if="row.status === 'DELETE'">
                  {{ $t('server.DELETE') }}
                </el-tag>
                <el-tag size="mini" type="success" v-else-if="row.status === 'VALID'">
                  {{ $t('server.VALID') }}
                </el-tag>
                <el-tag size="mini" type="danger" v-else-if="row.status === 'INVALID'">
                  {{ $t('server.INVALID') }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="type" v-if="checkedColumnNames.includes('type')" :label="$t('commons.type')" min-width="70" show-overflow-tooltip>
            <template v-slot:default="scope">
              <span v-if="scope.row.type === 'linux'">Linux</span>
              <span v-if="scope.row.type === 'windows'">Windows</span>
              <span v-if="!scope.row.type">N/A</span>
            </template>
          </el-table-column>
          <el-table-column prop="userName" v-if="checkedColumnNames.includes('userName')" :label="$t('server.server_user_name')" min-width="100" show-overflow-tooltip></el-table-column>
          <el-table-column min-width="160" v-if="checkedColumnNames.includes('updateTime')" :label="$t('account.update_time')" sortable
                           prop="updateTime">
            <template v-slot:default="scope">
              <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="groupName" v-if="checkedColumnNames.includes('groupName')" :label="$t('server.server_group')" min-width="110" show-overflow-tooltip/>
          <el-table-column prop="user" v-if="checkedColumnNames.includes('user')" :label="$t('account.creator')" min-width="100" show-overflow-tooltip/>
          <el-table-column min-width="180" :label="$t('commons.operating')" fixed="right">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </hide-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
      </el-card>

      <!--Create server-->
      <el-drawer class="rtl" :title="$t('server.server_create')" :visible.sync="createVisible" size="85%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div v-loading="rstResult.loading">
          <div style="margin: 10px;">
            <el-row>
              <el-button type="primary" icon="el-icon-circle-plus-outline" plain size="mini" @click="handleAddServerModel">
                {{ $t('server.server_add') }}
              </el-button>
              <el-button type="warning" icon="el-icon-circle-plus-outline" plain size="mini" @click="batchBind">
                {{ $t('commons.batch_settings') }}
              </el-button>
            </el-row>
          </div>
          <div>
            <el-row>
              <el-col :span="24">
                <el-table :data="servers" class="tb-edit" border :cell-style="rowClass" :header-cell-style="headClass" :key="itemKey">
                  <el-table-column :label="$t('server.server_name')" min-width="18%" prop="serverName">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.name" maxlength="100"></el-input>
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('server.server_type')" min-width="12%" prop="type">
                    <template slot-scope="scope">
                      <el-select style="width: 100%;" filterable :clearable="true" v-model="scope.row.type" :placeholder="$t('server.server_type')" @change="changeType(scope.row)">
                        <el-option
                          v-for="item in types"
                          :key="item.value"
                          :label="item.id"
                          :value="item.value">
                          &nbsp;&nbsp; {{ item.id }}
                        </el-option>
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column :label="'IP'" prop="ip" min-width="18%">
                    <template v-slot:default="{row}">
                      <el-input v-model="row.ip" maxlength="50"></el-input>
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('server.port')" min-width="10%" prop="port">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.port" maxlength="8"></el-input>
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('server.server_user_name')" min-width="15%" prop="userName">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.userName" maxlength="50"></el-input>
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('commons.certificate')" min-width="15%" prop="password">
                    <template slot-scope="scope">
                      <el-tooltip v-if="scope.row.isPublicKey==='no'" class="item" effect="dark" :content="scope.row.password" placement="left-start">
                        <el-button type="success" size="mini" @click="bindCertificate(scope.$index, scope.row)">{{ $t('server.no_public_key') }}</el-button>
                      </el-tooltip>
                      <el-tooltip v-if="scope.row.isPublicKey==='str'" class="item" effect="dark" :content="scope.row.publicKey" placement="left-start">
                        <el-button type="success" size="mini" @click="bindCertificate(scope.$index, scope.row)">{{ $t('server.str_public_key') }}</el-button>
                      </el-tooltip>
                      <el-button v-if="scope.row.isPublicKey==='file'" type="success" size="mini" @click="bindCertificate(scope.$index, scope.row)">{{ $t('server.file_public_key') }}</el-button>
                      <el-button v-if="scope.row.isPublicKey==null|| scope.row.isPublicKey==undefined" size="mini" @click="bindCertificate(scope.$index, scope.row)">{{ $t('server.to_be_set') }}</el-button>
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('commons.operating')" fixed="right" min-width="15%" prop="result">
                    <template v-slot:default="scope">
                      <el-button type="primary" size="mini" @click="bindCertificate(scope.$index, scope.row)">
                        {{ $t('commons.certificate') }}
                      </el-button>
                      <el-button type="danger" icon="el-icon-delete" size="mini" v-show="!scope.row.isSet"
                                 @click.native.prevent="deleteRowServer(scope.$index, scope.row)">
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-col>
            </el-row>
          </div>

          <!--bind Certificate-->
          <el-drawer
            size="60%"
            :title="$t('server.bind_certificate')"
            :append-to-body="true"
            :before-close="innerCertificateClose"
            :visible.sync="innerAddCertificate">
            <el-form :model="addCertificateForm" label-position="right" label-width="150px" size="small" ref="addCertificateForm" :rules="rule" style="padding: 5px 5% 5px 5px;">
              <el-form-item :label="$t('server.port')" ref="port" prop="port" :rules="{required: true, message: $t('server.port') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-input v-model="addCertificateForm.port" maxlength="8"></el-input>
              </el-form-item>
              <el-form-item :label="$t('server.server_user_name')" ref="userName" prop="userName" :rules="{required: true, message: $t('server.server_user_name') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-input v-model="addCertificateForm.userName" maxlength="50"></el-input>
              </el-form-item>
              <el-form-item :label="$t('server.bind_certificate')" ref="type" prop="type" :rules="{required: true, message: $t('server.bind_certificate') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-radio v-model="addCertificateForm.isCertificate" :label="false">{{ $t('server.menu_certificate') }}</el-radio>
                <el-radio v-model="addCertificateForm.isCertificate" :label="true">{{ $t('server.public_certificate') }}</el-radio>
              </el-form-item>
              <el-form-item v-if="addCertificateForm.isCertificate" :label="$t('server.public_certificate')">
                <el-select style="width: 100%;" filterable :clearable="true" v-model="addCertificateForm.certificateId" :placeholder="$t('server.public_certificate')">
                  <el-option
                    v-for="item in certificates"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                    &nbsp;&nbsp; {{ item.name }}
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item v-if="!addCertificateForm.isCertificate !==null && !addCertificateForm.isCertificate" :label="$t('server.is_public_key')" ref="type" prop="type" :rules="{required: true, message: $t('server.is_public_key') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-radio v-model="addCertificateForm.isPublicKey" label="no">{{ $t('server.no_public_key') }}</el-radio>
                <el-radio v-model="addCertificateForm.isPublicKey" label="str">{{ $t('server.str_public_key') }}</el-radio>
                <el-radio v-model="addCertificateForm.isPublicKey" label="file">{{ $t('server.file_public_key') }}</el-radio>
              </el-form-item>
              <el-form-item v-if="!addCertificateForm.isCertificate && addCertificateForm.isPublicKey === 'no'" :label="$t('commons.password')" ref="password" prop="password">
                <el-input type="password" v-model="addCertificateForm.password" maxlength="100" @input="change($event)" autocomplete="off" :placeholder="$t('commons.password')" show-password/>
              </el-form-item>
              <el-form-item v-if="!addCertificateForm.isCertificate && addCertificateForm.isPublicKey === 'str'" :label="$t('server.public_key')" ref="password">
                <el-input type="textarea" :rows="10" v-model="addCertificateForm.publicKey" @input="change($event)" autocomplete="off" :placeholder="$t('server.public_key')"/>
              </el-form-item>
              <el-form-item v-if="!addCertificateForm.isCertificate && addCertificateForm.isPublicKey === 'file'" :label="$t('server.public_key')" ref="password">
                <server-key-upload v-on:append="append" v-model="addCertificateForm.publicKeyPath" :param="addCertificateForm.publicKeyPath"/>
              </el-form-item>
              <el-form-item>
                <span style="color: red">{{ $t('server.certificate_note') }}</span>
              </el-form-item>
            </el-form>
            <dialog-footer
              @cancel="innerAddCertificate = false"
              @confirm="saveCertificate(addCertificateForm, rowindex)"/>
          </el-drawer>
          <!--bind Certificate-->

          <!--batch Certificate-->
          <el-drawer
            size="60%"
            :title="$t('server.bind_certificate')"
            :append-to-body="true"
            :before-close="innerCertificateClose"
            :visible.sync="batchBindCertificate">
            <el-form :model="batchBindForm" label-position="right" label-width="150px" size="small" ref="addCertificateForm" :rules="rule" style="padding: 5px 5% 5px 5px;">
              <el-form-item :label="$t('server.server_type')" ref="type" prop="type" :rules="{required: true, message: $t('server.server_type') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-select style="width: 100%;" filterable :clearable="true" v-model="batchBindForm.type" :placeholder="$t('server.server_type')">
                  <el-option
                    v-for="item in types"
                    :key="item.value"
                    :label="item.id"
                    :value="item.value">
                    &nbsp;&nbsp; {{ item.id }}
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('server.port')" ref="type" prop="port" :rules="{required: true, message: $t('server.port') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-input v-model="batchBindForm.port" maxlength="8"></el-input>
              </el-form-item>
              <el-form-item :label="$t('server.server_user_name')" ref="type" prop="userName" :rules="{required: true, message: $t('server.server_user_name') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-input v-model="batchBindForm.userName" maxlength="50"></el-input>
              </el-form-item>
              <el-form-item :label="$t('server.bind_certificate')" ref="type" prop="isCertificate" :rules="{required: true, message: $t('server.bind_certificate') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-radio v-model="batchBindForm.isCertificate" :label="false">{{ $t('server.menu_certificate') }}</el-radio>
                <el-radio v-model="batchBindForm.isCertificate" :label="true">{{ $t('server.public_certificate') }}</el-radio>
              </el-form-item>
              <el-form-item v-if="batchBindForm.isCertificate" :label="$t('server.public_certificate')">
                <el-select style="width: 100%;" filterable :clearable="true" v-model="batchBindForm.certificateId" :placeholder="$t('server.public_certificate')">
                  <el-option
                    v-for="item in certificates"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                    &nbsp;&nbsp; {{ item.name }}
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item v-if="!batchBindForm.isCertificate !==null && !batchBindForm.isCertificate" :label="$t('server.is_public_key')" ref="type" prop="type" :rules="{required: true, message: $t('server.is_public_key') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-radio v-model="batchBindForm.isPublicKey" label="no">{{ $t('server.no_public_key') }}</el-radio>
                <el-radio v-model="batchBindForm.isPublicKey" label="str">{{ $t('server.str_public_key') }}</el-radio>
                <el-radio v-model="batchBindForm.isPublicKey" label="file">{{ $t('server.file_public_key') }}</el-radio>
              </el-form-item>
              <el-form-item v-if="!batchBindForm.isCertificate && batchBindForm.isPublicKey === 'no'" :label="$t('commons.password')" ref="password" prop="password">
                <el-input type="password" v-model="batchBindForm.password" maxlength="100" autocomplete="off" :placeholder="$t('commons.password')" show-password/>
              </el-form-item>
              <el-form-item v-if="!batchBindForm.isCertificate && batchBindForm.isPublicKey === 'str'" :label="$t('server.public_key')" ref="password">
                <el-input type="textarea" :rows="10" v-model="batchBindForm.publicKey" autocomplete="off" :placeholder="$t('server.public_key')"/>
              </el-form-item>
              <el-form-item v-if="!batchBindForm.isCertificate && batchBindForm.isPublicKey === 'file'" :label="$t('server.public_key')" ref="password">
                <server-key-upload v-on:append="append" v-model="batchBindForm.publicKeyPath" :param="batchBindForm.publicKeyPath"/>
              </el-form-item>
              <el-form-item>
                <span style="color: red">{{ $t('server.certificate_note') }}</span>
              </el-form-item>
            </el-form>
            <dialog-footer
              @cancel="batchBindCertificate = false"
              @confirm="saveBatchBind(batchBindForm)"/>
          </el-drawer>
          <!--batch Certificate-->
          <div style="margin: 10px;">
            <dialog-footer
              @cancel="createVisible = false"
              @confirm="saveServer(servers)"/>
          </div>
        </div>
      </el-drawer>
      <!--Create server-->

      <!--Update server-->
      <el-drawer class="rtl" :title="$t('server.server_update')" :visible.sync="updateVisible" size="60%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div v-loading="rstResult.loading">
          <el-form :model="form" label-position="right" label-width="150px" size="small" :rules="rule" ref="createServerForm">
            <el-form-item :label="$t('server.server_group_name')" ref="serverGroupId" prop="serverGroupId">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="form.serverGroupId" :placeholder="$t('server.server_group_name')">
                <el-option
                  v-for="item in groups"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('server.server_name')" ref="name" prop="name">
              <el-input v-model="form.name" autocomplete="off" maxlength="100" :placeholder="$t('server.server_name')"/>
            </el-form-item>
            <el-form-item :label="$t('server.server_type')" ref="type" prop="type">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="form.type" :placeholder="$t('server.server_type')" @change="changeType(form)">
                <el-option
                  v-for="item in types"
                  :key="item.value"
                  :label="item.id"
                  :value="item.value">
                  &nbsp;&nbsp; {{ item.id }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="'IP'" ref="ip" prop="ip">
              <el-input v-model="form.ip" maxlength="50" autocomplete="off" :placeholder="'IP'"/>
            </el-form-item>
            <el-form-item :label="$t('server.port')" ref="port" prop="port">
              <el-input type="number" v-model="form.port" maxlength="8" autocomplete="off" :placeholder="$t('server.port')"/>
            </el-form-item>
            <el-form-item :label="$t('server.server_user_name')" ref="userName" prop="userName">
              <el-input v-model="form.userName" maxlength="50" autocomplete="off" :placeholder="$t('server.server_user_name')"/>
            </el-form-item>
            <el-form-item :label="$t('server.bind_certificate')" ref="type" prop="type" :rules="{required: true, message: $t('server.bind_certificate') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-radio v-model="form.isCertificate" :label="false">{{ $t('server.menu_certificate') }}</el-radio>
              <el-radio v-model="form.isCertificate" :label="true">{{ $t('server.public_certificate') }}</el-radio>
            </el-form-item>
            <el-form-item v-if="form.isCertificate" :label="$t('server.public_certificate')">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="form.certificateId" :placeholder="$t('server.public_certificate')">
                <el-option
                  v-for="item in certificates"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                  &nbsp;&nbsp; {{ item.name }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item v-if="!form.isCertificate !==null && !form.isCertificate" :label="$t('server.is_public_key')" ref="type" prop="type" :rules="{required: true, message: $t('server.is_public_key') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-radio v-model="form.isPublicKey" label="no">{{ $t('server.no_public_key') }}</el-radio>
              <el-radio v-model="form.isPublicKey" label="str">{{ $t('server.str_public_key') }}</el-radio>
              <el-radio v-model="form.isPublicKey" label="file">{{ $t('server.file_public_key') }}</el-radio>
            </el-form-item>
            <el-form-item v-if="!form.isCertificate && form.isPublicKey === 'no'" :label="$t('commons.password')" ref="password" prop="password">
              <el-input type="password" v-model="form.password" maxlength="100" autocomplete="off" :placeholder="$t('commons.password')" show-password/>
            </el-form-item>
            <el-form-item v-if="!form.isCertificate && form.isPublicKey === 'str'" :label="$t('server.public_key')" ref="password">
              <el-input type="textarea" :rows="10" v-model="form.publicKey" autocomplete="off" :placeholder="$t('server.public_key')"/>
            </el-form-item>
            <el-form-item v-if="!form.isCertificate && form.isPublicKey === 'file'" :label="$t('server.public_key')" ref="password">
              <server-key-upload v-on:append="append" v-model="form.publicKeyPath" :param="form.publicKeyPath"/>
            </el-form-item>
            <!--          <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">-->
            <!--            <el-switch v-model="form.isProxy"></el-switch>-->
            <!--          </el-form-item>-->
            <!--          <el-form-item v-if="form.isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">-->
            <!--            <el-select style="width: 100%;" filterable :clearable="true" v-model="form.proxyId" :placeholder="$t('commons.proxy')">-->
            <!--              <el-option-->
            <!--                v-for="item in proxys"-->
            <!--                :key="item.id"-->
            <!--                :label="item.proxyIp"-->
            <!--                :value="item.id">-->
            <!--                &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}-->
            <!--              </el-option>-->
            <!--            </el-select>-->
            <!--          </el-form-item>-->
          </el-form>
          <dialog-footer
            @cancel="updateVisible = false"
            @confirm="editServer(form)"/>
        </div>
      </el-drawer>
      <!--Update server-->

      <!--Copy server-->
      <el-drawer class="rtl" :title="$t('server.server_copy')" :visible.sync="copyVisible" size="60%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div v-loading="rstResult.loading">
          <el-form :model="form" label-position="right" label-width="150px" size="small" :rules="rule" ref="createServerForm">
            <el-form-item :label="$t('server.server_group_name')" ref="serverGroupId" prop="serverGroupId">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="form.serverGroupId" :placeholder="$t('server.server_group_name')">
                <el-option
                  v-for="item in groups"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('server.server_name')" ref="name" prop="name">
              <el-input v-model="form.name" maxlength="100" autocomplete="off" :placeholder="$t('server.server_name')"/>
            </el-form-item>
            <el-form-item :label="$t('server.server_type')" ref="type" prop="type">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="form.type" :placeholder="$t('server.server_type')" @change="changeType(form)">
                <el-option
                  v-for="item in types"
                  :key="item.value"
                  :label="item.id"
                  :value="item.value">
                  &nbsp;&nbsp; {{ item.id }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="'IP'" ref="ip" prop="ip">
              <el-input v-model="form.ip" maxlength="50" autocomplete="off" :placeholder="'IP'"/>
            </el-form-item>
            <el-form-item :label="$t('server.port')" ref="port" prop="port">
              <el-input type="number" v-model="form.port" maxlength="8" autocomplete="off" :placeholder="$t('server.port')"/>
            </el-form-item>
            <el-form-item :label="$t('server.server_user_name')" ref="userName" prop="userName">
              <el-input v-model="form.userName" maxlength="50" autocomplete="off" :placeholder="$t('server.server_user_name')"/>
            </el-form-item>
            <el-form-item :label="$t('server.bind_certificate')" ref="type" prop="type" :rules="{required: true, message: $t('server.bind_certificate') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-radio v-model="form.isCertificate" :label="false">{{ $t('server.menu_certificate') }}</el-radio>
              <el-radio v-model="form.isCertificate" :label="true">{{ $t('server.public_certificate') }}</el-radio>
            </el-form-item>
            <el-form-item v-if="form.isCertificate" :label="$t('server.public_certificate')">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="form.certificateId" :placeholder="$t('server.public_certificate')">
                <el-option
                  v-for="item in certificates"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                  &nbsp;&nbsp; {{ item.name }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item v-if="!form.isCertificate !==null && !form.isCertificate" :label="$t('server.is_public_key')" ref="type" prop="type" :rules="{required: true, message: $t('server.is_public_key') + $t('commons.cannot_be_empty'), trigger: 'change'}">
              <el-radio v-model="form.isPublicKey" label="no">{{ $t('server.no_public_key') }}</el-radio>
              <el-radio v-model="form.isPublicKey" label="str">{{ $t('server.str_public_key') }}</el-radio>
              <el-radio v-model="form.isPublicKey" label="file">{{ $t('server.file_public_key') }}</el-radio>
            </el-form-item>
            <el-form-item v-if="!form.isCertificate && form.isPublicKey === 'no'" :label="$t('commons.password')" ref="password" prop="password">
              <el-input type="password" v-model="form.password" maxlength="100" autocomplete="off" :placeholder="$t('commons.password')" show-password/>
            </el-form-item>
            <el-form-item v-if="!form.isCertificate && form.isPublicKey === 'str'" :label="$t('server.public_key')" ref="password">
              <el-input type="textarea" :rows="10" v-model="form.publicKey" autocomplete="off" :placeholder="$t('server.public_key')"/>
            </el-form-item>
            <el-form-item v-if="!form.isCertificate && form.isPublicKey === 'file'" :label="$t('server.public_key')" ref="password">
              <server-key-upload v-on:append="append" v-model="form.publicKeyPath" :param="form.publicKeyPath"/>
            </el-form-item>
            <!--          <el-form-item :label="$t('proxy.is_proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">-->
            <!--            <el-switch v-model="form.isProxy"></el-switch>-->
            <!--          </el-form-item>-->
            <!--          <el-form-item v-if="form.isProxy" :label="$t('commons.proxy')" :rules="{required: true, message: $t('commons.proxy') + $t('commons.cannot_be_empty'), trigger: 'change'}">-->
            <!--            <el-select style="width: 100%;" filterable :clearable="true" v-model="form.proxyId" :placeholder="$t('commons.proxy')">-->
            <!--              <el-option-->
            <!--                v-for="item in proxys"-->
            <!--                :key="item.id"-->
            <!--                :label="item.proxyIp"-->
            <!--                :value="item.id">-->
            <!--                &nbsp;&nbsp; {{ item.proxyIp + ':' + item.proxyPort }}-->
            <!--              </el-option>-->
            <!--            </el-select>-->
            <!--          </el-form-item>-->
          </el-form>
          <dialog-footer
            @cancel="copyVisible = false"
            @confirm="copyServer(form)"/>
        </div>
      </el-drawer>
      <!--Copy server-->

      <!--Update excel-->
      <el-drawer class="rtl" :title="$t('server.batch_update_excel')" :visible.sync="uploadExcel" size="60%" :before-close="handleClose" :direction="direction"
                 :destroy-on-close="true">
        <div v-loading="rstResult.loading">
          <el-form :model="uploadExcelForm" label-position="right" label-width="150px" size="small" :rules="rule" ref="uploadExcelForm">
            <el-form-item :label="$t('server.server_group_name')" ref="serverGroupId" prop="serverGroupId">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="uploadExcelForm.serverGroupId" :placeholder="$t('server.server_group_name')">
                <el-option
                  v-for="item in groups"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('server.excel_file_ex')" ref="file" prop="file">
              <el-button
                type="success"
                size="small"
                @click="downloadExcel"
              ><i class="el-icon-download" style="margin: 1px 3px 0 0;"></i> {{ $t('server.excel_file_ex_down') }}
            </el-button>
            </el-form-item>
            <el-form-item :label="$t('server.upload_excel')" ref="file" prop="file">
              <el-upload
                ref="upload"
                class="filter-item"
                name="file"
                action="string"
                :on-change="beforeAvatarUpload"
                accept=".xlsx,.xls"
                :auto-upload="false"
                :show-file-list="true"
                :file-list="fileList"
                :limit="1"
              >
                <el-button
                  type="primary"
                  size="small"
                ><i class="iconfont icon-excel"></i> {{ $t('server.upload_excel_file') }}
                </el-button>
              </el-upload>
            </el-form-item>
            <div style="color: red;font-style:italic;margin: 5px 0 10px 50px;">{{ $t('server.upload_excel_file_note') }}</div>
          </el-form>
          <dialog-footer
            @cancel="uploadExcel = false"
            @confirm="uploadServerExcel()"/>
        </div>
      </el-drawer>
      <!--Update excel-->

      <!-- 一键检测选择规则组 -->
      <el-dialog :close-on-click-modal="false"
                 :modal-append-to-body="false"
                 :title="$t('account.scan_group_quick')"
                 :visible.sync="scanVisible"
                 class="" width="70%">
        <div v-loading="groupResult.loading">
          <el-card class="box-card el-box-card">
            <div slot="header" class="clearfix">
              <span>
                <img :src="require(`@/assets/img/platform/${serverWithGroup.pluginIcon}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
             &nbsp;&nbsp; {{ serverWithGroup.pluginName }} {{ $t('server.server_rule_group') }}
              </span>
              <el-button style="float: right; padding: 3px 0" type="text"  @click="handleCheckAllByAccount">{{ $t('account.i18n_sync_all') }}</el-button>
            </div>
            <el-checkbox-group v-model="checkedGroups">
              <el-checkbox v-for="(group, index) in ruleGroups" :label="group.id" :value="group.id" :key="index" border >
                {{ group.name }}
              </el-checkbox>
            </el-checkbox-group>
          </el-card>
          <dialog-footer
            @cancel="scanVisible = false"
            @confirm="scanGroup()"/>
        </div>
      </el-dialog>
      <!-- 一键检测选择检测组 -->

    </main-container>
</template>

<script>
import TablePagination from "@/business/components/common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperator from "@/business/components/common/components/TableOperator";
import Container from "@/business/components/common/components/Container";
import MainContainer from "@/business/components/common/components/MainContainer";
import TableOperators from "@/business/components/common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {SERVER_CONFIGS} from "@/business/components/common/components/search/search-components";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import ServerKeyUpload from "@/business/components/server/head/ServerKeyUpload";
import HideTable from "@/business/components/common/hideTable/HideTable";
import {saveAs} from "@/common/js/FileSaver";
import {proxyListAllUrl} from "@/api/system/system";
import {
  addServerUrl,
  allCertificateListUrl,
  copyServerUrl, deleteServersUrl,
  deleteServerUrl,
  editServerUrl,
  serverDownloadExcelUrl,
  serverExcelInsertExpertsUrl,
  serverGroupListUrl,
  serverListUrl, serverRuleGroupsByTypeUrl,
  serverRuleGroupsUrl,
  serverScanUrl,
  serverValidatesUrl,
  serverValidateUrl,
} from "@/api/k8s/server/server";

//列表展示与隐藏
const columnOptions = [
  {
    label: 'server.server_name',
    props: 'name',
    disabled: false
  },
  {
    label: 'IP:Port',
    props: 'ip',
    disabled: false
  },
  {
    label: 'server.server_status',
    props: 'status',
    disabled: false
  },
  {
    label: 'server.server_type',
    props: 'type',
    disabled: false
  },
  {
    label: 'server.server_user_name',
    props: 'userName',
    disabled: false
  },
  {
    label: 'account.update_time',
    props: 'updateTime',
    disabled: false
  },
  {
    label: 'server.server_group',
    props: 'groupName',
    disabled: false
  },
  {
    label: 'account.creator',
    props: 'user',
    disabled: false
  },
];

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
      HideTable,
    },
    provide() {
      return {
        search: this.search,
      }
    },
    data() {
      return {
        result: {},
        rstResult: {},
        groupResult: {},
        servers: [],
        condition: {
          components: SERVER_CONFIGS
        },
        tableData: [],
        currentPage: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        selectIds: new Set(),
        createVisible: false,
        updateVisible: false,
        copyVisible: false,
        uploadExcel: false,
        item: {},
        form: {isPublicKey: "no"},
        script: '',
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
          ip: [
            {required: true, message: 'IP', trigger: 'blur'},
            {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
            {
              required: true,
              message: this.$t("workspace.special_characters_are_not_supported"),
              trigger: 'blur'
            }
          ],
          port: [
            {required: true, message: this.$t('server.port'), trigger: 'blur'},
            {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
            {
              required: true,
              message: this.$t("workspace.special_characters_are_not_supported"),
              trigger: 'blur'
            }
          ],
          userName: [
            {required: true, message: this.$t('server.server_user_name'), trigger: 'blur'},
            {min: 2, max: 150, message: this.$t('commons.input_limit', [2, 150]), trigger: 'blur'},
            {
              required: true,
              message: this.$t("workspace.special_characters_are_not_supported"),
              trigger: 'blur'
            }
          ],
          type: [
            {required: true, message: this.$t('server.server_type'), trigger: 'change'},
          ],
        },
        buttons: [
          {
            tip: this.$t('account.one_scan'), icon: "el-icon-s-promotion", type: "success",
            exec: this.openScanGroup
          },
          {
            tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
            exec: this.handleEdit
          }, {
            tip: this.$t('commons.copy'), icon: "el-icon-document-copy", type: "warning",
            exec: this.handleCopy
          }, {
            tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
            exec: this.handleDelete
          }
        ],
        statusFilters: [
          {text: this.$t('server.INVALID'), value: 'INVALID'},
          {text: this.$t('server.VALID'), value: 'VALID'},
          {text: this.$t('server.UNLINK'), value: 'UNLINK'},
          {text: this.$t('server.DELETE'), value: 'DELETE'}
        ],
        types: [
          {id: 'Linux', value: 'linux'},
          {id: 'Windows', value: 'windows'},
        ],
        serverGroupId: 'd691se79-2e8c-1s54-bbe6-491sd29e91fe',
        groups: [],
        proxyForm: {isProxy: false, proxyId: 0},
        proxys: [],
        certificates: [],
        keyFile: Object,
        innerAddCertificate: false,
        addCertificateForm: {
          isCertificate: false,
          isPublicKey: 'no',
        },
        batchBindCertificate: false,
        batchBindForm: {
          isCertificate: false,
          isPublicKey: 'no',
        },
        rowindex: 0,
        itemKey: Math.random(),
        checkedColumnNames: columnOptions.map((ele) => ele.props),
        columnNames: columnOptions,
        //名称搜索
        items: [
          {
            name: 'server.server_name',
            id: 'name'
          },
          {
            name: 'IP',
            id: 'ip'
          },
          {
            name: 'Port',
            id: 'port',
          },
          {
            name: 'server.server_user_name',
            id: 'userName',
          },
        ],
        checkAll: true,
        isIndeterminate: false,
        uploadExcelForm: {},
        fileList: [],
        excelFile: null,
        ruleGroups: [],
        scanVisible: false,
        checkedGroups: [],
        serverWithGroup: {pluginIcon: 'server.png'},
      }
    },
    props: {
      selectNodeIds: Array,
    },
    watch: {
      selectNodeIds() {
        this.search();
      },
      '$route': 'init',
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
      create() {
        this.servers = [];
        this.createVisible = true;
      },
      //查询代理
      activeProxy() {
        this.result = this.$get(proxyListAllUrl, response => {
          this.proxys = response.data;
        });
      },
      //查询证书下拉列表
      activeCertificates() {
        this.result = this.$get(allCertificateListUrl, response => {
          this.certificates = response.data;
        });
      },
      upload() {
        this.uploadExcelForm.serverGroupId = this.groups.length > 0 ? this.groups[0].id : this.serverGroupId;
        this.uploadExcel = true;
        this.excelFile = null;
        this.fileList = [];
      },
      //校验虚拟机ssh连接
      validate() {
        if (this.selectIds.size === 0) {
          this.$warning(this.$t('server.please_choose_server'));
          return;
        }
        this.$alert(this.$t('server.one_validate') + this.$t('server.server_setting') + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$request({
                method: 'POST',
                url: serverValidatesUrl,
                data: Array.from(this.selectIds),
                headers: {
                  'Content-Type': undefined
                }
              }, res => {
                if (res.data.length == 0) {
                  this.$success(this.$t('account.success'));
                } else {
                  let name = '';
                  for (let item of res.data) {
                    name = name + ' ' + item.server.name + ';';
                  }
                  this.$error(this.$t('server.failed_server') + name);
                }
                this.search();
              });
            }
          }
        });
      },
      deleteServers() {
        if (this.selectIds.size === 0) {
          this.$warning(this.$t('server.please_choose_server'));
          return;
        }
        this.$alert(this.$t('oss.delete_batch') + this.$t('server.server_1') + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$request({
                method: 'POST',
                url: deleteServersUrl,
                data: Array.from(this.selectIds),
                headers: {
                  'Content-Type': undefined
                }
              }, res => {
                this.$success(this.$t('commons.success'));
                this.search();
              });
            }
          }
        });
      },
      select(selection) {
        this.selectIds.clear();
        selection.forEach(s => {
          this.selectIds.add(s.id)
        });
      },
      validateStatus(row) {
        this.$alert(this.$t('server.validate') + this.$t('server.server_status') + ' : ' + row.name +  " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$post(serverValidateUrl + row.id, {}, response => {
                let data = response.data;
                if (data) {
                  if (data.flag) {
                    this.$success(this.$t('server.success'));
                  } else {
                    this.$error(data.message, 10000);
                  }
                } else {
                  this.$error(this.$t('server.error'));
                }
                this.search();
              });
            }
          }
        });
      },
      //查询列表
      search() {
        if(this.selectNodeIds.length!=0) {
          this.condition.serverGroupId = this.selectNodeIds[0];
          this.serverGroupId = this.selectNodeIds[0];
        } else {
          this.condition.serverGroupId = "";
        }
        let url = serverListUrl + this.currentPage + "/" + this.pageSize;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
      },
      initGroup() {
        this.result = this.$get(serverGroupListUrl, response => {
          if (response.data != undefined && response.data != null) {
            this.groups = response.data;
          }
        });
      },
      handleEdit(tmp) {
        this.form = tmp;
        this.updateVisible = true;
      },
      handleCopy (tmp) {
        this.form = tmp;
        this.copyVisible = true;
      },
      handleClose() {
        this.createVisible =  false;
        this.updateVisible =  false;
        this.copyVisible = false;
        this.uploadExcel = false;
      },
      handleDelete(obj) {
        this.$alert(this.$t('server.delete_confirm') + obj.name + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$get(deleteServerUrl + obj.id, response => {
                this.$success(this.$t('commons.delete_success'));
                this.search();
              });
            }
          }
        });
      },
      change(e) {
        this.$forceUpdate();
      },
      init() {
        this.selectIds.clear();
        this.search();
        this.initGroup();
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
      scanGroup () {
        let account = this.$t('server.one_scan') + this.$t('server.server_rule_group');
        this.$alert( account + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              if (this.checkedGroups.length === 0) {
                this.$warning(this.$t('account.please_choose_rule_group'));
                return;
              }
              let params = {
                accountId: this.serverWithGroup.id,
                groups: this.checkedGroups
              }
              this.groupResult = this.$post(serverScanUrl, params, () => {
                this.$success(this.$t('schedule.event_start'));
                this.scanVisible = false;
                this.$router.push({
                  path: '/server/result',
                }).catch(error => error);
              });
            }
          }
        });
      },
      openScanGroup(data) {
        if (data.status === 'INVALID') {
          this.$warning(this.$t('server.failed_server') + data.name);
          return;
        }
        this.serverWithGroup = data;
        this.result = this.$get(serverRuleGroupsByTypeUrl + data.type,response => {
          this.ruleGroups = response.data;
          this.scanVisible = true;
        });
      },
      rowClass() {
        return "text-align:center";
      },
      headClass() {
        return "text-align:center;background:'#ededed'";
      },
      removeServer(index, data) { //移除
        if (!data[index].id) {
          data.splice(index, 1)
        } else {
          data[parseInt(index)].isSet = false;
        }
      },
      handleAddServerModel() {
        let server = {};
        server.name = '';
        server.ip = '';
        server.port = '22';
        server.userName = 'root';
        server.type = 'linux';
        server.password = '';
        server.serverGroupId = this.serverGroupId;
        this.servers.push(server);
        this.proxyForm = {isProxy: false, proxyId: 0};
      },
      changeType(item) {
        if(item.type === 'linux') {
          item.port = '22';
          item.userName = 'root';
        }else if (item.type === 'windows'){
          item.port = '5985';
          item.userName = 'Administrator';
        }
      },
      deleteRowServer(index, data) { //删除
        this.servers.splice(index, 1);
      },
      bindCertificate(index, data) {
        this.rowindex = index;
        this.addCertificateForm = data;
        this.innerAddCertificate = true;
      },
      saveServer(servers) {
        //校验列表
        for (let server of servers) {
          if (!server.name) {
            this.$warning(this.$t('server.server_name_none'));
            return;
          } else if (!server.ip) {
            this.$warning(this.$t('server.server_ip_none'));
            return;
          } else if (!server.userName) {
            this.$warning(this.$t('server.server_username_none'));
            return;
          } else if (!server.port) {
            this.$warning(this.$t('server.server_port_none'));
            return;
          } else {
            //绑定手动凭证
            if (!server.isCertificate) {
              if (server.isPublicKey === "no" || !server.isPublicKey) {
                //密码不能为空
                if (!server.password) {
                  this.$warning(this.$t('server.server_password_none'));
                  return;
                }
              } else if (server.isPublicKey === "str") {
                //密钥文本不能为空
                if (!server.publicKey) {
                  this.$warning(this.$t('server.server_publickey_none'));
                  return;
                }
              } else if (server.isPublicKey === "file") {
                //密钥文件不能为空
                if (!server.publicKeyPath) {
                  this.$warning(this.$t('server.server_publickey_file_none'));
                  return;
                }
              }
            } else { //绑定统一凭证
              //绑定的统一凭证不能为空
              if (!server.certificateId) {
                this.$warning(this.$t('server.server_certificateId_none'));
                return;
              }
            }
          }
        }
        //保存列表
        let num = 0;
        for (let server of servers) {
          if (server.isPublicKey === null && server.password) {
            server.isPublicKey = "no";
          }
          let formData = new FormData();
          if (server.keyFile) {
            formData.append("keyFile", server.keyFile);
          }
          formData.append("request", new Blob([JSON.stringify(server)], {type: "application/json"}));
          let axiosRequestConfig = {
            method: "POST",
            url: addServerUrl,
            data: formData,
            headers: {
              "Content-Type": 'multipart/form-data'
            }
          };
          this.rstResult = this.$request(axiosRequestConfig, () => {
            this.$success(this.$t('commons.save_success'));
            this.search();
            num++;
            if(servers.length === num) {
              this.createVisible = false;
            }
          });
        }
      },
      editServer(server) {
        if (!server.isProxy) {
          server.proxyId = null;
        }
        let formData = new FormData();
        if (this.keyFile) {
          formData.append("keyFile", this.keyFile);
        }
        formData.append("request", new Blob([JSON.stringify(server)], {type: "application/json"}));
        let axiosRequestConfig = {
          method: "POST",
          url: editServerUrl,
          data: formData,
          headers: {
            "Content-Type": 'multipart/form-data'
          }
        };
        this.rstResult = this.$request(axiosRequestConfig, (res) => {
          if (res.success) {
            this.$success(this.$t('commons.save_success'));
            this.search();
            this.updateVisible = false;
          }
        });
      },
      copyServer (server) {
        if (!server.isProxy) {
          server.proxyId = null;
        }
        let formData = new FormData();
        if (this.keyFile) {
          formData.append("keyFile", this.keyFile);
        }
        formData.append("request", new Blob([JSON.stringify(server)], {type: "application/json"}));
        let axiosRequestConfig = {
          method: "POST",
          url: copyServerUrl,
          data: formData,
          headers: {
            "Content-Type": 'multipart/form-data'
          }
        };
        this.rstResult = this.$request(axiosRequestConfig, (res) => {
          if (res.success) {
            this.$success(this.$t('commons.save_success'));
            this.search();
            this.copyVisible = false;
          }
        });
      },
      append(file) {
        this.keyFile = file;
      },
      batchBind() {
        this.batchBindForm = {
          isCertificate: false,
          isPublicKey: 'no',
        };
        this.batchBindCertificate = true;
      },
      innerCertificateClose() {
        this.innerAddCertificate = false;
        this.batchBindCertificate = false;
      },
      saveCertificate(item, index) {
        let index_ = 0;
        for (let row of this.servers) {
          if(index_ === index) {
            row.isCertificate = item.isCertificate ? item.isCertificate : false;
            if (item.isCertificate) {
              for (let certificate of this.certificates) {
                if(item.certificateId === certificate.id) {
                  row.certificateId = item.certificateId ? item.certificateId : '';
                  row.isPublicKey = certificate.isPublicKey ? certificate.isPublicKey : '';
                  row.password = certificate.password ? certificate.password : '';
                  row.publicKey = certificate.publicKey ? certificate.publicKey : '';
                  row.publicKeyPath = certificate.publicKeyPath ? certificate.publicKeyPath : '';
                  break;
                }
              }
            } else {
              row.certificateId = item.certificateId ? item.certificateId : '';
              row.isPublicKey = item.isPublicKey ? item.isPublicKey : '';
              row.password = item.password ? item.password : '';
              row.publicKey = item.publicKey ? item.publicKey : '';
              row.publicKeyPath = item.publicKeyPath ? item.publicKeyPath : '';
            }
            row.port = item.port ? item.port : '';
            row.userName = item.userName ? item.userName : '';
            row.keyFile = item.keyFile ? item.keyFile : '';
            break;
          }
          index_++;
        }
        // 在tableData赋值的地方，顺便随机设置下key，页面就会刷新了
        this.itemKey = Math.random();
        this.innerCertificateClose();
      },
      saveBatchBind(item) {
        if (this.servers.length > 0) {
          for (let row of this.servers) {
            row.isCertificate = item.isCertificate?item.isCertificate:false;
            row.certificateId = item.certificateId?item.certificateId:'';
            row.isPublicKey = item.isPublicKey?item.isPublicKey:'';
            row.password = item.password?item.password:'';
            row.publicKey = item.publicKey?item.publicKey:'';
            row.publicKeyPath = item.publicKeyPath?item.publicKeyPath:'';
            row.port = item.port?item.port:'';
            row.type = item.type?item.type:'';
            row.userName = item.userName?item.userName:'';
            row.keyFile = item.keyFile?item.keyFile:'';
          }
          this.$success(this.$t('server.batch_success'));
          this.innerCertificateClose();
        } else {
          this.$warning(this.$t('server.batch_error'));
        }
      },
      // 上传前对文件的大小的判断
      beforeAvatarUpload(file, fileList) {
        this.fileList = fileList;
        this.excelFile = file;
        const extension = file.name.split('.')[1] === 'xls';
        const extension2 = file.name.split('.')[1] === 'xlsx';
        const isLt2M = file.size / 1024 / 1024 < 10;
        if (!extension && !extension2) {
          this.$message({
            message: this.$t('server.upload_template_note'),
            type: 'error'
          });
        }
        return extension || extension2 || isLt2M;
      },
      async uploadServerExcel() {
        const File = this.excelFile.raw;
        const formData = new FormData();
        formData.append('file', File);
        formData.append("request", new Blob([JSON.stringify(this.uploadExcelForm)], {type: "application/json"}));

        const loadingInstance = this.$loading({ text: this.$t('server.uploading') });

        let axiosRequestConfig = {
          method: "POST",
          url: serverExcelInsertExpertsUrl,
          data: formData,
          headers: {
            "Content-Type": "multipart/form-data"
          }
        };

        try {
          await this.$request(axiosRequestConfig, () => {
            this.$success(this.$t('commons.save_success'));
            this.uploadExcel = false;
            this.excelFile = null;
            this.search();
          });
          loadingInstance.close();
        } catch (e) {
          loadingInstance.close();
          this.$error(e, 10000);
        }
      },
      downloadExcel() {
        this.$fileDownload(serverDownloadExcelUrl, response => {
          let blob = new Blob([response.data], {type: "'application/octet-stream'"});
          saveAs(blob, "template.xlsx");
        }, error => {
          console.log("导出报错", error);
        });
      },
      handleCheckAllByAccount() {
        if (this.checkedGroups.length === this.ruleGroups.length) {
          this.checkedGroups = [];
        } else {
          let arr = [];
          this.checkedGroups = [];
          for (let group of this.ruleGroups) {
            arr.push(group.id);
          }
          let concatArr = this.checkedGroups.concat(arr);
          this.checkedGroups = Array.from(concatArr);
        }
      },
    },
    activated () {
      this.init();
      this.activeProxy();
      this.activeCertificates();
    },
  }
</script>

<style scoped>
  .table-content {
    width: 100%;
  }
  .rtl >>> .el-drawer__header {
    margin-bottom: 0;
  }

  .el-table {
    cursor: pointer;
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
  .code-mirror {
    height: 600px !important;
  }
  .code-mirror >>> .CodeMirror {
    /* Set height, width, borders, and global font properties here */
    height: 600px !important;
  }
  /deep/ :focus{outline:0;}
  .el-box-card {
    margin: 10px 0;
  }
  .table-card >>> .search {
    width: 350px !important;
  }
  .table-card >>> .search .el-input {
    width: 110px !important;
  }
  .table-card >>> .search .el-input-group__append {
    padding: 0 15px;
  }
  .iconfont {
    margin: 1px 3px 0 0;
    width: 24px;
    height: 18px;
    text-align: center;
    font-size: 12px;
  }
  .el-box-card {
    margin: 10px 0;
  }
  .el-box-card >>> .el-checkbox {
    margin: 5px 0;
  }
</style>
