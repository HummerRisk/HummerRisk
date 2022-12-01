<template>
    <main-container>
      <el-card class="table-card" v-loading="result.loading">
        <template v-slot:header>
          <table-header :condition.sync="condition" @search="search"
                        :title="$t('server.server_list')" class="table-header-l"
                        @create="create" :createTip="$t('server.server_create')"
                        @scan="scan" :scanTip="$t('server.one_scan')"
                        @validate="validate" :validateTip="$t('server.one_validate')"
                        :show-validate="true" :show-scan="true" :show-create="true"
                        :items="items" :columnNames="columnNames" :show-open="false"
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
          <el-table-column prop="userName" v-if="checkedColumnNames.includes('userName')" :label="$t('server.server_user_name')" min-width="100" show-overflow-tooltip></el-table-column>
          <el-table-column prop="status" v-if="checkedColumnNames.includes('status')" min-width="100" :label="$t('server.server_status')"
                           column-key="status"
                           :filters="statusFilters"
                           :filter-method="filterStatus">
            <template v-slot:default="{row}">
              <div @click="validateStatus(row)">
                <el-tag size="mini" type="warning" v-if="row.status === 'DELETE'">
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
          <el-table-column min-width="160" v-if="checkedColumnNames.includes('updateTime')" :label="$t('account.update_time')" sortable
                           prop="updateTime">
            <template v-slot:default="scope">
              <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="user" v-if="checkedColumnNames.includes('user')" :label="$t('account.creator')" min-width="110" show-overflow-tooltip/>
          <el-table-column min-width="140" :label="$t('commons.operating')" fixed="right">
            <template v-slot:default="scope">
              <table-operators :buttons="buttons" :row="scope.row"/>
            </template>
          </el-table-column>
        </hide-table>
        <table-pagination :change="search" :current-page.sync="currentPage" :page-size.sync="pageSize" :total="total"/>
      </el-card>

      <!--Create server-->
      <el-drawer class="rtl" :title="$t('server.server_create')" :visible.sync="createVisible" size="70%" :before-close="handleClose" :direction="direction"
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
                  <el-table-column :label="$t('server.server_name')" min-width="20%" prop="serverName">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.name"></el-input>
                    </template>
                  </el-table-column>
                  <el-table-column :label="'IP'" prop="ip" min-width="20%">
                    <template v-slot:default="{row}">
                      <el-input v-model="row.ip"></el-input>
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('server.port')" min-width="10%" prop="port">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.port"></el-input>
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('server.server_user_name')" min-width="20%" prop="userName">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.userName"></el-input>
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('commons.certificate')" min-width="15%" prop="password">
                    <template slot-scope="scope">
                      <el-tooltip v-if="scope.row.isPublicKey==='no'" class="item" effect="dark" :content="scope.row.password" placement="left-start">
                        <el-button type="success" size="mini">{{ $t('server.no_public_key') }}</el-button>
                      </el-tooltip>
                      <el-tooltip v-if="scope.row.isPublicKey==='str'" class="item" effect="dark" :content="scope.row.publicKey" placement="left-start">
                        <el-button type="success" size="mini">{{ $t('server.str_public_key') }}</el-button>
                      </el-tooltip>
                      <el-tooltip v-if="scope.row.isPublicKey==='file'" class="item" effect="dark" :content="scope.row.publicKeyPath" placement="left-start">
                        <el-button type="success" size="mini">{{ $t('server.file_public_key') }}</el-button>
                      </el-tooltip>
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
              <el-form-item :label="$t('server.port')" ref="type" prop="port" :rules="{required: true, message: $t('server.port') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-input v-model="addCertificateForm.port"></el-input>
              </el-form-item>
              <el-form-item :label="$t('server.server_user_name')" ref="type" prop="userName" :rules="{required: true, message: $t('server.server_user_name') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-input v-model="addCertificateForm.userName"></el-input>
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
                <el-input type="password" v-model="addCertificateForm.password" autocomplete="off" :placeholder="$t('commons.password')" show-password/>
              </el-form-item>
              <el-form-item v-if="!addCertificateForm.isCertificate && addCertificateForm.isPublicKey === 'str'" :label="$t('server.public_key')" ref="password">
                <el-input type="textarea" :rows="10" v-model="addCertificateForm.publicKey" autocomplete="off" :placeholder="$t('server.public_key')"/>
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
              <el-form-item :label="$t('server.port')" ref="type" prop="port" :rules="{required: true, message: $t('server.port') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-input v-model="batchBindForm.port"></el-input>
              </el-form-item>
              <el-form-item :label="$t('server.server_user_name')" ref="type" prop="userName" :rules="{required: true, message: $t('server.server_user_name') + $t('commons.cannot_be_empty'), trigger: 'change'}">
                <el-input v-model="batchBindForm.userName"></el-input>
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
                <el-input type="password" v-model="batchBindForm.password" autocomplete="off" :placeholder="$t('commons.password')" show-password/>
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
            <el-form-item :label="$t('server.server_group_name')" ref="groupId" prop="groupId">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="form.groupId" :placeholder="$t('server.server_group_name')">
                <el-option
                  v-for="item in groups"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('server.server_name')" ref="name" prop="name">
              <el-input v-model="form.name" autocomplete="off" :placeholder="$t('server.server_name')"/>
            </el-form-item>
            <el-form-item :label="'IP'" ref="ip" prop="ip">
              <el-input v-model="form.ip" autocomplete="off" :placeholder="'IP'"/>
            </el-form-item>
            <el-form-item :label="$t('server.port')" ref="port" prop="port">
              <el-input type="number" v-model="form.port" autocomplete="off" :placeholder="$t('server.port')"/>
            </el-form-item>
            <el-form-item :label="$t('server.server_user_name')" ref="userName" prop="userName">
              <el-input v-model="form.userName" autocomplete="off" :placeholder="$t('server.server_user_name')"/>
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
              <el-input type="password" v-model="form.password" autocomplete="off" :placeholder="$t('commons.password')" show-password/>
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
            <el-form-item :label="$t('server.server_group_name')" ref="groupId" prop="groupId">
              <el-select style="width: 100%;" filterable :clearable="true" v-model="form.groupId" :placeholder="$t('server.server_group_name')">
                <el-option
                  v-for="item in groups"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('server.server_name')" ref="name" prop="name">
              <el-input v-model="form.name" autocomplete="off" :placeholder="$t('server.server_name')"/>
            </el-form-item>
            <el-form-item :label="'IP'" ref="ip" prop="ip">
              <el-input v-model="form.ip" autocomplete="off" :placeholder="'IP'"/>
            </el-form-item>
            <el-form-item :label="$t('server.port')" ref="port" prop="port">
              <el-input type="number" v-model="form.port" autocomplete="off" :placeholder="$t('server.port')"/>
            </el-form-item>
            <el-form-item :label="$t('server.server_user_name')" ref="userName" prop="userName">
              <el-input v-model="form.userName" autocomplete="off" :placeholder="$t('server.server_user_name')"/>
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
              <el-input type="password" v-model="form.password" autocomplete="off" :placeholder="$t('commons.password')" show-password/>
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

    </main-container>
