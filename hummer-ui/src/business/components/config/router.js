/* eslint-disable */
export default {
  path: "/config",
  redirect: "/config/config",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/config/base')
  },
  children: [
    {
      path: "config",
      name: "config",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/config/home/Config'),
    },
    {
      path: "result",
      name: "configResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/config/home/Result'),
    },
    {
      path: "result-details/:id",
      name: "configResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/config/home/ResultDetails"),
    },
    {
      path: "overview",
      name: "configOverview",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/config/home/Overview"),
    },
    {
      path: "history",
      name: "configHistory",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/config/home/History"),
    },
  ]
}

