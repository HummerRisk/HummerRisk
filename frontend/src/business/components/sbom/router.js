/* eslint-disable */
export default {
  name: "Sbom",
  path: "/sbom",
  redirect: "/sbom/sbom",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/sbom/base')
  },
  children: [
    {
      path: "sbom",
      name: "sbom",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/sbom/home/Sbom"),
    },
  ]
}
