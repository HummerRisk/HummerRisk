/* eslint-disable */
export default {
  name: "TaskReport",
  path: "/taskReport",
  redirect: "/taskReport/report",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/taskReport/base')
  },
  children: [
    {
      path: "report",
      name: "taskReport",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/taskReport/home/Report'),
    },
  ]
}
