/* eslint-disable */
export default {
  name: "Log",
  path: "/log",
  redirect: "/log/sync",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/event/base')
  },
  children: [
    {
      path: "sync",
      name: "sync",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/event/home/Sync'),
    },
    {
      path: "event",
      name: "event",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/event/home/Event'),
    },
    {
      path: "overview",
      name: "EventOverview",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/event/home/Overview"),
    },
    {
      path: "group",
      name: "EventGroup",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/event/home/Group"),
    },
    {
      path: "ip",
      name: "EventIp",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/event/home/Ip"),
    },
  ]
}

