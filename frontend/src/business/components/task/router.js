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
      name: "task",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/task/home/Task'),
    },
    {
      path: "list",
      name: "list",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/task/home/List'),
    },
    {
      path: "report",
      name: "report",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/task/home/Report'),
    },
  ]
}
