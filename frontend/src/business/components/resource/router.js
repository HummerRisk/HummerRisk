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
      path: "VulnResultdetails/:id",
      name: "VulnResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/vuln/home/ResultDetails"),
    },
    {
      path: "ServerResult",
      name: "ServerResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Result'),
    },
    {
      path: "K8sResult",
      name: "K8sResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/k8s/home/Result'),
    },
    {
      path: "K8sResultdetails/:id",
      name: "K8sResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/ResultDetails"),
    },
    {
      path: "ConfigResult",
      name: "configResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/config/home/Result'),
    },
    {
      path: "ConfigResultdetails/:id",
      name: "ConfigResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/config/home/ResultDetails"),
    },
    {
      path: "ImageResult",
      name: "ImageResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Result'),
    },
    {
      path: "ImageResultdetails/:id",
      name: "ImageResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/image/home/ResultDetails"),
    },
    {
      path: "CodeResult",
      name: "CodeResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/code/home/Result'),
    },
    {
      path: "CodeResultdetails/:id",
      name: "CodeResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/code/home/ResultDetails"),
    },
    {
      path: "FsResult",
      name: "FsResultByResource",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/fs/home/Result'),
    },
    {
      path: "FsResultdetails/:id",
      name: "FsResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/fs/home/ResultDetails"),
    },
  ]
}
