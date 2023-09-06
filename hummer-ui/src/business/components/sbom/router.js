/* eslint-disable */
export default {
  path: "/sbom",
  redirect: "/sbom/project",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/sbom/base')
  },
  children: [
    {
      path: "project",
      name: "project",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/sbom/home/Project"),
    },
    {
      path: "sbom",
      name: "sbom",
      component: () => import(/* webpackChunkName: "api" */ "@/business/components/sbom/home/Sbom"),
    },
  ]
}
