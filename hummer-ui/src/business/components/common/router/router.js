import Vue from "vue";
import VueRouter from 'vue-router';
import RouterSidebar from "./RouterSidebar";
import Setting from "@/business/components/settings/router";
import Account from "@/business/components/account/router";
import Rule from "@/business/components/rule/router";
import Resource from "@/business/components/resource/router";
import Dashboard from "@/business/components/dashboard/router";
import Oss from "@/business/components/oss/router";
import Report from "@/business/components/report/router";
import CloudSituation from "@/business/components/cloudSituation/router";
import Log from "@/business/components/event/router";
import ReportCenter from "@/business/components/reportcenter/router";
import {signoutUrl} from "@/api/auth/auth";
import axios from "axios";

Vue.use(VueRouter);
/* eslint-disable */
const router = new VueRouter({
  routes: [
    {path: "/", redirect: '/dashboard/dashboard'},
    {
      path: "/sidebar",
      components: {
        sidebar: RouterSidebar
      }
    },
    Setting,
    Account,
    Rule,
    Resource,
    Oss,
    Dashboard,
    Report,
    CloudSituation,
    Log,
    ReportCenter,
  ]
});

router.beforeEach((to, from, next) => {
  //解决localStorage清空，cookie没失效导致的卡死问题
  if (!localStorage.getItem('Admin-Token')) {
    axios.get(signoutUrl);
    localStorage.setItem('Admin-Token', "{}");
    window.location.href = "/login";
    next();
  } else {
    next();
  }
});

export default router;
