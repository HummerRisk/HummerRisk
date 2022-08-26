/* eslint-disable */
export default {
  name: "Cost",
  path: "/cost",
  redirect: "/cost/overview",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/cost/base')
  },
  children: [
    {
      path: "overview",
      name: "CostOverview",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/cost/home/Overview'),
    },
    {
      path: "order",
      name: "order",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/cost/home/Order'),
    },
    {
      path: "apportion",
      name: "apportion",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/cost/home/Apportion'),
    },
    {
      path: "trend",
      name: "trend",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/cost/home/Trend'),
    },
    {
      path: "optimization",
      name: "optimization",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/cost/home/Optimization'),
    },
  ]
}

