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
      path: "quartz",
      name: "quartz",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/task/home/Task'),
    },
    {
      path: "overview",
      name: "overview",
      component: () => import(/* webpackChunkName: "api" */ '@/business/components/task/home/Overview'),
    },
  ]
}
