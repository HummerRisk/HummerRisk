<template>
  <el-aside :width="width" class="aside-container"
            :style="{'margin-left': !asideHidden ? 0 : '-' + width}">
    <div v-if="enableAsideHidden" class="hiddenBottom" @click="asideHidden = !asideHidden" :style="{'left': asideHidden ? 0 : width}">
      <i v-if="!asideHidden" class="el-icon-arrow-left"/>
      <i v-if="asideHidden" class="el-icon-arrow-right"/>
    </div>
    <slot></slot>
  </el-aside>
</template>

<script>
/* eslint-disable */
    export default {
      name: "AsideContainer",
      props: {
        width: {
          type: String,
          default: '300px'
        },
        enableAsideHidden: {
          type: Boolean,
          default: true
        },
      },
      data() {
        return {
          asideHidden: false
        }
      }
    }
</script>

<style scoped>

  .aside-container {
    border: 1px solid #e6e6e6;
    border-radius: 2px;
    box-sizing: border-box;
    background-color: #FFF;
    height: calc(100vh - 80px);
  }

  .hiddenBottom {
    width: 8px;
    height: 50px;
    top: calc((100vh - 80px)/3);
    line-height: 50px;
    border-radius: 0 15px 15px 0;
    background-color: #acb7c1;
    display: inline-block;
    position: absolute;
    cursor: pointer;
    opacity: 0.2;
    font-size: 2px;
    margin-left: 1px;
  }

  .hiddenBottom i {
    margin-left: -2px;
  }

  .hiddenBottom:hover {
    opacity: 0.8;
    width: 12px;
  }

  .hiddenBottom:hover i {
    margin-left: 0;
    color: white;
  }

</style>
