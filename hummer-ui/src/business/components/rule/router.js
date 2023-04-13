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
      path: "serverRule",
      name: "serverRuleToRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Rule'),
    },
    {
      path: "k8sRule",
      name: "k8sRuleToRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/k8s/home/Rule'),
    },
    {
      path: "imageRule",
      name: "imageRuleToRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Rule'),
    },
    {
      path: "codeRule",
      name: "codeRuleToRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/code/home/Rule'),
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
