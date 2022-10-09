/* eslint-disable */
export default {
  path: "/resource",
  name: "Resource",
  redirect: "/resource/result",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ "@/business/components/resource/base")
  },
  children: [
    {
      path: "result",
      name: "cloudResourceResult",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/resource/home/Result"),
    },
    {
      path: "resultdetails/:id",
      name: "cloudResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/resource/home/ResultDetails"),
    },
    {
      path: "vulnResult",
      name: "vulnResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/vuln/home/Result'),
    },
    {
      path: "serverResult",
      name: "serverResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Result'),
    },
    {
      path: "k8sResult",
      name: "k8sResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/k8s/home/Result'),
    },
    {
      path: "configResult",
      name: "configResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/config/home/Result'),
    },
    {
      path: "imageResult",
      name: "imageResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Result'),
    },
    {
      path: "codeResult",
      name: "codeResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/code/home/Result'),
    },
  ]
}
