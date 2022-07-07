/* eslint-disable */
export default {
  name: "Account",
  path: "/account",
  redirect: "/account/cloudaccount",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/account/base')
  },
  children: [
    {
      path: "clouddashboard",
      name: "cloudDashboard",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/account/home/Dashboard"),
    },
    {
      path: "cloudaccount",
      name: "cloudaccount",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/account/home/CloudAccount'),
    },
    {
      path: "accountscan/:id",
      name: "accountscan",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/account/home/AccountScan'),
    },
    {
      path: 'quartztask',
      name: 'quartztask',
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/account/home/QuartzTask'),
    },
    {
      path: "rule",
      name: "cloudRule",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/rule/home/Rule"),
    },
    {
      path: "result",
      name: "cloudResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/account/home/Result'),
    },
    {
      path: "resultdetails/:id",
      name: "resultdetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/account/home/ResultDetails"),
    },
    {
      path: "history",
      name: "history",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/account/home/History"),
    },
    {
      path: 'accountoverview',
      name: 'accountoverview',
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/account/home/AccountOverview'),
    },
  ]
}
