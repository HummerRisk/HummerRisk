<template>
  <el-main :width="width" class="main-container">
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
      <el-menu-item index="1">{{ $t('dashboard.type') }}</el-menu-item>
      <el-menu-item index="2">{{ $t('dashboard.setting') }}</el-menu-item>
    </el-menu>
    <div v-if="activeIndex==='1'">
      <el-row align="middle">
        <div class="block">
          <span class="descriptions__title">{{ $t('dashboard.exhibit') }}</span>
          <el-divider><i class="el-icon-data-analysis"></i></el-divider>
        </div>
        <el-col :span="10" v-for="(exhibit, index) in exhibits" :key="index" style="margin: 4%;">
          <el-card :body-style="{ padding: '10px', 'text-align': 'center' }" shadow="always">
            <el-image class="logo" :src="require(`@/assets/img/analysis/${exhibit.pluginIcon}`)"/>
            <div style="text-align: center;">
              <span><el-checkbox v-model="activePluginIcon">{{ exhibit.name }}</el-checkbox></span>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-row>
        <div class="block">
          <span class="descriptions__title">{{ $t('dashboard.color') }}</span>
          <el-divider><i class="el-icon-thumb"></i></el-divider>
        </div>
        <div class="block">
          <el-row>
            <el-col :span="12">
              <el-color-picker v-model="color1"></el-color-picker>
            </el-col>
            <el-col :span="12">
              <span style="font-size: 30px;">{{ color1 }}</span>
            </el-col>
          </el-row>
        </div>
      </el-row>
      <div class="dialog-footer">
        <el-button @click="cancel">{{ $t('dashboard.reset') }}</el-button>
        <el-button type="primary" @click="confirm">{{ $t('dashboard.qu_save') }}</el-button>
      </div>
    </div>
    <div v-if="activeIndex==='2'">
      <el-row align="middle" style="margin: 10px;">
        <el-form ref="form" :model="sizeForm" label-width="80px" size="mini">
          <el-form-item :label="$t('dashboard.analysis_cycle')">
            <el-input type="number" max="30" v-model="sizeForm.cycle" clearable :placeholder="$t('dashboard.analysis_cycle_placeholder')"></el-input>
          </el-form-item>
          <el-form-item :label="$t('dashboard.scan_users')">
            <el-select v-model="sizeForm.users" :placeholder="$t('dashboard.scan_users')" multiple>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('dashboard.scan_types')">
            <el-select v-model="sizeForm.types" :placeholder="$t('dashboard.scan_types')">
              <el-option :label="$t('dashboard.cloud_scan')" value="cloud_scan"></el-option>
              <el-option :label="$t('dashboard.vuln_scan')" value="vuln_scan"></el-option>
              <el-option :label="$t('dashboard.server_scan')" value="server_scan"></el-option>
              <el-option :label="$t('dashboard.package_scan')" value="package_scan"></el-option>
              <el-option :label="$t('dashboard.image_scan')" value="image_scan"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('dashboard.scan_account')">
            <el-select v-model="sizeForm.accounts" :placeholder="$t('dashboard.scan_account')" multiple>
            </el-select>
          </el-form-item>
          <el-form-item size="large">
            <el-button type="primary" @click="confirm">{{ $t('dashboard.qu_save') }}</el-button>
            <el-button @click="cancel">{{ $t('dashboard.reset') }}</el-button>
          </el-form-item>
        </el-form>
      </el-row>
    </div>
  </el-main>
</template>

<script>
/* eslint-disable */
    export default {
      name: "AsideContainer",
      props: {
        width: {
          type: String,
          default: '24%'
        },
        enableAsideHidden: {
          type: Boolean,
          default: true
        },
      },
      data() {
        return {
          asideHidden: false,
          activeIndex: '1',
          exhibits: [
            {name: this.$t('dashboard.basic_bar_chart'), pluginIcon: 'basic-bar.png'},
            {name: this.$t('dashboard.backgroud_bar_chart'), pluginIcon: 'backgroud-bar.png'},
            {name: this.$t('dashboard.tick_bar_chart'), pluginIcon: 'tick-bar.png'},
            {name: this.$t('dashboard.area_bar_chart'), pluginIcon: 'basic-area.png'},
            {name: this.$t('dashboard.basic_line_chart'), pluginIcon: 'basic-line.png'},
            {name: this.$t('dashboard.smooted_line_chart'), pluginIcon: 'smooted-line.png'},
            {name: this.$t('dashboard.stacked_line_chart'), pluginIcon: 'stacked-line.png'},
            {name: this.$t('dashboard.label_pie_chart'), pluginIcon: 'label-pie.png'},

          ],
          color1: '#409EFF',
          sizeForm: {},
          activePluginIcon: 'basic-bar.png',
        }
      },
      methods: {
        handleSelect(key, keyPath) {
          this.activeIndex = key;
        },
        cancel() {
        },
        confirm() {
        }
      }
    }
</script>

<style scoped>
  .el-menu-demo {
    margin-left: 10px;
  }
  .main-container {
    border: 1px solid #e6e6e6;
    padding: 10px 0 0 0;
    border-radius: 2px;
    box-sizing: border-box;
    background-color: #FFF;
    min-height: 1168px;
    border-left-color: #e8a97e;
    border-left-width: 3px;
  }
  .descriptions__title {
    font-size: 16px;
    font-weight: 700;
  }
  .block {
    margin: 10px 10px 10px 30px;
  }
  .logo {
    width: 88px;
    height: 66px;
  }
  .dialog-footer {
    text-align: center;
  }
</style>
