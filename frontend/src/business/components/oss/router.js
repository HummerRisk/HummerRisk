/* eslint-disable */
export default {
  name: "Oss",
  path: "/oss",
  redirect: "/oss/account",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/oss/base')
  },
  children: [
  ]
}
