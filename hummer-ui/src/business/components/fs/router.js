/* eslint-disable */
export default {
  path: "/fs",
  redirect: "/fs/fs",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/fs/base')
  },
  children: [
    {
      path: "fs",
      name: "fs",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/fs/home/Fs'),
    },
    {
      path: "result",
      name: "fsResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/fs/home/Result'),
    },
    {
      path: "result-details/:id",
      name: "fsResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/fs/home/ResultDetails"),
    },
    {
      path: "overview",
      name: "fsOverview",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/fs/home/Overview"),
    },
    {
      path: "history",
      name: "fsHistory",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/fs/home/History"),
    },
  ]
}
