/* eslint-disable */
export default {
  name: "K8s",
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
      name: "K8sResult",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/Result"),
    },
    {
      path: "resultdetails/:id",
      name: "K8sResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/ResultDetails"),
    },
    {
      path: "overview",
      name: "K8sOverview",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/Overview"),
    },
    {
      path: "history",
      name: "K8sHistory",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/History"),
    },
  ]
}
