/* eslint-disable */
export default {
  name: "Report",
  path: "/report",
  redirect: "/report/cloudReport",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/report/base')
  },
  children: [
    {
      path: "cloudReport",
      name: "cloudReport",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/report/home/List"),
    },
    {
      path: 'accountOverview',
      name: 'cloudOverview',
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/account/home/AccountOverview'),
    },
  ]
}
