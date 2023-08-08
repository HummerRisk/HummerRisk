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
      path: "clouddashboard",
      name: "cloudDashboard",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/account/home/Dashboard"),
    },
    {
      path: "result",
      name: "cloudResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/resource/home/Result'),
    },
    {
      path: "resultdetails/:id",
      name: "CloudResourceDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/resource/home/ResultDetails"),
    },
    {
      path: "cloudHistory",
      name: "cloudHistory",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/account/home/History"),
    },
    {
      path: 'accountOverview',
      name: 'cloudOverview',
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/account/home/AccountOverview'),
    },
  ]
}
