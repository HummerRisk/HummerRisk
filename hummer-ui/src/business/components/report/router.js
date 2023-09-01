/* eslint-disable */
export default {
  path: "/report",
  redirect: "/report/cloud-report",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/report/base')
  },
  children: [
    {
      path: "cloud-report",
      name: "cloudReport",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/report/home/List"),
    },
    {
      path: 'account-overview',
      name: 'cloudAccountOverview',
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/account/home/AccountOverview'),
    },
  ]
}
