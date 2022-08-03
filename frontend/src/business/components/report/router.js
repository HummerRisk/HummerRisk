/* eslint-disable */
export default {
  name: "Report",
  path: "/report",
  redirect: "/report/list",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/report/base')
  },
  children: [
    {
      path: "list",
      name: "list",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/report/home/List"),
    },
  ]
}
