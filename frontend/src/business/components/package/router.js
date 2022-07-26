/* eslint-disable */
export default {
  name: "Package",
  path: "/package",
  redirect: "/package/package",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/package/base')
  },
  children: [
    {
      path: "package",
      name: "package",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/package/home/Package'),
    },
    {
      path: "rule",
      name: "packageRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/package/home/Rule'),
    },
    {
      path: "result",
      name: "packageResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/package/home/Result'),
    },
    {
      path: "resultdetails/:id",
      name: "packageResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/package/home/ResultDetails"),
    },
  ]
}
