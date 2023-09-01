/* eslint-disable */
export default {
  path: "/image",
  redirect: "/image/image",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/image/base')
  },
  children: [
    {
      path: "image-repo",
      name: "imageRepo",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/ImageRepo'),
    },
    {
      path: "image",
      name: "image",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Image.vue'),
    },
    {
      path: "rule",
      name: "imageRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Rule'),
    },
    {
      path: "result",
      name: "imageResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Result'),
    },
    {
      path: "result-details/:id",
      name: "imageResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/image/home/ResultDetails"),
    },
    {
      path: "overview",
      name: "imageOverview",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/image/home/Overview"),
    },
    {
      path: "history",
      name: "imageHistory",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/image/home/History"),
    },
  ]
}
