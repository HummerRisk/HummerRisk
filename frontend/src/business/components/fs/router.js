/* eslint-disable */
export default {
  name: "Fs",
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
      path: "rule",
      name: "ImageRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/fs/home/Rule'),
    },
    {
      path: "result",
      name: "FsResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/fs/home/Result'),
    },
    {
      path: "resultdetails/:id",
      name: "FsResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/fs/home/ResultDetails"),
    },
    {
      path: "overview",
      name: "FsOverview",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/fs/home/Overview"),
    },
    {
      path: "history",
      name: "FsHistory",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/fs/home/History"),
    },
  ]
}
