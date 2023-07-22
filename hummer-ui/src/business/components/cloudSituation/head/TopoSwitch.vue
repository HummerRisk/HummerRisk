<template>
  <div class="head-div">
    <el-menu class="header-menu" :unique-opened="true" mode="horizontal" default-active="1" router background-color="aliceblue" active-text-color="red">
      <!-- 不激活项目路由-->
      <el-menu-item index="1" v-show="false">Placeholder</el-menu-item>
      <el-submenu index="2" popper-class="submenu">
        <template v-slot:title>
          <span class="account-name" :title="name">
            {{ $t('account.cloud_account') }}: {{ name }}
          </span>
        </template>
        <search-list @selectAccount="selectAccount"/>
      </el-submenu>
    </el-menu>
    <el-divider content-position="right">{{ $t('cloud_topo.resource_icon') }}</el-divider>
    <el-collapse accordion>
      <el-collapse-item>
        <template slot="title">
          <el-row style="padding: 10px 10px 10px 20px;width: 100%;background-color: aliceblue;">
            <el-col :span="3">
              <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-server.svg`)">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <span>{{ ' — ' + $t('cloud_topo.server_resource') }}</span>
            </el-col>
            <el-col :span="3">
              <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-db.svg`)">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <span>{{ ' — ' + $t('cloud_topo.db_resource') }}</span>
            </el-col>
            <el-col :span="3">
              <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-oss.svg`)">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <span>{{ ' — ' + $t('cloud_topo.oss_resource') }}</span>
            </el-col>
            <el-col :span="3">
              <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-disk.svg`)">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <span>{{ ' — ' + $t('cloud_topo.disk_resource') }}</span>
            </el-col>
            <el-col :span="3">
              <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-iam.svg`)">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <span>{{ ' — ' + $t('cloud_topo.iam_resource') }}</span>
            </el-col>
            <el-col :span="3">
              <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-eip.svg`)">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <span>{{ ' — ' + $t('cloud_topo.eip_resource') }}</span>
            </el-col>
            <el-col :span="3">
              <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-lb.svg`)">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <span>{{ ' — ' + $t('cloud_topo.elb_resource') }}</span>
            </el-col>
            <el-col :span="3">
              <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-sg.svg`)">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <span>{{ ' — ' + $t('cloud_topo.sg_resource') }}</span>
            </el-col>
          </el-row>
        </template>
        <el-row style="padding: 10px 31px 10px 20px;width: 100%;background-color: aliceblue;">
          <el-col :span="3">
            <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-vpc.svg`)">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <span>{{ ' — ' + $t('cloud_topo.vpc_resource') }}</span>
          </el-col>
          <el-col :span="3">
            <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-es.svg`)">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <span>{{ ' — ' + $t('cloud_topo.es_resource') }}</span>
          </el-col>
          <el-col :span="3">
            <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-redis.svg`)">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <span>{{ ' — ' + $t('cloud_topo.redis_resource') }}</span>
          </el-col>
          <el-col :span="3">
            <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-mongodb.svg`)">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <span>{{ ' — ' + $t('cloud_topo.mongodb_resource') }}</span>
          </el-col>
          <el-col :span="3">
            <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-postgresql.svg`)">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <span>{{ ' — ' + $t('cloud_topo.postgresql_resource') }}</span>
          </el-col>
          <el-col :span="3">
            <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-other.svg`)">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <span>{{ ' — ' + $t('cloud_topo.other_resource') }}</span>
          </el-col>
          <el-col :span="3">
          </el-col>
          <el-col :span="3">
          </el-col>
        </el-row>
        <el-divider content-position="right">{{ $t('cloud_topo.risk_icon') }}</el-divider>
        <el-row style="padding: 10px 31px 10px 20px;width: 100%;background-color: aliceblue;">
          <el-col :span="3">
            <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/n-server.svg`)">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <span>{{ ' — ' + $t('resource.n_risk') }}</span>
          </el-col>
          <el-col :span="3">
            <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/e-server.svg`)">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <span>{{ ' — ' + $t('resource.have_risk') }}</span>
          </el-col>
          <el-col :span="3">
            <el-image style="border-radius: 50%;width: 24px; height: 24px; vertical-align:middle;" :src="require(`@/assets/img/cloudtopo/u-server.svg`)">
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <span>{{ ' — ' + $t('resource.uncheck') }}</span>
          </el-col>
        </el-row>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import SearchList from "@/business/components/cloudSituation/head/SearchList";

/* eslint-disable */
export default {
  name: "TopoSwitch",
  props: {
  },
  components: {SearchList},
  data() {
    return {
      name: '',
    }
  },
  watch: {
  },
  methods: {
    selectAccount(accountId, accountName) {
      this.name = accountName;
      this.$emit('selectAccount', accountId, accountName);
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

.head-div {
  background-color: aliceblue;
}

.head-div >>> .el-collapse {
  margin: 10px 0 0 0;
}

.head-div >>> .el-collapse-item__header {
  background-color: aliceblue;
}

.head-div >>> .el-collapse-item__content {
  padding-bottom: 0;
}

.head-div >>> .el-divider__text {
  background-color: aliceblue;
  z-index: 999;
}

</style>
