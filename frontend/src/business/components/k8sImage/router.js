/* eslint-disable */
export default {
  name: "K8sImage",
  path: "/k8sImage",
  redirect: "/k8sImage/k8sImage",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/k8sImage/base')
  },
  children: [
    {
      path: "k8sImageRepo",
      name: "K8sImageRepo",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/ImageRepo'),
    },
    {
      path: "k8sImage",
      name: "K8sImageList",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/k8sImage/home/K8sImage'),
    },
    {
      path: "k8sImageResult",
      name: "K8sImageResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/k8sImage/home/Result'),
    },
    {
      path: "k8sImageResultDetails/:id",
      name: "K8sImageResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8sImage/home/ResultDetails"),
    },
  ]
}
