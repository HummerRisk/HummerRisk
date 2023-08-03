<template>
  <div>
    <el-popover
      ref="popover"
      placement="left"
      width="700"
      trigger="hover">
      <el-table :border="true" :stripe="true" :data="regions" class="adjust-table table-content">
        <el-table-column type="index" min-width="60"/>
        <el-table-column prop="regionId" :label="$t('account.region_id')" min-width="250">
          <template v-slot:default="scope">
            {{ scope.row.regionId }}
          </template>
        </el-table-column>
        <el-table-column prop="regionName" :label="$t('account.region_name')" min-width="250">
          <template v-slot:default="scope">
            {{ scope.row.regionName }}
          </template>
        </el-table-column>
        <el-table-column prop="check" :label="$t('account.check_regions')" min-width="250">
          <template v-slot:default="scope">
            <el-checkbox v-if="scope.row.check" v-model="scope.row.check">{{ $t('account.checked') }}</el-checkbox>
            <el-checkbox v-if="!scope.row.check" v-model="scope.row.check">{{ $t('account.not_checked') }}</el-checkbox>
          </template>
        </el-table-column>
      </el-table>
      <el-button slot="reference" size="mini" type="primary" plain @click="showRegions">
        {{ $t('account.regions') }}
      </el-button>
    </el-popover>

    <!--regions-->
    <el-drawer class="rtl" :title="$t('account.all_regions')" :visible.sync="regionsVisible" size="50%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <div style="color: red;font-style:italic;margin: 5px 0 10px 10px;">
        <el-row>
          <el-col :span="16">
            {{ $t('account.checked_note') }}
          </el-col>
          <el-col :span="8">
            <el-button style="float: right; padding: 3px 50px" type="text" @click="handleCheckAllByAccount">{{ $t('account.i18n_sync_all') }}</el-button>
          </el-col>
        </el-row>
      </div>
      <el-table :border="true" :stripe="true" :data="regions" class="adjust-table table-content">
        <el-table-column type="index" min-width="60"/>
        <el-table-column prop="regionId" :label="$t('account.region_id')" min-width="250">
          <template v-slot:default="scope">
            {{ scope.row.regionId }}
          </template>
        </el-table-column>
        <el-table-column prop="regionName" :label="$t('account.region_name')" min-width="250">
          <template v-slot:default="scope">
            {{ scope.row.regionName }}
          </template>
        </el-table-column>
        <el-table-column prop="check" :label="$t('account.check_regions')" min-width="250">
          <template v-slot:default="scope">
            <el-checkbox v-if="scope.row.check" v-model="scope.row.check">{{ $t('account.checked') }}</el-checkbox>
            <el-checkbox v-if="!scope.row.check" v-model="scope.row.check">{{ $t('account.not_checked') }}</el-checkbox>
          </template>
        </el-table-column>
      </el-table>
      <dialog-footer style="margin-top: 10px;"
        @cancel="regionsVisible = false"
        @confirm="saveCheckRegion()"/>
    </el-drawer>
    <!--regions-->
  </div>
</template>

<script>
  /* eslint-disable */
  import DialogFooter from "@/business/components/common/components/DialogFooter.vue";
  import {checkRegionsUrl} from "@/api/cloud/account/account";

  export default {
    name: "Regions",
    components: {DialogFooter},
    props: {
      row: Object,
    },
    watch: {
      row() {
        this.init();
      },
    },
    data() {
      return {
        regions: [],
        regionsVisible: false,
        direction: 'rtl',
        account: '',
      }
    },
    created() {
      this.init();
    },
    methods: {
      init() {
        this.account = this.row;
        let regions = this.row.regions?typeof(this.row.regions) === 'string'?JSON.parse(this.row.regions):this.row.regions:[];
        let checkRegions = this.row.checkRegions?typeof(this.row.checkRegions) === 'string'?JSON.parse(this.row.checkRegions):this.row.checkRegions:[];
        for (let region of regions) {
          for (let checkRegion of checkRegions) {
            if (region.regionId === checkRegion.regionId) {
              region.check = true;
            }
          }
        }
        this.regions = regions;
      },
      showRegions() {
        this.regionsVisible =  true;
      },
      handleClose() {
        this.regionsVisible =  false;
      },
      saveCheckRegion() {
        let checkRegions = [];
        for (let region of this.regions) {
          if (region.check) {
            checkRegions.push(region);
          }
        }
        let params = this.account;
        params.checkRegions = JSON.stringify(checkRegions);
        this.$post(checkRegionsUrl, params, response => {
          this.$success(this.$t('commons.success'));
          this.handleClose();
        });
      },
      handleCheckAllByAccount() {
        this.regions.forEach(function (e) {
          if (e.check) {
            e.check = false;
          } else {
            e.check = true;
          }
        });
      },
    },
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
