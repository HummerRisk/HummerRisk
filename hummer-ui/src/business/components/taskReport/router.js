/* eslint-disable */
export default {
  path: "/task-report",
  redirect: "/task-report/report",
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
