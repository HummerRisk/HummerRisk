/* eslint-disable */
export default {
  path: "/report-center",
  redirect: "/report-center/report-center",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/reportcenter/base')
  },
  children: [
    {
      path: "report-center",
      name: "reportCenterList",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/reportcenter/home/List'),
    },
  ]
}
