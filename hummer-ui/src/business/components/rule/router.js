/* eslint-disable */
export default {
  path: "/rule",
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
      path: "server-rule",
      name: "serverRuleToRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Rule'),
    },
    {
      path: "k8s-rule",
      name: "k8sRuleToRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/k8s/home/Rule'),
    },
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
