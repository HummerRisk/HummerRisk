/* eslint-disable */
export default {
  name: "Code",
  path: "/code",
  redirect: "/code/code",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/code/base')
  },
  children: [
    {
      path: "code",
      name: "code",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/code/home/Code"),
    },
  ]
}
