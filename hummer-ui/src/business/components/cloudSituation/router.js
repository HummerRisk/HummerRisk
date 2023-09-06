/* eslint-disable */
export default {
  path: "/cloud-situation",
  redirect: "/cloud-situation/cloud-situation",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/cloudSituation/base')
  },
  children: [
    {
      path: "cloud-situation",
      name: "cloudSituationList",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/cloudSituation/home/CloudSituation"),
    },
    {
      path: "cloud-sync-log",
      name: "cloudSyncLog",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/cloudSituation/home/CloudSyncLog"),
    },
    {
      path: "cloud-topology",
      name: "cloudTopology",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/cloudSituation/home/CloudTopology"),
    },
  ]
}
