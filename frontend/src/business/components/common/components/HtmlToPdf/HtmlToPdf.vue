<template>
  <div id="PdfPage1">
    <button type="button" class="btn btn-primary" @click="pdfBtn">{{ $t('pdf.export_pdf') }}</button>
  </div>
</template>
<script>
import htmlToPdf from "@/common/js/htmlToPdf";
export default {
  name: "htmlToPdf",
  data(){
    return {
      htmlTitle: this.$t('pdf.html_title'),
    }
  },
  created() {
  },
  methods:{
    pdfBtn(){
      htmlToPdf.getPdf(this.htmlTitle);
    }
  }
}
</script>
<style scoped>
</style>
