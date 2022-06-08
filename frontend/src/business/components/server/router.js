/* eslint-disable */
export default {
  name: "Server",
  path: "/server",
  redirect: "/server/server",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/server/base')
  },
  children: [
    {
      path: "server",
      name: "server",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Server'),
    },
    {
      path: "rule",
      name: "serverRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Rule'),
    },
    {
      path: "result",
      name: "serverResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/server/home/Result'),
    },
  ]
}
