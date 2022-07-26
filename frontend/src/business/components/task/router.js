/* eslint-disable */
export default {
  name: "task",
  path: "/task",
  redirect: "/task/task",
  components: {
    content: () => import(/* webpackChunkName: "setting" */ '@/business/components/task/base')
  },
  children: [
    {
      path: "task",
      name: "taskLay",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/task/home/Task'),
    },
    {
      path: "list",
      name: "taskList",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/task/home/List'),
    },
    {
      path: "report",
      name: "taskReport",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/task/home/Report'),
    },
  ]
}
