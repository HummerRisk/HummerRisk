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
