/* eslint-disable */
export default {
  name: "Server",
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
      path: "ruleGroup",
      name: "ServerRuleGroup",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/RuleGroup'),
    },
    {
      path: "rule",
      name: "ServerRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Rule'),
    },
    {
      path: "result",
      name: "ServerResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Result'),
    },
    {
      path: "overview",
      name: "ServerOverview",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/server/home/Overview"),
    },
    {
      path: "history",
      name: "ServerHistory",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/server/home/History"),
    },
  ]
}
