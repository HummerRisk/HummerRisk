/* eslint-disable */
export default {
  name: "CloudSituation",
  path: "/cloudSituation",
  redirect: "/cloudSituation/cloudSituation",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/cloudSituation/base')
  },
  children: [
    {
      path: "cloudSituation",
      name: "CloudSituationList",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/cloudSituation/home/CloudSituation"),
    },
    {
      path: "cloudSyncLog",
      name: "CloudSyncLog",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/cloudSituation/home/CloudSyncLog"),
    },
    {
      path: "cloudTopology",
      name: "CloudTopology",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/cloudSituation/home/CloudTopology"),
    },
  ]
}
