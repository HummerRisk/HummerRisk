/* eslint-disable */
export default {
  path: "/report-center",
  redirect: "/report-center/cloud-report",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/reportcenter/base')
  },
  children: [
    {
      path: "list",
      name: "reportCenterList",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/reportcenter/home/List'),
    },
    {
      path: "cloud-report",
      name: "cloudReportCenter",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/report/home/List"),
    },
  ]
}
