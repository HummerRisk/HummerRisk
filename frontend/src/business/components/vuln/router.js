/* eslint-disable */
export default {
  name: "Vuln",
  path: "/vuln",
  redirect: "/vuln/vuln",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/vuln/base')
  },
  children: [
    {
      path: "vulndashboard",
      name: "vulnDashboard",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/vuln/home/Dashboard"),
    },
    {
      path: "vuln",
      name: "VulnList",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/vuln/home/Vuln'),
    },
    {
      path: "rule",
      name: "VulnRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/vuln/home/Rule'),
    },
    {
      path: "result",
      name: "VulnResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/vuln/home/Result'),
    },
    {
      path: "resultdetails/:id",
      name: "VulnResultdetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/vuln/home/ResultDetails"),
    },
    {
      path: "vlunscan/:id",
      name: "Vlunscan",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/vuln/home/VulnScan'),
    },
  ]
}
