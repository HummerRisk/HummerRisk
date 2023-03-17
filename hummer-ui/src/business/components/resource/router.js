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
      name: "CloudResourceResult",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/resource/home/Result"),
    },
    {
      path: "resultdetails/:id",
      name: "CloudResourceDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/resource/home/ResultDetails"),
    },
    {
      path: "ServerResult",
      name: "ServerResourceResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Result'),
    },
    {
      path: "K8sResult",
      name: "K8sResourceResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/k8s/home/Result'),
    },
    {
      path: "K8sResultdetails/:id",
      name: "K8sResourceResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/ResultDetails"),
    },
    {
      path: "K8sResultConfigdetails/:id",
      name: "K8sResourceResultConfigDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/ResultConfigDetails"),
    },
    {
      path: "K8sResultKubenchdetails/:id",
      name: "K8sResourceResultkubenchDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/k8s/home/ResultKubenchDetails"),
    },
    {
      path: "ConfigResult",
      name: "ConfigResourceResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/config/home/Result'),
    },
    {
      path: "ConfigResultdetails/:id",
      name: "ConfigResourceResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/config/home/ResultDetails"),
    },
    {
      path: "ImageResult",
      name: "ImageResourceResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Result'),
    },
    {
      path: "ImageResultdetails/:id",
      name: "ImageResourceResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/image/home/ResultDetails"),
    },
    {
      path: "CodeResult",
      name: "CodeResourceResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/code/home/Result'),
    },
    {
      path: "CodeResultdetails/:id",
      name: "CodeResourceResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/code/home/ResultDetails"),
    },
    {
      path: "FsResult",
      name: "FsResourceResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/fs/home/Result'),
    },
    {
      path: "FsResultdetails/:id",
      name: "FsResourceResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/fs/home/ResultDetails"),
    },
  ]
}
