/* eslint-disable */
export default {
  name: "Image",
  path: "/image",
  redirect: "/image/image",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/image/base')
  },
  children: [
    {
      path: "imageRepo",
      name: "ImageRepo",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/ImageRepo'),
    },
    {
      path: "image",
      name: "image",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Image.vue'),
    },
    {
      path: "rule",
      name: "ImageRule",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Rule'),
    },
    {
      path: "result",
      name: "ImageResult",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Result'),
    },
    {
      path: "resultdetails/:id",
      name: "ImageResultDetails",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/image/home/ResultDetails"),
    },
    {
      path: "overview",
      name: "ImageOverview",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/image/home/Overview"),
    },
    {
      path: "history",
      name: "ImageHistory",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/image/home/History"),
    },
  ]
}
