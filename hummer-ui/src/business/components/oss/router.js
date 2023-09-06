/* eslint-disable */
export default {
  path: "/oss",
  redirect: "/oss/account",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/oss/base')
  },
  children: [
    {
      path: "account",
      name: "account",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/oss/home/Account'),
    },
    {
      path: "overview",
      name: "overview",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/oss/home/Overview'),
    },
    {
      path: "bucket",
      name: "bucket",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/oss/home/Bucket'),
    },
    {
      path: "oss-risk",
      name: "ossRisk",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/oss/home/Risk'),
    },
    {
      path: "oss-report",
      name: "ossReport",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/oss/home/Report'),
    },
  ]
}

