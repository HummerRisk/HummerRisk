<template>
  <el-main :width="width" class="main-container">
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
      <el-menu-item index="1">{{ $t('dashboard.type') }}</el-menu-item>
      <el-menu-item index="2">{{ $t('dashboard.setting') }}</el-menu-item>
    </el-menu>
    <el-row align="middle">
      <div class="block">
        <span class="descriptions__title">{{ $t('dashboard.exhibit') }}</span>
        <el-divider><i class="el-icon-data-analysis"></i></el-divider>
      </div>
      <el-col :span="10" v-for="(exhibit, index) in exhibits" :key="index" style="margin: 4%;">
        <el-card :body-style="{ padding: '10px', 'text-align': 'center' }" shadow="always">
          <el-image class="logo" :src="require(`@/assets/img/analysis/${exhibit.pluginIcon}`)"/>
          <div style="text-align: center;">
            <span><el-checkbox>{{ exhibit.name }}</el-checkbox></span>
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
      <el-button @click="cancel">{{ $t('commons.cancel') }}</el-button>
      <el-button type="primary" @click="confirm">{{ $t('commons.confirm') }}</el-button>
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
            {name: '带背景色柱状图', pluginIcon: 'backgroud-bar.png'},
            {name: '基础面积图', pluginIcon: 'basic-area.png'},
            {name: '基础柱状图', pluginIcon: 'basic-bar.png'},
            {name: '基础折线图', pluginIcon: 'basic-line.png'},
            {name: '标签对齐饼图', pluginIcon: 'label-pie.png'},
            {name: '基础平滑折线图', pluginIcon: 'smooted-line.png'},
            {name: '堆叠折线图', pluginIcon: 'stacked-line.png'},
            {name: '坐标刻度柱状图', pluginIcon: 'tick-bar.png'},
          ],
          color1: '#409EFF',
        }
      },
      methods: {
        handleSelect(key, keyPath) {
          console.log(key, keyPath);
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
    padding: 10px 0 10px 0 !important;
    border-radius: 2px;
    box-sizing: border-box;
    background-color: #FFF;
    min-height: 1800px;
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
