<template>
  <chart
    v-if="loaded"
    :style="{'height': chartHeight, 'width': chartWidth}"
    class="hr-chart"
    :init-options="defaultInitOptions"
    :options="options"
    :theme="theme"
    :group="group"
    @click="onClick"
    :watch-shallow="watchShallow"
    :manual-update="manualUpdate"
    :autoresize="autoresize" id="chartsShow"/>
</template>

<script>
import * as echarts from 'echarts';
export default {
  name: "HrChart",
  components: {
    echarts,
  },
  props: {
    options: Object,
    theme: [String, Object],
    initOptions: {
      type: Object,
      default() {
        return {
          renderer: 'canvas'
        }
      }
    },
    group: String,
    autoresize: Boolean,
    watchShallow: Boolean,
    manualUpdate: Boolean,
    height: {
      type: [Number, String],
      default() {
        return 400
      }
    },
    width: [Number, String],
  },
  data() {
    return {
      loaded: true,
      defaultInitOptions: this.initOptions
    };
  },
  computed: {
    chartHeight() {
      if (this.height.indexOf) {
        return this.height;
      } else {
        return this.height + 'px';
      }
    },
    chartWidth() {
      if (!this.width) {
        return this.width;
      }
      if (this.width.indexOf) {
        return this.width;
      } else {
        return this.width + 'px';
      }
    }
  },
  mounted() {
  },
  methods: {
    onClick(params) {
      this.$emit('onClick', params.data);
    },
    reload() {
      this.loaded = false;
      this.$nextTick(() => {
        this.loaded = true;
      })
    },
  }
};
</script>

<style scoped>

</style>
