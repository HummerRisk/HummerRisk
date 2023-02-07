<template>
  <div>
    <el-button v-if="type==='NO-SCAN'" slot="reference" size="mini" type="info" plain>
      {{ $t('resource.uncheck') }}
    </el-button>
    <el-button v-if="type==='NO-RISK'" slot="reference" size="mini" type="success" plain>
      {{ $t('resource.n_risk') }}
    </el-button>
    <el-button v-if="type==='HAVE-RISK'" slot="reference" size="mini" type="danger" plain @click="showRegions">
      {{ $t('resource.have_risk') }}
    </el-button>

    <!--regions-->
    <el-drawer class="rtl" :title="$t('resource.vuln_statistics')" :visible.sync="regionsVisible" size="70%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-table :border="true" :stripe="true" :data="string2PrettyFormat" class="adjust-table table-content">
        <el-table-column v-slot:default="scope" :label="$t('resource.i18n_task_type')" min-width="15%"
                         show-overflow-tooltip>
                <span>
                  <template v-for="tag in tagSelect">
                    <span :key="tag.value" v-if="scope.row.ruleTags">
                      <span :key="tag.tagKey" v-if="scope.row.ruleTags.indexOf(tag.tagKey) > -1"> {{
                          tag.tagName
                        }}</span>
                    </span>
                  </template>
                  <span v-if="!!scope.row.resourceTypes && scope.row.resourceTypes.indexOf('.')===-1"> {{
                      scope.row.resourceTypes
                    }}</span>
                  <span v-if="!!scope.row.resourceTypes && scope.row.resourceTypes.indexOf('.')>-1">
                    <template v-for="type in resourceTypes">
                      <span :key="type.value" v-if="scope.row.resourceTypes">
                        <span :key="type.value" v-if="scope.row.resourceTypes.indexOf(type.value) > -1"> [{{
                            type.value
                          }}]</span>
                      </span>
                    </template>
                  </span>
                </span>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('rule.rule_name')" min-width="18%" show-overflow-tooltip>
          <el-link type="primary" :underline="false" class="md-primary text-click" @click="showTaskDetail(scope.row)">
            {{ scope.row.taskName }}
          </el-link>
        </el-table-column>
        <el-table-column v-slot:default="scope" :label="$t('rule.severity')" min-width="8%"
                         :sort-by="['HighRisk', 'MediumRisk', 'LowRisk']" prop="severity" :sortable="true"
                         show-overflow-tooltip>
          <span v-if="scope.row.severity == 'HighRisk'" style="color: #f84846;"> {{ $t('rule.HighRisk') }}</span>
          <span v-else-if="scope.row.severity == 'MediumRisk'" style="color: #fe9636;">{{ $t('rule.MediumRisk') }}</span>
          <span v-else-if="scope.row.severity == 'LowRisk'" style="color: #4dabef;"> {{ $t('rule.LowRisk') }}</span>
          <span v-else> N/A</span>
        </el-table-column>

        <el-table-column v-slot:default="scope" :label="$t('resource.status_on_off')" prop="returnSum" sortable
                         show-overflow-tooltip min-width="8%">
          <span style="color: #f84846;">{{ $t('resource.i18n_compliance_false') }}</span>
        </el-table-column>
      </el-table>
    </el-drawer>
    <!--regions-->
  </div>
</template>

<script>
/* eslint-disable */
import {getCurrentAccountID} from "@/common/js/utils";
import {ACCOUNT_ID, ACCOUNT_NAME} from "@/common/js/constants";

export default {
    name: "ResourceType",
    props: {
      hummerId: String,
      riskCount: Number,
      accountId: String,
      regionId: String,
      resourceType: String,
      accountName: String,
    },
    data() {
      return {
        string2PrettyFormat: [],
        regionsVisible: false,
        direction: 'rtl',
        type: 'NO-SCAN',
        tagSelect: [],
        detailVisible:false,
        detailForm:{}
      }
    },
    created() {

    },
    methods: {
      async initSelect() {
        this.tagSelect = [];
        await this.$get("/tag/rule/list", response => {
          this.tagSelect = response.data;
        });
        this.resourceTypes = [];
        await this.$get("/rule/all/resourceTypes", response => {
          for (let item of response.data) {
            let typeItem = {};
            typeItem.value = item.name;
            typeItem.label = item.name;
            this.resourceTypes.push(typeItem);
          }
        });
        if (!!getCurrentAccountID()) {
          this.accountId = getCurrentAccountID();
        }
      },
      getType(){
        this.type = "NO-SCAN"
        if(this.riskCount && this.riskCount > 0){
          this.type = "HAVE-RISK"
        }else{
          this.result = this.$get("/cloud/resource/task/count/" +  this.accountId+"/"+this.regionId+"/"+this.resourceType,response => {
            if(response.data > 0){
              this.type = "NO-RISK"
            }else{
              this.type = "NO-SCAN"
            }
          });
        }
      },
      showTaskDetail(item) {
        localStorage.setItem(ACCOUNT_ID, item.accountId);
        localStorage.setItem(ACCOUNT_NAME, this.accountName);
        this.$router.push({
          path: '/account/result',
        }).catch(error => error);
        this.detailForm = {};
        this.$get("/cloud/task/detail/" + item.id, response => {
          if (response.success) {
            this.detailForm = response.data;
            this.detailVisible = true;
          }
        });
      },
      showRegions() {
        this.initSelect();
        this.result = this.$get("/cloud/resource/risk/list/"+this.regionId+"/"+  this.hummerId,response => {
          this.string2PrettyFormat = response.data
          this.regionsVisible =  true;
        });
      },
      handleClose() {
        this.regionsVisible =  false;
        this.detailVisible = false;

      },
    },
    watch:{
      hummerId:{
        handler(newVal, oldVal) {
          this.getType()
        },
        // 立即处理 进入页面就触发
        immediate: true
      }
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
    width: 60%;
  }
  .code-mirror {
    height: 600px !important;
  }
  .code-mirror >>> .CodeMirror {
    /* Set height, width, borders, and global font properties here */
    height: 600px !important;
  }
  /deep/ :focus{outline:0;}
</style>
