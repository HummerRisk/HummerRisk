/* eslint-disable */
export default {
  path: "/event",
  redirect: "/event/sync",
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
      name: "eventOverview",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/event/home/Overview"),
    },
    {
      path: "group",
      name: "eventGroup",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/event/home/Group"),
    },
    {
      path: "ip",
      name: "eventIp",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/event/home/Ip"),
    },
  ]
}

