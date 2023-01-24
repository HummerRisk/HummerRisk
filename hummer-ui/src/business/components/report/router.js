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
      path: "cloudHistory",
      name: "cloudHistory",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/account/home/History"),
    },
    {
      path: 'accountOverview',
      name: 'cloudOverview',
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/account/home/AccountOverview'),
    },
    {
      path: "vulnReport",
      name: "vulnReport",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/vuln/home/Report"),
    },
    {
      path: "vulnHistory",
      name: "vulnHistory",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/vuln/home/History"),
    },
    {
      path: 'vlunOverview',
      name: 'vlunOverview',
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/vuln/home/VulnOverview'),
    },
  ]
}
