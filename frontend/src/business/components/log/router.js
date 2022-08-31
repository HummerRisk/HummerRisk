/* eslint-disable */
export default {
  name: "Log",
  path: "/log",
  redirect: "/log/sync",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/log/base')
  },
  children: [
    {
      path: "sync",
      name: "sync",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/log/home/sync'),
    },
    {
      path: "event",
      name: "event",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/log/home/event'),
    },
  ]
}