</template>

<script>
import TablePagination from "../../common/pagination/TablePagination";
import TableHeader from "@/business/components/common/components/TableHeader";
import TableOperator from "../../common/components/TableOperator";
import Container from "../../common/components/Container";
import MainContainer from "../../common/components/MainContainer";
import TableOperators from "../../common/components/TableOperators";
import {_filter, _sort} from "@/common/js/utils";
import {SERVER_CONFIGS} from "../../common/components/search/search-components";
import DialogFooter from "@/business/components/common/components/DialogFooter";
import ServerKeyUpload from "@/business/components/server/head/ServerKeyUpload";
import HideTable from "@/business/components/common/hideTable/HideTable";

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
    label: 'server.server_user_name',
    props: 'userName',
    disabled: false
  },
  {
    label: 'server.server_status',
    props: 'status',
    disabled: false
  },
  {
    label: 'account.update_time',
    props: 'updateTime',
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
        },
        buttons: [
          {
            tip: this.$t('commons.edit'), icon: "el-icon-edit", type: "primary",
            exec: this.handleEdit
          }, {
            tip: this.$t('commons.copy'), icon: "el-icon-document-copy", type: "success",
            exec: this.handleCopy
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
        groupId: 'd691se79-2e8c-1s54-bbe6-491sd29e91fe',
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
        let url = "/proxy/list/all";
        this.result = this.$get(url, response => {
          this.proxys = response.data;
        });
      },
      //查询证书下拉列表
      activeCertificates() {
        let url = "/server/allCertificateList";
        this.result = this.$get(url, response => {
          this.certificates = response.data;
        });
      },
      download() {},
      upload() {},
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
              let formData = new FormData();
              this.result = this.$request({
                method: 'POST',
                url: "/server/validate",
                data: Array.from(this.selectIds),
                headers: {
                  'Content-Type': undefined
                }
              }, res => {
                if (res.data) {
                  this.$success(this.$t('account.success'));
                } else {
                  this.$error(this.$t('account.error'));
                }
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
              this.result = this.$post("/server/validate/" + row.id, {}, response => {
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
          this.condition.groupId = this.selectNodeIds[0];
          this.groupId = this.selectNodeIds[0];
        } else {
          this.condition.groupId = "";
        }
        let url = "/server/serverList/" + this.currentPage + "/" + this.pageSize;
        this.result = this.$post(url, this.condition, response => {
          let data = response.data;
          this.total = data.itemCount;
          this.tableData = data.listObject;
        });
      },
      handleEdit(tmp) {
        let url = "/server/serverGroupList";
        this.result = this.$get(url, response => {
          if (response.data != undefined && response.data != null) {
            this.groups = response.data;
          }
        });
        this.form = tmp;
        this.updateVisible = true;
      },
      handleCopy (tmp) {
        let url = "/server/serverGroupList";
        this.result = this.$get(url, response => {
          if (response.data != undefined && response.data != null) {
            this.groups = response.data;
          }
        });
        this.form = tmp;
        this.copyVisible = true;
      },
      handleClose() {
        this.createVisible =  false;
        this.updateVisible =  false;
        this.copyVisible = false;
      },
      handleDelete(obj) {
        this.$alert(this.$t('server.delete_confirm') + obj.name + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$get("/server/deleteServer/" + obj.id, response => {
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
      scan (){
        if (this.selectIds.size === 0) {
          this.$warning(this.$t('server.please_choose_server'));
          return;
        }
        this.$alert(this.$t('server.one_scan') + this.$t('server.server_rule') + " ？", '', {
          confirmButtonText: this.$t('commons.confirm'),
          callback: (action) => {
            if (action === 'confirm') {
              this.result = this.$request({
                method: 'POST',
                url: "/server/scan",
                data: Array.from(this.selectIds),
                headers: {
                  'Content-Type': undefined
                }
              }, res => {
                if (res.data) {
                  this.$success(this.$t('schedule.event_start'));
                } else {
                  this.$error(this.$t('schedule.event_failed'));
                }
                this.$router.push({
                  path: '/server/result',
                }).catch(error => error);
              });
            }
          }
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
        server.password = '';
        server.groupId = this.groupId;
        this.servers.push(server);
        this.proxyForm = {isProxy: false, proxyId: 0};
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
        for (let server of servers) {
          if(!server.name || !server.ip || !server.userName || !server.groupId) {
            this.$warning('Value will not be null');
            return;
          } else {
            if (!server.isCertificate && server.isPublicKey !== "no") {
              if (!server.password) {
                this.$warning('Password will not be null');
                return;
              }
              if (server.isPublicKey === null && server.password) {
                server.isPublicKey = "no";
              }
            }
            // if (this.proxyForm.isProxy) {
            //   server.isProxy = true;
            //   server.proxyId = this.proxyForm.proxyId;
            // } else {
            //   server.isProxy = false;
            //   server.proxyId = null;
            // }
            let formData = new FormData();
            if (server.keyFile) {
              formData.append("keyFile", server.keyFile);
            }
            formData.append("request", new Blob([JSON.stringify(server)], {type: "application/json"}));
            let axiosRequestConfig = {
              method: "POST",
              url: "/server/addServer",
              data: formData,
              headers: {
                "Content-Type": 'multipart/form-data'
              }
            };
            this.rstResult = this.$request(axiosRequestConfig, () => {
              this.$success(this.$t('commons.save_success'));
              this.search();
              this.createVisible = false;
            });
          }
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
          url: "/server/editServer",
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
          url: "/server/copyServer",
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
            row.isCertificate = item.isCertificate ? item.isCertificate : '';
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
            row.isCertificate = item.isCertificate?item.isCertificate:'';
            row.certificateId = item.certificateId?item.certificateId:'';
            row.isPublicKey = item.isPublicKey?item.isPublicKey:'';
            row.password = item.password?item.password:'';
            row.publicKey = item.publicKey?item.publicKey:'';
            row.publicKeyPath = item.publicKeyPath?item.publicKeyPath:'';
            row.port = item.port?item.port:'';
            row.userName = item.userName?item.userName:'';
            row.keyFile = item.keyFile?item.keyFile:'';
          }
          this.$success(this.$t('server.batch_success'));
          this.innerCertificateClose();
        } else {
          this.$warning(this.$t('server.batch_error'));
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
    width: 300px !important;
  }
  .table-card >>> .search .el-input {
    width: 90px !important;
  }
</style>
