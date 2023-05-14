/* eslint-disable */
export default {
  name: "ReportCenter",
  path: "/reportcenter",
  redirect: "/reportcenter/list",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/reportcenter/base')
  },
  children: [
    {
      path: "list",
      name: "ReportCenterList",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/reportcenter/home/List'),
    },
  ]
}
