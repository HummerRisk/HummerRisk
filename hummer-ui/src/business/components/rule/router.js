/* eslint-disable */
export default {
  path: "/rule",
  name: "Rule",
  redirect: "/rule/ruleGroup",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ "@/business/components/rule/base")
  },
  children: [
    {
      path: "rule",
      name: "cloudRule",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/rule/home/Rule"),
    },
    {
      path: "ruletag",
      name: "ruletag",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/rule/home/RuleTag"),
    },
    {
      path: "ruleGroup",
      name: "CloudRuleGroup",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/account/home/RuleGroup"),
    },
    {
      path: "regulation",
      name: "regulation",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/rule/home/Regulation"),
    },
  ]
}
