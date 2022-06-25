/* eslint-disable */
export default {
  name: "Oss",
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
      path: "oss",
      name: "oss",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/oss/home/Oss'),
    },
    {
      path: "bucket",
      name: "bucket",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/oss/home/Bucket'),
    },
    {
      path: "ossOrder",
      name: "ossOrder",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/oss/home/OssOrder'),
    },
  ]
}
