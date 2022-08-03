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
  ]
}
