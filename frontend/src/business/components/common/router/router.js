import Vue from "vue";
import VueRouter from 'vue-router';
import RouterSidebar from "./RouterSidebar";
import axios from "axios";
import Setting from "@/business/components/settings/router";
import Account from "@/business/components/account/router";
import Server from "@/business/components/server/router";
import Image from "@/business/components/image/router";
import Package from "@/business/components/package/router";
import Vuln from "@/business/components/vuln/router";
import Rule from "@/business/components/rule/router";
import Resource from "@/business/components/resource/router";
import Dashboard from "@/business/components/dashboard/router";
import Task from "@/business/components/task/router";
import TaskReport from "@/business/components/taskReport/router";
import Oss from "@/business/components/oss/router";
import Report from "@/business/components/report/router";
import K8s from "@/business/components/k8s/router";
import Situation from "@/business/components/situation/router";
import Code from "@/business/components/code/router";
import Sbom from "@/business/components/sbom/router";

Vue.use(VueRouter);
/* eslint-disable */
const router = new VueRouter({
  routes: [
    {path: "/", redirect: '/setting/personsetting'},
    {
      path: "/sidebar",
      components: {
        sidebar: RouterSidebar
      }
    },
    Setting,
    Account,
    Server,
    Image,
    Package,
    Vuln,
    Rule,
    Resource,
    Task,
    TaskReport,
    Oss,
    Dashboard,
    Report,
    K8s,
    Situation,
    Code,
    Sbom
  ]
});

router.beforeEach((to, from, next) => {
  //解决localStorage清空，cookie没失效导致的卡死问题
  if (!localStorage.getItem('Admin-Token')) {
    axios.get("/signout");
    localStorage.setItem('Admin-Token', "{}");
    window.location.href = "/login";
    next();
  } else {
    next();
  }
});

export default router
