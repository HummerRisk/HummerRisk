/* eslint-disable */
export default {
  path: "/rule",
  redirect: "/rule/rule-group",
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
      path: "rule-tag",
      name: "ruleTag",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/rule/home/RuleTag"),
    },
    {
      path: "rule-group",
      name: "cloudRuleGroup",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/account/home/RuleGroup"),
    },
    {
      path: "regulation",
      name: "regulation",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/rule/home/Regulation"),
    },
  ]
}
