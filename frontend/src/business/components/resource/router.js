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
      path: "VulnResult",
      name: "vulnResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/vuln/home/Result'),
    },
    {
      path: "ServerResult",
      name: "serverResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Result'),
    },
    {
      path: "K8sResult",
      name: "k8sResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/k8s/home/Result'),
    },
    {
      path: "ConfigResult",
      name: "configResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/config/home/Result'),
    },
    {
      path: "ImageResult",
      name: "imageResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Result'),
    },
    {
      path: "CodeResult",
      name: "codeResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/code/home/Result'),
    },
    {
      path: "FsResult",
      name: "fsResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/fs/home/Result'),
    },
  ]
}
