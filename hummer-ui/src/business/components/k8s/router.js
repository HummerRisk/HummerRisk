/* eslint-disable */
export default {
  path: "/k8s",
  redirect: "/k8s/k8s",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/k8s/base')
  },
  children: [
    {
      path: "k8s",
      name: "k8s",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/K8s"),
    },
    {
      path: "result",
      name: "k8sResult",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/Result"),
    },
    {
      path: "result-details/:id",
      name: "k8sResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/ResultDetails"),
    },
    {
      path: "result-config-details/:id",
      name: "k8sResultConfigDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/ResultConfigDetails"),
    },
    {
      path: "result-kubench-details/:id",
      name: "k8sResultKubenchDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/ResultKubenchDetails"),
    },
    {
      path: "cloud-result-details/:id",
      name: "k8sCloudResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/CloudResultDetails"),
    },
    {
      path: "overview",
      name: "k8sOverview",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/Overview"),
    },
    {
      path: "history",
      name: "k8sHistory",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/History"),
    },
    {
      path: "rule-group",
      name: "k8sRuleGroup",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/RuleGroup"),
    },
    {
      path: "rule",
      name: "k8sRule",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/Rule"),
    },
  ]
}
