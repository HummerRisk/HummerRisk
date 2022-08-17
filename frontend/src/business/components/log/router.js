/* eslint-disable */
export default {
  name: "Log",
  path: "/log",
  redirect: "/log/event",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/log/base')
  },
  children: [
    {
      path: "event",
      name: "event",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/log/home/event'),
    },
    {
      path: "audit",
      name: "audit",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/log/home/audit'),
    },
  ]
}

