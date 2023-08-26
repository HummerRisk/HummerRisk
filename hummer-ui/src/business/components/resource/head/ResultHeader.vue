<template>
  <el-menu class="header-menu" :unique-opened="true" mode="horizontal" default-active="1" router background-color="aliceblue" active-text-color="red">
    <el-button class="el-btn-btm" type="info" plain size="small" @click="back" style="margin: 0 10px;">{{ $t('vis.back') + $t('resource.resource_result_list') }}<i class="el-icon-back"></i></el-button>
    <el-button class="el-btn-btm" type="warning" plain size="small" @click="pdfDown" style="margin: 0 10px;">{{ $t('pdf.export_pdf') }}<i class="iconfont icon-pdf1"></i></el-button>
    <el-button class="el-btn-btm" type="success" plain size="small" @click="goReport" style="margin: 0 10px;">{{ $t('resource.statistics') }}<i class="el-icon-right el-icon--right"></i></el-button>

  </el-menu>
</template>

<script>
import SearchList from "@/business/components/common/head/SearchList";
import htmlToPdf from "@/common/js/htmlToPdf";

/* eslint-disable */
export default {
  name: "AccountSwitch",
  props: {
    accountId: String
  },
  components: {SearchList},
  data() {
    return {
      htmlTitle: this.$t('pdf.html_title'),
    }
  },
  created() {
  },
  methods: {
    back() {
      this.$emit('back');
    },
    goReport() {
      this.$emit('goReport');
    },
    //下载pdf
    pdfDown() {
      htmlToPdf.getPdfById(this.htmlTitle);
    },
  },
}
</script>

<style scoped>
.account-name {
  display: inline-block;
  width: 250px;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
}

.el-divider--horizontal {
  margin: 0;
}

.el-btn-btm {
  vertical-align: middle;
  margin: 3px 0;
}

.iconfont {
  margin: 0 0 0 3px;
  text-align: center;
  font-size: 12px;
}
</style>
