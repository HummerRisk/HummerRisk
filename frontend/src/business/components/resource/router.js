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
      path: "statistics",
      name: "resourceStatistics",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/resource/home/Statistics"),
    },
    {
      path: "result",
      name: "resourceResult",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/resource/home/Result"),
    },
    {
      path: "resultdetails/:id",
      name: "resultdetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/resource/home/ResultDetails"),
    },
    {
      path: "vulnResult",
      name: "vulnResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/vuln/home/Result'),
    },
    {
      path: "serverResult",
      name: "serverResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Result'),
    },
    {
      path: "imageResult",
      name: "imageResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Result'),
    },
    {
      path: "packageResult",
      name: "packageResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/package/home/Result'),
    },
  ]
}
