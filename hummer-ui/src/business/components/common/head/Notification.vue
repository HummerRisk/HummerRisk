<template>
  <ul class="header-user-menu align-right el-menu--horizontal el-menu"
      style="background-color: rgb(0, 74, 113);" @mouseenter="setColor(true)" @mouseout="setColor(false)">
    <li role="menuitem" aria-haspopup="true" class="el-submenu" tabindex="0">
      <div class="el-submenu__title" @click="notification" id="notification"
           style="border-bottom-color: transparent; color: rgb(255, 255, 255); background-color: #df913c;">
        <div slot="reference">
          <div>
            <i class="el-icon-close-notification qa-notification"></i>
            {{ $t('webmsg.web_msg') }}
            <span v-if="count || paginationConfig.total" class="msg-number">{{ count || paginationConfig.total }}</span>
          </div>
        </div>
      </div>
    </li>
  </ul>

</template>
<script>

import {unReadCountUrl} from "@/api/system/system";

export default {
  components: {},
  data() {
    return {
      showSetting: false,
      data: [],
      visible: false,
      paginationConfig: {
        currentPage: 1,
        pageSize: 5,
        total: 0
      },
      timer: null,
      count: 0
    }
  },
  mounted() {
  },
  beforeDestroy() {
    this.timer && clearInterval(this.timer)
  },
  destroyed() {
    this.timer && clearInterval(this.timer)
  },
  created() {
    // 每60s定时刷新拉取消息
    this.timer = setInterval(() => {
      this.queryCount();
    }, 60000);
  },
  methods: {
    setColor(e) {
      if (e) {
        document.querySelector("#notification").setAttribute("style", "background-color:#b26133;color:white;");
      } else {
        document.querySelector("#notification").setAttribute("style", "background-color:#df913c;color:white;");
      }
    },
    queryCount() {
      this.$post(unReadCountUrl, null, response => {
        this.count = response.data;
      });
    },
    notification() {
      this.$router.push('/setting/webmsg').catch(error => error);
    },
  }
}
</script>

<style scoped>
.qa-notification {
  color: #fff;
  margin: 0;
  font-size: 14px;
}
.msg-number {
  min-width: 14px;
  text-align: center;
  line-height: 14px;
  display: inline-block;
  position: fixed;
  background: red;
  color: #fff;
  border-radius: 17px;
  padding: 4px 7px;
  font-size: 16px;
  transform: scale(.7);
  font-family: Tahoma!important;
}
</style>
