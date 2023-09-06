/* eslint-disable */
export default {
  path: "/account",
  redirect: "/account/cloud-account",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/account/base')
  },
  children: [
    {
      path: "cloud-account",
      name: "cloudAccount",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/account/home/CloudAccount'),
    },
    {
      path: "account-scan/:id",
      name: "accountScan",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/account/home/AccountScan'),
    },
    {
      path: "account-scaning-log/:id",
      name: "accountScaningLog",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/account/home/AccountScaningLog'),
    },
  ]
}
