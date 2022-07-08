<template>
  <main-container>
    <el-card class="table-card" v-loading="result.loading">
      <el-row :gutter="20">
        <el-col :span="5" style="max-height: 468px;">
          <el-card class="box-card" style="max-height: 489px;">
            <div slot="header" class="clearfix">
              <span>{{ $t('task.first_task') }}</span>
            </div>
            <account @nodeSelectEvent="nodeChange"/>
          </el-card>
        </el-col>
        <el-col :span="19" style="max-height: 513px;">
          <rule :account="account" @addTask="addTask"/>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <task-order :taskOrder="taskOrder"/>
        </el-col>
      </el-row>
      <div class="dialog-footer">
        <el-button @click="reset">{{ $t('task.reset') }}</el-button>
        <el-button type="primary" @click="confirm" @keydown.enter.native.prevent>{{ $t('task.save_task') }}</el-button>
      </div>
    </el-card>
  </main-container>
</template>

<script>
import Account from "@/business/components/task/home/Account";
import Rule from "@/business/components/task/home/Rule";
import TaskOrder from "@/business/components/task/home/TaskOrder";
import MainContainer from "@/business/components/common/components/MainContainer";

/* eslint-disable */
export default {
  components: {
    Account,
    Rule,
    TaskOrder,
    MainContainer,
  },
  data() {
    return {
      result: {},
      loading: false,
      account: {},
      taskOrder: {},
    }
  },
  methods: {
    addTask(item) {
      this.taskOrder = item;
    },
    confirm() {},
    reset() {},
    nodeChange(node, nodeIds, pNodes) {
      this.account = node.data;
    },
  },
}
</script>

<style scoped>
.box-card {
  margin: 10px 0 0 0;
}
.box-card >>> .el-card__header {
  padding: 5px 20px;
  background-color: #b0abab;
  color: #fff;
}
.box-card >>> .el-card__body {
  padding: 10px;
  min-height: 405px;
}
.dialog-footer {
  text-align: center;
}
</style>

