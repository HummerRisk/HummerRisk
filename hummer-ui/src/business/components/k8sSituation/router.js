/* eslint-disable */
export default {
  path: "/k8s-situation",
  redirect: "/k8s-situation/k8s-situation",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/k8sSituation/base')
  },
  children: [
    {
      path: "k8s-situation",
      name: "k8sSituationList",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8sSituation/home/K8sSituation"),
    },
    {
      path: "k8s-topology",
      name: "k8sTopology",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8sSituation/home/K8sTopology"),
    },
    {
      path: "rbac-topology",
      name: "rbacTopology",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8sSituation/home/RbacTopology"),
    },
    {
      path: "k8s-sync-log",
      name: "k8sSyncLog",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8sSituation/home/K8sSyncLog"),
    },
  ]
}
