/* eslint-disable */
export default {
  name: "CloudTask",
  path: "/cloud/task",
  redirect: "/cloud/task/task",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/task/base')
  },
  children: [
    {
      path: "task",
      name: "task",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/task/home/Task'),
    },
    {
      path: "taskOverview",
      name: "taskOverview",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/task/home/TaskOverview'),
    },
  ]
}
