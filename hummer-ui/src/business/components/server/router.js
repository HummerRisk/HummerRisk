/* eslint-disable */
export default {
  path: "/server",
  redirect: "/server/server",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/server/base')
  },
  children: [
    {
      path: "server",
      name: "server",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Server'),
    },
    {
      path: "certificate",
      name: "certificate",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Certificate'),
    },
    {
      path: "rule-group",
      name: "serverRuleGroup",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/RuleGroup'),
    },
    {
      path: "rule",
      name: "serverRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Rule'),
    },
    {
      path: "result",
      name: "serverResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Result'),
    },
    {
      path: "overview",
      name: "serverOverview",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/server/home/Overview"),
    },
    {
      path: "history",
      name: "serverHistory",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/server/home/History"),
    },
  ]
}
