/* eslint-disable */
export default {
  path: "/code",
  redirect: "/code/code",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/code/base')
  },
  children: [
    {
      path: "overview",
      name: "codeOverview",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/code/home/Overview"),
    },
    {
      path: "code",
      name: "code",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/code/home/Code"),
    },
    {
      path: "rule",
      name: "codeRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/code/home/Rule'),
    },
    {
      path: "result",
      name: "codeResult",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/code/home/Result"),
    },
    {
      path: "result-details/:id",
      name: "codeResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/code/home/ResultDetails"),
    },
    {
      path: "history",
      name: "codeHistory",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/code/home/History"),
    },
  ]
}
