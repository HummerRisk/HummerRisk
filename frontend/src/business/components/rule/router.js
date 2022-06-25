/* eslint-disable */
export default {
  path: "/rule",
  name: "Rule",
  redirect: "/rule/rule",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ "@/business/components/rule/base")
  },
  children: [
    {
      path: "rule",
      name: "rule",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/rule/home/Rule"),
    },
    {
      path: "vulnRule",
      name: "vulnRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/vuln/home/Rule'),
    },
    {
      path: "serverRule",
      name: "serverRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Rule'),
    },
    {
      path: "packageRule",
      name: "packageRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/package/home/Rule'),
    },
    {
      path: "imageRule",
      name: "imageRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Rule'),
    },
    {
      path: "ruletag",
      name: "ruletag",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/rule/home/RuleTag"),
    },
    {
      path: "ruleGroup",
      name: "ruleGroup",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/rule/home/RuleGroup"),
    },
    {
      path: "regulation",
      name: "regulation",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/rule/home/Regulation"),
    },
  ]
}
