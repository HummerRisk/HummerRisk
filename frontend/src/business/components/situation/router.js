/* eslint-disable */
export default {
  name: "Situation",
  path: "/situation",
  redirect: "/situation/situation",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/situation/base')
  },
  children: [
    {
      path: "situation",
      name: "Situation",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/situation/home/Situation"),
    },
  ]
}
