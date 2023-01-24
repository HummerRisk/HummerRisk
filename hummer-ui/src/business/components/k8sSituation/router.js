/* eslint-disable */
export default {
  name: "K8sSituation",
  path: "/k8sSituation",
  redirect: "/k8sSituation/k8sSituation",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/k8sSituation/base')
  },
  children: [
    {
      path: "k8sSituation",
      name: "K8sSituationList",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8sSituation/home/K8sSituation"),
    },
    {
      path: "k8sTopology",
      name: "K8sTopology",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8sSituation/home/K8sTopology"),
    },
    {
      path: "rbacTopology",
      name: "RbacTopology",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8sSituation/home/RbacTopology"),
    },
    {
      path: "k8sSyncLog",
      name: "K8sSyncLog",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8sSituation/home/K8sSyncLog"),
    },
  ]
}
