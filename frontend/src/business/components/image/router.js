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
      name: "imageRepo",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/ImageRepo'),
    },
    {
      path: "image",
      name: "image",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/image/home/Image'),
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
  ]
}
