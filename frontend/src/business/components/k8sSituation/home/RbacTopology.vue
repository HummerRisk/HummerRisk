<template>
  <main-container class="card">
    <el-card class="table-card" v-loading="result.loading">
      <el-tabs type="border-card">
        <el-tab-pane :label="$t('k8s.resource_perspective')">
          <svg id="k8s-topo"></svg>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!--Result log-->
    <el-drawer class="rtl" :title="$t('resource.i18n_log_detail')" :visible.sync="logVisible" size="85%" :before-close="handleClose" :direction="direction"
               :destroy-on-close="true">
      <el-row class="el-form-item-dev" v-if="logData.length == 0">
        <span>{{ $t('resource.i18n_no_data') }}<br></span>
      </el-row>
      <el-row class="el-form-item-dev" v-if="logData.length > 0">
        <div>
          <el-row style="background: #e5e9f2;">
              <el-col :span="8" style="background: #e5e9f2;">
                <span class="grid-content-log-span"> {{ logForm.name }}</span>
              </el-col>
              <el-col :span="8" style="background: #e5e9f2;">
                <span class="grid-content-log-span">
                    <img :src="require(`@/assets/img/platform/${logForm.pluginIcon?logForm.pluginIcon:'k8s.png'}`)" style="width: 16px; height: 16px; vertical-align:middle" alt=""/>
                  </span>
              </el-col>
              <el-col :span="8" style="background: #e5e9f2;">
                 <span class="grid-content-status-span" v-if="logForm.resultStatus === 'APPROVED'" style="color: #579df8">
                    <i class="el-icon-loading"></i> {{ $t('resource.i18n_in_process') }}
                  </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'FINISHED'" style="color: #7ebf50">
                    <i class="el-icon-success"></i> {{ $t('resource.i18n_done') }}
                  </span>
                <span class="grid-content-status-span" v-else-if="logForm.resultStatus === 'ERROR'" style="color: red;">
                    <i class="el-icon-error"></i> {{ $t('resource.i18n_has_exception') }}
                  </span>
              </el-col>
          </el-row>
        </div>
        <el-table :show-header="false" :data="logData" class="adjust-table table-content">
          <el-table-column>
            <template v-slot:default="scope">
              <div class="bg-purple-div">
                <span
                  v-bind:class="{true: 'color-red', false: ''}[scope.row.result == false]">
                      {{ scope.row.createTime | timestampFormatDate }}
                      {{ scope.row.operator }}
                      {{ scope.row.operation }}
                      {{ scope.row.output }}<br>
                </span>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <log-form :logForm="logForm"/>
      </el-row>
      <template v-slot:footer>
        <dialog-footer
          @cancel="logVisible = false"
          @confirm="logVisible = false"/>
      </template>
    </el-drawer>
    <!--Result log-->
  </main-container>
</template>
<script>
import MainContainer from "../../common/components/MainContainer";
import SearchList from "@/business/components/k8sSituation/home/SearchList";
import LogForm from "@/business/components/k8s/home/LogForm";
import * as d3 from 'd3';
import htmlToPdf from "@/common/js/htmlToPdf";
import Topo from "@/common/js/Topo";

/* eslint-disable */
export default {
  components: {
    MainContainer,
    SearchList,
    LogForm,
    d3,
  },
  data() {
    return {
      result: {},
      direction: 'rtl',
      logVisible: false,
      logForm: {},
      logData: [],
      htmlTitle: this.$t('pdf.html_title'),
      options: {
        data: [
          {
            type: 'app',             //节点类型
            name: 'app',             //节点名称
            active: 3,               //已完成数
            total: 10,               //总数
            health: 10,              //监控健康程度
            underText: 'underText',  //节点之间连线下方的文字
            upwardText: 'upwardText',//节点之间连线上方的文字
            isConfig: 'true',        //节点配置状态
            x: 100,
            y: 100,
          },
        ],
        edges: [
          {
            source: 0,//连线起点（数值对应data数组中的元素）
            target: 3,//连线终点（数值对应data数组中的元素）
          }
        ],
      },
    };
  },
  methods: {
    init() {
      let t = new Topo('#k8s-topo', this.options);
      t.render();
    },

    handleClose() {
      this.logVisible=false;
    },
    handleClick(tab, event) {
    },
    //下载pdf
    pdfDown() {
      htmlToPdf.getPdfById(this.htmlTitle);
    },
  },

  mounted() {
    this.init();
  }

}
</script>

<style scoped>
svg {
  margin: 25px;
}
.table-card >>> .el-card__body {
  padding: 0;
}
.table-card >>> .el-tabs__content{
  padding: 0;
}

#k8s-topo {
   border: 1px solid #ccc;
   user-select: none;
 }

#k8s-topo text {
  font-size: 10px;
  /*和js里保持一致*/
  fill: #1a2c3f;
  text-anchor: middle;
}

#k8s-topo .node-other {
  text-anchor: start;
}

#k8s-topo .health1 {
  stroke: #92e1a2;
}

#k8s-topo .health2 {
  stroke: orange;
}

#k8s-topo .health3 {
  stroke: red;
}

#k8s-topo #cloud,
#k8s-topo #database {
  fill: #ccc;
}

#k8s-topo .link {
  stroke: black;
}

#k8s-topo .node-title {
  font-size: 14px;
}

#k8s-topo .node-code circle {
  fill: green;
}

#k8s-topo .node-code text {
  fill: #fff;
}

#k8s-topo .node-bg {
  fill: #fff;
}

#k8s-topo .arrow {
  fill: black;
}

</style>
