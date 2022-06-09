/* eslint-disable */
export default {
  path: "/dashboard",
  name: "Dashboard",
  redirect: "/dashboard/dashboard",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ "@/business/components/dashboard/base")
  },
  children: [
    {
      path: "dashboard",
      name: "dashboard",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/dashboard/home/Dashboard"),
    },
    {
      path: "active",
      name: "active",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/dashboard/home/Active"),
    },
  ]
}
