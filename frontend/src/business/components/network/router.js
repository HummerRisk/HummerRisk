/* eslint-disable */
export default {
  name: "Network",
  path: "/network",
  redirect: "/network/network",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/network/base')
  },
  children: [
    {
      path: "network",
      name: "network",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/network/home/Network'),
    },
    {
      path: "rule",
      name: "networkRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/network/home/Rule'),
    },
    {
      path: "result",
      name: "networkResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/network/home/Result'),
    },
  ]
}
